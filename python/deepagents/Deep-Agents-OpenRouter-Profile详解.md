# Deep Agents _openrouter.py 详细解析

> 本文档深入解析 `libs/deepagents/deepagents/profiles/_openrouter.py` 文件
> 
> **阅读时间**：15-20 分钟
> 
> **适用人群**：想要理解 OpenRouter Provider 配置、版本检查机制和 App Attribution 的开发者

---

## 目录

1. [文件概述](#一文件概述)
2. [核心概念：OpenRouter](#二核心概念openrouter)
3. [文件结构](#三文件结构)
4. [常量定义](#四常量定义)
5. [版本检查机制](#五版本检查机制)
6. [App Attribution 机制](#六app-attribution-机制)
7. [Profile 注册](#七profile-注册)
8. [完整执行流程](#八完整执行流程)
9. [测试验证](#九测试验证)
10. [关键设计点总结](#十关键设计点总结)

---

## 一、文件概述

### 文件定位

```python
"""OpenRouter provider helpers.

!!! warning

    This is an internal API subject to change without deprecation. It is not
    intended for external use or consumption.

Constants and runtime checks for the OpenRouter integration (version
enforcement, app-attribution kwargs).
"""
```

**一句话定义**：`_openrouter.py` 为 OpenRouter Provider 提供版本检查、App Attribution 配置和 Harness Profile 注册。

**文件大小**：91 行（比 OpenAI 的 14 行复杂）

**重要警告**：这是**内部 API**，随时可能更改，不供外部使用。

---

### 为什么比 OpenAI 复杂？

| 功能 | OpenAI `_openai.py` | OpenRouter `_openrouter.py` |
|------|---------------------|----------------------------|
| **init_kwargs** | ✅ 静态 `{"use_responses_api": True}` | ❌ 无静态参数 |
| **init_kwargs_factory** | ❌ 无 | ✅ 动态生成（读取环境变量） |
| **pre_init** | ❌ 无 | ✅ 版本检查 |
| **常量定义** | ❌ 无 | ✅ 3 个常量 |
| **辅助函数** | ❌ 无 | ✅ 2 个函数 |
| **行数** | 14 行 | 91 行 |

**原因**：
- OpenRouter 需要版本检查（最低 0.2.0）
- App Attribution 参数需要动态生成（环境变量优先）
- 更多的配置灵活性

---

## 二、核心概念：OpenRouter

### 什么是 OpenRouter？

**OpenRouter** = LLM API 统一网关

**核心特点**：
- 单一 API 访问多个 Provider
- 价格比较和优化
- 负载均衡和故障切换
- App Attribution（应用归属标记）

---

### OpenRouter vs 直接调用 Provider

| 特性 | 直接调用 Anthropic | 通过 OpenRouter |
|------|-------------------|-----------------|
| **API Key** | Anthropic API Key | OpenRouter API Key（一个 Key 访问所有） |
| **模型格式** | `claude-4-sonnet` | `anthropic/claude-4-sonnet`（Provider 前缀） |
| **价格** | 固定价格 | 可选择不同定价方案 |
| **归属标记** | 不支持 | 支持 App Attribution |
| **故障切换** | 不支持 | 自动切换到备用模型 |

---

### App Attribution 概念

**App Attribution** = 应用归属标记

**用途**：
- 告诉 OpenRouter 请求来自哪个应用
- 用于统计、计费分析
- 可能影响定价折扣

**HTTP Headers**：
```
HTTP-Referer: https://github.com/langchain-ai/deepagents
X-Title: Deep Agents
```

**映射到 LangChain 参数**：
```python
init_chat_model(
    "openrouter:anthropic/claude-4-sonnet",
    app_url="https://github.com/langchain-ai/deepagents",  # → HTTP-Referer
    app_title="Deep Agents",  # → X-Title
)
```

---

### OpenRouter 文档参考

**官方文档**：https://openrouter.ai/docs/app-attribution

**关键信息**：
- App Attribution 用于识别请求来源
- 可以帮助获得更好的定价
- 建议为每个应用设置唯一的标识

---

## 三、文件结构

### 完整结构图

```
_openrouter.py (91 lines)
├── 文档字符串 (line 1-10)
├── 导入依赖 (line 12-20)
│   ├── os (环境变量)
│   ├── importlib.metadata (版本检查)
│   ├── packaging.version (版本解析)
│   └── _HarnessProfile, _register_harness_profile
├── 常量定义 (line 22-35)
│   ├── OPENROUTER_MIN_VERSION
│   ├── _OPENROUTER_APP_URL
│   └── _OPENROUTER_APP_TITLE
├── 辅助函数 (line 38-82)
│   ├── _openrouter_attribution_kwargs() (line 38-55)
│   └── check_openrouter_version() (line 58-82)
└── Profile 注册 (line 85-91)
│   └── _register_harness_profile("openrouter", ...)
```

---

### 导入依赖详解

```python
from __future__ import annotations

import os
from importlib.metadata import PackageNotFoundError, version as pkg_version
from typing import Any

from packaging.version import InvalidVersion, Version

from deepagents.profiles._harness_profiles import _HarnessProfile, _register_harness_profile
```

| 导入 | 用途 |
|------|------|
| `os` | 读取环境变量 `OPENROUTER_APP_URL`, `OPENROUTER_APP_TITLE` |
| `importlib.metadata` | 获取已安装的 `langchain-openrouter` 版本 |
| `packaging.version` | 解析和比较版本号（PEP 440） |
| `_HarnessProfile` | Profile 数据类 |
| `_register_harness_profile` | 注册函数 |

---

## 四、常量定义

### OPENROUTER_MIN_VERSION

```python
OPENROUTER_MIN_VERSION = "0.2.0"  # app attribution support added
"""Minimum required version of `langchain-openrouter`.

Used to enforce a consistent version floor at runtime.
"""
```

**作用**：
- 最低版本要求
- 低于此版本会抛出 ImportError

**为什么是 0.2.0**：
- App Attribution 功能在 0.2.0 版本引入
- 早期版本不支持 `app_url`, `app_title` 参数

---

### _OPENROUTER_APP_URL

```python
_OPENROUTER_APP_URL = "https://github.com/langchain-ai/deepagents"
"""Default `app_url` (maps to `HTTP-Referer`) for OpenRouter attribution.

See https://openrouter.ai/docs/app-attribution for details.
"""
```

**作用**：
- 默认 App URL
- 映射到 HTTP Header `HTTP-Referer`

**值**：
- Deep Agents GitHub 仓库 URL
- 标识请求来自 Deep Agents

---

### _OPENROUTER_APP_TITLE

```python
_OPENROUTER_APP_TITLE = "Deep Agents"
"""Default `app_title` (maps to `X-Title`) for OpenRouter attribution."""
```

**作用**：
- 默认 App Title
- 映射到 HTTP Header `X-Title`

**值**：
- "Deep Agents" — 应用名称
- 用于 OpenRouter 统计识别

---

### 常量命名约定

**命名模式**：
- `OPENROUTER_MIN_VERSION` — 公开常量（导出到 `__all__`）
- `_OPENROUTER_APP_URL` — 私有常量（下划线前缀）
- `_OPENROUTER_APP_TITLE` — 私有常量

**导出**（见 `profiles/__init__.py`）：
```python
__all__ = [
    "OPENROUTER_MIN_VERSION",  # 公开
    "_OPENROUTER_APP_URL",     # 导出但建议不外部使用
    "_OPENROUTER_APP_TITLE",   # 导出但建议不外部使用
    ...
]
```

---

## 五、版本检查机制

### check_openrouter_version() 函数

```python
def check_openrouter_version() -> None:
    """Raise if the installed `langchain-openrouter` is below the minimum.

    If the package is not installed at all the check is skipped;
    `init_chat_model` will surface its own missing-dependency error downstream.

    Raises:
        ImportError: If the installed version is too old.
    """
    try:
        installed = pkg_version("langchain-openrouter")
    except PackageNotFoundError:
        return
    try:
        is_old = Version(installed) < Version(OPENROUTER_MIN_VERSION)
    except InvalidVersion:
        # Non-PEP-440 version (dev build, fork, etc.) — skip the check
        return
    if is_old:
        msg = (
            f"deepagents requires langchain-openrouter>={OPENROUTER_MIN_VERSION}, "
            f"but {installed} is installed. "
            f"Run: pip install 'langchain-openrouter>={OPENROUTER_MIN_VERSION}'"
        )
        raise ImportError(msg)
```

---

### 版本检查流程图

```
check_openrouter_version() 执行
    ↓
尝试获取 langchain-openrouter 版本
    ↓ pkg_version("langchain-openrouter")
    ↓
    ├─ PackageNotFoundError → return（未安装，跳过检查）
    │     ↓
    │     init_chat_model 会抛出 missing-dependency 错误
    │
    └─ 成功获取版本 → installed = "0.1.5" / "0.2.0" / "0.3.0"
        ↓
        解析版本号
        ↓ Version(installed)
        ↓
        ├─ InvalidVersion → return（非 PEP 440 格式，跳过）
        │     例如："dev-branch", "fork-v1"
        │     ↓
        │     假设是开发版本，不检查
        │
        └─ 成功解析 → Version(installed)
            ↓
            比较版本
            ↓ Version(installed) < Version("0.2.0")
            ↓
            ├─ is_old = False → return（版本足够）
            │
            └─ is_old = True → raise ImportError
                ↓
                错误消息：
                "deepagents requires langchain-openrouter>=0.2.0,
                 but 0.1.5 is installed.
                 Run: pip install 'langchain-openrouter>=0.2.0'"
```

---

### 版本解析详解

#### pkg_version() 获取版本

```python
from importlib.metadata import version as pkg_version

installed = pkg_version("langchain-openrouter")
# 返回已安装版本字符串，例如：
# "0.1.5" — 低于最低版本
# "0.2.0" — 最低版本
# "0.3.0" — 高于最低版本
```

---

#### PackageNotFoundError 处理

```python
try:
    installed = pkg_version("langchain-openrouter")
except PackageNotFoundError:
    return  # 包未安装，跳过检查
```

**为什么跳过**：
- 包未安装时，`init_chat_model` 会抛出自己的错误
- 不需要在此处拦截

---

#### Version() 解析版本号

```python
from packaging.version import Version, InvalidVersion

try:
    is_old = Version(installed) < Version("0.2.0")
except InvalidVersion:
    return  # 非标准版本格式，跳过检查
```

**PEP 440 版本格式**：
- `0.1.5` — 标准格式
- `0.2.0` — 标准格式
- `1.0.0a1` — 预发布版本
- `dev-branch` — 非标准格式（InvalidVersion）

---

#### InvalidVersion 处理

```python
except InvalidVersion:
    # Non-PEP-440 version (dev build, fork, etc.) — skip the check
    return
```

**为什么跳过**：
- 开发版本、分支版本格式不规范
- 无法准确比较
- 假设开发版本足够新

---

### 版本比较逻辑

```python
is_old = Version(installed) < Version(OPENROUTER_MIN_VERSION)

if is_old:
    msg = (
        f"deepagents requires langchain-openrouter>={OPENROUTER_MIN_VERSION}, "
        f"but {installed} is installed. "
        f"Run: pip install 'langchain-openrouter>={OPENROUTER_MIN_VERSION}'"
    )
    raise ImportError(msg)
```

**示例**：

| installed | MIN_VERSION | is_old | 结果 |
|-----------|-------------|--------|------|
| `"0.1.5"` | `"0.2.0"` | True | ImportError |
| `"0.2.0"` | `"0.2.0"` | False | 正常 |
| `"0.3.0"` | `"0.2.0"` | False | 正常 |

---

### ImportError 消息设计

```python
msg = (
    f"deepagents requires langchain-openrouter>={OPENROUTER_MIN_VERSION}, "
    f"but {installed} is installed. "
    f"Run: pip install 'langchain-openrouter>={OPENROUTER_MIN_VERSION}'"
)
```

**特点**：
- **清晰**：说明需要的版本和当前版本
- **可操作**：提供具体的 pip 命令
- **可复制**：命令可以直接复制粘贴

---

## 六、App Attribution 机制

### _openrouter_attribution_kwargs() 函数

```python
def _openrouter_attribution_kwargs() -> dict[str, Any]:
    """Build OpenRouter attribution kwargs, deferring to env var overrides.

    `ChatOpenRouter` reads `OPENROUTER_APP_URL` and `OPENROUTER_APP_TITLE` via
    `from_env()` defaults. Explicit kwargs passed to the constructor take
    precedence over those env-var defaults, so we only inject our SDK defaults
    when the corresponding env var is **not** set — otherwise the user's env var
    would be overridden.

    Returns:
        Dictionary of attribution kwargs to spread into `init_chat_model`.
    """
    kwargs: dict[str, Any] = {}
    if not os.environ.get("OPENROUTER_APP_URL"):
        kwargs["app_url"] = _OPENROUTER_APP_URL
    if not os.environ.get("OPENROUTER_APP_TITLE"):
        kwargs["app_title"] = _OPENROUTER_APP_TITLE
    return kwargs
```

---

### 核心设计理念

**环境变量优先**：

```
优先级：
    1. 用户显式传递的 kwargs（最高）
    2. 环境变量 OPENROUTER_APP_URL / OPENROUTER_APP_TITLE
    3. SDK 默认值（最低）
```

**问题**：
- 如果 SDK 直接传递 `app_url="..."`
- 会覆盖用户的环境变量设置

**解决方案**：
- 检查环境变量是否存在
- 只有环境变量未设置时才注入 SDK 默认

---

### 环境变量优先级详解

#### ChatOpenRouter 的参数处理

```python
# langchain_openrouter/chat_models/openai.py（伪代码）
class ChatOpenRouter(BaseChatModel):
    def __init__(self, model: str, app_url: str | None = None, app_title: str | None = None):
        # 优先级：
        # 1. 显式 kwargs
        if app_url is None:
            # 2. 环境变量
            app_url = os.environ.get("OPENROUTER_APP_URL")
        if app_url is None:
            # 3. 默认值
            app_url = "https://openrouter.ai"
```

---

#### SDK 的策略

```python
# _openrouter_attribution_kwargs() 策略：
kwargs: dict[str, Any] = {}
if not os.environ.get("OPENROUTER_APP_URL"):
    kwargs["app_url"] = _OPENROUTER_APP_URL  # SDK 默认
```

**逻辑**：
- 如果环境变量 `OPENROUTER_APP_URL` 存在 → 不注入 SDK 默认
- 环境变量会通过 `ChatOpenRouter.from_env()` 自动生效
- SDK 默认只在环境变量未设置时作为 fallback

---

### 代码逻辑详解

```python
kwargs: dict[str, Any] = {}

# 检查 OPENROUTER_APP_URL
if not os.environ.get("OPENROUTER_APP_URL"):
    kwargs["app_url"] = _OPENROUTER_APP_URL

# 检查 OPENROUTER_APP_TITLE
if not os.environ.get("OPENROUTER_APP_TITLE"):
    kwargs["app_title"] = _OPENROUTER_APP_TITLE

return kwargs
```

---

### 返回值示例

#### 示例 1：无环境变量

```python
# 环境变量未设置
os.environ.get("OPENROUTER_APP_URL") → None
os.environ.get("OPENROUTER_APP_TITLE") → None

# 返回值
kwargs = {
    "app_url": "https://github.com/langchain-ai/deepagents",
    "app_title": "Deep Agents",
}
```

---

#### 示例 2：设置 OPENROUTER_APP_URL

```python
# 环境变量设置
os.environ["OPENROUTER_APP_URL"] = "https://custom.app"

# 返回值
kwargs = {
    "app_title": "Deep Agents",  # SDK 默认（环境变量未设置）
    # app_url 不注入（环境变量已设置）
}
```

---

#### 示例 3：两个环境变量都设置

```python
# 环境变量设置
os.environ["OPENROUTER_APP_URL"] = "https://custom.app"
os.environ["OPENROUTER_APP_TITLE"] = "My Custom App"

# 返回值
kwargs = {}  # 空 dict（不注入任何 SDK 默认）
```

---

### 环境变量配置示例

```bash
# 设置 App URL
export OPENROUTER_APP_URL="https://my-app.example.com"

# 设置 App Title
export OPENROUTER_APP_TITLE="My Application"

# 运行 Deep Agents
python -c "from deepagents import create_deep_agent; ..."
# → 使用自定义 App Attribution
```

---

## 七、Profile 注册

### 注册代码

```python
_register_harness_profile(
    "openrouter",
    _HarnessProfile(
        pre_init=lambda _spec: check_openrouter_version(),
        init_kwargs_factory=_openrouter_attribution_kwargs,
    ),
)
```

---

### 字段详解

| 字段 | 值 | 说明 |
|------|-----|------|
| `init_kwargs` | `{}`（默认） | 无静态参数 |
| `pre_init` | `lambda _spec: check_openrouter_version()` | 版本检查 |
| `init_kwargs_factory` | `_openrouter_attribution_kwargs` | 动态生成 App Attribution |
| `base_system_prompt` | `None`（默认） | 无自定义 prompt |
| `system_prompt_suffix` | `None`（默认） | 无 suffix |
| `tool_description_overrides` | `{}`（默认） | 无工具描述修改 |
| `excluded_tools` | `frozenset()`（默认） | 无工具排除 |
| `extra_middleware` | `()`（默认） | 无额外中间件 |

---

### pre_init 字段详解

```python
pre_init=lambda _spec: check_openrouter_version()
```

**作用**：
- 在 `init_chat_model` 之前执行
- 检查 `langchain-openrouter` 版本

**参数 `_spec`**：
- 接收 model spec 字符串
- 例如：`"openrouter:anthropic/claude-4-sonnet"`
- `check_openrouter_version()` 不使用此参数

**lambda 原因**：
- `pre_init` 需要 `Callable[[str], None]` 类型
- `check_openrouter_version` 是 `Callable[[], None]`
- 使用 lambda 包装以匹配签名

---

### init_kwargs_factory 字段详解

```python
init_kwargs_factory=_openrouter_attribution_kwargs
```

**作用**：
- 返回动态 kwargs dict
- 在 init 时调用

**返回值**：
```python
{
    "app_url": "https://github.com/langchain-ai/deepagents",
    "app_title": "Deep Agents",
}
# 或根据环境变量调整
```

**为什么需要 factory**：
- 环境变量需要延迟读取（运行时而非导入时）
- 用户可能在不同时间设置环境变量

---

### 与 OpenAI Profile 对比

```python
# OpenAI（静态）
_register_harness_profile(
    "openai",
    _HarnessProfile(init_kwargs={"use_responses_api": True}),
)

# OpenRouter（动态）
_register_harness_profile(
    "openrouter",
    _HarnessProfile(
        pre_init=lambda _spec: check_openrouter_version(),
        init_kwargs_factory=_openrouter_attribution_kwargs,
    ),
)
```

| 特性 | OpenAI | OpenRouter |
|------|--------|------------|
| `init_kwargs` | 静态 dict | 无（用 factory） |
| `pre_init` | 无 | 有（版本检查） |
| `init_kwargs_factory` | 无 | 有（动态参数） |
| **原因** | 参数固定 | 参数依赖运行时状态 |

---

## 八、完整执行流程

### resolve_model("openrouter:anthropic/claude-4-sonnet") 流程

```
resolve_model("openrouter:anthropic/claude-4-sonnet")
    ↓
检查 model 类型（str → 需要解析）
    ↓
调用 _get_harness_profile("openrouter:anthropic/claude-4-sonnet")
    ↓
查找 Profile：
    1. exact = _HARNESS_PROFILES.get("openrouter:anthropic/claude-4-sonnet") → None
    2. provider, sep, _ = "openrouter:anthropic/claude-4-sonnet".partition(":")
       → provider = "openrouter", sep = ":"
    3. base = _HARNESS_PROFILES.get("openrouter") → Profile
    ↓
返回 base Profile
    ↓
Profile 内容：
    init_kwargs = {}
    pre_init = lambda _spec: check_openrouter_version()
    init_kwargs_factory = _openrouter_attribution_kwargs
    ↓
执行 pre_init
    ↓ profile.pre_init("openrouter:anthropic/claude-4-sonnet")
    ↓ lambda _spec: check_openrouter_version()
    ↓
check_openrouter_version() 执行
    ↓
版本检查：
    ├─ 包未安装 → return（跳过）
    ├─ 版本过低 → raise ImportError
    └─ 版本足够 → return（正常）
    ↓
构建 kwargs
    ↓ kwargs: dict[str, Any] = {**profile.init_kwargs}
    ↓ kwargs = {}  # init_kwargs 为空
    ↓
执行 init_kwargs_factory
    ↓ if profile.init_kwargs_factory is not None
    ↓ kwargs.update(profile.init_kwargs_factory())
    ↓ _openrouter_attribution_kwargs() 执行
    ↓
返回 kwargs：
    ├─ 无环境变量 → {"app_url": "...", "app_title": "..."}
    ├─ 有 OPENROUTER_APP_URL → {"app_title": "..."}
    └─ 两个环境变量都有 → {}
    ↓
kwargs.update() 合
    ↓ kwargs = {"app_url": "...", "app_title": "..."} 或其他情况
    ↓
调用 init_chat_model
    ↓ init_chat_model("openrouter:anthropic/claude-4-sonnet", **kwargs)
    ↓ init_chat_model("openrouter:anthropic/claude-4-sonnet", app_url="...", app_title="...")
    ↓
返回 ChatOpenRouter 实例
```

---

### 代码对照

```python
# deepagents/_models.py
def resolve_model(model: str | BaseChatModel) -> BaseChatModel:
    if isinstance(model, BaseChatModel):
        return model

    # Step 1: 获取 Profile
    profile = _get_harness_profile(model)
    # model = "openrouter:anthropic/claude-4-sonnet"
    # profile.pre_init = lambda _spec: check_openrouter_version()
    # profile.init_kwargs_factory = _openrouter_attribution_kwargs

    # Step 2: 执行 pre_init（版本检查）
    if profile.pre_init is not None:
        profile.pre_init(model)
        # → check_openrouter_version() 执行

    # Step 3: 构建 kwargs（静态部分）
    kwargs: dict[str, Any] = {**profile.init_kwargs}
    # kwargs = {}  # init_kwargs 为空 dict

    # Step 4: 执行 factory（动态部分）
    if profile.init_kwargs_factory is not None:
        kwargs.update(profile.init_kwargs_factory())
        # → _openrouter_attribution_kwargs() 执行
        # → kwargs = {"app_url": "...", "app_title": "..."} 或 {}

    # Step 5: 初始化模型
    return init_chat_model(model, **kwargs)
    # = init_chat_model("openrouter:anthropic/claude-4-sonnet", app_url="...", app_title="...")
```

---

### 版本过低时的错误处理

```python
# 假设 langchain-openrouter==0.1.5 已安装
resolve_model("openrouter:anthropic/claude-4-sonnet")
    ↓
profile.pre_init("openrouter:anthropic/claude-4-sonnet")
    ↓
check_openrouter_version() 执行
    ↓
installed = pkg_version("langchain-openrouter") → "0.1.5"
    ↓
is_old = Version("0.1.5") < Version("0.2.0") → True
    ↓
raise ImportError(
    "deepagents requires langchain-openrouter>=0.2.0, "
    "but 0.1.5 is installed. "
    "Run: pip install 'langchain-openrouter>=0.2.0'"
)
    ↓
resolve_model 抛出 ImportError
    ↓
用户看到错误消息
```

---

## 九、测试验证

### test_models.py 测试（OpenRouter 相关）

```python
# tests/unit_tests/test_models.py

class TestResolveModel:
    """Tests for resolve_model."""

    def test_openrouter_prefix_sets_attribution(self) -> None:
        """验证 OpenRouter 前缀自动添加 App Attribution"""
        with patch("deepagents._models.init_chat_model") as mock:
            mock.return_value = MagicMock(spec=BaseChatModel)
            result = resolve_model("openrouter:anthropic/claude-sonnet-4-6")

        # 验证 init_chat_model 调用参数
        mock.assert_called_once_with(
            "openrouter:anthropic/claude-sonnet-4-6",
            app_url=_OPENROUTER_APP_URL,
            app_title=_OPENROUTER_APP_TITLE,
        )
        assert result is mock.return_value
```

---

### 环境变量覆盖测试

```python
def test_openrouter_env_var_overrides_app_url(self) -> None:
    """验证环境变量 OPENROUTER_APP_URL 覆盖 SDK 默认"""
    env = {"OPENROUTER_APP_URL": "https://custom.app"}
    with (
        patch("deepagents._models.init_chat_model") as mock,
        patch.dict("os.environ", env),
    ):
        mock.return_value = MagicMock(spec=BaseChatModel)
        resolve_model("openrouter:anthropic/claude-sonnet-4-6")

    _, kwargs = mock.call_args
    assert "app_url" not in kwargs  # SDK 默认不注入
    assert kwargs["app_title"] == _OPENROUTER_APP_TITLE  # SDK 默认仍注入


def test_openrouter_env_var_overrides_app_title(self) -> None:
    """验证环境变量 OPENROUTER_APP_TITLE 覆盖 SDK 默认"""
    env = {"OPENROUTER_APP_TITLE": "My Custom App"}
    with (
        patch("deepagents._models.init_chat_model") as mock,
        patch.dict("os.environ", env),
    ):
        mock.return_value = MagicMock(spec=BaseChatModel)
        resolve_model("openrouter:anthropic/claude-sonnet-4-6")

    _, kwargs = mock.call_args
    assert kwargs["app_url"] == _OPENROUTER_APP_URL  # SDK 默认仍注入
    assert "app_title" not in kwargs  # SDK 默认不注入


def test_openrouter_env_vars_override_both(self) -> None:
    """验证两个环境变量都设置时，不注入任何 SDK 默认"""
    env = {
        "OPENROUTER_APP_URL": "https://custom.app",
        "OPENROUTER_APP_TITLE": "My Custom App",
    }
    with (
        patch("deepagents._models.init_chat_model") as mock,
        patch.dict("os.environ", env),
    ):
        mock.return_value = MagicMock(spec=BaseChatModel)
        resolve_model("openrouter:anthropic/claude-sonnet-4-6")

    # 无任何额外 kwargs
    mock.assert_called_once_with("openrouter:anthropic/claude-sonnet-4-6")
```

---

### 测试覆盖矩阵

| 测试场景 | 环境变量设置 | SDK 默认行为 |
|----------|-------------|-------------|
| `test_openrouter_prefix_sets_attribution` | 无 | 两个都注入 |
| `test_openrouter_env_var_overrides_app_url` | `OPENROUTER_APP_URL` | 只注入 `app_title` |
| `test_openrouter_env_var_overrides_app_title` | `OPENROUTER_APP_TITLE` | 只注入 `app_url` |
| `test_openrouter_env_vars_override_both` | 两个都设置 | 不注入任何 |

---

## 十、关键设计点总结

| 设计点 | 说明 | WHY |
|--------|------|-----|
| **版本检查** | `pre_init` 执行版本验证 | 确保依赖包支持 App Attribution |
| **动态参数** | `init_kwargs_factory` 生成 kwargs | 支持环境变量优先级 |
| **环境变量优先** | 检查环境变量后才注入 SDK 默认 | 用户可自定义 App Attribution |
| **PackageNotFoundError 跳过** | 包未安装时不拦截 | 让 init_chat_model 抛出自己的错误 |
| **InvalidVersion 跳过** | 开发版本不检查 | 无法准确比较非标准版本 |
| **lambda 包装** | 包装 `check_openrouter_version` 匹配签名 | `pre_init` 需要 `Callable[[str], None]` |
| **清晰错误消息** | 包含版本信息和 pip 命令 | 用户可快速解决问题 |

---

### 与 OpenAI Profile 对比总结

| 特性 | OpenAI | OpenRouter |
|------|--------|------------|
| **代码行数** | 14 | 91 |
| **init_kwargs** | 静态 `{"use_responses_api": True}` | 无（动态生成） |
| **pre_init** | 无 | 版本检查 |
| **init_kwargs_factory** | 无 | App Attribution |
| **常量定义** | 无 | 3 个 |
| **辅助函数** | 无 | 2 个 |
| **环境变量支持** | 无 | 有（优先级） |
| **版本依赖** | 无限制 | >= 0.2.0 |

---

### 设计原则

#### 1. 环境变量优先

```python
if not os.environ.get("OPENROUTER_APP_URL"):
    kwargs["app_url"] = _OPENROUTER_APP_URL
```

- 用户环境变量优先
- SDK 默认作为 fallback
- 尊重用户配置

---

#### 2. 错误处理策略

```python
except PackageNotFoundError:
    return  # 跳过，让下游处理

except InvalidVersion:
    return  # 跳过，假设开发版本足够
```

- 不拦截未安装错误（下游更清晰）
- 开发版本不强制检查

---

#### 3. 清晰错误消息

```python
msg = (
    f"deepagents requires langchain-openrouter>={OPENROUTER_MIN_VERSION}, "
    f"but {installed} is installed. "
    f"Run: pip install 'langchain-openrouter>={OPENROUTER_MIN_VERSION}'"
)
```

- 说明需要的版本
- 说明当前版本
- 提供可执行的命令

---

#### 4. 延迟计算

```python
init_kwargs_factory=_openrouter_attribution_kwargs
```

- 环境变量在运行时读取
- 不是模块导入时固定
- 支持动态配置

---

### 一句话总结

`_openrouter.py` 为 OpenRouter Provider 注册 Harness Profile，通过 `pre_init` 执行版本检查（确保 `langchain-openrouter >= 0.2.0`），通过 `init_kwargs_factory` 动态生成 App Attribution 参数（支持环境变量优先级），让用户可以通过环境变量自定义 App Attribution，同时保证依赖包版本满足要求。

---

### 核心价值

- **版本安全**：确保依赖包支持必要功能
- **配置灵活**：环境变量优先，SDK 默认 fallback
- **错误友好**：清晰错误消息，可操作解决方案
- **延迟计算**：运行时读取环境变量

---

### 用户建议

| 场景 | 建议 |
|------|------|
| 使用 OpenRouter | 无需配置，自动添加 App Attribution |
| 自定义 Attribution | 设置环境变量 `OPENROUTER_APP_URL`, `OPENROUTER_APP_TITLE` |
| 版本过低错误 | 运行 `pip install 'langchain-openrouter>=0.2.0'` |
| 开发版本 | 使用非标准版本会跳过检查 |

---

**文档生成时间**：2026-04-15
**适用版本**：Deep Agents v0.5.3