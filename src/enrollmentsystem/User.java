package enrollmentsystem;
import java.time.LocalDateTime;

public abstract class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String access;
    private LocalDateTime createdAt;
    private boolean isActive;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public User(int userId, String username, String email, String password,
                String access, LocalDateTime createdAt, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.access = access;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return String.format("[%s] %s <%s>", access, username, email);
    }
}
