package signal;

public class SingleInstance {
    private static SingleInstance lazySingleInstance = new SingleInstance();

    private SingleInstance() {

    }

    public static SingleInstance getInstance() {
        return lazySingleInstance;
    }
}
