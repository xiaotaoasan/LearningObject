package springaop;


import lombok.NonNull;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import springnewobject.User;

@Aspect
@Component
@Validated
public class PrintLogAspect {

    @Pointcut("@annotation(springaop.PrintLog)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint proceedingJoinPoint) throws Exception {
//        Signature signature = proceedingJoinPoint.getSignature();
//        System.out.println(signature.getName()); //  方法名
//        Object[] args = proceedingJoinPoint.getArgs();
//
//        System.out.println(args);
        System.out.println("before");
    }

    @After("pointCut()")
    public void after(JoinPoint proceedingJoinPoint) {
        System.out.println("after");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object object = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return object;
    }

    public void getString(){

    }
}
