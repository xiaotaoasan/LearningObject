package signal;

public class LazySingle {

    private static volatile LazySingle lazySingle;

    private LazySingle() {

    }

    public static LazySingle getInstance() {
        if (lazySingle == null) {
            synchronized (LazySingle.class) {
                if (lazySingle == null) {
                    lazySingle = new LazySingle();
                }
            }
        }
        return lazySingle;
    }
}
