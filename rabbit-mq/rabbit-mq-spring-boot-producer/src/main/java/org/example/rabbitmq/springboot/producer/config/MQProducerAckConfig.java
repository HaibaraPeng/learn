package org.example.rabbitmq.springboot.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author Roc
 * @Date 2024/12/21 11:07
 */
@Component
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送成功! message: " + correlationData);
        } else {
            log.info("消息发送失败! message: " + correlationData + ", cause: " + cause);
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("消息主体: " + new String(returnedMessage.getMessage().getBody()));
        log.info("应答码: " + returnedMessage.getReplyCode());
        log.info("描述：" + returnedMessage.getReplyText());
        log.info("消息使用的交换器 exchange : " + returnedMessage.getExchange());
        log.info("消息使用的路由键 routing : " + returnedMessage.getRoutingKey());
    }
}
