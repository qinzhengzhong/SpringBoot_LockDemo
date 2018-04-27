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

@Service("redisLockServer")
public class RedisLockServer implements Lock{

    @Resource
    private JedisConnectionFactory factory;

    private static final String LOCK_KEY="lock";

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



    @Override
    public boolean tryLock() {
        //获取redis原始连接
        Jedis jedis=(Jedis)factory.getConnection().getNativeConnection();
        /**
         * 获得连接后，通过连接获得setnx命令
         */
        String uuid= UUID.randomUUID().toString();//生产随机值（必须）
        threadLocal.set(LOCK_KEY);//线程变量传值
        String ret= jedis.set(LOCK_KEY,uuid,"NX","PX",3000);
        if (StringUtils.isNotBlank(ret) && ret.equals("ok")){
            //加锁成功，返回
            return true;
        }
        return false;
    }



    @Override
    public void unlock() {
        try {
            /**
             * 读取lua脚本配置文件
             */
            File file = ResourceUtils.getFile("classpath:unlock.lua");
            //获取Redis连接
            Jedis jedis=(Jedis)factory.getConnection().getNativeConnection();
            //执行lua脚本
//            jedis.eval(file.toString(), Arrays.asList(LOCK_KEY),Arrays.asList(threadLocal.get()));
            jedis.eval(script, Arrays.asList(LOCK_KEY),Arrays.asList(threadLocal.get()));



        } catch (FileNotFoundException e) {
            System.out.println("找不到解锁文件");
            e.printStackTrace();
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
