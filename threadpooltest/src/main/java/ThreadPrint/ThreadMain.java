package ThreadPrint;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadMain {

    public static void main(String[] args) {
        ThreadLocal<Integer> threadLocal1 = new ThreadLocal<>();
        ThreadLocal<String> threadLocal2 = new ThreadLocal<>();

        new Thread(() -> {
            threadLocal1.set(1);
            System.out.println(threadLocal1.get());
        }).start();
        new Thread(() -> {
            threadLocal2.set("a");
            System.out.println(threadLocal2.get());
        }).start();

        LinkedHashMap<String, String> linkedHashMap =
                new LinkedHashMap<String, String>(10, 0.75f, true);


        System.out.println(threadLocal1.get());
        System.out.println(threadLocal2.get());
    }
}
