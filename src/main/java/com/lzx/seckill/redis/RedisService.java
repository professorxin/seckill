package com.lzx.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;


    /**
     * redis的get操作，通过key获取存储在redis中的对象
     *
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * redis的set操作，将对象存储在redis中
     *
     * @param keyPrefix
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix keyPrefix, String key, T t) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(t);
            if (str == null || str.length() < 0) {
                return false;
            }
            String realKey = keyPrefix.getPrefix() + key;
            int expireSeconds = keyPrefix.expireSeconds();
            if (expireSeconds <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, expireSeconds, str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 判断key是否存在redis中
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 自增
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 自减
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 根据key删除redis的数据
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean delete(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix + key;
            Long del = jedis.del(realKey);
            return del > 0;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 将对象转换为对应的json字符串
     *
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class clazz = value.getClass();
        //处理基本类型
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else
            return JSON.toJSONString(value);
    }

    /**
     * 将字符串转化为class对应类型的对象
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        //处理基本类型
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
    }

    /**
     * 返回连接对象到连接池
     *
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
