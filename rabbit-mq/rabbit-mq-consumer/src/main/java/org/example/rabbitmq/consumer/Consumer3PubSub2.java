package org.example.rabbitmq.consumer;

import com.rabbitmq.client.*;
import org.example.rabbitmq.common.ConnectionUtil;

import java.io.IOException;

public class Consumer3PubSub2 {

    private static final String QUEUE_NAME = "test_fanout_queue2";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        DefaultConsumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumerTag：" + consumerTag);
                System.out.println("Exchange：" + envelope.getExchange());
                System.out.println("RoutingKey：" + envelope.getRoutingKey());
                System.out.println("properties：" + properties);
                System.out.println("body：" + new String(body));
                System.out.println("Consumer3PubSub2--------------------------------");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
