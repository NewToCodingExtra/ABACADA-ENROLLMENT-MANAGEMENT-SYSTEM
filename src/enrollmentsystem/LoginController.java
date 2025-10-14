/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package enrollmentsystem;

import javafx.scene.image.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Joshua
 */
public class LoginController implements Initializable {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private Button loginButton;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView togglePasswordButton;

    private boolean isPasswordVisible = false;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    


    @FXML
    private void onMouseExitRegisterAction(MouseEvent event) {
    }

    @FXML
    private void onMouseEnterRegisterAction(MouseEvent event) {
    }

    @FXML
    private void onMouseClickedRegisterAction(MouseEvent event) {
    }

    @FXML
    private void loginButtonAction(ActionEvent event) {
    }

    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        if (isPasswordVisible) {
            passwordField.setText(passwordTextField.getText());
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.setImage(new Image(getClass().getResourceAsStream("\\img\\hidden.png")));
            isPasswordVisible = false;
        } else {
            passwordTextField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            togglePasswordButton.setImage(new Image(getClass().getResourceAsStream("\\img\\eye.png")));
            isPasswordVisible = true;
        }
    }
    
}
