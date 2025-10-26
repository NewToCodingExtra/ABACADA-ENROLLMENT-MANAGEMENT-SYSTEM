package enrollmentsystem;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.json.JSONObject;

public class Enrollment2Controller {
    @FXML private ComboBox<String> yearLevelCombo;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> courseCombo;
    @FXML private ComboBox<String> studentTypeCombo;
    @FXML private TextField previousSchoolField;
    @FXML private TextField schoolYearField;
    @FXML private Button uploadPhotoButton;
    @FXML private Button uploadBirthCertButton;
    @FXML private Button uploadReportCardButton;
    @FXML private Button backButton;
    @FXML private Button submitButton;
    @FXML private Button uploadForm137Btn;

    private Enrollee currentEnrollee;
    private String photoLink;
    private String birthCertLink;
    private String reportCardLink;
    private String form137Link;

    public void initialize() {
        yearLevelCombo.getItems().addAll("1st Year", "2nd Year", "3rd Year", "4th Year");
        courseCombo.getItems().addAll("BSIT", "BSCS", "BSEd", "BSBA");
        studentTypeCombo.getItems().addAll("New", "Transferee", "Returning");
        
        loadEnrolleeData();
    }

    private void loadEnrolleeData() {
        currentEnrollee = EnrolleeDataLoader.loadEnrolleeData();
        if (currentEnrollee != null) {
            populateFieldsFromEnrollee(currentEnrollee);
        }
    }

    private void populateFieldsFromEnrollee(Enrollee enrollee) {
        if (enrollee.getEmailAddress() != null) emailField.setText(enrollee.getEmailAddress());
        if (enrollee.getProgramAppliedFor() != null) courseCombo.setValue(enrollee.getProgramAppliedFor());
        if (enrollee.getLastSchoolAttended() != null) previousSchoolField.setText(enrollee.getLastSchoolAttended());
        if (enrollee.getLastSchoolYear() != null) schoolYearField.setText(enrollee.getLastSchoolYear());
        if (enrollee.getYearLevel() != null) yearLevelCombo.setValue(enrollee.getYearLevel());
        if (enrollee.getStudentType() != null) studentTypeCombo.setValue(enrollee.getStudentType());

        // Load existing file links
        photoLink = enrollee.getPhotoLink();
        birthCertLink = enrollee.getBirthCertLink();
        reportCardLink = enrollee.getReportCardLink();
        form137Link = enrollee.getForm137Link();
        
        updateButtonLabels();
    }

    private void updateButtonLabels() {
        if (photoLink != null && !photoLink.isEmpty()) {
            uploadPhotoButton.setText("✓ Photo Uploaded");
            uploadPhotoButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
        if (birthCertLink != null && !birthCertLink.isEmpty()) {
            uploadBirthCertButton.setText("✓ Birth Cert Uploaded");
            uploadBirthCertButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
        if (reportCardLink != null && !reportCardLink.isEmpty()) {
            uploadReportCardButton.setText("✓ Report Card Uploaded");
            uploadReportCardButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
        if (form137Link != null && !form137Link.isEmpty()) {
            uploadForm137Btn.setText("✓ Form 137 Uploaded");
            uploadForm137Btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
    }

    @FXML
    private void uploadPhotoBtnAction(ActionEvent event) {
        uploadFile("Photo", "image", photoLink, link -> {
            photoLink = link;
            uploadPhotoButton.setText("✓ Photo Uploaded");
            uploadPhotoButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        });
    }

    @FXML
    private void uploadBirthCirtBtnAction(ActionEvent event) {
        uploadFile("Birth Certificate", "document", birthCertLink, link -> {
            birthCertLink = link;
            uploadBirthCertButton.setText("✓ Birth Cert Uploaded");
            uploadBirthCertButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        });
    }

    @FXML
    private void uploadReportCardBtnAction(ActionEvent event) {
        uploadFile("Report Card", "document", reportCardLink, link -> {
            reportCardLink = link;
            uploadReportCardButton.setText("✓ Report Card Uploaded");
            uploadReportCardButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        });
    }

    @FXML
    private void uploadForm137BtnAction(ActionEvent event) {
        uploadFile("Form 137", "document", form137Link, link -> {
            form137Link = link;
            uploadForm137Btn.setText("✓ Form 137 Uploaded");
            uploadForm137Btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        });
    }

    private void uploadFile(String documentName, String fileType, String existingLink, FileUploadCallback callback) {
        // Check if file already exists and show warning
        if (existingLink != null && !existingLink.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(null,
                    documentName + " has already been uploaded.\n\n" +
                    "Do you want to replace it?\n" +
                    "Note: The old file will be deleted from Google Drive.",
                    "Replace Existing File?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                return; // User cancelled
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select " + documentName);
        
        if (fileType.equals("image")) {
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
            );
        } else {
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
        }

        Stage stage = (Stage) uploadPhotoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Validate file size (10MB limit)
            if (selectedFile.length() > 10 * 1024 * 1024) {
                JOptionPane.showMessageDialog(null,
                        "File size exceeds 10MB limit.\nPlease choose a smaller file.",
                        "File Too Large",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show progress dialog
            ProgressDialog progressDialog = new ProgressDialog("Uploading " + documentName + "...");
            progressDialog.show();

            // Create folder name: FirstName_LastName_EnrolleeID
            String folderName = String.format("%s_%s_%s", 
                currentEnrollee.getFirstName() != null ? currentEnrollee.getFirstName() : "Unknown",
                currentEnrollee.getLastName() != null ? currentEnrollee.getLastName() : "Unknown",
                currentEnrollee.getEnrolleeId() != null ? currentEnrollee.getEnrolleeId() : "Unknown"
            );

            // Upload in background thread
            Task<String> uploadTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    // Pass existing link for deletion
                    return DriveUploader.uploadToDrive(selectedFile, documentName, folderName, existingLink);
                }
            };

            uploadTask.setOnSucceeded(e -> {
                progressDialog.close();
                String response = uploadTask.getValue();
                
                System.out.println("Raw Response: " + response);
                
                try {
                    // Clean the response
                    String cleanedResponse = response.trim();
                    
                    if (cleanedResponse.contains("{") && cleanedResponse.contains("}")) {
                        int jsonStart = cleanedResponse.indexOf("{");
                        int jsonEnd = cleanedResponse.lastIndexOf("}") + 1;
                        cleanedResponse = cleanedResponse.substring(jsonStart, jsonEnd);
                    }
                    
                    System.out.println("Cleaned Response: " + cleanedResponse);
                    
                    JSONObject jsonResponse = new JSONObject(cleanedResponse);
                    
                    String status = jsonResponse.optString("status", "");
                    System.out.println("Status: " + status);
                    
                    if ("success".equalsIgnoreCase(status)) {
                        // Try "link" first, then "fileLink"
                        String fileLink = jsonResponse.optString("link", "");
                        if (fileLink.isEmpty()) {
                            fileLink = jsonResponse.optString("fileLink", "");
                        }
                        System.out.println("File Link: " + fileLink);
                        
                        if (fileLink != null && !fileLink.isEmpty()) {
                            // Update the link in memory
                            callback.onUploadSuccess(fileLink);
                            
                            // ✅ SAVE TO DATABASE IMMEDIATELY
                            if (saveFileLink(documentName, fileLink)) {
                                // Show success message
                                String message = documentName + " uploaded successfully!";
                                if (existingLink != null && !existingLink.isEmpty()) {
                                    message += "\n\nThe old file has been replaced.";
                                }
                                
                                JOptionPane.showMessageDialog(null,
                                        message,
                                        "Upload Successful",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        documentName + " uploaded to Drive but failed to save link to database.\n\n" +
                                        "Please try uploading again or contact support.",
                                        "Database Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Upload completed but no file link returned.\n\nResponse: " + cleanedResponse,
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        String errorMsg = jsonResponse.optString("message", "Unknown error");
                        System.out.println("Error Message: " + errorMsg);
                        
                        JOptionPane.showMessageDialog(null,
                                "Failed to upload " + documentName + ".\n\nError: " + errorMsg,
                                "Upload Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    System.err.println("JSON Parsing Error: " + ex.getMessage());
                    ex.printStackTrace();
                    
                    JOptionPane.showMessageDialog(null,
                            "Failed to parse upload response.\n\n" +
                            "Error: " + ex.getMessage() + "\n\n" +
                            "Raw Response: " + response,
                            "Parse Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            uploadTask.setOnFailed(e -> {
                progressDialog.close();
                Throwable exception = uploadTask.getException();
                System.err.println("Upload Task Failed: " + exception.getMessage());
                exception.printStackTrace();
                
                JOptionPane.showMessageDialog(null,
                        "Failed to upload " + documentName + ".\n\nError: " + exception.getMessage(),
                        "Upload Failed",
                        JOptionPane.ERROR_MESSAGE);
            });

            new Thread(uploadTask).start();
        }
    }

    @FXML
    private void backBtnAction(ActionEvent event) {
        int choice = JOptionPane.showConfirmDialog(null,
                "Any unsaved changes will be lost.\n\nGo back to previous page?",
                "Confirm Navigation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            WindowOpener.openSceneWithCSS("/enrollmentsystem/Enrollment1.fxml", 
                                 "/enrollment.css",
                                 "ABAKADA UNIVERSITY - ENROLLEE FORM",
                                 900, 520);
        }
    }

    @FXML
    private void SubmitBtnAction(ActionEvent event) {
        if (!validateAllFields()) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to submit your enrollment application?\n\n" +
                "Make sure all information is correct before submitting.\n" +
                "You will not be able to edit your application after submission.",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            updateEnrolleeFromFields();

            if (saveCompleteEnrollment()) {
                JOptionPane.showMessageDialog(null,
                        "Enrollment application submitted successfully!\n\n" +
                        "Your application status: PENDING\n" +
                        "Please wait for the admin to review your application.",
                        "Submission Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                
                WindowOpener.openSceneWithCSS("/enrollmentsystem/EnrolleeDashboard.fxml", 
                             "/enrolleedashboard.css",
                             "ABAKADA UNIVERSITY - ENROLLEE DASHBOARD",
                             1200, 700);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Failed to submit enrollment application.\n\n" +
                        "Please check your internet connection and try again.",
                        "Submission Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateEnrolleeFromFields() {
        currentEnrollee.setEmailAddress(emailField.getText().trim());
        currentEnrollee.setProgramAppliedFor(courseCombo.getValue());
        currentEnrollee.setYearLevel(yearLevelCombo.getValue());
        currentEnrollee.setStudentType(studentTypeCombo.getValue());
        currentEnrollee.setLastSchoolAttended(previousSchoolField.getText().trim());
        currentEnrollee.setLastSchoolYear(schoolYearField.getText().trim());
        
        currentEnrollee.setPhotoLink(photoLink);
        currentEnrollee.setBirthCertLink(birthCertLink);
        currentEnrollee.setReportCardLink(reportCardLink);
        currentEnrollee.setForm137Link(form137Link);
        currentEnrollee.setEnrollmentStatus("Pending");
        currentEnrollee.setDateApplied(LocalDateTime.now());
        currentEnrollee.setHasFilledUpForm(true);
    }

    private boolean saveCompleteEnrollment() {
        String query = "UPDATE enrollees SET email_address = ?, last_school_attended = ?, school_year_to_enroll = ?, " +
            "program_applied_for = ?, year_level = ?, student_type = ?, " +
            "photo_link = ?, birth_cert_link = ?, report_card_link = ?, form_137_link = ?, " +
            "enrollment_status = ?, date_applied = ?, has_filled_up_form = ? " +
            "WHERE enrollee_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (conn == null) {
                System.err.println("Database connection is null!");
                return false;
            }

            ps.setString(1, currentEnrollee.getEmailAddress());
            ps.setString(2, currentEnrollee.getLastSchoolAttended());
            ps.setString(3, currentEnrollee.getLastSchoolYear());
            ps.setString(4, currentEnrollee.getProgramAppliedFor());
            ps.setString(5, currentEnrollee.getYearLevel());
            ps.setString(6, currentEnrollee.getStudentType());
            ps.setString(7, currentEnrollee.getPhotoLink());
            ps.setString(8, currentEnrollee.getBirthCertLink());
            ps.setString(9, currentEnrollee.getReportCardLink());
            ps.setString(10, currentEnrollee.getForm137Link());
            ps.setString(11, currentEnrollee.getEnrollmentStatus());
            ps.setTimestamp(12, Timestamp.valueOf(currentEnrollee.getDateApplied()));
            ps.setBoolean(13, currentEnrollee.hasFilledUpForm());
            ps.setString(14, currentEnrollee.getEnrolleeId());
            ps.setInt(15, currentEnrollee.getUserId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Complete enrollment data saved successfully");
                return true;
            } else {
                System.err.println("No rows affected - enrollee record may not exist");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error saving complete enrollment: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private boolean validateAllFields() {
        List<String> emptyFields = new ArrayList<>();

        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            emptyFields.add("Email Address");
        } else if (!isValidEmail(emailField.getText().trim())) {
            emptyFields.add("Email Address (invalid format)");
        }

        if (courseCombo.getValue() == null) emptyFields.add("Course/Program");
        if (yearLevelCombo.getValue() == null) emptyFields.add("Year Level");
        if (studentTypeCombo.getValue() == null) emptyFields.add("Student Type");
        if (previousSchoolField.getText() == null || previousSchoolField.getText().trim().isEmpty()) {
            emptyFields.add("Previous School");
        }
        if (schoolYearField.getText() == null || schoolYearField.getText().trim().isEmpty()) {
            emptyFields.add("School Year to Enroll");
        }

        // Check uploaded files
        if (photoLink == null || photoLink.isEmpty()) emptyFields.add("Photo (not uploaded)");
        if (birthCertLink == null || birthCertLink.isEmpty()) emptyFields.add("Birth Certificate (not uploaded)");
        if (reportCardLink == null || reportCardLink.isEmpty()) emptyFields.add("Report Card (not uploaded)");
        if (form137Link == null || form137Link.isEmpty()) emptyFields.add("Form 137 (not uploaded)");

        if (!emptyFields.isEmpty()) {
            showValidationDialog(emptyFields);
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean saveFileLink(String documentName, String fileLink) {
        String columnName;
        
        // Map document name to database column
        switch (documentName) {
            case "Photo":
                columnName = "photo_link";
                break;
            case "Birth Certificate":
                columnName = "birth_cert_link";
                break;
            case "Report Card":
                columnName = "report_card_link";
                break;
            case "Form 137":
                columnName = "form_137_link";
                break;
            default:
                System.err.println("Unknown document type: " + documentName);
                return false;
        }
        
        String query = "UPDATE enrollees SET " + columnName + " = ? WHERE enrollee_id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            if (conn == null) {
                System.err.println("Database connection is null!");
                return false;
            }
            
            ps.setString(1, fileLink);
            ps.setString(2, currentEnrollee.getEnrolleeId());
            ps.setInt(3, currentEnrollee.getUserId());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(documentName + " link saved to database: " + fileLink);
                return true;
            } else {
                System.err.println("Failed to save " + documentName + " link - no rows affected");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving " + documentName + " link: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void showValidationDialog(List<String> emptyFields) {
        StringBuilder message = new StringBuilder("Please complete the following required fields:\n\n");

        for (String fieldName : emptyFields) {
            message.append("• ").append(fieldName).append("\n");
        }

        JOptionPane.showMessageDialog(
                null,
                message.toString(),
                "Incomplete Form",
                JOptionPane.WARNING_MESSAGE
        );
    }

    @FunctionalInterface
    interface FileUploadCallback {
        void onUploadSuccess(String fileLink);
    }

    private static class ProgressDialog {
        private final Alert alert;

        public ProgressDialog(String message) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uploading File");
            alert.setHeaderText(message);
            alert.setContentText("Please wait while the file is being uploaded to Google Drive...");
            alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        }

        public void show() {
            Platform.runLater(() -> alert.show());
        }

        public void close() {
            Platform.runLater(() -> alert.close());
        }
    }
}