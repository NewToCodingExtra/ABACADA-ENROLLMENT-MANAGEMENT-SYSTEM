package enrollmentsystem;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PaymentMethodController implements Initializable {

    @FXML
    private Button gCashBtn;
    @FXML
    private Button cardBtn;
    @FXML
    private Button cashBtn;
    @FXML
    private Button cancelBtn;
    
    private boolean dialogOpen = false; // Flag to prevent multiple opens

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void cardBtnAction(ActionEvent event) {
        if (dialogOpen) return; // Prevent multiple opens
        dialogOpen = true;
        
        // Disable all buttons immediately
        disableAllButtons(true);
        
        // Get current dialog stage to pass as owner
        Stage currentStage = (Stage) cancelBtn.getScene().getWindow();
        
        try {
            // Open new dialog with current dialog as owner
            WindowOpener.openDialogWithCSSAndOwner(
                "/enrollmentsystem/PaymentWindow.fxml", 
                "/cardpayment.css", 
                "Paying Tuition using Credit Card", 
                574.4, 
                431.6,
                currentStage
            );
            // After payment dialog closes, close this dialog
            closeDialog();
        } catch (Exception e) {
            System.err.println("Error opening payment dialog: " + e.getMessage());
            e.printStackTrace();
            
            disableAllButtons(false);
            dialogOpen = false;
        }
    }

    @FXML
    private void gCashBtnAction(ActionEvent event) {
        if (dialogOpen) return; 
        dialogOpen = true;
        
        disableAllButtons(true);
        
        Stage currentStage = (Stage) cancelBtn.getScene().getWindow();
        
        try {
            WindowOpener.openDialogWithOwner(
                "/enrollmentsystem/PaymentWindow2.fxml", 
                "Paying Tuition using GCash", 
                574.4, 
                329,
                currentStage
            );
            closeDialog();
        } catch (Exception e) {
            System.err.println("Error opening GCash dialog: " + e.getMessage());
            e.printStackTrace();
            
            disableAllButtons(false);
            dialogOpen = false;
        }
    }

    @FXML
    private void cashBtnAction(ActionEvent event) {
        if (dialogOpen) return;
        dialogOpen = true;
        disableAllButtons(true);
        closeDialog();
    }

    @FXML
    private void cancelBtnButton(ActionEvent event) {
        if (dialogOpen) return;
        dialogOpen = true;
        disableAllButtons(true);
        closeDialog();
    }
    
    private void disableAllButtons(boolean disable) {
        gCashBtn.setDisable(disable);
        cardBtn.setDisable(disable);
        cashBtn.setDisable(disable);
        cancelBtn.setDisable(disable);
    }
    
    private void closeDialog() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}