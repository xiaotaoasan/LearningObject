package ThreadPrint;



import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FactoryTest {
    private Lock lock = new ReentrantLock();
    Condition conditionA = lock.newCondition();
    Condition conditionB = lock.newCondition();


    public void produceA() {
        lock.lock();

        for (int i = 0; i < 10; i++) {
            try {
                conditionB.signal();
                System.out.println("a" + Thread.currentThread().getName());
                conditionA.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    public void produceB() {
        lock.lock();

        for (int i = 0; i < 10; i++) {
            try {
                conditionA.signal();
                System.out.println("b" + Thread.currentThread().getName());
                conditionB.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

}
