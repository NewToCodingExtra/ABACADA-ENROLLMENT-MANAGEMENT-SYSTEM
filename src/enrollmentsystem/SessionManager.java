package enrollmentsystem;

public class SessionManager {
    private static SessionManager instance;
    private int userId;
    private String enrolleeId;
    private String adminId;
    private String studentId;
    private String facultyId;
    private String cashierId;

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
    public void setUserId(int userId) { this.userId = userId; System.out.println("UserId: "+ userId);}
    public void setEnrolleeId(String enrolleeId) { this.enrolleeId = enrolleeId; System.out.println("enrolleeId: "+ enrolleeId);}
    public int getUserId() { return userId; }
    public String getEnrolleeId() { return enrolleeId; }
    public String getAdminId() {return adminId;}
    public void setAdminId(String adminId) {this.adminId = adminId; System.out.println("adminId: "+ adminId);}
    public String getStudentId() {return studentId;}
    public void setStudentId(String studentId) {this.studentId = studentId; System.out.println("studentId: "+ studentId);}
    public String getFacultyId() {return facultyId;}
    public void setFacultyId(String facultyId) {this.facultyId = facultyId; System.out.println("facultyId: "+ facultyId);}
    public String getCashierId() {return cashierId;}
    public void setCashierId(String cashierId) {this.cashierId = cashierId; System.out.println("cashierId: "+ cashierId);}

}
