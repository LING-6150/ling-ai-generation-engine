# Multi-Agent 重构学习笔记

> 项目：Ling-AI-CODE-generation  
> 目标：把 `agent=true` 路径从单一线性工作流重构为 Multi-Agent 架构  
> 状态：Phase 1-3 已完成

---

## 一、项目现状速览

### 核心技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5.11 / Java 21 |
| AI 框架 | LangChain4j 1.1.0 + LangGraph4j 1.6.0-rc2 |
| LLM | DeepSeek（OpenAI 兼容接口） |
| ORM | MyBatis-Flex 1.11.0 |
| 缓存/限流/会话 | Redis（Spring Session + Redisson + LangChain4j Redis Memory） |
| 流式输出 | Project Reactor（Flux / SSE） |
| 监控 | Micrometer + Prometheus |

### 代码生成类型

```
CodeGenTypeEnum
  ├── HTML        → 单文件 HTML
  ├── MULTI_FILE  → 多文件（HTML + CSS + JS）
  └── VUE_PROJECT → Vue 项目（带 npm build）
```

### 两条主路径

```
chatToGenCode(agent=false)  ← 保持现状，不动
  └─ AiCodeGeneratorFacade → StreamingChatModel → Flux<String>

chatToGenCode(agent=true)   ← 本次重构目标
  旧：CodeGenWorkflow（LangGraph4j 线性状态机）
  新：OrchestratorAgent（Multi-Agent 编排）
```

---

## 二、发现的 Bug（重构时一并修复）

| # | Bug | 文件 | 严重程度 |
|---|-----|------|----------|
| 1 | `agent=true` 路径 AI 回复不写入 `chat_history` | `AppServiceImpl` | 严重 |
| 2 | `PromptSafetyInputGuardrail` 错误提示写的是"1000"，实际限制是 10000 | `PromptSafetyInputGuardrail` | 轻微 |
| 3 | `ImageCollectorNode` 的静态线程池不受 Spring 管理，JVM 关闭时泄漏 | `ImageCollectorNode` | 中等 |

**Bug #1 根因**：

```java
// AppServiceImpl.chatToGenCode()
if (agent) {
    // ❌ 只有这一行，没有调用 streamHandlerExecutor
    return codeStream.doFinally(...);
} else {
    // ✅ agent=false 走了 streamHandlerExecutor，会保存 AI 回复
    return streamHandlerExecutor.doExecute(codeStream, chatHistoryService, ...);
}
```

**Bug #3 根因**：

```java
// ImageCollectorNode.java
// static final → Spring 不管，@PreDestroy 不触发
private static final ExecutorService IMAGE_EXECUTOR = Executors.newFixedThreadPool(3);
```

---

## 三、目标架构：Multi-Agent

### Agent 分工

```
OrchestratorAgent（编排者，纯 Java，非 LLM）
    ├─ RequirementAgent   → 理解需求，输出 RequirementSpec
    ├─ PlannerAgent       → 规划任务，输出 TaskGraph
    ├─ AssetAgent         → 图片收集（可与 Requirement/Planning 并行）
    ├─ CodeGenAgent       → 代码生成（流式）
    ├─ ReviewAgent        → 异构模型审查，输出 ReviewReport
    └─ RefineAgent        → 基于 ReviewReport 局部修复
```

### 关键设计原则

1. **Agent 通信用 Java 类型，不用自然语言**——输入输出都是 record 或 Lombok 类
2. **异构模型**——ReviewAgent 必须用与 CodeGenAgent 不同的 Provider，避免同一模型给自己审查
3. **Orchestrator 负责持久化**——修复 Bug #1，每次生成完成后写入 `chat_history`
4. **并发安全**——`@Scope("prototype")` 模式继续保留

### 各 Agent 使用的模型

| Agent | 模型 | Provider | 理由 |
|-------|------|----------|------|
| RequirementAgent | `routingChatModel`（现有） | DeepSeek | 轻量分类，256 tokens 够 |
| PlannerAgent | `routingChatModel`（现有） | DeepSeek | 决策简单 |
| AssetAgent | `imageCollectionChatModel`（现有） | DeepSeek | 复用图片搜索链路 |
| CodeGenAgent | `reasoningStreamingChatModel`（现有, prototype） | DeepSeek | 最强推理，流式 |
| ReviewAgent | `reviewChatModel`（**新增**） | OpenAI GPT-4o-mini（主）/ 通义千问备选 | 异构视角审查 |
| RefineAgent | `reasoningStreamingChatModel`（现有, prototype） | DeepSeek | 同 CodeGenAgent |

---

## 四、核心数据结构

### Agent 接口

```java
@FunctionalInterface
public interface Agent<I, O> {
    O execute(I input, AgentContext context);
}
```

**为什么用泛型接口而不是统一签名**：类型安全，IDE 可检查，测试易 mock。代价是 Orchestrator 内部各 Agent 调用写法不统一，这是可接受的。

### AgentContext（record）

```java
public record AgentContext(Long appId, Long userId, int retryBudget, String agentName) {
    public AgentContext withAgentName(String name) { ... }   // 返回新实例，不可变
    public AgentContext withRetryBudget(int budget) { ... }
}
```

**为什么是 record**：纯数据载体，不可变，跨 Agent 传递时不会被修改。

### 4 个核心数据传输类

```
RequirementSpec（Lombok @Data）← LangChain4j 反序列化需要无参构造
  ├── CodeGenTypeEnum codeGenType
  ├── String projectDescription
  ├── List<String> features
  ├── List<String> techConstraints
  └── Complexity complexity        ← enum: SIMPLE / MEDIUM / COMPLEX

TaskGraph（Java record）
  ├── boolean parallelizeAsset     ← AssetAgent 是否与 Requirement 并行
  ├── int suggestedRetries
  ├── String codeGenHints          ← 给 CodeGenAgent 的补充指令
  └── String reviewFocusAreas      ← 给 ReviewAgent 的重点审查方向

ReviewReport（Lombok @Data）← LangChain4j 反序列化
  ├── Boolean passed
  ├── Integer score               ← 0-100
  ├── List<Issue> issues
  └── String summary
  内部类 Issue { Severity severity, String file, String description, String suggestion }
  内部 enum Severity { BLOCKER, MAJOR, MINOR }

RefinementPlan（Java record）
  ├── List<String> targetFiles
  ├── String instructions         ← 从 ReviewReport 提炼的修复指令（JSON）
  └── int attemptNumber           ← 第几次重试（1-3）
```

### 为什么 RequirementSpec/ReviewReport 用 Lombok，TaskGraph 用 record？

- **LangChain4j AiServices** 通过 Jackson 反序列化 LLM 的 JSON 输出到 Java 对象
- Jackson 反序列化需要无参构造函数，Lombok `@NoArgsConstructor` 提供
- Java record 没有无参构造，如果直接作为 LangChain4j service 返回类型会有风险
- `TaskGraph` 由 Orchestrator 手动构建（不是 LLM 直接输出），所以 record 安全
- **验证**：Phase 1 测试中 `Json.fromJson(json, TaskGraph.class)` 通过，证明 LangChain4j 内部 Jackson 支持 record

---

## 五、并行执行设计

```
START
  ├──► RequirementAgent(prompt) ──► RequirementSpec ──┐
  │                                                    ├──► PlannerAgent ──► TaskGraph
  └──► AssetAgent(prompt) ──► imageListStr ────────────┘
                                                        │
                                                        ▼
                                              CodeGenAgent(CodeGenInput)
                                                        │
                                                        ▼
                                              ReviewAgent(codeDir, spec)
                                                        │
                                         ┌──── passed ──┘
                                         │
                                    failed + retryBudget > 0
                                         │
                                         ▼
                                   RefineAgent(RefineInput)  ←─ 最多 3 次
                                         │
                                         └──► ReviewAgent（再审）
```

**为什么 AssetAgent 可以和 RequirementAgent 并行**：
- 两者都只需要 `originalPrompt`，互不依赖
- AssetAgent（HTTP 图片搜索）约 5-10s，RequirementAgent（LLM 分类）约 1-2s
- 并行可节省约 5-8s

**为什么 CodeGenAgent 要等两者都完成**：
- 需要把图片 URL 注入 prompt（`CodeGenInput.buildPrompt()`）
- 需要 `RequirementSpec.codeGenType` 决定生成模式

---

## 六、chat_history 持久化设计（修复 Bug #1）

### 新增 messageType 枚举值（零 schema 变更）

```java
// ChatHistoryMessageTypeEnum 新增：
AI_SUMMARY("ai_summary")         ← 人类可读摘要
AI_REVIEW_REPORT("ai_review_report")  ← ReviewReport JSON
```

`chat_history` 表的 `message` 字段是 `TEXT` 类型，直接存 JSON 字符串。

### 持久化时机（由 OrchestratorAgent 负责）

```
1. CodeGenAgent 完成后：写入 AI_SUMMARY（"代码生成完成，类型：xxx，目录：xxx"）
2. 每次 RefineAgent 完成后：写入 AI_SUMMARY（"第N次优化，修改了：xxx"）
3. 每次 ReviewAgent 完成后：写入 AI_REVIEW_REPORT（ReviewReport JSON）
4. 最终完成后：写入 AI_SUMMARY（"工作流完成，最终得分：xx"）
```

---

## 七、目录结构

```
ai/
├── guardrail/               ← 现有，不动
├── langgraph4j/             ← 现有，完整保留为 fallback
│   ├── node/
│   ├── state/
│   ├── model/
│   └── workflow/CodeGenWorkflow.java
│
├── multiagent/              ← 全部新增
│   ├── agent/
│   │   ├── Agent.java           (泛型接口)
│   │   ├── AgentContext.java    (record)
│   │   ├── RequirementAgent.java
│   │   ├── PlannerAgent.java
│   │   ├── AssetAgent.java
│   │   ├── CodeGenAgent.java
│   │   ├── ReviewAgent.java
│   │   └── RefineAgent.java
│   ├── model/
│   │   ├── Complexity.java
│   │   ├── RequirementSpec.java
│   │   ├── TaskGraph.java
│   │   ├── ReviewReport.java
│   │   ├── RefinementPlan.java
│   │   ├── CodeGenInput.java
│   │   └── RefineInput.java
│   └── orchestrator/
│       └── OrchestratorAgent.java
│
└── tools/                   ← 现有，不动
```

**包边界规则**：
- `ai/langgraph4j/` 包内代码只读，不修改（保留 fallback）
- `ai/multiagent/` 不引用 `ai/langgraph4j/` 的任何类
- 两个包都可以引用 `ai/tools/` 和 `core/`

---

## 八、分阶段实施计划

| Phase | 名称 | 核心产出 | 状态 |
|-------|------|----------|------|
| **P1** | Foundation | record/interface/enum + Bug #2 | ✅ 完成 |
| **P2** | Non-streaming Agents | RequirementAgent + PlannerAgent + ReviewModelConfig | ✅ 完成 |
| **P3** | AssetAgent 迁移 | AssetAgent + Bug #3 修复 | ✅ 完成 |
| **P4** | CodeGenAgent + RefineAgent | 流式生成 Agent | ✅ 完成 |
| **P5** | ReviewAgent | 异构模型审查（OpenAI json_schema / zhipu 降级） | ✅ 完成 |
| **P6** | OrchestratorAgent 全链路 | 完整编排 + Bug #1 修复 + Review→Refine 循环 | ✅ 完成 (f926d88) |
| **P7** | SSE 流式升级 | code_token 实时透传 + cancel 信号 + MonitorContext 传播 | ✅ 完成 (f926d88) |
| P8 | HTTP 取消优化 | 替换 SimpleClientHttpRequestFactory → OkHttp 实现完整 HTTP 级取消 | 待规划 |

---

## 九、Phase 1 完成情况

### 新建文件

```
src/main/java/.../ai/multiagent/agent/Agent.java
src/main/java/.../ai/multiagent/agent/AgentContext.java
src/main/java/.../ai/multiagent/model/Complexity.java
src/main/java/.../ai/multiagent/model/RequirementSpec.java
src/main/java/.../ai/multiagent/model/TaskGraph.java
src/main/java/.../ai/multiagent/model/ReviewReport.java
src/main/java/.../ai/multiagent/model/RefinementPlan.java
src/main/java/.../ai/multiagent/model/CodeGenInput.java
src/main/java/.../ai/multiagent/model/RefineInput.java
src/test/java/.../multiagent/model/ModelConstructionTest.java
```

### 修改文件

```
src/main/java/.../ai/guardrail/PromptSafetyInputGuardrail.java
  第 49 行："under 1000" → "under 10000"  （Bug #2）
```

### 测试结果

```
ModelConstructionTest: 12 tests, 0 failures ✅
mvn compile: BUILD SUCCESS ✅
```

---

## 十、关键设计决策记录

### 决策 1：不在 P1 创建 OrchestratorAgent 空壳

原因：空壳没有实用价值，`@Component` 带来 Spring 注入顺序风险。P6 直接建完整实现。

### 决策 2：CodeGenAgent 和 RefineAgent 的泛型签名

```java
CodeGenAgent implements Agent<CodeGenInput, Flux<String>>
RefineAgent  implements Agent<RefineInput,  Flux<String>>
```

流式生成是这两个 Agent 的本质能力，`Flux<String>` 直接作为返回类型，不用 `executeStreaming()` 附加方法。

### 决策 3：ReviewReport → RefineAgent 传递方式

直接将 `ReviewReport` 序列化为 JSON 传给 `RefineAgent` 的 prompt。RefineAgent 的 system prompt 明确说明：
- 按 `BLOCKER → MAJOR → MINOR` 优先级修复
- 输出中必须包含"修改了哪些文件"的清单

不新增 `RefinementPlannerAgent`（省一次 LLM 调用）。

### 决策 4：chat_history 零 schema 变更

不修改 `chat_history` 表，不新建辅助表。  
新增两个 `messageType` 枚举值，利用现有 `TEXT` 类型的 `message` 字段存储 JSON。

### 决策 5：Lombok vs record 的选择标准

| 场景 | 用哪个 | 原因 |
|------|--------|------|
| LangChain4j AiServices 返回类型 | Lombok `@Data` | 需要无参构造供 Jackson 反序列化 |
| Agent 间纯数据传递（不经 LLM） | Java record | 不可变，简洁，Java 21 原生支持 |
| 配置/上下文载体 | Java record | 同上 |

---

## 十一、待决定事项（进入 Phase 2 前需确认）

- [ ] OpenAI API Key 是否可用？若不行，切换为通义千问备选方案
- [ ] Phase 2 开始前确认 ReviewModelConfig 的 Provider 选择

---

## 十二、常用参考

### LangChain4j AiServices 基本用法

```java
// 定义接口
public interface RequirementLlmService {
    @SystemMessage(fromResource = "prompt/requirement-analysis-system-prompt.txt")
    RequirementSpec analyze(@UserMessage String userPrompt);
}

// 构建服务（在 Factory/Config 中）
RequirementLlmService service = AiServices.builder(RequirementLlmService.class)
    .chatModel(routingChatModel)
    .build();
```

### @Scope("prototype") 并发模型

```java
// 每次 getBean() 返回新实例，解决 StreamingChatModel 内部串行阻塞问题
@Bean
@Scope("prototype")
public OpenAiStreamingChatModel openAiStreamingChatModel() { ... }

// 调用方
StreamingChatModel model = SpringContextUtil.getBean("openAiStreamingChatModel", ...);
```

### LangGraph4j 节点写法（fallback 路径用的模式）

```java
public static AsyncNodeAction<AgentState> create() {
    return node_async(state -> {
        WorkflowContext context = WorkflowContext.getContext(state);
        // ... 业务逻辑
        context.setCurrentStep("xxx");
        return WorkflowContext.saveContext(context);
    });
}
```

### Flux + 虚拟线程桥接（agent=true SSE 输出模式）

```java
return Flux.create(sink -> {
    Thread.startVirtualThread(() -> {
        try {
            // 阻塞操作在虚拟线程里跑，不占用平台线程
            sink.next(formatSseEvent("step", data));
            sink.complete();
        } catch (Exception e) {
            sink.error(e);
        }
    });
});
```

---

## 十三、Phase 2 详解：Non-streaming Agents

### 13.1 新增文件清单

```
ai/multiagent/llm/
  RequirementLlmService.java   ← LangChain4j AiService 接口
  PlannerLlmService.java       ← LangChain4j AiService 接口

ai/multiagent/agent/
  RequirementAgent.java        ← @Component, implements Agent<String, RequirementSpec>
  PlannerAgent.java            ← @Component, implements Agent<RequirementSpec, TaskGraph>

ai/multiagent/config/
  NonStreamingAgentConfig.java ← @Configuration，把 AiService 接口变成 Spring Bean

config/
  StructuredRoutingChatModelConfig.java  ← 新增专用模型 Bean
  ReviewModelConfig.java                 ← OpenAI/Qwen 双 Provider

resources/prompt/
  requirement-analysis-system-prompt.txt
  planning-system-prompt.txt
```

### 13.2 LangChain4j AiServices 工作原理

**核心概念**：LangChain4j 可以读取一个 Java 接口，在运行时用动态代理（JDK Proxy）自动实现它。你只需声明接口，框架负责：
1. 把 `@SystemMessage` 注入到 LLM 的 system 角色
2. 把 `@UserMessage` 参数注入到 user 角色
3. 把 LLM 的文本输出自动 Jackson 反序列化为返回类型

```java
// 接口定义
public interface RequirementLlmService {
    @SystemMessage(fromResource = "prompt/requirement-analysis-system-prompt.txt")
    RequirementSpec analyze(@UserMessage String userPrompt);
}

// 在 @Configuration 中构建 Bean
@Bean
public RequirementLlmService requirementLlmService() {
    return AiServices.builder(RequirementLlmService.class)
            .chatModel(structuredRoutingChatModel)   // 指定用哪个模型
            .build();
}

// 注入到 Agent 后直接调用，框架自动完成 LLM 调用 + JSON 解析
RequirementSpec spec = requirementLlmService.analyze(userPrompt);
```

**关键约束**：LangChain4j 用 Jackson 反序列化 JSON → Java 对象，因此**返回类型必须有无参构造函数**。这就是 `RequirementSpec` 和 `ReviewReport` 用 Lombok `@Data @NoArgsConstructor` 而不是 record 的原因。

### 13.3 为什么新建 StructuredRoutingChatModelConfig？

**问题背景**：Phase 2 初版错误地把 `NonStreamingAgentConfig` 连接到了 `openAiChatModel`，而 `openAiChatModel` 是通过 LangChain4j 的 Spring Boot Starter 自动配置的 Bean（前缀 `langchain4j.open-ai.chat-model`），已经被用于其他地方。

**三个方案**：
- **Option A**：复用现有 `routingChatModel`（max_tokens=256）——太紧，结构化 JSON 可能被截断
- **Option B**：直接用 `openAiChatModel`——语义不对，主力模型（max_tokens=8192）被用于小分类任务
- **Option C（选定）**：新建 `structuredRoutingChatModel`（max_tokens=512，`json_object` 模式）

```java
// StructuredRoutingChatModelConfig.java
@Data @Configuration
@ConfigurationProperties(prefix = "langchain4j.structured-routing-chat-model")
public class StructuredRoutingChatModelConfig {
    // 关键：responseFormat("json_object") 强制 LLM 只输出合法 JSON
    @Bean
    public ChatModel structuredRoutingChatModel() {
        return OpenAiChatModel.builder()
                .responseFormat("json_object")
                .maxTokens(maxTokens)     // 512，在 RequirementSpec(~100 tokens) 和安全边际之间
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }
}
```

**模型配置分层**（4 个 use case，各用各的 Bean）：

| Bean 名 | 用途 | max_tokens | 特殊配置 |
|---------|------|------------|----------|
| `reasoningStreamingChatModel` | 代码生成（流式） | 8192 | `@Scope("prototype")` |
| `structuredRoutingChatModel` | RequirementAgent / PlannerAgent | 512 | `json_object` 模式 |
| `routingChatModel` | 路由判断（CodeGenType） | 256 | 无 |
| `imageCollectionChatModel` | 图片关键词规划 | — | 独立配置 |

### 13.4 RequirementAgent 实现要点

```java
@Component
public class RequirementAgent implements Agent<String, RequirementSpec> {

    @Resource
    private RequirementLlmService requirementLlmService;

    @Override
    public RequirementSpec execute(String prompt, AgentContext ctx) {
        try {
            RequirementSpec spec = requirementLlmService.analyze(prompt);
            // 防御：LLM 可能返回 null 字段
            if (spec.getCodeGenType() == null) spec.setCodeGenType(CodeGenTypeEnum.HTML);
            if (spec.getComplexity() == null)  spec.setComplexity(Complexity.MEDIUM);
            return spec;
        } catch (Exception e) {
            // 降级：任何 LLM 失败都不抛异常，返回保守默认值
            return RequirementSpec.builder()
                    .codeGenType(CodeGenTypeEnum.HTML)
                    .complexity(Complexity.MEDIUM)
                    .features(new ArrayList<>())
                    .techConstraints(new ArrayList<>())
                    .build();
        }
    }
}
```

**核心原则**：Agent 内部所有异常必须被 catch，降级返回默认值，不向 Orchestrator 抛出。Orchestrator 层统一决定是否重试。

### 13.5 PlannerAgent 实现要点

```java
@Component
public class PlannerAgent implements Agent<RequirementSpec, TaskGraph> {

    @Override
    public TaskGraph execute(RequirementSpec spec, AgentContext ctx) {
        try {
            // RequirementSpec → JSON string → LLM → TaskGraph
            String specJson = Json.toJson(spec);   // LangChain4j 内置 Jackson 序列化
            TaskGraph graph = plannerLlmService.plan(specJson);
            // 钳位：防止 LLM 返回 99 次重试这种异常值
            int clamped = Math.min(3, Math.max(0, graph.suggestedRetries()));
            return new TaskGraph(graph.parallelizeAsset(), clamped,
                                 graph.codeGenHints(), graph.reviewFocusAreas());
        } catch (Exception e) {
            // 降级：基于 complexity 给保守默认值
            int defaultRetries = switch (spec.getComplexity()) {
                case SIMPLE -> 1; case MEDIUM -> 2; case COMPLEX -> 3;
                default -> 2;
            };
            return new TaskGraph(true, defaultRetries, null, null);
        }
    }
}
```

**为什么要 clamp suggestedRetries**：LLM 是概率模型，可能输出 99 或 -1，直接用会导致无限循环或从不重试。钳位到 [0, 3] 是防御性编程。

### 13.6 ReviewModelConfig 与 @ConditionalOnProperty

```java
@Configuration
public class ReviewModelConfig {

    // 满足 review.model.provider=openai（默认）时注册这个 Bean
    @Bean("reviewChatModel")
    @ConditionalOnProperty(name = "review.model.provider", havingValue = "openai", matchIfMissing = true)
    public ChatModel reviewChatModelOpenAi(...) { /* GPT-4o-mini */ }

    // 满足 review.model.provider=qwen 时注册这个 Bean
    @Bean("reviewChatModel")
    @ConditionalOnProperty(name = "review.model.provider", havingValue = "qwen")
    public ChatModel reviewChatModelQwen(...) { /* 通义千问 */ }
}
```

**关键理解**：两个 `@Bean` 方法起了相同名字 `"reviewChatModel"`，但 `@ConditionalOnProperty` 保证**永远只有一个**会被注册。这不依赖 `allow-bean-definition-overriding=true`（那个设置是项目原有配置，与此无关）。  

**为什么要异构模型**：代码是 CodeGenAgent（DeepSeek）生成的，如果 ReviewAgent 也用 DeepSeek，容易有"自己检查自己"的盲点。用 GPT-4o-mini 或通义千问能提供不同视角。

### 13.7 chat_history 持久化扩展（零 schema 变更）

```java
// 新增两个枚举值
AI_SUMMARY("AI summary", "ai_summary"),
AI_REVIEW_REPORT("AI review report", "ai_review_report");

// 新增接口方法（供 OrchestratorAgent 调用）
boolean addStructuredMessage(Long appId, String content, String messageType, Long userId);

// 实现：复用现有 addChatMessage，只加了 messageType 校验
public boolean addStructuredMessage(Long appId, String content, String messageType, Long userId) {
    // 只允许 AI 生成的结构化类型写入此方法
    if (!messageType.equals(ChatHistoryMessageTypeEnum.AI_SUMMARY.getValue()) &&
        !messageType.equals(ChatHistoryMessageTypeEnum.AI_REVIEW_REPORT.getValue())) {
        throw new IllegalArgumentException("Invalid messageType: " + messageType);
    }
    return addChatMessage(appId, content, messageType, userId);
}
```

### 13.8 踩坑：application-local.yml 重复 key

**现象**：`mvn test` 时所有 Spring 上下文测试报 `IllegalState Failed to load ApplicationContext`，根因是：

```
Caused by: found duplicate key langchain4j in 'reader', line 94
```

**原因**：Phase 2 初版把 `review-model` 配置写在了文件末尾的第二个 `langchain4j:` 块下，而 Spring Boot 3.5 升级了 SnakeYAML 到 2.x，后者对 YAML 规范更严格，**禁止重复 key**（之前的版本会静默合并，新版直接报错）。

**修复**：把 `review-model` 部分合并进第一个 `langchain4j:` 块，删除第二个重复块。

**教训**：YAML 的 `mapping` 里每个 key 只能出现一次。如果配置复杂，建议用 `---` 分隔多个 YAML 文档，或者拆分为多个 profile 文件。

### 13.9 测试策略

**原则**：所有 Agent 单元测试都用 Mockito，不启动 Spring 上下文，不调用真实 LLM。

```java
@ExtendWith(MockitoExtension.class)
class RequirementAgentTest {
    @Mock
    private RequirementLlmService requirementLlmService;  // Mock LLM 调用

    @InjectMocks
    private RequirementAgent requirementAgent;            // Mockito 注入 mock

    @Test
    void execute_llmThrowsException_returnsFallbackSpec() {
        when(requirementLlmService.analyze(anyString()))
            .thenThrow(new RuntimeException("API timeout"));

        RequirementSpec result = requirementAgent.execute("build a todo app", ctx);

        // 验证降级逻辑，不依赖 LLM 行为
        assertEquals(CodeGenTypeEnum.HTML, result.getCodeGenType());
        assertEquals(Complexity.MEDIUM, result.getComplexity());
    }
}
```

**要覆盖的 case**：
1. 正常返回（LLM 返回合法 JSON）
2. LLM 返回 null 字段（测试默认值填充）
3. LLM 抛异常（测试 fallback 分支）
4. 边界值（如 suggestedRetries 越界）

---

## 十四、Phase 3 详解：AssetAgent 迁移 + Bug #3 修复

### 14.1 新增/修改文件清单

```
新增：
config/ImageSearchExecutorConfig.java          ← Spring 管理的线程池 Bean
ai/multiagent/agent/AssetAgent.java            ← 图片收集 Agent

修改：
ai/langgraph4j/node/ImageCollectorNode.java    ← Bug #3 修复（移除静态线程池）
```

### 14.2 Bug #3 根因与修复

**根因**：

```java
// ImageCollectorNode.java（修复前）
public class ImageCollectorNode {
    // 问题：JVM 类加载时创建，和 Spring 容器生命周期完全无关
    // Spring 的 @PreDestroy / shutdown hook 不会调用 .shutdown()
    // 应用关闭时线程池里的线程会阻止 JVM 正常退出
    private static final ExecutorService IMAGE_EXECUTOR =
            Executors.newFixedThreadPool(3);
}
```

**什么时候会触发问题**：
- 生产环境优雅停机（`kill -15`）：Spring 上下文关闭，但这 3 个线程还活着，JVM 无法退出
- 测试环境：每次 `@SpringBootTest` 启动都创建新池，多次测试后线程泄漏

**修复方案**：

```java
// Step 1：新建 Spring 管理的 Bean
@Configuration
public class ImageSearchExecutorConfig {
    @Bean(name = "imageSearchExecutor", destroyMethod = "shutdown")
    public ExecutorService imageSearchExecutor() {
        return Executors.newFixedThreadPool(3);
    }
}
// destroyMethod = "shutdown"：Spring 容器关闭时自动调用 ExecutorService.shutdown()
// 这样线程池和 Spring 上下文同生命周期

// Step 2：ImageCollectorNode 内部改为用 SpringContextUtil 获取 Bean
ExecutorService imageExecutor =
    SpringContextUtil.getBean("imageSearchExecutor", ExecutorService.class);
```

**为什么用 `SpringContextUtil.getBean()` 而不是 `@Resource` 注入**：`ImageCollectorNode` 是 LangGraph4j 的节点，通过 `static create()` 工厂方法创建，不是 `@Component`，Spring 不管理它的实例，所以字段注入无效。`SpringContextUtil` 相当于手动获取 Spring Bean 的逃生通道。

### 14.3 AssetAgent 设计

**为什么不 implements Agent<String, String>**：用户明确指定不实现泛型接口。原因是 AssetAgent 的执行本质上是 IO 密集型并发任务（多个 HTTP 请求），用 `Agent<I,O>` 接口的语义（单次 LLM 调用）不合适，保持独立 class 更清晰。

```java
@Slf4j
@Component
public class AssetAgent {

    @Resource
    private ImagePlanningService imagePlanningService;   // LLM：生成关键词
    @Resource
    private ImageSearchTool imageSearchTool;             // HTTP：调 Pexels API
    @Resource(name = "imageSearchExecutor")
    private ExecutorService imageSearchExecutor;         // Spring 管理的线程池

    public String execute(String prompt, AgentContext ctx) { ... }
}
```

**并发执行流程**：

```
prompt
  │
  ▼ LLM 调用（同步）
planImageCollection(prompt)
  → ImageCollectionPlan { keywords: ["modern office", "team meeting", "workspace"] }
  │
  ▼ 对每个 keyword，提交给线程池异步执行
CompletableFuture.supplyAsync(() -> imageSearchTool.searchContentImages(keyword), imageExecutor)
  │
  ▼ 等待所有 Future 完成
CompletableFuture.allOf(...).join()
  │
  ▼ 汇总 URL（每个 keyword 最多取 3 张）
"https://pexels.com/img1.jpg\nhttps://pexels.com/img2.jpg\n..."
```

**容错设计**：每个 keyword 的搜索失败（`catch` 内返回 `""`）不影响其他 keyword。外层 `catch (Exception e)` 捕获 `planImageCollection` 失败，返回空字符串。整个 AssetAgent 失败不会阻断主流程（Orchestrator 层检查结果是否为空）。

**新旧路径共享线程池**：

```
agent=false 路径：
  ImageCollectorNode（LangGraph4j）
    └─ SpringContextUtil.getBean("imageSearchExecutor") ─┐
                                                          ├── 同一个线程池（3 threads）
agent=true 路径：                                         │
  AssetAgent                                              │
    └─ @Resource("imageSearchExecutor") ─────────────────┘
```

不存在两个池、6 个线程的情况。

### 14.4 测试 ExecutorService 注入问题

**问题**：`AssetAgent` 用 `@Resource(name = "imageSearchExecutor")` 注入 `ExecutorService`。Mockito 的 `@InjectMocks` 会尝试注入 `@Mock` 字段，但我们**不想 mock 线程池**（mock `ExecutorService` 后 `supplyAsync` 不会真正执行，`Future` 永远不完成）。

**解决方案**：让 Mockito 只 mock 业务对象，用 `ReflectionTestUtils` 手动注入真实线程池：

```java
@ExtendWith(MockitoExtension.class)
class AssetAgentTest {
    @Mock
    private ImagePlanningService imagePlanningService;  // mock LLM

    @Mock
    private ImageSearchTool imageSearchTool;            // mock HTTP 工具

    @InjectMocks
    private AssetAgent assetAgent;                      // imageSearchExecutor 字段为 null

    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(3);
        // 直接设置私有字段，绕过 Spring DI
        ReflectionTestUtils.setField(assetAgent, "imageSearchExecutor", executor);
    }

    @AfterEach
    void tearDown() {
        executor.shutdown();   // 测试结束后清理线程
    }
}
```

**`ReflectionTestUtils.setField()`** 是 Spring Test 提供的反射工具，专门用于在测试中设置私有字段。它不需要字段是 `public` 或有 setter。

**并发测试注意点**：`imageSearchTool.searchContentImages()` 在 `supplyAsync` 里被异步调用，此时在另一个线程上操作 Mockito mock 对象。Mockito 的 mock 是线程安全的（stub 读操作不修改状态），所以 `when().thenReturn()` 在多线程调用场景下是安全的。

---

## 十五、Phase 4 详解：CodeGenAgent + RefineAgent

### 15.1 新增/修改文件清单

```
新增：
ai/multiagent/agent/CodeGenAgent.java          ← Agent<CodeGenInput, Flux<String>>
ai/multiagent/agent/RefineAgent.java           ← Agent<RefineInput, Flux<String>>
ai/multiagent/llm/RefineAiService.java         ← LangChain4j AiService 接口
resources/prompt/refine-agent-system-prompt.txt

修改：
ai/tools/FileReadTool.java      ← 加 baseDir 构造函数
ai/tools/FileModifyTool.java    ← 同上
ai/tools/FileDirReadTool.java   ← 同上
ai/tools/FileWriteTool.java     ← 同上（RefineAgent 不用，保持工具类一致性）
ai/tools/FileDeleteTool.java    ← 同上（RefineAgent 不用，同上）
core/CodeFileSaver.java         ← 加 resolveOutputDir() 静态方法
```

### 15.2 CodeGenAgent — 三行 Wrapper 的设计哲学

```java
@Component
public class CodeGenAgent implements Agent<CodeGenInput, Flux<String>> {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Override
    public Flux<String> execute(CodeGenInput input, AgentContext ctx) {
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(
                input.buildPrompt(),
                input.requirementSpec().getCodeGenType(),
                ctx.appId()
        );
    }
}
```

**为什么只有三行**：`AiCodeGeneratorFacade.generateAndSaveCodeStream()` 已经完整实现了：
1. 从 `AiCodeGeneratorServiceFactory` 获取 prototype 实例（并发安全）
2. `switch(codeGenType)` 路由三条分支（HTML / MULTI_FILE / VUE_PROJECT）
3. HTML/MULTI_FILE：`doOnComplete` 时自动解析并保存文件
4. VUE_PROJECT：`onCompleteResponse` 时同步 npm build，build 完成后 Flux 才 complete

CodeGenAgent 作为 wrapper，不重复任何逻辑，符合"复用现有 Factory，不重新实现"的约束。

**`CodeGenInput.buildPrompt()` 的输出结构**：

```
{projectDescription}

## Available Image Resources          ← 仅当 imageListStr 非空时
Please use the following images ...
{imageListStr}

## Additional Instructions             ← 仅当 taskGraph.codeGenHints() 非空时
{codeGenHints}
```

### 15.3 路径唯一真相：CodeFileSaver.resolveOutputDir()

**问题背景**：CodeGenAgent 完成后，OrchestratorAgent 需要知道文件保存在哪里，才能把路径传给 `RefineInput.generatedCodeDir`。如果每处代码各自拼路径字符串，未来一改就出 bug。

**解决**：在 `CodeFileSaver`（已有 `buildUniqueDir` 的地方）新增一个 public 静态方法：

```java
// 命名规则来自 CodeFileSaver.buildUniqueDir()：
// dirName = codeGenType.getValue() + "_" + appId
// 例：html_888, multi_file_888, vue_project_888
public static String resolveOutputDir(CodeGenTypeEnum type, Long appId) {
    return FILE_SAVE_ROOT_DIR + File.separator + type.getValue() + "_" + appId;
}
```

OrchestratorAgent 调用：
```java
String generatedCodeDir = CodeFileSaver.resolveOutputDir(
    input.requirementSpec().getCodeGenType(), ctx.appId()
);
// 直接传给 RefineInput
```

**为什么不让 CodeGenAgent 返回路径**：`Agent<CodeGenInput, Flux<String>>` 的返回类型已固定为 `Flux<String>`（流式 token）。路径是确定性的（appId + codeGenType → 固定目录），不需要通过返回值传递。

### 15.4 File Tools 改造：双构造模式

**发现的问题**：所有 5 个 File Tools 的 `resolveSafePath()` 都硬编码了 `vue_project_{appId}` 路径。RefineAgent 需要访问 `html_{appId}` 和 `multi_file_{appId}` 目录，工具无法支持。

**为什么不新建一套工具类**（被否掉的方案）：
- `resolveSafePath` 的路径遍历防护逻辑（`normalize()` + `startsWith()` 安全校验）是安全性代码，最不应该有两份独立实现
- 一处有 bug，另一处不一定同步修复

**最终方案：双构造函数**

```java
public class FileReadTool {
    private final String baseDir;   // null = 旧路径；非 null = 新路径（完整绝对路径）

    // 旧路径：AiCodeGeneratorServiceFactory 用，new FileReadTool() → @ToolMemoryId 注入 appId
    public FileReadTool() {
        this.baseDir = null;
    }

    // 新路径：RefineAgent 用，传入 generatedCodeDir 的完整路径
    public FileReadTool(String baseDir) {
        this.baseDir = baseDir;
    }

    @Tool(...)
    public String readFile(@P(...) String relativePath, @ToolMemoryId Long appId) {
        // 只改这一行分支，其余路径遍历防护完全不变
        Path projectRoot = (baseDir != null)
                ? Paths.get(baseDir)                                              // 新路径
                : Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, "vue_project_" + appId); // 旧路径
        ...
    }
}
```

**`@ToolMemoryId Long appId` 为什么必须保留**：这是 LangChain4j 框架的参数标记，表示"由框架从 ChatMemory ID 注入，不由 LLM 提供"。如果移除这个参数，框架会把 `appId` 当成 LLM 需要提供的参数，导致工具签名错误。新路径下 `appId` 被忽略，但签名必须保留。

**影响面评估**（事前 grep 确认）：全项目只有一处实例化 File Tools：
```java
// AiCodeGeneratorServiceFactory.java:105-109
new FileWriteTool()   // 无参，不受影响
new FileReadTool()    // 无参，不受影响
new FileDirReadTool() // 无参，不受影响
new FileModifyTool()  // 无参，不受影响
new FileDeleteTool()  // 无参，不受影响
```
`AiCodeGeneratorServiceFactory` **未做任何修改**，agent=false 路径零退化。

### 15.5 最小权限原则：RefineAgent 的工具配置

```java
RefineAiService refineService = AiServices.builder(RefineAiService.class)
    .streamingChatModel(model)
    .chatMemoryProvider(memoryId -> chatMemory)
    .tools(
        new FileDirReadTool(generatedCodeDir),  // 看目录结构
        new FileReadTool(generatedCodeDir),     // 读文件内容（先读再改）
        new FileModifyTool(generatedCodeDir)    // 精确替换（oldContent 找不到→报错，不覆盖）
    )
    // FileWriteTool → 整体覆写，修复阶段风险太高，不给
    // FileDeleteTool → 修复阶段删文件风险更高，不给
    .hallucinatedToolNameStrategy(req ->
            ToolExecutionResultMessage.from(req, "Error: no tool called " + req.name()))
    .maxSequentialToolsInvocations(20)
    .build();
```

**最小权限原则（Principle of Least Privilege）在 Agent 设计里的体现**：给 Agent 的工具权限应该刚好够用，不能多。RefineAgent 的职责是"修复已有代码的缺陷"，不需要创建新文件（FileWriteTool）或删除文件（FileDeleteTool）。给多余权限意味着 LLM 出现幻觉或理解偏差时，损害范围更大。

**`FileModifyTool` vs `FileWriteTool` 的关键区别**：

| 工具 | 行为 | 修复场景适用性 |
|------|------|----------------|
| `FileModifyTool` | 找到 `oldContent` → 替换为 `newContent`；找不到 → 报错 | ✅ 强迫 LLM 先读文件，精确定位 |
| `FileWriteTool` | 直接覆盖整个文件 | ❌ LLM 可能遗漏文件中其他正确的部分 |

### 15.6 RefineAgent 的流式架构

**为什么 RefineAiService 返回 TokenStream 而不是 Flux<String>**：

```java
public interface RefineAiService {
    @SystemMessage(fromResource = "prompt/refine-agent-system-prompt.txt")
    TokenStream refine(@UserMessage String refinementRequest);
}
```

- `Flux<String>`：适合简单流式对话，**不支持工具调用**
- `TokenStream`：支持工具调用的流式接口，可以监听 `onPartialResponse`（token）和 `onToolExecuted`（工具执行结果）两个事件

RefineAgent 有工具调用（FileDirReadTool / FileReadTool / FileModifyTool），必须用 `TokenStream`。

**TokenStream → Flux<String> 的桥接**（与 `AiCodeGeneratorFacade.processTokenStream` 相同模式）：

```java
return Flux.create(sink -> tokenStream
    .onPartialResponse(sink::next)          // LLM 输出 token → 发到 Flux
    .onToolExecuted(exec -> log.info(...))  // 工具执行 → 只记日志，不发到 Flux
    .onCompleteResponse(response -> sink.complete())
    .onError(error -> sink.error(new RuntimeException(error.getMessage())))
    .start());
```

工具执行过程（`onToolExecuted`）不发到 Flux，只发 LLM 的文字输出 token。前端看到的是 RefineAgent 的"思考过程 + 修改说明"，看不到内部工具调用细节。

**RefineAgent 为什么不用 Redis 持久化 ChatMemory**：

```java
// 使用纯内存 ChatMemory，不注入 RedisChatMemoryStore
MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
    .id(ctx.appId())
    .maxMessages(30)
    .build();
```

每次 RefineAgent 调用都是一次全新的"读文件→分析→修改"对话，不需要跨请求保留对话历史。用 Redis 反而会引入旧的上下文干扰，增加 token 消耗。

### 15.7 buildRefinementRequest() — 把 ReviewReport 序列化进 Prompt

```java
public String buildRefinementRequest(RefineInput input) {
    String reviewJson = Json.toJson(input.reviewReport()); // LangChain4j 内置 Jackson
    ...
    return String.format("""
        Code directory: %s
        Attempt: %d of 3

        Review Report JSON:
        %s

        Additional instructions: %s

        Focus on these files: %s
        ...
        """,
        input.generatedCodeDir(),
        input.refinementPlan().attemptNumber(),
        reviewJson,
        ...
    );
}
```

**为什么把 ReviewReport 序列化为 JSON 而不是自然语言描述**：

1. JSON 格式有精确的 severity / file / description / suggestion 字段，LLM 不会误读
2. system prompt 已经告诉 LLM ReviewReport 的 JSON 结构，它知道怎么解析
3. 自然语言描述需要额外的提示工程，且 LLM 可能丢失细节（尤其是多个 issue 时）

**`dev.langchain4j.internal.Json`**：LangChain4j 内置的 Jackson 工具类，在 Agent 内部序列化用它比引入 hutool 的 `JSONUtil` 更一致（同一个 Jackson 实例，相同的序列化配置）。

### 15.8 三条设计决策的决策过程

**决策 a：CodeGenAgent 内部实现**

```
Option A: 大 switch，各分支自己实现
  ✗ 重复 AiCodeGeneratorFacade 里已有的完整逻辑
Option B: 三个子类 + 工厂
  ✗ 过度工程，OCP 收益在此场景不存在
Option C: wrap AiCodeGeneratorFacade（选用）
  ✓ 三行代码，零重复，facade 里的 prototype/并发/文件保存逻辑全部复用
```

**决策 b/c：RefineAgent 读写文件**

关键发现（事前读代码）：所有 File Tools 硬编码 `vue_project_{appId}`，不支持 HTML/MULTI_FILE 目录。

```
Option B（原始）：直接用现有 File Tools
  ✗ 只能访问 vue_project_ 目录
新建 RefineFileReadTool + RefineFileModifyTool：
  ✗ resolveSafePath 安全逻辑两份，一处有 bug 另一处不同步
改造现有 File Tools（选用）：
  ✓ 加 baseDir 构造函数，无参路径不动，AiCodeGeneratorServiceFactory 不需改
  ✓ 安全逻辑只有一份
  ✓ RefineAgent 传 generatedCodeDir 完整路径，三种 CodeGenType 全支持
```

**影响面评估方法**：先 `grep "new FileReadTool\|new FileModifyTool..."` 确认实例化点，发现全项目只有 `AiCodeGeneratorServiceFactory` 一处，且用无参构造，改造后完全不影响。这是做改动前的标准动作——**先 grep 实例化点再做判断**。

### 15.9 测试策略

**CodeGenAgentTest（单元）**：mock `AiCodeGeneratorFacade`，用 `verify()` 确认三种类型都路由正确，用 `argThat(prompt -> ...)` 验证 prompt 内容包含 projectDescription + codeGenHints + imageListStr。

**RefineAgentTest（单元）**：直接调用 `refineAgent.buildRefinementRequest()`（public 方法），验证 ReviewReport 被正确 JSON 序列化进 prompt（包含 BLOCKER/MAJOR severity、文件名、描述）。不需要 Spring 上下文，不需要 mock LLM。

**CodeGenAgentIntegrationTest（集成）**：`@SpringBootTest` + 真实 DeepSeek API，生成一个最简单的 HTML 页面（"hello world + one paragraph"，约 17s），验证 `CodeFileSaver.resolveOutputDir(HTML, 888L)` 下的 `index.html` 被创建且非空。

**RefineAgent 集成测试**：P4 阶段 skip，等 P5 ReviewAgent 完成后做完整的"生成→审查→修复"链路测试。
