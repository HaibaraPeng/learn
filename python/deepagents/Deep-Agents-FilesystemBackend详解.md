# Deep Agents FilesystemBackend 详细解析

> 本文档深入解析 `libs/deepagents/deepagents/backends/filesystem.py` 文件
> 
> **阅读时间**：20-25 分钟
> 
> **适用人群**：想要理解本地文件系统 Backend 实现的开发者

---

## 目录

1. [核心概念：FilesystemBackend 是什么？](#一核心概念filesystembackend-是什么)
2. [安全警告（重要）](#二安全警告重要)
3. [初始化参数详解](#三初始化参数详解)
4. [路径解析机制](#四路径解析机制)
5. [核心方法详解](#五核心方法详解)
6. [批量操作方法](#六批量操作方法)
7. [错误处理机制](#七错误处理机制)
8. [安全设计分析](#八安全设计分析)
9. [使用示例](#九使用示例)
10. [关键设计点总结](#十关键设计点总结)

---

## 一、核心概念：FilesystemBackend 是什么？

### 定义

```python
class FilesystemBackend(BackendProtocol):
    """Backend that reads and writes files directly from the filesystem.
    
    Files are accessed using their actual filesystem paths.
    """
```

**一句话定义**：FilesystemBackend 是一个直接读写本地文件系统的 Backend 实现。

---

### WHY 需要 FilesystemBackend？

**场景需求**：
- Agent 需要读写用户本地文件（开发工具、CLI）
- Agent 需要修改项目代码（coding assistant）
- Agent 需要访问配置文件（IDE 集成）
- Agent 需要运行脚本并查看输出

**其他 Backend 的局限性**：
- **StateBackend**：文件存储在内存，不持久化，重启丢失
- **StoreBackend**：文件存储在 LangGraph Store，不是真实文件
- **SandboxBackend**：需要远程沙箱环境，启动慢，成本高

**FilesystemBackend 的优势**：
- 直接访问真实文件系统
- 持久化到磁盘，重启不丢失
- 无需额外基础设施（沙箱）
- 启动速度快，响应延迟低

---

### 与 Protocol 的关系

```
BackendProtocol (协议定义)
    ↓
FilesystemBackend (具体实现)
    ↓
实现所有 Protocol 方法：
    - ls(path) → 列出目录
    - read(file_path) → 读取文件
    - write(file_path, content) → 写入文件
    - edit(file_path, old, new) → 编辑文件
    - grep(pattern, path, glob) → 搜索内容
    - glob(pattern, path) → 搜索文件
    - upload_files(files) → 上传文件
    - download_files(paths) → 下载文件
```

---

## 二、安全警告（重要）

### 官方警告内容

```python
!!! warning "Security Warning"
    
    This backend grants agents direct filesystem read/write access. 
    Use with caution and only in appropriate environments.
    
    **Appropriate use cases:**
    - Local development CLIs (coding assistants, development tools)
    - CI/CD pipelines (see security considerations below)
    
    **Inappropriate use cases:**
    - Web servers or HTTP APIs
    - Production environments
    
    **Security risks:**
    - Agents can read any accessible file, including secrets
    - Secrets may be exfiltrated via SSRF attacks
    - File modifications are permanent and irreversible
    
    **Recommended safeguards:**
    1. Enable Human-in-the-Loop (HITL) middleware
    2. Exclude secrets from accessible paths
    3. Prefer StateBackend/StoreBackend/SandboxBackend for production
```

---

### 安全风险详解

#### 1. 读取敏感文件

**风险**：Agent 可以读取任何有权限访问的文件

```python
# Agent 可以读取这些文件：
backend.read("/etc/passwd")              # 系统用户信息
backend.read("/home/user/.ssh/id_rsa")   # SSH 私钥
backend.read("/app/.env")                # API keys、密码
backend.read("/app/config/secrets.json") # 敏感配置
```

**后果**：
- API keys 泄露 → 财务损失
- SSH keys 泄露 → 服务器被入侵
- 密码泄露 → 账号被接管

---

#### 2. SSRF 攻击组合

**风险**：Agent 读取敏感文件 + 网络工具 → 数据泄露

```
Agent 读取 .env 文件（包含 API_KEY）
    ↓
Agent 使用网络工具（curl/wget）
    ↓
Agent 将 API_KEY 发送到外部服务器
    ↓
攻击者获得 API_KEY
```

**防御**：
- 启用 Human-in-the-Loop（审批敏感操作）
- 禁止读取敏感路径
- 禁用网络工具
- 使用 SandboxBackend（隔离环境）

---

#### 3. 文件修改不可逆

**风险**：Agent 修改文件后无法恢复

```python
# Agent 可能执行：
backend.edit("/app/config.py", "DEBUG = False", "DEBUG = True")

# 如果没有备份，无法恢复
```

**防御**：
- 启用 Human-in-the-Loop（审批修改）
- 使用 Git（可以回滚）
- 定期备份重要文件

---

### 适用场景分析

| 场景 | 推荐 Backend | 原因 |
|------|--------------|------|
| **本地开发 CLI** | FilesystemBackend | 用户控制，信任 Agent |
| **CI/CD Pipeline** | FilesystemBackend + HITL | 自动化审批流程 |
| **Web API 服务** | StateBackend/StoreBackend | 避免文件系统暴露 |
| **生产环境** | SandboxBackend | 需要隔离 |
| **云服务** | SandboxBackend | 需要隔离 |
| **多租户系统** | StateBackend | 每个租户独立状态 |

---

## 三、初始化参数详解

### __init__ 方法

```python
def __init__(
    self,
    root_dir: str | Path | None = None,
    virtual_mode: bool | None = None,
    max_file_size_mb: int = 10,
) -> None:
```

---

### 参数详解

#### 1. root_dir - 根目录

**类型**：`str | Path | None`

**默认值**：`None` → 当前工作目录（`Path.cwd()`）

**作用**：
- 设置文件操作的基准目录
- 影响相对路径的解析
- 影响 virtual_mode 的行为

**示例**：

```python
# 默认：当前工作目录
backend = FilesystemBackend()
backend.read("main.py")  # → Path.cwd() / "main.py"

# 指定根目录
backend = FilesystemBackend(root_dir="/workspace")
backend.read("main.py")  # → /workspace/main.py

# 使用 Path 对象
backend = FilesystemBackend(root_dir=Path("/workspace"))
```

---

#### 2. virtual_mode - 虚拟路径模式

**类型**：`bool | None`

**默认值**：`None` → 发出警告，默认 `False`

**作用**：控制路径解析和访问限制

**重要变更**：
```python
if virtual_mode is None:
    warnings.warn(
        "FilesystemBackend virtual_mode default will change in deepagents 0.5.0; "
        "please specify virtual_mode explicitly.",
        DeprecationWarning,
    )
    virtual_mode = False
```

---

#### virtual_mode 行为对比

| 特性 | `virtual_mode=False` | `virtual_mode=True` |
|------|---------------------|---------------------|
| **绝对路径** | 直接使用（如 `/etc/passwd`） | 视为虚拟路径，限制在 `root_dir` |
| **相对路径** | 相对于 `root_dir` | 视为虚拟路径，限制在 `root_dir` |
| **路径穿越** (`..`) | 允许（可逃逸 `root_dir`） | 禁止 |
| **符号链接** (`~`) | 允许 | 禁止 |
| **路径限制** | 无限制（可访问任意路径） | 限制在 `root_dir` 内 |
| **返回路径** | 绝对路径 | 虚拟路径（相对于 `root_dir`） |

---

#### virtual_mode=False 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=False)

# 绝对路径：直接使用
backend.read("/etc/passwd")  # → 读取 /etc/passwd（逃逸 root_dir）

# 相对路径：相对于 root_dir
backend.read("main.py")  # → /workspace/main.py

# 路径穿越：可以逃逸
backend.read("../secrets.env")  # → /secrets.env（逃逸 root_dir）
```

**风险**：Agent 可以访问任何有权限的文件。

---

#### virtual_mode=True 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 绝对路径：视为虚拟路径，限制在 root_dir
backend.read("/main.py")  # → /workspace/main.py
backend.read("/etc/passwd")  # → ValueError（逃逸 root_dir）

# 相对路径：视为虚拟路径
backend.read("main.py")  # → /workspace/main.py

# 路径穿越：禁止
backend.read("../secrets.env")  # → ValueError（路径穿越）

# 返回路径：虚拟路径
backend.ls("/")  # → 返回 ["main.py", "config.json"]（虚拟路径）
```

**限制**：所有路径都限制在 `root_dir` 内。

---

#### 3. max_file_size_mb - 最大文件大小

**类型**：`int`

**默认值**：`10` (10 MB)

**作用**：限制 grep 的 Python fallback 搜索文件大小

**计算**：

```python
self.max_file_size_bytes = max_file_size_mb * 1024 * 1024
# 例如：10 * 1024 * 1024 = 10,485,760 bytes
```

**用途**：
- grep 搜索时跳过超大文件
- 避免 Python fallback 性能问题
- 优先使用 ripgrep（不受此限制）

---

### 初始化示例

```python
# 最简单：使用默认值
backend = FilesystemBackend()  # 当前目录，virtual_mode=False

# 推荐：明确指定 virtual_mode
backend = FilesystemBackend(
    root_dir="./workspace",
    virtual_mode=True,  # 明确指定
)

# 开发环境：允许路径穿越
backend = FilesystemBackend(
    root_dir="./project",
    virtual_mode=False,  # 允许访问外部路径
)

# CI/CD：限制访问范围
backend = FilesystemBackend(
    root_dir="/ci/workspace",
    virtual_mode=True,  # 限制在 workspace
    max_file_size_mb=50,  # 允许更大文件
)
```

---

## 四、路径解析机制

### _resolve_path 方法

```python
def _resolve_path(self, key: str) -> Path:
    """Resolve a file path with security checks.
    
    Returns:
        Resolved absolute Path object.
    
    Raises:
        ValueError: If path traversal is attempted in virtual_mode.
    """
```

这是 FilesystemBackend 的核心路径解析方法。

---

### virtual_mode=False 的解析逻辑

```python
if not self.virtual_mode:
    path = Path(key)
    if path.is_absolute():
        return path  # 绝对路径：直接返回
    return (self.cwd / path).resolve()  # 相对路径：相对于 cwd
```

**示例**：

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=False)

backend._resolve_path("/etc/passwd")      # → Path("/etc/passwd")
backend._resolve_path("main.py")          # → Path("/workspace/main.py")
backend._resolve_path("../secrets.env")   # → Path("/secrets.env")
```

---

### virtual_mode=True 的解析逻辑

```python
if self.virtual_mode:
    # 1. 规范化为虚拟路径（以 / 开头）
    vpath = key if key.startswith("/") else "/" + key
    
    # 2. 检查路径穿越
    if ".." in vpath or vpath.startswith("~"):
        raise ValueError("Path traversal not allowed")
    
    # 3. 解析到 root_dir
    full = (self.cwd / vpath.lstrip("/")).resolve()
    
    # 4. 检查是否在 root_dir 内
    try:
        full.relative_to(self.cwd)
    except ValueError:
        raise ValueError(f"Path:{full} outside root directory")
    
    return full
```

**示例**：

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

backend._resolve_path("/main.py")         # → Path("/workspace/main.py")
backend._resolve_path("main.py")          # → Path("/workspace/main.py")
backend._resolve_path("../secrets.env")   # → ValueError（路径穿越）
backend._resolve_path("/etc/passwd")      # → ValueError（逃逸 root_dir）
```

---

### 路径解析流程图

```
输入路径 key
    ↓
判断 virtual_mode
    ↓
┌─────────────────────────┬─────────────────────────┐
│ virtual_mode=False      │ virtual_mode=True       │
├─────────────────────────┼─────────────────────────┤
│ 判断是否绝对路径        │ 规范化为虚拟路径        │
│     ↓                   │     ↓                   │
│ 绝对：直接返回          │ 检查路径穿越            │
│ 相对：cwd / key         │     ↓                   │
│                         │ 解析到 cwd              │
│                         │     ↓                   │
│                         │ 检查是否在 cwd 内       │
│                         │     ↓                   │
│                         │ 在内：返回              │
│                         │ 在外：抛 ValueError     │
└─────────────────────────┴─────────────────────────┘
    ↓
返回 Path 对象
```

---

### _to_virtual_path 方法

```python
def _to_virtual_path(self, path: Path) -> str:
    """Convert a filesystem path to a virtual path relative to cwd.
    
    Returns:
        Forward-slash relative path prefixed with '/'.
    
    Raises:
        ValueError: If path is outside cwd.
    """
    return "/" + path.resolve().relative_to(self.cwd).as_posix()
```

**用途**：将文件系统路径转换为虚拟路径（用于返回结果）

**示例**：

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

backend._to_virtual_path(Path("/workspace/main.py"))
# → "/main.py"

backend._to_virtual_path(Path("/workspace/src/utils/helper.py"))
# → "/src/utils/helper.py"
```

---

## 五、核心方法详解

### 1. ls - 列出目录

```python
def ls(self, path: str) -> LsResult:
    """List files and directories in the specified directory (non-recursive).
    
    Args:
        path: Absolute directory path to list files from.
    
    Returns:
        List of FileInfo dicts for files and directories.
    """
```

---

#### 实现要点

**非递归列出**：

```python
for child_path in dir_path.iterdir():  # 只列出直接子项，不递归
    is_file = child_path.is_file()
    is_dir = child_path.is_dir()
```

**目录路径带 `/` 后缀**：

```python
if is_dir:
    results.append({
        "path": virt_path + "/",  # 目录路径带 /
        "is_dir": True,
    })
```

**获取文件元数据**：

```python
st = child_path.stat()
results.append({
    "path": virt_path,
    "is_dir": False,
    "size": int(st.st_size),
    "modified_at": datetime.fromtimestamp(st.st_mtime).isoformat(),
})
```

---

#### virtual_mode 行为

```python
# virtual_mode=False：返回绝对路径
backend.ls("/workspace")
# → [{"path": "/workspace/main.py", ...}, {"path": "/workspace/src/", ...}]

# virtual_mode=True：返回虚拟路径
backend.ls("/")
# → [{"path": "/main.py", ...}, {"path": "/src/", ...}]
```

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

result = backend.ls("/")
# 返回：
# [
#     {"path": "/main.py", "is_dir": False, "size": 1024},
#     {"path": "/src/", "is_dir": True, "size": 0},
#     {"path": "/config.json", "is_dir": False, "size": 256},
# ]

result = backend.ls("/src")
# 返回：
# [
#     {"path": "/src/utils.py", "is_dir": False, "size": 512},
#     {"path": "/src/helpers/", "is_dir": True, "size": 0},
# ]
```

---

### 2. read - 读取文件

```python
def read(
    self,
    file_path: str,
    offset: int = 0,    # 起始行号（0-indexed）
    limit: int = 2000,  # 最大行数
) -> ReadResult:
    """Read file content for the requested line range.
    
    Returns:
        ReadResult with raw (unformatted) content.
    """
```

---

#### 实现要点

**使用 os.open 安全打开**：

```python
fd = os.open(resolved_path, os.O_RDONLY | getattr(os, "O_NOFOLLOW", 0))
# O_NOFOLLOW：防止符号链接攻击
```

**二进制文件处理**：

```python
if _get_file_type(file_path) != "text":
    # 图片、音频、视频等二进制文件
    with os.fdopen(fd, "rb") as f:
        raw = f.read()
    encoded = base64.standard_b64encode(raw).decode("ascii")
    return ReadResult(file_data={"content": encoded, "encoding": "base64"})
```

**空文件警告**：

```python
empty_msg = check_empty_content(content)
if empty_msg:
    return ReadResult(file_data={"content": empty_msg, "encoding": "utf-8"})
# 返回：File exists but has empty contents
```

**行范围读取**：

```python
lines = content.splitlines()
start_idx = offset
end_idx = min(start_idx + limit, len(lines))

if start_idx >= len(lines):
    return ReadResult(error=f"Line offset {offset} exceeds file length")

selected_lines = lines[start_idx:end_idx]
return ReadResult(file_data={"content": "\n".join(selected_lines), "encoding": "utf-8"})
```

---

#### 文件类型判断

```python
def _get_file_type(path: str) -> FileType:
    """Classify file by extension."""
    _EXTENSION_TO_FILE_TYPE = {
        ".png": "image", ".jpg": "image", ".gif": "image",
        ".mp4": "video", ".mov": "video",
        ".mp3": "audio", ".wav": "audio",
        ".pdf": "file", ".pptx": "file",
    }
    return _EXTENSION_TO_FILE_TYPE.get(suffix, "text")
```

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 读取整个文件
result = backend.read("/main.py")
# → ReadResult(file_data={"content": "...", "encoding": "utf-8"})

# 读取前 100 行
result = backend.read("/main.py", offset=0, limit=100)

# 读取第 50-60 行
result = backend.read("/main.py", offset=50, limit=10)

# 读取二进制文件（图片）
result = backend.read("/image.png")
# → ReadResult(file_data={"content": "iVBORw0KGgo...", "encoding": "base64"})

# 读取不存在的文件
result = backend.read("/nonexistent.txt")
# → ReadResult(error="File '/nonexistent.txt' not found")

# 偏移超出文件长度
result = backend.read("/small.txt", offset=1000)
# → ReadResult(error="Line offset 1000 exceeds file length (50 lines)")
```

---

### 3. write - 写入文件

```python
def write(self, file_path: str, content: str) -> WriteResult:
    """Create a new file with content.
    
    Returns:
        WriteResult with path on success, or error if file exists.
    """
```

---

#### 实现要点

**检查文件是否存在**：

```python
if resolved_path.exists():
    return WriteResult(
        error=f"Cannot write to {file_path} because it already exists. "
              "Read and then make an edit, or write to a new path."
    )
```

**创建父目录**：

```python
resolved_path.parent.mkdir(parents=True, exist_ok=True)
# 自动创建所有必要的父目录
```

**使用 os.open 安全写入**：

```python
flags = os.O_WRONLY | os.O_CREAT | os.O_TRUNC
if hasattr(os, "O_NOFOLLOW"):
    flags |= os.O_NOFOLLOW
fd = os.open(resolved_path, flags, 0o644)
# 0o644：文件权限（用户读写，其他只读）
# O_NOFOLLOW：防止符号链接攻击
```

---

#### 注意事项

- **write 只用于创建新文件**
- **如果文件已存在，返回错误**
- **修改现有文件应该用 edit 方法**

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 创建新文件
result = backend.write("/new_file.txt", "Hello World")
# → WriteResult(path="/new_file.txt")

# 创建文件（自动创建父目录）
result = backend.write("/src/utils/helper.py", "def helper(): pass")
# → WriteResult(path="/src/utils/helper.py")

# 文件已存在
result = backend.write("/existing.txt", "new content")
# → WriteResult(error="Cannot write to /existing.txt because it already exists")

# 写入失败（权限问题）
result = backend.write("/readonly/file.txt", "content")
# → WriteResult(error="Error writing file '/readonly/file.txt': Permission denied")
```

---

### 4. edit - 编辑文件

```python
def edit(
    self,
    file_path: str,
    old_string: str,
    new_string: str,
    replace_all: bool = False,
) -> EditResult:
    """Edit a file by replacing string occurrences.
    
    Returns:
        EditResult with path and occurrence count on success.
    """
```

---

#### 实现要点

**行结束符标准化**：

```python
# Python 读取文件时，会转换所有换行符为 \n
# 但 old_string/new_string 可能包含 \r\n 或 \r
old_string = old_string.replace("\r\n", "\n").replace("\r", "\n")
new_string = new_string.replace("\r\n", "\n").replace("\r", "\n")
```

**替换验证**：

```python
result = perform_string_replacement(content, old_string, new_string, replace_all)

if isinstance(result, str):
    # 返回错误消息
    return EditResult(error=result)

new_content, occurrences = result
# 成功替换
```

---

#### perform_string_replacement 函数

```python
def perform_string_replacement(
    content: str,
    old_string: str,
    new_string: str,
    replace_all: bool = False,
) -> tuple[str, int] | str:
    """Perform string replacement with occurrence validation."""
    
    occurrences = content.count(old_string)
    
    if occurrences == 0:
        return f"Error: String not found in file: '{old_string}'"
    
    if occurrences > 1 and not replace_all:
        return (
            f"Error: String '{old_string}' appears {occurrences} times. "
            f"Use replace_all=True or provide a more specific string."
        )
    
    new_content = content.replace(old_string, new_string)
    return new_content, occurrences
```

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 替换单个匹配
result = backend.edit(
    "/config.py",
    old_string="DEBUG = False",
    new_string="DEBUG = True"
)
# → EditResult(path="/config.py", occurrences=1)

# 替换所有匹配
result = backend.edit(
    "/utils.py",
    old_string="old_function",
    new_string="new_function",
    replace_all=True
)
# → EditResult(path="/utils.py", occurrences=5)

# 字符串不存在
result = backend.edit("/file.py", "nonexistent", "new")
# → EditResult(error="Error: String not found in file: 'nonexistent'")

# 多个匹配但未指定 replace_all
result = backend.edit("/file.py", "common_pattern", "new")
# → EditResult(error="Error: String 'common_pattern' appears 3 times...")
```

---

### 5. grep - 搜索内容

```python
def grep(
    self,
    pattern: str,
    path: str | None = None,
    glob: str | None = None,
) -> GrepResult:
    """Search for a literal text pattern in files.
    
    Uses ripgrep if available, falling back to Python search.
    
    Args:
        pattern: Literal string to search (NOT regex).
        path: Directory or file path to search.
        glob: Optional glob pattern to filter files.
    
    Returns:
        GrepResult with matches or error.
    """
```

---

#### 双引擎设计

FilesystemBackend 使用两个搜索引擎：

```
grep() 调用
    ↓
尝试使用 ripgrep（_ripgrep_search）
    ↓
┌─────────────┬─────────────┐
│ ripgrep 成功 │ ripgrep 失败 │
│ 返回结果    │ 调用 Python  │
│             │ fallback     │
│             │ (_python_    │
│             │ search)      │
└─────────────┴─────────────┘
    ↓
返回 GrepResult
```

---

#### ripgrep 实现（_ripgrep_search）

```python
def _ripgrep_search(self, pattern: str, base_full: Path, include_glob: str | None):
    """Search using ripgrep with fixed-string (literal) mode."""
    
    # 构建命令
    cmd = ["rg", "--json", "-F"]  # -F：固定字符串（字面搜索）
    if include_glob:
        cmd.extend(["--glob", include_glob])
    cmd.extend(["--", pattern, str(base_full)])
    
    # 执行命令
    proc = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
    
    # 解析 JSON 输出
    for line in proc.stdout.splitlines():
        data = json.loads(line)
        if data.get("type") == "match":
            # 提取匹配信息
            fpath = data["data"]["path"]["text"]
            line_num = data["data"]["line_number"]
            line_text = data["data"]["lines"]["text"].rstrip("\n")
            results[virt_path].append((int(line_num), line_text))
    
    return results
```

**WHY ripgrep**：
- 极快（比 grep 快 10-100 倍）
- 支持 Unicode
- 自动跳过二进制文件
- 支持 glob 过滤
- 输出 JSON（易于解析）

---

#### Python fallback 实现（_python_search）

```python
def _python_search(self, pattern: str, base_full: Path, include_glob: str | None):
    """Fallback search using Python."""
    
    # 编译正则（pattern 已被 re.escape 处理）
    regex = re.compile(pattern)
    
    for fp in root.rglob("*"):
        if not fp.is_file():
            continue
        
        # glob 过滤
        if include_glob:
            rel_path = str(fp.relative_to(root))
            if not wcglob.globmatch(rel_path, include_glob):
                continue
        
        # 文件大小限制
        if fp.stat().st_size > self.max_file_size_bytes:
            continue
        
        # 读取并搜索
        content = fp.read_text()
        for line_num, line in enumerate(content.splitlines(), 1):
            if regex.search(line):
                results[virt_path].append((line_num, line))
    
    return results
```

---

#### ripgrep vs Python fallback

| 特性 | ripgrep | Python fallback |
|------|---------|-----------------|
| **速度** | 极快（100x grep） | 较慢（Python 循环） |
| **大文件** | 无限制 | 受 `max_file_size_bytes` 限制 |
| **依赖** | 需要安装 `rg` | 无需额外依赖 |
| **Unicode** | 自动处理 | 需要处理编码 |
| **二进制文件** | 自动跳过 | 可能抛 UnicodeDecodeError |

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 搜索所有文件中的 "TODO"
result = backend.grep("TODO")
# → GrepResult(matches=[
#     {"path": "/main.py", "line": 10, "text": "# TODO: implement"},
#     {"path": "/utils.py", "line": 25, "text": "# TODO: refactor"},
# ])

# 只搜索 Python 文件
result = backend.grep("TODO", glob="*.py")

# 搜索特定目录
result = backend.grep("TODO", path="/src")

# 搜索不存在的模式
result = backend.grep("nonexistent_pattern")
# → GrepResult(matches=[])
```

---

### 6. glob - 搜索文件

```python
def glob(self, pattern: str, path: str = "/") -> GlobResult:
    """Find files matching a glob pattern.
    
    Args:
        pattern: Glob pattern (e.g., '*.py', '**/*.txt').
        path: Base directory to search from.
    
    Returns:
        GlobResult with matching files or error.
    """
```

---

#### 实现要点

**递归搜索**：

```python
for matched_path in search_path.rglob(pattern):
    # rglob：递归搜索子目录
```

**路径穿越检查**：

```python
if self.virtual_mode and ".." in Path(pattern).parts:
    raise ValueError("Path traversal not allowed in glob pattern")
```

**virtual_mode 路径转换**：

```python
if self.virtual_mode:
    # 检查是否在 cwd 内
    matched_path.resolve().relative_to(self.cwd)
    # 转换为虚拟路径
    virt = self._to_virtual_path(matched_path)
```

---

#### Glob 模式语法

| 模式 | 说明 | 示例 |
|------|------|------|
| `*` | 匹配任意字符（单层） | `*.py` → 当前目录的 .py 文件 |
| `**` | 匹配任意目录（递归） | `**/*.py` → 所有目录的 .py 文件 |
| `?` | 匹配单个字符 | `test?.py` → `test1.py`, `test2.py` |
| `[abc]` | 匹配字符集合 | `test[0-9].py` → `test0.py` 到 `test9.py` |
| `{a,b}` | 匹配多个模式 | `*.{py,js}` → .py 或 .js 文件 |

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 查找所有 Python 文件（当前目录）
result = backend.glob("*.py", path="/")
# → GlobResult(matches=[
#     {"path": "/main.py", "size": 1024},
#     {"path": "/utils.py", "size": 512},
# ])

# 查找所有 Python 文件（递归）
result = backend.glob("**/*.py", path="/")
# → GlobResult(matches=[
#     {"path": "/main.py"},
#     {"path": "/src/utils.py"},
#     {"path": "/tests/test_main.py"},
# ])

# 查找特定目录的文件
result = backend.glob("*.txt", path="/docs")

# 查找多种扩展名
result = backend.glob("*.{py,js,ts}", path="/")
```

---

## 六、批量操作方法

### 1. upload_files - 上传文件

```python
def upload_files(self, files: list[tuple[str, bytes]]) -> list[FileUploadResponse]:
    """Upload multiple files to the filesystem.
    
    Args:
        files: List of (path, content) tuples.
    
    Returns:
        List of FileUploadResponse objects, one per file.
    """
```

---

#### 实现要点

**批量处理**：

```python
responses: list[FileUploadResponse] = []
for path, content in files:
    # 处理每个文件
    responses.append(FileUploadResponse(path=path, error=None))
return responses
```

**二进制写入**：

```python
fd = os.open(resolved_path, flags, 0o644)
with os.fdopen(fd, "wb") as f:  # 二进制模式
    f.write(content)
```

**错误分类**：

```python
error = _map_exception_to_standard_error(exc)
if error is None:
    raise  # 无法分类的错误，直接抛出
responses.append(FileUploadResponse(path=path, error=error))
```

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 上传多个文件
responses = backend.upload_files([
    ("/file1.txt", b"Content 1"),
    ("/file2.txt", b"Content 2"),
    ("/file3.txt", b"Content 3"),
])

# 返回：
# [
#     FileUploadResponse(path="/file1.txt", error=None),
#     FileUploadResponse(path="/file2.txt", error=None),
#     FileUploadResponse(path="/file3.txt", error=None),
# ]

# 上传到只读目录
responses = backend.upload_files([
    ("/readonly/file.txt", b"content"),
])
# → FileUploadResponse(path="/readonly/file.txt", error="permission_denied")
```

---

### 2. download_files - 下载文件

```python
def download_files(self, paths: list[str]) -> list[FileDownloadResponse]:
    """Download multiple files from the filesystem.
    
    Args:
        paths: List of file paths to download.
    
    Returns:
        List of FileDownloadResponse objects, one per path.
    """
```

---

#### 实现要点

**二进制读取**：

```python
fd = os.open(resolved_path, os.O_RDONLY | getattr(os, "O_NOFOLLOW", 0))
with os.fdopen(fd, "rb") as f:  # 二进制模式
    content = f.read()
responses.append(FileDownloadResponse(path=path, content=content, error=None))
```

---

#### 示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 下载多个文件
responses = backend.download_files([
    "/file1.txt",
    "/file2.txt",
    "/image.png",
])

# 返回：
# [
#     FileDownloadResponse(path="/file1.txt", content=b"Content 1", error=None),
#     FileDownloadResponse(path="/file2.txt", content=b"Content 2", error=None),
#     FileDownloadResponse(path="/image.png", content=b"\x89PNG...", error=None),
# ]

# 下载不存在的文件
responses = backend.download_files(["/nonexistent.txt"])
# → FileDownloadResponse(path="/nonexistent.txt", content=None, error="file_not_found")
```

---

## 七、错误处理机制

### _map_exception_to_standard_error 函数

```python
def _map_exception_to_standard_error(exc: Exception) -> FileOperationError | None:
    """Map exception to standardized FileOperationError code.
    
    Classification based on exception type only (stdlib hierarchy).
    Returns None for unrecognized exceptions.
    """
    if isinstance(exc, FileNotFoundError):
        return "file_not_found"
    if isinstance(exc, PermissionError):
        return "permission_denied"
    if isinstance(exc, IsADirectoryError):
        return "is_directory"
    if isinstance(exc, (NotADirectoryError, FileExistsError)):
        return "invalid_path"
    if isinstance(exc, ValueError):
        return "invalid_path"
    return None
```

---

### 异常映射表

| Python 异常 | FileOperationError | 说明 |
|------------|--------------------|------|
| `FileNotFoundError` | `"file_not_found"` | 文件不存在 |
| `PermissionError` | `"permission_denied"` | 权限拒绝 |
| `IsADirectoryError` | `"is_directory"` | 尝试读取目录 |
| `NotADirectoryError` | `"invalid_path"` | 路径不是目录 |
| `FileExistsError` | `"invalid_path"` | 文件已存在 |
| `ValueError` | `"invalid_path"` | 路径格式错误 |

---

### WHY 标准化错误？

**问题**：Python 异常不适合 LLM

```python
# Python 异常（不适合 LLM）
except FileNotFoundError as e:
    return f"Error: {e}"  # → "Error: [Errno 2] No such file or directory: 'file.txt'"
```

**解决**：标准化错误码

```python
# 标准化错误（适合 LLM）
error = _map_exception_to_standard_error(exc)
return FileDownloadResponse(path=path, content=None, error="file_not_found")
```

**好处**：
- LLM 能理解错误含义
- LLM 能采取修正行动
- 跨 Backend 一致性

---

## 八、安全设计分析

### 1. O_NOFOLLOW - 防止符号链接攻击

**什么是符号链接攻击**？

```
攻击者创建符号链接：
ln -s /etc/passwd /workspace/malicious_link.txt
    ↓
Agent 读取 "malicious_link.txt"
    ↓
Backend.read() 通过符号链接读取 /etc/passwd
    ↓
敏感信息泄露
```

**O_NOFOLLOW 防御**：

```python
fd = os.open(resolved_path, os.O_RDONLY | os.O_NOFOLLOW)
# 如果 resolved_path 是符号链接，open() 会失败
```

**效果**：
- 防止通过符号链接访问外部文件
- 防止符号链接攻击

---

### 2. virtual_mode - 路径限制

**限制范围**：

```python
if self.virtual_mode:
    # 1. 禁止路径穿越
    if ".." in vpath or vpath.startswith("~"):
        raise ValueError("Path traversal not allowed")
    
    # 2. 检查是否在 cwd 内
    full.relative_to(self.cwd)  # 如果不在 cwd 内，抛 ValueError
```

**防御效果**：
- 禁止 `..` 路径穿越
- 禁止 `~` 用户目录访问
- 禁止绝对路径逃逸

---

### 3. 安全措施对比

| 安全措施 | virtual_mode=False | virtual_mode=True | O_NOFOLLOW |
|---------|--------------------|--------------------|-----------|
| **路径穿越防御** | ❌ 无 | ✅ 有 | - |
| **绝对路径限制** | ❌ 无 | ✅ 有 | - |
| **符号链接防御** | - | - | ✅ 有 |
| **目录限制** | ❌ 无 | ✅ 有 | - |

**最佳组合**：

```python
backend = FilesystemBackend(
    root_dir="/workspace",
    virtual_mode=True,  # 路径限制
)
# + O_NOFOLLOW（自动启用，防止符号链接）
```

---

### 4. 安全建议

**开发者使用建议**：

| 场景 | 安全配置 | 原因 |
|------|---------|------|
| **本地开发 CLI** | `virtual_mode=True` + HITL | 用户审批敏感操作 |
| **CI/CD** | `virtual_mode=True` + 权限控制 | 限制访问范围 |
| **Web API** | 不使用 FilesystemBackend | 使用 StateBackend/StoreBackend |
| **生产环境** | 使用 SandboxBackend | 需要完整隔离 |

---

## 九、使用示例

### 1. 创建 FilesystemBackend

```python
from deepagents.backends import FilesystemBackend
from pathlib import Path

# 方式 1: 默认配置（当前目录）
backend = FilesystemBackend()

# 方式 2: 指定根目录
backend = FilesystemBackend(root_dir="/workspace")

# 方式 3: 虚拟路径模式（推荐）
backend = FilesystemBackend(
    root_dir="./project",
    virtual_mode=True,
)

# 方式 4: 使用 Path 对象
backend = FilesystemBackend(root_dir=Path.home() / "workspace")

# 方式 5: CI/CD 配置
backend = FilesystemBackend(
    root_dir="/ci/workspace",
    virtual_mode=True,
    max_file_size_mb=50,
)
```

---

### 2. 集成到 Agent

```python
from deepagents import create_deep_agent
from deepagents.backends import FilesystemBackend
from deepagents.middleware.permissions import FilesystemPermission

# 创建 Backend
backend = FilesystemBackend(
    root_dir="./workspace",
    virtual_mode=True,
)

# 配置权限（只允许读取）
permissions = [
    FilesystemPermission(operations=["read"], paths=["/**"]),
    FilesystemPermission(operations=["write"], paths=["/**"], mode="deny"),
]

# 创建 Agent
agent = create_deep_agent(
    backend=backend,
    permissions=permissions,
)

# 运行 Agent
result = agent.invoke({
    "messages": [{"role": "user", "content": "读取 README.md"}]
})
```

---

### 3. 与 CompositeBackend 组合

```python
from deepagents.backends import CompositeBackend, FilesystemBackend, StateBackend, StoreBackend
from langgraph.store.memory import InMemoryStore

# 创建组合 Backend
backend = CompositeBackend(
    default=StateBackend(),  # 默认：临时存储
    routes={
        "/workspace/": FilesystemBackend(
            root_dir="./workspace",
            virtual_mode=True,
        ),  # workspace 路径：真实文件系统
        "/memories/": StoreBackend(
            store=InMemoryStore(),
            namespace=lambda rt: ("memories",),
        ),  # memories 路径：Store 存储
    }
)

# 不同路径自动路由
backend.read("/workspace/main.py")  # → FilesystemBackend
backend.read("/memories/notes.md")  # → StoreBackend
backend.read("/temp/data.txt")      # → StateBackend (default)
```

---

### 4. 错误处理示例

```python
backend = FilesystemBackend(root_dir="/workspace", virtual_mode=True)

# 读取文件
result = backend.read("/config.json")
if result.error:
    print(f"Error: {result.error}")
    # 根据错误类型采取行动
    if result.error == "file_not_found":
        # 尝试其他路径
        result = backend.read("/backup/config.json")
else:
    content = result.file_data["content"]
    print(f"Content: {content}")

# 写入文件
result = backend.write("/new_file.txt", "content")
if result.error:
    if "already exists" in result.error:
        # 文件已存在，使用 edit
        result = backend.edit("/new_file.txt", "old", "new")
    else:
        print(f"Write error: {result.error}")
else:
    print(f"Created: {result.path}")

# 批量下载
responses = backend.download_files(["/file1.txt", "/file2.txt", "/nonexistent.txt"])
for response in responses:
    if response.error:
        print(f"Failed to download {response.path}: {response.error}")
    else:
        print(f"Downloaded {response.path}: {len(response.content)} bytes")
```

---

## 十、关键设计点总结

| 设计点 | 说明 | WHY |
|--------|------|-----|
| **O_NOFOLLOW** | 打开文件时禁用符号链接 | 防止符号链接攻击 |
| **virtual_mode** | 限制路径访问范围 | 防止路径穿越和逃逸 |
| **双引擎 grep** | ripgrep + Python fallback | 优先速度，fallback 可靠性 |
| **行结束符标准化** | `\r\n` → `\n` | 跨平台兼容性 |
| **write 只创建** | 不覆盖现有文件 | 防止意外覆盖 |
| **标准化错误码** | 异常 → `FileOperationError` | LLM 可理解 |
| **文件大小限制** | `max_file_size_mb` | grep fallback 性能保护 |
| **自动创建父目录** | `mkdir(parents=True)` | 简化写入操作 |
| **文件类型判断** | `_get_file_type()` | 二进制文件 base64 处理 |

---

## 文件位置参考

| 文件 | 说明 |
|------|------|
| `filesystem.py` | FilesystemBackend 实现（本文档解析） |
| `protocol.py` | Backend Protocol 定义 |
| `utils.py` | Backend 工具函数 |
| `state.py` | StateBackend 实现 |
| `store.py` | StoreBackend 实现 |
| `composite.py` | CompositeBackend 实现 |

---

## 一句话总结

`filesystem.py` 实现了直接访问本地文件系统的 Backend，通过 `virtual_mode` 和 `O_NOFOLLOW` 提供安全防护，使用 ripgrep 和 Python fallback 双引擎实现高效搜索，适合本地开发 CLI 和 CI/CD 场景，但不适合 Web API 和生产环境。

**核心价值**：
- 直接文件系统访问 → 真实文件操作
- 安全设计 → 路径限制 + 符号链接防御
- 高效搜索 → ripgrep + Python fallback
- 标准化错误 → LLM 可理解

**安全警告**：
- ❌ 不适合 Web API 和生产环境
- ✅ 推荐配合 HITL middleware 使用
- ✅ 推荐配置权限控制
- ✅ 推荐使用 `virtual_mode=True`

---

**文档生成时间**：2026-04-13
**适用版本**：Deep Agents v0.5.2