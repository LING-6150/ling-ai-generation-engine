# Multi-Agent 重构学习笔记

> 项目：Ling-AI-CODE-generation  
> 目标：把 `agent=true` 路径从单一线性工作流重构为 Multi-Agent 架构  
> 状态：Phase 1 已完成

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
| P2 | Non-streaming Agents | RequirementAgent + PlannerAgent + ReviewModelConfig | 待开始 |
| P3 | AssetAgent 迁移 | AssetAgent + Bug #3 修复 | 待开始 |
| P4 | CodeGenAgent + RefineAgent | 流式生成 Agent | 待开始 |
| P5 | ReviewAgent | 异构模型审查 | 待开始 |
| P6 | OrchestratorAgent 全链路 | 完整编排 + Bug #1 修复 + fix #4 #5 #6 | 待开始 |
| P7 | SSE 流式升级 | token 实时透传 + 监控 agent 维度 | 待开始 |

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
