package enrollmentsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    public void initialize() {
        yearLevelCombo.getItems().addAll("Grade 11", "Grade 12", "1st Year", "2nd Year", "3rd Year", "4th Year");
        courseCombo.getItems().addAll("BSIT", "BSCS", "BSEd", "BSBA");
        studentTypeCombo.getItems().addAll("New", "Transferee", "Returning");
    }

    @FXML
    private void onUploadPhoto() {
        System.out.println("Upload 1x1 photo clicked");
    }

    @FXML
    private void onUploadBirthCert() {
        System.out.println("Upload birth certificate clicked");
    }

    @FXML
    private void onUploadReportCard() {
        System.out.println("Upload report card clicked");
    }

    @FXML
    private void onBack() {
        System.out.println("Back button clicked");
        // Navigation logic can be added here (switch to Enrollment1.fxml)
    }

    @FXML
    private void onSubmit() {
        System.out.println("Submit button clicked");
        // Submit or save logic here
    }
}
