# Deep Agents _harness_profiles.py 详细解析

> 本文档深入解析 `libs/deepagents/deepagents/profiles/_harness_profiles.py` 文件
> 
> **阅读时间**：20-25 分钟
> 
> **适用人群**：想要理解 Provider/Model 配置系统和 Profile 合机制的开发者

---

## 目录

1. [文件概述](#一文件概述)
2. [核心概念：Harness Profile](#二核心概念harness-profile)
3. [_HarnessProfile 数据类](#三harnessprofile-数据类)
4. [Profile 注册表](#四profile-注册表)
5. [Profile 查找流程](#五profile-查找流程)
6. [Profile 合机制](#六profile-合机制)
7. [中间件合并策略](#七中间件合并策略)
8. [Provider Profile 示例](#八provider-profile-示例)
9. [完整使用流程](#九完整使用流程)
10. [关键设计点总结](#十关键设计点总结)

---

## 一、文件概述

### 文件定位

```python
"""Harness profile registry for model- and provider-specific configuration.

!!! warning

    This is an internal API subject to change without deprecation. It is not
    intended for external use or consumption.

Defines the `_HarnessProfile` dataclass and the harness profile registry used
by `resolve_model` and `create_deep_agent` to apply provider- and model-specific
configuration.
"""
```

**一句话定义**：`_harness_profiles.py` 定义了 `_HarnessProfile` 数据类和注册表系统，用于存储和合并 Provider/Model 特定配置。

**重要警告**：这是**内部 API**，随时可能更改，不供外部使用。

---

### 文件结构概览

```
_harness_profiles.py (283 lines)
├── 导入依赖 (line 13-21)
├── _HarnessProfile 数据类 (line 28-111)
│   ├── init_kwargs (line 41-43)
│   ├── pre_init (line 45-53)
│   ├── init_kwargs_factory (line 55-61)
│   ├── base_system_prompt (line 63-69)
│   ├── system_prompt_suffix (line 71-76)
│   ├── tool_description_overrides (line 78-90)
│   ├── excluded_tools (line 92-101)
│   └── extra_middleware (line 103-110)
├── Profile 注册表 (line 117-123)
│   └── _HARNESS_PROFILES (dict)
├── 注册函数 (line 126-135)
│   └── _register_harness_profile()
├── 查找函数 (line 138-171)
│   └── _get_harness_profile()
├── 中间件解析 (line 174-180)
│   └── _resolve_middleware_seq()
├── 中间件合并 (line 183-224)
│   └── _merge_middleware()
└── Profile 合 (line 227-283)
    └── _merge_profiles()
```

---

### 模块关系图

```
profiles/__init__.py
    ├── 导入 _openai.py     → 注册 openai profile
    ├── 导入 _openrouter.py → 注册 openrouter profile
    └── 导入 _harness_profiles.py
        ↓
_resolve_model() / create_deep_agent()
    ↓
_get_harness_profile(spec)
    ↓
返回 _HarnessProfile
    ↓
应用于：
    - init_chat_model (init_kwargs, init_kwargs_factory)
    - System Prompt (base_system_prompt, system_prompt_suffix)
    - Tool Set (excluded_tools, tool_description_overrides)
    - Middleware (extra_middleware)
```

---

## 二、核心概念：Harness Profile

### 什么是 Harness Profile？

**Harness Profile** = Provider/Model 特定的配置声明

**解决的问题**：
- 不同 Provider 有不同的 API 特性
- 不同 Model 有不同的能力限制
- 需要统一的配置机制

**类比**：
- Harness Profile ≈ "配置模板"
- Harness Profile ≈ "提供商适配器"
- Harness Profile ≈ "模型能力描述"

---

### 为什么需要 Profile？

#### 问题场景

```
OpenAI:
    - 支持 Responses API
    - 模型：gpt-4, gpt-5, o3-pro

Anthropic:
    - 支持 Prompt Caching
    - 模型：claude-4-sonnet, claude-4-opus

OpenRouter:
    - 需要 App Attribution Headers
    - 版本检查：>= 0.2.0

某些模型：
    - 不支持某些工具（如 o3-pro 不支持 bash）
    - 需要特殊的 System Prompt
```

#### Profile 的解决方案

```
Provider Profile:
    - 定义 Provider 级默认配置
    - 所有该 Provider 的 Model 继承

Model Profile:
    - 定义特定 Model 的配置
    - 覆盖 Provider 默认值

合机制:
    - Provider + Model = 最终配置
    - 避免重复定义
```

---

### Profile 的两层结构

```
┌─────────────────────────────────────┐
│ Provider Profile (openai)           │
├─────────────────────────────────────┤
│ init_kwargs:                        │
│   use_responses_api: True           │
│                                     │
│ extra_middleware:                   │
│   - [PromptCachingMiddleware]       │
└─────────────────────────────────────┘
    ↓ 继承
┌─────────────────────────────────────┐
│ Model Profile (openai:o3-pro)       │
├─────────────────────────────────────┤
│ excluded_tools:                     │
│   frozenset({"bash", "edit_file"})  │
│                                     │
│ system_prompt_suffix:               │
│   "Note: File ops disabled"         │
└─────────────────────────────────────┘
    ↓ 合
┌─────────────────────────────────────┐
│ 最终 Profile (openai:o3-pro)        │
├─────────────────────────────────────┤
│ init_kwargs:                        │
│   use_responses_api: True  ← 继承   │
│                                     │
│ excluded_tools:                     │
│   frozenset({"bash", "edit_file"})  │
│                                     │
│ system_prompt_suffix:               │
│   "Note: File ops disabled"         │
│                                     │
│ extra_middleware:                   │
│   - [PromptCachingMiddleware] ← 继承│
└─────────────────────────────────────┘
```

---

### Profile 消费者

| 消费者 | 使用字段 | 用途 |
|--------|----------|------|
| `resolve_model()` | `init_kwargs`, `pre_init`, `init_kwargs_factory` | 模型初始化参数 |
| `create_deep_agent()` | `base_system_prompt`, `system_prompt_suffix`, `excluded_tools`, `tool_description_overrides`, `extra_middleware` | Agent 配置 |

---

## 三、_HarnessProfile 数据类

### 类定义

```python
@dataclass(frozen=True)
class _HarnessProfile:
    """Declarative configuration for the Deep Agent harness.

    Applied based on the selected model or provider. Each field is optional —
    its default means "no change from baseline behavior". Profiles are looked
    up by `_get_harness_profile` (exact model spec first, then provider prefix)
    and consumed by `resolve_model` (for `init_kwargs` / `pre_init`) and
    `create_deep_agent` (for everything else).

    Register profiles via `_register_harness_profile`.
    """
```

**关键特性**：
- `@dataclass` — 自动生成 `__init__`, `__repr__`
- `frozen=True` — **不可变对象**（immutable）
- 所有字段可选 — 默认值表示"无变化"

---

### 字段总览

| 字段 | 类型 | 默认值 | 消费者 |
|------|------|--------|--------|
| `init_kwargs` | `dict[str, Any]` | `{}` | `resolve_model` |
| `pre_init` | `Callable[[str], None] \| None` | `None` | `resolve_model` |
| `init_kwargs_factory` | `Callable[[], dict] \| None` | `None` | `resolve_model` |
| `base_system_prompt` | `str \| None` | `None` | `create_deep_agent` |
| `system_prompt_suffix` | `str \| None` | `None` | `create_deep_agent` |
| `tool_description_overrides` | `dict[str, str]` | `{}` | `create_deep_agent` |
| `excluded_tools` | `frozenset[str]` | `frozenset()` | `create_deep_agent` |
| `extra_middleware` | `Sequence \| Callable` | `()` | `create_deep_agent` |

---

### 字段详解

#### 1. init_kwargs — 模型初始化参数

```python
init_kwargs: dict[str, Any] = field(default_factory=dict)
"""Extra keyword arguments forwarded to `init_chat_model` when resolving
a string model spec (e.g. `{"use_responses_api": True}` for OpenAI)."""
```

**用途**：传递给 `langchain.chat_models.init_chat_model()` 的额外参数

**示例**：
```python
# OpenAI Profile
_register_harness_profile(
    "openai",
    _HarnessProfile(init_kwargs={"use_responses_api": True}),
)

# 效果：init_chat_model("openai:gpt-4", use_responses_api=True)
```

---

#### 2. pre_init — 初始化前检查

```python
pre_init: Callable[[str], None] | None = None
"""Optional callable invoked with the raw model spec string *before*
`init_chat_model` runs.

Use for version checks or other preconditions
(e.g. `check_openrouter_version`).

Must raise on failure.
"""
```

**用途**：在模型初始化前执行的检查函数

**特点**：
- 接收 model spec 字符串作为参数
- 失败时必须抛出异常
- 用于版本检查、前置条件验证

**示例**：
```python
# OpenRouter Profile
def check_openrouter_version() -> None:
    installed = pkg_version("langchain-openrouter")
    if Version(installed) < Version("0.2.0"):
        raise ImportError("deepagents requires langchain-openrouter>=0.2.0")

_register_harness_profile(
    "openrouter",
    _HarnessProfile(
        pre_init=lambda _spec: check_openrouter_version(),
    ),
)

# 执行流程：
# 1. _get_harness_profile("openrouter:model")
# 2. profile.pre_init("openrouter:model")
# 3. check_openrouter_version() 执行
# 4. 如果版本过低，抛出 ImportError
```

---

#### 3. init_kwargs_factory — 动态参数工厂

```python
init_kwargs_factory: Callable[[], dict[str, Any]] | None = None
"""Optional factory called at init time to produce dynamic kwargs that
are merged *on top of* `init_kwargs`.

Use when values depend on runtime state like environment variables
(e.g. OpenRouter attribution headers that defer to env var overrides).
"""
```

**用途**：运行时动态生成 `init_kwargs`

**为什么需要**：
- 静态 `init_kwargs` 在模块加载时就固定了
- 环境变量、运行时状态需要延迟读取

**合并顺序**：
```
静态 init_kwargs
    ↓
动态 init_kwargs_factory() 结果
    ↓
最终 kwargs（factory 覆盖 static）
```

**示例**：
```python
# OpenRouter Attribution
def _openrouter_attribution_kwargs() -> dict[str, Any]:
    kwargs: dict[str, Any] = {}
    if not os.environ.get("OPENROUTER_APP_URL"):
        kwargs["app_url"] = "https://github.com/langchain-ai/deepagents"
    if not os.environ.get("OPENROUTER_APP_TITLE"):
        kwargs["app_title"] = "Deep Agents"
    return kwargs

_register_harness_profile(
    "openrouter",
    _HarnessProfile(
        init_kwargs_factory=_openrouter_attribution_kwargs,
    ),
)

# 执行流程：
# resolve_model("openrouter:model")
#     ↓
# kwargs = {**profile.init_kwargs}  # 静态部分
# kwargs.update(profile.init_kwargs_factory())  # 动态部分
# init_chat_model("openrouter:model", **kwargs)
```

---

#### 4. base_system_prompt — 完整替换系统提示

```python
base_system_prompt: str | None = None
"""When set, completely replaces `BASE_AGENT_PROMPT` as the base system
prompt.  `None` (default) means use `BASE_AGENT_PROMPT` unchanged.

If both `base_system_prompt` and `system_prompt_suffix` are set, the
suffix is appended to this custom base.
"""
```

**用途**：完全替换默认的 `BASE_AGENT_PROMPT`

**场景**：
- 特定 Provider 需要不同的 Agent 行为
- 模型有特殊能力/限制

**示例**：
```python
_register_harness_profile(
    "custom-provider",
    _HarnessProfile(
        base_system_prompt="You are a specialized agent...",
    ),
)

# 效果：Agent 使用完全不同的 System Prompt
# BASE_AGENT_PROMPT 被忽略
```

---

#### 5. system_prompt_suffix — 系统提示后缀

```python
system_prompt_suffix: str | None = None
"""Text appended to the base system prompt (either `BASE_AGENT_PROMPT`
or the profile's `base_system_prompt` when set).

`None` means no suffix.
"""
```

**用途**：在基础 System Prompt 后添加额外指令

**优先级**：
```
用户提供的 system_prompt（最高）
    ↓
base_system_prompt 或 BASE_AGENT_PROMPT
    ↓
system_prompt_suffix（最低，追加）
```

**示例**：
```python
_register_harness_profile(
    "openai:o3-pro",
    _HarnessProfile(
        system_prompt_suffix="Note: File editing tools are disabled for this model.",
    ),
)

# 效果：
# System Prompt = BASE_AGENT_PROMPT + "\n\n" + suffix
```

---

#### 6. tool_description_overrides — 工具描述替换

```python
tool_description_overrides: dict[str, str] = field(default_factory=dict)
"""Per-tool description replacements, keyed by tool name.

Applied only where Deep Agents has a stable description hook: built-in
filesystem tools, the `task` tool, and user-supplied `BaseTool` / dict
tools. Plain callable tools are left unchanged.

!!! warning

    Keys are matched by tool name string. If a built-in tool is renamed
    or removed, stale keys silently become no-ops with no error. Keep
    overrides minimal and verify against the current tool names.
"""
```

**用途**：替换特定工具的描述文本

**场景**：
- 模型对工具理解有偏差
- Provider 需要优化工具描述

**警告**：
- 工具重命名后，旧的 key 会静默失败
- 需要验证工具名称

**示例**：
```python
_register_harness_profile(
    "anthropic",
    _HarnessProfile(
        tool_description_overrides={
            "bash": "Execute shell commands with proper sandboxing...",
        },
    ),
)

# 效果：bash 工具的描述被替换
```

---

#### 7. excluded_tools — 排除工具列表

```python
excluded_tools: frozenset[str] = frozenset()
"""Tool names to remove from the tool set for this provider/model.

Filtered via `_ToolExclusionMiddleware`, which strips both user-supplied
and middleware-injected tools from `request.tools` before the model
sees them.

Merged via union when profiles are combined, so provider-level exclusions
and model-level exclusions accumulate.
"""
```

**用途**：禁用特定工具

**特点**：
- `frozenset` — 不可变集合
- 合时使用**集合合并**（union）
- Provider 级 + Model 级排除会累加

**过滤机制**：
```
ToolExclusionMiddleware
    ↓
检查 request.tools
    ↓
移除 excluded_tools 中的工具
    ↓
模型看不到被排除的工具
```

**示例**：
```python
# Provider 级排除
_register_harness_profile(
    "openai",
    _HarnessProfile(
        excluded_tools=frozenset({"deprecated_tool"}),
    ),
)

# Model 级排除
_register_harness_profile(
    "openai:o3-pro",
    _HarnessProfile(
        excluded_tools=frozenset({"bash", "edit_file"}),
    ),
)

# 最终：excluded_tools = frozenset({"deprecated_tool", "bash", "edit_file"})
# 合！累加，不是覆盖
```

---

#### 8. extra_middleware — 额外中间件

```python
extra_middleware: Sequence[AgentMiddleware] | Callable[[], Sequence[AgentMiddleware]] = ()
"""Provider-specific middleware appended to every middleware stack (main
agent, general-purpose subagent, and per-subagent).

May be a static sequence or a zero-arg factory that returns one (use a
factory when the middleware instances should not be shared/reused across
stacks).
"""
```

**用途**：添加 Provider 特定的中间件

**两种形式**：
- 静态序列：`[MiddlewareA(), MiddlewareB()]`
- 工厂函数：`() -> Sequence[AgentMiddleware]`

**为什么需要工厂**：
- 中间件实例不应跨 Agent 共享
- 每个 Agent 需要独立实例

**示例**：
```python
# Anthropic Prompt Caching
_register_harness_profile(
    "anthropic",
    _HarnessProfile(
        extra_middleware=[AnthropicPromptCachingMiddleware()],
    ),
)

# 效果：所有 Anthropic Agent 都添加 Prompt Caching
```

---

## 四、Profile 注册表

### 注册表定义

```python
_HARNESS_PROFILES: dict[str, _HarnessProfile] = {}
"""Registry mapping profile keys to `_HarnessProfile` instances.

Keys are either a full `provider:model` spec (for per-model overrides) or a
bare provider name (for provider-wide defaults).  Lookup order:
exact spec → provider prefix → empty default.
"""
```

**特点**：
- 全局字典
- 模块加载时填充
- Provider 模块导入时自动注册

---

### Key 格式

| Key 格式 | 示例 | 含义 |
|----------|------|------|
| Provider 级 | `"openai"` | Provider 默认配置 |
| Model 级 | `"openai:o3-pro"` | 特定 Model 配置 |

---

### 注册时机

```
profiles/__init__.py
    ↓
导入 _openai.py
    ↓
_register_harness_profile("openai", ...) 执行
    ↓
导入 _openrouter.py
    ↓
_register_harness_profile("openrouter", ...) 执行
    ↓
_HARNESS_PROFILES 填充完成
```

---

### 注册函数

```python
def _register_harness_profile(key: str, profile: _HarnessProfile) -> None:
    """Register a `_HarnessProfile` for a provider or specific model.

    Args:
        key: A provider name (e.g. `"openai"`) for provider-wide defaults,
            or a full `provider:model` spec (e.g. `"openai:o3-pro"`) for a
            per-model override.
        profile: The profile to register.
    """
    _HARNESS_PROFILES[key] = profile
```

**简单直接**：
- 接收 key 和 profile
- 存入全局字典
- 无验证（信任调用者）

---

### 注册示例

```python
# _openai.py
from deepagents.profiles._harness_profiles import _HarnessProfile, _register_harness_profile

_register_harness_profile(
    "openai",
    _HarnessProfile(init_kwargs={"use_responses_api": True}),
)

# _openrouter.py
_register_harness_profile(
    "openrouter",
    _HarnessProfile(
        pre_init=lambda _spec: check_openrouter_version(),
        init_kwargs_factory=_openrouter_attribution_kwargs,
    ),
)

# Model 级注册（假设）
_register_harness_profile(
    "openai:o3-pro",
    _HarnessProfile(
        excluded_tools=frozenset({"bash"}),
        system_prompt_suffix="o3-pro specific instructions",
    ),
)
```

---

## 五、Profile 查找流程

### 查找函数

```python
def _get_harness_profile(spec: str) -> _HarnessProfile:
    """Look up the `_HarnessProfile` for a model spec.

    Resolution order:

    1. Exact match on `spec` (supports per-model overrides).
    2. Provider prefix (everything before the first `:`; for bare names
        without a colon, the full string is used).
    3. A default empty `_HarnessProfile`.

    When both an exact-model profile and a provider-level profile exist, they
    are merged: the provider profile serves as the base and the exact-model
    profile is layered on top. This ensures per-model tweaks inherit provider
    defaults (e.g. `use_responses_api` for OpenAI, prompt-caching middleware
    for Anthropic) instead of silently dropping them.

    Args:
        spec: Model spec in `provider:model` format, or a bare model name.

    Returns:
        The matching `_HarnessProfile`, or an empty default.
    """
```

---

### 查找顺序

```
输入 spec = "openai:o3-pro"
    ↓
1. 精确匹配 _HARNESS_PROFILES["openai:o3-pro"]
    ↓ 存在？
    ↓
    Yes → 获取 exact profile
    ↓
    No → 继续
    ↓
2. Provider 匹配 _HARNESS_PROFILES["openai"]
    ↓ 存在？
    ↓
    Yes → 获取 base profile
    ↓
    检查是否有 exact？
    ↓
    有 exact + 有 base → _merge_profiles(base, exact)
    ↓
    只有 base → 返回 base
    ↓
    No → 继续
    ↓
3. 返回空默认 _HarnessProfile()
```

---

### 代码实现

```python
def _get_harness_profile(spec: str) -> _HarnessProfile:
    # Step 1: 精确匹配
    exact = _HARNESS_PROFILES.get(spec)

    # Step 2: Provider 提取
    provider, sep, _ = spec.partition(":")
    base = _HARNESS_PROFILES.get(provider) if sep else None

    # Step 3: 决策逻辑
    if exact is not None and base is not None:
        return _merge_profiles(base, exact)  # 合
    if exact is not None:
        return exact  # 只有精确
    if base is not None:
        return base  # 只有 Provider
    return _HarnessProfile()  # 空默认
```

---

### spec.partition(":") 解析

```python
# spec = "openai:o3-pro"
provider, sep, _ = spec.partition(":")
# provider = "openai"
# sep = ":"
# _ = "o3-pro"

# spec = "gpt-4"（无 Provider）
provider, sep, _ = spec.partition(":")
# provider = "gpt-4"
# sep = ""
# _ = ""

# sep 存在 → 使用 provider 匹配
# sep 不存在 → 不使用 Provider 匹配
```

---

### 查找示例

#### 示例 1：只有 Provider Profile

```python
# 注册
_register_harness_profile("openai", _HarnessProfile(init_kwargs={"use_responses_api": True}))

# 查找
profile = _get_harness_profile("openai:gpt-4")

# 流程：
# 1. exact = _HARNESS_PROFILES.get("openai:gpt-4") → None
# 2. provider = "openai", sep = ":"
#    base = _HARNESS_PROFILES.get("openai") → Profile
# 3. exact is None, base is not None → return base

# 结果：返回 openai Provider Profile
```

---

#### 示例 2：Provider + Model Profile

```python
# 注册
_register_harness_profile("openai", _HarnessProfile(init_kwargs={"use_responses_api": True}))
_register_harness_profile("openai:o3-pro", _HarnessProfile(excluded_tools=frozenset({"bash"})))

# 查找
profile = _get_harness_profile("openai:o3-pro")

# 流程：
# 1. exact = _HARNESS_PROFILES.get("openai:o3-pro") → Profile
# 2. provider = "openai", sep = ":"
#    base = _HARNESS_PROFILES.get("openai") → Profile
# 3. exact is not None, base is not None → _merge_profiles(base, exact)

# 结果：
# init_kwargs: {"use_responses_api": True}  ← 继承
# excluded_tools: frozenset({"bash"})        ← Model 特有
```

---

#### 示例 3：无任何 Profile

```python
# 无注册
# 查找
profile = _get_harness_profile("unknown:model")

# 流程：
# 1. exact = _HARNESS_PROFILES.get("unknown:model") → None
# 2. provider = "unknown", sep = ":"
#    base = _HARNESS_PROFILES.get("unknown") → None
# 3. all None → return _HarnessProfile()

# 结果：空 Profile（所有字段默认）
```

---

## 六、Profile 合机制

### 合函数

```python
def _merge_profiles(base: _HarnessProfile, override: _HarnessProfile) -> _HarnessProfile:
    """Merge two profiles, layering `override` on top of `base`.

    Dict fields are merged (override wins per-key). Callables (`pre_init`,
    `init_kwargs_factory`) are chained. Middleware sequences are merged by
    type: if the override supplies a middleware whose type already exists in
    the base, it replaces the base entry (preserving position); novel override
    entries are appended. Scalar fields (e.g. prompts) use the override
    value when set, otherwise fall back to the base.

    Args:
        base: Provider-level profile (lower priority).
        override: Exact-model profile (higher priority).

    Returns:
        A new merged `_HarnessProfile`.
    """
```

---

### 字段合策略总览

| 字段类型 | 合策略 | 说明 |
|----------|--------|------|
| `dict` (`init_kwargs`, `tool_description_overrides`) | 合并 + 覆盖 | `{**base, **override}` |
| `frozenset` (`excluded_tools`) | 集合合并 | `base \| override`（累加） |
| `Callable` (`pre_init`, `init_kwargs_factory`) | 串联调用 | 先 base，后 override |
| `str \| None` (`base_system_prompt`, `system_prompt_suffix`) | Override 优先 | override 有值则用 override |
| `Sequence \| Callable` (`extra_middleware`) | 按类型合并 | 相同类型替换，新类型追加 |

---

### dict 字段合

```python
# init_kwargs 合
init_kwargs={**base.init_kwargs, **override.init_kwargs}

# tool_description_overrides 合
tool_description_overrides={
    **base.tool_description_overrides,
    **override.tool_description_overrides,
}

# 示例：
# base.init_kwargs = {"use_responses_api": True, "timeout": 30}
# override.init_kwargs = {"timeout": 60, "max_tokens": 1000}
# 合结果 = {"use_responses_api": True, "timeout": 60, "max_tokens": 1000}
#          ↑ 继承           ↑ override 覆盖    ↑ override 新增
```

---

### frozenset 字段合并（累加）

```python
# excluded_tools 合（集合合并）
excluded_tools=base.excluded_tools | override.excluded_tools

# 示例：
# base.excluded_tools = frozenset({"deprecated_tool"})
# override.excluded_tools = frozenset({"bash", "edit_file"})
# 合结果 = frozenset({"deprecated_tool", "bash", "edit_file"})
#          ↑ 累加，不是覆盖！
```

**WHY 累加**：
- Provider 级排除可能是安全/合规要求
- Model 级排除是能力限制
- 两者都应该生效

---

### Callable 字段串联

#### pre_init 串联

```python
# Chain pre_init callables
if base.pre_init is not None and override.pre_init is not None:
    base_pre = base.pre_init
    over_pre = override.pre_init

    def chained_pre_init(spec: str) -> None:
        base_pre(spec)
        over_pre(spec)

    pre_init: Callable[[str], None] | None = chained_pre_init
else:
    pre_init = override.pre_init or base.pre_init

# 示例：
# base.pre_init = check_provider_version
# override.pre_init = check_model_requirements
# 合结果：先 check_provider_version，后 check_model_requirements
```

---

#### init_kwargs_factory 串联

```python
# Chain init_kwargs_factory callables
if base.init_kwargs_factory is not None and override.init_kwargs_factory is not None:
    base_fac = base.init_kwargs_factory
    over_fac = override.init_kwargs_factory

    def chained_factory() -> dict[str, Any]:
        result = {**base_fac()}
        result.update(over_fac())
        return result

    init_kwargs_factory: Callable[[], dict[str, Any]] | None = chained_factory
else:
    init_kwargs_factory = override.init_kwargs_factory or base.init_kwargs_factory

# 示例：
# base.init_kwargs_factory = lambda: {"app_url": "...", "app_title": "..."}
# override.init_kwargs_factory = lambda: {"custom_header": "..."}
# 合结果：{"app_url": "...", "app_title": "...", "custom_header": "..."}
```

---

### str 字段合并（Override 优先）

```python
# base_system_prompt 合
base_system_prompt=(
    override.base_system_prompt if override.base_system_prompt is not None 
    else base.base_system_prompt
)

# system_prompt_suffix 合
system_prompt_suffix=(
    override.system_prompt_suffix if override.system_prompt_suffix is not None 
    else base.system_prompt_suffix
)

# 示例：
# base.base_system_prompt = None
# override.base_system_prompt = "Special prompt"
# 合结果 = "Special prompt"

# base.system_prompt_suffix = "Provider suffix"
# override.system_prompt_suffix = None
# 合结果 = "Provider suffix"  ← 继承
```

---

### 合结果构建

```python
return _HarnessProfile(
    init_kwargs={**base.init_kwargs, **override.init_kwargs},
    pre_init=pre_init,
    init_kwargs_factory=init_kwargs_factory,
    base_system_prompt=(
        override.base_system_prompt if override.base_system_prompt is not None 
        else base.base_system_prompt
    ),
    system_prompt_suffix=(
        override.system_prompt_suffix if override.system_prompt_suffix is not None 
        else base.system_prompt_suffix
    ),
    tool_description_overrides={
        **base.tool_description_overrides,
        **override.tool_description_overrides,
    },
    excluded_tools=base.excluded_tools | override.excluded_tools,
    extra_middleware=_merge_middleware(base.extra_middleware, override.extra_middleware),
)
```

---

## 七、中间件合并策略

### 问题背景

**场景**：
```python
# Provider Profile
_register_harness_profile(
    "provider",
    _HarnessProfile(
        extra_middleware=[CachingMiddleware(ttl=60)],
    ),
)

# Model Profile
_register_harness_profile(
    "provider:model",
    _HarnessProfile(
        extra_middleware=[CachingMiddleware(ttl=0)],  # 相同类型，不同参数
    ),
)

# 问题：
# 如果简单合并 → 两个 CachingMiddleware
# 应该 → 只保留一个（override 的）
```

---

### 合策略

**按类型合并**：
- 相同类型 → override 替换 base（保留位置）
- 新类型 → 追加到末尾

```
Base: [A, B, C]
Override: [B', D]

合结果: [A, B', C, D]
        ↑ B' 替换 B    ↑ D 新增
```

---

### 代码实现

```python
def _merge_middleware(
    base_mw: Sequence[AgentMiddleware] | Callable[[], Sequence[AgentMiddleware]],
    over_mw: Sequence[AgentMiddleware] | Callable[[], Sequence[AgentMiddleware]],
) -> Sequence[AgentMiddleware] | Callable[[], Sequence[AgentMiddleware]]:
    """Merge two middleware sequences by type.

    If the override supplies a middleware whose type already exists in the base,
    the override instance replaces it in-place (preserving position). Novel
    override entries are appended.

    Example: a provider profile registers `CachingMiddleware(ttl=60)` and a
    model-specific profile registers `CachingMiddleware(ttl=0)`. The merged
    result contains a single `CachingMiddleware(ttl=0)` — the model-specific
    instance replaces the provider-level one rather than duplicating it.
    """
    if not base_mw or not over_mw:
        return over_mw or base_mw

    def factory() -> Sequence[AgentMiddleware]:
        base_seq = _resolve_middleware_seq(base_mw)
        over_seq = _resolve_middleware_seq(over_mw)
        over_by_type: dict[type, AgentMiddleware] = {type(m): m for m in over_seq}
        merged: list[AgentMiddleware] = []
        seen: set[type] = set()
        
        # 遍历 base，替换相同类型
        for m in base_seq:
            mtype = type(m)
            if mtype in over_by_type:
                merged.append(over_by_type[mtype])  # 替换
                seen.add(mtype)
            else:
                merged.append(m)  # 保留
        
        # 追加新类型
        merged.extend(m for m in over_seq if type(m) not in seen)
        return merged

    return factory
```

---

### 合流程图

```
Base: [A, B, CachingMiddleware(ttl=60), D]
Override: [CachingMiddleware(ttl=0), E]
    ↓
_resolve_middleware_seq(base_mw)
    ↓
base_seq = [A, B, CachingMiddleware(60), D]
    ↓
_resolve_middleware_seq(over_mw)
    ↓
over_seq = [CachingMiddleware(0), E]
    ↓
over_by_type = {CachingMiddleware: CachingMiddleware(0)}
    ↓
遍历 base_seq:
    A → type(A) not in over_by_type → append A
    B → type(B) not in over_by_type → append B
    CachingMiddleware(60) → type in over_by_type → append CachingMiddleware(0)
        seen.add(CachingMiddleware)
    D → type(D) not in over_by_type → append D
    ↓
追加新类型:
    E → type(E) not in seen → append E
    CachingMiddleware(0) → type in seen → 跳过
    ↓
结果: [A, B, CachingMiddleware(0), D, E]
```

---

### 中间件解析函数

```python
def _resolve_middleware_seq(
    middleware: Sequence[AgentMiddleware] | Callable[[], Sequence[AgentMiddleware]],
) -> Sequence[AgentMiddleware]:
    """Resolve middleware to a concrete sequence, calling factory if needed."""
    if callable(middleware):
        return middleware()
    return middleware
```

**用途**：
- 支持静态序列和工厂函数两种形式
- 统一转换为具体序列

---

### 合示例

#### 示例 1：相同类型替换

```python
# Provider
base_mw = [LoggingMiddleware(), CachingMiddleware(ttl=60)]

# Model
over_mw = [CachingMiddleware(ttl=0)]

# 合结果
merged = [LoggingMiddleware(), CachingMiddleware(ttl=0)]
#         ↑ 保留                ↑ 替换（ttl=0）
```

---

#### 示例 2：新类型追加

```python
# Provider
base_mw = [LoggingMiddleware()]

# Model
over_mw = [MetricsMiddleware()]

# 合结果
merged = [LoggingMiddleware(), MetricsMiddleware()]
#         ↑ 保留                ↑ 追加
```

---

#### 示例 3：混合情况

```python
# Provider
base_mw = [A, B, C]

# Model
over_mw = [B', D]

# 合结果
merged = [A, B', C, D]
#         ↑ 保留 ↑ 替换 ↑ 保留 ↑ 追加
```

---

## 八、Provider Profile 示例

### OpenAI Profile

```python
# _openai.py
"""OpenAI provider harness profile."""

from deepagents.profiles._harness_profiles import _HarnessProfile, _register_harness_profile

_register_harness_profile(
    "openai",
    _HarnessProfile(init_kwargs={"use_responses_api": True}),
)
```

**作用**：
- 所有 OpenAI 模型默认使用 Responses API
- `init_chat_model("openai:gpt-4")` → `init_chat_model("openai:gpt-4", use_responses_api=True)`

---

### OpenRouter Profile

```python
# _openrouter.py
"""OpenRouter provider helpers."""

import os
from importlib.metadata import PackageNotFoundError, version as pkg_version
from packaging.version import Version

from deepagents.profiles._harness_profiles import _HarnessProfile, _register_harness_profile

OPENROUTER_MIN_VERSION = "0.2.0"

_OPENROUTER_APP_URL = "https://github.com/langchain-ai/deepagents"
_OPENROUTER_APP_TITLE = "Deep Agents"


def _openrouter_attribution_kwargs() -> dict[str, Any]:
    """Build OpenRouter attribution kwargs, deferring to env var overrides."""
    kwargs: dict[str, Any] = {}
    if not os.environ.get("OPENROUTER_APP_URL"):
        kwargs["app_url"] = _OPENROUTER_APP_URL
    if not os.environ.get("OPENROUTER_APP_TITLE"):
        kwargs["app_title"] = _OPENROUTER_APP_TITLE
    return kwargs


def check_openrouter_version() -> None:
    """Raise if the installed `langchain-openrouter` is below the minimum."""
    try:
        installed = pkg_version("langchain-openrouter")
    except PackageNotFoundError:
        return
    if Version(installed) < Version(OPENROUTER_MIN_VERSION):
        raise ImportError(
            f"deepagents requires langchain-openrouter>={OPENROUTER_MIN_VERSION}, "
            f"but {installed} is installed."
        )


_register_harness_profile(
    "openrouter",
    _HarnessProfile(
        pre_init=lambda _spec: check_openrouter_version(),
        init_kwargs_factory=_openrouter_attribution_kwargs,
    ),
)
```

**作用**：
1. **版本检查**：确保 `langchain-openrouter >= 0.2.0`
2. **App Attribution**：添加 `app_url` 和 `app_title` headers（可被环境变量覆盖）

---

### profiles/__init__.py

```python
"""Harness profiles and provider-specific configuration.

Re-exports the profile dataclass, registry helpers, and provider modules so
internal consumers can import from `deepagents.profiles` directly.
"""

# Provider modules register their profiles as a side effect of import.
from deepagents.profiles import _openai as _openai
from deepagents.profiles._harness_profiles import (
    _HARNESS_PROFILES,
    _get_harness_profile,
    _HarnessProfile,
    _merge_profiles,
    _register_harness_profile,
)
from deepagents.profiles._openrouter import (
    OPENROUTER_MIN_VERSION,
    _openrouter_attribution_kwargs,
    check_openrouter_version,
)

__all__ = [
    "OPENROUTER_MIN_VERSION",
    "_HARNESS_PROFILES",
    "_HarnessProfile",
    "_get_harness_profile",
    "_merge_profiles",
    "_register_harness_profile",
    "check_openrouter_version",
    "_openrouter_attribution_kwargs",
]
```

**关键点**：
- 导入 `_openai` → 触发注册 `openai` profile
- 导入 `_openrouter` → 触发注册 `openrouter` profile
- Side effect import：模块导入即注册

---

## 九、完整使用流程

### resolve_model 使用

```python
# _models.py
from deepagents.profiles import _get_harness_profile

def resolve_model(model: str | BaseChatModel) -> BaseChatModel:
    """Resolve a model string to a `BaseChatModel`."""
    if isinstance(model, BaseChatModel):
        return model

    # 获取 Profile
    profile = _get_harness_profile(model)

    # 执行 pre_init（如有）
    if profile.pre_init is not None:
        profile.pre_init(model)

    # 合 kwargs
    kwargs: dict[str, Any] = {**profile.init_kwargs}
    if profile.init_kwargs_factory is not None:
        kwargs.update(profile.init_kwargs_factory())

    # 初始化模型
    return init_chat_model(model, **kwargs)
```

---

### resolve_model 执行流程

```
resolve_model("openrouter:anthropic/claude-4")
    ↓
_get_harness_profile("openrouter:anthropic/claude-4")
    ↓
查找：
    1. exact = None（未注册特定 model）
    2. provider = "openrouter"
    3. base = _HARNESS_PROFILES["openrouter"]
    ↓
返回 base profile
    ↓
profile.pre_init("openrouter:anthropic/claude-4")
    ↓
check_openrouter_version() 执行
    ↓
版本检查：
    - 如果 < 0.2.0 → ImportError
    - 如果 >= 0.2.0 → 继续
    ↓
kwargs = {**profile.init_kwargs}  # {}
    ↓
kwargs.update(profile.init_kwargs_factory())
    ↓
_openrouter_attribution_kwargs() 执行
    ↓
返回：
    {
        "app_url": "https://github.com/langchain-ai/deepagents",
        "app_title": "Deep Agents"
    }
    （除非环境变量已设置）
    ↓
kwargs = {"app_url": "...", "app_title": "..."}
    ↓
init_chat_model("openrouter:anthropic/claude-4", app_url="...", app_title="...")
    ↓
返回 ChatOpenRouter 实例
```

---

### create_deep_agent 使用（伪代码）

```python
# graph.py（简化版）
from deepagents.profiles import _get_harness_profile

def create_deep_agent(model: str, ...):
    # 解析模型
    chat_model = resolve_model(model)
    
    # 获取 Profile
    profile = _get_harness_profile(model)
    
    # 构建 System Prompt
    system_prompt = profile.base_system_prompt or BASE_AGENT_PROMPT
    if profile.system_prompt_suffix:
        system_prompt += "\n\n" + profile.system_prompt_suffix
    
    # 配置工具
    tools = get_default_tools()
    excluded = profile.excluded_tools
    tools = [t for t in tools if t.name not in excluded]
    
    # 应用工具描述覆盖
    for tool in tools:
        if tool.name in profile.tool_description_overrides:
            tool.description = profile.tool_description_overrides[tool.name]
    
    # 配置中间件
    middleware_stack = [
        FilesystemMiddleware(),
        SubAgentMiddleware(),
        ...
    ]
    
    # 添加 Provider 特定中间件
    extra_mw = _resolve_middleware_seq(profile.extra_middleware)
    middleware_stack.extend(extra_mw)
    
    # 创建 Agent
    return Agent(system_prompt=system_prompt, tools=tools, middleware=middleware_stack)
```

---

## 十、关键设计点总结

| 设计点 | 说明 | WHY |
|--------|------|-----|
| **frozen=True** | 数据类不可变 | 安全共享、线程安全 |
| **两层 Profile** | Provider + Model | 避免重复、继承默认 |
| **Profile 合** | Provider + Model = 最终配置 | 灵活定制、继承默认 |
| **dict 合** | `{**base, **override}` | 覆盖特定键、保留其他 |
| **frozenset 合** | 集合合并（累加） | 安全限制累积 |
| **Callable 串联** | 先 base，后 override | 不丢失任何检查 |
| **中间件按类型合并** | 相同类型替换，新类型追加 | 避免重复中间件 |
| **init_kwargs_factory** | 动态生成 kwargs | 支持环境变量 |
| **pre_init** | 初始化前检查 | 版本验证、前置条件 |
| **Side effect import** | 导入即注册 | 简洁注册流程 |

---

### 设计原则

#### 1. 不可变性

```python
@dataclass(frozen=True)
class _HarnessProfile:
    ...
```

- **线程安全**
- **无副作用修改**
- **安全共享**

---

#### 2. 分层继承

```
Provider Profile (默认)
    ↓ 继承
Model Profile (覆盖)
    ↓ 合
最终配置
```

- **避免重复定义**
- **Provider 默认自动继承**
- **Model 只需定义差异**

---

#### 3. 按类型合并

- **dict**: 合并 + 覆盖
- **frozenset**: 集合合并（累加）
- **Callable**: 串联
- **str**: Override 优先
- **Middleware**: 按类型替换

---

#### 4. 延迟计算

```python
init_kwargs_factory: Callable[[], dict] | None
```

- **环境变量延迟读取**
- **运行时动态参数**
- **避免模块加载时固定**

---

### 一句话总结

`_harness_profiles.py` 实现了 Harness Profile 系统，通过 `_HarnessProfile` 数据类声明 Provider/Model 特定配置，使用注册表存储配置，通过分层查找和智能合并（dict 合、frozenset 累加、Callable 串联、Middleware 按类型替换）生成最终配置，让 resolve_model 和 create_deep_agent 能自动应用 Provider 默认和 Model 特定设置。

**核心价值**：
- 两层 Profile → 避免重复定义
- 智能合 → Provider 默认自动继承
- frozen=True → 安全共享
- 延迟工厂 → 支持运行时动态参数

---

**文档生成时间**：2026-04-15
**适用版本**：Deep Agents v0.5.3