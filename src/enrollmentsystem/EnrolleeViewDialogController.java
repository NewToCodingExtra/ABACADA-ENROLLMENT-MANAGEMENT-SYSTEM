package enrollmentsystem;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for viewing enrollee details in read-only mode
 * Used by admin to review enrollment forms before approval
 */
public class EnrolleeViewDialogController {

    // Personal Information Fields
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField contactField;
    @FXML private TextField provinceField;
    @FXML private TextField cityField;
    @FXML private TextField barangayField;
    @FXML private TextField streetField;
    @FXML private TextField houseNoField;
    @FXML private DatePicker datePicker;
    @FXML private TextField genderField;
    
    // Academic Information Fields
    @FXML private ComboBox<String> yearLevelCombo;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> courseCombo;
    @FXML private ComboBox<String> studentTypeCombo;
    @FXML private TextField previousSchoolField;
    @FXML private TextField schoolYearField;
    @FXML private TextField guardianNameField;
    @FXML private TextField guardianContactField;
    
    // Document Upload Buttons (Modified to open Drive links)
    @FXML private Button uploadPhotoButton;
    @FXML private Button uploadBirthCertButton;
    @FXML private Button uploadReportCardButton;
    @FXML private Button uploadForm137Btn;
    
    // Action Buttons
    @FXML private Button closeButton;
    
    private Enrollee currentEnrollee;
    private Stage dialogStage;
    private String enrolleeIdToLoad;

    /**
     * CRITICAL: Set enrollee ID BEFORE showing dialog
     */
    public void setEnrolleeId(String enrolleeId) {
        this.enrolleeIdToLoad = enrolleeId;
        System.out.println("EnrolleeViewDialog: Set enrollee ID to load: " + enrolleeId);
    }

    public void initialize() {
        // Load will happen after setEnrolleeId is called
    }
    
    /**
     * Call this after setEnrolleeId to load and display data
     */
    public void loadAndDisplay() {
        if (enrolleeIdToLoad == null) {
            showError("Error", "No enrollee ID provided");
            return;
        }
        
        loadEnrolleeDataFromDatabase(enrolleeIdToLoad);
        disableAllFields();
        updateDocumentButtons();
    }
    
    /**
     * Sets the dialog stage (called by opener)
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    /**
     * Load enrollee data directly from database using enrollee_id
     */
    private void loadEnrolleeDataFromDatabase(String enrolleeId) {
        String query = "SELECT e.*, u.username, u.email, u.password, u.access, u.created_at, u.is_active " +
                       "FROM enrollees e " +
                       "JOIN users u ON e.user_id = u.user_id " +
                       "WHERE e.enrollee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, enrolleeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentEnrollee = createEnrolleeFromResultSet(rs);
                populateFields();
                System.out.println("Successfully loaded enrollee data for: " + enrolleeId);
            } else {
                showError("Error", "No enrollee found with ID: " + enrolleeId);
            }
        } catch (SQLException e) {
            System.err.println("Error loading enrollee data: " + e.getMessage());
            e.printStackTrace();
            showError("Database Error", "Failed to load enrollee data: " + e.getMessage());
        }
    }
    
    private Enrollee createEnrolleeFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String access = rs.getString("access");
        java.time.LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        boolean isActive = rs.getBoolean("is_active");

        String enrolleeId = rs.getString("enrollee_id");
        String firstName = rs.getString("first_name");
        String middleName = rs.getString("middle_name");
        String lastName = rs.getString("last_name");
        String suffix = rs.getString("suffix");
        Date birthDateSql = rs.getDate("birth_date");
        java.time.LocalDate birthDate = birthDateSql != null ? birthDateSql.toLocalDate() : null;
        String gender = rs.getString("gender");
        String address = rs.getString("address");
        String province = rs.getString("province");
        String city = rs.getString("city");
        String contactNumber = rs.getString("contact_number");
        String emailAddress = rs.getString("email_address");
        String guardianName = rs.getString("guardian_name");
        String guardianContact = rs.getString("guardian_contact");
        String lastSchoolAttended = rs.getString("last_school_attended");
        String lastSchoolYear = rs.getString("school_year_to_enroll");
        String yearLevel = rs.getString("year_level");
        String studentType = rs.getString("student_type");
        String programAppliedFor = rs.getString("program_applied_for");
        String enrollmentStatus = rs.getString("enrollment_status");
        Timestamp dateAppliedTs = rs.getTimestamp("date_applied");
        String photoLink = rs.getString("photo_link");
        String birthCertLink = rs.getString("birth_cert_link");
        String reportCardLink = rs.getString("report_card_link");
        String form137Link = rs.getString("form_137_link");
        java.time.LocalDateTime dateApplied = dateAppliedTs != null ? dateAppliedTs.toLocalDateTime() : null;
        Integer reviewedBy = (Integer) rs.getObject("reviewed_by");
        Timestamp reviewedOnTs = rs.getTimestamp("reviewed_on");
        java.time.LocalDateTime reviewedOn = reviewedOnTs != null ? reviewedOnTs.toLocalDateTime() : null;
        boolean hasFilledUpForm = rs.getBoolean("has_filled_up_form");

        Enrollee enrollee = new Enrollee(userId, username, email, password, access, createdAt, isActive,
                enrolleeId, firstName, middleName, lastName, suffix, birthDate, gender,
                address, province, city, contactNumber, emailAddress,
                guardianName, guardianContact, yearLevel, studentType, lastSchoolAttended, lastSchoolYear,
                programAppliedFor, enrollmentStatus, dateApplied, reviewedBy, reviewedOn);
        enrollee.setHasFilledUpForm(hasFilledUpForm);
        
        enrollee.setPhotoLink(photoLink);
        enrollee.setBirthCertLink(birthCertLink);
        enrollee.setReportCardLink(reportCardLink);
        enrollee.setForm137Link(form137Link);
        return enrollee;
    }
    
    private void populateFields() {
        if (currentEnrollee == null) return;
        
        // Personal Information
        if (currentEnrollee.getFirstName() != null) 
            firstNameField.setText(currentEnrollee.getFirstName());
        if (currentEnrollee.getMiddleName() != null) 
            middleNameField.setText(currentEnrollee.getMiddleName());
        if (currentEnrollee.getLastName() != null) 
            lastNameField.setText(currentEnrollee.getLastName());
        if (currentEnrollee.getProvince() != null) 
            provinceField.setText(currentEnrollee.getProvince());
        if (currentEnrollee.getCity() != null) 
            cityField.setText(currentEnrollee.getCity());
        if (currentEnrollee.getContactNumber() != null) 
            contactField.setText(currentEnrollee.getContactNumber());
        if (currentEnrollee.getGender() != null) 
            genderField.setText(currentEnrollee.getGender());
        
        // Parse address
        if (currentEnrollee.getAddress() != null && !currentEnrollee.getAddress().trim().isEmpty()) {
            parseAndFillAddress(currentEnrollee.getAddress());
        }
        
        // Birth date
        if (currentEnrollee.getBirthDate() != null) {
            datePicker.setValue(currentEnrollee.getBirthDate());
        }
        
        // Academic Information
        if (currentEnrollee.getEmailAddress() != null) 
            emailField.setText(currentEnrollee.getEmailAddress());
        if (currentEnrollee.getProgramAppliedFor() != null) 
            courseCombo.setValue(currentEnrollee.getProgramAppliedFor());
        if (currentEnrollee.getLastSchoolAttended() != null) 
            previousSchoolField.setText(currentEnrollee.getLastSchoolAttended());
        if (currentEnrollee.getLastSchoolYear() != null) 
            schoolYearField.setText(currentEnrollee.getLastSchoolYear());
        if (currentEnrollee.getYearLevel() != null) 
            yearLevelCombo.setValue(currentEnrollee.getYearLevel());
        if (currentEnrollee.getStudentType() != null) 
            studentTypeCombo.setValue(currentEnrollee.getStudentType());
        if (currentEnrollee.getGuardianName() != null) 
            guardianNameField.setText(currentEnrollee.getGuardianName());
        if (currentEnrollee.getGuardianContact() != null) 
            guardianContactField.setText(currentEnrollee.getGuardianContact());
    }
    
    private void parseAndFillAddress(String address) {
        String[] parts = address.split(",");
        if (parts.length >= 1) houseNoField.setText(parts[0].trim());
        if (parts.length >= 2) streetField.setText(parts[1].trim());
        if (parts.length >= 3) barangayField.setText(parts[2].trim());
    }
    
    /**
     * Disables all input fields for read-only view
     */
    private void disableAllFields() {
        // Disable text fields
        lastNameField.setDisable(true);
        firstNameField.setDisable(true);
        middleNameField.setDisable(true);
        contactField.setDisable(true);
        provinceField.setDisable(true);
        cityField.setDisable(true);
        barangayField.setDisable(true);
        streetField.setDisable(true);
        houseNoField.setDisable(true);
        emailField.setDisable(true);
        previousSchoolField.setDisable(true);
        schoolYearField.setDisable(true);
        guardianNameField.setDisable(true);
        guardianContactField.setDisable(true);
        
        if (genderField != null) genderField.setDisable(true);
        
        datePicker.setDisable(true);
        
        yearLevelCombo.setDisable(true);
        courseCombo.setDisable(true);
        studentTypeCombo.setDisable(true);
        
        String readOnlyStyle = "-fx-opacity: 1.0; -fx-background-color: #f5f5f5;";
        lastNameField.setStyle(readOnlyStyle);
        firstNameField.setStyle(readOnlyStyle);
        middleNameField.setStyle(readOnlyStyle);
        contactField.setStyle(readOnlyStyle);
        provinceField.setStyle(readOnlyStyle);
        cityField.setStyle(readOnlyStyle);
        barangayField.setStyle(readOnlyStyle);
        streetField.setStyle(readOnlyStyle);
        houseNoField.setStyle(readOnlyStyle);
        emailField.setStyle(readOnlyStyle);
        previousSchoolField.setStyle(readOnlyStyle);
        schoolYearField.setStyle(readOnlyStyle);
        guardianNameField.setStyle(readOnlyStyle);
        guardianContactField.setStyle(readOnlyStyle);
        if (genderField != null) genderField.setStyle(readOnlyStyle);
        datePicker.setStyle(readOnlyStyle);
    }

    private void updateDocumentButtons() {
        if (currentEnrollee == null) return;
        
        // Photo
        if (currentEnrollee.getPhotoLink() != null && !currentEnrollee.getPhotoLink().isEmpty()) {
            uploadPhotoButton.setText("📄 View Photo");
            uploadPhotoButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
            uploadPhotoButton.setOnAction(e -> openDriveLink(currentEnrollee.getPhotoLink(), "Photo"));
        } else {
            uploadPhotoButton.setText("❌ No Photo Uploaded");
            uploadPhotoButton.setDisable(true);
        }
        
        // Birth Certificate
        if (currentEnrollee.getBirthCertLink() != null && !currentEnrollee.getBirthCertLink().isEmpty()) {
            uploadBirthCertButton.setText("📄 View Birth Certificate");
            uploadBirthCertButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
            uploadBirthCertButton.setOnAction(e -> openDriveLink(currentEnrollee.getBirthCertLink(), "Birth Certificate"));
        } else {
            uploadBirthCertButton.setText("❌ No Birth Cert Uploaded");
            uploadBirthCertButton.setDisable(true);
        }
        
        // Report Card
        if (currentEnrollee.getReportCardLink() != null && !currentEnrollee.getReportCardLink().isEmpty()) {
            uploadReportCardButton.setText("📄 View Report Card");
            uploadReportCardButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
            uploadReportCardButton.setOnAction(e -> openDriveLink(currentEnrollee.getReportCardLink(), "Report Card"));
        } else {
            uploadReportCardButton.setText("❌ No Report Card Uploaded");
            uploadReportCardButton.setDisable(true);
        }
        
        // Form 137
        if (currentEnrollee.getForm137Link() != null && !currentEnrollee.getForm137Link().isEmpty()) {
            uploadForm137Btn.setText("📄 View Form 137");
            uploadForm137Btn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
            uploadForm137Btn.setOnAction(e -> openDriveLink(currentEnrollee.getForm137Link(), "Form 137"));
        } else {
            uploadForm137Btn.setText("❌ No Form 137 Uploaded");
            uploadForm137Btn.setDisable(true);
        }
    }

    private void openDriveLink(String driveLink, String documentName) {
        try {
            if (driveLink == null || driveLink.isEmpty()) {
                showError("No Link", documentName + " link is not available.");
                return;
            }
            
            // Open in default browser
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(driveLink));
                System.out.println("Opened " + documentName + " link: " + driveLink);
            } else {
                showError("Browser Error", "Cannot open browser. Please copy the link manually:\n\n" + driveLink);
            }
            
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error opening Drive link: " + e.getMessage());
            e.printStackTrace();
            showError("Error", 
                "Failed to open " + documentName + ".\n\n" +
                "Link: " + driveLink + "\n\n" +
                "Please copy and paste this link into your browser.");
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}