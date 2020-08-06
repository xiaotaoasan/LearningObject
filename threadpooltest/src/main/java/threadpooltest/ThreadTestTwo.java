package threadpooltest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ThreadTestTwo extends Thread {
    @Override
    public void run() {
        System.out.println("start thread");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ThreadTestTwo().start();
        new Thread(new RunnableTest()).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("8978");
            }
        }).start();

        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("integer");
                return -1;
            }
        };

        FutureTask<Integer> futureTask = new FutureTask<Integer>(callable);
        new Thread(futureTask).start();

        Integer in = futureTask.get();
        System.out.println(in);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        Future<List> ab = executorService.submit(()->getAllList(list));
        System.out.println(ab.get());

    }

    public static List<String> getAllList(List<String> list1) {
        return list1.stream().map(id -> id.toUpperCase()).collect(Collectors.toList());
    }


}
