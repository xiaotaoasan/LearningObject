package springaop;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

@Component
@Data
public class Sender implements BeanNameAware {
    private int age;

    @Override
    public void setBeanName(String name) {

    }

}
