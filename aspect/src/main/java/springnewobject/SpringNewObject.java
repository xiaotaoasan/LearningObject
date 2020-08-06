package springnewobject;


import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.springframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

public class SpringNewObject {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        User user = (User) applicationContext.getBean("user");
        user.setBeanName("user");

        System.out.println(user);
        System.out.println(user.toString());
        get(1,null);
        System.out.println(applicationContext.getBean(User.class));

    }

    public static void get(@Range(min = 2,max = 10) Integer a,@NotBlank String name){
        System.out.println("ok");
    }
}
