import java.util.HashMap;
import java.util.concurrent.*;

public class Jvmtest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        HashMap<String, String> hashMap = new HashMap<String, String>();
        ExecutorService executor =
                new ThreadPoolExecutor(10, 20, 500L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1024));
//        Future<String> a = executor.submit(() -> {
//            System.out.println("aaaaa");
//            return "111";
//        });
//
//        String result = a.get();
//        System.out.println(result);
//        ConcurrentHashMap<String, String> concurrentHashMap =
//                new ConcurrentHashMap<>();
//        concurrentHashMap.put("a","b");
        new Thread(()->{
            System.out.println("bbb");
        },"bbb").start();

        new Thread(()->{
            System.out.println("ccc");
        },"ccc").start();
        new Thread(() -> {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }
            push();
        }, "aaa").start();
    }

    public static void push() {
        for (; ; ) {
            System.out.println("push is ok");
        }
    }

}
