package enrollmentsystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class Enrollment1Controller {
    
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField ageField;
    @FXML private TextField contactField;
    @FXML private TextField provinceField;
    @FXML private TextField cityField;
    @FXML private TextField barangayField;
    @FXML private TextField streetField;
    @FXML private TextField houseNoField;
    @FXML private Button homeButton;
    @FXML private Button nextButton;
    
    private Enrollee currentEnrollee;
    
    @FXML
    public void initialize() {
        // Load existing enrollee data when form opens
        loadEnrolleeData();
    }
    
    /**
     * Loads enrollee data from database using Enrollee class
     */
    private void loadEnrolleeData() {
        String enrolleeId = SessionManager.getInstance().getEnrolleeId();
        Integer userId = SessionManager.getInstance().getUserId();
        
        if (enrolleeId == null || userId == null || userId == 0) {
            System.out.println("No enrollee session found, starting fresh form");
            currentEnrollee = new Enrollee();
            return;
        }
        
        // Fetch enrollee from database
        currentEnrollee = fetchEnrolleeFromDatabase(enrolleeId, userId);
        
        if (currentEnrollee != null) {
            populateFieldsFromEnrollee(currentEnrollee);
            System.out.println("Loaded existing enrollee data for: " + enrolleeId);
        } else {
            System.out.println("No existing data found, creating new enrollee");
            currentEnrollee = new Enrollee();
            currentEnrollee.setEnrolleeId(enrolleeId);
            currentEnrollee.setUserId(userId);
        }
    }
    
    /**
     * Fetches Enrollee object from database
     */
    private Enrollee fetchEnrolleeFromDatabase(String enrolleeId, int userId) {
        String query = "SELECT e.*, u.username, u.email, u.password, u.access, u.created_at, u.is_active " +
                      "FROM enrollees e " +
                      "JOIN users u ON e.user_id = u.user_id " +
                      "WHERE e.enrollee_id = ? AND e.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrolleeId);
            ps.setInt(2, userId);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return createEnrolleeFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading enrollee data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Creates Enrollee object from ResultSet
     */
    private Enrollee createEnrolleeFromResultSet(ResultSet rs) throws SQLException {
        // Get User fields
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String access = rs.getString("access");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        boolean isActive = rs.getBoolean("is_active");
        
        // Get Enrollee fields
        String enrolleeId = rs.getString("enrollee_id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String suffix = rs.getString("suffix");
        Date birthDateSql = rs.getDate("birth_date");
        LocalDate birthDate = birthDateSql != null ? birthDateSql.toLocalDate() : null;
        String gender = rs.getString("gender");
        String address = rs.getString("address");
        String province = rs.getString("province");
        String city = rs.getString("city");
        String contactNumber = rs.getString("contact_number");
        String emailAddress = rs.getString("email_address");
        String guardianName = rs.getString("guardian_name");
        String guardianContact = rs.getString("guardian_contact");
        String lastSchoolAttended = rs.getString("last_school_attended");
        String lastSchoolYear = rs.getString("last_school_year");
        String programAppliedFor = rs.getString("program_applied_for");
        String enrollmentStatus = rs.getString("enrollment_status");
        Timestamp dateAppliedTs = rs.getTimestamp("date_applied");
        LocalDateTime dateApplied = dateAppliedTs != null ? dateAppliedTs.toLocalDateTime() : null;
        Integer reviewedBy = (Integer) rs.getObject("reviewed_by");
        Timestamp reviewedOnTs = rs.getTimestamp("reviewed_on");
        LocalDateTime reviewedOn = reviewedOnTs != null ? reviewedOnTs.toLocalDateTime() : null;
        
        // Create and return Enrollee object
        return new Enrollee(userId, username, email, password, access, createdAt, isActive,
                          enrolleeId, firstName, lastName, suffix, birthDate, gender,
                          address, province, city, contactNumber, emailAddress,
                          guardianName, guardianContact, lastSchoolAttended, lastSchoolYear,
                          programAppliedFor, enrollmentStatus, dateApplied, reviewedBy, reviewedOn);
    }
    
    /**
     * Populates form fields from Enrollee object
     */
    private void populateFieldsFromEnrollee(Enrollee enrollee) {
        if (enrollee.getFirstName() != null) {
            firstNameField.setText(enrollee.getFirstName());
        }
        if (enrollee.getLastName() != null) {
            lastNameField.setText(enrollee.getLastName());
        }
        if (enrollee.getProvince() != null) {
            provinceField.setText(enrollee.getProvince());
        }
        if (enrollee.getCity() != null) {
            cityField.setText(enrollee.getCity());
        }
        if (enrollee.getContactNumber() != null) {
            contactField.setText(enrollee.getContactNumber());
        }
        
        // Parse address into individual fields
        if (enrollee.getAddress() != null && !enrollee.getAddress().trim().isEmpty()) {
            parseAndFillAddress(enrollee.getAddress());
        }
        
        // Calculate and display age from birth date
        if (enrollee.getBirthDate() != null) {
            int age = Period.between(enrollee.getBirthDate(), LocalDate.now()).getYears();
            ageField.setText(String.valueOf(age));
        }
    }
    
    /**
     * Parses address string and fills individual address fields
     * Expected format: "HouseNo, Street, Barangay"
     */
    private void parseAndFillAddress(String address) {
        String[] parts = address.split(",");
        if (parts.length >= 1) houseNoField.setText(parts[0].trim());
        if (parts.length >= 2) streetField.setText(parts[1].trim());
        if (parts.length >= 3) barangayField.setText(parts[2].trim());
    }
    
    /**
     * Updates Enrollee object with current form values
     */
    private void updateEnrolleeFromFields() {
        currentEnrollee.setFirstName(firstNameField.getText().trim());
        currentEnrollee.setLastName(lastNameField.getText().trim());
        currentEnrollee.setProvince(provinceField.getText().trim());
        currentEnrollee.setCity(cityField.getText().trim());
        currentEnrollee.setContactNumber(contactField.getText().trim());
        
        // Build complete address
        String fullAddress = String.format("%s, %s, %s", 
            houseNoField.getText().trim(),
            streetField.getText().trim(),
            barangayField.getText().trim()
        );
        currentEnrollee.setAddress(fullAddress);
        
        // Calculate birth date from age (approximate)
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            LocalDate birthDate = LocalDate.now().minusYears(age);
            currentEnrollee.setBirthDate(birthDate);
        } catch (NumberFormatException e) {
            System.err.println("Invalid age format");
        }
    }
    
    @FXML
    private void homeBtnAction(ActionEvent event) {
        if (validateAllFields()) {
            ProceedDialogHelper.navigateToLogin();
        } else {
            int choice = JOptionPane.showConfirmDialog(null, 
                "You have unsaved changes. Back to login page?", 
                "Warning", 
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                ProceedDialogHelper.navigateToLogin();
            }
        }
    }
    
    @FXML
    private void nextBtnAction(ActionEvent event) {
        if (validateAllFields()) {
            // Update enrollee object with current form values
            updateEnrolleeFromFields();
            
            // Save or update enrollee data
            if (saveEnrollee(currentEnrollee)) {
                WindowOpener.openSceneWithCSS(
                    "/enrollmentsystem/Enrollment2.fxml", 
                    "/enrollment.css", 
                    "ABAKADA UNIVERSITY - ENROLLEE FORM", 
                    902, 504
                );
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Failed to save enrollment data. Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, 
                "Please fill in all required fields before proceeding.", 
                "Incomplete Form", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Saves Enrollee object to database (insert or update)
     */
    private boolean saveEnrollee(Enrollee enrollee) {
        if (enrollee.getEnrolleeId() == null) {
            System.err.println("No enrollee ID found");
            return false;
        }
        
        // Check if record exists
        if (checkIfEnrolleeExists(enrollee.getEnrolleeId(), enrollee.getUserId())) {
            return updateEnrolleeInDatabase(enrollee);
        } else {
            return insertEnrolleeInDatabase(enrollee);
        }
    }
    
    /**
     * Checks if enrollee record exists in database
     */
    private boolean checkIfEnrolleeExists(String enrolleeId, int userId) {
        String query = "SELECT COUNT(*) FROM enrollees WHERE enrollee_id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrolleeId);
            ps.setInt(2, userId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking enrollee existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
  
    private boolean insertEnrolleeInDatabase(Enrollee enrollee) {
        String query = "INSERT INTO enrollees (enrollee_id, user_id, first_name, last_name, " +
                      "birth_date, address, province, city, contact_number, has_filled_up_form) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrollee.getEnrolleeId());
            ps.setInt(2, enrollee.getUserId());
            ps.setString(3, enrollee.getFirstName());
            ps.setString(4, enrollee.getLastName());
            ps.setDate(5, enrollee.getBirthDate() != null ? Date.valueOf(enrollee.getBirthDate()) : null);
            ps.setString(6, enrollee.getAddress());
            ps.setString(7, enrollee.getProvince());
            ps.setString(8, enrollee.getCity());
            ps.setString(9, enrollee.getContactNumber());
            ps.setBoolean(10, false);  
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Enrollee data inserted successfully");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting enrollee data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    private boolean updateEnrolleeInDatabase(Enrollee enrollee) {
        String query = "UPDATE enrollees SET first_name = ?, last_name = ?, birth_date = ?, " +
                      "address = ?, province = ?, city = ?, contact_number = ? " +
                      "WHERE enrollee_id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrollee.getFirstName());
            ps.setString(2, enrollee.getLastName());
            ps.setDate(3, enrollee.getBirthDate() != null ? Date.valueOf(enrollee.getBirthDate()) : null);
            ps.setString(4, enrollee.getAddress());
            ps.setString(5, enrollee.getProvince());
            ps.setString(6, enrollee.getCity());
            ps.setString(7, enrollee.getContactNumber());
            ps.setString(8, enrollee.getEnrolleeId());
            ps.setInt(9, enrollee.getUserId());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Enrollee data updated successfully");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating enrollee data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
   
    private boolean validateAllFields() {
        List<String> emptyFields = new ArrayList<>();
        
        if (isFieldEmpty(lastNameField)) emptyFields.add("Last Name");
        if (isFieldEmpty(firstNameField)) emptyFields.add("First Name");
        if (isFieldEmpty(middleNameField)) emptyFields.add("Middle Name");
        if (isFieldEmpty(ageField)) emptyFields.add("Age");
        if (isFieldEmpty(contactField)) emptyFields.add("Contact Number");
        if (isFieldEmpty(provinceField)) emptyFields.add("Province");
        if (isFieldEmpty(cityField)) emptyFields.add("City");
        if (isFieldEmpty(barangayField)) emptyFields.add("Barangay");
        if (isFieldEmpty(streetField)) emptyFields.add("Street");
        if (isFieldEmpty(houseNoField)) emptyFields.add("House Number");
        
        // Validate age is a number
        if (!isFieldEmpty(ageField)) {
            try {
                int age = Integer.parseInt(ageField.getText().trim());
                if (age < 1 || age > 150) {
                    emptyFields.add("Age (invalid value)");
                }
            } catch (NumberFormatException e) {
                emptyFields.add("Age (must be a number)");
            }
        }
        
        if (!emptyFields.isEmpty()) {
            showValidationDialog(emptyFields);
            return false;
        }
        
        return true;
    }

    private boolean isFieldEmpty(TextField field) {
        return field == null || field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showValidationDialog(List<String> emptyFields) {
        StringBuilder message = new StringBuilder("Please fill in the following required fields:\n\n");
        
        for (String fieldName : emptyFields) {
            message.append("• ").append(fieldName).append("\n");
        }
        
        javax.swing.JOptionPane.showMessageDialog(
            null,
            message.toString(),
            "Incomplete Form",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
    }
}