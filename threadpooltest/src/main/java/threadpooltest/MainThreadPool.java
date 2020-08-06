package threadpooltest;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MainThreadPool {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return "aaa";
            }
        });
        int coreSize = Runtime.getRuntime().availableProcessors();
        System.out.println(coreSize);




    }
}
