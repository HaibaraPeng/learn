package org.example.rabbitmq.provider;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.rabbitmq.common.ConnectionUtil;

import java.nio.charset.StandardCharsets;

public class Producer2WorkQueue {

    private static final String QUEUE_NAME = "work_queue";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        for (int i = 0; i < 10; i++) {
            String body = i + " work queue";
            channel.basicPublish("", QUEUE_NAME, null, body.getBytes(StandardCharsets.UTF_8));
        }

        channel.close();
        connection.close();
    }
}
