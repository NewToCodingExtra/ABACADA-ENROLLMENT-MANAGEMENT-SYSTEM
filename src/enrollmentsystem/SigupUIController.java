/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package enrollmentsystem;

import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Joshua
 */
public class SigupUIController implements Initializable {

    @FXML
    private TextField uNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField fPasswordTextField;
    @FXML
    private CheckBox fShowPassword;
    @FXML
    private TextField sPasswordTextField;
    @FXML
    private CheckBox sShowPassword;
    @FXML
    private PasswordField fPassField;
    @FXML
    private PasswordField sPassField;
    @FXML
    private Label termsAndConditon;
    @FXML
    private Label LogInHere;
    @FXML
    private Label UsernameValidationText;
    @FXML
    private Label EmailValidationText;
    @FXML
    private Label PasswordLengthValidation;
    @FXML
    private Label PasswordMatchValidation;
    @FXML
    private Button createAccountBtn;
    @FXML
    private CheckBox checkAcceptTermsAndConditionBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
        fPasswordTextField.managedProperty().bind(fShowPassword.selectedProperty());
        fPasswordTextField.visibleProperty().bind(fShowPassword.selectedProperty());
        fPassField.managedProperty().bind(fShowPassword.selectedProperty().not());
        fPassField.visibleProperty().bind(fShowPassword.selectedProperty().not());
        fPasswordTextField.textProperty().bindBidirectional(fPassField.textProperty());

        sPasswordTextField.managedProperty().bind(sShowPassword.selectedProperty());
        sPasswordTextField.visibleProperty().bind(sShowPassword.selectedProperty());
        sPassField.managedProperty().bind(sShowPassword.selectedProperty().not());
        sPassField.visibleProperty().bind(sShowPassword.selectedProperty().not());
        sPasswordTextField.textProperty().bindBidirectional(sPassField.textProperty());
    }    

    @FXML
    private void termsAndConditionClicked(MouseEvent event) {
        try {
            URL url = getClass().getResource("/terms.html");
            if (url != null) {
                String path = url.toURI().getPath();
 
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()});
                } else { 
                    java.awt.Desktop.getDesktop().browse(url.toURI());
                } 
            } else {
                System.out.println("terms.html not found in resources folder!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void LogInClicked(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/enrollmentsystem/LoginUI.fxml"));
            Scene scene = new Scene(root, 600, 400);  
            Stage stage = EnrollmentSystem.mainStage;  

            stage.setScene(scene);
            stage.setTitle("ABAKADA UNIVERSITY - LOGIN PAGE");
            stage.setResizable(false);

            // ✅ Manual window centering
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - 600) / 2);
            stage.setY((screenBounds.getHeight() - 400) / 2);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createAccountBtnAction(ActionEvent event) {
        UsernameValidationText.setText("");
        EmailValidationText.setText("");
        PasswordLengthValidation.setText("");
        PasswordMatchValidation.setText("");

        String username = uNameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = fPassField.getText().trim();
        String confirmPassword = sPassField.getText().trim();
 
        if (username.length() < 8 || !username.matches("^[A-Za-z0-9_@]+$")) {
            UsernameValidationText.setText("Username must be 8+ characters (letters, numbers, _ or @ only)");
            return;
        }
 
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            EmailValidationText.setText("Invalid email format!");
            return;
        }
 
        if (password.length() < 6) {
            PasswordLengthValidation.setText("Password must be at least 6 characters!");
            return;
        }
 
        if (!password.equals(confirmPassword)) {
            PasswordMatchValidation.setText("Passwords do not match!");
            return;
        }
        if (!checkAcceptTermsAndConditionBtn.isSelected()) {
            javax.swing.JOptionPane.showMessageDialog(null, "You must accept the Terms and Conditions before creating an account.", "Terms Not Accepted", javax.swing.JOptionPane.WARNING_MESSAGE);
            return; 
        }

        // 🔹 Insert user into Database
        String query = "INSERT INTO users (username, email, password, access) VALUES (?, ?, ?, 'Enrollees')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            stmt.executeUpdate();

            javax.swing.JOptionPane.showMessageDialog(null,"Account Created Successfully! You can now login.");

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("username")) {
                    UsernameValidationText.setText("Username already exists!");
                } else if (e.getMessage().contains("email")) {
                    EmailValidationText.setText("Email already registered!");
                }
            } else {
                e.printStackTrace();
            }
        }
    }
    
}
