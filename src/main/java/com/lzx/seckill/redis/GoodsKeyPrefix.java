package com.lzx.seckill.redis;

public class GoodsKeyPrefix extends BaseKeyPrefix {

    public GoodsKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKeyPrefix goodsListKeyPrefix = new GoodsKeyPrefix(60, "goodsList");

    public static GoodsKeyPrefix goodsDetailKeyPrefix = new GoodsKeyPrefix(60, "goodsDetail");


}
