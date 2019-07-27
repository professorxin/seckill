package com.lzx.seckill.service;

import com.lzx.seckill.dao.OrderDao;
import com.lzx.seckill.domain.OrderInfo;
import com.lzx.seckill.domain.SeckillOrder;
import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long id, Long goodsId) {
        return orderDao.getSeckillOrderByUserIdAndGoodsId(id, goodsId);
    }


    @Transactional
    public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        //订单信息插入order_info表中
        long orderId = orderDao.insert(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(orderId);
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setUserId(user.getId());

        //秒杀订单信息插入seckill_order表
        orderDao.insertSeckillOrder(seckillOrder);

        return orderInfo;
    }
}
