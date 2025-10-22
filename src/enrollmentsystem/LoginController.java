/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package enrollmentsystem;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.*;
import java.sql.SQLException;

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
        String password = isPasswordVisible ? passwordTextField.getText().trim() : passField.getText().trim();

        if (userInput.isEmpty()) {
            userNameString.setText("No username or email!");
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

            // ✅ Instantiate correct user type based on access
            User loggedInUser = createUserFromResultSet(rs);

            if (loggedInUser instanceof Enrollee enrollee) {
                if (enrollee.hasFilledUpForm()) {
                    openPage("/enrollmentsystem/EnrolleeDashboard.fxml",
                             "ABAKADA UNIVERSITY - ENROLLEE DASHBOARD",
                             1024, 600);
                } else {
                    openPage("/enrollmentsystem/Enrollment1.fxml",
                             "ABAKADA UNIVERSITY - ENROLLEE FORM",
                             900, 520);
                }
                return;
            }

            switch (loggedInUser.getAccess()) {
                case "Admin" ->
                    openPage("/enrollmentsystem/AdminDashboard.fxml",
                             "ABAKADA UNIVERSITY - ADMIN DASHBOARD",
                             950, 550);
                case "Cashier" ->
                    openPage("/enrollmentsystem/CashierDashboard.fxml",
                             "ABAKADA UNIVERSITY - CASHIER DASHBOARD",
                             950, 550);
                case "Faculty" ->
                    openPage("/enrollmentsystem/FacultyDashboard.fxml",
                             "ABAKADA UNIVERSITY - FACULTY DASHBOARD",
                             950, 550);
                case "Student" ->
                    openPage("/enrollmentsystem/StudentDashboard.fxml",
                             "ABAKADA UNIVERSITY - STUDENT DASHBOARD",
                             950, 550);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image tryLoadImage(String... candidates) {
        for (String path : candidates) {
            if (path == null) continue;
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
        if (eyeImage == null || hiddenImage == null) {
             
            String[] eyeCandidates = {
                "/img/eye.png",        
                "img/eye.png",         
                "/images/eye.png",     
                "images/eye.png"
            };
            String[] hiddenCandidates = {
                "/img/hidden.png",
                "img/hidden.png",
                "/images/hidden.png",
                "images/hidden.png"
            };
            
            eyeImage = tryLoadImage(eyeCandidates);
            hiddenImage = tryLoadImage(hiddenCandidates);

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
            ex.printStackTrace();
        }
    }

    @FXML
    private void onRegisterAction(ActionEvent event) {
        openPage("/enrollmentsystem/Register.fxml", "ABAKADA UNIVERSITY - SIGNUP PAGE", 898.4, 543.2);
    }
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String access = rs.getString("access");
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        boolean isActive = rs.getBoolean("is_active");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        switch (access) {
//            case "Admin":
//                return new Admin(userId, username, email, password, createdAt, isActive);
//            case "Cashier":
//                return new Cashier(userId, username, email, password, createdAt, isActive);
//            case "Faculty":
//                return new Faculty(userId, username, email, password, createdAt, isActive);
//            case "Student":
//                return new Student(userId, username, email, password, createdAt, isActive);
            default: {
                // ✅ Fetch enrollee data (including has_filled_up_form)
                String enrolleeQuery = "SELECT has_filled_up_form FROM enrollees WHERE user_id = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(enrolleeQuery)) {

                    ps.setInt(1, userId);
                    ResultSet enrolleeRS = ps.executeQuery();

                    boolean hasFilled = false;
                    if (enrolleeRS.next()) {
                        hasFilled = enrolleeRS.getBoolean("has_filled_up_form");
                    }

                    return new Enrollee(userId, username, email, password, createdAt, isActive, hasFilled);
                }
            }
        }
    }
    private void openPage(String fxmlPath, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root, width, height);
            
            if(fxmlPath.equals("/enrollmentsystem/Register.fxml")) 
                scene.getStylesheets().add( getClass().getResource("/sigupui.css").toExternalForm() );
            
            Stage stage = EnrollmentSystem.mainStage;

            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);

            // Center the stage on screen (similar to your register code)
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - width) / 2);
            stage.setY((screenBounds.getHeight() - height) / 2);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
