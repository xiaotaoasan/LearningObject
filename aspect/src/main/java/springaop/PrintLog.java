package springaop;

import org.omg.CORBA.Environment;

import java.lang.annotation.*;

@Target(ElementType.METHOD)    // 注解修饰在什么地方
@Retention(RetentionPolicy.RUNTIME)    // 保留，什么时候保留
@Documented
public @interface PrintLog {
}
