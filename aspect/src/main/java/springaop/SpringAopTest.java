package springaop;


import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springnewobject.User;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableAspectJAutoProxy
public class SpringAopTest {
    public static void main(String[] args) {
        SpringApplication.run(SpringAopTest.class, args);
       // ApplicationContext ctx = new AnnotationConfigApplicationContext();
//        Object object = ctx.getBean(User.class);
//        System.out.println(object);
    }
}
