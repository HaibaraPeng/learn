# Deep Agents 项目学习路径

> 本文档提供一个循序渐进的学习路线，帮助你从零开始掌握 Deep Agents 项目。
> 
> **适用人群**：Python 开发者、AI Agent 开发者、LangChain/LangGraph 用户
> 
> **预计学习时间**：2-4 周（根据个人背景和目标调整）

---

## 学习目标设定

根据你的学习目标，选择对应的学习路径：

| 学习目标 | 推荐路径 | 重点章节 | 时间投入 |
|---------|---------|---------|---------|
| **快速上手使用 SDK** | 快速路径 | 第 1-2 阶段 | 1-2 天 |
| **理解核心原理** | 标准路径 | 第 1-4 阶段 | 1-2 周 |
| **深度定制开发** | 深度路径 | 第 1-6 阶段 | 2-4 周 |
| **贡献代码/面试准备** | 专家路径 | 全部阶段 + 源码分析 | 3-5 周 |

---

## 学习路径概览

```
阶段 0：前置准备（环境配置）
    ↓
阶段 1：快速体验（SDK 基础使用）
    ↓
阶段 2：核心概念（理解 Agent 架构）
    ↓
阶段 3：进阶功能（Middleware、Backend、Profile）
    ↓
阶段 4：实战应用（Examples 深度学习）
    ↓
阶段 5：扩展开发（Partners、ACP、自定义）
    ↓
阶段 6：源码精通（深入核心实现）
```

---

## 阶段 0：前置准备

### 学习目标
- 配置开发环境
- 了解项目基本结构
- 准备必要的工具和依赖

### 学习内容

#### 0.1 环境配置

**WHY 重要**：正确的环境配置是后续学习的基础，避免版本冲突和依赖问题。

```bash
# 1. 安装 Python 3.12（推荐版本）
# Windows: 使用 scoop
scoop install python312

# macOS/Linux: 使用 pyenv 或系统包管理器

# 2. 安装 uv（包管理器）
# Windows
scoop install uv

# macOS/Linux
curl -LsSf https://astral.sh/uv/install.sh | sh

# 3. 克隆项目
git clone https://github.com/langchain-ai/deepagents.git
cd deepagents

# 4. 安装 SDK
pip install deepagents
# 或使用 uv
uv add deepagents
```

#### 0.2 项目结构认知

**WHY 重要**：了解 Monorepo 结构，知道代码在哪里。

阅读以下文档：
- `README.md` — 项目概览
- `AGENTS.md` — AI Agent 开发指南
- `Deep-Agents-项目组件分析.md` — 组件关系详解

**关键目录认知**：

```
deepagents/
├── libs/deepagents/          # SDK 核心（阶段 1-3 重点）
├── libs/cli/                 # CLI 工具（阶段 4）
├── libs/acp/                 # ACP 集成（阶段 5）
├── libs/partners/            # 第三方集成（阶段 5）
├── examples/                 # 示例项目（阶段 4）
└── .github/                  # CI/CD（阶段 6）
```

#### 0.3 配置 LLM Provider

**WHY 重要**：Agent 需要连接 LLM，配置 Provider 是第一步。

**推荐配置**：

```bash
# 方式 1: 环境变量
export ANTHROPIC_API_KEY="your-key"
export OPENAI_API_KEY="your-key"

# 方式 2: 配置文件 (~/.deepagents/config.toml)
[models]
default = "anthropic:claude-3-5-sonnet"

[models.providers.anthropic]
base_url = "https://api.anthropic.com"
```

### 实践任务

1. ✅ 成功运行 `python -c "import deepagents; print(deepagents.__version__)"`
2. ✅ 成功配置至少一个 LLM Provider（Anthropic/OpenAI）
3. ✅ 成功运行 `python -c "from deepagents import create_deep_agent; print('OK')"`
4. ✅ 阅读 README.md，了解项目定位

### 学习检查清单

- [ ] Python 3.12 环境已配置
- [ ] uv 已安装并可用
- [ ] 项目已克隆并理解目录结构
- [ ] 至少一个 LLM Provider 已配置
- [ ] 基础依赖已安装（langchain、langgraph）

---

## 阶段 1：快速体验（SDK 基础使用）

### 学习目标
- 体验 SDK 基础功能
- 理解 `create_deep_agent` 核心接口
- 完成第一个 Agent 程序

### 学习内容

#### 1.1 最简 Agent 示例

**WHY 学习**：从最简单的例子开始，建立信心和基本认知。

```python
# 1-1-hello_agent.py
from deepagents import create_deep_agent

# 创建最简 Agent（使用默认配置）
agent = create_deep_agent()

# 运行 Agent
result = agent.invoke({
    "messages": [{"role": "user", "content": "Hello, what can you do?"}]
})

# 输出结果
print(result["messages"][-1].content)
```

**关键概念**：
- `create_deep_agent()` — SDK 核心函数，返回编译后的 LangGraph StateGraph
- `agent.invoke()` — 执行 Agent，传入消息列表
- `result["messages"]` — Agent 执行结果，包含对话历史

#### 1.2 Agent 核心功能体验

**WHY 学习**：了解 Agent 的核心能力，理解"开箱即用"的含义。

```python
# 1-2-agent_features.py
from deepagents import create_deep_agent

agent = create_deep_agent()

# 测试 1: 任务规划（write_todos）
result = agent.invoke({
    "messages": [
        {"role": "user", "content": "帮我写一个 Python 爬虫，抓取豆瓣电影评分"}
    ]
})

# 测试 2: 文件操作（read_file/write_file）
result = agent.invoke({
    "messages": [
        {"role": "user", "content": "读取当前目录下的 README.md，总结内容"}
    ]
})

# 测试 3: 子 Agent 调用（task）
result = agent.invoke({
    "messages": [
        {"role": "user", "content": "帮我分析 LangGraph 的核心特性，需要深入研究"}
    ]
})
```

**体验要点**：
- Agent 自动规划任务（write_todos）
- Agent 自动读写文件（read_file/write_file）
- Agent 自动创建子 Agent（task）

#### 1.3 自定义模型和工具

**WHY 学习**：理解 Agent 可定制性，学会添加自己的工具。

```python
# 1-3-custom_agent.py
from deepagents import create_deep_agent
from langchain_anthropic import ChatAnthropic
from langchain_core.tools import tool

# 自定义工具
@tool
def get_weather(city: str) -> str:
    """获取指定城市的天气信息"""
    # 实际应用中可调用天气 API
    return f"{city} 当前天气晴朗，温度 25°C"

# 自定义 Agent
agent = create_deep_agent(
    model=ChatAnthropic(model="claude-3-5-sonnet"),
    tools=[get_weather],
    system_prompt="你是一个友好的助手，擅长回答天气相关问题。",
)

# 运行
result = agent.invoke({
    "messages": [{"role": "user", "content": "北京今天天气怎么样？"}]
})

print(result["messages"][-1].content)
```

**关键概念**：
- `model` 参数 — 指定 LLM 模型（支持字符串或对象）
- `tools` 参数 — 添加自定义工具（Python 函数 + @tool 装饰器）
- `system_prompt` 参数 — 自定义系统提示词

#### 1.4 配置 Backend 和权限

**WHY 学习**：理解 Backend 抽象，学会控制 Agent 的文件访问权限。

```python
# 1-4-backend_agent.py
from deepagents import create_deep_agent
from deepagents.backends import FilesystemBackend
from deepagents.middleware.permissions import FilesystemPermission

# 配置 Backend（指定工作目录，启用虚拟路径模式）
backend = FilesystemBackend(root_dir="./workspace", virtual_mode=True)

# 配置权限（只允许读取 /workspace 目录，禁止写入）
permissions = [
    # 允许读取 /workspace 目录下的所有文件
    FilesystemPermission(operations=["read"], paths=["/workspace/**"]),
    # 禁止写入 /workspace 目录下的所有文件
    FilesystemPermission(operations=["write"], paths=["/workspace/**"], mode="deny"),
]

# 创建 Agent
agent = create_deep_agent(
    backend=backend,
    permissions=permissions,
)

# 运行（只能读，不能写）
result = agent.invoke({
    "messages": [{"role": "user", "content": "读取 ./workspace/data.txt"}]
})
```

**关键概念**：
- `Backend` — Agent 的运行环境抽象（本地 filesystem 或远程 sandbox）
- `FilesystemPermission` — 文件系统权限控制规则
- `root_dir` — Backend 工作根目录
- `virtual_mode` — 虚拟路径模式，启用后路径会被限制在 root_dir 内

**FilesystemPermission 参数说明**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `operations` | `list["read" \| "write"]` | 操作类型（read 包含 ls/read_file/glob/grep，write 包含 write_file/edit_file） |
| `paths` | `list[str]` | Glob 路径模式（必须以 `/` 开头，如 `["/workspace/**"]`） |
| `mode` | `"allow" \| "deny"` | 允许或拒绝（默认 `allow`） |

**注意**：
- `paths` 参数的路径必须以 `/` 开头
- 使用 Glob 模式匹配（如 `/**` 匹配所有子目录）
- 规则按声明顺序评估，第一个匹配的规则生效

### 实践任务

1. ✅ 运行 `hello_agent.py`，成功获得 Agent 响应
2. ✅ 测试 Agent 的三种核心能力（规划、文件、子 Agent）
3. ✅ 创建一个自定义工具，并集成到 Agent
4. ✅ 配置 Backend 和权限，测试文件访问控制
5. ✅ 阅读 `graph.py` 的函数签名，理解 `create_deep_agent` 参数

### 学习检查清单

- [ ] 理解 `create_deep_agent` 的核心作用
- [ ] 知道 Agent 的三个核心能力（规划、文件、子 Agent）
- [ ] 能创建自定义工具并集成
- [ ] 能配置 Backend 和权限
- [ ] 能阅读 SDK 源码的函数签名

---

## 阶段 2：核心概念（理解 Agent 架构）

### 学习目标
- 理解 Agent 的核心架构
- 掌握 Middleware、Backend、Profile 机制
- 建立概念网络，而非孤立记忆

### 学习内容

#### 2.1 LangGraph 基础概念

**WHY 学习**：Deep Agents 基于 LangGraph，理解 LangGraph 是理解 Agent 的前提。

**LangGraph 核心概念**：

| 概念 | 说明 | Deep Agents 对应 |
|------|------|-----------------|
| **StateGraph** | 状态图，定义 Agent 流程 | `create_deep_agent` 返回值 |
| **Node** | 图节点，执行特定任务 | agent_node、tools_node |
| **Edge** | 图边，定义节点连接 | agent → tools → agent |
| **State** | 状态对象，传递数据 | AgentState（messages、todos等） |
| **Checkpointer** | 状态持久化 | MemorySaver、SqliteSaver |

**学习资源**：
- [LangGraph 官方文档](https://langchain-ai.github.io/langgraph/)
- `libs/deepagents/deepagents/graph.py` — StateGraph 构建

#### 2.2 Middleware 栈机制

**WHY 学习**：Middleware 是 Agent 功能扩展的核心机制，理解 Middleware 才能理解 Agent 如何组合功能。

**Middleware 核心概念**：

```
用户请求 → Middleware1 → Middleware2 → ... → Agent → 响应
                ↓
            拦截/增强/记录
```

**Deep Agents 内置 Middleware**：

| Middleware | 功能 | WHY 需要 |
|-----------|------|---------|
| **FilesystemMiddleware** | 文件读写拦截 | 控制文件访问、权限检查 |
| **MemoryMiddleware** | 记忆管理 | 加载 AGENTS.md、项目上下文 |
| **SkillsMiddleware** | 技能加载 | 加载 SKILL.md 工作流指导 |
| **SubagentsMiddleware** | 子 Agent 管理 | 创建和管理子 Agent |
| **SummarizationMiddleware** | 自动摘要 | 长对话自动压缩 |

**阅读源码**：
- `libs/deepagents/deepagents/middleware/__init__.py`
- `libs/deepagents/deepagents/middleware/filesystem.py` — 文件操作拦截
- `libs/deepagents/deepagents/middleware/skills.py` — 技能文件加载

#### 2.3 Backend Protocol 抽象

**WHY 学习**：Backend 是 Agent 的运行环境抽象，理解 Backend 才能理解 Agent 可在何处运行。

**Backend Protocol 定义**：

```python
class BackendProtocol(Protocol):
    def execute(self, command: str) -> ExecuteResult: ...
    def read_file(self, path: str) -> str: ...
    def write_file(self, path: str, content: str) -> None: ...
    def ls(self, path: str) -> list[str]: ...
    def glob(self, pattern: str) -> list[str]: ...
```

**WHY 这样设计**：
- 不强制继承，鸭子类型检查
- 支持多种实现（filesystem、sandbox、langsmith）
- 易于扩展新 Backend（只需实现 Protocol）

**Deep Agents 内置 Backend**：

| Backend | 运行环境 | WHY 使用 |
|---------|---------|---------|
| **FilesystemBackend** | 本地文件系统 | 默认，本地开发 |
| **SandboxBackend** | 远程沙箱 | 安全隔离，云端执行 |
| **LangSmithBackend** | LangSmith 环境 | 追踪调试，生产环境 |

**阅读源码**：
- `libs/deepagents/deepagents/backends/protocol.py`
- `libs/deepagents/deepagents/backends/filesystem.py`

#### 2.4 Provider Profile 机制

**WHY 学习**：Profile 自动应用 Provider 配置，理解 Profile 才能理解不同 Provider 的差异如何封装。

**Profile 核心机制**：

```python
# 注册 Profile
_register_harness_profile("openai", Profile(init_kwargs={"use_responses_api": True}))

# 查找 Profile
profile = _get_harness_profile("openai:gpt-4")
# → 找到 "openai" Provider Profile
# → 应用 {"use_responses_api": True}
```

**Profile 查找流程**：

```
spec = "openai:gpt-4"
    ↓
精确匹配 "openai:gpt-4" → 无
    ↓
Provider 匹配 "openai" → 找到
    ↓
返回 Profile(init_kwargs={"use_responses_api": True})
    ↓
创建 ChatOpenAI(use_responses_api=True)
```

**阅读源码**：
- `libs/deepagents/deepagents/profiles/_harness_profiles.py`
- `libs/deepagents/deepagents/profiles/_openai.py`
- `libs/deepagents/deepagents/profiles/_openrouter.py`

### 实践任务

1. ✅ 阅读 `graph.py`，理解 StateGraph 构建流程
2. ✅ 阅读 `middleware/filesystem.py`，理解 Middleware 拦截机制
3. ✅ 阅读 `backends/protocol.py`，理解 Backend Protocol 定义
4. ✅ 阅读 `profiles/_harness_profiles.py`，理解 Profile 注册和查找
5. ✅ 绘制概念关系图（Middleware、Backend、Profile 的依赖关系）

### 学习检查清单

- [ ] 理解 LangGraph StateGraph、Node、Edge 概念
- [ ] 理解 Middleware 栈的工作原理（拦截/增强）
- [ ] 理解 Backend Protocol 的抽象意义（鸭子类型）
- [ ] 理解 Provider Profile 的查找流程（精确 → Provider → 默认）
- [ ] 能解释 "WHY 用 Middleware 而不硬编码功能"

---

## 阺段 3：进阶功能（深入核心模块）

### 学习目标
- 掌握 Skills 和 Memory 机制
- 理解 SubAgent 工作原理
- 学会配置和扩展 Profile

### 学习内容

#### 3.1 Skills 技能系统

**WHY 学习**：Skills 是 Agent 的"工作流指导"，理解 Skills 才能教会 Agent 特定工作方式。

**Skills 核心概念**：

```markdown
# SKILL.md 格式
---
name: query-writing
description: Writes and executes SQL queries...
---

# Query Writing Skill

## Workflow for Simple Queries
1. Identify the table
2. Get the schema
3. Write the query
...
```

**Skills 加载流程**：

```
skills=["./skills/"]
    ↓
SkillsMiddleware 扫描目录
    ↓
加载 SKILL.md 文件
    ↓
注入到 System Prompt
    ↓
Agent 按技能指导执行
```

**阅读示例**：
- `examples/text-to-sql-agent/skills/query-writing/SKILL.md`
- `examples/text-to-sql-agent/skills/schema-exploration/SKILL.md`
- `libs/deepagents/deepagents/middleware/skills.py`

#### 3.2 Memory 记忆系统

**WHY 学习**：Memory 是 Agent 的"长期记忆"，理解 Memory 才能让 Agent 记住项目上下文。

**Memory 核心概念**：

```python
# 加载记忆文件
memory=["./AGENTS.md", "./project-context.md"]
```

**Memory 文件格式**：

```markdown
# AGENTS.md

## 项目信息
- 项目名称：Deep Agents
- 核心功能：Agent 框架
...

## 开发指南
- 使用 uv 管理依赖
- 测试命令：make test
...
```

**阅读示例**：
- `AGENTS.md`（项目全局开发指南）
- `examples/text-to-sql-agent/AGENTS.md`（示例项目记忆）
- `libs/deepagents/deepagents/middleware/memory.py`

#### 3.3 SubAgent 子 Agent 系统

**WHY 学习**：SubAgent 是 Agent 的"分工协作"，理解 SubAgent 才能让 Agent 拆解复杂任务。

**SubAgent 核心概念**：

```python
# 定义子 Agent
subagents=[
    SubAgent(name="research", description="负责信息收集和研究"),
    SubAgent(name="coding", description="负责代码编写"),
]

# Agent 自动创建子 Agent
agent.invoke({"messages": [{"role": "user", "content": "帮我研究并实现一个爬虫"}]})
# → 主 Agent 创建 research 子 Agent
# → research 子 Agent 收集信息
# → 主 Agent 创建 coding 子 Agent
# → coding 子 Agent 编写代码
```

**SubAgent 工作流程**：

```
用户任务
    ↓
主 Agent 分析任务
    ↓
创建子 Agent（task 工具）
    ↓
子 Agent 独立执行（隔离 context）
    ↓
子 Agent 返回结果
    ↓
主 Agent 汇合结果
```

**阅读源码**：
- `libs/deepagents/deepagents/middleware/subagents.py`
- `libs/deepagents/deepagents/middleware/async_subagents.py`

#### 3.4 Profile 扩展实践

**WHY 学习**：学会添加自定义 Profile，封装 Provider 特定配置。

**添加 Anthropic Profile 示例**：

```python
# profiles/_anthropic.py（假设）
from deepagents.profiles._harness_profiles import _HarnessProfile, _register_harness_profile

_register_harness_profile(
    "anthropic",
    _HarnessProfile(
        system_prompt_suffix="使用 prompt caching 优化性能。",
        # 注意：extra_middleware 需要具体实现
    ),
)
```

### 实践任务

1. ✅ 创建一个自定义 Skill（如 `./skills/data-analysis/SKILL.md`）
2. ✅ 创建一个 Memory 文件（如 `./memory/my-project.md`）
3. ✅ 运行 Agent，观察 Skill 和 Memory 如何影响行为
4. ✅ 阅读 `subagents.py`，理解 SubAgent 创建机制
5. ✅ 尝试添加一个简单的 Provider Profile（如本地模型）

### 学习检查清单

- [ ] 理解 SKILL.md 格式和加载机制
- [ ] 理解 Memory 文件的作用（项目上下文）
- [ ] 理解 SubAgent 的分工协作原理
- [ ] 能创建自定义 Skill 和 Memory
- [ ] 能解释 "WHY 用 Skill 而不硬编码流程"

---

## 阶段 4：实战应用（Examples 深度学习）

### 学习目标
- 通过 Examples 学习实际应用
- 理解不同场景的 Agent 配置
- 完成一个完整的 Agent 项目

### 学习内容

#### 4.1 Examples 目录概览

**WHY 学习**：Examples 是最佳的学习材料，展示真实场景的 Agent 配置。

**Examples 清单**：

| Example | 应用场景 | 学习重点 |
|---------|---------|---------|
| **text-to-sql-agent** | SQL 查询 Agent | Skills、Toolkit、Backend |
| **deep_research** | 深度研究 Agent | SubAgent、Web Search |
| **content-builder-agent** | 内容生成 Agent | Skills、Memory |
| **deploy-coding-agent** | 部署 Agent | CLI deploy、permissions |
| **nvidia_deep_agent** | GPU Agent | Custom Backend、Skills |

#### 4.2 text-to-sql-agent 详细学习

**WHY 学习**：最完整的示例，包含 Skills、Toolkit、Backend。

**代码结构**：

```
text-to-sql-agent/
├── agent.py           # 入口（create_deep_agent）
├── skills/
│   ├── query-writing/SKILL.md      # 查询编写技能
│   └── schema-exploration/SKILL.md # Schema 探索技能
├── AGENTS.md          # Agent 身份定义
├── .env               # API keys
└── chinook.db         # 测试数据库
```

**学习步骤**：

1. **阅读 agent.py**：理解如何集成 SQL Toolkit
   ```python
   toolkit = SQLDatabaseToolkit(db=db, llm=model)
   sql_tools = toolkit.get_tools()
   
   agent = create_deep_agent(
       model=model,
       tools=sql_tools,
       skills=["./skills/"],
       memory=["./AGENTS.md"],
       backend=FilesystemBackend(root_dir=base_dir),
   )
   ```

2. **阅读 Skills 文件**：理解 Skill 如何指导 Agent
   - `query-writing/SKILL.md` — SQL 查询流程指导
   - `schema-exploration/SKILL.md` — Schema 探索流程指导

3. **运行并观察**：
   ```bash
   python agent.py "How many customers are from Canada?"
   ```

#### 4.3 deep_research 详细学习

**WHY 学习**：展示 SubAgent 和 Web Search 的集成。

**学习重点**：
- SubAgent 如何分工协作
- Web Search 如何集成（tavily）
- Research 流程如何编排

#### 4.4 content-builder-agent 详细学习

**WHY 学习**：展示 Skills 和 Memory 的典型用法。

**学习重点**：
- Skills 如何定义写作流程
- Memory 如何提供写作上下文
- Agent 如何生成内容

### 实践任务

1. ✅ 完整运行 text-to-sql-agent，理解每个组件的作用
2. ✅ 阅读 3 个 Skill 文件，理解 Skill 格式
3. ✅ 修改 text-to-sql-agent 的 AGENTS.md，观察行为变化
4. ✅ 运行 deep_research，观察 SubAgent 如何工作
5. ✅ 完成一个自己的 Example（如"天气查询 Agent"）

### 学习检查清单

- [ ] 成功运行至少 3 个 Examples
- [ ] 理解 text-to-sql-agent 的完整架构
- [ ] 能解释 Toolkit、Skills、Memory 的组合方式
- [ ] 能修改 Example 并观察行为变化
- [ ] 能创建一个完整的 Agent 项目（包含 Skills、Memory、Backend）

---

## 阶段 5：扩展开发（Partners、ACP、自定义）

### 学习目标
- 理解 Partners 包的 Backend 插件机制
- 理解 ACP 集成原理
- 学会自定义 Backend 和 Middleware

### 学习内容

#### 5.1 Partners 包架构

**WHY 学习**：理解 Backend 插件化，学会集成第三方服务。

**Partners 清单**：

| Partner | Backend 类型 | WHY 使用 |
|---------|-------------|---------|
| **daytona** | Daytona 沙箱 | 远程开发环境 |
| **modal** | Modal 沙箱 | 云端函数执行 |
| **runloop** | Runloop 沙箱 | GPU 加速执行 |
| **quickjs** | QuickJS REPL | JavaScript 执行 |

**阅读源码**：
- `libs/partners/daytona/langchain_daytona/backend.py`
- `libs/partners/modal/langchain_modal/backend.py`

**Backend Protocol 实现**：

```python
# Daytona Backend 实现
class DaytonaSandbox(BackendProtocol):
    def __init__(self, sandbox):
        self.sandbox = sandbox
    
    def execute(self, command: str) -> ExecuteResult:
        result = self.sandbox.run_command(command)
        return ExecuteResult(output=result.stdout, exit_code=result.code)
```

#### 5.2 ACP 集成原理

**WHY 学习**：理解 Agent Client Protocol，学会在编辑器中使用 Agent。

**ACP 核心概念**：
- Agent Client Protocol — 编辑器与 Agent 的通信协议
- Zed 编辑器 — 支持 ACP 的编辑器
- AgentServerACP — ACP 服务器实现

**阅读源码**：
- `libs/acp/deepagents_acp/server.py`

**ACP 使用流程**：

```
Zed 编辑器
    ↓
ACP 协议通信
    ↓
AgentServerACP
    ↓
Deep Agent 执行
    ↓
返回结果到 Zed
```

#### 5.3 自定义 Backend 实践

**WHY 学习**：学会创建自定义 Backend，集成自己的运行环境。

**自定义 Backend 示例**：

```python
# custom_backend.py
from deepagents.backends.protocol import BackendProtocol, ExecuteResult

class MyCustomBackend(BackendProtocol):
    def __init__(self, config):
        self.config = config
    
    def execute(self, command: str) -> ExecuteResult:
        # 自定义执行逻辑
        result = self.my_executor.run(command)
        return ExecuteResult(output=result.stdout, exit_code=result.code)
    
    def read_file(self, path: str) -> str:
        # 自定义读取逻辑
        return self.my_storage.read(path)
    
    def write_file(self, path: str, content: str) -> None:
        # 自定义写入逻辑
        self.my_storage.write(path, content)

# 使用自定义 Backend
agent = create_deep_agent(backend=MyCustomBackend(config))
```

#### 5.4 自定义 Middleware 实践

**WHY 学习**：学会创建自定义 Middleware，扩展 Agent 功能。

**自定义 Middleware 示例**：

```python
# custom_middleware.py
from langchain.agents.middleware.types import AgentMiddleware

class MyCustomMiddleware(AgentMiddleware):
    def __init__(self, config):
        self.config = config
    
    def process_request(self, request):
        # 拦截请求
        # 可以修改 request.messages、request.tools 等
        return request
    
    def process_response(self, response):
        # 拦截响应
        # 可以修改 response.content、response.tool_calls 等
        return response

# 使用自定义 Middleware
agent = create_deep_agent(
    middleware=[MyCustomMiddleware(config)]
)
```

### 实践任务

1. ✅ 阅读 2 个 Partners Backend 实现（daytona、modal）
2. ✅ 阅读 ACP server.py，理解 ACP 集成原理
3. ✅ 创建一个简单的自定义 Backend（如 Docker Backend）
4. ✅ 创建一个简单的自定义 Middleware（如日志 Middleware）
5. ✅ 测试自定义 Backend 和 Middleware

### 学习检查清单

- [ ] 理解 Backend Protocol 的接口定义
- [ ] 理解 Partners 包的插件化设计
- [ ] 理解 ACP 协议的作用和原理
- [ ] 能创建自定义 Backend
- [ ] 能创建自定义 Middleware
- [ ] 能解释 "WHY 用 Protocol 而不用继承"

---

## 阺段 6：源码精通（深入核心实现）

### 学习目标
- 精读核心源码（graph.py、profiles、middleware）
- 理解设计细节和权衡
- 达到"能独立实现类似功能"的水平

### 学习内容

#### 6.1 graph.py 核心源码精读

**WHY 学习**：graph.py 是 SDK 核心，精读后理解 Agent 构建的完整流程。

**精读重点**：

| 函数/类 | 作用 | WHY 深入 |
|---------|------|---------|
| `create_deep_agent` | Agent 构建入口 | 理解参数处理、配置组合 |
| `_build_state_graph` | StateGraph 构建 | 理解节点、边、状态定义 |
| `_compile_agent` | Agent 编译 | 理解编译流程、Checkpointer |
| `resolve_model` | 模型解析 | 理解 Provider 识别、Profile 应用 |

**精读方法**：
1. 逐行阅读，理解每个变量的作用
2. 画出执行流程图
3. 标注关键设计点（WHY 这样设计）
4. 对比替代方案（WHY 不用其他）

**阅读文档**：
- `Deep-Agents-项目组件分析.md` — 第 6 章片段 #1

#### 6.2 profiles 模块精读

**WHY 学习**：理解 Profile 注册和查找的完整机制。

**精读重点**：

| 文件 | 作用 | WHY 深入 |
|------|------|---------|
| `_harness_profiles.py` | Profile 注册机制 | 理解 Registry Pattern |
| `_openai.py` | OpenAI Profile | 理解静态配置封装 |
| `_openrouter.py` | OpenRouter Profile | 理解动态配置（版本检查、Attribution） |

**阅读文档**：
- `Deep-Agents-项目组件分析.md` — 第 6 章片段 #2

#### 6.3 middleware 模块精读

**WHY 学习**：理解 Middleware 的完整实现和拦截机制。

**精读重点**：

| 文件 | 作用 | WHY 深入 |
|------|------|---------|
| `filesystem.py` | 文件操作拦截 | 理解权限检查、路径验证 |
| `skills.py` | Skills 加载 | 理解 SKILL.md 解析、注入 Prompt |
| `memory.py` | Memory 加载 | 理解记忆文件加载、上下文注入 |
| `subagents.py` | SubAgent 管理 | 理解子 Agent 创建、结果汇合 |

**精读方法**：
1. 阅读 Middleware Protocol 定义
2. 理解 request/response 拦截流程
3. 标注关键设计点（WHY 拦截、WHY 不直接修改）

#### 6.4 backends 模块精读

**WHY 学习**：理解 Backend 的完整实现和协议设计。

**精读重点**：

| 文件 | 作用 | WHY 深入 |
|------|------|---------|
| `protocol.py` | Protocol 定义 | 理鸭子类型、接口契约 |
| `filesystem.py` | 本地 Backend | 理解本地文件操作实现 |
| `composite.py` | 组合 Backend | 理解多 Backend 组合 |
| `sandbox.py` | Sandbox Backend | 理解远程沙箱抽象 |

### 实践任务

1. ✅ 精读 graph.py，画出完整的执行流程图
2. ✅ 精读 profiles/_harness_profiles.py，理解 Registry Pattern
3. ✅ 精读 middleware/filesystem.py，理解拦截机制
4. ✅ 精读 backends/protocol.py，理解 Protocol 设计
5. ✅ 尝试重构一个核心函数（如 `_get_harness_profile`），理解设计权衡

### 学习检查清单

- [ ] 能解释 create_deep_agent 的完整执行流程
- [ ] 能解释 Profile 查找的分层逻辑（精确 → Provider → 默认）
- [ ] 能解释 Middleware 的拦截原理（request/response）
- [ ] 能解释 Backend Protocol 的设计意义（鸭子类型）
- [ ] 能回答"四能"测试：
    1. ✅ 能否理解代码的设计思路？
    2. ✅ 能否独立实现类似功能？
    3. ✅ 能否应用到不同场景？
    4. ✅ 能否向他人清晰解释？

---

## 学习资源推荐

### 官方文档

| 资源 | 链接 | 用途 |
|------|------|------|
| Deep Agents 文档 | https://docs.langchain.com/oss/python/deepagents/overview | 概念和 API 参考 |
| LangGraph 文档 | https://langchain-ai.github.io/langgraph/ | 底层框架理解 |
| LangChain 文档 | https://python.langchain.com/ | 工具和模型集成 |

### 项目内文档

| 资源 | 位置 | 用途 |
|------|------|------|
| 项目概览 | README.md | 快速了解项目定位 |
| 开发指南 | AGENTS.md | AI Agent 开发规范 |
| 组件分析 | Deep-Agents-项目组件分析.md | 架构和关系详解 |
| Examples 学习 | examples/README.md | Examples 概览 |

### 外部学习资源

| 资源 | 链接 | 用途 |
|------|------|------|
| LangChain 官方教程 | https://python.langchain.com/docs/tutorials/ | LangChain 基础 |
| LangGraph 教程 | https://langchain-ai.github.io/langgraph/tutorials/ | LangGraph 基础 |
| Python Protocol | https://typing.readthedocs.io/en/latest/protocols.html | Protocol 概念 |

---

## 学习时间建议

### 快速路径（1-2 天）

适合：快速上手使用 SDK

```
阶段 0（半天）→ 阶段 1（1 天）→ 完成
```

### 标准路径（1-2 周）

适合：理解核心原理

```
阶段 0（半天）→ 阶段 1（1 天）→ 阶段 2（2 天）→ 阺段 3（2 天）→ 阺段 4（2 天）→ 完成
```

### 深度路径（2-4 周）

适合：深度定制开发

```
阶段 0-4（1-2 周）→ 阶段 5（1 周）→ 阺段 6（1 周）→ 完成
```

### 专家路径（3-5 周）

适合：贡献代码、面试准备

```
全部阶段 + 源码分析 + 贡献实践
```

---

## 学习检查总清单

### 阺段 0 检查
- [ ] Python 3.12 环境已配置
- [ ] uv 已安装并可用
- [ ] 项目已克隆并理解目录结构
- [ ] 至少一个 LLM Provider 已配置
- [ ] 基础依赖已安装

### 阶段 1 检查
- [ ] 理解 create_deep_agent 的核心作用
- [ ] 知道 Agent 的三个核心能力
- [ ] 能创建自定义工具并集成
- [ ] 能配置 Backend 和权限
- [ ] 能阅读 SDK 源码的函数签名

### 阺段 2 检查
- [ ] 理解 LangGraph StateGraph、Node、Edge 概念
- [ ] 理解 Middleware 栈的工作原理
- [ ] 理解 Backend Protocol 的抽象意义
- [ ] 理解 Provider Profile 的查找流程
- [ ] 能解释 "WHY 用 Middleware 而不硬编码"

### 阺段 3 检查
- [ ] 理解 SKILL.md 格式和加载机制
- [ ] 理解 Memory 文件的作用
- [ ] 理解 SubAgent 的分工协作原理
- [ ] 能创建自定义 Skill 和 Memory
- [ ] 能解释 "WHY 用 Skill 而不硬编码流程"

### 阺段 4 检查
- [ ] 成功运行至少 3 个 Examples
- [ ] 理解 text-to-sql-agent 的完整架构
- [ ] 能解释 Toolkit、Skills、Memory 的组合方式
- [ ] 能修改 Example 并观察行为变化
- [ ] 能创建一个完整的 Agent 项目

### 阺段 5 检查
- [ ] 理解 Backend Protocol 的接口定义
- [ ] 理解 Partners 包的插件化设计
- [ ] 理解 ACP 协议的作用和原理
- [ ] 能创建自定义 Backend
- [ ] 能创建自定义 Middleware

### 阺段 6 检查
- [ ] 能解释 create_deep_agent 的完整执行流程
- [ ] 能解释 Profile 查找的分层逻辑
- [ ] 能解释 Middleware 的拦截原理
- [ ] 能解释 Backend Protocol 的设计意义
- [ ] 通过"四能"测试

---

## 最终目标：成为 Deep Agents 专家

**专家标准**：
1. ✅ 能解释所有核心概念的设计动机（WHY）
2. ✅ 能独立实现类似功能（不依赖文档）
3. ✅ 能应用到不同场景（迁移能力）
4. ✅ 能向他人清晰讲解（表达能力）
5. ✅ 能贡献代码或提出改进建议

**恭喜你完成学习！** 🎉

---

**文档生成时间**：2026-04-12  
**适用版本**：Deep Agents v0.5.2