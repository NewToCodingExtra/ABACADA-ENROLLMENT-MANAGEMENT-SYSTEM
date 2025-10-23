package enrollmentsystem;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private TextField fShowPassword;
    @FXML
    private TextField sPasswordTextField;
    @FXML
    private PasswordField sPassField;
    @FXML
    private Hyperlink termsAndConditon;
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
    @FXML
    private Button cancelButton;
    private Image eyeImage = null;
    private Image hiddenImage = null;
  
    @FXML
    private PasswordField fPassField;
    
    private boolean  isFPassVisible = false;
    private boolean  isSPassVisible = false;
    @FXML
    private ImageView fShowPass;
    @FXML
    private ImageView sShowPass;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
    }    

    @FXML
    private void createAccountBtnAction(ActionEvent event) {
        System.out.println("Create account button clicked");
        
        // Clear validation messages
        UsernameValidationText.setText("");
        EmailValidationText.setText("");
        PasswordLengthValidation.setText("");
        PasswordMatchValidation.setText("");

        String username = uNameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = isFPassVisible 
            ? fShowPassword.getText().trim()
            : fPassField.getText().trim();
        String confirmPassword = isSPassVisible 
            ? sPasswordTextField.getText().trim()
            : sPassField.getText().trim();
 
        // Validate username
        if (username.length() < 8 || !username.matches("^[A-Za-z0-9_@]+$")) {
            UsernameValidationText.setText("Username must be 8+ characters (letters, numbers, _ or @ only)");
            return;
        }
 
        // Validate email
        if (!email.matches("^[\\w.+\\-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            EmailValidationText.setText("Invalid email format!");
            return;
        }
 
        // Validate password length
        if (password.length() < 6) {
            PasswordLengthValidation.setText("Password must be at least 6 characters!");
            return;
        }
 
        // Validate password match
        if (!password.equals(confirmPassword)) {
            PasswordMatchValidation.setText("Passwords do not match!");
            return;
        }
        
        // Validate terms and conditions
        if (!checkAcceptTermsAndConditionBtn.isSelected()) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                "You must accept the Terms and Conditions before creating an account.", 
                "Terms Not Accepted", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return; 
        }

        // Create Enrollee object and generate unique enrollment ID
        Enrollee newEnrollee = new Enrollee();
        String enrolleeId = generateUniqueEnrolleeId();
        
        if (enrolleeId == null) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error generating enrollment ID. Please try again.", 
                "System Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String userQuery = "INSERT INTO users (username, email, password, access) VALUES (?, ?, ?, 'Enrollees')";
        String enrolleeQuery = "INSERT INTO enrollees (enrollee_id, user_id, enrollment_status, has_filled_up_form) VALUES (?, ?, 'Pending', false)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement enrolleeStmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            System.out.println("Inserting user into database...");
            
            userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, username);
            userStmt.setString(2, email);
            userStmt.setString(3, password);
            userStmt.executeUpdate();
            
            generatedKeys = userStmt.getGeneratedKeys();
            int userId = -1;
            if (generatedKeys.next()) {
                userId = generatedKeys.getInt(1);
                System.out.println("User created with ID: " + userId);
            } else {
                throw new SQLException("Failed to retrieve user_id after insert");
            }
            
            System.out.println("Inserting enrollee with ID: " + enrolleeId);
            enrolleeStmt = conn.prepareStatement(enrolleeQuery);
            enrolleeStmt.setString(1, enrolleeId);
            enrolleeStmt.setInt(2, userId);
            enrolleeStmt.executeUpdate();
            
            conn.commit(); 
            System.out.println("Transaction committed successfully");
            
            // Set enrollee properties
            newEnrollee.setUserId(userId);
            newEnrollee.setUsername(username);
            newEnrollee.setEmail(email);
            newEnrollee.setPassword(password);
            newEnrollee.setAccess("Enrollees");
            newEnrollee.setCreatedAt(LocalDateTime.now());
            newEnrollee.setActive(true);
            newEnrollee.setEnrolleeId(enrolleeId);
            newEnrollee.setEnrollmentStatus("Pending");
            newEnrollee.setHasFilledUpForm(false);
            
            System.out.println("Created enrollee: " + newEnrollee.toString());
            
//            javax.swing.JOptionPane.showMessageDialog(null,
//                "Account Created Successfully!\n\n" +
//                "Your Enrollment ID: " + enrolleeId + "\n\n" +
//                "Please save this ID for your records.\n" +
//                "You can now login or proceed to fill up your enrollment form.",
//                "Registration Successful",
//                javax.swing.JOptionPane.INFORMATION_MESSAGE);
//            
//            // Show proceed dialog to let user choose next action
            showProceedWindow();

        } catch (SQLException e) {
            // Rollback on error
            System.err.println("SQL Error occurred, rolling back transaction...");
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error during rollback:");
                    ex.printStackTrace();
                }
            }
            
            // Handle specific errors
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("username")) {
                    UsernameValidationText.setText("Username already exists!");
                } else if (e.getMessage().contains("email")) {
                    EmailValidationText.setText("Email already registered!");
                } else if (e.getMessage().contains("enrollee_id")) {
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "Enrollment ID conflict. Please try again.", 
                        "Database Error", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } else {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Error creating account: " + e.getMessage(), 
                    "Database Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            // Clean up resources
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (userStmt != null) userStmt.close();
                if (enrolleeStmt != null) enrolleeStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources:");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Generates a unique enrollment ID for the enrollee
     * Format: SA{YY}-{4-digit-random-number}
     * Ensures uniqueness by checking against existing IDs in database
     * @return Unique enrollment ID or null if generation fails
     */
    private String generateUniqueEnrolleeId() {
        Enrollee tempEnrollee = new Enrollee();
        String enrolleeId = null;
        int attempts = 0;
        int maxAttempts = 10;
        
        while (attempts < maxAttempts) {
            enrolleeId = tempEnrollee.generateID();
            System.out.println("Generated ID attempt " + (attempts + 1) + ": " + enrolleeId);
            
            // Check if ID already exists in database
            String checkQuery = "SELECT COUNT(*) FROM enrollees WHERE enrollee_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                
                stmt.setString(1, enrolleeId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Unique ID found: " + enrolleeId);
                    return enrolleeId;
                } else {
                    System.out.println("ID already exists, generating new one...");
                }
                
                rs.close();
                
            } catch (SQLException e) {
                System.err.println("Error checking enrollee ID uniqueness:");
                e.printStackTrace();
            }
            
            attempts++;
        }
        
        System.err.println("Failed to generate unique enrollee ID after " + maxAttempts + " attempts");
        return null;
    }

    @FXML
    private void termsAndConditionClicked(ActionEvent event) {
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
    private void LogInClicked(ActionEvent event) {
        ProceedDialogHelper.navigateToLogin();
    }

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        ProceedDialogHelper.navigateToLogin();
    }
    
    @FXML
    private void fShowPassAction(MouseEvent event) {
        isFPassVisible = togglePasswordVisibility(fShowPass, isFPassVisible, fPassField, fShowPassword);
    }

    @FXML
    private void sShowPassAction(MouseEvent event) {
        isSPassVisible = togglePasswordVisibility(sShowPass, isSPassVisible, sPassField, sPasswordTextField);
    }
    
    private Image tryLoadImage(String... candidates) {
        for (String path : candidates) {
            if (path == null) continue;
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    return new Image(is);
                }
            } catch (Exception ex) {
                // Continue to next candidate
            }
            try {
                URL url = getClass().getResource(path);
                if (url != null) {
                    return new Image(url.toExternalForm());
                }
            } catch (Exception ex) {
                // Continue to next candidate
            }
        }
        return null;
    }
    
    private boolean togglePasswordVisibility(ImageView togglePasswordButton, boolean isPasswordVisible, 
                                            PasswordField passField, TextField passwordTextField) {
        if (eyeImage == null || hiddenImage == null) {
            String[] eyeCandidates = {
                "/img/eye.png", "img/eye.png", "/images/eye.png", "images/eye.png"
            };
            String[] hiddenCandidates = {
                "/img/hidden.png", "img/hidden.png", "/images/hidden.png", "images/hidden.png"
            };
            
            eyeImage = tryLoadImage(eyeCandidates);
            hiddenImage = tryLoadImage(hiddenCandidates);

            if (eyeImage == null && togglePasswordButton.getImage() != null) {
                eyeImage = togglePasswordButton.getImage();
            }
            if (hiddenImage == null && togglePasswordButton.getImage() != null) {
                hiddenImage = togglePasswordButton.getImage();
            }
        }

        try {
            boolean hadFocus = passField.isFocused() || passwordTextField.isFocused();
            int caretPosition = passField.isFocused() ? passField.getCaretPosition()
                                : passwordTextField.getCaretPosition();
            
            if (isPasswordVisible) {
                passField.setText(passwordTextField.getText());
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);
                if (hiddenImage != null) {
                    togglePasswordButton.setImage(hiddenImage);
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
                }
                isPasswordVisible = true;
            }
            
            if (hadFocus) {
                if (isPasswordVisible) {
                    passwordTextField.requestFocus();
                    passwordTextField.positionCaret(caretPosition);
                } else {
                    passField.requestFocus();
                    passField.positionCaret(caretPosition);
                }
            }
            return isPasswordVisible;
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return isPasswordVisible;
    }

    private void showProceedWindow() {
        ProceedDialogHelper.showDialogAndNavigate(EnrollmentSystem.mainStage);
    }
}