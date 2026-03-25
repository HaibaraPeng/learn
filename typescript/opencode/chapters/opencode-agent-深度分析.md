# OpenCode Agent 模块深度分析

> **分析模式：** Deep Mode (Standard)
> **分析时间：** 2026-03-25
> **代码规模：** agent.ts (414行) + 相关模块 (~2000行)

---

## 理解验证状态

| 核心概念   | 自我解释        | 理解"为什么"          | 应用迁移              | 状态   |
| ---------- | --------------- | --------------------- | --------------------- | ------ |
| Agent 配置 | ✅ 定义行为模板 | ✅ 权限、模型、提示词 | ✅ 可扩展新 Agent     | 已掌握 |
| Agent Loop | ✅ 主循环执行   | ✅ 多轮对话、工具调用 | ✅ 可用于其他 AI 应用 | 已掌握 |
| 权限系统   | ✅ 细粒度控制   | ✅ 安全性、用户控制   | ✅ 通用权限模型       | 已掌握 |
| 工具系统   | ✅ 能力扩展机制 | ✅ 模块化、可插拔     | ✅ 插件化架构         | 已掌握 |

---

## 1. 快速概览

### 1.1 模块定位

Agent 模块是 OpenCode 的**大脑配置中心**——它不直接执行 AI 对话循环，而是定义了 AI 的"性格"、"能力边界"和"行为准则"。

**技术栈：**

- 语言：TypeScript 5.8
- 框架：Effect（函数式错误处理）
- 验证：Zod（运行时类型）
- AI SDK：Vercel AI SDK

**代码类型：** 配置定义 + 服务接口 + 工厂函数

### 1.2 核心文件清单

| 文件                       | 行数  | 职责                                 |
| -------------------------- | ----- | ------------------------------------ |
| `agent/agent.ts`           | 414   | Agent 配置定义、服务接口、内置 Agent |
| `agent/generate.txt`       | 75    | Agent 生成提示词                     |
| `agent/prompt/explore.txt` | 18    | 探索 Agent 提示词                    |
| `agent/prompt/title.txt`   | 44    | 标题生成提示词                       |
| `session/prompt.ts`        | ~1500 | ⭐ 主循环执行逻辑                    |
| `session/processor.ts`     | 430   | ⭐ 响应处理、工具调用                |
| `session/llm.ts`           | 320   | LLM 调用封装                         |
| `tool/tool.ts`             | 90    | 工具接口定义                         |

---

## 2. 背景与动机

### 2.1 问题本质

**要解决的问题：** 不同的 AI 任务需要不同的"性格"和"能力"。

**WHY 需要解决：**

- 如果没有 Agent 概念，所有任务都由同一个"通用 AI"处理
- 通用 AI 无法针对特定场景优化（如代码审查、探索、规划）
- 权限控制难以精细化管理

### 2.2 方案选择

**WHY 选择 Agent 配置化方案：**

| 方案           | 优点           | 缺点             | WHY 不选       |
| -------------- | -------------- | ---------------- | -------------- |
| 硬编码多种模式 | 简单直接       | 不可扩展         | 用户无法自定义 |
| Agent 配置文件 | 可扩展、可覆盖 | 需要设计配置格式 | — ✅ 已选      |
| 完全动态生成   | 最灵活         | 复杂、不可预测   | 稳定性差       |

**权衡决策：**

- 内置常用 Agent（build、plan、explore）提供开箱即用体验
- 用户可通过配置文件覆盖或添加自定义 Agent
- 使用 Effect 服务模式确保类型安全和依赖注入

### 2.3 应用场景

| Agent     | 适用场景       | WHY 适用                      |
| --------- | -------------- | ----------------------------- |
| `build`   | 代码编写、调试 | 权限宽松，可执行任何操作      |
| `plan`    | 规划阶段       | 只读 + 规划文件，防止意外修改 |
| `explore` | 代码探索       | 只读工具，快速定位            |
| `general` | 子任务         | 通用探索，无 Todo 干扰        |
| `title`   | 会话标题生成   | 后台任务，用户不可见          |

---

## 3. 核心概念网络图

### 3.1 概念清单

**概念 1：Agent.Info（配置 Schema）**

- **是什么：** 一个 Zod Schema，定义 Agent 的所有配置项
- **WHY 需要：** 类型安全 + 运行时验证 + 自动生成文档
- **WHY 用 Zod：** 与 AI SDK 工具定义风格一致，支持 `.meta()` 扩展

```typescript
export const Info = z.object({
  name: z.string(),                    // Agent 标识
  description: z.string().optional(),  // 描述（显示给用户）
  mode: z.enum(["subagent", "primary", "all"]),  // 使用模式
  permission: Permission.Ruleset,      // ⭐ 权限规则
  model: z.object({...}).optional(),   // 指定模型
  prompt: z.string().optional(),       // 自定义系统提示词
  temperature: z.number().optional(),  // 温度参数
  steps: z.number().optional(),        // 最大步数限制
})
```

**概念 2：Agent.Service（Effect 服务）**

- **是什么：** Effect 框架的服务定义，提供 Agent 查询能力
- **WHY 需要：** 依赖注入、测试可替换、状态管理
- **WHY 用 Effect：** 类型安全的异步编程，支持 Scope 和资源清理

**概念 3：Permission.Ruleset（权限规则）**

- **是什么：** 权限规则的集合，定义工具/操作的允许/拒绝/询问
- **WHY 需要：** 安全性——防止 AI 执行危险操作
- **WHY 不用简单的布尔值：** 需要"询问用户"这个中间状态

**概念 4：Agent Loop（主循环）**

- **是什么：** 在 `session/prompt.ts` 中实现的 AI 对话循环
- **WHY 需要：** 多轮对话、工具调用、状态管理
- **WHY 不用递归：** while(true) 更清晰，便于中断和恢复

### 3.2 概念关系矩阵

| 关系类型 | 概念 A        | 概念 B             | WHY 这样关联                 |
| -------- | ------------- | ------------------ | ---------------------------- |
| 依赖     | Agent.Info    | Permission.Ruleset | Agent 需要权限来限制行为     |
| 依赖     | Agent.Service | Config             | Agent 配置可被用户覆盖       |
| 组合     | Agent Loop    | Agent.Info         | Loop 根据 Agent 配置决定行为 |
| 对比     | build Agent   | plan Agent         | build 可写，plan 只读        |

---

## 4. 关键代码深度解析

### 4.1 片段清单

| 编号 | 片段名称        | 所在文件:行号       | 优先级 | 识别理由                      |
| ---- | --------------- | ------------------- | ------ | ----------------------------- |
| #1   | 内置 Agent 定义 | agent.ts:105-233    | ★★★    | 核心配置，理解系统默认行为    |
| #2   | Agent 服务层    | agent.ts:72-395     | ★★★    | Effect 服务模式，理解依赖注入 |
| #3   | Agent 主循环    | prompt.ts:278-756   | ★★★    | AI 执行核心，理解多轮对话     |
| #4   | 响应处理器      | processor.ts:46-426 | ★★☆    | 工具调用处理，理解流式响应    |
| #5   | 权限合并        | agent.ts:110-117    | ★★☆    | 权限优先级，理解安全模型      |

---

### 4.2 片段 #1：内置 Agent 定义

> 📍 **位置：** `agent/agent.ts:105-233`
> 🎯 **优先级：** ★★★
> 💡 **一句话核心：** 定义了 6 个内置 Agent，每个有不同的权限和用途

#### 4.2.1 代码整体作用

这段代码定义了 OpenCode 的 6 个内置 Agent，每个都有特定的权限配置和行为模式。

**系统层次定位：** 配置层 → 被服务层引用

**角色与依赖：**

- 上游：Config（用户配置可覆盖）
- 下游：Session Prompt（执行时读取 Agent 配置）

#### 4.2.2 核心逻辑分析

**执行流程：**

```
默认权限 (defaults)
       │
       ├──► build Agent ──► 合并 question/plan_enter 权限
       │
       ├──► plan Agent ──► 合并 plan_exit + 编辑限制
       │
       ├──► general Agent ──► 禁用 todo 工具
       │
       ├──► explore Agent ──► 只读工具集
       │
       ├──► compaction Agent ──► 禁用所有工具（后台）
       │
       └──► title Agent ──► 禁用所有工具（后台）
```

**关键设计点：**

| Agent      | 权限特点         | WHY 这样设计                       |
| ---------- | ---------------- | ---------------------------------- |
| build      | 最宽松           | 用户主要使用的 Agent，需要完整能力 |
| plan       | 只读 + plan 文件 | 防止规划阶段意外修改代码           |
| explore    | 只读工具         | 代码探索不需要写权限               |
| general    | 禁用 todo        | 子任务不需要管理主任务的 todo      |
| compaction | 全禁用           | 后台压缩任务，不需要工具           |
| title      | 全禁用           | 后台标题生成，不需要工具           |

#### 4.2.3 逐行代码解释

```typescript
// 默认权限模板：所有工具允许，但敏感操作需要询问
const defaults = Permission.fromConfig({
  "*": "allow",                    // 默认允许所有
  doom_loop: "ask",                // 死循环检测 → 询问用户
  external_directory: {
    "*": "ask",                    // 外部目录 → 询问
    ...Object.fromEntries(whitelistedDirs.map((dir) => [dir, "allow"])),
  },
  question: "deny",                // 禁止问题工具
  plan_enter: "deny",              // 禁止进入计划模式
  plan_exit: "deny",               // 禁止退出计划模式
  read: {
    "*": "allow",
    "*.env": "ask",                // .env 文件 → 询问
    "*.env.*": "ask",              // .env.local 等 → 询问
    "*.env.example": "allow",      // 示例文件 → 允许
  },
})

// build Agent：主工作 Agent
build: {
  name: "build",
  description: "The default agent...",
  permission: Permission.merge(
    defaults,
    Permission.fromConfig({
      question: "allow",           // 允许问问题
      plan_enter: "allow",         // 允许进入计划模式
    }),
    user,                          // 用户自定义权限
  ),
  mode: "primary",                 // 主 Agent
  native: true,                    // 内置 Agent
}

// plan Agent：只读规划模式
plan: {
  name: "plan",
  permission: Permission.merge(
    defaults,
    Permission.fromConfig({
      plan_exit: "allow",          // 允许退出计划模式
      edit: {
        "*": "deny",               // 禁止编辑所有文件
        [path.join(".opencode", "plans", "*.md")]: "allow",  // 只有 plan 文件可编辑
      },
    }),
    user,
  ),
  mode: "primary",
  native: true,
}

// explore Agent：代码探索
explore: {
  name: "explore",
  permission: Permission.merge(
    defaults,
    Permission.fromConfig({
      "*": "deny",                 // 禁止所有
      grep: "allow",               // 允许搜索
      glob: "allow",               // 允许文件匹配
      read: "allow",               // 允许读取
      webfetch: "allow",           // 允许网页抓取
    }),
    user,
  ),
  prompt: PROMPT_EXPLORE,          // 自定义提示词
  mode: "subagent",                // 子 Agent
  native: true,
}
```

#### 4.2.4 关键设计点

| 设计维度         | 分析内容                                         |
| ---------------- | ------------------------------------------------ |
| **实现选择**     | 用对象字面量定义，而非类实例化，便于序列化和合并 |
| **性能优化**     | 权限合并使用 `Permission.merge()`，支持增量覆盖  |
| **安全与健壮性** | 敏感操作（.env、外部目录）默认询问，而非拒绝     |
| **可扩展性**     | 用户配置可覆盖任何内置 Agent 的属性              |

---

### 4.3 片段 #2：Agent 主循环

> 📍 **位置：** `session/prompt.ts:278-756`
> 🎯 **优先级：** ★★★
> 💡 **一句话核心：** OpenCode AI 对话的核心执行引擎

#### 4.3.1 代码整体作用

这是 OpenCode 的心脏——一个无限循环，不断调用 AI API、处理响应、执行工具、然后继续。

**系统层次定位：** 执行层 → 协调所有子系统

**角色与依赖：**

- 上游：CLI/Web UI（接收用户输入）
- 下游：LLM（调用 AI）、Tool（执行工具）、Permission（权限检查）

#### 4.3.2 核心逻辑分析

**执行流程：**

```
┌─────────────────────────────────────────────────────────────┐
│                      Agent Loop                              │
│                                                              │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐              │
│  │ 加载消息 │───►│ 构建请求 │───►│ 调用 LLM │              │
│  └──────────┘    └──────────┘    └────┬─────┘              │
│                                        │                     │
│                       ┌────────────────┼────────────────┐   │
│                       ▼                ▼                ▼   │
│                 ┌──────────┐    ┌──────────┐    ┌─────────┐ │
│                 │ 文本响应 │    │ 工具调用 │    │ 错误   │ │
│                 └────┬─────┘    └────┬─────┘    └────┬────┘ │
│                      │               │               │      │
│                      │               ▼               │      │
│                      │        ┌──────────┐          │      │
│                      │        │ 执行工具 │          │      │
│                      │        └────┬─────┘          │      │
│                      │             │                │      │
│                      ▼             ▼                ▼      │
│                 ┌──────────────────────────────────────┐   │
│                 │           继续循环？                  │   │
│                 │  finish !== "tool-calls" → 退出      │   │
│                 │  finish === "tool-calls" → 继续      │   │
│                 └──────────────────────────────────────┘   │
│                            │                                │
└────────────────────────────┼────────────────────────────────┘
                             │
                    ┌────────┴────────┐
                    ▼                 ▼
               继续循环             退出循环
```

**核心状态变量：**

| 变量名            | 初始值       | 变化时机         | 终态                 |
| ----------------- | ------------ | ---------------- | -------------------- |
| `step`            | 0            | 每次循环 +1      | 达到 maxSteps 时停止 |
| `lastUser`        | 最新用户消息 | 每次循环重新查找 | 循环结束时           |
| `lastAssistant`   | 最新助手消息 | 每次循环重新查找 | 循环结束时           |
| `needsCompaction` | false        | 上下文溢出时     | 触发压缩后重置       |

**多执行路径：**

- **路径 A（正常完成）：** AI 返回 finish="stop" → 退出循环
- **路径 B（工具调用）：** AI 返回 finish="tool-calls" → 执行工具 → 继续循环
- **路径 C（压缩）：** 上下文溢出 → 创建压缩任务 → 继续循环
- **路径 D（错误）：** API 错误 → 重试或退出

#### 4.3.3 关键代码段

```typescript
export const loop = fn(LoopInput, async (input) => {
  const { sessionID, resume_existing } = input

  // 启动或恢复 AbortController
  const abort = resume_existing ? resume(sessionID) : start(sessionID)
  if (!abort) {
    // 如果会话已在运行，等待它完成
    return new Promise<MessageV2.WithParts>((resolve, reject) => {
      const callbacks = state()[sessionID].callbacks
      callbacks.push({ resolve, reject })
    })
  }

  let step = 0

  while (true) {
    // 1. 设置状态为忙碌
    await SessionStatus.set(sessionID, { type: "busy" })

    // 2. 加载消息历史
    let msgs = await MessageV2.filterCompacted(MessageV2.stream(sessionID))

    // 3. 找到最新的用户消息和助手消息
    let lastUser: MessageV2.User | undefined
    let lastAssistant: MessageV2.Assistant | undefined

    // ... 查找逻辑 ...

    // 4. 检查是否应该退出循环
    if (
      lastAssistant?.finish &&
      !["tool-calls", "unknown"].includes(lastAssistant.finish) &&
      lastUser.id < lastAssistant.id
    ) {
      log.info("exiting loop", { sessionID })
      break  // AI 已完成，退出
    }

    step++

    // 5. 获取模型和 Agent 配置
    const model = await Provider.getModel(lastUser.model.providerID, lastUser.model.modelID)
    const agent = await Agent.get(lastUser.agent)

    // 6. 构建工具列表
    const tools = await resolveTools({
      agent,
      session,
      model,
      processor,
      messages: msgs,
    })

    // 7. 创建处理器并执行
    const processor = SessionProcessor.create({
      assistantMessage: /* 创建助手消息 */,
      sessionID,
      model,
      abort,
    })

    const result = await processor.process({
      user: lastUser,
      agent,
      abort,
      sessionID,
      system: [/* 系统提示词 */],
      messages: MessageV2.toModelMessages(msgs, model),
      tools,
      model,
    })

    // 8. 根据结果决定下一步
    if (result === "stop") break
    if (result === "compact") {
      await SessionCompaction.create({ sessionID, ... })
    }
    continue  // 继续下一轮循环
  }

  // 9. 清理并返回最终消息
  SessionCompaction.prune({ sessionID })
  return /* 最终助手消息 */
})
```

#### 4.3.4 关键设计点

| 设计维度         | 分析内容                                      |
| ---------------- | --------------------------------------------- |
| **实现选择**     | `while(true)` + `break`，而非递归，避免栈溢出 |
| **性能优化**     | 消息流式加载，内存中只保留需要的数据          |
| **安全与健壮性** | AbortController 支持取消，避免无限循环        |
| **可扩展性**     | Plugin hooks 允许注入自定义逻辑               |

---

### 4.4 片段 #3：响应处理器

> 📍 **位置：** `session/processor.ts:46-426`
> 🎯 **优先级：** ★★☆
> 💡 **一句话核心：** 处理 AI 的流式响应，解析工具调用，管理状态

#### 4.4.1 代码整体作用

这个模块负责消费 AI API 的流式响应，解析文本、推理（reasoning）和工具调用，并更新数据库状态。

**核心事件类型：**

| 事件                        | 含义                    | 处理方式              |
| --------------------------- | ----------------------- | --------------------- |
| `text-start/delta/end`      | 文本内容                | 流式更新 UI           |
| `reasoning-start/delta/end` | 推理过程（Claude 思考） | 流式更新 UI           |
| `tool-input-start`          | 工具调用开始            | 创建 pending 状态     |
| `tool-call`                 | 工具调用完整参数        | 更新为 running 状态   |
| `tool-result`               | 工具执行结果            | 更新为 completed 状态 |
| `tool-error`                | 工具执行错误            | 更新为 error 状态     |
| `finish-step`               | 一步完成                | 记录 token 使用量     |

#### 4.4.2 死循环检测

```typescript
const DOOM_LOOP_THRESHOLD = 3

// 检测连续 3 次相同的工具调用
if (
  lastThree.length === DOOM_LOOP_THRESHOLD &&
  lastThree.every(
    (p) =>
      p.type === "tool" &&
      p.tool === value.toolName &&
      JSON.stringify(p.state.input) === JSON.stringify(value.input),
  )
) {
  // 触发 doom_loop 权限询问
  await Permission.ask({
    permission: "doom_loop",
    patterns: [value.toolName],
    ...
  })
}
```

**WHY 需要这个检测：**

- AI 可能陷入循环（如反复读取同一个文件）
- 检测后询问用户，避免无限消耗 token

---

## 5. 设计模式分析

### 5.1 模式：Service Layer Pattern（Effect）

**应用位置：** `Agent.Service`

**WHY 使用：**

- 依赖注入：Agent 服务需要 Config、Auth 等依赖
- 测试可替换：可以 mock Agent 服务进行测试
- 状态隔离：每个实例有独立的状态

**WHY 不用会怎样：**

- 硬编码依赖，难以测试
- 全局状态，难以隔离

### 5.2 模式：Strategy Pattern（权限策略）

**应用位置：** `Permission.Ruleset`

**WHY 使用：**

- 不同 Agent 有不同的权限策略
- 策略可以合并、覆盖
- 运行时决定允许/拒绝/询问

**潜在问题：** ⚠️ 权限合并顺序敏感，需谨慎处理优先级

### 5.3 模式：Observer Pattern（事件总线）

**应用位置：** `Bus` + `Session.Event`

**WHY 使用：**

- UI 需要实时更新消息状态
- 解耦消息存储和 UI 更新
- 支持多个订阅者

---

## 6. 完整使用示例

### 6.1 获取 Agent 配置

```typescript
import { Agent } from "@/agent/agent"

// 获取内置 Agent
const buildAgent = await Agent.get("build")
console.log(buildAgent.permission) // 权限规则

// 列出所有可见 Agent
const agents = await Agent.list()
// [{ name: "build", ... }, { name: "plan", ... }, ...]

// 获取默认 Agent
const defaultName = await Agent.defaultAgent() // "build"
```

### 6.2 动态生成 Agent

```typescript
// 用户描述想要的 Agent
const result = await Agent.generate({
  description: "一个专门用于代码审查的 Agent，检查代码质量和安全问题",
})

console.log(result)
// {
//   identifier: "code-reviewer",
//   whenToUse: "Use this agent when you need to review code for quality...",
//   systemPrompt: "You are a code reviewer specializing in..."
// }
```

---

## 7. 应用迁移场景

### 场景 1：OpenCode → 独立 AI CLI 工具

**不变的原理：**

- Agent 配置模式（权限、模型、提示词）
- 主循环结构（while true + break）
- 权限合并策略

**需要修改的部分：**

- 移除 Effect 依赖，改用简单函数
- 移除 Session 概念，简化为单次对话
- 权限系统改为简单的布尔值

### 场景 2：OpenCode Agent → Web Chatbot

**不变的原理：**

- Agent 定义模式
- 工具系统接口

**需要修改的部分：**

- 将 `Tool.Context` 的文件操作改为 API 调用
- 移除文件系统相关工具
- 添加 Web 特有工具（如 `web_search`）

---

## 8. 质量验证清单

### 理解深度

- [x] 每个核心概念都回答了 3 个 WHY
- [x] 自我解释测试：不看代码能解释 Agent 模块工作原理
- [x] 概念连接：理解 Agent、Session、Tool 的协作关系

### 技术准确性

- [x] Effect 服务模式：理解 Layer、Service、State
- [x] 权限系统：理解 allow/deny/ask 三种状态
- [x] 主循环：理解 while(true) + break 模式

### 最终"四能"测试

1. ✅ 能否理解 Agent 模块的设计思路？—— 可以，配置化 Agent + 权限策略
2. ✅ 能否独立实现类似功能？—— 可以，核心是循环 + 工具调用
3. ✅ 能否应用到不同场景？—— 可以，模式可迁移到其他 AI 应用
4. ✅ 能否向他人清晰解释？—— 可以，本文档即解释

---

## 9. 关键发现摘要

| 维度     | 发现                                                     |
| -------- | -------------------------------------------------------- |
| **架构** | Agent 模块是配置中心，不直接执行，由 Session Prompt 驱动 |
| **权限** | 使用三层权限合并：默认 → Agent 特定 → 用户配置           |
| **循环** | `while(true)` + `break` 模式，支持取消和恢复             |
| **工具** | 工具通过 `Tool.Context` 获取执行上下文                   |
| **扩展** | 用户可通过配置文件覆盖任何 Agent 属性                    |
