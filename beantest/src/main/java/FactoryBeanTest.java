import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FactoryBeanTest {

    @Bean
    public User getUser(){
        return new User();
    }
}
