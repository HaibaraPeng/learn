package org.example.rabbitmq.provider;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.rabbitmq.common.ConnectionUtil;

import java.nio.charset.StandardCharsets;

public class Producer3PubSub {

    private static final String QUEUE_NAME_1 = "test_fanout_queue1";
    private static final String QUEUE_NAME_2 = "test_fanout_queue2";
    private static final String EXCHANGE_NAME = "test_fanout";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_1, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_2, true, false, false, null);

        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, "");
        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, "");

        String body = "Producer3PubSub 测试";

        channel.basicPublish(EXCHANGE_NAME, "", null, body.getBytes(StandardCharsets.UTF_8));

        channel.close();
        connection.close();
    }
}
