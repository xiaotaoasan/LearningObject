package jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxy {
    public static void main(String[] args) {
        Tank tank = new Tank();
        // 第一种实现动态代理的方式
        // 生成实现了MovAlbe接口的代理类$Proxy，只不过这个类在内存中，我们看不到，向这个$Proxy类传了一个InvokeHandler对象
        MoveAble moveAble = (MoveAble) Proxy.newProxyInstance(Tank.class.getClassLoader(), Tank.class.getInterfaces(), new JdkProxyTest(tank));
        moveAble.move(); // 实际是$Proxy类调的move，然后这个move又掉了InvokeHandler对象的invoke方法。


        // 第二种匿名内部类
        MoveAble moveAble2 = (MoveAble) Proxy.newProxyInstance(Tank.class.getClassLoader(), Tank.class.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("======");
                method.invoke(tank, args);
                System.out.println(".......");
                return null;
            }
        });

        moveAble2.move();
    }
}

class JdkProxyTest implements InvocationHandler {
    Tank tank;

    JdkProxyTest(Tank tank) {
        this.tank = tank;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 这个proxy是生成的动态代理类 $Proxy,这里没啥用，因为没有用到。
        System.out.println(proxy.getClass().getName());
        System.out.println("======");
        return method.invoke(tank, args);
    }
}

class Tank implements MoveAble {
    @Override
    public void move() {
        System.out.println("Tank move");
    }
}

interface MoveAble {
    void move();
}
