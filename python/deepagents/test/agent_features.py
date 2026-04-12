import json
import os

from deepagents import create_deep_agent
from langchain_anthropic import ChatAnthropic

os.environ["ANTHROPIC_API_KEY"] = "sk-sp-617c87fc3f4849da9478cc8a859edd1d"


def main():
    model = ChatAnthropic(
        model="glm-5",
        temperature=0,
        anthropic_api_url="https://coding.dashscope.aliyuncs.com/apps/anthropic",
    )

    agent = create_deep_agent(model=model)

    # 测试 1: 任务规划（write_todos）
    # result = agent.invoke({
    #     "messages": [
    #         {"role": "user", "content": "帮我写一个 Python 爬虫，抓取豆瓣电影评分"}
    #     ]
    # })

    # 测试 2: 文件操作（read_file/write_file）
    # result = agent.invoke({
    #     "messages": [
    #         {"role": "user", "content": "读取当前目录下的 README.md，总结内容"}
    #     ]
    # })

    # 测试 3: 子 Agent 调用（task）
    result = agent.invoke({
        "messages": [
            {"role": "user", "content": "帮我分析 LangGraph 的核心特性，需要深入研究"}
        ]
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
