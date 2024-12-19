package org.example.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1HelloWorld {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("123456");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = "simple_queue";
        // 如果没有一个名字叫simple_queue的队列，则会创建该队列，如果有则不会创建
        // 参数1. queue：队列名称
        // 参数2. durable：是否持久化。如果持久化，则当MQ重启之后还在
        // 参数3. exclusive：是否独占。
        // 参数4. autoDelete：是否自动删除。当没有Consumer时，自动删除掉
        // 参数5. arguments：其它参数。
        channel.queueDeclare(queueName, true, false, false, null);

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
            }
        };

        // 参数1. queue：队列名称
        // 参数2. autoAck：是否自动确认，类似咱们发短信，发送成功会收到一个确认消息
        // 参数3. callback：回调对象
        // 消费者类似一个监听程序，主要是用来监听消息
        channel.basicConsume(queueName, true, consumer);
    }
}
