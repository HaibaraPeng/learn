# Deep Agents _openai.py 详细解析

> 本文档深入解析 `libs/deepagents/deepagents/profiles/_openai.py` 文件
> 
> **阅读时间**：10-15 分钟
> 
> **适用人群**：想要理解 OpenAI Provider 配置和 Responses API 的开发者

---

## 目录

1. [文件概述](#一文件概述)
2. [核心概念：Responses API](#二核心概念responses-api)
3. [代码详解](#三代码详解)
4. [注册流程](#四注册流程)
5. [执行流程](#五执行流程)
6. [数据保留问题](#六数据保留问题)
7. [如何禁用 Responses API](#七如何禁用-responses-api)
8. [测试验证](#八测试验证)
9. [关键设计点总结](#九关键设计点总结)

---

## 一、文件概述

### 文件定位

```python
"""OpenAI provider harness profile.

!!! warning

    This is an internal API subject to change without deprecation. It is not
    intended for external use or consumption.
"""
```

**一句话定义**：`_openai.py` 为 OpenAI Provider 注册 Harness Profile，启用 Responses API 作为默认行为。

**文件大小**：仅 14 行（最简洁的 Provider Profile）

**重要警告**：这是**内部 API**，随时可能更改，不供外部使用。

---

### 文件结构

```python
# _openai.py (14 lines)
├── 文档字符串 (line 1-7)
├── 导入依赖 (line 9)
│   └── _HarnessProfile, _register_harness_profile
└── Profile 注册 (line 11-14)
│   └── _register_harness_profile("openai", ...)
```

---

### 文件完整代码

```python
"""OpenAI provider harness profile.

!!! warning

    This is an internal API subject to change without deprecation. It is not
    intended for external use or consumption.
"""

from deepagents.profiles._harness_profiles import _HarnessProfile, _register_harness_profile

_register_harness_profile(
    "openai",
    _HarnessProfile(init_kwargs={"use_responses_api": True}),
)
```

---

### 模块关系图

```
profiles/__init__.py
    ↓ 导入
_openai.py
    ↓ 执行
_register_harness_profile("openai", ...)
    ↓
_HARNESS_PROFILES["openai"] = _HarnessProfile(...)
    ↓
resolve_model("openai:gpt-4")
    ↓
_get_harness_profile("openai:gpt-4")
    ↓
返回 Profile with init_kwargs={"use_responses_api": True}
    ↓
init_chat_model("openai:gpt-4", use_responses_api=True)
    ↓
ChatOpenAI (Responses API enabled)
```

---

## 二、核心概念：Responses API

### 什么是 Responses API？

**Responses API** 是 OpenAI 在 2025 年推出的新 API，相比传统的 Chat Completions API：

| 特性 | Chat Completions API | Responses API |
|------|---------------------|---------------|
| **数据保留** | 不保留对话历史 | **默认保留对话历史** |
| **状态管理** | 需手动传递历史 | 自动管理对话状态 |
| **工具调用** | 手动处理 tool_calls | 自动处理工具调用 |
| **推理过程** | 不暴露 | 可访问 `reasoning.encrypted_content` |
| **Token 计费** | 按每次请求计费 | 可能按存储+请求计费 |
| **对话恢复** | 需要传递完整历史 | 使用 `previous_response_id` 恢复 |

---

### Responses API vs Chat Completions

#### Chat Completions API（传统）

```python
# 传统方式：每次都要传递完整历史
response = client.chat.completions.create(
    model="gpt-4",
    messages=[
        {"role": "user", "content": "Hello"},
        {"role": "assistant", "content": "Hi there"},
        {"role": "user", "content": "How are you?"},
        # ... 需要传递所有历史
    ]
)
# 数据不保留，每次重新开始
```

---

#### Responses API（新）

```python
# Responses API：自动管理历史
response = client.responses.create(
    model="gpt-4",
    input="How are you?",  # 只需要当前输入
    previous_response_id="resp_abc123"  # 使用之前的 response_id 恢复对话
)
# OpenAI 自动加载历史，无需手动传递
# **数据被保留在 OpenAI 服务器上**
```

---

### 为什么 Deep Agents 默认使用 Responses API？

#### 优势

1. **简化状态管理**
   - Deep Agent 的对话历史自动保存
   - 无需手动传递 messages history

2. **自动工具调用**
   - Agent 的工具调用自动处理
   - 减少 framework 代码复杂度

3. **推理过程访问**
   - 可访问模型的推理过程（encrypted）
   - 用于调试和分析

---

#### 隐忧

1. **数据保留风险**
   - 所有对话数据（包括工具输出）保留在 OpenAI
   - GDPR/数据主权问题

2. **隐私问题**
   - 敏感数据可能被 OpenAI 存储
   - 用户可能不知情

3. **依赖性**
   - 对话依赖 OpenAI 的存储
   - 如果 OpenAI 服务中断，历史可能丢失

---

### THREAT_MODEL.md 的警告

```markdown
#### T7: OpenAI Responses API Conversation Data Retention

- **Flow**: DF10 (Framework → LLM via OpenAI Responses API)
- **Description**: `_models.py:resolve_model` detects the `openai:` prefix and 
  initializes the model with `use_responses_api=True`. This causes implicit 
  data retention on OpenAI servers for all conversation data including tool outputs.
- **Preconditions**: User passes `"openai:..."` model spec without explicit 
  `store=False` override.
```

**关键点**：
- 默认行为导致数据隐式保留
- 用户可能不知情
- 需要明确 opt-out 才能禁用

---

## 三、代码详解

### 导入语句

```python
from deepagents.profiles._harness_profiles import _HarnessProfile, _register_harness_profile
```

**导入内容**：
- `_HarnessProfile` — Profile 数据类
- `_register_harness_profile` — 注册函数

---

### Profile 注册

```python
_register_harness_profile(
    "openai",
    _HarnessProfile(init_kwargs={"use_responses_api": True}),
)
```

**拆解分析**：

#### 1. Key: `"openai"`

- Provider 级别注册
- 适用于所有 `openai:` 前缀的模型
- 例如：`openai:gpt-4`, `openai:gpt-5`, `openai:o3-pro`

---

#### 2. Profile: `_HarnessProfile(...)`

```python
_HarnessProfile(
    init_kwargs={"use_responses_api": True}
)
```

**只设置一个字段**：

| 字段 | 值 | 含义 |
|------|-----|------|
| `init_kwargs` | `{"use_responses_api": True}` | 启用 Responses API |
| `pre_init` | `None`（默认） | 无前置检查 |
| `init_kwargs_factory` | `None`（默认） | 无动态参数 |
| `base_system_prompt` | `None`（默认） | 使用默认 prompt |
| `system_prompt_suffix` | `None`（默认） | 无 suffix |
| `tool_description_overrides` | `{}`（默认） | 无工具描述修改 |
| `excluded_tools` | `frozenset()`（默认） | 无工具排除 |
| `extra_middleware` | `()`（默认） | 无额外中间件 |

---

### init_kwargs 字段详解

```python
init_kwargs: dict[str, Any] = {"use_responses_api": True}
```

**作用**：
- 传递给 `langchain.chat_models.init_chat_model()` 的参数
- 告诉 LangChain 使用 Responses API

**执行效果**：

```python
# resolve_model("openai:gpt-4") 执行：
init_chat_model("openai:gpt-4", use_responses_api=True)
#                                        ↑ 来自 Profile
```

---

### langchain-openai 的 use_responses_api 参数

**来源**：`langchain-openai` 包的 `ChatOpenAI` 类

```python
# langchain_openai/chat_models/openai.py
class ChatOpenAI(BaseChatModel):
    def __init__(
        self,
        model: str,
        use_responses_api: bool = False,  # 默认 False
        ...
    ):
        if use_responses_api:
            # 使用 Responses API
            self._client = OpenAI().responses
        else:
            # 使用 Chat Completions API
            self._client = OpenAI().chat.completions
```

**Deep Agents 的决策**：
- 默认启用 `use_responses_api=True`
- 通过 Profile 机制自动应用
- 用户无需手动设置

---

## 四、注册流程

### 注册时机

```
profiles/__init__.py 加载
    ↓
from deepagents.profiles import _openai as _openai
    ↓
_openai.py 模块执行
    ↓
_register_harness_profile(...) 执行
    ↓
_HARNESS_PROFILES["openai"] = _HarnessProfile(...)
    ↓
注册完成
```

---

### Side Effect Import

```python
# profiles/__init__.py
# Provider modules register their profiles as a side effect of import.
from deepagents.profiles import _openai as _openai
```

**关键点**：
- 导入即注册（Side Effect）
- 不需要显式调用注册函数
- 模块导入顺序决定注册顺序

---

### 注册结果

```python
# 注册后，全局字典内容：
_HARNESS_PROFILES = {
    "openai": _HarnessProfile(init_kwargs={"use_responses_api": True}),
    "openrouter": _HarnessProfile(...),  # 来自 _openrouter.py
    ...
}
```

---

### 何时触发注册？

**场景 1：Deep Agents 包导入**

```python
# 用户代码
from deepagents import create_deep_agent

# 内部流程
from deepagents.profiles import ...  # 触发 __init__.py
    ↓
from deepagents.profiles import _openai  # 触发注册
    ↓
_HARNESS_PROFILES["openai"] 已注册
```

---

**场景 2：resolve_model 调用**

```python
# 用户代码
from deepagents._models import resolve_model

resolve_model("openai:gpt-4")

# 内部流程
from deepagents.profiles import _get_harness_profile
    ↓
profiles/__init__.py 加载（如果未加载）
    ↓
_openai.py 注册执行
    ↓
_get_harness_profile("openai:gpt-4") 查询
    ↓
返回已注册的 Profile
```

---

## 五、执行流程

### resolve_model 完整流程

```
用户调用 resolve_model("openai:gpt-4")
    ↓
检查 model 类型（str → 需要解析）
    ↓
调用 _get_harness_profile("openai:gpt-4")
    ↓
查找 Profile：
    1. exact = _HARNESS_PROFILES.get("openai:gpt-4") → None（未注册）
    2. provider, sep, _ = "openai:gpt-4".partition(":")
       → provider = "openai", sep = ":"
    3. base = _HARNESS_PROFILES.get("openai") → Profile
    ↓
返回 base Profile
    ↓
Profile 内容：
    init_kwargs = {"use_responses_api": True}
    pre_init = None
    init_kwargs_factory = None
    ↓
执行 pre_init（None → 跳过）
    ↓
构建 kwargs：
    kwargs = {**profile.init_kwargs}
    kwargs = {"use_responses_api": True}
    ↓
执行 init_kwargs_factory（None → 跳过）
    ↓
调用 init_chat_model("openai:gpt-4", use_responses_api=True)
    ↓
返回 ChatOpenAI 实例（Responses API enabled）
```

---

### 代码对照

```python
# deepagents/_models.py
def resolve_model(model: str | BaseChatModel) -> BaseChatModel:
    if isinstance(model, BaseChatModel):
        return model

    # Step 1: 获取 Profile
    profile = _get_harness_profile(model)  # model = "openai:gpt-4"

    # Step 2: 执行 pre_init（本例为 None，跳过）
    if profile.pre_init is not None:
        profile.pre_init(model)

    # Step 3: 构建 kwargs
    kwargs: dict[str, Any] = {**profile.init_kwargs}
    # kwargs = {"use_responses_api": True}

    # Step 4: 执行 factory（本例为 None，跳过）
    if profile.init_kwargs_factory is not None:
        kwargs.update(profile.init_kwargs_factory())

    # Step 5: 初始化模型
    return init_chat_model(model, **kwargs)
    # = init_chat_model("openai:gpt-4", use_responses_api=True)
```

---

### init_chat_model 内部行为

```python
# langchain/chat_models/__init__.py
def init_chat_model(model: str, **kwargs) -> BaseChatModel:
    # 解析 provider
    provider, model_name = parse_model_string(model)
    # provider = "openai", model_name = "gpt-4"

    # 选择 ChatModel 类
    if provider == "openai":
        from langchain_openai import ChatOpenAI
        return ChatOpenAI(model=model_name, **kwargs)
        # kwargs 包含 use_responses_api=True
```

---

### ChatOpenAI 初始化

```python
# langchain_openai/chat_models/openai.py
class ChatOpenAI(BaseChatModel):
    def __init__(self, model: str, use_responses_api: bool = False, ...):
        self.model = model
        self.use_responses_api = use_responses_api  # True

        if use_responses_api:
            # 使用 Responses API client
            self._client = self._get_responses_client()
        else:
            # 使用 Chat Completions client
            self._client = self._get_chat_client()
```

---

### 实际 API 调用差异

#### Responses API（use_responses_api=True）

```python
# ChatOpenAI._generate() 内部
if self.use_responses_api:
    response = self._client.responses.create(
        model=self.model,
        input=messages,  # OpenAI 自动处理历史
        tools=self._format_tools(tools),
        store=True,  # 默认存储
    )
    # 对话历史被 OpenAI 存储
```

---

#### Chat Completions（use_responses_api=False）

```python
# ChatOpenAI._generate() 内部
else:
    response = self._client.chat.completions.create(
        model=self.model,
        messages=messages,  # 需要传递完整历史
        tools=self._format_tools(tools),
    )
    # 对话历史不被存储
```

---

## 六、数据保留问题

### 问题背景

**核心问题**：OpenAI Responses API 默认保留对话数据

**影响范围**：
- 用户输入
- Assistant 回复
- 工具调用和工具输出
- 推理过程（encrypted）

---

### THREAT_MODEL.md 分析

```markdown
| Model provider (OpenAI Responses API) | DF10 (outbound) | T7 | 
  None (opt-out via user model config) | User (opt-out) | 
  `use_responses_api=True` default on `openai:` prefix causes implicit retention |
```

**解读**：
- **数据流**：DF10（Framework → LLM）
- **威胁**：T7（数据隐式保留）
- **责任**：用户需要 opt-out
- **问题**：默认行为导致隐式保留

---

### 数据保留流程

```
用户输入："查询客户数据"
    ↓
Agent 调用工具：read_file("customers.csv")
    ↓
工具输出："name: John, email: john@example.com"
    ↓
Responses API 将所有数据发送到 OpenAI
    ↓
OpenAI 存储在服务器
    ↓
**敏感数据被保留！**
```

---

### GDPR/数据主权问题

**欧洲 GDPR**：
- 数据处理需要明确同意
- 用户有权要求删除
- 数据应该最小化保留

** Responses API 默认行为**：
- 无明确同意提示
- 数据自动保留
- 用户可能不知情

---

### 数据保留时长

**OpenAI 官方政策**（可能变化）：
- Responses API 数据保留用于改进服务
- 具体时长未明确公开
- 用户可联系 OpenAI 要求删除

---

## 七、如何禁用 Responses API

### 方法 1：使用 Chat Completions API

```python
from langchain.chat_models import init_chat_model
from deepagents import create_deep_agent

# 手动初始化模型，禁用 Responses API
chat_model = init_chat_model("openai:gpt-4", use_responses_api=False)

# 使用手动初始化的模型
agent = create_deep_agent(model=chat_model)

# 结果：使用传统 Chat Completions API，数据不保留
```

---

### 方法 2：使用 Responses API 但禁用存储

```python
from langchain.chat_models import init_chat_model
from deepagents import create_deep_agent

# 使用 Responses API，但禁用存储
chat_model = init_chat_model(
    "openai:gpt-4",
    use_responses_api=True,
    store=False,  # 禁用存储
    include=["reasoning.encrypted_content"]  # 可选：访问推理过程
)

agent = create_deep_agent(model=chat_model)

# 结果：使用 Responses API，但数据不存储在 OpenAI
```

---

### 方法 3：修改 Profile（高级）

```python
from deepagents.profiles import _register_harness_profile, _HarnessProfile

# 注册自定义 Profile（覆盖默认）
_register_harness_profile(
    "openai",
    _HarnessProfile(init_kwargs={"use_responses_api": False}),
)

# 之后所有 openai: 模型使用 Chat Completions API
from deepagents import create_deep_agent
agent = create_deep_agent(model="openai:gpt-4")  # 自动使用新 Profile
```

**注意**：需要在导入 `deepagents` 之前修改

---

### 方法 4：使用其他 Provider

```python
from deepagents import create_deep_agent

# 使用 Anthropic（无数据保留问题）
agent = create_deep_agent(model="anthropic:claude-4-sonnet")

# 使用 OpenRouter（可能有不同政策）
agent = create_deep_agent(model="openrouter:anthropic/claude-4-sonnet")

# 使用本地模型（无数据保留）
agent = create_deep_agent(model="ollama:llama3")
```

---

### graph.py 的官方说明

```python
!!! note "OpenAI Models and Data Retention"

    If an `openai:` model is used, the agent will use the OpenAI
    Responses API by default. To use OpenAI chat completions
    instead, initialize the model with
    `init_chat_model("openai:...", use_responses_api=False)` and
    pass the initialized model instance here.

    To disable data retention with the Responses API, use
    `init_chat_model("openai:..., use_responses_api=True, store=False, 
    include=["reasoning.encrypted_content"])` and pass the initialized 
    model instance here.
```

---

## 八、测试验证

### test_models.py 测试

```python
# tests/unit_tests/test_models.py

def test_openai_prefix_sets_responses_api(self) -> None:
    """验证 OpenAI 前缀自动启用 Responses API"""
    with patch("deepagents._models.init_chat_model") as mock:
        mock.return_value = MagicMock(spec=BaseChatModel)
        result = resolve_model("openai:gpt-5")

    # 验证 init_chat_model 调用参数
    mock.assert_called_once_with("openai:gpt-5", use_responses_api=True)
    # ↑ 关键：use_responses_api=True 来自 Profile
    assert result is mock.return_value
```

---

### Profile 合测试

```python
# tests/unit_tests/test_models.py

def test_openai_profile_init_kwargs(self) -> None:
    """验证 OpenAI Profile 的 init_kwargs"""
    profile = _get_harness_profile("openai:gpt-5")
    
    assert profile.init_kwargs == {"use_responses_api": True}
    # ↑ 确认 Profile 正确注册


def test_model_override_of_provider_profile(self) -> None:
    """验证 Model Profile 可以覆盖 Provider Profile"""
    # 注册 Model 级 Profile
    _register_harness_profile(
        "openai:custom-model",
        _HarnessProfile(init_kwargs={"use_responses_api": False}),
    )
    
    profile = _get_harness_profile("openai:custom-model")
    
    assert profile.init_kwargs == {"use_responses_api": False}
    # ↑ Model 级覆盖生效，Provider 默认被替换


def test_openai_model_with_provider_base(self) -> None:
    """验证无 Model Profile 时继承 Provider Profile"""
    profile = _get_harness_profile("openai:gpt-5")
    
    assert profile.init_kwargs == {"use_responses_api": True}
    # ↑ 无 Model Profile，继承 Provider 默认
```

---

### 测试覆盖要点

| 测试场景 | 验证内容 |
|----------|----------|
| `test_openai_prefix_sets_responses_api` | `resolve_model("openai:...")` 自动传递 `use_responses_api=True` |
| `test_openai_profile_init_kwargs` | Profile 正确注册 `init_kwargs={"use_responses_api": True}` |
| `test_model_override_of_provider_profile` | Model Profile 可以覆盖 Provider 默认 |
| `test_openai_model_with_provider_base` | 无 Model Profile 时继承 Provider 默认 |

---

## 九、关键设计点总结

| 设计点 | 说明 | WHY |
|--------|------|-----|
| **简洁性** | 仅 14 行代码 | 单一职责：注册 OpenAI Profile |
| **单一字段** | 只设置 `init_kwargs` | OpenAI 只需要 Responses API 参数 |
| **Provider 级注册** | `"openai"` 而非 `"openai:gpt-4"` | 所有 OpenAI 模型共享配置 |
| **Side Effect Import** | 导入即注册 | 无需显式调用 |
| **默认启用 Responses API** | `use_responses_api=True` | 简化状态管理、自动工具调用 |
| **数据保留隐忧** | 对话数据保留在 OpenAI | 用户需知情并 opt-out |
| **可覆盖** | Model Profile 可覆盖 Provider | 支持特定模型自定义 |
| **可禁用** | 提供 opt-out 方法 | 用户可选择隐私保护 |

---

### 设计决策分析

#### 为什么默认启用 Responses API？

**优点**：
1. **简化开发**
   - 无需手动管理对话历史
   - 自动处理工具调用

2. **功能增强**
   - 访问推理过程
   - 更好的对话恢复

3. **一致性**
   - OpenAI 推荐的新 API
   - 未来主要发展方向

---

**缺点**：
1. **隐私问题**
   - 数据隐式保留
   - 用户可能不知情

2. **合规问题**
   - GDPR 要求明确同意
   - 数据主权问题

3. **依赖性**
   - 对话依赖 OpenAI 存储
   - 服务中断风险

---

### 设计权衡

**Deep Agents 的选择**：
- 默认启用（简化开发）
- 提供禁用方法（尊重隐私）
- 文档警告（知情同意）

**THREAT_MODEL.md 的态度**：
- 明确记录威胁
- 用户责任 opt-out
- 不改变默认行为

---

### 文件简洁性的原因

#### 为什么只有 14 行？

**OpenAI Provider 的特殊性**：
- 只需要一个参数：`use_responses_api`
- 无版本检查（langchain-openai 无最低版本要求）
- 无动态参数（无环境变量依赖）
- 无特殊中间件（Anthropic 需要 Prompt Caching）
- 无工具排除（所有 OpenAI 模型支持所有工具）

**对比 OpenRouter**（91 行）：
- 版本检查：`check_openrouter_version()`
- 动态参数：`init_kwargs_factory` 读取环境变量
- App Attribution：`app_url`, `app_title`
- 常量定义：`OPENROUTER_MIN_VERSION`, `_OPENROUTER_APP_URL`

---

## 十、一句话总结

`_openai.py` 为 OpenAI Provider 注册 Harness Profile，通过 `init_kwargs={"use_responses_api": True}` 启用 Responses API，让所有 OpenAI 模型自动使用新 API（自动管理对话历史），但同时也导致对话数据隐式保留在 OpenAI 服务器，用户需通过显式 opt-out 禁用或使用手动初始化模型来保护隐私。

---

### 核心价值

- **简洁**：14 行完成 Provider 配置
- **自动化**：所有 OpenAI 模型自动启用 Responses API
- **一致性**：Provider 级配置，所有模型继承
- **可覆盖**：Model Profile 可覆盖 Provider 默认

---

### 核心隐忧

- **数据保留**：对话数据保留在 OpenAI
- **隐私风险**：敏感数据可能被存储
- **知情同意**：用户可能不知默认行为
- **合规问题**：GDPR/数据主权问题

---

### 用户建议

| 场景 | 建议 |
|------|------|
| 无敏感数据 | 可使用默认配置 |
| 有敏感数据 | 禁用 Responses API 或禁用存储 |
| 企业合规 | 使用其他 Provider 或本地模型 |
| 开发测试 | 可使用默认配置 |

---

**文档生成时间**：2026-04-15
**适用版本**：Deep Agents v0.5.3