package com.mall.common;

import com.google.common.collect.Lists;
import com.mall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.List;

public class RedisSharedPool {
    //Jedis连接池
    private static ShardedJedisPool pool;
    //最大连接数
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getPropery("redis.max.total"));
    //最大空闲数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.getPropery("redis.max.idle"));
    //最小空闲数
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.getPropery("redis.min.idle"));
    //borrow一个jedis实例时,是否需要测试可用,若为true,则borrow的必可用
    private static Boolean testonBorrow = Boolean.valueOf(PropertiesUtil.getPropery("redis.test.borrow"));
    //return一个jedis实例时,是否需要测试可用,若为true,则返回的必可用
    private static Boolean testonReturn = Boolean.valueOf(PropertiesUtil.getPropery("redis.test.return"));

    private static String ip = PropertiesUtil.getPropery("redis.ip");
    private static String[] ports = PropertiesUtil.getPropery("redis.port").split(",");
    private static Integer port1 = Integer.valueOf(ports[0]);
    private static Integer port2 = Integer.valueOf(ports[1]);

    private RedisSharedPool() {
    }

    private static void init() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testonBorrow);
        config.setTestOnReturn(testonReturn);
        //无可用资源(连接)后阻塞,防止报错
        config.setBlockWhenExhausted(true);

        JedisShardInfo info1 = new JedisShardInfo(ip, port1, 1000 * 2);
        JedisShardInfo info2 = new JedisShardInfo(ip, port2, 1000 * 2);

        List<JedisShardInfo> jedisShardInfoList = Lists.newArrayList();
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        init();
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }


    public static void main(String[] args) {
        ShardedJedis jedis = getJedis();
        for (int i = 0; i < 10; i++) {
            jedis.set("key:" + i, "value:" + i);
        }
        returnResource(jedis);
    }
}
