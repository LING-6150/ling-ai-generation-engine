# 🤖 Ling-AI-CODE-generation

> **AI-Powered Web Application Generation Platform** — Generate production-ready websites and Vue 3 projects from natural language descriptions, with real-time streaming, intelligent workflow orchestration, and enterprise-grade observability.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)](https://spring.io/projects/spring-boot)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-1.1.0-blue)](https://github.com/langchain4j/langchain4j)
[![LangGraph4j](https://img.shields.io/badge/LangGraph4j-1.6.0-purple)](https://github.com/bsorrentino/langgraph4j)
[![Vue](https://img.shields.io/badge/Vue-3-brightgreen)](https://vuejs.org/)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Live Demo](#live-demo)
- [Core Features](#core-features)
- [Architecture](#architecture)
- [Tech Stack Highlights](#tech-stack-highlights)
- [Key Technical Achievements](#key-technical-achievements)
- [Getting Started](#getting-started)
- [MCP Server Integration](#mcp-server-integration)
- [Observability](#observability)
- [Project Structure](#project-structure)

---

## Overview

Ling-AI-CODE-generation is a full-stack AI platform that lets users describe a website in plain English and instantly receive a working, deployable web application. The platform supports three generation modes — single HTML, multi-file static sites, and full Vue 3 + Vite projects — with real-time streaming output, AI-powered workflow orchestration, and production-grade system optimization.

**Built for**: North American SDE internship interviews, demonstrating distributed systems, AI integration, and engineering best practices.

---

## Live Demo

> *(Screenshots placeholder — add your screenshots here)*

| Feature | Screenshot |
|---|---|
| Home page with prompt input | `screenshots/home.png` |
| Real-time code generation (SSE) | `screenshots/chat.png` |
| Live Preview | `screenshots/preview.png` |
| Grafana monitoring dashboard | `screenshots/grafana.png` |
| MCP Inspector connected | `screenshots/mcp-inspector.png` |

---

## Core Features

### 🚀 Three Generation Modes

| Mode | Description | Use Case |
|---|---|---|
| **HTML** | Single self-contained HTML file | Landing pages, portfolios |
| **Multi-file** | Separate HTML + CSS + JS | Static multi-page sites |
| **Vue Project** | Full Vue 3 + Vite + npm build | Complex SPAs with routing |

### ⚡ Real-time Streaming (SSE)
- Code streams token by token via Server-Sent Events
- Live Preview updates automatically as generation completes
- Custom `event:done` signal for precise completion detection

### 🧠 AI Workflow Orchestration (LangGraph4j)
- 6-node directed graph: Image Collection → Prompt Enhancement → Routing → Code Generation → Quality Check → Build
- Conditional edges with automatic retry on quality check failure
- Real-time SSE progress events per workflow step

### 🔧 AI Agent with File Tools
- 5 file operation tools: `writeFile`, `readFile`, `readDir`, `modifyFile`, `deleteFile`
- Incremental modification workflow: AI reads existing code before making targeted changes
- Security: path traversal protection, important file deletion prevention

### 📊 Enterprise Observability
- Custom Prometheus metrics: request count, token usage, response time, error rate
- Grafana dashboard with 11 panels
- `ChatModelListener` hooks into LangChain4j model lifecycle

### 🔌 MCP Server Integration
- Standalone MCP Server exposing code generation as standardized tools
- SSE transport, verified via MCP Inspector
- `generateWebsite` and `listRecentApps` tools available to any MCP client

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (Vue 3)                         │
│  HomeView → AppChatView → Live Preview (iframe) → AppDetailView │
└──────────────────────────┬──────────────────────────────────────┘
                           │ SSE / REST API
┌──────────────────────────▼──────────────────────────────────────┐
│                    Spring Boot Backend (8123)                     │
│                                                                   │
│  ┌─────────────┐  ┌──────────────────┐  ┌──────────────────┐    │
│  │AppController│  │  AppServiceImpl   │  │AiCodeGenerator   │    │
│  │             │  │  + Rate Limiting  │  │Facade            │    │
│  │  SSE Stream │  │  + Auth Check     │  │                  │    │
│  └──────┬──────┘  └────────┬─────────┘  └────────┬─────────┘    │
│         │                  │                      │               │
│  ┌──────▼──────────────────▼──────────────────────▼─────────┐   │
│  │              AI Layer (LangChain4j 1.1.0)                 │   │
│  │                                                            │   │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐  │   │
│  │  │ AiCodeGen   │  │  LangGraph4j │  │  File Operation │  │   │
│  │  │ Service     │  │  Workflow    │  │  Tools (5)      │  │   │
│  │  │ (DeepSeek)  │  │  (6 nodes)  │  │                 │  │   │
│  │  └─────────────┘  └──────────────┘  └─────────────────┘  │   │
│  └────────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐   │
│  │  MySQL 8.x   │  │  Redis       │  │  Prometheus          │   │
│  │  (App/User)  │  │  (Session +  │  │  + Grafana           │   │
│  │              │  │  Chat Memory │  │  (Observability)     │   │
│  │              │  │  + Cache)    │  │                      │   │
│  └──────────────┘  └──────────────┘  └──────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                           │
         ┌─────────────────▼──────────────────┐
         │   ling-codegen-mcp-server (8127)    │
         │   Spring AI MCP Server (SSE)        │
         │   Tools: generateWebsite,           │
         │          listRecentApps             │
         └────────────────────────────────────┘
```

---

## Tech Stack Highlights

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Virtual threads for async Vue builds |
| Spring Boot | 3.5.x | Core framework |
| LangChain4j | 1.1.0 | AI model integration, tool calling, AI Services |
| LangGraph4j | 1.6.0-rc2 | AI workflow orchestration (directed graph) |
| MyBatis Flex | 1.11.0 | ORM with snowflake ID generation |
| Redisson | 3.50.0 | Distributed rate limiting (token bucket) |
| Micrometer | 1.15.x | Metrics facade |
| Spring AI | 1.0.0-SNAPSHOT | MCP Server implementation |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| Vue 3 | Latest | Reactive UI framework |
| Vite | Latest | Build tool |
| Ant Design Vue | 4.x | UI component library |
| TypeScript | Latest | Type safety |

### Infrastructure
| Technology | Purpose |
|---|---|
| MySQL 8.x | Primary database |
| Redis | Session, chat memory, distributed cache |
| Prometheus | Metrics collection (pull-based) |
| Grafana | Metrics visualization |
| Docker Compose | Local monitoring environment |

---

## Key Technical Achievements

### 1. 🔄 Concurrent AI Request Handling — Prototype Scope Pattern

**Problem**: LangChain4j's `StreamingChatModel` internally uses `SpringRestClient` for synchronous SSE stream parsing. Despite returning `Flux<String>`, concurrent requests were serialized — each blocked until the previous finished.

**Solution**: Applied Spring's `@Scope("prototype")` to all streaming model Config beans, combined with `SpringContextUtil.getBean()` for dynamic instance retrieval (not `@Resource` injection which would still be singleton).

```java
// Each request gets a fresh StreamingChatModel instance — no blocking
@Bean
@Scope("prototype")
public OpenAiStreamingChatModel openAiStreamingChatModel() { ... }

// Dynamic retrieval in factory — creates new instance every call
StreamingChatModel model = SpringContextUtil.getBean(
    "openAiStreamingChatModel", StreamingChatModel.class);
```

**Impact**: Eliminated request serialization, enabling true concurrent AI code generation.

---

### 2. 🕸️ LangGraph4j Workflow with Conditional Retry

**Design**: 6-node directed acyclic graph with a conditional edge implementing automatic quality-check retry:

```
ImageCollector → PromptEnhancer → Router → CodeGenerator
                                               ↓
                                         QualityCheck
                                        /     |      \
                               isValid=true  isValid=false  VUE_PROJECT
                              /              |              \
                           END         CodeGenerator      ProjectBuilder
                                       (retry loop)
```

**Key Implementation**:
- `WorkflowContext` stored as key/value inside `AgentState` (not a custom state class)
- All nodes use static factory methods + `SpringContextUtil.getBean()` for Spring integration
- Java 21 virtual threads run the workflow asynchronously, SSE pushes per-step progress
- Cross-thread context passing: `onRequest` stores context in `requestContext.attributes()` for safe retrieval in `onResponse`/`onError`

---

### 3. 🛠️ AI Agent with Incremental File Modification

**Challenge**: Regenerating the entire Vue project for every modification wastes tokens and overwrites manual edits.

**Solution**: 5 file operation tools enable precise incremental changes:

```
User: "Change the navbar color to dark blue"
  → AI calls readDir() to understand project structure
  → AI calls readFile("src/App.vue") to get exact current content
  → AI calls modifyFile() with precise oldContent → newContent replacement
  → Only the specific CSS value changes, all other files untouched
```

**Security measures**:
- All paths validated to stay within `vue_project_{appId}/` directory
- `FileDeleteTool` maintains a protected file list (`package.json`, `vite.config.js`, etc.)
- Relative paths only — absolute paths rejected to prevent path traversal

---

### 4. 📡 ChatModelListener for AI Observability

**Design**: Implements LangChain4j's `ChatModelListener` — works for both `ChatModel` and `StreamingChatModel` with a single implementation.

**Cross-thread context challenge**: In streaming mode, `onResponse`/`onError` callbacks may execute on different threads than `onRequest`. Solution:

```java
// onRequest (caller thread) — ThreadLocal is safe
MonitorContext context = MonitorContextHolder.getContext();
requestContext.attributes().put(MONITOR_CONTEXT_KEY, context); // store for cross-thread access

// onResponse (potentially different thread) — read from attributes, not ThreadLocal
MonitorContext context = (MonitorContext) responseContext.attributes().get(MONITOR_CONTEXT_KEY);
```

**Metrics exposed**:
- `ai_model_requests_total` (Counter, tagged: user_id, app_id, model_name, status)
- `ai_model_tokens_total` (Counter, tagged: token_type = input/output/total)
- `ai_model_response_duration_seconds` (Timer, auto-calculates percentiles)
- `ai_model_errors_total` (Counter, tagged: error_message)

---

### 5. 🔒 Distributed Rate Limiting with Redisson

```java
// Token bucket algorithm — allows bursts, enforces long-term average
RRateLimiter rateLimiter = redissonClient.getRateLimiter("rate_limit:user:" + userId);
rateLimiter.trySetRate(RateType.OVERALL, 5, 60, RateIntervalUnit.SECONDS); // 5 req/min
rateLimiter.expire(Duration.ofHours(1)); // prevent key accumulation
if (!rateLimiter.tryAcquire(1)) {
    throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, "...");
}
```

**SSE-aware error handling**: Rate limit exceptions occur before the SSE stream opens. Custom `GlobalExceptionHandler` detects SSE requests and writes errors as `event:business-error` SSE events instead of HTTP status codes.

---

### 6. 🔑 Multi-Model Routing Architecture

Five separate AI model beans with distinct configurations:

| Bean | Model | Config | Purpose |
|---|---|---|---|
| `openAiChatModel` | deepseek-chat | `response_format: json_object` | Structured output |
| `openAiStreamingChatModel` | deepseek-chat | Streaming, `@Scope("prototype")` | HTML/MULTI_FILE generation |
| `reasoningStreamingChatModel` | deepseek-chat | Tool calling, `@Scope("prototype")` | VUE_PROJECT generation |
| `routingChatModelPrototype` | deepseek-chat | `max_tokens: 256` | AI routing classification |
| `imageCollectionChatModel` | deepseek-chat | No `response_format` | Pexels image collection |

> **Key insight**: `imageCollectionChatModel` must NOT have `response_format: json_object` — DeepSeek skips tool invocation and returns JSON directly when this flag is set.

---

### 7. ⚡ Vue Project Build Pipeline

```
AI generates Vue 3 source files via tool calls
          ↓
onCompleteResponse() callback triggered (synchronous in stream lifecycle)
          ↓
VueProjectBuilder.buildProject() executes:
  → npm install (5min timeout)
  → npm run build (3min timeout)
  → validates dist/ directory exists
          ↓
sink.complete() — frontend receives done signal
          ↓
User can immediately preview — no manual refresh needed
```

**Why synchronous build?** Async build (Java 21 virtual threads) was the initial approach but caused "generation complete but preview blank" UX confusion. Synchronous build ensures the `dist/` directory is ready the moment the SSE stream ends.

---

## Getting Started

### Prerequisites

- Java 21+
- Node.js 20+
- MySQL 8.x
- Redis
- Docker (for Prometheus + Grafana)
- DeepSeek API Key ([get one here](https://platform.deepseek.com/))
- Pexels API Key ([get one here](https://www.pexels.com/api/))

### Backend Setup

**1. Clone and configure**

```bash
git clone https://github.com/LING-6150/Ling-AI-CODE-generation.git
cd Ling-AI-CODE-generation
```

**2. Create database**

```sql
CREATE DATABASE ling_ai_code_generation;
USE ling_ai_code_generation;
-- Run the SQL scripts in /sql directory
```

**3. Create `application-local.yml`** (gitignored)

```yaml
langchain4j:
  open-ai:
    chat-model:
      base-url: https://api.deepseek.com
      api-key: YOUR_DEEPSEEK_API_KEY
      model-name: deepseek-chat
      max-tokens: 8192
      response-format: json_object
      timeout: 120s
      log-requests: true
      log-responses: true
    streaming-chat-model:
      base-url: https://api.deepseek.com
      api-key: YOUR_DEEPSEEK_API_KEY
      model-name: deepseek-chat
      max-tokens: 8192
    reasoning-streaming-chat-model:
      base-url: https://api.deepseek.com
      api-key: YOUR_DEEPSEEK_API_KEY
      model-name: deepseek-chat
      max-tokens: 8192
    routing-chat-model:
      base-url: https://api.deepseek.com
      api-key: YOUR_DEEPSEEK_API_KEY
      model-name: deepseek-chat
      max-tokens: 256
    image-collection-chat-model:
      base-url: https://api.deepseek.com
      api-key: YOUR_DEEPSEEK_API_KEY
      model-name: deepseek-chat
      max-tokens: 2048

pexels:
  api-key: YOUR_PEXELS_API_KEY

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ling_ai_code_generation
    username: root
    password: YOUR_MYSQL_PASSWORD
  data:
    redis:
      host: localhost
      port: 6379
  session:
    store-type: redis
  ai:
    embedding:
      model:
        enabled: false
```

**4. Start the backend**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Backend runs at: `http://localhost:8123`

### Frontend Setup

```bash
cd Ling-AI-CODE-generation-frontend
npm install
npm run dev
```

Frontend runs at: `http://localhost:5173`

### Monitoring Setup (Optional)

```bash
# From project root directory
docker-compose up -d

# Prometheus: http://localhost:9090
# Grafana:    http://localhost:3000 (admin / admin123)
```

Import `grafana-dashboard.json` in Grafana for the pre-built 11-panel dashboard.

---

## MCP Server Integration

A standalone MCP (Model Context Protocol) Server exposes the code generation capability as standardized tools, enabling any MCP-compatible client to create applications programmatically.

**Repository**: [ling-codegen-mcp-server](https://github.com/LING-6150/ling-codegen-mcp-server)

### Available Tools

| Tool | Description |
|---|---|
| `generateWebsite(prompt)` | Creates a new app from a natural language description, returns appId + chat URL |
| `listRecentApps()` | Returns a list of recently featured applications |

### Architecture

```
MCP Client (Claude Desktop / MCP Inspector)
    ↕ SSE Transport (http://localhost:8127/sse)
ling-codegen-mcp-server (Spring AI 1.0.0-SNAPSHOT)
    ↕ REST API
Ling-AI-CODE-generation backend (http://localhost:8123)
```

### Running the MCP Server

```bash
git clone https://github.com/LING-6150/ling-codegen-mcp-server.git
cd ling-codegen-mcp-server

# Update application.yml with your session cookie
# (copy SESSION=xxx from browser DevTools → Network → any request → Cookie header)

mvn spring-boot:run
```

### Verification with MCP Inspector

```bash
npx @modelcontextprotocol/inspector http://localhost:8127/sse
```

Open the Inspector UI → Connection Type: **Via Proxy** → Connect:

- ✅ Connected to `ling-codegen-mcp-server v1.0.0`
- ✅ Tools registered: `generateWebsite`, `listRecentApps`

> **Note**: Claude Desktop integration is pending Spring AI MCP protocol version alignment (Claude Desktop uses `2025-11-25`, current Spring AI uses `2024-11-05`).

---

## Observability

### Prometheus Metrics

After starting the backend, metrics are available at:
`http://localhost:8123/api/actuator/prometheus`

Custom AI metrics:

```
# Request tracking (status: started / success / error)
ai_model_requests_total{user_id="...", app_id="...", model_name="deepseek-chat", status="success"} 42.0

# Token consumption breakdown
ai_model_tokens_total{token_type="input"} 16800.0
ai_model_tokens_total{token_type="output"} 180000.0
ai_model_tokens_total{token_type="total"} 196800.0

# Response time (Timer auto-computes count, sum, max, percentiles)
ai_model_response_duration_seconds_sum{...} 524.7
```

### Grafana Dashboard

The pre-built dashboard (`grafana-dashboard.json`) includes 11 panels:

| Panel | Type | Description |
|---|---|---|
| Total Requests | Stat | Successful request count |
| Error Count | Stat | Error count (turns red if > 0) |
| Total Tokens | Stat | Cumulative token consumption |
| Avg Response Time | Stat | Yellow > 15s, Red > 30s |
| Request Rate | Time series | Success/error per minute |
| Token Usage Over Time | Time series | Input vs output trends |
| Response Time Distribution | Time series | avg + max |
| Token by Type | Pie chart | Input/output ratio |
| Requests by User | Table | Sorted by count |
| Token by User | Table | Sorted by consumption |
| Error Rate | Time series | Percentage, red if > 10% |

---

## Project Structure

```
Ling-AI-CODE-generation/
├── src/main/java/com/ling/lingaicodegeneration/
│   ├── ai/
│   │   ├── AiCodeGeneratorService.java          # AI Service interface (LangChain4j)
│   │   ├── AiCodeGeneratorServiceFactory.java   # Factory with prototype scope handling
│   │   ├── AiCodeGenTypeRoutingService.java      # AI routing (structured enum output)
│   │   ├── ImageCollectionService.java           # Pexels image collection
│   │   ├── CodeQualityCheckService.java          # AI code quality validation
│   │   ├── guardrail/                            # Input/Output Guardrails
│   │   ├── langgraph4j/
│   │   │   ├── node/                             # 6 workflow nodes
│   │   │   ├── state/WorkflowContext.java        # Workflow state carrier
│   │   │   └── workflow/CodeGenWorkflow.java      # LangGraph4j directed graph
│   │   └── tools/                               # 6 file operation tools + ImageSearchTool
│   ├── config/
│   │   ├── StreamingChatModelConfig.java        # @Scope("prototype") streaming model
│   │   ├── ReasoningStreamingChatModelConfig.java
│   │   ├── RoutingAiModelConfig.java
│   │   └── RedisCacheManagerConfig.java         # Redis cache with TTL config
│   ├── controller/
│   │   ├── AppController.java                   # SSE + REST endpoints
│   │   └── StaticResourceController.java        # Serves generated website files
│   ├── core/
│   │   ├── AiCodeGeneratorFacade.java            # Unified entry point (Facade pattern)
│   │   ├── builder/VueProjectBuilder.java        # npm install + build
│   │   └── handler/                             # Stream handlers (Strategy pattern)
│   ├── monitor/
│   │   ├── MonitorContext.java
│   │   ├── MonitorContextHolder.java             # ThreadLocal context
│   │   ├── AiModelMetricsCollector.java          # Micrometer metrics
│   │   └── AiModelMonitorListener.java           # ChatModelListener implementation
│   ├── ratelimit/
│   │   ├── annotation/RateLimit.java
│   │   └── aspect/RateLimitAspect.java           # Redisson token bucket
│   └── service/
│       ├── AppService.java
│       └── impl/AppServiceImpl.java
├── src/main/resources/
│   ├── prompt/                                  # System prompts for all AI services
│   └── application.yml
├── prometheus.yml                               # Prometheus scrape config
├── docker-compose.yml                           # Prometheus + Grafana
└── grafana-dashboard.json                       # Pre-built 11-panel dashboard
```

---

## Design Patterns Used

| Pattern | Where Applied | Purpose |
|---|---|---|
| **Facade** | `AiCodeGeneratorFacade` | Unified entry for generation + file saving |
| **Strategy** | `StreamHandlerExecutor` | Different stream handlers per generation mode |
| **Factory** | `AiCodeGeneratorServiceFactory` | Create AI Service instances per request |
| **Adapter** | `processTokenStream()` | Convert `TokenStream` → `Flux<String>` |
| **Observer** | `ChatModelListener` | Hook into model lifecycle for metrics |

---

## Author

**Ling Duan** — MS Information Systems, Northeastern University  
GitHub: [@LING-6150](https://github.com/LING-6150)

---

*Built with ❤️ using Spring Boot 3, LangChain4j, LangGraph4j, and Vue 3*
