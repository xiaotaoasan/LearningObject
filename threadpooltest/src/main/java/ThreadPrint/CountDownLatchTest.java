package ThreadPrint;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i =0;i<10;i++){
            final int temp = i;
            new Thread(()->{
                countDownLatch.countDown();
                System.out.println(temp+""+Thread.currentThread().getName());
            }).start();

        }

        ArrayList<Runnable> arrayList = new ArrayList<>();


        arrayList.add(new Runnable() {
            @Override
            public void run() {
                System.out.println("a");
            }
        });
        arrayList.add(new Runnable() {
            @Override
            public void run() {
                System.out.println("b");
            }
        });
        arrayList.add(new Runnable() {
            @Override
            public void run() {
                System.out.println("c");
            }
        });



    }
}
