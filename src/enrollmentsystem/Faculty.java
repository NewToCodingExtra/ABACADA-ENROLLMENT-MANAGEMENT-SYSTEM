/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Faculty extends User implements UniqueIDGenerator, Schedulable {
    private String facultyId;
    private String firstName;
    private String lastName;
    private String department;
    private String specialization;
    private int yearsOfService;
    private int subjectHolding;
    private int sectionsHandled;
    
    public Faculty() {
        super();
    }
    
    public Faculty(int userId, String username, String email, String password,
                   String access, LocalDateTime createdAt, boolean isActive, String facultyId) {
        super(userId, username, email, password, access, createdAt, isActive);
        this.facultyId = facultyId;
    }
    
    public Faculty(int userId, String username, String email, String password,
                   LocalDateTime createdAt, boolean isActive) {
        super(userId, username, email, password, "Faculty", createdAt, isActive);
    }
    
    // Getters
    public String getFacultyId() { return facultyId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDepartment() { return department; }
    public String getSpecialization() { return specialization; }
    public int getYearsOfService() { return yearsOfService; }
    public int getSubjectHolding() { return subjectHolding; }
    public int getSectionsHandled() { return sectionsHandled; }
    
    // Setters
    public void setFacultyId(String facultyId) { 
        this.facultyId = facultyId;
        SessionManager.getInstance().setFacultyId(facultyId);
    }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDepartment(String department) { this.department = department; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setYearsOfService(int yearsOfService) { this.yearsOfService = yearsOfService; }
    public void setSubjectHolding(int subjectHolding) { this.subjectHolding = subjectHolding; }
    public void setSectionsHandled(int sectionsHandled) { this.sectionsHandled = sectionsHandled; }
    
    @Override
    public String generateID() {
        String prefix = "FAC";
        int year = LocalDate.now().getYear() % 100;
        int randomNum = new Random().nextInt(9000) + 1000;
        return String.format("%s%02d-%04d", prefix, year, randomNum);
    }
    
    @Override
    public boolean createSchedule() {
        System.out.println("Creating schedule for faculty: " + facultyId);
        return true;
    }
    
    /**
     * Load faculty data from database by faculty ID
     * @param facultyId the faculty ID to load
     * @return Faculty object or null if not found
     */
    public static Faculty loadById(String facultyId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT f.*, u.username, u.email, u.password, u.access, " +
                        "u.created_at, u.is_active FROM faculty f " +
                        "JOIN users u ON f.user_id = u.user_id WHERE f.faculty_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Faculty faculty = new Faculty(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("access"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("is_active"),
                    rs.getString("faculty_id")
                );
                faculty.setFirstName(rs.getString("first_name"));
                faculty.setLastName(rs.getString("last_name"));
                faculty.setDepartment(rs.getString("department"));
                faculty.setSpecialization(rs.getString("specialization"));
                faculty.setYearsOfService(rs.getInt("years_of_service"));
                faculty.setSubjectHolding(rs.getInt("subject_holding"));
                faculty.setSectionsHandled(rs.getInt("sections_handled"));
                return faculty;
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading faculty: " + e.getMessage());
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
     * Update subject and section counts from course_offerings
     */
    public void updateTeachingLoad() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return;
            
            // Count distinct courses (subjects) and sections
            String sql = "SELECT COUNT(DISTINCT course_id) as subject_count, " +
                        "COUNT(DISTINCT section_id) as section_count " +
                        "FROM course_offerings WHERE faculty_id = ? AND status = 'Open'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                this.subjectHolding = rs.getInt("subject_count");
                this.sectionsHandled = rs.getInt("section_count");
                
                // Update in database
                String updateSql = "UPDATE faculty SET subject_holding = ?, sections_handled = ? WHERE faculty_id = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setInt(1, subjectHolding);
                pstmt.setInt(2, sectionsHandled);
                pstmt.setString(3, facultyId);
                pstmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating teaching load: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get list of subjects taught by this faculty
     * @return List of course titles
     */
    public List<String> getSubjectsList() {
        List<String> subjects = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return subjects;
            
            String sql = "SELECT DISTINCT c.course_title FROM course_offerings co " +
                        "JOIN courses c ON co.course_id = c.course_id " +
                        "WHERE co.faculty_id = ? AND co.status = 'Open'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                subjects.add(rs.getString("course_title"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting subjects list: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return subjects;
    }
    
    @Override
    public String toString() {
        return "Faculty{" +
               "facultyId='" + facultyId + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", department='" + department + '\'' +
               ", specialization='" + specialization + '\'' +
               ", yearsOfService=" + yearsOfService +
               ", subjectHolding=" + subjectHolding +
               ", sectionsHandled=" + sectionsHandled +
               ", username='" + getUsername() + '\'' +
               ", email='" + getEmail() + '\'' +
               '}';
    }
}