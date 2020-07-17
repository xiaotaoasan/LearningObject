package springnewobject.ataotest;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springnewobject.User;

import javax.jws.soap.SOAPBinding;

@Component
public class BeanUseTest {

    @Bean
    public User getUser(){
        return new User();
    }
}
