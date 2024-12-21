package org.example.rabbitmq.springboot.consumer.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DeadLetterMessageListener {

    public static final String EXCHANGE_NORMAL = "exchange.normal.video";
    public static final String EXCHANGE_DEAD_LETTER = "exchange.dead.letter.video";

    public static final String ROUTING_KEY_NORMAL = "routing.key.normal.video";
    public static final String ROUTING_KEY_DEAD_LETTER = "routing.key.dead.letter.video";

    public static final String QUEUE_NORMAL = "queue.normal.video";
    public static final String QUEUE_DEAD_LETTER = "queue.dead.letter.video";

    // 定义死信队列
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(QUEUE_DEAD_LETTER, true); // durable: 是否持久化
    }

    // 定义死信交换机
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DEAD_LETTER);
    }

    // 绑定死信队列到死信交换机
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(ROUTING_KEY_DEAD_LETTER);
    }

    // 定义普通队列，设置死信队列相关参数
    @Bean
    public Queue normalQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_DEAD_LETTER); // 设置死信交换机
        args.put("x-dead-letter-routing-key", ROUTING_KEY_DEAD_LETTER); // 设置死信路由键
        return new Queue(QUEUE_NORMAL, true, false, false, args); // durable: 是否持久化, exclusive: 排他, autoDelete: 自动删除, arguments: 参数
    }

    // 定义普通交换机
    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange(EXCHANGE_NORMAL);
    }

    // 绑定普通队列到普通交换机
    @Bean
    public Binding normalBinding(Queue normalQueue, DirectExchange normalExchange) {
        return BindingBuilder.bind(normalQueue).to(normalExchange).with(ROUTING_KEY_NORMAL);
    }

    @RabbitListener(queues = {QUEUE_NORMAL})
    public void processMessageNormal(Message message, Channel channel) throws IOException {
        // 监听正常队列，但是拒绝消息
        log.info("★[normal]消息接收到，但我拒绝。");
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = {QUEUE_DEAD_LETTER})
    public void processMessageDead(String dataString, Message message, Channel channel) throws IOException {
        // 监听死信队列
        log.info("★[dead letter]dataString = " + dataString);
        log.info("★[dead letter]我是死信监听方法，我接收到了死信消息");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
