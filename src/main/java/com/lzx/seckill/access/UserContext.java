package com.lzx.seckill.access;

import com.lzx.seckill.domain.SeckillUser;

public class UserContext {

    public static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setUser(SeckillUser user){
        userHolder.set(user);
    }

    public static SeckillUser getUser(){
        return userHolder.get();
    }
}
