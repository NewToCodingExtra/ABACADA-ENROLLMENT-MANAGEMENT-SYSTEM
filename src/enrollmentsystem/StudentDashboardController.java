package enrollmentsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StudentDashboardController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label studentEmailLabel;

    @FXML
    private Label studentIdLabel;

    @FXML
    private TabPane mainTabPane;
    @FXML
    private HBox topBar;
    @FXML
    private VBox sidebar;
    @FXML
    private VBox mainContainer;
    @FXML
    private TableView<?> overViewTable;
    @FXML
    private TableColumn<?, ?> semCol;
    @FXML
    private TableColumn<?, ?> courseCol;
    @FXML
    private TableColumn<?, ?> sectionCol;
    @FXML
    private TableColumn<?, ?> yearCol;
    @FXML
    private TableView<?> evalTable;
    @FXML
    private TableColumn<?, ?> evalCol;
    @FXML
    private TableColumn<?, ?> statusCol;
    @FXML
    private TableColumn<?, ?> courseCode;
    @FXML
    private TableColumn<?, ?> courseNameCol;
    @FXML
    private TableColumn<?, ?> faculrtCol;
    @FXML
    private TableColumn<?, ?> roomCol;
    @FXML
    private TableColumn<?, ?> dayCol;
    @FXML
    private TableColumn<?, ?> timeCol;

    public void initialize() {
        // Example of setting dynamic data (replace with real data from login/session)
        studentNameLabel.setText("Earl Wesley Tadique");
        studentEmailLabel.setText("earl.tadique@starlight.student");
        studentIdLabel.setText("ID No. 2025-00123");
    }
}
