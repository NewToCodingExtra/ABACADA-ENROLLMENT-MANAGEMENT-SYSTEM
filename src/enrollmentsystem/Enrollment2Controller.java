package enrollmentsystem;

import javafx.event.ActionEvent;
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
    private Button uploadForm137Btn;

    public void initialize() {
        yearLevelCombo.getItems().addAll("1st Year", "2nd Year", "3rd Year", "4th Year");
        courseCombo.getItems().addAll("BSIT", "BSCS", "BSEd", "BSBA");
        studentTypeCombo.getItems().addAll("New", "Transferee", "Returning");
    }

  

    @FXML
    private void uploadPhotoBtnAction(ActionEvent event) {
    }

    @FXML
    private void uploadBirthCirtBtnAction(ActionEvent event) {
    }

    @FXML
    private void uploadReportCardBtnAction(ActionEvent event) {
    }

    @FXML
    private void uploadForm137BtnAction(ActionEvent event) {
    }

    @FXML
    private void backBtnAction(ActionEvent event) {
        WindowOpener.openSceneWithCSS("/enrollmentsystem/Enrollment1.fxml", 
                             "/enrollment.css",
                             "ABAKADA UNIVERSITY - ENROLLEE FORM",
                             900, 520);
    }

    @FXML
    private void SubmitBtnAction(ActionEvent event) {
    }
}
