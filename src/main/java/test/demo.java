package test;

/**
 * 多线程售票demo
 */
public class demo {

    public static void main(String[] args) {
        TicketTest tr = new TicketTest();

        Thread t1 = new Thread(tr, "窗口A");
        Thread t2 = new Thread(tr, "窗口B");
        Thread t3 = new Thread(tr, "窗口C");
        Thread t4 = new Thread(tr, "窗口D");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
