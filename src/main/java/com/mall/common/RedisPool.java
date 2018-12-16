package com.mall.common;

import com.mall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    //Jedis连接池
    private static JedisPool pool;
    //最大连接数
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getPropery("redis.max.total"));
    //最大空闲数
    private static Integer maxIdle=Integer.valueOf(PropertiesUtil.getPropery("redis.max.idle"));
    //最小空闲数
    private static Integer minIdle=Integer.valueOf(PropertiesUtil.getPropery("redis.min.idle"));
    //borrow一个jedis实例时,是否需要测试可用,若为true,则borrow的必可用
    private static Boolean testonBorrow = Boolean.valueOf(PropertiesUtil.getPropery("redis.test.borrow"));
    //return一个jedis实例时,是否需要测试可用,若为true,则返回的必可用
    private static Boolean testonReturn = Boolean.valueOf(PropertiesUtil.getPropery("redis.test.return"));

    private static String ip = PropertiesUtil.getPropery("redis.ip");
    private static Integer port = Integer.valueOf(PropertiesUtil.getPropery("redis.port"));

    private static void init(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testonBorrow);
        config.setTestOnReturn(testonReturn);
        //无可用资源(连接)后阻塞,防止报错
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config, ip, port, 1000 * 2);
    }
    static {
        init();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    private RedisPool(){}

//    public static void main(String[] args) {
//        Jedis jedis = getJedis();
//        jedis.set("name", "xiaoming");
//        String name = jedis.get("name");
//        System.out.println("name = " + name);
//        returnResource(jedis);
//    }
}
