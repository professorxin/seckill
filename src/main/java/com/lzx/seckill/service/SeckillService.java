package com.lzx.seckill.service;

import com.lzx.seckill.domain.OrderInfo;
import com.lzx.seckill.domain.SeckillUser;
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

    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
        //减库存，下订单，写入秒杀订单
        //1.减库存
        goodsService.reduceStock(goods);

        //2.下订单，写入秒杀订单
        return orderService.createOrder(user, goods);
    }
}
