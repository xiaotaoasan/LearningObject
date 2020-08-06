package ThreadPrint;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPrintMain {
    public static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Condition conditionA = lock.newCondition();
        Condition conditionB = lock.newCondition();

        Thread a = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    conditionB.signal();
                    System.out.println("a" + Thread.currentThread().getName());
                    conditionA.await();
                } catch (Exception e) {

                } finally {
                    lock.unlock();
                }

            }

        });
        a.start();
        Thread b = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    conditionA.signal();
                    System.out.println("A" + Thread.currentThread().getName());
                    conditionB.await();
                } catch (Exception e) {

                } finally {
                    lock.unlock();
                }

            }

        });
        b.start();

        try {
            a.join();
            b.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

        System.out.println("swfwwfwevfe");

        FactoryTest factoryTest = new FactoryTest();
        new Thread(() -> {
            factoryTest.produceA();
        }).start();
        new Thread(() -> {
            factoryTest.produceB();
        }).start();

    }
}
