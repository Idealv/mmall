package com.mall.util;

import com.mall.common.RedisSharedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {
    private RedisShardedPoolUtil(){}
    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{},value:{} error", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static String setEx(String key,String value,Integer exTime){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("set key:{},value:{} error", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    /**
     * @param key redis键值
     * @param exTime 失效时间,单位为秒
     * @return 1:设置失效时间成功 0:设置失效时间失败
     */
    public static Long expire(String key,Integer exTime){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        set("keyTest", "test");
        expire("keyTest", 60 * 10);
    }
}
