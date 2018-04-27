package com.allan.lockdemo.controller;

public class TicketTest implements Runnable {

    /**
     * 100张票
     */
    private int TicketNum = 100;


    @Override
    public void run() {
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
