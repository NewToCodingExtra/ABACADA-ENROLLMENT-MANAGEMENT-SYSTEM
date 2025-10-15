package enrollmentsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class StudentDashboardController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label studentEmailLabel;

    @FXML
    private Label studentIdLabel;

    @FXML
    private TabPane mainTabPane;

    public void initialize() {
        // Example of setting dynamic data (replace with real data from login/session)
        studentNameLabel.setText("Earl Wesley Tadique");
        studentEmailLabel.setText("earl.tadique@starlight.student");
        studentIdLabel.setText("ID No. 2025-00123");
    }
}
