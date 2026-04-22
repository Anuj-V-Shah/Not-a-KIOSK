public interface Authenticatable {
    boolean authenticate(String username, char[] password);
}
