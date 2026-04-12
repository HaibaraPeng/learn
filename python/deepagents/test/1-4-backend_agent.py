import json

from deepagents import create_deep_agent
from dotenv import load_dotenv
from langchain_anthropic import ChatAnthropic
from deepagents.backends import FilesystemBackend
from deepagents.middleware.permissions import FilesystemPermission

load_dotenv()


def main():
    model = ChatAnthropic(
        model="glm-5",
        temperature=0,
        anthropic_api_url="https://coding.dashscope.aliyuncs.com/apps/anthropic",
    )

    # 配置 Backend（指定工作目录）
    backend = FilesystemBackend(root_dir="./workspace", virtual_mode=True)

    # 配置权限（只允许读取）
    permissions = [
        FilesystemPermission(operations=["read"], paths=["/workspace/**"]),
        FilesystemPermission(operations=["write"], paths=["/workspace/**"], mode="deny"),
    ]

    # 创建 Agent
    agent = create_deep_agent(
        model=model,
        backend=backend,
        permissions=permissions,
    )

    # 运行（只能读，不能写）
    result = agent.invoke({
        "messages": [{"role": "user", "content": "读取 ./workspace/data.txt"}]
    })

    print("=" * 50)
    print("所有 Messages 内容：")
    print("=" * 50)

    for i, msg in enumerate(result["messages"], 1):
        print(f"\n--- Message {i} ---")
        print(f"Type: {type(msg).__name__}")
        if hasattr(msg, "role"):
            print(f"Role: {msg.role}")
        if hasattr(msg, "content"):
            print(f"Content: {msg.content}")
        # print(f"Full: {json.dumps(msg.__dict__, indent=2, default=str, ensure_ascii=False)}")

    print("\n" + "=" * 50)
    print("最后一条 Message：")
    print("=" * 50)
    print(result["messages"][-1].content)


if __name__ == "__main__":
    main()
