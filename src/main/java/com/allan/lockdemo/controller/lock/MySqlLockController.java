package com.allan.lockdemo.controller.lock;

import com.allan.lockdemo.service.MySqlLockServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @ClassName: MySqlLockController
 * @Description: 基于MySql的分布式锁:
 *               原理：利用数据库提供的锁机制，要求数据库支持行锁。
 *               优点：实现简单，稳定可靠
 *               缺点：性能差，无法适应高并发场景。
 *                    容易出现死锁的情况。
 *                    无法优雅实现阻塞式锁，不可重入。
 * @author qinzz
 * @date 2018年8月30日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MySqlLockController {
    private static final Logger log = LoggerFactory.getLogger(MySqlLockController.class);
    @Autowired
    MySqlLockServer mySqlLockServer;

    private int TicketNum = 10;

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

    /**
     *
     * @ClassName: TickectRunnable
     * @Description: 用mysql作为锁
     * @author qinzz
     * @date 2018年8月30日
     *
     */
    public class TickectRunnable implements Runnable {
        @Override
        public void run() {
            while (TicketNum > 0) {
                if (TicketNum > 0) {
                    // 加锁
                    mySqlLockServer.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + "，售出第" + (TicketNum--) + "张票");
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    } finally {
                        // 解锁，避免死锁
                        mySqlLockServer.unlock();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }

    }
}
