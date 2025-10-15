/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package enrollmentsystem;

import java.io.InputStream;
import javafx.scene.image.Image;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
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
    private Hyperlink registerLink;
    @FXML
    private Button loginButton;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView togglePasswordButton;

    private boolean isPasswordVisible = false;
    private Image eyeImage = null;
    private Image hiddenImage = null;
    @FXML
    private TextField userNameTField;
    @FXML
    private PasswordField passField;
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
        userNameString.setText("");
        passwordString.setText("");
     
        String userInput = userNameTField.getText().trim();
        String password = isPasswordVisible 
        ? passwordTextField.getText().trim()
        : passField.getText().trim();

        if (userInput.isEmpty()) {
            userNameString.setText("Please enter username or email!");
            return;
        }
        if (password.isEmpty()) {
            passwordString.setText("Please enter password!");
            return;
        }

        boolean isEmail = userInput.matches("^[\\w.+\\-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

        String query = "SELECT * FROM users WHERE " + (isEmail ? "email" : "username") + " = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userInput);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                userNameString.setText((isEmail ? "Email" : "Username") + " not found!");
                return;
            }

            if (!rs.getString("password").equals(password)) {
                passwordString.setText("Incorrect password!");
                return;
            }

            javax.swing.JOptionPane.showMessageDialog(null,"✅ Login successful! Access role: " + rs.getString("access"), "Successful", javax.swing.JOptionPane.INFORMATION_MESSAGE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image tryLoadImage(String... candidates) {
        for (String path : candidates) {
            if (path == null) continue;
            // Try getResourceAsStream with path as-is
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    System.out.println("Loaded image via getResourceAsStream: " + path);
                    return new Image(is);
                } else {
                    System.out.println("getResourceAsStream returned null for: " + path);
                }
            } catch (Exception ex) {
                System.out.println("Exception loading resource as stream " + path + " : " + ex);
            }
            // Try getResource (URL) next
            try {
                URL url = getClass().getResource(path);
                if (url != null) {
                    System.out.println("Loaded image via getResource URL: " + path + " -> " + url);
                    return new Image(url.toExternalForm());
                } else {
                    System.out.println("getResource returned null for: " + path);
                }
            } catch (Exception ex) {
                System.out.println("Exception loading resource URL " + path + " : " + ex);
            }
        }
        // As a last resort, try to load from absolute file path on disk (non-portable)
        // UPDATE this path to your real file system path if you want a fallback
        String fsFallback = "file:/C:/Users/Joshua/OneDrive/Documents/NetBeansProjects/ABACADA-ENROLLMENT-MANAGEMENT-SYSTEM/resource/img/eye.png";
        try {
            System.out.println("Attempting file fallback: " + fsFallback);
            return new Image(fsFallback);
        } catch (Exception ex) {
            System.out.println("File fallback failed: " + ex);
        }
        return null;
    }

    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        // lazy load icons (only once)
        if (eyeImage == null || hiddenImage == null) {
            // list of candidates to try (order matters)
            String[] eyeCandidates = {
                "/img/eye.png",       // common expected path
                "img/eye.png",        // relative path (same package)
                "/images/eye.png",    // alternative folder name
                "images/eye.png"
            };
            String[] hiddenCandidates = {
                "/img/hidden.png",
                "img/hidden.png",
                "/images/hidden.png",
                "images/hidden.png"
            };

            // Try to load each — note we use tryLoadImage to get best attempt
            eyeImage = tryLoadImage(eyeCandidates);
            hiddenImage = tryLoadImage(hiddenCandidates);

            // If still null, but the ImageView already has an image loaded via FXML,
            // use that as a fallback for both variants (not ideal, but prevents NPE)
            if (eyeImage == null && togglePasswordButton.getImage() != null) {
                System.out.println("Using togglePasswordButton existing image as eyeImage fallback");
                eyeImage = togglePasswordButton.getImage();
            }
            if (hiddenImage == null && togglePasswordButton.getImage() != null) {
                System.out.println("Using togglePasswordButton existing image as hiddenImage fallback");
                hiddenImage = togglePasswordButton.getImage();
            }
        }

        try {
            if (isPasswordVisible) {
                passField.setText(passwordTextField.getText());
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);
                if (hiddenImage != null) {
                    togglePasswordButton.setImage(hiddenImage);
                } else {
                    System.out.println("hiddenImage is null — keeping existing image");
                }
                isPasswordVisible = false;
            } else {
                passwordTextField.setText(passField.getText());
                passField.setVisible(false);
                passField.setManaged(false);
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
                if (eyeImage != null) {
                    togglePasswordButton.setImage(eyeImage);
                } else {
                    System.out.println("eyeImage is null — keeping existing image");
                }
                isPasswordVisible = true;
            }
        } catch (Exception ex) {
            // prevent the app from crashing — log the issue
            ex.printStackTrace();
        }
    }
    
}
