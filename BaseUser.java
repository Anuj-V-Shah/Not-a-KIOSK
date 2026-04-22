import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class BaseUser implements Authenticatable {
    private final String username;
    private final String displayName;
    private final byte[] passwordHash;

    protected BaseUser(String username, String displayName, char[] password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        this.username = username.trim();
        this.displayName = (displayName == null || displayName.isBlank()) ? this.username : displayName.trim();
        this.passwordHash = hashPassword(password);
    }

    public final String getUsername() {
        return username;
    }

    public final String getDisplayName() {
        return displayName;
    }

    public abstract String getRole();

    @Override
    public final boolean authenticate(String username, char[] password) {
        if (username == null) return false;
        if (!this.username.equals(username.trim())) return false;
        byte[] attempt = hashPassword(password);
        return MessageDigest.isEqual(this.passwordHash, attempt);
    }

    private static byte[] hashPassword(char[] password) {
        if (password == null) password = new char[0];
        byte[] bytes = new String(password).getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
