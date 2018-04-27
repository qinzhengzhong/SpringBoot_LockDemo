package com.allan.lockdemo.controller;

import com.allan.lockdemo.service.MySqlLockServer;
import com.allan.lockdemo.service.RedisLockServer;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;

public class LockController {
    private int TicketNum = 100;

    /**
     * 基于并发包的锁
     */
//    private Lock lock=new ReentrantLock();

  /*  @Resource(name = "mySqlLockServer")
   private Lock lock;*/

    @Resource(name = "redisLockServer")
    private Lock lock;




    @Test
    public void LockTest() throws InterruptedException {

        TickectRunnable tr = new TickectRunnable();

        Thread t1 = new Thread(tr, "窗口A");
        Thread t2 = new Thread(tr, "窗口B");
        Thread t3 = new Thread(tr, "窗口C");
        Thread t4 = new Thread(tr, "窗口D");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        Thread.currentThread().join();

    }


    public class TickectRunnable implements Runnable {

        @Override
        public void run() {
            while (TicketNum > 0) {
                if (TicketNum > 0) {
                    //加锁
                    lock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + "，售出第" + (TicketNum--) + "张票");
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        //解锁
                        lock.unlock();
                    }


                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
