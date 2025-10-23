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

            User loggedInUser = createUserFromResultSet(rs);

            if (loggedInUser instanceof Enrollee enrollee) {
                if (enrollee.hasFilledUpForm()) {
                    WindowOpener.openScene("/enrollmentsystem/EnrolleeDashboard.fxml",
                             "ABAKADA UNIVERSITY - ENROLLEE DASHBOARD",
                             1024, 600);
                } else {
                    WindowOpener.openSceneWithCSS("/enrollmentsystem/Enrollment1.fxml", 
                             "/enrollment.css",
                             "ABAKADA UNIVERSITY - ENROLLEE FORM",
                             900, 520);
                }
                return;
            }

            switch (loggedInUser.getAccess()) {
                case "Admin" ->
                    WindowOpener.openSceneWithCSS("/enrollmentsystem/AdminDashboard.fxml", 
                             "/admindashboard.css",
                             "ABAKADA UNIVERSITY - ADMIN DASHBOARD",
                             1200, 700);
                case "Cashier" ->
                    WindowOpener.openSceneWithCSS("/enrollmentsystem/CashierDashboard.fxml", 
                             "/cashierdashboard.css",
                             "ABAKADA UNIVERSITY - CASHIER DASHBOARD",
                             950, 550);
                case "Faculty" ->
                    WindowOpener.openSceneWithCSS("/enrollmentsystem/FacultyDashboard.fxml",
                             "/studentdashboard.css",
                             "ABAKADA UNIVERSITY - FACULTY DASHBOARD",
                             950, 550);
                case "Student" ->
                    WindowOpener.openSceneWithCSS("/enrollmentsystem/StudentDashboard.fxml",
                              "/studentdashboard.css",
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
        WindowOpener.openSceneWithLoader("/enrollmentsystem/Register.fxml", "ABAKADA UNIVERSITY - SIGNUP PAGE", 898.4, 543.2);
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String access = rs.getString("access");
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        boolean isActive = rs.getBoolean("is_active");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        SessionManager.getInstance().setUserId(userId);

        switch (access) {
            case "Admin":
                return createAdmin(userId, username, email, password, access, createdAt, isActive);
//            case "Cashier":
//                return createCashier(userId, username, email, password, access, createdAt, isActive);
//            case "Faculty":
//                return createFaculty(userId, username, email, password, access, createdAt, isActive);
//            case "Student":
//                return createStudent(userId, username, email, password, access, createdAt, isActive);
            default:
                return createEnrollee(userId, username, email, password, createdAt, isActive);
        }
    }

    private Admin createAdmin(int userId, String username, String email, String password, 
                             String access, LocalDateTime createdAt, boolean isActive) throws SQLException {
        String query = "SELECT admin_id, first_name, last_name FROM admin WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String adminId = rs.getString("admin_id");
                SessionManager.getInstance().setAdminId(adminId);

                Admin admin = new Admin(userId, username, email, password, 
                                       access, createdAt, isActive, adminId);
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                return admin;
            }
        }
        return new Admin(userId, username, email, password, createdAt, isActive);
    }

//    private Cashier createCashier(int userId, String username, String email, String password, 
//                                 String access, LocalDateTime createdAt, boolean isActive) throws SQLException {
//        String query = "SELECT cashier_id, first_name, last_name FROM cashier WHERE user_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(query)) {
//            ps.setInt(1, userId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                String cashierId = rs.getString("cashier_id");
//                // Store cashier_id in session
//                SessionManager.getInstance().setCashierId(cashierId);
//
//                Cashier cashier = new Cashier(userId, username, email, password, 
//                                             access, createdAt, isActive, cashierId);
//                cashier.setFirstName(rs.getString("first_name"));
//                cashier.setLastName(rs.getString("last_name"));
//                return cashier;
//            }
//        }
//        return new Cashier(userId, username, email, password, createdAt, isActive);
//    }
//
//    private Faculty createFaculty(int userId, String username, String email, String password, 
//                                 String access, LocalDateTime createdAt, boolean isActive) throws SQLException {
//        String query = "SELECT faculty_number, first_name, last_name FROM faculty WHERE user_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(query)) {
//            ps.setInt(1, userId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                String facultyId = rs.getString("faculty_number");
//                // Store faculty_id in session
//                SessionManager.getInstance().setFacultyId(facultyId);
//
//                Faculty faculty = new Faculty(userId, username, email, password, 
//                                             access, createdAt, isActive, facultyId);
//                faculty.setFirstName(rs.getString("first_name"));
//                faculty.setLastName(rs.getString("last_name"));
//                return faculty;
//            }
//        }
//        return new Faculty(userId, username, email, password, createdAt, isActive);
//    }
//
//    private Student createStudent(int userId, String username, String email, String password, 
//                                 String access, LocalDateTime createdAt, boolean isActive) throws SQLException {
//        String query = "SELECT student_id, first_name, last_name FROM students WHERE user_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(query)) {
//            ps.setInt(1, userId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                String studentId = rs.getString("student_id");
//                SessionManager.getInstance().setStudentId(studentId);
//
//                Student student = new Student(userId, username, email, password, 
//                                             access, createdAt, isActive, studentId);
//                student.setFirstName(rs.getString("first_name"));
//                student.setLastName(rs.getString("last_name"));
//                return student;
//            }
//        }
//        return new Student(userId, username, email, password, createdAt, isActive);
//    }

    private Enrollee createEnrollee(int userId, String username, String email, String password, 
                               LocalDateTime createdAt, boolean isActive) throws SQLException {
        String query = "SELECT enrollee_id, has_filled_up_form FROM enrollees WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            boolean hasFilled = false;
            String enrolleeId = null;

            if (rs.next()) {
                enrolleeId = rs.getString("enrollee_id");
                hasFilled = rs.getBoolean("has_filled_up_form");

                SessionManager.getInstance().setEnrolleeId(enrolleeId);
                System.out.println("Enrollee ID loaded: " + enrolleeId + ", hasFilled: " + hasFilled);
            } else {
                System.out.println("No enrollee record found for userId: " + userId);
            }

            return new Enrollee(userId, username, email, password, createdAt, isActive, hasFilled);
        }
    }

}
