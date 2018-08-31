package com.allan.lockdemo.controller.lock;

import org.junit.Test;

/**
 * 
 * @ClassName: LockDemoController
 * @Description: 基于synchronized的分布式锁demo（单机）
 * @author qinzz
 * @date 2018年8月30日
 *
 */
public class LockDemoController {

	private int TicketNum = 50;
	

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
	 * @ClassName: TickectRunnable2
	 * @Description: TODO(这里用一句话描述这个类的作用)
	 * @author qinzz
	 * @date 2018年8月30日
	 *
	 */
	public class TickectRunnable implements Runnable {
		@Override
		public void run() {
			/**
			 * synchronized 锁
			 */
			synchronized (this) {
				while (TicketNum > 0) {
					if (TicketNum > 0) {
						System.out.println(Thread.currentThread().getName() + "，售出第" + (TicketNum--) + "张票");
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

}
