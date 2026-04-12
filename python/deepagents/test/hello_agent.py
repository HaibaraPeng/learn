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

    result = agent.invoke({"messages": [{"role": "user", "content": "Hello, what can you do?"}]})

    print(result["messages"][-1].content)


if __name__ == "__main__":
    main()
