package org.example.rabbitmq.springboot.producer;

import org.junit.jupiter.api.Test;
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
}
