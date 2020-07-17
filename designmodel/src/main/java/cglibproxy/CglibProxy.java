package cglibproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib代理是类的代理，不需要类去实现某一个接口，使用Enhancer增强器来执行,需要导包cglib
 */

public class CglibProxy {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(Tank.class); // 设置这个增强器的父类是我们想要代理的类,注意被代理的类不能是final的

        // 匿名内部类的方式
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println(o.getClass().getName()); // 这里的o是 Tank的代理类 cglibproxy.Tank$$EnhancerByCGLIB$$396b2847

                System.out.println("增强功能1");
                return methodProxy.invokeSuper(o, objects);
            }
        }); // 将methodInterceptor传给$Proxy代理类 ,类似JDK代理的InvocationHandler

        Tank tank = (Tank) enhancer.create(); // 生成类似$Proxy的动态代理对象

        tank.move(); // 调用代理类的move方法，里面调用了 MethodInterceptor 的 intercept 方法

        enhancer.setCallback(new TankMethodInterceptor()); // 将MethodInterceptor传给enhancer代理类
        Tank tank1 = (Tank) enhancer.create(); // 生成类似$Proxy的动态代理对象
        tank1.move(); // 实际调用的是 MethodInterceptor里面的intercept方法

    }

}

class Tank {
    public void move() {
        System.out.println("Tank move");
    }
}

class TankMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(o.getClass().getName()); // 这里的o是 Tank的代理类 cglibproxy.Tank$$EnhancerByCGLIB$$396b2847

        System.out.println("增强功能2");
        return methodProxy.invokeSuper(o, objects);
    }
}


