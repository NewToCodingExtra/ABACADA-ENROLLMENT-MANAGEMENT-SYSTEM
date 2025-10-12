/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package enrollmentsystem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void termsAndConditionClicked(MouseEvent event) {
        try {
            java.awt.Desktop.getDesktop().browse(
                new java.io.File("\terms.html").toURI()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void LogInClicked(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/enrollmentsystem/LoginUI.fxml"));
            Scene scene = new Scene(root, 600, 400); // ← Your Login window size
            Stage stage = EnrollmentSystem.mainStage; // ← Use same stage

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
    
}
