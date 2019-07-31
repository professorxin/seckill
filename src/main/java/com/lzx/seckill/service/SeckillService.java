package com.lzx.seckill.service;

import com.lzx.seckill.domain.OrderInfo;
import com.lzx.seckill.domain.SeckillOrder;
import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.redis.RedisService;
import com.lzx.seckill.redis.SeckillKeyPrefix;
import com.lzx.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
        //减库存，下订单，写入秒杀订单
        //1.减库存
        boolean ret = goodsService.reduceStock(goods);
        if (!ret) {
            setGoodsOver(goods.getId());
            return null;
        }
        //2.下订单，写入秒杀订单
        return orderService.createOrder(user, goods);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(SeckillKeyPrefix.isGoodsOver, "" + goodsId);
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKeyPrefix.isGoodsOver, "" + goodsId, true);
    }

    public long getSeckillResult(Long userId, Long goodsId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
        if (seckillOrder != null) {//秒杀成功
            return seckillOrder.getOrderId();
        } else {
            boolean over = getGoodsOver(goodsId);
            if (over) {//秒杀失败
                return -1;
            } else {//排队中，等待秒杀完成
                return 0;
            }
        }
    }
}
