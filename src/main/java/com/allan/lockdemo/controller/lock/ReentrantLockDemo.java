package com.allan.lockdemo.controller.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;



/**
 * 
* @ClassName: ReentrantLockDemo
* @Description: 基于 ReentrantLock 的锁demo（单机）
* @author qinzz
* @date 2018年8月30日
*
 */
public class ReentrantLockDemo {
	
private int TicketNum = 100;
	
	/**
     * 基于并发包的锁
     */
    private Lock lock=new ReentrantLock();

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
	 * @Description: 模拟多线程
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
					lock.lock();
					try {
						System.out.println(Thread.currentThread().getName() + "，售出第" + (TicketNum--) + "张票");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 解锁
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
