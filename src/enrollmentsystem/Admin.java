package enrollmentsystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class Admin extends User implements UniqueIDGenerator, AccountCreation {

    private String adminId;
    private String firstName;
    private String lastName;
    

    public Admin() {
        super();
    }

    public Admin(int userId, String username, String email, String password,
                 String access, LocalDateTime createdAt, boolean isActive, String adminId) {
        super(userId, username, email, password, access, createdAt, isActive);
        this.adminId = adminId;
    }
    
    public Admin(int userId, String username, String email, String password,
                LocalDateTime createdAt, boolean isActive) {
        super(userId, username, email, password, "Admin", createdAt, isActive);
    }

    public String getAdminId() { return adminId; }
    public String getFName() { return firstName; }
    public String getLName() { return lastName; }
    
    public void setAdminId(String adminId) { this.adminId = adminId; SessionManager.getInstance().setAdminId(adminId); }
    public void setFirstName(String fn) { firstName = fn; }
    public void setLastName(String ln) { lastName = ln; }

    @Override
    public String generateID() {
        String prefix = "ADM";
        int year = LocalDate.now().getYear() % 100;
        int randomNum = new Random().nextInt(9000) + 1000;
        return String.format("%s%02d-%04d", prefix, year, randomNum);
    }

    @Override
    public void createAdmin(User user) throws SQLException {
        if (!(user instanceof Admin)) {
            throw new IllegalArgumentException("User must be an Admin instance");
        }
        Admin admin = (Admin) user;
        String adminId = admin.generateID();
        int userId = insertUser(user, "Admin");
        insertIntoRoleTable("admin", "admin_id", adminId, userId, admin);
        System.out.println("Admin created with ID: " + adminId);
    }

    @Override
    public void createCashier(User user) throws SQLException {
        if (!(user instanceof Cashier)) {
            throw new IllegalArgumentException("User must be a Cashier instance");
        }
        Cashier cashier = (Cashier) user;
        String cashierId = cashier.generateID();
        int userId = insertUser(user, "Cashier");
        insertIntoRoleTable("cashier", "cashier_id", cashierId, userId, cashier);
        System.out.println("Cashier created with ID: " + cashierId);
    }

    @Override
    public void createFaculty(User user) throws SQLException {
        if (!(user instanceof Faculty)) {
            throw new IllegalArgumentException("User must be a Faculty instance");
        }
        Faculty faculty = (Faculty) user;
        String facultyId = faculty.generateID();
        int userId = insertUser(user, "Faculty");
        insertIntoRoleTable("faculty", "faculty_id", facultyId, userId, faculty);
        System.out.println("Faculty created with ID: " + facultyId);
    }

    @Override
    public void createStudent(User user) throws SQLException {
        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User must be a Student instance");
        }
        Student student = (Student) user;
        String studentId = student.generateID();
        
        int userId = insertUser(user, "Student");

        insertStudent(studentId, userId, student);
        System.out.println("Student created with ID: " + studentId);
    }

    private int insertUser(User user, String access) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, access, created_at, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, access);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(6, true);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated user_id.");
                }
            }
        }
    }

    private void insertIntoRoleTable(String tableName, String idColumn, String generatedId,
                                     int userId, User user) throws SQLException {
        String sql = "INSERT INTO " + tableName +
                     " (user_id, " + idColumn + ", first_name, last_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, generatedId);
            
            String firstName = null;
            String lastName = null;
            
            if (user instanceof Admin) {
                Admin admin = (Admin) user;
                firstName = admin.getFName();
                lastName = admin.getLName();
                
                SessionManager.getInstance().setAdminId(idColumn);
            } 
            else if (user instanceof Cashier) {
                Cashier cashier = (Cashier) user;
                firstName = cashier.getFName();
                lastName = cashier.getLName();
                
                SessionManager.getInstance().setCashierId(idColumn);
            } else if (user instanceof Faculty) {
                Faculty faculty = (Faculty) user;
                firstName = faculty.getFirstName();
                lastName = faculty.getLastName();
                
                SessionManager.getInstance().setFacultyId(idColumn);
            } 
            
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.executeUpdate();
        }
    }
    private void insertStudent(String studentId, int userId, Student student) throws SQLException {
        String sql = "INSERT INTO students (student_id, user_id, program_id, enrollment_status, date_enrolled) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.setInt(2, userId);
            stmt.setString(3, student.getProgramId());  
            stmt.setString(4, student.getEnrollmentStatus());  
            stmt.setTimestamp(5, Timestamp.valueOf(student.getDateEnrolled())); 

            stmt.executeUpdate();
        }

        SessionManager.getInstance().setStudentId(studentId);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", access='" + getAccess() + '\'' +
                '}';
    }
}