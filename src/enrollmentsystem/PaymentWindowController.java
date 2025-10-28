/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package enrollmentsystem;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Wes
 */
public class PaymentWindowController implements Initializable {

    @FXML
    private TextField cardHolderName;
    @FXML
    private TextField cardNo;
    @FXML
    private TextField expireMonth;
    @FXML
    private TextField expireDay;
    @FXML
    private TextField cvcNo;
    @FXML
    private Button confirmBtn;
    @FXML
    private Button returnBtn;
    @FXML
    private Label nameError;
    @FXML
    private Label creditCardNoError;
    @FXML
    private Label expirationError;
    @FXML
    private Label cvcError;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    private void closeDialog() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void returnBtnAction(ActionEvent event) {
        closeDialog();
    }

    @FXML
    private void confirmBtnAction(ActionEvent event) {
    }
    
}
