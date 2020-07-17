public class ProxyImplTest implements ProxyTest01 {
    @Override
    public Object get(User user) {
        return "aaaaaa";
    }

    @Override
    public Object cut() {
        return null;
    }
}
