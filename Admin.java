public final class Admin extends BaseUser {
    public Admin(String username, String displayName, char[] password) {
        super(username, displayName, password);
    }

    public Admin(String username, char[] password) {
        this(username, username, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public boolean canAccessManagerDashboard() {
        return true;
    }
}
