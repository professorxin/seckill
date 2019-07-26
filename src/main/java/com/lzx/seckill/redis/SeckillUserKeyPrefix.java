package com.lzx.seckill.redis;

public class SeckillUserKeyPrefix extends BaseKeyPrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    public SeckillUserKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillUserKeyPrefix token = new SeckillUserKeyPrefix(TOKEN_EXPIRE, "token");

}
