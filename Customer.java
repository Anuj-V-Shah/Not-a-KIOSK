public final class Customer extends BaseUser {
    public Customer(String username, String displayName, char[] password) {
        super(username, displayName, password);
    }

    public Customer(String username, char[] password) {
        this(username, username, password);
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}
