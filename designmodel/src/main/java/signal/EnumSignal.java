package signal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.omg.CORBA.INTERNAL;

/**
 * 使用枚举实现单例模式
 */

public enum EnumSignal {
    GREEN("green", 10),
    RED("red", 20),
    BLUE("bule", 30);
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    EnumSignal(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
