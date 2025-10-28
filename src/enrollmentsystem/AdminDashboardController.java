package enrollmentsystem;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminDashboardController implements Initializable {

    // ========== TOP BAR ==========
    @FXML
    private HBox topBar;

    // ========== SIDEBAR ==========
    @FXML
    private VBox sidebar;

    @FXML
    private ImageView adminLogo;

    @FXML
    private Label studentName;

    @FXML
    private Label studentEmail;

    @FXML
    private Label studentId;

    // ========== MAIN CONTENT ==========
    @FXML
    private VBox mainContainer;

    @FXML
    private TabPane mainTabPane;

    // ========== HOME TAB ==========
    @FXML
    private TableView<?> homeTable;

    @FXML
    private TableColumn<?, ?> homeColEnrolleeID;

    @FXML
    private TableColumn<?, ?> homeColStudentName;

    @FXML
    private TableColumn<?, ?> homeColCourse;

    @FXML
    private TableColumn<?, ?> homeColYearLevel;

    @FXML
    private TableColumn<?, ?> homeColAction;

    @FXML
    private TableColumn<?, ?> homeColExtra;

    // ========== ACCOUNT CREATION TAB ==========
    @FXML
    private TableView<?> accountCreationTable;

    @FXML
    private TableColumn<?, ?> accountColStudentName;

    @FXML
    private TableColumn<?, ?> accountColCourse;

    @FXML
    private TableColumn<?, ?> accountColAmountPaid;

    @FXML
    private TableColumn<?, ?> accountColAction;

    @FXML
    private TableColumn<?, ?> accountColApprovalStatus;

    @FXML
    private TableColumn<?, ?> accountColPaymentStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize logic here (optional)
        // Example: configure columns, load data, etc.
    }
}
