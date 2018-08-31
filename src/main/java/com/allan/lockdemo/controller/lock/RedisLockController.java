package com.allan.lockdemo.controller.lock;


import com.allan.lockdemo.service.RedisLockServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 
 * @ClassName: RedisLockController
 * @Description: 基于Redis的分布式锁
 * @author qinzz
 * @date 2018年8月30日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLockController{

	private static final Logger log = LoggerFactory.getLogger(RedisLockController.class);
    /**
     * Redis锁服务
     */
	@Autowired
	RedisLockServer redisLockServer;

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
	 * @Description: 用Redis作为锁
	 *               原理：使用缓存的CAS机制实现，保证对缓存操作序列的原子性。
	 *               优点：性能好。（可参考Redis的性能）
	 *               缺点：实现相对复杂。
	 *                     有可能出现死锁情况。
	 *                     无法优雅实现阻塞式锁，不可重入。
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
					redisLockServer.lock();
					try {
						System.out.println(Thread.currentThread().getName() + "，售出第" + (TicketNum--) + "张票");
					} catch (Exception e) {
						log.error(e.getMessage());
					} finally {
						// 解锁，避免死锁
						redisLockServer.unlock();
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
