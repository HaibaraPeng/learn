package org.example.rabbitmq.springboot.producer;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {

    public static final String EXCHANGE_DIRECT = "exchange.direct.springboot.order";
    public static final String EXCHANGE_DIRECT_ERROR = "exchange.direct.springboot.error.order";
    public static final String ROUTING_KEY = "order";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage() {
        rabbitTemplate.convertAndSend(
                EXCHANGE_DIRECT,
                ROUTING_KEY,
                "Hello world");
    }

    @Test
    public void testSendErrorMessage() {
        rabbitTemplate.convertAndSend(
                EXCHANGE_DIRECT_ERROR,
                ROUTING_KEY,
                "error message");
    }

    @Test
    public void testSendPrefetchMessage() {
        for (int i = 0; i < 100; i++) {
            rabbitTemplate.convertAndSend(
                    EXCHANGE_DIRECT,
                    ROUTING_KEY,
                    "Hello world " + i);
        }

    }

    @Test
    public void testSendMessageTTL() {

        // 1、创建消息后置处理器对象
        MessagePostProcessor messagePostProcessor = (Message message) -> {
            // 设定 TTL 时间，以毫秒为单位
            message.getMessageProperties().setExpiration("5000");
            return message;
        };

        // 2、发送消息
        rabbitTemplate.convertAndSend(
                EXCHANGE_DIRECT,
                ROUTING_KEY,
                "test ttl", messagePostProcessor);
    }

    public static final String EXCHANGE_NORMAL = "exchange.normal.video";
    public static final String ROUTING_KEY_NORMAL = "routing.key.normal.video";

    @Test
    public void testSendMessageButReject() {
        rabbitTemplate
                .convertAndSend(
                        EXCHANGE_NORMAL,
                        ROUTING_KEY_NORMAL,
                        "测试死信情况1：消息被拒绝");
    }
}
