package enrollmentsystem;

public class SessionManager {
    private static SessionManager instance;
    
    private int userId;
    private String enrolleeId;
    private String adminId;
    private String studentId;
    private String facultyId;
    private String cashierId;
    private String userAccess; // Store user role/access level
    
    private SessionManager() {} 
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    // Set full session
    public void setSession(int userId, String enrolleeId) {
        this.userId = userId;
        this.enrolleeId = enrolleeId;
    }
    
    // User ID
    public void setUserId(int userId) { 
        this.userId = userId; 
        System.out.println("UserId: " + userId);
    }
    public int getUserId() { 
        return userId; 
    }
    
    // Enrollee ID
    public void setEnrolleeId(String enrolleeId) { 
        this.enrolleeId = enrolleeId; 
        System.out.println("enrolleeId: " + enrolleeId);
    }
    public String getEnrolleeId() { 
        return enrolleeId; 
    }
    
    // Admin ID
    public void setAdminId(String adminId) {
        this.adminId = adminId; 
        System.out.println("adminId: " + adminId);
    }
    public String getAdminId() {
        return adminId;
    }
    
    // Student ID
    public void setStudentId(String studentId) {
        this.studentId = studentId; 
        System.out.println("studentId: " + studentId);
    }
    public String getStudentId() {
        return studentId;
    }
    
    // Faculty ID
    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId; 
        System.out.println("facultyId: " + facultyId);
    }
    public String getFacultyId() {
        return facultyId;
    }
    
    // Cashier ID
    public void setCashierId(String cashierId) {
        this.cashierId = cashierId; 
        System.out.println("cashierId: " + cashierId);
    }
    public String getCashierId() {
        return cashierId;
    }
    
    // User Access/Role
    public void setUserAccess(String userAccess) {
        this.userAccess = userAccess;
        System.out.println("userAccess: " + userAccess);
    }
    public String getUserAccess() {
        return userAccess;
    }
    
    /**
     * Clear all session data (for logout)
     */
    public void clearSession() {
        this.userId = 0;
        this.enrolleeId = null;
        this.adminId = null;
        this.studentId = null;
        this.facultyId = null;
        this.cashierId = null;
        this.userAccess = null;
        System.out.println("Session cleared");
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return userId > 0;
    }
    
    /**
     * Get current role ID based on access level
     */
    public String getCurrentRoleId() {
        if (userAccess == null) return null;
        
        switch (userAccess.toLowerCase()) {
            case "admin":
                return adminId;
            case "student":
                return studentId;
            case "faculty":
                return facultyId;
            case "cashier":
                return cashierId;
            case "enrollee":
                return enrolleeId;
            default:
                return null;
        }
    }
    
    @Override
    public String toString() {
        return "SessionManager{" +
                "userId=" + userId +
                ", enrolleeId='" + enrolleeId + '\'' +
                ", adminId='" + adminId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", facultyId='" + facultyId + '\'' +
                ", cashierId='" + cashierId + '\'' +
                ", userAccess='" + userAccess + '\'' +
                '}';
    }
}