package com.lzx.seckill.redis;

public class SeckillKeyPrefix extends BaseKeyPrefix{


    public SeckillKeyPrefix(String prefix) {
        super(prefix);
    }

    public static SeckillKeyPrefix isGoodsOver = new SeckillKeyPrefix("isGoodsOver");
}
