package enrollmentsystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;


public class Student extends User implements UniqueIDGenerator, Schedulable{
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
        // Placeholder for actual schedule logic (students typically enroll in schedules)
        System.out.println("Creating schedule for student: " + studentId);
        return true;
    }

    @Override
    public String toString() {
        return "Student{" +
               "studentId='" + studentId + '\'' +
               ", userId=" + userId +
               ", programId='" + programId + '\'' +
               ", enrollmentStatus='" + enrollmentStatus + '\'' +
               ", dateEnrolled=" + dateEnrolled +
               '}';
    }
}

