package com.lzx.seckill.rabbitmq;

import com.lzx.seckill.domain.OrderInfo;
import com.lzx.seckill.domain.SeckillOrder;
import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.redis.RedisService;
import com.lzx.seckill.service.GoodsService;
import com.lzx.seckill.service.OrderService;
import com.lzx.seckill.service.SeckillService;
import com.lzx.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    /**
     * Direct 模式
     *
     * @param message
     */
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void topic1Receive(String message) {
        //log.info("receive queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void topic2Receive(String message) {
        //log.info("receive queue2 message:" + message);
    }


    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void fanout1Receive(String message) {
        log.info("receive queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void fanout2Receive(String message) {
        log.info("receive queue2 message:" + message);
    }

    @RabbitListener(queues = MQConfig.HEAD_QUEUE)
    public void headerReceive(byte[] message) {
        log.info("receive headerqueue message:" + new String(message));
    }

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receiveSeckillMsg(String message) {
        log.info("receive seckill_queue message:" + message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);
        SeckillUser user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();
        //判断是否还有秒杀库存
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            return;
        }
        //判断是否重复下单
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return;
        }
        //减库存，下订单，写入秒杀订单表
        seckillService.seckill(user, goods);
    }
}
