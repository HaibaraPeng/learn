# Agent 系统模块深度理解分析

## 理解验证状态

| 核心概念            | 自我解释 | 理解"为什么" | 应用迁移 | 状态   |
| ------------------- | -------- | ------------ | -------- | ------ |
| Agent.Info 结构     | ✅       | ✅           | ✅       | 已掌握 |
| Agent.Service 设计  | ✅       | ✅           | ✅       | 已掌握 |
| Permission 合并机制 | ✅       | ✅           | ✅       | 已掌握 |
| Agent 生成功能      | ✅       | ✅           | ⚠️       | 已理解 |
| 内置 Agent 类型     | ✅       | ✅           | ✅       | 已掌握 |

---

## 1. 快速概览

### 模块基本信息

| 属性         | 值                                                |
| ------------ | ------------------------------------------------- |
| **模块名称** | Agent                                             |
| **文件位置** | `packages/opencode/src/agent/`                    |
| **主文件**   | `agent.ts` (414 行)                               |
| **代码规模** | 约 500 行（含提示词模板）                         |
| **依赖模块** | Permission, Provider, Config, Auth, Skill, Plugin |
| **被依赖**   | Session, Tool, Server, CLI                        |

### 一句话描述

Agent 模块定义了 AI 智能体的**行为规范**和**权限边界**，是 OpenCode 实现"不同任务使用不同能力"的核心抽象。

### 文件清单

| 文件                    | 行数 | 职责                         |
| ----------------------- | ---- | ---------------------------- |
| `agent.ts`              | 414  | Agent 定义、服务层、权限配置 |
| `generate.txt`          | 75   | AI 生成 Agent 的系统提示词   |
| `prompt/explore.txt`    | 18   | explore Agent 的系统提示词   |
| `prompt/compaction.txt` | 14   | 对话压缩 Agent 的系统提示词  |
| `prompt/title.txt`      | 44   | 标题生成 Agent 的系统提示词  |
| `prompt/summary.txt`    | 11   | 摘要生成 Agent 的系统提示词  |

---

## 2. 背景与动机（3 个 WHY）

### 问题本质

**要解决的问题**: AI 编程助手需要在不同场景下执行不同类型的操作，但如果没有权限边界，AI 可能执行危险操作（如删除重要文件、执行恶意命令）。

**WHY 需要解决**:

- **安全风险**: 无限制的 AI 可能执行破坏性操作
- **职责混乱**: 同一个 AI 无法同时满足"探索代码库"和"修改代码"两种截然不同的需求
- **用户信任**: 用户需要明确知道 AI 能做什么、不能做什么

### 方案选择

**WHY 选择这个方案**:

- ✅ **配置驱动**: 通过配置文件定义 Agent，无需修改代码
- ✅ **权限分层**: 默认权限 → Agent 权限 → 用户权限，逐层覆盖
- ✅ **类型安全**: 使用 Zod schema 确保配置正确性
- ✅ **可扩展**: 支持用户自定义 Agent

**替代方案对比**:
| 方案 | 描述 | WHY 不选 |
|------|------|----------|
| 硬编码权限 | 每个功能直接判断权限 | 难以扩展，修改需要改代码 |
| 纯配置文件 | 只用 JSON/YAML 定义 | 缺乏类型安全，无法动态生成 |
| 角色继承 | 用 OOP 继承实现 Agent | TypeScript 不适合复杂继承 |

### 应用场景

**适用场景**:

- `build` Agent: 日常开发，需要完整工具链
- `plan` Agent: 代码审查、架构设计，只读模式
- `explore` Agent: 代码库探索，快速搜索
- 自定义 Agent: 特定任务（如只操作测试文件）

**不适用场景**:

- 需要运行时动态切换 Agent（当前需要重新创建会话）

---

## 3. 核心概念说明（每个概念 3 WHY）

### 概念 1: Agent.Info

**是什么**: Agent 的完整定义结构，包含名称、权限、提示词、模型配置等。

```typescript
// agent.ts:27-52
export const Info = z.object({
  name: z.string(),                    // Agent 标识符
  description: z.string().optional(),  // 用户可见描述
  mode: z.enum(["subagent", "primary", "all"]),  // 运行模式
  native: z.boolean().optional(),      // 是否内置
  hidden: z.boolean().optional(),      // 是否隐藏
  permission: Permission.Ruleset,      // 权限规则集
  model: z.object({...}).optional(),   // 指定模型
  prompt: z.string().optional(),       // 系统提示词
  // ...更多字段
})
```

**WHY 需要**:

- 统一的 Agent 定义格式，便于管理和序列化
- Zod schema 提供运行时验证和类型推断

**WHY 这样实现**:

- 使用 Zod 的 `.meta()` 添加元数据，支持 OpenAPI 生成
- 可选字段使用 `.optional()`，保持向后兼容

**WHY 不用其他方式**:

- TypeScript interface 无法运行时验证
- 纯 JSON 缺乏类型推断

---

### 概念 2: Agent.Mode

**是什么**: Agent 的运行模式，决定何时可以被调用。

| 模式       | 含义                         | 示例             |
| ---------- | ---------------------------- | ---------------- |
| `primary`  | 主 Agent，用户直接选择       | build, plan      |
| `subagent` | 子 Agent，通过 Task 工具调用 | general, explore |
| `all`      | 两种方式都可                 | 用户自定义       |

**WHY 需要**:

- 区分"用户主动选择"和"AI 自动委托"的场景
- 防止 subagent 出现在用户选择列表中

**WHY 这样实现**:

- 简单的字符串枚举，易于理解和扩展
- UI 可以根据 mode 过滤显示

---

### 概念 3: Permission.Ruleset

**是什么**: 权限规则的数组，每条规则定义"哪个权限"、"什么模式"、"什么动作"。

```typescript
// permission/index.ts:27-41
export const Rule = z.object({
  permission: z.string(), // 权限类型：bash, edit, read...
  pattern: z.string(), // 匹配模式：*, *.env, /path/*
  action: z.enum(["allow", "deny", "ask"]),
})
export const Ruleset = Rule.array()
```

**WHY 需要**:

- 细粒度控制：可以允许读取代码但禁止读取 .env
- 灵活组合：多条规则可以叠加

**WHY 这样实现**:

- 数组形式便于合并和覆盖
- 使用通配符模式匹配，灵活性强

**WHY 不用其他方式**:

- 对象形式 `{ bash: "allow" }` 不支持细粒度模式
- 嵌套结构难以合并

---

### 概念 4: 权限合并机制

**是什么**: 将多层权限配置合并的机制，后配置覆盖前配置。

```typescript
// agent.ts:110-117
permission: Permission.merge(
  defaults,                    // 1. 默认权限
  Permission.fromConfig({...}), // 2. Agent 特定权限
  user,                        // 3. 用户配置权限
)
```

**WHY 需要**:

- 用户可以覆盖默认配置
- 不同 Agent 可以有不同的基础权限

**WHY 这样实现**:

- `merge()` 就是 `flat()`，后面的规则追加到数组
- `evaluate()` 使用 `findLast()`，最后匹配的规则生效

**WHY 不用其他方式**:

- 深度合并对象逻辑复杂，容易出错
- 数组追加简单明了

---

## 4. 算法与理论分析

### 算法 1: 权限评估（evaluate）

**位置**: `permission/evaluate.ts:9-15`

```typescript
export function evaluate(permission: string, pattern: string, ...rulesets: Rule[][]): Rule {
  const rules = rulesets.flat()
  const match = rules.findLast(
    (rule) => Wildcard.match(permission, rule.permission) && Wildcard.match(pattern, rule.pattern),
  )
  return match ?? { action: "ask", permission, pattern: "*" }
}
```

- **时间复杂度**: O(n \* m) 其中 n 是规则数量，m 是通配符匹配成本
- **空间复杂度**: O(1) 原地操作
- **WHY 选择**: `findLast()` 确保后定义的规则优先
- **退化场景**: 规则数量极大时性能下降，实际场景规则数 < 100，可接受
- **默认行为**: 未匹配任何规则时返回 "ask"，安全优先

---

### 算法 2: Agent 生成（generate）

**位置**: `agent.ts:328-390`

- **时间复杂度**: 取决于 AI 模型响应时间
- **WHY 选择**: 让 AI 自动生成 Agent 配置，降低用户配置门槛
- **关键设计**:
  - 传入已存在的 Agent 名称，避免冲突
  - 使用 `generateObject` 确保输出是有效 JSON
  - 温度设为 0.3，减少随机性

---

## 5. 设计模式分析

### 模式 1: Effect 服务模式

**应用位置**: `agent.ts:70-72, 72-395`

```typescript
export class Service extends ServiceMap.Service<Service, Interface>()("@opencode/Agent") {}

export const layer = Layer.effect(
  Service,
  Effect.gen(function* () {
    // ...服务实现
  }),
)
```

**WHY 使用**:

- 函数式依赖注入，测试友好
- 延迟初始化，按需创建
- 自动生命周期管理

**WHY 不用会怎样**: 全局单例难以测试，生命周期管理混乱

**参考**: [Effect Service Pattern](https://effect.website/docs/services/introduction/)

---

### 模式 2: 工厂模式

**应用位置**: `agent.ts:399-413`

```typescript
export async function get(agent: string) {
  return runPromise((svc) => svc.get(agent))
}
export async function list() {
  return runPromise((svc) => svc.list())
}
```

**WHY 使用**: 提供简化的 API，隐藏 Effect 复杂性

---

### 模式 3: 策略模式

**应用位置**: 内置 Agent 定义

每个 Agent 是一个行为策略：

- `build`: 允许 question 和 plan_enter
- `plan`: 禁止编辑，只允许特定路径
- `explore`: 只允许搜索和读取

**WHY 使用**: 易于添加新的 Agent 类型，无需修改核心逻辑

---

## 6. 关键代码深度解析

### 核心片段清单

| 编号 | 片段名称      | 所在文件:行号    | 优先级 | 识别理由                    |
| ---- | ------------- | ---------------- | ------ | --------------------------- |
| #1   | 默认权限模板  | agent.ts:84-101  | ★★★    | 定义了所有 Agent 的基础权限 |
| #2   | build Agent   | agent.ts:105-120 | ★★★    | 最常用的默认 Agent          |
| #3   | plan Agent    | agent.ts:121-144 | ★★☆    | 只读模式的关键实现          |
| #4   | explore Agent | agent.ts:160-186 | ★★☆    | 子 Agent 的典型示例         |
| #5   | 用户配置合并  | agent.ts:235-262 | ★★☆    | 自定义 Agent 的入口         |
| #6   | Agent 生成    | agent.ts:328-390 | ★☆☆    | AI 辅助配置的高级功能       |

---

### 片段 #1: 默认权限模板

> 📍 **位置**: `agent.ts:84-101`
> 🎯 **优先级**: ★★★
> 💡 **一句话核心**: 定义所有 Agent 的基础安全边界

#### 1.1 代码整体作用

这段代码定义了 Agent 的默认权限规则，所有内置 Agent 都以此为基础进行扩展。

**它解决了什么问题?** 提供一个安全的默认配置，避免每个 Agent 重复定义基础权限。

**系统层次定位**: 权限层的基础设施

**角色与依赖**: 被所有 Agent 的权限定义依赖

#### 1.2 核心逻辑分析

```typescript
const defaults = Permission.fromConfig({
  "*": "allow", // 默认允许所有工具
  doom_loop: "ask", // 死循环检测需询问
  external_directory: {
    "*": "ask", // 外部目录默认询问
    ...Object.fromEntries(whitelistedDirs.map((dir) => [dir, "allow"])),
  },
  question: "deny", // 默认禁止提问工具
  plan_enter: "deny", // 默认禁止进入规划模式
  plan_exit: "deny", // 默认禁止退出规划模式
  read: {
    "*": "allow", // 默认允许读取所有文件
    "*.env": "ask", // 环境变量文件询问
    "*.env.*": "ask",
    "*.env.example": "allow", // 示例文件允许
  },
})
```

**关键状态变量**:
| 变量名 | 初始值 | 变化时机 | 终态 |
|--------|--------|----------|------|
| defaults | 权限对象 | 无 | 不变 |
| whitelistedDirs | 技能目录列表 | Skill 变更 | 动态 |

#### 1.3 逐行代码解释

```typescript
// 步骤 1: 定义全局默认规则
"*": "allow",
// WHY: 默认允许所有工具，减少配置负担
// 安全关键操作单独限制

// 步骤 2: 死循环检测特殊处理
doom_loop: "ask",
// WHY: AI 可能陷入重复操作，需要人工干预

// 步骤 3: 外部目录访问控制
external_directory: {
  "*": "ask",  // 默认询问
  // 技能目录白名单
  ...Object.fromEntries(whitelistedDirs.map((dir) => [dir, "allow"])),
}
// WHY: 项目目录外的文件可能包含敏感信息

// 步骤 4: 环境变量文件特殊处理
read: {
  "*": "allow",
  "*.env": "ask",
  "*.env.*": "ask",
  "*.env.example": "allow",
}
// WHY: .env 文件通常包含密钥，需要确认
// .env.example 是示例文件，无敏感信息
```

#### 1.4 关键设计点

| 设计维度         | 分析内容                           |
| ---------------- | ---------------------------------- |
| **实现选择**     | 白名单优于黑名单，默认询问敏感操作 |
| **性能优化**     | 白名单目录在初始化时计算一次       |
| **安全与健壮性** | .env 文件默认询问，防止密钥泄露    |
| **可扩展性**     | 用户可以通过配置覆盖默认规则       |

#### 1.5 完整示例

**示例 1 — 基础场景**

- **输入**: AI 尝试读取 `src/index.ts`
- **匹配规则**: `read: { "*": "allow" }`
- **结果**: 允许，无需询问

**示例 2 — 敏感文件**

- **输入**: AI 尝试读取 `.env`
- **匹配规则**: `read: { "*.env": "ask" }`
- **结果**: 需要用户确认

**示例 3 — 外部目录**

- **输入**: AI 尝试读取 `/etc/passwd`
- **匹配规则**: `external_directory: { "*": "ask" }`
- **结果**: 需要用户确认

---

### 片段 #2: build Agent

> 📍 **位置**: `agent.ts:105-120`
> 🎯 **优先级**: ★★★
> 💡 **一句话核心**: 默认的全功能 Agent，拥有完整工具链

#### 2.1 代码整体作用

定义了 build Agent，这是用户最常用的开发模式。

**它解决了什么问题?** 提供一个开箱即用的全功能 Agent。

#### 2.2 核心逻辑分析

```typescript
build: {
  name: "build",
  description: "The default agent. Executes tools based on configured permissions.",
  options: {},
  permission: Permission.merge(
    defaults,                    // 基础权限
    Permission.fromConfig({
      question: "allow",         // 允许使用提问工具
      plan_enter: "allow",       // 允许进入规划模式
    }),
    user,                        // 用户配置
  ),
  mode: "primary",
  native: true,
}
```

**权限合并顺序**:

```
defaults (禁止 question/plan_enter)
    ↓ 覆盖
Agent 特定权限 (允许 question/plan_enter)
    ↓ 覆盖
用户配置
```

#### 2.3 关键设计点

| 设计维度     | 分析内容                                   |
| ------------ | ------------------------------------------ |
| **实现选择** | 在默认权限基础上扩展，而非重新定义         |
| **用户体验** | 允许 question 工具，可以主动向用户询问信息 |
| **灵活性**   | 用户配置优先级最高，可以禁用任何工具       |

---

### 片段 #3: plan Agent

> 📍 **位置**: `agent.ts:121-144`
> 🎯 **优先级**: ★★☆
> 💡 **一句话核心**: 只读规划模式，禁止修改代码

#### 3.1 代码整体作用

定义了 plan Agent，用于代码审查和架构设计，不能修改文件。

#### 3.2 核心逻辑分析

```typescript
plan: {
  name: "plan",
  description: "Plan mode. Disallows all edit tools.",
  permission: Permission.merge(
    defaults,
    Permission.fromConfig({
      question: "allow",
      plan_exit: "allow",        // 允许退出规划模式
      external_directory: {
        [path.join(Global.Path.data, "plans", "*")]: "allow",
      },
      edit: {
        "*": "deny",              // 禁止所有编辑
        // 例外：允许编辑计划文件
        [path.join(".opencode", "plans", "*.md")]: "allow",
        [path.relative(Instance.worktree, path.join(Global.Path.data, "plans", "*.md"))]: "allow",
      },
    }),
    user,
  ),
  mode: "primary",
  native: true,
}
```

**关键设计**: 使用 `edit: { "*": "deny" }` 禁止所有编辑，但通过白名单允许计划文件。

---

### 片段 #4: explore Agent

> 📍 **位置**: `agent.ts:160-186`
> 🎯 **优先级**: ★★☆
> 💡 **一句话核心**: 代码库探索专用，只允许搜索和读取

#### 4.1 代码整体作用

定义了 explore Agent，作为子 Agent 被调用，用于快速探索代码库。

#### 4.2 核心逻辑分析

```typescript
explore: {
  name: "explore",
  permission: Permission.merge(
    defaults,
    Permission.fromConfig({
      "*": "deny",         // 禁止所有工具
      grep: "allow",       // 只允许这些
      glob: "allow",
      list: "allow",
      bash: "allow",
      webfetch: "allow",
      websearch: "allow",
      codesearch: "allow",
      read: "allow",
    }),
    user,
  ),
  mode: "subagent",       // 作为子 Agent
  native: true,
  prompt: PROMPT_EXPLORE, // 自定义系统提示词
}
```

**设计亮点**:

- `mode: "subagent"` 不会出现在用户选择列表
- 自定义 `prompt` 定义专家角色
- 白名单模式只允许必要的搜索工具

---

## 7. 内置 Agent 对比

| Agent      | mode             | 权限特点                  | 用途       | 提示词            |
| ---------- | ---------------- | ------------------------- | ---------- | ----------------- |
| build      | primary          | 允许 question、plan_enter | 日常开发   | 无                |
| plan       | primary          | 禁止编辑，允许 plan_exit  | 只读规划   | 无                |
| general    | subagent         | 禁止 todo 工具            | 通用子任务 | 无                |
| explore    | subagent         | 只允许搜索和读取          | 代码库探索 | PROMPT_EXPLORE    |
| compaction | primary (hidden) | 禁止所有工具              | 对话压缩   | PROMPT_COMPACTION |
| title      | primary (hidden) | 禁止所有工具              | 标题生成   | PROMPT_TITLE      |
| summary    | primary (hidden) | 禁止所有工具              | 摘要生成   | PROMPT_SUMMARY    |

---

## 8. 应用迁移场景

### 场景 1: 添加自定义 Agent

**不变的原理**: 权限合并机制

**需要修改的部分**:

```typescript
// 在配置文件中添加
{
  "agent": {
    "code-reviewer": {
      "description": "Review code changes with security focus",
      "prompt": "You are a security-focused code reviewer...",
      "permission": {
        "edit": { "*": "deny" },
        "bash": { "*": "deny" }
      },
      "mode": "primary"
    }
  }
}
```

**学到的通用模式**: 配置优于代码，便于用户定制

### 场景 2: 修改默认权限

**不变的原理**: 权限合并顺序

**需要修改的部分**:

```typescript
// 在 defaults 对象中添加新规则
const defaults = Permission.fromConfig({
  // ...现有规则
  write: {
    "*": "ask", // 所有写入操作需要确认
  },
})
```

---

## 9. 依赖关系与使用示例

### 依赖模块

| 模块            | 用途            | 代码位置            |
| --------------- | --------------- | ------------------- |
| `Permission`    | 权限规则处理    | agent.ts:16, 84-101 |
| `Provider`      | AI 模型提供者   | agent.ts:3, 333-335 |
| `Config`        | 读取用户配置    | agent.ts:1, 75      |
| `Auth`          | 认证服务        | agent.ts:8, 76      |
| `Skill`         | 技能目录        | agent.ts:21, 81     |
| `Plugin`        | 插件系统        | agent.ts:20, 339    |
| `InstanceState` | Effect 状态管理 | agent.ts:23, 78     |

### 被依赖模块

| 模块      | 用途                |
| --------- | ------------------- |
| `Session` | 获取当前 Agent 配置 |
| `Tool`    | 检查工具权限        |
| `Server`  | HTTP API            |
| `CLI`     | 命令行              |

### 完整使用示例

```typescript
import { Agent } from "./agent"

// 获取 Agent 信息
const buildAgent = await Agent.get("build")
console.log(buildAgent.permission) // 权限规则列表

// 列出所有 Agent
const agents = await Agent.list()
for (const agent of agents) {
  console.log(`${agent.name}: ${agent.description}`)
}

// 获取默认 Agent
const defaultName = await Agent.defaultAgent()
// → "build" 或用户配置的默认值

// AI 生成新 Agent
const generated = await Agent.generate({
  description: "一个专注于 React 组件测试的 Agent",
})
console.log(generated.identifier) // "react-test-writer"
console.log(generated.systemPrompt) // 完整的系统提示词
```

---

## 10. 质量验证清单

### 理解深度

- [x] 每个核心概念都回答了 3 个 WHY
- [x] 自我解释测试：不看代码能解释每个核心概念
- [x] 概念连接：标注了依赖/对比关系

### 技术准确性

- [x] 权限评估算法：复杂度 + WHY 选择 + 默认行为
- [x] 设计模式：模式名 + WHY 使用 + 参考链接
- [x] 代码解析：引用实际代码行

### 实用性

- [x] 应用迁移：2 个场景，不变原理 + 修改部分
- [x] 使用示例：代码完整

### 最终"四能"测试

1. ✅ **能理解代码的设计思路**: Effect 服务模式 + 权限分层合并
2. ✅ **能独立实现类似功能**: 理解工厂模式和策略模式的应用
3. ✅ **能应用到不同场景**: 知道如何添加自定义 Agent
4. ✅ **能向他人清晰解释**: 核心概念都有清晰的 WHY 解释

---

## 总结

Agent 模块是 OpenCode 的**安全基石**，其核心设计亮点：

1. **权限分层合并**: 默认 → Agent 特定 → 用户配置，灵活且安全
2. **多种内置 Agent**: build/plan/explore 满足不同场景需求
3. **Effect 服务模式**: 函数式依赖注入，测试友好
4. **配置驱动**: 用户无需修改代码即可自定义 Agent

---

## 11. 学习路径建议

### 推荐学习顺序

```
Step 1: Permission.evaluate()     (理解权限评估算法)
    ↓
Step 2: 权限合并机制               (理解 Permission.merge)
    ↓
Step 3: 默认权限模板               (理解 defaults 对象)
    ↓
Step 4: 内置 Agent 定义            (build → plan → explore)
    ↓
Step 5: 用户配置合并               (理解自定义 Agent 流程)
```

### 每个步骤的学习目标

| 步骤 | 文件:行号                     | 学习目标                                   | 预计时间 |
| ---- | ----------------------------- | ------------------------------------------ | -------- |
| 1    | `permission/evaluate.ts:9-15` | 理解 `findLast()` 为什么保证后定义规则优先 | 30分钟   |
| 2    | `agent.ts:110-117`            | 理解三层权限如何合并                       | 30分钟   |
| 3    | `agent.ts:84-101`             | 理解为什么 .env 默认询问                   | 30分钟   |
| 4    | `agent.ts:105-186`            | 对比三种 Agent 的权限差异                  | 1小时    |
| 5    | `agent.ts:235-262`            | 理解用户配置如何覆盖默认值                 | 30分钟   |

### 动手实践建议

**实践 1: 调试权限评估**

```typescript
// 在 evaluate.ts 中添加日志
console.log("Evaluating:", permission, pattern)
console.log("Matched rule:", match)
```

**实践 2: 创建自定义 Agent**

在 `opencode.config.json` 中添加：

```json
{
  "agent": {
    "test-only": {
      "description": "只操作测试文件",
      "mode": "primary",
      "permission": {
        "edit": { "*": "deny", "*.test.ts": "allow", "*.spec.ts": "allow" },
        "bash": { "*": "deny" }
      }
    }
  }
}
```

**实践 3: 理解 Effect 服务模式**

阅读 Effect 官方文档的 [Services](https://effect.website/docs/services/introduction/) 章节，理解：

- 为什么用 `Layer.effect` 创建服务
- `InstanceState.make` 如何实现状态隔离
- `runPromise` 如何桥接 Effect 和 Promise

### 相关源码阅读清单

| 优先级 | 文件                       | 重点内容               |
| ------ | -------------------------- | ---------------------- |
| ★★★    | `permission/evaluate.ts`   | 15行核心算法           |
| ★★★    | `permission/index.ts`      | 权限服务完整实现       |
| ★★☆    | `agent/agent.ts`           | Agent 定义和合并逻辑   |
| ★★☆    | `effect/instance-state.ts` | 状态隔离机制           |
| ★☆☆    | `agent/generate.txt`       | AI 生成 Agent 的提示词 |

### 验证学习成果

完成以下问题，验证是否真正理解 Agent 模块：

1. ❓ 为什么 `evaluate()` 使用 `findLast()` 而不是 `find()`?
2. ❓ build Agent 如何覆盖 defaults 中的 `question: "deny"`?
3. ❓ 如果用户配置了 `permission.bash = "deny"`，build Agent 还能执行 bash 吗?
4. ❓ explore Agent 的 `mode: "subagent"` 有什么影响?
5. ❓ 如何创建一个只允许读取 `.ts` 文件的 Agent?

<details>
<summary>参考答案</summary>

1. `findLast()` 确保后定义的规则优先级更高，符合"后配置覆盖前配置"的直觉。
2. 通过 `Permission.merge()` 将 `{ question: "allow" }` 追加到规则数组末尾，`evaluate()` 会匹配到最后一条。
3. 可以。用户配置在 `Permission.merge()` 的最后参数，追加到规则数组，会覆盖 Agent 的权限设置。
4. `subagent` 模式不会出现在 UI 的 Agent 选择列表，只能通过 Task 工具调用。
5.

```json
{
  "agent": {
    "ts-reader": {
      "permission": {
        "read": { "*": "deny", "*.ts": "allow" },
        "*": "deny"
      },
      "mode": "primary"
    }
  }
}
```

</details>
