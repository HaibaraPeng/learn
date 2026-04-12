import json
import os

from deepagents import create_deep_agent
from dotenv import load_dotenv
from langchain_anthropic import ChatAnthropic
from langchain_core.tools import tool

load_dotenv()


# 自定义工具
@tool
def get_weather(city: str) -> str:
    """获取指定城市的天气信息"""
    # 实际应用中可调用天气 API
    return f"{city} 当前天气晴朗，温度 25°C"


def main():
    model = ChatAnthropic(
        model="glm-5",
        temperature=0,
        anthropic_api_url="https://coding.dashscope.aliyuncs.com/apps/anthropic",
    )

    # 自定义 Agent
    agent = create_deep_agent(
        model=model,
        tools=[get_weather],
        system_prompt="你是一个友好的助手，擅长回答天气相关问题。",
    )

    # 运行
    result = agent.invoke({"messages": [{"role": "user", "content": "北京今天天气怎么样？"}]})

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
