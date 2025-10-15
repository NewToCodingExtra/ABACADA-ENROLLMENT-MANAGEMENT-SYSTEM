
package enrollmentsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class FacultyDashboardController {

    @FXML
    private Label facultyNameLabel;

    @FXML
    private Label facultyEmailLabel;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private void initialize() {
        // Placeholder text only — no backend logic
        facultyNameLabel.setText("Faculty Full Name");
        facultyEmailLabel.setText("facultyname@starlight.faculty");
    }
}
