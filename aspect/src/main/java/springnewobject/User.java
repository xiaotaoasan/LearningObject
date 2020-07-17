package springnewobject;


import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
public class User {

    @NotBlank
    private String name;
    @NotBlank
    @Range(min = 2, max = 10, message = "not correct")
    private Integer age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
