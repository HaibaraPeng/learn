# Deep Agents Backend Protocol 详细解析

> 本文档深入解析 `libs/deepagents/deepagents/backends/protocol.py` 文件
> 
> **阅读时间**：15-20 分钟
> 
> **适用人群**：想要理解 Backend 抽象设计的开发者

---

## 目录

1. [核心概念：什么是 Protocol？](#一核心概念什么是-protocol)
2. [数据类型定义](#二数据类型定义)
3. [BackendProtocol 类（核心接口）](#三backendprotocol-类核心接口)
4. [SandboxBackendProtocol（扩展协议）](#四sandboxbackendprotocol扩展协议)
5. [实际实现类](#五实际实现类)
6. [设计模式和原则](#六设计模式和原则)
7. [使用示例](#七使用示例)
8. [关键设计点总结](#八关键设计点总结)

---

## 一、核心概念：什么是 Protocol？

### WHY 需要 Protocol？

**问题**：Agent 需要读写文件，但文件可能存储在不同位置：
- 本地文件系统
- LangGraph 状态
- LangGraph Store
- 远程沙箱（容器、VM）
- 第三方服务

**解决方案**：定义一个 **统一的接口**（Protocol），让所有 Backend 实现都遵循相同的契约。

```python
# 不同 Backend，相同接口
backend = StateBackend()      # 文件存储在 LangGraph 状态
backend = FilesystemBackend()  # 文件存储在本地磁盘
backend = StoreBackend()       # 文件存储在 LangGraph Store
backend = SandboxBackend()     # 文件存储在远程沙箱

# 统一的调用方式
result = backend.read("/app/config.json")
backend.write("/app/output.txt", "content")
```

### Protocol vs Abstract Base Class

**Protocol（协议）**：
- 定义接口契约（方法签名）
- 不强制继承（鸭子类型）
- 允许部分实现（不强制所有方法）

**Abstract Base Class（抽象基类）**：
- 强制继承
- 强制实现所有抽象方法
- 更严格的约束

**WHY 选择 Protocol**：
- Deep Agents 的 Backend 有不同的功能需求
- 有些 Backend 只需要文件操作（StateBackend）
- 有些 Backend 需要命令执行（SandboxBackend）
- Protocol 允许灵活的功能组合

---

## 二、数据类型定义

### 1. 文件格式版本

```python
FileFormat = Literal["v1", "v2"]
```

| 版本 | content 格式 | encoding 字段 | 说明 |
|------|-------------|--------------|------|
| `v1` (旧) | `list[str]` (按换行分割) | 无 | 遗留格式，已废弃 |
| `v2` (新) | `str` (完整字符串) | 有 (`"utf-8"` 或 `"base64"` | 当前推荐格式 |

**WHY v2 更好**：
- 支持二进制文件（base64 编码）
- 不破坏长行（v1 会按 `\n` 分割）
- 更简单的数据结构（直接是字符串）
- 明确的 encoding 标记

**迁移示例**：

```python
# v1 格式（旧）
{
    "content": ["line 1", "line 2", "line 3"],  # 分割成数组
    "created_at": "2024-01-01",
    "modified_at": "2024-01-01"
}

# v2 格式（新）
{
    "content": "line 1\nline 2\nline 3",  # 完整字符串
    "encoding": "utf-8",
    "created_at": "2024-01-01",
    "modified_at": "2024-01-01"
}
```

---

### 2. 错误类型

```python
FileOperationError = Literal[
    "file_not_found",     # 文件不存在
    "permission_denied",  # 权限拒绝
    "is_directory",       # 尝试读取目录
    "invalid_path",       # 路径格式错误
]
```

**WHY 标准化错误**：

1. **LLM 可理解**：Agent 能理解这些错误并采取修正行动
2. **可恢复**：这些错误 Agent 可以尝试修复
3. **一致性**：不同 Backend 返回相同的错误码

**示例场景**：

```
LLM 调用 read_file("/config.json")
    ↓
Backend 返回 error="file_not_found"
    ↓
LLM 理解：文件不存在
    ↓
LLM 行动：尝试 ls() 找到正确路径
    ↓
LLM 再次调用 read_file("/app/config.json")
```

---

### 3. 操作结果类型

所有操作返回统一的结果对象：

#### ReadResult

```python
@dataclass
class ReadResult:
    error: str | None = None       # 失败时的错误消息
    file_data: FileData | None = None  # 成功时的文件数据
```

**成功示例**：

```python
ReadResult(
    error=None,
    file_data={
        "content": "Hello World",
        "encoding": "utf-8",
        "created_at": "2024-01-01T00:00:00Z",
        "modified_at": "2024-01-01T00:00:00Z"
    }
)
```

**失败示例**：

```python
ReadResult(
    error="file_not_found",
    file_data=None
)
```

---

#### WriteResult

```python
@dataclass
class WriteResult:
    error: str | None = None
    path: str | None = None        # 成功写入的路径
```

**成功示例**：

```python
WriteResult(
    error=None,
    path="/app/output.txt"
)
```

---

#### LsResult / GrepResult / GlobResult

```python
@dataclass
class LsResult:
    error: str | None = None
    entries: list[FileInfo] | None = None

@dataclass
class GrepResult:
    error: str | None = None
    matches: list[GrepMatch] | None = None

@dataclass
class GlobResult:
    error: str | None = None
    matches: list[FileInfo] | None = None
```

---

### 4. 文件数据结构

```python
class FileData(TypedDict):
    content: str       # 文件内容（utf-8 文本 或 base64 二进制）
    encoding: str      # 编码类型："utf-8" 或 "base64"
    created_at: NotRequired[str]    # 创建时间（ISO 8601），可选
    modified_at: NotRequired[str]   # 修改时间（ISO 8601），可选
```

**文本文件示例**：

```python
{
    "content": "print('hello')",
    "encoding": "utf-8",
    "created_at": "2024-01-01T00:00:00Z",
    "modified_at": "2024-01-01T00:00:00Z"
}
```

**二进制文件示例（图片）**：

```python
{
    "content": "iVBORw0KGgoAAAANSUhEUgAA...",  # base64 编码
    "encoding": "base64",
    "created_at": "2024-01-01T00:00:00Z",
    "modified_at": "2024-01-01T00:00:00Z"
}
```

---

### 5. 其他辅助类型

#### FileInfo（文件列表条目）

```python
class FileInfo(TypedDict):
    path: str                      # 文件路径（必需）
    is_dir: NotRequired[bool]      # 是否是目录
    size: NotRequired[int]         # 文件大小（字节）
    modified_at: NotRequired[str]  # 修改时间
```

---

#### GrepMatch（搜索匹配项）

```python
class GrepMatch(TypedDict):
    path: str    # 文件路径
    line: int    # 行号（1-indexed）
    text: str    # 匹配的文本内容
```

---

## 三、BackendProtocol 类（核心接口）

### 类定义

```python
class BackendProtocol(abc.ABC):  # noqa: B024
    """Protocol for pluggable memory backends."""
```

**关键点**：
- 使用 `abc.ABC` 作为基类
- **没有 `@abstractmethod` 装饰器**（这是关键！）
- 允许子类只实现部分方法

**WHY 不强制所有方法**：
- 不同的 Backend 有不同的功能需求
- StateBackend 只需要基本文件操作
- SandboxBackend 需要额外的命令执行
- 部分实现更灵活

---

### 核心方法一览

#### 文件操作

| 方法 | 说明 | 参数 | 返回类型 |
|------|------|------|---------|
| `ls(path)` | 列出目录内容 | `path: str` | `LsResult` |
| `read(file_path, offset, limit)` | 读取文件 | `file_path: str, offset=0, limit=2000` | `ReadResult` |
| `write(file_path, content)` | 写入新文件 | `file_path: str, content: str` | `WriteResult` |
| `edit(file_path, old, new)` | 编辑文件 | `file_path, old_string, new_string, replace_all=False` | `EditResult` |
| `grep(pattern, path, glob)` | 搜索内容 | `pattern, path=None, glob=None` | `GrepResult` |
| `glob(pattern, path)` | 搜索文件 | `pattern, path="/"` | `GlobResult` |

---

#### 批量操作

| 方法 | 说明 | 参数 |
|------|------|------|
| `upload_files(files)` | 上传多个文件 | `files: list[tuple[str, bytes]]` |
| `download_files(paths)` | 下载多个文件 | `paths: list[str]` |

---

#### 异步版本

每个方法都有对应的异步版本（`a` 前缀）：

```python
als(path)          # async ls
aread(file_path)   # async read
awrite(file_path)  # async write
aedit(file_path)   # async edit
agrep(pattern)     # async grep
aglob(pattern)     # async glob
aupload_files(files)   # async upload_files
adownload_files(paths) # async download_files
```

---

### 方法详解

#### 1. ls - 列出目录

```python
def ls(self, path: str) -> LsResult:
    """List all files in a directory with metadata.
    
    Args:
        path: Absolute path to the directory. Must start with '/'.
    
    Returns:
        LsResult with directory entries or error.
    """
```

**示例**：

```python
result = backend.ls("/workspace")
if result.error:
    print(f"Error: {result.error}")
else:
    for entry in result.entries:
        print(f"{entry['path']} - {entry['is_dir']}")
```

---

#### 2. read - 读取文件

```python
def read(
    self,
    file_path: str,
    offset: int = 0,    # 起始行号（0-indexed）
    limit: int = 2000,  # 最大行数
) -> ReadResult:
    """Read file content with line numbers.
    
    Args:
        file_path: Absolute path. Must start with '/'.
        offset: Line number to start reading from (0-indexed).
        limit: Maximum number of lines to read.
    
    Returns:
        ReadResult with file data formatted with line numbers.
    """
```

**示例**：

```python
# 读取整个文件
result = backend.read("/app/main.py")

# 读取前 100 行
result = backend.read("/app/main.py", offset=0, limit=100)

# 读取第 50-60 行
result = backend.read("/app/main.py", offset=50, limit=10)
```

---

#### 3. write - 写入文件

```python
def write(self, file_path: str, content: str) -> WriteResult:
    """Write content to a new file, error if file exists.
    
    Args:
        file_path: Absolute path where file should be created.
        content: String content to write.
    
    Returns:
        WriteResult
    """
```

**注意**：`write` 只用于创建新文件，如果文件已存在会报错。

**示例**：

```python
result = backend.write("/app/new_file.txt", "Hello World")
if result.error:
    print(f"Error: {result.error}")  # 可能是 "file_exists"
else:
    print(f"Created: {result.path}")
```

---

#### 4. edit - 编辑文件

```python
def edit(
    self,
    file_path: str,
    old_string: str,
    new_string: str,
    replace_all: bool = False,
) -> EditResult:
    """Perform exact string replacements in existing file.
    
    Args:
        file_path: Absolute path to edit.
        old_string: Exact string to search for.
        new_string: String to replace with.
        replace_all: If True, replace all occurrences.
                     If False, old_string must be unique.
    
    Returns:
        EditResult with path and occurrences count.
    """
```

**示例**：

```python
# 替换单个匹配
result = backend.edit(
    "/app/config.py",
    old_string="DEBUG = False",
    new_string="DEBUG = True"
)

# 替换所有匹配
result = backend.edit(
    "/app/utils.py",
    old_string="old_function",
    new_string="new_function",
    replace_all=True
)
```

---

#### 5. grep - 搜索内容

```python
def grep(
    self,
    pattern: str,       # 搜索字符串（字面匹配，不是正则）
    path: str | None = None,    # 搜索目录
    glob: str | None = None,    # 文件过滤
) -> GrepResult:
    """Search for a literal text pattern in files.
    
    Args:
        pattern: Literal string to search (NOT regex).
        path: Optional directory path.
        glob: Optional glob pattern to filter files.
    
    Returns:
        GrepResult with matches or error.
    """
```

**示例**：

```python
# 搜索所有文件中的 "TODO"
result = backend.grep("TODO")

# 只搜索 Python 文件
result = backend.grep("TODO", glob="*.py")

# 搜索特定目录
result = backend.grep("TODO", path="/workspace/src")
```

---

#### 6. glob - 搜索文件

```python
def glob(self, pattern: str, path: str = "/") -> GlobResult:
    """Find files matching a glob pattern.
    
    Args:
        pattern: Glob pattern with wildcards.
        path: Base directory to search from.
    
    Returns:
        GlobResult with matching files or error.
    """
```

**Glob 模式语法**：

| 符号 | 说明 | 示例 |
|------|------|------|
| `*` | 匹配任意字符（单层） | `*.py` 匹配当前目录的 Python 文件 |
| `**` | 匹配任意目录（递归） | `**/*.py` 匹配所有目录的 Python 文件 |
| `?` | 匹配单个字符 | `test?.py` 匹配 `test1.py`、`test2.py` |
| `[abc]` | 匹配字符集合 | `test[0-9].py` 匹配 `test0.py` 到 `test9.py` |

**示例**：

```python
# 查找所有 Python 文件
result = backend.glob("**/*.py")

# 查找特定目录的配置文件
result = backend.glob("*.json", path="/config")
```

---

### 异步方法实现

所有异步方法都使用 `asyncio.to_thread()` 包装同步方法：

```python
async def aread(self, file_path: str, offset=0, limit=2000) -> ReadResult:
    """Async version of read."""
    return await asyncio.to_thread(self.read, file_path, offset, limit)
```

**WHY 这样设计**：
- 同步方法实现简单（直接操作）
- 异步方法自动包装（避免重复实现）
- 用户可根据场景选择同步/异步
- 在异步环境中不阻塞主线程

---

### 废弃方法处理

Protocol 保留了一些旧方法名，但发出废弃警告：

```python
def ls_info(self, path: str) -> list[FileInfo]:
    """List all files in a directory.
    
    !!! warning "Deprecated"
        Use `ls` instead.
    """
    warnings.warn(
        "`ls_info` is deprecated; use `ls` instead.",
        DeprecationWarning,
    )
    result = self.ls(path)
    return result.entries or []
```

**废弃方法列表**：

| 旧方法 | 新方法 | 说明 |
|--------|--------|------|
| `ls_info` | `ls` | 返回类型改变 |
| `glob_info` | `glob` | 返回类型改变 |
| `grep_raw` | `grep` | 返回类型改变 |

---

## 四、SandboxBackendProtocol（扩展协议）

### 定义

```python
class SandboxBackendProtocol(BackendProtocol):
    """Extension that adds shell command execution.
    
    Designed for backends running in isolated environments
    (containers, VMs, remote hosts).
    """
```

**WHY 需要扩展**：
- 普通文件操作不足以满足 Agent 需求
- Agent 需要执行 shell 命令：
  - 编译代码：`gcc main.c`
  - 运行测试：`pytest tests/`
  - 安装依赖：`pip install package`
  - 查看进程：`ps aux`
- Sandbox 环境（容器、VM）支持命令执行

---

### 新增方法

#### 1. id 属性

```python
@property
def id(self) -> str:
    """Unique identifier for the sandbox backend instance."""
    raise NotImplementedError
```

**用途**：标识 Sandbox 实例，用于：
- 日志追踪
- 多 Sandbox 管理
- 调试和监控

---

#### 2. execute 方法

```python
def execute(
    self,
    command: str,
    *,
    timeout: int | None = None,
) -> ExecuteResponse:
    """Execute a shell command in the sandbox environment.
    
    Args:
        command: Full shell command string to execute.
        timeout: Maximum time in seconds to wait.
                 If None, uses backend's default timeout.
    
    Returns:
        ExecuteResponse with output, exit_code, truncated flag.
    """
```

---

### ExecuteResponse 结构

```python
@dataclass
class ExecuteResponse:
    output: str          # stdout + stderr 合并输出
    exit_code: int       # 退出码（0=成功，非零=失败）
    truncated: bool      # 输出是否被截断（超出限制）
```

**示例**：

```python
response = backend.execute("python script.py")
print(f"Output: {response.output}")
print(f"Exit code: {response.exit_code}")
print(f"Truncated: {response.truncated}")

if response.exit_code == 0:
    print("Command succeeded")
else:
    print(f"Command failed with code {response.exit_code}")
```

---

### timeout 参数检查

```python
@lru_cache(maxsize=128)
def execute_accepts_timeout(cls: type[SandboxBackendProtocol]) -> bool:
    """Check if backend's execute accepts timeout kwarg.
    
    Older backend packages may not accept the timeout keyword.
    Results are cached to avoid repeated introspection overhead.
    """
    try:
        sig = inspect.signature(cls.execute)
        return "timeout" in sig.parameters
    except (ValueError, TypeError):
        logger.warning("Could not inspect signature")
        return False
```

**WHY 需要检查**：
- 旧的 Backend SDK 可能不支持 `timeout` 参数
- 避免传入不支持的参数导致错误
- 使用 `@lru_cache` 缓存结果（避免重复检查）

---

### SandboxBackend 基类

Deep Agents 提供了一个 `SandboxBackend` 基类，它实现了所有文件操作，只需实现 `execute()`：

```python
# sandbox.py（简化）
class SandboxBackend(SandboxBackendProtocol):
    """Base class for sandbox backends.
    
    Implements all file operations by delegating to execute().
    """
    
    def read(self, file_path: str) -> ReadResult:
        # 通过 execute() 实现读取
        response = self.execute(f"cat {file_path}")
        if response.exit_code != 0:
            return ReadResult(error=response.output)
        return ReadResult(file_data={"content": response.output, "encoding": "utf-8"})
    
    def write(self, file_path: str, content: str) -> WriteResult:
        # 通过 execute() 实现写入
        response = self.execute(f"echo '{content}' > {file_path}")
        if response.exit_code != 0:
            return WriteResult(error=response.output)
        return WriteResult(path=file_path)
```

**好处**：
- Partner 只需实现 `execute()` 方法
- 所有文件操作自动可用
- 减少重复代码

---

## 五、实际实现类

### Backend 类型关系图

```
BackendProtocol (协议)
    ↓
├── StateBackend
│       文件存储在 LangGraph 状态（临时）
│       使用 CONFIG_KEY_READ / CONFIG_KEY_SEND
│
├── FilesystemBackend  
│       文件存储在本地文件系统
│       直接读写磁盘
│
├── StoreBackend
│       文件存储在 LangGraph Store
│       使用 BaseStore API
│
├── CompositeBackend
│       组合多个 Backend（路由）
│       根据路径选择不同 Backend
│
SandboxBackendProtocol (扩展协议)
    ↓
├── SandboxBackend（基类）
│       使用 execute() 实现所有文件操作
│
└── [Partners]
    ├── DaytonaSandbox (libs/partners/daytona)
    ├── ModalSandbox   (libs/partners/modal)
    ├── RunloopSandbox (libs/partners/runloop)
    └── ...其他 Partner Backend
```

---

### StateBackend 详解

**文件位置**：`libs/deepagents/deepagents/backends/state.py`

**特点**：
- 文件存储在 Agent 状态中（内存）
- 生命周期：单次对话内有效
- 不持久化到磁盘
- 通过 LangGraph checkpoint 持久化

**核心实现**：

```python
class StateBackend(BackendProtocol):
    """Backend that stores files in agent state (ephemeral)."""
    
    def read(self, file_path: str) -> ReadResult:
        # 从 LangGraph 状态读取文件
        state = self._get_state()
        files = state.get("files", {})
        if file_path not in files:
            return ReadResult(error="file_not_found")
        return ReadResult(file_data=files[file_path])
    
    def write(self, file_path: str, content: str) -> WriteResult:
        # 写入到 LangGraph 状态
        file_data = create_file_data(content)
        # 使用 CONFIG_KEY_SEND 发送状态更新
        self._send_state_update({"files": {file_path: file_data}})
        return WriteResult(path=file_path)
```

**使用场景**：
- 临时文件处理
- 不需要持久化的场景
- 多轮对话中传递文件

---

### FilesystemBackend 详解

**文件位置**：`libs/deepagents/deepagents/backends/filesystem.py`

**特点**：
- 直接访问本地文件系统
- 持久化到磁盘
- 有安全风险（需要权限控制）
- 支持 virtual_mode（虚拟路径）

**核心实现**：

```python
class FilesystemBackend(BackendProtocol):
    """Backend that reads/writes files directly from filesystem."""
    
    def read(self, file_path: str) -> ReadResult:
        # 直接读取本地文件
        path = self._resolve_path(file_path)  # 处理 root_dir 和 virtual_mode
        if not path.exists():
            return ReadResult(error="file_not_found")
        content = path.read_text()
        return ReadResult(file_data=create_file_data(content))
    
    def write(self, file_path: str, content: str) -> WriteResult:
        # 直接写入本地文件
        path = self._resolve_path(file_path)
        if path.exists():
            return WriteResult(error="file_exists")
        path.write_text(content)
        return WriteResult(path=str(path))
```

**安全警告**：
- Agent 可以读取任何可访问的文件（包括 `.env`、密钥）
- 需要配合 PermissionMiddleware 控制权限
- 推荐使用 Human-in-the-Loop 审批敏感操作

---

### StoreBackend 详解

**文件位置**：`libs/deepagents/deepagents/backends/store.py`

**特点**：
- 文件存储在 LangGraph Store
- 跨对话线程共享
- 使用 BaseStore API
- 支持命名空间隔离

**核心实现**：

```python
class StoreBackend(BackendProtocol):
    """Backend that stores files in LangGraph Store."""
    
    def __init__(self, store: BaseStore, namespace: Callable):
        self.store = store
        self.namespace = namespace  # 例如 lambda rt: ("filesystem",)
    
    def read(self, file_path: str) -> ReadResult:
        # 从 Store 读取
        item = self.store.get(self.namespace(runtime), file_path)
        if item is None:
            return ReadResult(error="file_not_found")
        return ReadResult(file_data=item.value)
    
    def write(self, file_path: str, content: str) -> WriteResult:
        # 写入到 Store
        self.store.put(
            self.namespace(runtime),
            file_path,
            create_file_data(content)
        )
        return WriteResult(path=file_path)
```

---

### CompositeBackend 详解

**文件位置**：`libs/deepagents/deepagents/backends/composite.py`

**特点**：
- 组合多个 Backend
- 根据路径路由到不同 Backend
- 支持路径前缀匹配

**示例**：

```python
# 组合 Backend
backend = CompositeBackend(
    default=StateBackend(),           # 默认 Backend
    routes={
        "/memories/": StoreBackend(),  # /memories/ 路径使用 StoreBackend
        "/workspace/": FilesystemBackend(),  # /workspace/ 使用文件系统
    }
)

# 读取不同路径会路由到不同 Backend
backend.read("/app/config.json")       # → StateBackend（default）
backend.read("/memories/notes.md")     # → StoreBackend
backend.read("/workspace/main.py")     # → FilesystemBackend
```

---

### Partner Backend 示例

**Daytona Backend**（`libs/partners/daytona`）：

```python
class DaytonaSandbox(SandboxBackendProtocol):
    """Daytona sandbox backend."""
    
    def __init__(self, sandbox_id: str, client: DaytonaClient):
        self._id = sandbox_id
        self.client = client
    
    @property
    def id(self) -> str:
        return self._id
    
    def execute(self, command: str, timeout=None) -> ExecuteResponse:
        # 调用 Daytona API 执行命令
        result = self.client.execute(self._id, command, timeout=timeout)
        return ExecuteResponse(
            output=result.stdout + result.stderr,
            exit_code=result.exit_code,
            truncated=result.truncated
        )
```

---

## 六、设计模式和原则

### 1. Protocol Pattern（协议模式）

**定义**：定义接口契约，不强制继承

```python
# ✅ 正确：实现 Protocol
class MyBackend(BackendProtocol):
    def read(self, file_path: str) -> ReadResult:
        return ReadResult(file_data={"content": "hello", "encoding": "utf-8"})
    
    def write(self, file_path: str, content: str) -> WriteResult:
        return WriteResult(path=file_path)

# ✅ 也正确：鸭子类型（不继承，但实现所有方法）
class DuckBackend:  # 不继承 BackendProtocol
    def read(self, file_path: str) -> ReadResult:
        return ReadResult(file_data={"content": "world"})
    
    def write(self, file_path: str, content: str) -> WriteResult:
        return WriteResult(path=file_path)
```

**WHY 这样设计**：
- 灵活性：可以选择继承或不继承
- 兼容性：第三方实现不需要导入 Protocol
- 部分实现：可以只实现需要的方法

---

### 2. Result Pattern（结果模式）

**定义**：所有操作返回统一结果对象

```python
# ✅ 正确：返回 Result 对象
result = backend.read("/file.txt")
if result.error:
    print(f"Error: {result.error}")
else:
    print(f"Content: {result.file_data['content']}")

# ❌ 错误：返回裸数据或抛异常
def read_bad(file_path):
    try:
        with open(file_path) as f:
            return f.read()  # 不统一，无法区分错误
    except FileNotFoundError:
        raise  # 抛异常，LLM 无法理解
```

**WHY Result Pattern 更好**：
- 统一的错误处理方式
- LLM 可以理解并处理错误
- 不需要 try-catch（简化调用）
- 明确的成功/失败状态

---

### 3. Async Wrapper Pattern（异步包装模式）

**定义**：同步方法 + 异步包装

```python
# 同步实现（核心逻辑）
def read(self, file_path: str) -> ReadResult:
    # 简单的同步实现
    content = open(file_path).read()
    return ReadResult(file_data={"content": content, "encoding": "utf-8"})

# 异步包装（自动生成）
async def aread(self, file_path: str) -> ReadResult:
    # 使用 asyncio.to_thread 包装同步方法
    return await asyncio.to_thread(self.read, file_path)
```

**WHY 这样设计**：
- 同步方法简单实现（不需要 async/await）
- 异步方法自动包装（避免重复实现逻辑）
- 用户可根据场景选择同步/异步
- 在异步环境中不阻塞主线程

---

### 4. Deprecation Pattern（废弃模式）

**定义**：保留旧方法，但发出警告

```python
def ls_info(self, path: str) -> list[FileInfo]:
    """List all files in a directory.
    
    !!! warning "Deprecated"
        Use `ls` instead.
    """
    # 发出废弃警告
    warnings.warn(
        "`ls_info` is deprecated; use `ls` instead.",
        DeprecationWarning,
        stacklevel=2,
    )
    # 调用新方法
    result = self.ls(path)
    return result.entries or []
```

**WHY 需要废弃模式**：
- API 演进需要改名（`ls_info` → `ls`）
- 保留旧 API 兼容旧代码
- 通过警告引导用户迁移
- 给用户足够的迁移时间

---

### 5. Extension Protocol Pattern（扩展协议模式）

**定义**：基础协议 + 扩展协议

```python
# 基础协议：文件操作
class BackendProtocol(abc.ABC):
    def read(self, file_path: str) -> ReadResult: ...
    def write(self, file_path: str, content: str) -> WriteResult: ...

# 扩展协议：添加命令执行
class SandboxBackendProtocol(BackendProtocol):
    def execute(self, command: str) -> ExecuteResponse: ...
```

**WHY 这样设计**：
- 功能分层：基础功能 + 扩展功能
- 不强制：Backend 可以只实现基础协议
- 可组合：SandboxBackend 同时具有文件操作和命令执行
- 清晰的职责边界

---

## 七、使用示例

### 1. 创建 Agent 并选择 Backend

```python
from deepagents import create_deep_agent
from deepagents.backends import FilesystemBackend, StateBackend

# 方式 1: 本地文件系统（持久化）
backend = FilesystemBackend(
    root_dir="./workspace",
    virtual_mode=True  # 启用虚拟路径，限制在 root_dir
)
agent = create_deep_agent(backend=backend)

# 方式 2: 状态存储（临时）
backend = StateBackend()
agent = create_deep_agent(backend=backend)

# 方式 3: LangGraph Store（跨线程共享）
from langgraph.store.memory import InMemoryStore
backend = StoreBackend(
    store=InMemoryStore(),
    namespace=lambda rt: ("filesystem",)
)
agent = create_deep_agent(backend=backend)

# 方式 4: 组合 Backend（路由）
backend = CompositeBackend(
    default=StateBackend(),
    routes={
        "/memories/": StoreBackend(),
        "/workspace/": FilesystemBackend(),
    }
)
agent = create_deep_agent(backend=backend)

# 方式 5: 远程沙箱（隔离）
from langchain_daytona import DaytonaSandbox
backend = DaytonaSandbox(...)
agent = create_deep_agent(backend=backend)
```

---

### 2. Middleware 如何使用 Backend

```python
from langchain.agents.middleware.types import AgentMiddleware, ModelRequest, ModelResponse

class FilesystemMiddleware(AgentMiddleware):
    def __init__(self, backend: BackendProtocol):
        self.backend = backend
    
    def wrap_model_call(
        self,
        request: ModelRequest,
        handler: Callable,
    ) -> ModelResponse:
        # Middleware 可以直接使用 Backend
        # 例如：检查文件是否存在
        result = self.backend.read("/config.json")
        if result.error:
            # 文件不存在，注入提示到 system message
            system_prompt = "Note: config.json not found. Create it first."
            request = request.override(system_message=...)
        
        return handler(request)
    
    def wrap_tool_call(self, request, handler):
        # 工具调用也可以使用 Backend
        tool_name = request.tool_call["name"]
        if tool_name == "read_file":
            file_path = request.tool_call["args"]["file_path"]
            result = self.backend.read(file_path)
            # 返回文件内容或错误
            return ToolMessage(content=result.file_data["content"] or result.error)
        return handler(request)
```

---

### 3. 自定义 Backend 实现

```python
from deepagents.backends.protocol import BackendProtocol, ReadResult, WriteResult

class MyCustomBackend(BackendProtocol):
    """Custom backend that stores files in Redis."""
    
    def __init__(self, redis_client):
        self.redis = redis_client
    
    def read(self, file_path: str, offset=0, limit=2000) -> ReadResult:
        # 从 Redis 读取
        content = self.redis.get(f"files:{file_path}")
        if content is None:
            return ReadResult(error="file_not_found")
        return ReadResult(file_data={
            "content": content.decode("utf-8"),
            "encoding": "utf-8",
            "created_at": "...",
            "modified_at": "...",
        })
    
    def write(self, file_path: str, content: str) -> WriteResult:
        # 写入 Redis
        self.redis.set(f"files:{file_path}", content.encode("utf-8"))
        return WriteResult(path=file_path)

# 使用自定义 Backend
backend = MyCustomBackend(redis_client=redis)
agent = create_deep_agent(backend=backend)
```

---

### 4. 异步使用 Backend

```python
import asyncio

async def process_files():
    backend = StateBackend()
    
    # 异步读取
    result = await backend.aread("/config.json")
    if result.error:
        print(f"Error: {result.error}")
        return
    
    content = result.file_data["content"]
    
    # 处理内容
    processed = process_content(content)
    
    # 异步写入
    write_result = await backend.awrite("/output.txt", processed)
    if write_result.error:
        print(f"Write error: {write_result.error}")

# 运行异步任务
asyncio.run(process_files())
```

---

## 八、关键设计点总结

| 设计点 | 说明 | WHY |
|--------|------|-----|
| **Protocol 不用 abstractmethod** | 允许部分实现 | 灵活性：不同 Backend 功能需求不同 |
| **Result 对象统一返回** | error + data | 错误处理标准化，LLM 可理解 |
| **异步方法包装同步** | `asyncio.to_thread` | 减少重复代码，用户可选择同步/异步 |
| **废弃方法保留** | 警告 + 重定向 | API 兼容性，平滑迁移 |
| **Sandbox 扩展 Protocol** | 添加 execute | 功能分层：文件操作 + 命令执行 |
| **FileFormat v1→v2** | content: list[str] → str | 支持二进制，不破坏长行 |
| **标准化错误码** | file_not_found 等 | LLM 可理解并采取修正行动 |
| **CompositeBackend 路由** | 路径前缀匹配 Backend | 组合多个 Backend，灵活配置 |

---

## 文件位置参考

| 文件 | 说明 |
|------|------|
| `protocol.py` | Protocol 定义（本文档解析） |
| `state.py` | StateBackend 实现 |
| `filesystem.py` | FilesystemBackend 实现 |
| `store.py` | StoreBackend 实现 |
| `composite.py` | CompositeBackend 实现 |
| `sandbox.py` | SandboxBackend 基类 |
| `utils.py` | Backend 工具函数 |

---

## 一句话总结

`protocol.py` 定义了 Backend 的统一接口，让 Agent 能在不同存储环境（本地、状态、沙箱）中使用相同的方法读写文件，实现了 **存储抽象** 和 **可插拔 Backend**。

**核心价值**：
- 统一接口 → 简化 Agent 开发
- Result Pattern → LLM 可理解的错误处理
- Protocol Pattern → 灵活的 Backend 实现
- 扩展协议 → Sandbox 命令执行能力

---

**文档生成时间**：2026-04-13
**适用版本**：Deep Agents v0.5.2