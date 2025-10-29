package enrollmentsystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

public class Student extends User implements UniqueIDGenerator, Schedulable {
    private String studentId;            
    private int userId;                  
    private String programId;             
    private String enrollmentStatus;     
    private LocalDateTime dateEnrolled;  

    public Student() {
        super();
        this.enrollmentStatus = "Active";
        this.dateEnrolled = LocalDateTime.now();
        userId = SessionManager.getInstance().getUserId();
    }

    public Student(int userId, String username, String email, String password,
                   String access, LocalDateTime createdAt, boolean isActive, String studentId) {
        super(userId, username, email, password, access, createdAt, isActive);
        this.studentId = studentId;
        this.userId = userId;
        this.enrollmentStatus = "Active";
        this.dateEnrolled = LocalDateTime.now();
    }

    public Student(int userId, String username, String email, String password,
                   LocalDateTime createdAt, boolean isActive) {
        super(userId, username, email, password, "Student", createdAt, isActive);
        this.userId = userId;
        this.enrollmentStatus = "Active";
        this.dateEnrolled = LocalDateTime.now();
    }

    // Getters
    public String getStudentId() { return studentId; }
    public int getUserId() { return userId; }
    public String getProgramId() { return programId; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public LocalDateTime getDateEnrolled() { return dateEnrolled; }

    // Setters
    public void setStudentId(String studentId) {
        this.studentId = studentId;
        SessionManager.getInstance().setStudentId(studentId);
    }
    public void setUserId(int userId) { this.userId = userId; }
    public void setProgramId(String programId) { this.programId = programId; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public void setDateEnrolled(LocalDateTime dateEnrolled) { this.dateEnrolled = dateEnrolled; }

    @Override
    public String generateID() {
        String prefix = "STU";
        int year = LocalDateTime.now().getYear() % 100;
        int randomNum = new Random().nextInt(9000) + 1000;
        return String.format("%s%02d-%04d", prefix, year, randomNum);
    }

    @Override
    public boolean createSchedule() {
        System.out.println("Creating schedule for student: " + studentId);
        return true;
    }
    
    /**
     * Load student data from database by student ID
     * @param studentId the student ID to load
     * @return Student object or null if not found
     */
    public static Student loadById(String studentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT s.*, u.username, u.email, u.password, u.access, " +
                        "u.created_at, u.is_active FROM students s " +
                        "JOIN users u ON s.user_id = u.user_id WHERE s.student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("access"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("is_active"),
                    rs.getString("student_id")
                );
                student.setProgramId(rs.getString("program_id"));
                student.setEnrollmentStatus(rs.getString("enrollment_status"));
                
                Timestamp enrolledTs = rs.getTimestamp("date_enrolled");
                if (enrolledTs != null) {
                    student.setDateEnrolled(enrolledTs.toLocalDateTime());
                }
                
                return student;
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading student: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * Get student's current enrolled courses count
     */
    public int getEnrolledCoursesCount() {
        if (studentId == null) return 0;
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT COUNT(*) as count FROM enrollments " +
                          "WHERE student_id = ? AND status = 'Enrolled'";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting enrolled courses count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Get student's GPA (if grades are stored)
     */
    public double getGPA() {
        if (studentId == null) return 0.0;
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT AVG(CAST(grade AS DECIMAL(3,2))) as gpa " +
                          "FROM enrollments " +
                          "WHERE student_id = ? AND grade IS NOT NULL " +
                          "AND grade REGEXP '^[0-9]+(\\.[0-9]+)?$'";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("gpa");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating GPA: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "Student{" +
               "studentId='" + studentId + '\'' +
               ", userId=" + userId +
               ", programId='" + programId + '\'' +
               ", enrollmentStatus='" + enrollmentStatus + '\'' +
               ", dateEnrolled=" + dateEnrolled +
               ", username='" + getUsername() + '\'' +
               ", email='" + getEmail() + '\'' +
               '}';
    }
}