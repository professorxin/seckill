package com.lzx.seckill.redis;

public class OrderKeyPrefix extends BaseKeyPrefix {

    public OrderKeyPrefix(String prefix) {
        super(prefix);
    }

    public static OrderKeyPrefix getSeckillOrderByUidGid = new OrderKeyPrefix("getSeckillOrderByUidGid");
}
