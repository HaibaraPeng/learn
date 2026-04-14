# Deep Agents graph.py 详细解析

> 本文档深入解析 `libs/deepagents/deepagents/graph.py` 文件
> 
> **阅读时间**：30-40 分钟
> 
> **适用人群**：想要理解 Deep Agent 核心创建流程的开发者

---

## 目录

1. [文件概述](#一文件概述)
2. [导入依赖分析](#二导入依赖分析)
3. [BASE_AGENT_PROMPT - 默认系统提示词](#三base_agent_prompt---默认系统提示词)
4. [辅助函数详解](#四辅助函数详解)
5. [create_deep_agent 函数详解](#五create_deep_agent-函数详解)
6. [Middleware 构建流程](#六middleware-构建流程)
7. [SubAgent 处理流程](#七subagent-处理流程)
8. [最终 Agent 创建](#八最终-agent-创建)
9. [参数传递图](#九参数传递图)
10. [关键设计点总结](#十关键设计点总结)

---

## 一、文件概述

### 文件定位

```python
"""Primary graph assembly module for Deep Agents.

Provides `create_deep_agent`, the main entry point for constructing a fully
configured Deep Agent with planning, filesystem, subagent, and summarization
middleware.
"""
```

**一句话定义**：`graph.py` 是 Deep Agents 的核心入口模块，提供 `create_deep_agent` 函数，用于构建完整的 Deep Agent。

---

### 核心功能

```
create_deep_agent(model, tools, skills, memory, permissions, ...)
    ↓
构建 Middleware 栈
    ↓
处理 SubAgent 配置
    ↓
组装 System Prompt
    ↓
调用 create_agent（LangChain）
    ↓
返回 CompiledStateGraph（可执行 Agent）
```

---

### 文件结构概览

```
graph.py (635 lines)
├── 导入依赖 (line 1-48)
├── BASE_AGENT_PROMPT (line 50-99) - 默认系统提示词
├── get_default_model() (line 101-113) - 获取默认模型
├── _resolve_extra_middleware() (line 116-130) - 解析 Profile middleware
├── _harness_profile_for_model() (line 133-160) - 查找 Profile
├── _tool_name() (line 163-176) - 提取工具名称
├── _apply_tool_description_overrides() (line 179-215) - 应用工具描述覆盖
├── create_deep_agent() (line 218-635) - 核心 Agent 创建函数
│   ├── 参数定义 (line 219-237)
│   ├── 文档字符串 (line 238-415)
│   ├── 模型解析 (line 416-430)
│   ├── 工具处理 (line 432-438)
│   ├── Backend 设置 (line 440)
│   ├── GP SubAgent middleware (line 443-466)
│   ├── SubAgent 处理 (line 468-548)
│   ├── Main Agent middleware (line 551-598)
│   ├── System Prompt 组装 (line 600-612)
│   └── 调用 create_agent (line 614-635)
```

---

## 二、导入依赖分析

### LangChain/LangGraph 导入

```python
from langchain.agents import AgentState, create_agent  # 核心：Agent 创建函数
from langchain.agents.middleware import (
    HumanInTheLoopMiddleware,  # 人机交互 middleware
    InterruptOnConfig,          # 中断配置
    TodoListMiddleware,         # Todo 列表 middleware
)
from langchain.agents.middleware.types import (
    AgentMiddleware,            # Middleware 基类
    ResponseT,                  # 响应类型
    _InputAgentState,           # 输入状态
    _OutputAgentState,          # 输出状态
)
from langchain.agents.structured_output import ResponseFormat  # 结构化输出
from langchain_anthropic import ChatAnthropic  # Anthropic 模型
from langchain_anthropic.middleware import AnthropicPromptCachingMiddleware  # Prompt 缓存
from langchain_core.language_models import BaseChatModel  # 模型基类
from langchain_core.messages import SystemMessage  # 系统消息
from langchain_core.tools import BaseTool  # 工具基类
from langgraph.cache.base import BaseCache  # 缓存基类
from langgraph.graph.state import CompiledStateGraph  # 编译后的状态图
from langgraph.store.base import BaseStore  # 存储基类
from langgraph.types import Checkpointer  # 检查点
from langgraph.typing import ContextT  # 上下文类型
```

**关键导入**：
- `create_agent`：LangChain 的 Agent 创建函数，Deep Agents 的底层
- `AgentState`：LangGraph Agent 的状态定义
- `AgentMiddleware`：Middleware 基类，所有 middleware 都继承它

---

### Deep Agents 内部导入

```python
from deepagents._models import (
    get_model_identifier,  # 获取模型标识符
    get_model_provider,     # 获取模型提供商
    resolve_model,          # 解析模型字符串/对象
)
from deepagents._version import __version__  # 版本号
from deepagents.backends import StateBackend  # 状态后端（默认）
from deepagents.backends.protocol import (
    BackendFactory,   # Backend 工厂类型
    BackendProtocol,  # Backend 协议
)
from deepagents.middleware._tool_exclusion import _ToolExclusionMiddleware  # 工具排除
from deepagents.middleware.async_subagents import (
    AsyncSubAgent,              # 异步子 Agent 类型
    AsyncSubAgentMiddleware,    # 异步子 Agent middleware
)
from deepagents.middleware.filesystem import FilesystemMiddleware  # 文件系统 middleware
from deepagents.middleware.memory import MemoryMiddleware  # 记忆 middleware
from deepagents.middleware.patch_tool_calls import PatchToolCallsMiddleware  # 工具调用修复
from deepagents.middleware.permissions import (
    FilesystemPermission,       # 文件权限类型
    _PermissionMiddleware,      # 权限 middleware
)
from deepagents.middleware.skills import SkillsMiddleware  # 技能 middleware
from deepagents.middleware.subagents import (
    GENERAL_PURPOSE_SUBAGENT,  # 通用子 Agent 默认配置
    CompiledSubAgent,          # 编译后的子 Agent
    SubAgent,                  # 子 Agent 类型
    SubAgentMiddleware,        # 子 Agent middleware
)
from deepagents.middleware.summarization import create_summarization_middleware  # 摘要 middleware
from deepagents.profiles import (
    _get_harness_profile,  # 获取 Profile
    _HarnessProfile,       # Profile 类型
)
```

---

### 导入依赖关系图

```
LangChain Core
    ├── create_agent (核心 Agent 创建)
    ├── AgentMiddleware (Middleware 基类)
    └── BaseChatModel, BaseTool (基础类型)
    ↓
Deep Agents
    ├── graph.py (本文件)
    ├── middleware/*.py (各种 Middleware 实现)
    ├── backends/*.py (各种 Backend 实现)
    ├── profiles/*.py (Provider Profile)
    └── _models.py (模型解析)
```

---

## 三、BASE_AGENT_PROMPT - 默认系统提示词

### 定义

```python
BASE_AGENT_PROMPT = """You are a Deep Agent, an AI assistant that helps users 
accomplish tasks using tools. You respond with text and tool calls. 
The user can see your responses and tool outputs in real time.

## Core Behavior

- Be concise and direct. Don't over-explain unless asked.
- NEVER add unnecessary preamble ("Sure!", "Great question!", "I'll now...").
- Don't say "I'll now do X" — just do it.
- If the request is underspecified, ask only the minimum followup needed.
...

## Professional Objectivity

- Prioritize accuracy over validating the user's beliefs
- Disagree respectfully when the user is incorrect
...

## Doing Tasks

When the user asks you to do something:

1. **Understand first** — read relevant files, check existing patterns.
2. **Act** — implement the solution. Work quickly but accurately.
3. **Verify** — check your work against what was asked.
...

## Clarifying Requests

- Do not ask for details the user already supplied.
- Use reasonable defaults when the request clearly implies them.
...

## Progress Updates

For longer tasks, provide brief progress updates at reasonable intervals.
"""  # noqa: E501
```

---

### 提示词设计原则

#### 1. 简洁直接

```
- Be concise and direct. Don't over-explain unless asked.
- NEVER add unnecessary preamble ("Sure!", "Great question!", "I'll now...").
- Don't say "I'll now do X" — just do it.
```

**WHY**：
- 减少无效输出
- 提高响应速度
- 节省 token 成本

---

#### 2. 专业客观

```
- Prioritize accuracy over validating the user's beliefs
- Disagree respectfully when the user is incorrect
- Avoid unnecessary superlatives, praise, or emotional validation
```

**WHY**：
- 避免迎合用户错误
- 保持专业态度
- 提供准确信息

---

#### 3. 任务执行流程

```
When the user asks you to do something:

1. **Understand first** — read relevant files, check existing patterns.
2. **Act** — implement the solution. Work quickly but accurately.
3. **Verify** — check your work against what was asked.
```

**WHY**：
- 标准化工作流程
- 提高成功率
- 避免盲目执行

---

#### 4. 持续工作直到完成

```
Keep working until the task is fully complete. Don't stop partway and 
explain what you would do — just do it. Only yield back to the user 
when the task is done or you're genuinely blocked.
```

**WHY**：
- 避免"半途而废"
- 确保 Agent 主动完成
- 减少用户干预

---

#### 5. 错误处理

```
**When things go wrong:**
- If something fails repeatedly, stop and analyze *why* — don't keep 
  retrying the same approach.
- If you're blocked, tell the user what's wrong and ask for guidance.
```

**WHY**：
- 避免无限循环
- 及时请求帮助
- 分析根本原因

---

### 提示词组合方式

```python
# 用户传入 system_prompt
system_prompt = "你是一个专业的 Python 开发助手..."

# 组合方式：用户 prompt + BASE_AGENT_PROMPT
final_system_prompt = system_prompt + "\n\n" + BASE_AGENT_PROMPT

# 结果：
"""
你是一个专业的 Python 开发助手...

You are a Deep Agent, an AI assistant that helps users accomplish tasks...
"""
```

**组合顺序**：
- 用户 prompt 在前（定制化）
- BASE_AGENT_PROMPT 在后（通用行为规范）

---

## 四、辅助函数详解

### 1. get_default_model() - 获取默认模型

```python
def get_default_model() -> ChatAnthropic:
    """Get the default model for Deep Agents.
    
    Used as a fallback when `model=None` is passed to `create_deep_agent`.
    
    Requires `ANTHROPIC_API_KEY` to be set in the environment.
    
    Returns:
        `ChatAnthropic` instance configured with `claude-sonnet-4-6`.
    """
    return ChatAnthropic(
        model_name="claude-sonnet-4-6",
    )
```

**用途**：
- 当用户未指定模型时，使用默认模型
- 默认：Anthropic Claude Sonnet 4.6
- 需要环境变量 `ANTHROPIC_API_KEY`

**废弃警告**：

```python
if model is None:
    warnings.warn(
        "Passing `model=None` to `create_deep_agent` is deprecated...",
        DeprecationWarning,
    )
```

---

### 2. _resolve_extra_middleware() - 解析 Profile middleware

```python
def _resolve_extra_middleware(
    profile: _HarnessProfile,
) -> list[AgentMiddleware[Any, Any, Any]]:
    """Materialize the `extra_middleware` from a provider profile.
    
    Args:
        profile: The provider profile to read from.
    
    Returns:
        A fresh list of middleware instances (may be empty).
    """
    extra = profile.extra_middleware
    if callable(extra):
        return list(extra())  # 如果是函数，调用它
    return list(extra)       # 如果是列表，直接返回
```

**用途**：
- 从 Provider Profile 提取额外的 middleware
- 支持 lazy initialization（ callable）
- 确保每次调用返回新列表

---

### 3. _harness_profile_for_model() - 查找 Profile

```python
def _harness_profile_for_model(
    model: BaseChatModel,
    spec: str | None,
) -> _HarnessProfile:
    """Look up the `_HarnessProfile` for an already-resolved model.
    
    Args:
        model: Resolved chat model instance.
        spec: Original model spec string, or `None` for pre-built instances.
    
    Returns:
        The matching `_HarnessProfile`, or an empty default (null object).
    """
```

---

#### Profile 查找流程

```
查找 Profile 的优先级：

1. 如果 spec 存在（用户传入字符串）
    ↓
   使用 spec 直接查找 Profile
    ↓
   _get_harness_profile(spec)
    ↓
   例如："openai:gpt-4" → 找到 OpenAI Profile

2. 如果 spec 不存在（用户传入对象）
    ↓
   从 model 提取 identifier
    ↓
   get_model_identifier(model)
    ↓
   使用 identifier 查找 Profile
    ↓
   如果找到，返回 Profile

3. 如果 identifier 查找失败
    ↓
   从 model 提取 provider
    ↓
   get_model_provider(model)
    ↓
   使用 provider 查找 Profile
    ↓
   例如："anthropic" → Anthropic Profile

4. 所有查找都失败
    ↓
   返回空 Profile（_HarnessProfile()）
```

---

### 4. _tool_name() - 提取工具名称

```python
def _tool_name(tool: BaseTool | Callable | dict[str, Any]) -> str | None:
    """Extract the tool name from any supported tool type.
    
    Args:
        tool: A tool in any of the forms accepted by `create_deep_agent`.
    
    Returns:
        The tool name, or `None` if it cannot be determined.
    """
    if isinstance(tool, dict):
        name = tool.get("name")
        return name if isinstance(name, str) else None
    name = getattr(tool, "name", None)
    return name if isinstance(name, str) else None
```

**用途**：
- 工具可以是三种形式：`BaseTool`、`Callable`、`dict`
- 统一提取工具名称
- 用于工具描述覆盖

---

### 5. _apply_tool_description_overrides() - 应用工具描述覆盖

```python
def _apply_tool_description_overrides(
    tools: Sequence[BaseTool | Callable | dict[str, Any]] | None,
    overrides: dict[str, str],
) -> list[BaseTool | Callable | dict[str, Any]] | None:
    """Apply description overrides without mutating caller-owned tools.
    
    Args:
        tools: User-supplied tools to copy and possibly rewrite.
        overrides: Description overrides keyed by tool name.
    
    Returns:
        A copied tool list with supported overrides applied, or `None`.
    """
```

---

#### 实现要点

**不修改原始工具**：

```python
# 复制工具，避免修改用户传入的对象
copied_tools: list[BaseTool | Callable | dict[str, Any]] = []
for tool in tools:
    # ...
    copied_tools.append(tool)  # 复制后添加
```

**支持两种工具类型**：

```python
# dict 工具：直接修改字典
if isinstance(tool, dict):
    rewritten_tool = tool.copy()
    rewritten_tool["description"] = override
    copied_tools.append(rewritten_tool)

# BaseTool 工具：使用 model_copy
if isinstance(tool, BaseTool):
    copied_tools.append(tool.model_copy(update={"description": override}))

# Callable 工具：无法安全修改描述，保持原样
copied_tools.append(tool)
```

---

#### 示例

```python
# Profile 配置的工具描述覆盖
overrides = {
    "execute": "Run shell commands in the sandbox environment...",
    "task": "Delegate work to specialized subagents...",
}

# 应用覆盖
tools = [
    {"name": "execute", "description": "Run commands"},  # dict
    ExecuteTool(),  # BaseTool
    my_function,    # Callable
]

result = _apply_tool_description_overrides(tools, overrides)

# 结果：
# [
#     {"name": "execute", "description": "Run shell commands in the sandbox..."},
#     ExecuteTool(description="Run shell commands in the sandbox..."),
#     my_function,  # 未修改
# ]
```

---

## 五、create_deep_agent 函数详解

### 函数签名

```python
def create_deep_agent(  # noqa: C901, PLR0912, PLR0915
    model: str | BaseChatModel | None = None,
    tools: Sequence[BaseTool | Callable | dict[str, Any]] | None = None,
    *,
    system_prompt: str | SystemMessage | None = None,
    middleware: Sequence[AgentMiddleware] = (),
    subagents: Sequence[SubAgent | CompiledSubAgent | AsyncSubAgent] | None = None,
    skills: list[str] | None = None,
    memory: list[str] | None = None,
    permissions: list[FilesystemPermission] | None = None,
    response_format: ResponseFormat[ResponseT] | type[ResponseT] | dict[str, Any] | None = None,
    context_schema: type[ContextT] | None = None,
    checkpointer: Checkpointer | None = None,
    store: BaseStore | None = None,
    backend: BackendProtocol | BackendFactory | None = None,
    interrupt_on: dict[str, bool | InterruptOnConfig] | None = None,
    debug: bool = False,
    name: str | None = None,
    cache: BaseCache | None = None,
) -> CompiledStateGraph[AgentState[ResponseT], ContextT, _InputAgentState, _OutputAgentState[ResponseT]]:
```

---

### 参数详解

#### 1. model - 模型配置

```python
model: str | BaseChatModel | None = None
```

**类型**：
- `str`：模型字符串（如 `"openai:gpt-4"`）
- `BaseChatModel`：已初始化的模型对象
- `None`：使用默认模型（废弃）

**示例**：

```python
# 字符串形式
agent = create_deep_agent(model="openai:gpt-4")
agent = create_deep_agent(model="anthropic:claude-3-5-sonnet")

# 对象形式
from langchain_openai import ChatOpenAI
model = ChatOpenAI(model="gpt-4")
agent = create_deep_agent(model=model)

# 默认（废弃）
agent = create_deep_agent()  # → claude-sonnet-4-6
```

---

#### 2. tools - 自定义工具

```python
tools: Sequence[BaseTool | Callable | dict[str, Any]] | None = None
```

**类型**：
- `BaseTool`：LangChain 工具对象
- `Callable`：Python 函数（自动转换为工具）
- `dict`：工具定义字典

**示例**：

```python
from langchain_core.tools import tool

# 方式 1: 使用 @tool 装饰器
@tool
def get_weather(city: str) -> str:
    """Get weather for a city"""
    return f"Weather in {city}: sunny"

agent = create_deep_agent(tools=[get_weather])

# 方式 2: 使用 BaseTool
from langchain_core.tools import StructuredTool
weather_tool = StructuredTool.from_function(get_weather)
agent = create_deep_agent(tools=[weather_tool])

# 方式 3: 使用 dict
agent = create_deep_agent(tools=[
    {"name": "get_weather", "description": "...", "parameters": {...}}
])
```

---

#### 3. system_prompt - 系统提示词

```python
system_prompt: str | SystemMessage | None = None
```

**类型**：
- `str`：简单字符串（与 BASE_AGENT_PROMPT 拼接）
- `SystemMessage`：复杂消息（支持 content blocks）
- `None`：仅使用 BASE_AGENT_PROMPT

**拼接方式**：

```python
# String: system_prompt + BASE_AGENT_PROMPT
final_system_prompt = system_prompt + "\n\n" + BASE_AGENT_PROMPT

# SystemMessage: 合并 content_blocks
final_system_prompt = SystemMessage(
    content_blocks=[*system_prompt.content_blocks, {"type": "text", "text": BASE_AGENT_PROMPT}]
)

# None: 仅 BASE_AGENT_PROMPT
final_system_prompt = BASE_AGENT_PROMPT
```

---

#### 4. middleware - 自定义 middleware

```python
middleware: Sequence[AgentMiddleware] = ()
```

**用途**：
- 添加用户自定义 middleware
- 插入位置：在基础栈之后，在尾部栈之前

**插入位置**：

```
Middleware 栈顺序：

基础栈：
    - TodoListMiddleware
    - SkillsMiddleware (条件)
    - FilesystemMiddleware
    - SubAgentMiddleware
    - SummarizationMiddleware
    - PatchToolCallsMiddleware
    - AsyncSubAgentMiddleware (条件)

★ 用户 middleware 插入位置 ★

尾部栈：
    - Profile extra_middleware
    - _ToolExclusionMiddleware (条件)
    - AnthropicPromptCachingMiddleware
    - MemoryMiddleware (条件)
    - HumanInTheLoopMiddleware (条件)
    - _PermissionMiddleware (条件，必须最后)
```

---

#### 5. subagents - 子 Agent 配置

```python
subagents: Sequence[SubAgent | CompiledSubAgent | AsyncSubAgent] | None = None
```

**类型**：
- `SubAgent`：声明式子 Agent（自动构建）
- `CompiledSubAgent`：预编译子 Agent（自定义 Runnable）
- `AsyncSubAgent`：异步子 Agent（远程执行）

**示例**：

```python
from deepagents.middleware.subagents import SubAgent
from deepagents.middleware.async_subagents import AsyncSubAgent

# 声明式子 Agent
subagents = [
    SubAgent(
        name="research",
        description="Research and gather information",
        system_prompt="You are a research agent...",
        tools=[web_search_tool],
    )
]

# 异步子 Agent
async_subagents = [
    AsyncSubAgent(
        name="background_analysis",
        description="Background analysis task",
        graph_id="analysis-agent",
        url="https://api.langsmith.com/...",
    )
]

agent = create_deep_agent(subagents=subagents)
```

---

#### 6. skills - 技能文件

```python
skills: list[str] | None = None
```

**用途**：
- 加载 SKILL.md 文件
- 注入工作流指导到 system prompt

**示例**：

```python
agent = create_deep_agent(
    skills=["/skills/query-writing/", "/skills/schema-exploration/"]
)

# SKILL.md 格式：
# ---
# name: query-writing
# description: Writes and executes SQL queries
# ---
# # Query Writing Skill
# ## Workflow
# 1. Identify the table
# 2. Get the schema
# ...
```

---

#### 7. memory - 记忆文件

```python
memory: list[str] | None = None
```

**用途**：
- 加载 AGENTS.md 文件
- 注入项目上下文到 system prompt

**示例**：

```python
agent = create_deep_agent(
    memory=["/AGENTS.md", "/project-context.md"]
)

# AGENTS.md 格式：
# # Project Context
# - Project: Deep Agents
# - Language: Python
# - Tools: uv, pytest
```

---

#### 8. permissions - 权限规则

```python
permissions: list[FilesystemPermission] | None = None
```

**用途**：
- 配置文件系统权限规则
- `_PermissionMiddleware` 必须在栈最后

**示例**：

```python
from deepagents.middleware.permissions import FilesystemPermission

permissions = [
    FilesystemPermission(
        operations=["read"],
        paths=["/workspace/**"],
    ),
    FilesystemPermission(
        operations=["write"],
        paths=["/workspace/output/**"],
    ),
    FilesystemPermission(
        operations=["write"],
        paths=["/workspace/secrets/**"],
        mode="deny",
    ),
]

agent = create_deep_agent(permissions=permissions)
```

---

#### 9. backend - 后端配置

```python
backend: BackendProtocol | BackendFactory | None = None
```

**用途**：
- 配置文件存储和执行环境
- 默认：`StateBackend()`（内存）

**示例**：

```python
from deepagents.backends import FilesystemBackend, StateBackend

# 内存存储（默认）
backend = StateBackend()
agent = create_deep_agent(backend=backend)

# 本地文件系统
backend = FilesystemBackend(root_dir="./workspace", virtual_mode=True)
agent = create_deep_agent(backend=backend)

# 远程沙箱
from langchain_daytona import DaytonaSandbox
backend = DaytonaSandbox(...)
agent = create_deep_agent(backend=backend)
```

---

#### 10. interrupt_on - 人机交互配置

```python
interrupt_on: dict[str, bool | InterruptOnConfig] | None = None
```

**用途**：
- 配置工具调用中断
- 启用 Human-in-the-Loop（HITL）

**示例**：

```python
# 简单配置：中断所有 edit_file
interrupt_on = {"edit_file": True}

# 复杂配置：自定义中断逻辑
from langchain.agents.middleware import InterruptOnConfig

interrupt_on = {
    "edit_file": InterruptOnConfig(
        before=True,  # 执行前中断
        after=False,  # 执行后不中断
    ),
    "write_file": True,
}

agent = create_deep_agent(
    interrupt_on=interrupt_on,
    checkpointer=InMemorySaver(),  # HITL 需要 checkpointer
)
```

---

#### 11. 其他参数

```python
response_format: ResponseFormat | None     # 结构化输出
context_schema: type[ContextT] | None      # 上下文 schema
checkpointer: Checkpointer | None          # 状态持久化
store: BaseStore | None                    # 持久化存储
debug: bool = False                        # 调试模式
name: str | None                           # Agent 名称
cache: BaseCache | None                    # 缓存配置
```

---

### 返回值

```python
-> CompiledStateGraph[AgentState[ResponseT], ContextT, _InputAgentState, _OutputAgentState[ResponseT]]
```

**类型**：`CompiledStateGraph`

**用途**：
- LangGraph 编译后的状态图
- 可以调用 `.invoke()` 或 `.stream()` 执行

---

## 六、Middleware 构建流程

### 三种 Middleware 栈

`create_deep_agent` 构建三种 middleware 栈：

1. **GP SubAgent middleware**（通用子 Agent）
2. **User SubAgent middleware**（用户定义子 Agent）
3. **Main Agent middleware**（主 Agent）

---

### 1. GP SubAgent Middleware（通用子 Agent）

**位置**：line 443-466

```python
gp_middleware: list[AgentMiddleware] = [
    TodoListMiddleware(),                      # 1. Todo 列表
    FilesystemMiddleware(backend=backend),     # 2. 文件操作
    create_summarization_middleware(model),    # 3. 自动摘要
    PatchToolCallsMiddleware(),                # 4. 工具调用修复
]

if skills is not None:
    gp_middleware.append(SkillsMiddleware(...))  # 5. 技能（条件）

gp_middleware.extend(_resolve_extra_middleware(_profile))  # 6. Profile middleware

if _profile.excluded_tools:
    gp_middleware.append(_ToolExclusionMiddleware(...))  # 7. 工具排除（条件）

gp_middleware.append(AnthropicPromptCachingMiddleware(...))  # 8. Prompt 缓存

if permissions:
    gp_middleware.append(_PermissionMiddleware(...))  # 9. 权限（条件，最后）
```

---

### GP SubAgent Middleware 栈顺序

```
请求 → TodoListMiddleware
    ↓
    FilesystemMiddleware
    ↓
    SummarizationMiddleware
    ↓
    PatchToolCallsMiddleware
    ↓
    SkillsMiddleware (条件)
    ↓
    Profile extra_middleware
    ↓
    _ToolExclusionMiddleware (条件)
    ↓
    AnthropicPromptCachingMiddleware
    ↓
    _PermissionMiddleware (条件)
    ↓
    模型调用
```

---

### 2. User SubAgent Middleware（用户子 Agent）

**位置**：line 500-522

```python
subagent_middleware: list[AgentMiddleware] = [
    TodoListMiddleware(),                      # 1. Todo 列表
    FilesystemMiddleware(backend=backend),     # 2. 文件操作
    create_summarization_middleware(subagent_model),  # 3. 自动摘要
    PatchToolCallsMiddleware(),                # 4. 工具调用修复
]

subagent_skills = spec.get("skills")
if subagent_skills:
    subagent_middleware.append(SkillsMiddleware(...))  # 5. 技能（条件）

subagent_middleware.extend(spec.get("middleware", []))  # 6. 用户 middleware

subagent_middleware.extend(_resolve_extra_middleware(_subagent_profile))  # 7. Profile middleware

if _subagent_profile.excluded_tools:
    subagent_middleware.append(_ToolExclusionMiddleware(...))  # 8. 工具排除（条件）

subagent_middleware.append(AnthropicPromptCachingMiddleware(...))  # 9. Prompt 缓存

if subagent_permissions:
    subagent_middleware.append(_PermissionMiddleware(...))  # 10. 权限（条件）
```

---

### User SubAgent Middleware 栈顺序

```
请求 → TodoListMiddleware
    ↓
    FilesystemMiddleware
    ↓
    SummarizationMiddleware
    ↓
    PatchToolCallsMiddleware
    ↓
    SkillsMiddleware (条件)
    ↓
    ★ 用户自定义 middleware ★
    ↓
    Profile extra_middleware
    ↓
    _ToolExclusionMiddleware (条件)
    ↓
    AnthropicPromptCachingMiddleware
    ↓
    _PermissionMiddleware (条件)
    ↓
    模型调用
```

**与 GP SubAgent 的区别**：
- 有用户自定义 middleware（`spec.get("middleware", [])`)
- 没有 SubAgentMiddleware（不能创建子子 Agent）

---

### 3. Main Agent Middleware（主 Agent）

**位置**：line 551-598

```python
deepagent_middleware: list[AgentMiddleware] = [
    TodoListMiddleware(),                      # 1. Todo 列表
]

if skills is not None:
    deepagent_middleware.append(SkillsMiddleware(...))  # 2. 技能（条件）

deepagent_middleware.extend([
    FilesystemMiddleware(backend=backend),     # 3. 文件操作
    SubAgentMiddleware(subagents=inline_subagents),  # 4. 子 Agent 管理
    create_summarization_middleware(model),    # 5. 自动摘要
    PatchToolCallsMiddleware(),                # 6. 工具调用修复
])

if async_subagents:
    deepagent_middleware.append(AsyncSubAgentMiddleware(...))  # 7. 异步子 Agent（条件）

if middleware:
    deepagent_middleware.extend(middleware)  # 8. 用户 middleware

deepagent_middleware.extend(_resolve_extra_middleware(_profile))  # 9. Profile middleware

if _profile.excluded_tools:
    deepagent_middleware.append(_ToolExclusionMiddleware(...))  # 10. 工具排除（条件）

deepagent_middleware.append(AnthropicPromptCachingMiddleware(...))  # 11. Prompt 缓存

if memory is not None:
    deepagent_middleware.append(MemoryMiddleware(...))  # 12. 记忆（条件）

if interrupt_on is not None:
    deepagent_middleware.append(HumanInTheLoopMiddleware(...))  # 13. HITL（条件）

if permissions:
    deepagent_middleware.append(_PermissionMiddleware(...))  # 14. 权限（最后）
```

---

### Main Agent Middleware 栈顺序

```
请求 → TodoListMiddleware
    ↓
    SkillsMiddleware (条件)
    ↓
    FilesystemMiddleware
    ↓
    SubAgentMiddleware  ★ 管理子 Agent ★
    ↓
    SummarizationMiddleware
    ↓
    PatchToolCallsMiddleware
    ↓
    AsyncSubAgentMiddleware (条件)
    ↓
    ★ 用户自定义 middleware ★
    ↓
    Profile extra_middleware
    ↓
    _ToolExclusionMiddleware (条件)
    ↓
    AnthropicPromptCachingMiddleware
    ↓
    MemoryMiddleware (条件)
    ↓
    HumanInTheLoopMiddleware (条件)
    ↓
    _PermissionMiddleware (条件)
    ↓
    模型调用
```

---

### 主 Agent Middleware 的特殊性

| 特性 | GP SubAgent | User SubAgent | Main Agent |
|------|------------|---------------|------------|
| **SubAgentMiddleware** | ❌ 无 | ❌ 无 | ✅ 有（管理子 Agent） |
| **用户 middleware** | ❌ 无 | ✅ 有 | ✅ 有 |
| **MemoryMiddleware** | ❌ 无 | ❌ 无 | ✅ 有（加载记忆） |
| **HITL Middleware** | ❌ 无 | ✅ 继承 | ✅ 有 |

---

## 七、SubAgent 处理流程

### SubAgent 分类

```python
subagents: Sequence[SubAgent | CompiledSubAgent | AsyncSubAgent] | None
```

**三种类型**：

| 类型 | 标识字段 | 处理方式 |
|------|---------|---------|
| `AsyncSubAgent` | `graph_id` | 添加到 `async_subagents` 列表 |
| `CompiledSubAgent` | `runnable` | 添加到 `inline_subagents` 列表 |
| `SubAgent` | 无特殊字段 | 构建 middleware 后添加 |

---

### SubAgent 处理流程图

```
遍历 subagents 参数
    ↓
判断类型
    ↓
┌─────────────────┬─────────────────┬─────────────────┐
│ AsyncSubAgent   │ CompiledSubAgent│ SubAgent        │
│ ("graph_id")    │ ("runnable")    │ (其他)          │
├─────────────────┼─────────────────┼─────────────────┤
│ 添加到          │ 直接添加到      │ 构建 middleware │
│ async_subagents │ inline_subagents│     ↓           │
│                 │                 │ 解析模型        │
│                 │                 │     ↓           │
│                 │                 │ 解析权限        │
│                 │                 │     ↓           │
│                 │                 │ 构建 middleware│
│                 │                 │     ↓           │
│                 │                 │ 解析工具        │
│                 │                 │     ↓           │
│                 │                 │ 添加到          │
│                 │                 │ inline_subagents│
└─────────────────┴─────────────────┴─────────────────┘
    ↓
检查是否需要添加 GP SubAgent
    ↓
构建主 Agent middleware（包含 SubAgentMiddleware）
```

---

### AsyncSubAgent 处理

```python
if "graph_id" in spec:
    # Then spec is an AsyncSubAgent
    async_subagents.append(cast("AsyncSubAgent", spec))
    continue  # 跳过后续处理
```

**AsyncSubAgent 字段**：
- `name`：名称
- `description`：描述
- `graph_id`：LangSmith 图 ID（标识字段）
- `url`：可选，远程 URL
- `headers`：可选，请求头

**用途**：
- 远程异步执行
- 通过 LangSmith Deployment 运行
- 非阻塞执行

---

### CompiledSubAgent 处理

```python
if "runnable" in spec:
    # CompiledSubAgent - use as-is
    inline_subagents.append(spec)
```

**CompiledSubAgent 字段**：
- `name`：名称
- `description`：描述
- `runnable`：预编译的 Runnable（标识字段）

**用途**：
- 用户自定义 Runnable
- 完全控制子 Agent 实现
- 不使用 Deep Agents 默认 middleware

---

### SubAgent 处理（详细）

**步骤 1：解析模型**

```python
raw_subagent_model = spec.get("model", model)  # 默认继承主 Agent 模型
subagent_model = resolve_model(raw_subagent_model)

_subagent_spec = raw_subagent_model if isinstance(raw_subagent_model, str) else None
_subagent_profile = _harness_profile_for_model(subagent_model, _subagent_spec)
```

---

**步骤 2：解析权限**

```python
subagent_permissions = spec.get("permissions", permissions)
# 子 Agent 可以定义自己的权限，否则继承主 Agent 权限
```

---

**步骤 3：构建 middleware**

```python
subagent_middleware: list[AgentMiddleware] = [
    TodoListMiddleware(),
    FilesystemMiddleware(backend=backend),
    create_summarization_middleware(subagent_model, backend),
    PatchToolCallsMiddleware(),
]

subagent_skills = spec.get("skills")
if subagent_skills:
    subagent_middleware.append(SkillsMiddleware(...))

subagent_middleware.extend(spec.get("middleware", []))  # 用户自定义

subagent_middleware.extend(_resolve_extra_middleware(_subagent_profile))

if _subagent_profile.excluded_tools:
    subagent_middleware.append(_ToolExclusionMiddleware(...))

subagent_middleware.append(AnthropicPromptCachingMiddleware(...))

if subagent_permissions:
    subagent_middleware.append(_PermissionMiddleware(...))
```

---

**步骤 4：解析工具**

```python
subagent_interrupt_on = spec.get("interrupt_on", interrupt_on)  # 继承或覆盖

raw_subagent_tools = spec.get("tools") if "tools" in spec else tools  # 继承或自定义
subagent_tools = _apply_tool_description_overrides(raw_subagent_tools, ...)
```

---

**步骤 5：组装 processed_spec**

```python
processed_spec: SubAgent = {
    **spec,
    "model": subagent_model,
    "tools": subagent_tools or [],
    "middleware": subagent_middleware,
}
if subagent_interrupt_on is not None:
    processed_spec["interrupt_on"] = subagent_interrupt_on

inline_subagents.append(processed_spec)
```

---

### GP SubAgent 自动添加

```python
general_purpose_spec: SubAgent = {
    **GENERAL_PURPOSE_SUBAGENT,
    "model": model,
    "tools": _tools or [],
    "middleware": gp_middleware,
}

# 如果用户没有定义 general-purpose 子 Agent，自动添加
if not any(spec["name"] == GENERAL_PURPOSE_SUBAGENT["name"] for spec in inline_subagents):
    inline_subagents.insert(0, general_purpose_spec)
```

**WHY 自动添加 GP SubAgent**：
- 每个 Deep Agent 都有一个通用子 Agent
- 用于处理一般性任务
- 用户可以通过定义同名子 Agent 来覆盖

---

## 八、最终 Agent 创建

### System Prompt 组装

**位置**：line 600-612

```python
# 1. 选择 base prompt
base_prompt = _profile.base_system_prompt if _profile.base_system_prompt is not None else BASE_AGENT_PROMPT

# 2. 添加 Profile suffix（如果有）
if _profile.system_prompt_suffix is not None:
    base_prompt = base_prompt + "\n\n" + _profile.system_prompt_suffix

# 3. 处理用户 system_prompt
if system_prompt is None:
    final_system_prompt: str | SystemMessage = base_prompt
elif isinstance(system_prompt, SystemMessage):
    # SystemMessage: 合并 content_blocks
    final_system_prompt = SystemMessage(
        content_blocks=[*system_prompt.content_blocks, {"type": "text", "text": f"\n\n{base_prompt}"}]
    )
else:
    # String: 拼接
    final_system_prompt = system_prompt + "\n\n" + base_prompt
```

---

### System Prompt 组装流程图

```
base_prompt 选择
    ↓
┌─────────────────┬─────────────────┐
│ Profile 设置    │ Profile 未设置  │
│ base_system_    │ 使用            │
│ prompt          │ BASE_AGENT_     │
│                 │ PROMPT          │
└─────────────────┴─────────────────┘
    ↓
添加 Profile suffix（条件）
    ↓
处理用户 system_prompt
    ↓
┌─────────────────┬─────────────────┬─────────────────┐
│ system_prompt   │ system_prompt   │ system_prompt   │
│ is None         │ is SystemMessage│ is str          │
├─────────────────┼─────────────────┼─────────────────┤
│ 仅 base_prompt  │ 合并 content_   │ system_prompt + │
│                 │ blocks          │ base_prompt     │
└─────────────────┴─────────────────┴─────────────────┘
    ↓
final_system_prompt
```

---

### 调用 create_agent

**位置**：line 614-626

```python
return create_agent(
    model,
    system_prompt=final_system_prompt,
    tools=_tools,
    middleware=deepagent_middleware,
    response_format=response_format,
    context_schema=context_schema,
    checkpointer=checkpointer,
    store=store,
    debug=debug,
    name=name,
    cache=cache,
).with_config(
    {
        "recursion_limit": 9_999,  # 高递归限制
        "metadata": {
            "ls_integration": "deepagents",
            "versions": {"deepagents": __version__},
            "lc_agent_name": name,
        },
    }
)
```

---

### 关键配置

**recursion_limit: 9_999**：
- LangGraph 递归限制
- 高值允许复杂任务链
- 避免提前终止

**metadata**：
- `ls_integration`：LangSmith 集成标识
- `versions`：Deep Agents 版本
- `lc_agent_name`：Agent 名称

---

### 返回类型

```python
CompiledStateGraph[AgentState[ResponseT], ContextT, _InputAgentState, _OutputAgentState[ResponseT]]
```

**用途**：
- `.invoke()`：同步执行
- `.stream()`：流式执行
- `.ainvoke()`：异步执行

---

## 九、参数传递图

### 参数传递流程

```
用户传入参数
    ↓
create_deep_agent()
    ↓
┌─────────────────────────────────────────────────┐
│ 参数处理                                        │
├─────────────────────────────────────────────────┤
│ model → resolve_model() → model 对象            │
│ tools → _apply_tool_description_overrides()     │
│ backend → 默认 StateBackend                     │
│ _profile → _harness_profile_for_model()         │
└─────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────┐
│ Middleware 构建                                 │
├─────────────────────────────────────────────────┤
│ GP SubAgent middleware                          │
│ User SubAgent middleware                        │
│ Main Agent middleware                           │
└─────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────┐
│ SubAgent 处理                                   │
├─────────────────────────────────────────────────┤
│ AsyncSubAgent → async_subagents                 │
│ CompiledSubAgent → inline_subagents             │
│ SubAgent → 构建 middleware → inline_subagents   │
│ 自动添加 GP SubAgent（如果需要）                 │
└─────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────┐
│ System Prompt 组装                              │
├─────────────────────────────────────────────────┤
│ base_prompt = Profile.base_system_prompt        │
│        or BASE_AGENT_PROMPT                     │
│ base_prompt += Profile.system_prompt_suffix     │
│ final = user_system_prompt + base_prompt        │
└─────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────┐
│ 调用 create_agent                               │
├─────────────────────────────────────────────────┤
│ create_agent(                                   │
│     model,                                      │
│     system_prompt,                              │
│     tools,                                      │
│     middleware,                                 │
│     ...,                                        │
│ ).with_config({"recursion_limit": 9999})        │
└─────────────────────────────────────────────────┘
    ↓
返回 CompiledStateGraph
```

---

### 参数分组

| 参数组 | 参数 | 用途 |
|--------|------|------|
| **模型组** | `model` | LLM 模型 |
| **工具组** | `tools` | 自定义工具 |
| **Middleware 组** | `middleware` | 用户 middleware |
| **SubAgent 组** | `subagents` | 子 Agent 配置 |
| **上下文组** | `skills`, `memory` | 技能和记忆 |
| **权限组** | `permissions` | 文件权限 |
| **HITL 组** | `interrupt_on` | 人机交互 |
| **存储组** | `backend`, `store`, `checkpointer` | 存储配置 |
| **输出组** | `response_format` | 结构化输出 |
| **调试组** | `debug`, `name`, `cache` | 调试配置 |

---

## 十、关键设计点总结

| 设计点 | 说明 | WHY |
|--------|------|-----|
| **三种 Middleware 栈** | GP、User、Main | 不同角色需要不同功能 |
| **Middleware 顺序** | 用户 middleware 在中间 | 不影响尾部栈（缓存、权限） |
| **_PermissionMiddleware 最后** | 必须看到所有工具 | 检查所有工具的权限 |
| **Prompt 缓存无条件** | AnthropicPromptCachingMiddleware | "ignore" 对非 Anthropic 无害 |
| **GP SubAgent 自动添加** | 每个 Agent 都有通用子 Agent | 标准化任务分工 |
| **System Prompt 拼接** | user + base + profile suffix | 定制化 + 标准化 |
| **recursion_limit: 9999** | 高递归限制 | 支持复杂任务链 |
| **Profile 查找优先级** | spec → identifier → provider | 精确匹配优先 |
| **工具描述不修改原对象** | model_copy / dict.copy | 保护用户传入对象 |
| **SubAgent 权限继承/覆盖** | subagent.permissions || parent | 灵活权限管理 |

---

### 设计原则

#### 1. 灵活性 + 标准化

- **灵活性**：用户可自定义 middleware、subagents、tools
- **标准化**：默认 middleware栈、GP SubAgent、BASE_AGENT_PROMPT

---

#### 2. 安全性

- `_PermissionMiddleware` 在栈最后
- 工具描述不修改原对象
- SubAgent 权限继承/覆盖机制

---

#### 3. 可扩展性

- Profile 支持额外 middleware
- SubAgent 支持三种类型
- Backend 支持多种实现

---

#### 4. 性能优化

- Anthropic Prompt Caching（无条件，无害）
- 高 recursion_limit（避免提前终止）
- Profile lazy initialization（callable）

---

## 一句话总结

`graph.py` 是 Deep Agents 的核心入口，通过 `create_deep_agent` 函数构建完整的 Agent，包括三种 middleware栈（GP、User、Main）、自动添加 GP SubAgent、System Prompt 组装、最终调用 LangChain 的 `create_agent` 返回可执行的 `CompiledStateGraph`。

**核心价值**：
- 统一入口 → 简化 Agent 创建
- 灵活配置 → middleware、subagents、tools 可定制
- 标准化栈 → 默认功能完整（Todo、Filesystem、SubAgent、Summarization）
- 安全设计 → 权限 middleware 在栈最后

---

**文档生成时间**：2026-04-13
**适用版本**：Deep Agents v0.5.2