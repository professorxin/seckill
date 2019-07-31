package com.lzx.seckill.rabbitmq;

import com.lzx.seckill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, message);
    }

    /**
     * 将消息发送到topic exchange
     *
     * @param message
     */
    public void sendTopic(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send topic message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }


    public void sendFanout(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send fanout message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }

    public void sendHeaders(Object message) {
        String mes = RedisService.beanToString(message);
        log.info("send headers message:" + mes);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("key1", "value1");
        messageProperties.setHeader("key2", "value2");
        Message msg = new Message(mes.getBytes(), messageProperties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", msg);

    }

    public void sendSeckillMsg(Object message) {
        String mes = RedisService.beanToString(message);
        log.info("send seckill message:" + mes);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, mes);
    }


}
