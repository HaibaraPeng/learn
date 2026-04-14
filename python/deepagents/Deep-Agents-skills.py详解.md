# Deep Agents skills.py 详细解析

> 本文档深入解析 `libs/deepagents/deepagents/middleware/skills.py` 文件
> 
> **阅读时间**：25-30 分钟
> 
> **适用人群**：想要理解 Skills 系统和 Progressive Disclosure 模式的开发者

---

## 目录

1. [文件概述](#一文件概述)
2. [Skill 结构与规范](#二skill-结构与规范)
3. [SkillMetadata 类型定义](#三skillmetadata-类型定义)
4. [Skill 名称验证](#四skill-名称验证)
5. [YAML Frontmatter 解析](#五yaml-frontmatter-解析)
6. [Skills 加载流程](#六skills-加载流程)
7. [SkillsMiddleware 实现](#七skillsmiddleware-实现)
8. [Progressive Disclosure 模式](#八progressive-disclosure-模式)
9. [System Prompt 注入](#九system-prompt-注入)
10. [使用示例](#十使用示例)
11. [关键设计点总结](#十一关键设计点总结)

---

## 一、文件概述

### 文件定位

```python
"""Skills middleware for loading and exposing agent skills to the system prompt.

This module implements Anthropic's agent skills pattern with progressive disclosure,
loading skills from backend storage via configurable sources.
"""
```

**一句话定义**：`skills.py` 实现了 SkillsMiddleware，用于加载和暴露 Agent Skills 到系统提示词，采用 Progressive Disclosure（渐进式披露）模式。

---

### 核心概念：什么是 Skills？

**Skills** 是一种**可复用的任务工作流模板**，定义了：
- 任务场景（何时使用）
- 执行步骤（如何做）
- 最佳实践（注意事项）
- 辅助文件（脚本、配置）

**类比**：
- Skills ≈ 编程中的"设计模式"
- Skills ≈ IDE 中的"代码模板"
- Skills ≈ 文档中的"工作流程"

---

### Skills vs Tools

| 特性 | Skills | Tools |
|------|--------|-------|
| **定义内容** | 工作流、最佳实践 | 具体操作函数 |
| **执行方式** | Agent 阅读 + 执行 | Agent 调用函数 |
| **粒度** | 高层任务流程 | 低层具体操作 |
| **灵活性** | Agent 可调整 | 固定实现 |
| **示例** | "如何做 Web 研究" | "搜索网页" |

**关系**：Skills 引导 Agent 使用 Tools

```
Skill: web-research
    ↓
指导 Agent 使用：
    - Tool: web_search
    - Tool: read_file
    - Tool: write_file
```

---

### Progressive Disclosure（渐进式披露）

**概念**：先显示摘要，需要时再显示详细内容

```
第一层（摘要）：
    - Skill name: web-research
    - Description: Structured web research workflow
    - Path: /skills/user/web-research/SKILL.md

第二层（详情）：
    - Agent 读取 SKILL.md 获取完整工作流
    - 包含步骤、最佳实践、示例

第三层（辅助）：
    - Agent 访问辅助文件（helper.py 等）
```

**WHY Progressive Disclosure**：
- 避免 System Prompt 过长
- Agent 只在需要时加载详情
- 减少 token 消耗
- 提高响应速度

---

### 文件结构概览

```
skills.py (834 lines)
├── 导入依赖 (line 91-124)
├── 常量定义 (line 126-133)
├── SkillMetadata TypedDict (line 135-193)
├── SkillsState TypedDict (line 195-207)
├── _validate_skill_name() (line 209-247) - 名称验证
├── _parse_skill_metadata() (line 250-353) - YAML 解析
├── _validate_metadata() (line 355-380) - metadata 验证
├── _format_skill_annotations() (line 383-401) - 注解格式化
├── _list_skills() (line 404-479) - 同步加载
├── _alist_skills() (line 482-557) - 异步加载
├── SKILLS_SYSTEM_PROMPT (line 560-599) - System Prompt 模板
├── SkillsMiddleware 类 (line 602-832)
│   ├── __init__() (line 634-644)
│   ├── _get_backend() (line 646-673)
│   ├── _format_skills_locations() (line 675-684)
│   ├── _format_skills_list() (line 686-703)
│   ├── modify_request() (line 705-725)
│   ├── before_agent() (line 727-761)
│   ├── abefore_agent() (line 763-797)
│   ├── wrap_model_call() (line 799-814)
│   └── awrap_model_call() (line 816-831)
```

---

## 二、Skill 结构与规范

### Skill 目录结构

```
/skills/user/web-research/
├── SKILL.md          # Required: YAML frontmatter + markdown instructions
└── helper.py         # Optional: supporting files
```

**必需文件**：`SKILL.md`

**可选文件**：
- `helper.py` — Python 辅助脚本
- `config.json` — 配置文件
- `examples/` — 示例文件
- 其他任何辅助文件

---

### SKILL.md 格式

```markdown
---
name: web-research
description: Structured approach to conducting thorough web research
license: MIT
compatibility: Python 3.10+
allowed-tools: web_search read_file write_file
metadata:
  author: Deep Agents Team
  version: 1.0
---

# Web Research Skill

## When to Use
- User asks you to research a topic
- Need to gather information from multiple sources
- Want to create a comprehensive summary

## Workflow

### Step 1: Define Research Scope
- Identify the main topic and key questions
- Determine the depth of research needed

### Step 2: Search and Gather
- Use `web_search` tool to find relevant sources
- Save promising URLs for later review

### Step 3: Read and Analyze
- Use `read_file` to examine saved content
- Extract key information and insights

### Step 4: Synthesize Results
- Combine findings into coherent summary
- Use `write_file` to save research output

## Best Practices
- Start with broad searches, then narrow down
- Validate information across multiple sources
- Document sources for credibility
```

---

### YAML Frontmatter 字段

| 字段 | 必需 | 类型 | 说明 |
|------|------|------|------|
| `name` | ✅ | str | Skill 标识符 |
| `description` | ✅ | str | Skill 描述 |
| `license` | ❌ | str | 许可证 |
| `compatibility` | ❌ | str | 环境要求 |
| `allowed-tools` | ❌ | str | 推荐工具列表 |
| `metadata` | ❌ | dict | 自定义元数据 |

---

### Agent Skills 规范约束

```python
# Agent Skills specification constraints (https://agentskills.io/specification)
MAX_SKILL_NAME_LENGTH = 64
MAX_SKILL_DESCRIPTION_LENGTH = 1024
MAX_SKILL_COMPATIBILITY_LENGTH = 500
```

**来源**：[Agent Skills Specification](https://agentskills.io/specification)

---

### Sources（技能来源）

**定义**：技能目录路径列表

```python
sources = [
    "/skills/base/",    # 基础技能
    "/skills/user/",    # 用户技能
    "/skills/project/", # 项目技能
]
```

**加载顺序**：先加载前面的，后面的覆盖同名 Skill

```
base/query-writing → 先加载
user/query-writing → 覆盖 base
project/query-writing → 覆盖 user

最终：project/query-writing（最后优先）
```

**覆盖规则**：`later sources override earlier ones (last one wins)`

---

## 三、SkillMetadata 类型定义

### TypedDict 定义

```python
class SkillMetadata(TypedDict):
    """Metadata for a skill per Agent Skills specification."""
    
    path: str
    """Path to the SKILL.md file."""
    
    name: str
    """Skill identifier.
    
    Constraints:
    - 1-64 characters
    - Unicode lowercase alphanumeric and hyphens only
    - Must not start or end with `-`
    - Must not contain consecutive `--`
    - Must match the parent directory name
    """
    
    description: str
    """What the skill does (1-1024 characters)."""
    
    license: str | None
    """License name or reference."""
    
    compatibility: str | None
    """Environment requirements (1-500 characters)."""
    
    metadata: dict[str, str]
    """Arbitrary key-value mapping."""
    
    allowed_tools: list[str]
    """Tool names the skill recommends using."""
```

---

### 字段详解

#### 1. path - 文件路径

```python
path: str
# 例如："/skills/user/web-research/SKILL.md"
```

**用途**：
- Agent 读取完整 Skill 内容
- 访问辅助文件

---

#### 2. name - Skill 标识符

```python
name: str
# 例如："web-research"
```

**约束**（Agent Skills 规范）：
- 1-64 字符
- Unicode lowercase alphanumeric 和 hyphens
- 不以 `-` 开头或结尾
- 不包含连续 `--`
- 必须匹配父目录名

**有效示例**：
- `web-research`
- `query-writing`
- `code-review`
- `café-skills`（支持 Unicode）

**无效示例**：
- `-invalid`（以 `-` 开头）
- `invalid-`（以 `-` 结尾）
- `web--research`（连续 `--`）
- `WebResearch`（大写字母）

---

#### 3. description - Skill 描述

```python
description: str
# 例如："Structured approach to conducting thorough web research"
```

**约束**：
- 1-1024 字符
- 应描述做什么和何时使用

**最佳实践**：
```markdown
description: "Conducts structured web research for comprehensive topic coverage. Use when user asks to research, investigate, or gather information about a topic."
```

---

#### 4. allowed_tools - 推荐工具

```python
allowed_tools: list[str]
# 例如：["web_search", "read_file", "write_file"]
```

**来源**：YAML frontmatter 的 `allowed-tools` 字段

```yaml
allowed-tools: web_search read_file write_file
# 解析为：["web_search", "read_file", "write_file"]
```

**解析逻辑**：
```python
raw_tools = frontmatter_data.get("allowed-tools")
if isinstance(raw_tools, str):
    allowed_tools = [
        t.strip(",")  # 支持逗号分隔（兼容 Claude Code）
        for t in raw_tools.split()
        if t.strip(",")
    ]
```

---

### SkillsState 定义

```python
class SkillsState(AgentState):
    """State for the skills middleware."""
    
    skills_metadata: NotRequired[Annotated[list[SkillMetadata], PrivateStateAttr]]
    """List of loaded skill metadata. Not propagated to parent agents."""
```

**关键点**：
- `PrivateStateAttr` 标记 → 不传播给父 Agent
- 只在当前 Agent 可见

**WHY PrivateStateAttr**：
- 子 Agent 有自己的 Skills
- 不应该继承父 Agent 的 Skills
- 避免 Skills 冗余

---

## 四、Skill 名称验证

### _validate_skill_name() 函数

```python
def _validate_skill_name(name: str, directory_name: str) -> tuple[bool, str]:
    """Validate skill name per Agent Skills specification.
    
    Args:
        name: Skill name from YAML frontmatter
        directory_name: Parent directory name
    
    Returns:
        (is_valid, error_message) tuple.
    """
```

---

### 验证规则

```python
# 1. 必需字段
if not name:
    return False, "name is required"

# 2. 长度限制
if len(name) > MAX_SKILL_NAME_LENGTH:
    return False, "name exceeds 64 characters"

# 3. 不能以 - 开头或结尾
if name.startswith("-") or name.endswith("-"):
    return False, "name must be lowercase alphanumeric with single hyphens only"

# 4. 不能包含连续 --
if "--" in name:
    return False, "name must be lowercase alphanumeric with single hyphens only"

# 5. 字符验证
for c in name:
    if c == "-":
        continue
    if (c.isalpha() and c.islower()) or c.isdigit():
        continue
    return False, "name must be lowercase alphanumeric with single hyphens only"

# 6. 必须匹配目录名
if name != directory_name:
    return False, f"name '{name}' must match directory name '{directory_name}'"

return True, ""
```

---

### Unicode 支持

```python
# Unicode lowercase alphanumeric 意味着：
# c.isalpha() and c.islower() → True（支持 accented Latin）
# c.isdigit() → True（数字）

# 有效示例：
"café-skills"     # é 是 lowercase alpha
"über-tool"       # ü 是 lowercase alpha
"技能-123"        # 中文字符是 alpha
```

---

### 验证示例

```python
# ✅ 有效
_validate_skill_name("web-research", "web-research")
# → (True, "")

_validate_skill_name("query-writing", "query-writing")
# → (True, "")

_validate_skill_name("café-skills", "café-skills")
# → (True, "")

# ❌ 无效
_validate_skill_name("-invalid", "-invalid")
# → (False, "name must be lowercase alphanumeric with single hyphens only")

_validate_skill_name("WebResearch", "WebResearch")
# → (False, "name must be lowercase alphanumeric with single hyphens only")

_validate_skill_name("web-research", "different-name")
# → (False, "name 'web-research' must match directory name 'different-name'")
```

---

### 验证失败处理

**策略**：警告但继续加载（向后兼容）

```python
is_valid, error = _validate_skill_name(str(name), directory_name)
if not is_valid:
    logger.warning(
        "Skill '%s' in %s does not follow Agent Skills specification: %s. "
        "Consider renaming for spec compliance.",
        name,
        skill_path,
        error,
    )
    # 不返回 None，继续加载
```

**WHY 向后兼容**：
- 不中断加载流程
- 提示用户修正
- 支持迁移过渡

---

## 五、YAML Frontmatter 解析

### _parse_skill_metadata() 函数

```python
def _parse_skill_metadata(
    content: str,
    skill_path: str,
    directory_name: str,
) -> SkillMetadata | None:
    """Parse YAML frontmatter from `SKILL.md` content.
    
    Args:
        content: Content of the `SKILL.md` file
        skill_path: Path to the file (for error messages)
        directory_name: Parent directory name
    
    Returns:
        SkillMetadata if parsing succeeds, None if fails.
    """
```

---

### 解析流程

```
读取 SKILL.md 内容
    ↓
检查文件大小（防止 DoS）
    ↓
匹配 YAML frontmatter（---分隔符）
    ↓
解析 YAML（yaml.safe_load）
    ↓
验证必需字段（name、description）
    ↓
验证 name 格式
    ↓
截断过长字段
    ↓
解析 allowed_tools
    ↓
返回 SkillMetadata
```

---

### YAML Frontmatter 格式

```markdown
---
name: web-research
description: Structured web research workflow
license: MIT
---
# Markdown content here
```

**分隔符规则**：
- 必须在文件开头
- `---` 开始和结束
- YAML 在中间

---

### 正则匹配

```python
frontmatter_pattern = r"^---\s*\n(.*?)\n---\s*\n"
match = re.match(frontmatter_pattern, content, re.DOTALL)

if not match:
    logger.warning("Skipping %s: no valid YAML frontmatter found", skill_path)
    return None

frontmatter_str = match.group(1)  # 提取 YAML 内容
```

**正则解释**：
- `^---\s*\n` — 开始分隔符
- `(.*?)` — YAML 内容（非贪婪）
- `\n---\s*\n` — 结束分隔符
- `re.DOTALL` — 跨行匹配

---

### YAML 解析

```python
try:
    frontmatter_data = yaml.safe_load(frontmatter_str)
except yaml.YAMLError as e:
    logger.warning("Invalid YAML in %s: %s", skill_path, e)
    return None

if not isinstance(frontmatter_data, dict):
    logger.warning("Skipping %s: frontmatter is not a mapping", skill_path)
    return None
```

**WHY safe_load**：
- 安全解析（不执行任意代码）
- 支持 nested structure
- 标准 YAML 解析

---

### 必需字段验证

```python
name = str(frontmatter_data.get("name", "")).strip()
description = str(frontmatter_data.get("description", "")).strip()

if not name or not description:
    logger.warning("Skipping %s: missing required 'name' or 'description'", skill_path)
    return None
```

---

### 字段截断（防止过长）

```python
# Description 截断
if len(description_str) > MAX_SKILL_DESCRIPTION_LENGTH:
    logger.warning("Description exceeds %d characters, truncating", ...)
    description_str = description_str[:MAX_SKILL_DESCRIPTION_LENGTH]

# Compatibility 截断
if compatibility_str and len(compatibility_str) > MAX_SKILL_COMPATIBILITY_LENGTH:
    compatibility_str = compatibility_str[:MAX_SKILL_COMPATIBILITY_LENGTH]
```

---

### allowed_tools 解析

```python
raw_tools = frontmatter_data.get("allowed-tools")
if isinstance(raw_tools, str):
    allowed_tools = [
        t.strip(",")  # 支持逗号（兼容 Claude Code）
        for t in raw_tools.split()
        if t.strip(",")
    ]
else:
    if raw_tools is not None:
        logger.warning("Ignoring non-string 'allowed-tools' in %s", skill_path)
    allowed_tools = []
```

**支持格式**：
```yaml
# 空格分隔
allowed-tools: web_search read_file write_file

# 逗号分隔（兼容 Claude Code）
allowed-tools: web_search, read_file, write_file
```

---

### metadata 验证

```python
metadata=_validate_metadata(frontmatter_data.get("metadata", {}), skill_path)

def _validate_metadata(raw: object, skill_path: str) -> dict[str, str]:
    """Validate and normalize metadata field."""
    if not isinstance(raw, dict):
        if raw:
            logger.warning("Ignoring non-dict metadata in %s", skill_path)
        return {}
    return {str(k): str(v) for k, v in raw.items()}
```

**WHY 类型强制**：
- YAML 可能返回任意类型
- SkillMetadata 要求 `dict[str, str]`
- 强制转换为正确类型

---

### 返回 SkillMetadata

```python
return SkillMetadata(
    name=str(name),
    description=description_str,
    path=skill_path,
    metadata=_validate_metadata(frontmatter_data.get("metadata", {})),
    license=str(frontmatter_data.get("license", "")).strip() or None,
    compatibility=compatibility_str,
    allowed_tools=allowed_tools,
)
```

---

## 六、Skills 加载流程

### _list_skills() 函数（同步）

```python
def _list_skills(backend: BackendProtocol, source_path: str) -> list[SkillMetadata]:
    """List all skills from a backend source.
    
    Expected structure:
    
    source_path/
    └── skill-name/
        ├── SKILL.md   # Required
        └── helper.py  # Optional
    
    Args:
        backend: Backend instance
        source_path: Path to skills directory
    
    Returns:
        List of skill metadata from successfully parsed SKILL.md files.
    """
```

---

### 加载流程图

```
backend.ls(source_path)
    ↓
列出目录内容
    ↓
识别 Skill 目录（is_dir=True）
    ↓
构建 SKILL.md 路径
    ↓
backend.download_files([...SKILL.md])
    ↓
批量下载 SKILL.md 文件
    ↓
逐个解析 YAML frontmatter
    ↓
返回 SkillMetadata 列表
```

---

### 目录结构识别

```python
# 列出 source_path 的内容
ls_result = backend.ls(source_path)
items = ls_result.entries if isinstance(ls_result, LsResult) else ls_result

# 找到所有 Skill 目录
skill_dirs = []
for item in items or []:
    if not item.get("is_dir"):
        continue
    skill_dirs.append(item["path"])
```

**期望结构**：

```
/skills/user/
├── web-research/   # Skill 目录
│   └── SKILL.md
├── query-writing/  # Skill 目录
│   └── SKILL.md
└── config.json     # 不是 Skill（文件，不是目录）
```

---

### SKILL.md 路径构建

```python
skill_md_paths = []
for skill_dir_path in skill_dirs:
    # 使用 PurePosixPath 安全构建路径
    skill_dir = PurePosixPath(skill_dir_path)
    skill_md_path = str(skill_dir / "SKILL.md")
    skill_md_paths.append((skill_dir_path, skill_md_path))

# 结果：
# [
#     ("/skills/user/web-research", "/skills/user/web-research/SKILL.md"),
#     ("/skills/user/query-writing", "/skills/user/query-writing/SKILL.md"),
# ]
```

**WHY PurePosixPath**：
- 平台无关路径操作
- 使用 POSIX 标准（`/`）
- Backend 负责平台转换

---

### 批量下载

```python
paths_to_download = [skill_md_path for _, skill_md_path in skill_md_paths]
responses = backend.download_files(paths_to_download)

# 每个响应：
# FileDownloadResponse(path="/skills/user/web-research/SKILL.md", content=b"...", error=None)
```

**批量下载好处**：
- 减少网络请求次数
- 提高加载速度
- Backend 优化（并发下载）

---

### 解析每个 SKILL.md

```python
for (skill_dir_path, skill_md_path), response in zip(skill_md_paths, responses):
    if response.error:
        continue  # 没有 SKILL.md，跳过
    
    if response.content is None:
        logger.warning("Downloaded skill file %s has no content", skill_md_path)
        continue
    
    try:
        content = response.content.decode("utf-8")
    except UnicodeDecodeError as e:
        logger.warning("Error decoding %s: %s", skill_md_path, e)
        continue
    
    # 提取目录名
    directory_name = PurePosixPath(skill_dir_path).name
    
    # 解析 metadata
    skill_metadata = _parse_skill_metadata(content, skill_md_path, directory_name)
    if skill_metadata:
        skills.append(skill_metadata)
```

---

### 异步版本（_alist_skills）

```python
async def _alist_skills(backend: BackendProtocol, source_path: str) -> list[SkillMetadata]:
    """Async version of _list_skills."""
    
    # 使用异步 Backend 方法
    ls_result = await backend.als(source_path)
    responses = await backend.adownload_files(paths_to_download)
    
    # 解析逻辑相同
    ...
```

---

## 七、SkillsMiddleware 实现

### 类定义

```python
class SkillsMiddleware(AgentMiddleware[SkillsState, ContextT, ResponseT]):
    """Middleware for loading and exposing agent skills to the system prompt.
    
    Loads skills from backend sources and injects them into the system prompt
    using progressive disclosure (metadata first, full content on demand).
    """
    
    state_schema = SkillsState
```

**继承关系**：
- 继承 `AgentMiddleware`
- 定义 `state_schema = SkillsState`

---

### 初始化

```python
def __init__(self, *, backend: BACKEND_TYPES, sources: list[str]) -> None:
    """Initialize the skills middleware.
    
    Args:
        backend: Backend instance (e.g. StateBackend()).
        sources: List of skill source paths.
    """
    self._backend = backend
    self.sources = sources
    self.system_prompt_template = SKILLS_SYSTEM_PROMPT
```

---

### Backend 解析

```python
def _get_backend(self, state, runtime, config) -> BackendProtocol:
    """Resolve backend from instance or factory."""
    
    if callable(self._backend):
        # Backend 是工厂函数
        tool_runtime = ToolRuntime(...)
        backend = self._backend(tool_runtime)
        if backend is None:
            raise AssertionError("SkillsMiddleware requires a valid backend")
        return backend
    
    # Backend 是实例
    return self._backend
```

**支持两种 Backend 形式**：
- Backend 实例（直接使用）
- Backend 工厂函数（需要调用）

---

### before_agent() - Skills 加载

```python
def before_agent(self, state, runtime, config) -> SkillsStateUpdate | None:
    """Load skills metadata before agent execution.
    
    Loads skills once per session. If skills_metadata is already present,
    the load is skipped and None is returned.
    """
    # 检查是否已加载
    if "skills_metadata" in state:
        return None
    
    backend = self._get_backend(state, runtime, config)
    all_skills: dict[str, SkillMetadata] = {}
    
    # 按顺序加载，后面的覆盖前面的
    for source_path in self.sources:
        source_skills = _list_skills(backend, source_path)
        for skill in source_skills:
            all_skills[skill["name"]] = skill
    
    skills = list(all_skills.values())
    return SkillsStateUpdate(skills_metadata=skills)
```

**关键点**：
- 只加载一次（检查 `skills_metadata` 是否存在）
- 后面的 source 覆盖前面的
- 返回 State Update

---

### Source 覆盖逻辑

```python
all_skills: dict[str, SkillMetadata] = {}

for source_path in self.sources:
    source_skills = _list_skills(backend, source_path)
    for skill in source_skills:
        all_skills[skill["name"]] = skill  # 字典覆盖

# 结果：最后加载的同名 Skill 保留
```

**示例**：

```python
sources = [
    "/skills/base/",
    "/skills/user/",
    "/skills/project/",
]

# base: query-writing v1.0
# user: query-writing v1.5
# project: query-writing v2.0

# 最终：query-writing v2.0
```

---

### wrap_model_call() - System Prompt 注入

```python
def wrap_model_call(self, request, handler) -> ModelResponse:
    """Inject skills documentation into the system prompt."""
    modified_request = self.modify_request(request)
    return handler(modified_request)

def modify_request(self, request) -> ModelRequest:
    """Inject skills documentation into system message."""
    skills_metadata = request.state.get("skills_metadata", [])
    
    skills_locations = self._format_skills_locations()
    skills_list = self._format_skills_list(skills_metadata)
    
    skills_section = self.system_prompt_template.format(
        skills_locations=skills_locations,
        skills_list=skills_list,
    )
    
    new_system_message = append_to_system_message(request.system_message, skills_section)
    return request.override(system_message=new_system_message)
```

---

### SkillsMiddleware 执行流程

```
Agent 启动
    ↓
before_agent() 执行
    ↓
加载 Skills Metadata
    ↓
State Update（skills_metadata）
    ↓
wrap_model_call() 执行
    ↓
注入 Skills Section 到 System Prompt
    ↓
模型调用（看到 Skills 列表）
```

---

## 八、Progressive Disclosure 模式

### 概念解释

**Progressive Disclosure** = 渐进式披露

**核心思想**：
- 第一层：显示摘要（name、description、path）
- 第二层：需要时加载详情（SKILL.md 内容）
- 第三层：访问辅助文件（helper.py 等）

---

### 三层披露

```
┌─────────────────────────────────────┐
│ 第一层：System Prompt（摘要）       │
├─────────────────────────────────────┤
│ - **web-research**: Structured      │
│   web research workflow             │
│   -> Read `/skills/user/...`        │
│                                     │
│ - **query-writing**: SQL query      │
│   construction patterns             │
│   -> Read `/skills/user/...`        │
└─────────────────────────────────────┘
    ↓ Agent 决定使用某个 Skill
    ↓
┌─────────────────────────────────────┐
│ 第二层：读取 SKILL.md（详情）       │
├─────────────────────────────────────┤
│ # Web Research Skill                │
│                                     │
│ ## Workflow                         │
│ 1. Define scope                     │
│ 2. Search and gather                │
│ 3. Read and analyze                 │
│ 4. Synthesize results               │
└─────────────────────────────────────┘
    ↓ Agent 需要辅助文件
    ↓
┌─────────────────────────────────────┐
│ 第三层：访问辅助文件                │
├─────────────────────────────────────┤
│ read_file("/skills/user/.../helper. │
│ py")                                │
└─────────────────────────────────────┘
```

---

### WHY Progressive Disclosure

#### 1. Token 经济

```
如果直接加载所有 SKILL.md 内容：

System Prompt = 50 KB
    ↓
每个请求成本 = $0.05（假设）

使用 Progressive Disclosure：

System Prompt = 2 KB（只有摘要）
    ↓
每个请求成本 = $0.002

节省：96%
```

---

#### 2. 响应速度

```
加载所有 SKILL.md：
    ↓
backend.read() × 10 skills
    ↓
等待时间 = 5 秒

加载摘要：
    ↓
已缓存在 state
    ↓
等待时间 = 0 秒
```

---

#### 3. 按需加载

```
用户："研究量子计算最新进展"
    ↓
Agent 匹配到 "web-research" Skill
    ↓
Agent 决定读取 SKILL.md
    ↓
Agent 执行工作流
    ↓
其他 Skills 的 SKILL.md 未加载（节省）
```

---

## 九、System Prompt 注入

### SKILLS_SYSTEM_PROMPT 模板

```python
SKILLS_SYSTEM_PROMPT = """

## Skills System

You have access to a skills library that provides specialized capabilities.

{skills_locations}

**Available Skills:**

{skills_list}

**How to Use Skills (Progressive Disclosure):**

1. **Recognize when a skill applies**
2. **Read the skill's full instructions**: Use the path shown above
3. **Follow the skill's instructions**: SKILL.md contains workflows
4. **Access supporting files**: Use absolute paths

**When to Use Skills:**
- User's request matches a skill's domain
- You need specialized knowledge
- A skill provides proven patterns

**Example Workflow:**

User: "Can you research the latest developments in quantum computing?"

1. Check available skills -> See "web-research"
2. Read the skill using the path shown
3. Follow the skill's research workflow
4. Use helper scripts with absolute paths
"""
```

---

### skills_locations 格式化

```python
def _format_skills_locations(self) -> str:
    """Format skills locations for display."""
    locations = []
    
    for i, source_path in enumerate(self.sources):
        name = PurePosixPath(source_path.rstrip("/")).name.capitalize()
        suffix = " (higher priority)" if i == len(self.sources) - 1 else ""
        locations.append(f"**{name} Skills**: `{source_path}`{suffix}")
    
    return "\n".join(locations)
```

**示例输出**：

```
**Base Skills**: `/skills/base/`
**User Skills**: `/skills/user/`
**Project Skills**: `/skills/project/` (higher priority)
```

---

### skills_list 格式化

```python
def _format_skills_list(self, skills: list[SkillMetadata]) -> str:
    """Format skills metadata for display."""
    if not skills:
        return "(No skills available yet. You can create skills in ...)"
    
    lines = []
    for skill in skills:
        annotations = _format_skill_annotations(skill)
        desc_line = f"- **{skill['name']}**: {skill['description']}"
        if annotations:
            desc_line += f" ({annotations})"
        lines.append(desc_line)
        
        if skill["allowed_tools"]:
            lines.append(f"  -> Allowed tools: {', '.join(skill['allowed_tools'])}")
        
        lines.append(f"  -> Read `{skill['path']}` for full instructions")
    
    return "\n".join(lines)
```

**示例输出**：

```
- **web-research**: Structured web research workflow (License: MIT)
  -> Allowed tools: web_search, read_file, write_file
  -> Read `/skills/user/web-research/SKILL.md` for full instructions

- **query-writing**: SQL query construction patterns
  -> Read `/skills/user/query-writing/SKILL.md` for full instructions
```

---

### 注解格式化

```python
def _format_skill_annotations(skill: SkillMetadata) -> str:
    """Build annotation string from optional fields."""
    parts: list[str] = []
    
    if skill.get("license"):
        parts.append(f"License: {skill['license']}")
    
    if skill.get("compatibility"):
        parts.append(f"Compatibility: {skill['compatibility']}")
    
    return ", ".join(parts)
```

**示例输出**：
- `"License: MIT, Compatibility: Python 3.10+"`
- `"License: Apache 2.0"`
- `""`（无注解）

---

### 最终 System Prompt 效果

```markdown
## Skills System

You have access to a skills library...

**Base Skills**: `/skills/base/`
**User Skills**: `/skills/user/`
**Project Skills**: `/skills/project/` (higher priority)

**Available Skills:**

- **web-research**: Structured web research workflow
  -> Read `/skills/user/web-research/SKILL.md` for full instructions

- **query-writing**: SQL query construction patterns
  -> Read `/skills/user/query-writing/SKILL.md` for full instructions

**How to Use Skills (Progressive Disclosure):**

1. Recognize when a skill applies
2. Read the skill's full instructions
...
```

---

## 十、使用示例

### 1. 创建 SkillsMiddleware

```python
from deepagents.backends import FilesystemBackend
from deepagents.middleware.skills import SkillsMiddleware

# 使用 FilesystemBackend
backend = FilesystemBackend(root_dir="./workspace", virtual_mode=True)

middleware = SkillsMiddleware(
    backend=backend,
    sources=[
        "/skills/base/",
        "/skills/user/",
        "/skills/project/",
    ],
)
```

---

### 2. 创建 SKILL.md

```markdown
---
name: web-research
description: Structured approach to conducting thorough web research
license: MIT
compatibility: Python 3.10+
allowed-tools: web_search read_file write_file
metadata:
  author: Deep Agents Team
  version: 1.0
---

# Web Research Skill

## When to Use
- User asks to research a topic
- Need comprehensive information gathering

## Workflow

### Step 1: Define Research Scope
- Identify main topic and key questions
- Determine research depth

### Step 2: Search and Gather
- Use `web_search` to find sources
- Save promising URLs

### Step 3: Read and Analyze
- Use `read_file` to examine content
- Extract key insights

### Step 4: Synthesize Results
- Combine findings into summary
- Use `write_file` to save output

## Best Practices
- Validate across multiple sources
- Document sources for credibility
```

---

### 3. 集成到 Agent

```python
from deepagents import create_deep_agent
from deepagents.backends import FilesystemBackend

backend = FilesystemBackend(root_dir="./workspace", virtual_mode=True)

agent = create_deep_agent(
    model="openai:gpt-4",
    backend=backend,
    skills=[
        "/skills/base/",
        "/skills/user/",
        "/skills/project/",
    ],
)

# Agent 启动时自动加载 Skills
result = agent.invoke({
    "messages": [{"role": "user", "content": "研究量子计算最新进展"}]
})
```

---

### 4. Agent 使用 Skills 的流程

```
用户："研究量子计算最新进展"
    ↓
Agent 收到请求
    ↓
System Prompt 包含 Skills 列表
    ↓
Agent 匹配到 "web-research" Skill
    ↓
Agent 决定读取 SKILL.md
    ↓
Agent 执行工具调用：
    read_file("/skills/user/web-research/SKILL.md")
    ↓
Agent 获取完整工作流
    ↓
Agent 按工作流执行：
    1. web_search("quantum computing latest developments")
    2. read_file(saved_urls)
    3. write_file(summary)
    ↓
Agent 返回结果
```

---

### 5. Skill 覆盖示例

```python
# base/query-writing/SKILL.md
---
name: query-writing
description: Basic SQL query patterns
version: 1.0
---

# user/query-writing/SKILL.md
---
name: query-writing
description: Advanced SQL query patterns with optimization
version: 2.0
---

# 最终 Agent 使用：user/query-writing v2.0
# base 的版本被覆盖
```

---

## 十一、关键设计点总结

| 设计点 | 说明 | WHY |
|--------|------|-----|
| **Progressive Disclosure** | 先摘要，后详情 | Token 经济、响应速度 |
| **Source 覆盖** | later wins | 灵活覆盖基础 Skills |
| **PrivateStateAttr** | 不传播给父 Agent | 子 Agent 有独立 Skills |
| **YAML safe_load** | 安全解析 | 防止代码注入 |
| **名称验证** | Agent Skills 规范 | 标准化、一致性 |
| **PurePosixPath** | 平台无关路径 | Backend 负责转换 |
| **批量下载** | download_files | 减少 API 调用 |
| **只加载一次** | 检查 state | 避免重复加载 |
| **向后兼容** | 警告但继续 | 支持迁移过渡 |
| **字段截断** | 防止过长 | 遵守规范约束 |

---

### 设计原则

#### 1. Progressive Disclosure

- 第一层：摘要（快速匹配）
- 第二层：详情（按需加载）
- 第三层：辅助（可选）

---

#### 2. 标准化

- Agent Skills 规范
- YAML frontmatter 格式
- 名称约束

---

#### 3. 可扩展性

- 多 Sources 支持
- Source 覆盖机制
- 自定义 metadata

---

#### 4. 安全性

- YAML safe_load
- 文件大小限制（10MB）
- 字段截断

---

## 一句话总结

`skills.py` 实现了 SkillsMiddleware，通过 Progressive Disclosure 模式加载和暴露 Agent Skills 到系统提示词，支持多 Source 覆盖、Agent Skills 规范、YAML frontmatter 解析，让 Agent 能在需要时获取专业工作流指导。

**核心价值**：
- Progressive Disclosure → Token 经济
- Source 覆盖 → 灵活定制
- Agent Skills 规范 → 标准化
- YAML frontmatter → 易于编写

---

**文档生成时间**：2026-04-13
**适用版本**：Deep Agents v0.5.2