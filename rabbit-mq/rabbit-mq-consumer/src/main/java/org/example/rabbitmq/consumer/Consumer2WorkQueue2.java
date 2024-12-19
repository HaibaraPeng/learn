package org.example.rabbitmq.consumer;

import com.rabbitmq.client.*;
import org.example.rabbitmq.common.ConnectionUtil;

import java.io.IOException;

public class Consumer2WorkQueue2 {

    private static final String QUEUE_NAME = "work_queue";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        DefaultConsumer consumer = new DefaultConsumer(channel) {

            // 回调方法,当收到消息后，会自动执行该方法
            // 参数1. consumerTag：标识
            // 参数2. envelope：获取一些信息，交换机，路由key...
            // 参数3. properties：配置信息
            // 参数4. body：数据
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumerTag：" + consumerTag);
                System.out.println("Exchange：" + envelope.getExchange());
                System.out.println("RoutingKey：" + envelope.getRoutingKey());
                System.out.println("properties：" + properties);
                System.out.println("body：" + new String(body));
                System.out.println("--------------------------------");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
