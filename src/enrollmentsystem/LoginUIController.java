/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package enrollmentsystem;

import java.io.IOException;
import javafx.application.Application;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Joshua
 */
public class LoginUIController implements Initializable {

    @FXML
    private CheckBox showPass;
    @FXML
    private TextField userNameTField;
    @FXML
    private PasswordField passField;
    @FXML
    private TextField passVisibleField;
    @FXML
    private Button loginBtn;
    @FXML
    private Button signupBtn;
    @FXML
    private Label userNameString;
    @FXML
    private Label passwordString;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // Bind password field and visible text field
        passVisibleField.managedProperty().bind(showPass.selectedProperty());
        passVisibleField.visibleProperty().bind(showPass.selectedProperty());

        passField.managedProperty().bind(showPass.selectedProperty().not());
        passField.visibleProperty().bind(showPass.selectedProperty().not());

        // Keep both fields' text synchronized
        passVisibleField.textProperty().bindBidirectional(passField.textProperty());
    }

    @FXML
    private void loginBtnAction(ActionEvent event) {
        userNameString.setText("");
        passwordString.setText("");
     
        String userInput = userNameTField.getText().trim();
        String password = passField.getText().trim();

        // Basic validation
        if (userInput.isEmpty()) {
            userNameString.setText("Please enter username or email!");
            return;
        }
        if (password.isEmpty()) {
            passwordString.setText("Please enter password!");
            return;
        }

        // Determine if input is email or username
        boolean isEmail = userInput.matches("^[A-Za-z0-9+_.-]+@(.+)$");

        // Build SQL dynamically
        String query = "SELECT * FROM users WHERE " + (isEmail ? "email" : "username") + " = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userInput);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                userNameString.setText((isEmail ? "Email" : "Username") + " not found!");
                return;
            }

            // Check password
            if (!rs.getString("password").equals(password)) {
                passwordString.setText("Incorrect password!");
                return;
            }

            // SUCCESS
            javax.swing.JOptionPane.showMessageDialog(null,"✅ Login successful! Access role: " + rs.getString("access"), "Successful", javax.swing.JOptionPane.INFORMATION_MESSAGE);

            // TODO: Load next window based on role later

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void signupBtnAction(ActionEvent event) {
       
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/enrollmentsystem/SigupUI.fxml"));
            Scene scene = new Scene(root, 801, 580);
            Stage stage = EnrollmentSystem.mainStage;

            stage.setScene(scene);
            stage.setTitle("ABAKADA UNIVERSITY - SIGNUP PAGE");
            stage.setResizable(false);

            // ✅ Manually center after scene switch
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - 801) / 2);
            stage.setY((screenBounds.getHeight() - 580) / 2);

            stage.show(); 
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    @FXML
    private void showPassClickedBTN(ActionEvent event) {
        // No code needed here because binding already handles show/hide
    }

}
