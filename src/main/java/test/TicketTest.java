package test;

public class TicketTest implements Runnable {

    private int TicketNum = 50;


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
