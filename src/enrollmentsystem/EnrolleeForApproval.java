package enrollmentsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.*;

/**
 * Model class for enrollees awaiting admin approval
 * Used in Admin Dashboard table
 */
public class EnrolleeForApproval {
    private final StringProperty enrolleeId;
    private final StringProperty studentName;
    private final StringProperty program;
    private final StringProperty yearLevel;
    private final StringProperty paymentStatus;
    private final IntegerProperty userId;
    
    public EnrolleeForApproval(String enrolleeId, String studentName, String program, 
                              String yearLevel, String paymentStatus, int userId) {
        this.enrolleeId = new SimpleStringProperty(enrolleeId);
        this.studentName = new SimpleStringProperty(studentName);
        this.program = new SimpleStringProperty(program);
        this.yearLevel = new SimpleStringProperty(yearLevel);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
        this.userId = new SimpleIntegerProperty(userId);
    }
    
    // Enrollee ID
    public String getEnrolleeId() { return enrolleeId.get(); }
    public void setEnrolleeId(String value) { enrolleeId.set(value); }
    public StringProperty enrolleeIdProperty() { return enrolleeId; }
    
    // Student Name
    public String getStudentName() { return studentName.get(); }
    public void setStudentName(String value) { studentName.set(value); }
    public StringProperty studentNameProperty() { return studentName; }
    
    // Program
    public String getProgram() { return program.get(); }
    public void setProgram(String value) { program.set(value); }
    public StringProperty programProperty() { return program; }
    
    // Year Level
    public String getYearLevel() { return yearLevel.get(); }
    public void setYearLevel(String value) { yearLevel.set(value); }
    public StringProperty yearLevelProperty() { return yearLevel; }
    
    // Payment Status
    public String getPaymentStatus() { return paymentStatus.get(); }
    public void setPaymentStatus(String value) { paymentStatus.set(value); }
    public StringProperty paymentStatusProperty() { return paymentStatus; }
    
    // User ID
    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }
    
    /**
     * Load all enrollees with verified payment and pending status
     */
    public static List<EnrolleeForApproval> loadPendingWithVerifiedPayment() {
        List<EnrolleeForApproval> list = new ArrayList<>();
        
        String query = "SELECT e.enrollee_id, e.first_name, e.middle_name, e.last_name, " +
                      "e.program_applied_for, e.year_level, e.payment_status, e.user_id " +
                      "FROM enrollees e " +
                      "WHERE e.enrollment_status = 'Pending' " +
                      "AND e.payment_status = 'Verified' " +
                      "AND e.has_filled_up_form = 1 " +
                      "ORDER BY e.date_applied ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String enrolleeId = rs.getString("enrollee_id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                
                // Build full name
                StringBuilder fullName = new StringBuilder();
                if (firstName != null) fullName.append(firstName);
                if (middleName != null && !middleName.isEmpty()) {
                    if (fullName.length() > 0) fullName.append(" ");
                    fullName.append(middleName);
                }
                if (lastName != null && !lastName.isEmpty()) {
                    if (fullName.length() > 0) fullName.append(" ");
                    fullName.append(lastName);
                }
                
                String program = rs.getString("program_applied_for");
                String yearLevel = rs.getString("year_level");
                String paymentStatus = rs.getString("payment_status");
                int userId = rs.getInt("user_id");
                
                EnrolleeForApproval enrollee = new EnrolleeForApproval(
                    enrolleeId,
                    fullName.toString(),
                    program,
                    yearLevel,
                    paymentStatus,
                    userId
                );
                
                list.add(enrollee);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading enrollees for approval: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
}