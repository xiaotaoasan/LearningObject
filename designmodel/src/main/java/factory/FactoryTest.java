package factory;

public class FactoryTest {
    public Apple createApple(String name) {
        if (name.equals("red")) return new RedApple();
        if (name.equals("Black")) return new BlackApple();
        return null;
    }
}
