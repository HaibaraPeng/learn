package org.example.rabbitmq.springboot.consumer.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 该消费者的主要业务将报错，然后将消息转入处理异常的队列中，以便程序员后续处理这些没有执行的消息
 */
@Component
@Slf4j
public class MyErrorMessageListener {

    public static final String EXCHANGE_DIRECT = "exchange.direct.springboot.error.order";
    public static final String EXCHANGE_DIRECT_BACKUP = "exchange.direct.springboot.backup.order";
    public static final String ROUTING_KEY = "order";
    public static final String QUEUE_NAME_ERROR = "springboot.queue.error.order";
    public static final String QUEUE_NAME_BACKUP = "springboot.queue.backup.order";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = QUEUE_NAME_ERROR, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = EXCHANGE_DIRECT, durable = "true", autoDelete = "false"),
            key = {ROUTING_KEY}
    ))
    public void processMessage(String dateString,
                               Message message,
                               Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 业务处理, 此时模拟业务异常
            int i = 1 / 0;
            // 正常发送确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            Boolean redelivered = message.getMessageProperties().getRedelivered();
            if (!redelivered) {
                // 如果没有被投递过，那就重新放回队列，重新投递，再试一次
                log.error("第一次消息消费失败，消息ID：{}，消息内容：{}", deliveryTag, dateString);
                channel.basicNack(deliveryTag, false, true);
            } else {
                // 如果已经被投递过，且这一次仍然进入了 catch 块，那么返回拒绝且不再放回队列，放入backup队列
                channel.basicReject(deliveryTag, false);
                channel.basicPublish(EXCHANGE_DIRECT_BACKUP, ROUTING_KEY, null, message.getBody());
            }
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = QUEUE_NAME_BACKUP, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = EXCHANGE_DIRECT_BACKUP, durable = "true", autoDelete = "false"),
            key = {ROUTING_KEY}
    ))
    public void backupProcessMessage(String dateString,
                                     Message message,
                                     Channel channel) throws IOException {
        log.info("备份队列消费消息：{}", dateString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
