package com.allan.lockdemo.service;

import com.allan.lockdemo.mapper.TlockMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于MySQL的锁
 */
@Service("mySqlLockServer")
public class MySqlLockServer implements Lock {

    /**
     * 引入mapper
     */
    @Resource
    private TlockMapper tlockMapper;

    /**
     * 唯一ID
     */
    private static final Integer NUMBER = 1;



    /**
     * 加锁（阻塞式锁）
     */
    @Override
    public void lock() {
        /**
         * 试图加锁，加锁失败，随机休眠10毫秒，继续加锁。。
         * 递归调用自己
         */
        if (!tryLock()) {
            try {
                Thread.sleep(new Random().nextInt(10) + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lock();
        }

    }

    /**
     * 加锁（非阻塞式锁）
     *
     * @return
     */
    @Override
    public boolean tryLock() {
        try {
            /**
             * 新增记录
             */
            tlockMapper.insert(NUMBER);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 解锁
     */
    @Override
    public void unlock() {
        /**
         * 删除记录
         */
        tlockMapper.deleteByPrimaryKey(NUMBER);

    }


    /**
     * *****************************************************************************************
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
