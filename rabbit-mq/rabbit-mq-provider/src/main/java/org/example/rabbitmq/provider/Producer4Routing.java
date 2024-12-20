package org.example.rabbitmq.provider;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.rabbitmq.common.ConnectionUtil;

import java.nio.charset.StandardCharsets;

public class Producer4Routing {

    private static final String QUEUE_NAME_1 = "test_direct_queue1";
    private static final String QUEUE_NAME_2 = "test_direct_queue2";
    private static final String EXCHANGE_NAME = "test_direct";
    private static final String ROUTING_KEY_ERROR = "error";
    private static final String ROUTING_KEY_WARN = "warn";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_1, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_2, true, false, false, null);

        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, ROUTING_KEY_ERROR);

        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, ROUTING_KEY_ERROR);
        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, ROUTING_KEY_WARN);

//        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY_ERROR, null, body.getBytes(StandardCharsets.UTF_8));

        channel.close();
        connection.close();
    }
}
