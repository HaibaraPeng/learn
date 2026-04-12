import os

from deepagents import create_deep_agent
from dotenv import load_dotenv
from langchain_anthropic import ChatAnthropic

load_dotenv()


def main():
    model = ChatAnthropic(
        model="glm-5",
        temperature=0,
        anthropic_api_url="https://coding.dashscope.aliyuncs.com/apps/anthropic",
    )

    agent = create_deep_agent(model=model)

    result = agent.invoke({"messages": [{"role": "user", "content": "Hello, what can you do?"}]})

    print(result["messages"][-1].content)


if __name__ == "__main__":
    main()
