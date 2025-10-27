package enrollmentsystem;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class - Dialog-style controller
 *
 * @author Joshua
 */
public class DeclineWindowController implements Initializable {
    
    @FXML
    private Button toEnrollmentBTN;
    
    @FXML
    private Button toLOginBTn;
    
    private int result = -1; // -1 = no selection, 0 = enrollment, 1 = login
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up button actions
        toEnrollmentBTN.setOnAction(event -> handleEnrollmentButton());
        toLOginBTn.setOnAction(event -> handleLoginButton());
    }
    
    /**
     * Handles enrollment button click - sets result to 0 and closes window
     */
    private void handleEnrollmentButton() {
        result = 0;
        closeDialog();
    }
    
    /**
     * Handles login button click - sets result to 1 and closes window
     */
    private void handleLoginButton() {
        result = 1;
        closeDialog();
    }
    
    /**
     * Closes the dialog window
     */
    private void closeDialog() {
        Stage stage = (Stage) toEnrollmentBTN.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Gets the result of the dialog
     * @return 0 if enrollment was selected, 1 if login was selected, -1 if no selection
     */
    public int getResult() {
        return result;
    }
}