package factory;

public class Factorymain {
    public static void main(String[] args) {
        FactoryTest factoryTest = new FactoryTest();
        Apple redApple= factoryTest.createApple("red");
        System.out.println(redApple);
    }
}
