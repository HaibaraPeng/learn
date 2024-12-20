package org.example.rabbitmq.provider;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.rabbitmq.common.ConnectionUtil;

import java.nio.charset.StandardCharsets;

public class Producer5Topics {

    private static final String QUEUE_NAME_1 = "test_topic_queue1";
    private static final String QUEUE_NAME_2 = "test_topic_queue2";
    private static final String EXCHANGE_NAME = "test_topic";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_1, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_2, true, false, false, null);

        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, "#.error");
        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, "order.*");

        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, "*.*");

        channel.basicPublish(EXCHANGE_NAME, "order.info", null, "ROUTING_KEY_ERROR".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(EXCHANGE_NAME, "good.info", null, "ROUTING_KEY_ERROR".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(EXCHANGE_NAME, "good.error", null, "ROUTING_KEY_ERROR".getBytes(StandardCharsets.UTF_8));

        channel.close();
        connection.close();
    }
}
