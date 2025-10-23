package enrollmentsystem;

public class SessionManager {
    private static SessionManager instance;
    private int userId;
    private String enrolleeId;

    private SessionManager() {} 

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSession(int userId, String enrolleeId) {
        this.userId = userId;
        this.enrolleeId = enrolleeId;
    }
    public void setUserId(int userId) { this.userId = userId; }
    public void setEnrolleeId(String enrolleeId) { this.enrolleeId = enrolleeId; }
    
    public int getUserId() { return userId; }
    public String getEnrolleeId() { return enrolleeId; }
}
