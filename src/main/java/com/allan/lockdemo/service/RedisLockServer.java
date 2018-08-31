package com.allan.lockdemo.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于redis分布式锁：
 * 1 互斥性。在任意时刻，只有一个客户端能持有锁。
 * 2 不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
 * 3 具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
 * 4 加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
 */
@Service
public class RedisLockServer implements Lock{

    @Resource
    private JedisConnectionFactory factory;

    private static final String LOCK_KEY="lock";
    /**
     * Redis setnx
     */
    private static final String SET_IF_NOT_EXIST = "NX";
    /**
     *
     */
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    /**
     * 超时时间：3秒
     */
    private static final int TIME_OUT = 3000;
    /**
     * 加锁后返回是结果，Redis加锁成功，返回ok
     */
    private static final String LOCK_RESULT_SUCCESS = "OK";

    /**
     * redis lua脚本
     */
    private   String script="if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    //线程级变量
    ThreadLocal<String> threadLocal=new ThreadLocal<String>();


    @Override
    public void lock() {
        if (!tryLock()){
            try {
                Thread.sleep(new Random().nextInt(10)+1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock();
        }

    }


    /**
     * 使用Redis尝试加锁
     */
    @Override
    public boolean tryLock() {
        //获取redis原始连接
        Jedis jedis=(Jedis)factory.getConnection().getNativeConnection();
        try {

            String uuid= UUID.randomUUID().toString().replaceAll("-", "");//生产随机值（必须）
            threadLocal.set(LOCK_KEY);//线程变量传值
            /**
             *  Redis锁原理：
             *
             * 通过setnx命令,向特定的key写入一个随机值，并同时设置失效时间，写值成功就是加锁成功。
             * setnx命令:set key value NX PX 3000
             * 注意：1、必须给锁设置失效时间，避免死锁。
             * 	    2、加锁时，每个节点产生一个随机值， 避免锁误删（保证谁的锁谁删除，只有本线程才能删除锁，其他线程不能删除他人的锁）
             *      3、写入随机值和设置失效时间必须是同时的，保证锁的原子性
             */
            String result = jedis.set(LOCK_KEY, uuid, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, TIME_OUT);
            if (StringUtils.isNotBlank(result) && result.equals(LOCK_RESULT_SUCCESS)) {
                // 加锁成功，返回
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();// 释放连接资源
        }

        return false;
    }


    /**
     * 解锁原理：保证获取数据，判断一致，删除数据三个操作是原子的
     */
    @Override
    public void unlock() {
        //获取Redis连接
        Jedis jedis=(Jedis)factory.getConnection().getNativeConnection();
        try {
            /**
             * 读取lua脚本配置文件 （可以不用配置lua脚本）
             */
           /* File file = ResourceUtils.getFile("classpath:unlock.lua");
            jedis.eval(file.toString(), Arrays.asList(LOCK_KEY),Arrays.asList(threadLocal.get()));*/
            /**
             * 首先获取锁对应的value值，检查是否与uuid相等，如果相等则删除锁（解锁） uuid 通过threadLocal来传递
             * 保证获取数据，判断一致，删除数据三个操作是原子的
             *
             * 简单来说，就是在eval命令执行Lua代码的时候，Lua代码将被当成一个命令去执行，并且直到eval命令执行完成，Redis才会执行其他命令
             */
            jedis.eval(script, Arrays.asList(LOCK_KEY),Arrays.asList(threadLocal.get()));
        } catch (Exception exception){
            exception.printStackTrace();
        }
        finally {
            jedis.close();// 释放连接资源
        }
    }


    /**
     *
     * *********************************************************************
     */


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
