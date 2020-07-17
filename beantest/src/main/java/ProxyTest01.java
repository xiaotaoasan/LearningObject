public interface ProxyTest01 {
    default Object get(User user) {
        return user;
    }

    Object cut();
}
