package factory;

import java.sql.DatabaseMetaData;


/**
 * 实现统一的接口，传什么类型的，我就返回什么类型的对象
 *
 */
public class FactoryTest {
    public Apple createApple(String name) {
        if (name.equals("red")) return new RedApple();
        if (name.equals("Black")) return new BlackApple();
        return null;
    }

    interface Apple {
        String getColor();
    }

    class RedApple implements Apple {
        @Override
        public String getColor() {
            return "red";
        }
    }

    class BlackApple implements Apple {
        @Override
        public String getColor() {
            return "black";
        }
    }

    public static void main(String[] args) {
        FactoryTest factoryTest = new FactoryTest();
        Apple redApple = factoryTest.createApple("red");
        System.out.println(redApple.getColor());
    }
}
