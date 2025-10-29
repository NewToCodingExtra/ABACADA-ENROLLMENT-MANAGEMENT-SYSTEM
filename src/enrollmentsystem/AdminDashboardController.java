package enrollmentsystem;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * Admin Dashboard Controller
 * Manages enrollee approval/rejection process
 * Creates student accounts and handles enrollment workflow
 * FIXED: Now automatically enrolls students in section courses
 */
public class AdminDashboardController implements Initializable {

    @FXML private HBox topBar;
    @FXML private VBox sidebar;
    @FXML private ImageView adminLogo;
    @FXML private Label studentName;
    @FXML private Label studentEmail;
    @FXML private Label studentId;
    @FXML private VBox mainContainer;
    @FXML private TabPane mainTabPane;
    
    @FXML private Label enrolledNoLabel;
    @FXML private Label pendingEnrollmentLabel;
    @FXML private Label pendingPaymentLabel;
    @FXML private Label paidLabel;
    
    @FXML private TableView<EnrolleeForApproval> homeTable;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColEnrolleeID;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColStudentName;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColCourse;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColYearLevel;
    @FXML private TableColumn<EnrolleeForApproval, Void> homeColAction;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColExtra;
    
    @FXML private TableView<?> accountCreationTable;
    @FXML private TableColumn<?, ?> accColStudentName;
    @FXML private TableColumn<?, ?> accColCourse;
    @FXML private TableColumn<?, ?> accColAmountPaid;
    @FXML private TableColumn<?, ?> accColAction;
    @FXML private TableColumn<?, ?> accColApprovalStatus;
    @FXML private TableColumn<?, ?> accColPaymentStatus;

    private String adminId;
    private ObservableList<EnrolleeForApproval> enrolleesList;
    private Timeline refreshTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        adminId = SessionManager.getInstance().getAdminId();
        
        if (adminId == null) {
            showError("Session Error", "Admin ID not found. Please log in again.");
            return;
        }
        
        setupTableColumns();
        loadEnrollees();
        loadDashboardStatistics();  

        startAutoRefresh(5);
        
        System.out.println("Admin Dashboard initialized for admin: " + adminId);
    }
   
    private void loadDashboardStatistics() {
        try (Connection conn = DBConnection.getConnection()) {
            
            int enrolledCount = getCountFromQuery(conn, 
                "SELECT COUNT(*) FROM enrollees WHERE enrollment_status = 'Enrolled'");
            enrolledNoLabel.setText(String.valueOf(enrolledCount));
            
            int pendingEnrollmentCount = getCountFromQuery(conn,
                "SELECT COUNT(*) FROM enrollees WHERE enrollment_status = 'Pending'");
            pendingEnrollmentLabel.setText(String.valueOf(pendingEnrollmentCount));
            
            int pendingPaymentCount = getCountFromQuery(conn,
                "SELECT COUNT(*) FROM enrollees WHERE payment_status IN ('Not_Paid', 'Paid_Pending_Verification')");
            pendingPaymentLabel.setText(String.valueOf(pendingPaymentCount));
            
            int paidCount = getCountFromQuery(conn,
                "SELECT COUNT(*) FROM enrollees WHERE payment_status = 'Verified'");
            paidLabel.setText(String.valueOf(paidCount));
            
            System.out.println("Dashboard stats loaded - Enrolled: " + enrolledCount + 
                             ", Pending: " + pendingEnrollmentCount + 
                             ", Pending Payment: " + pendingPaymentCount + 
                             ", Paid: " + paidCount);
            
        } catch (SQLException e) {
            System.err.println("Error loading dashboard statistics: " + e.getMessage());
            e.printStackTrace();
            
            enrolledNoLabel.setText("0");
            pendingEnrollmentLabel.setText("0");
            pendingPaymentLabel.setText("0");
            paidLabel.setText("0");
        }
    }
    
    private int getCountFromQuery(Connection conn, String query) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    private void setupTableColumns() {
        homeColEnrolleeID.setCellValueFactory(new PropertyValueFactory<>("enrolleeId"));
        homeColEnrolleeID.setStyle("-fx-alignment: CENTER;");
        
        homeColStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        homeColStudentName.setCellFactory(column -> new TableCell<EnrolleeForApproval, String>() {
            private final Hyperlink hyperlink = new Hyperlink();
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    hyperlink.setText(item);
                    hyperlink.setStyle("-fx-text-fill: blue; -fx-underline: true;");
                    hyperlink.setOnAction(e -> {
                        EnrolleeForApproval enrollee = getTableView().getItems().get(getIndex());
                        openEnrolleeViewDialog(enrollee);
                    });
                    setGraphic(hyperlink);
                }
            }
        });
        
        homeColCourse.setCellValueFactory(new PropertyValueFactory<>("program"));
        homeColCourse.setStyle("-fx-alignment: CENTER;");
        
        homeColYearLevel.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));
        homeColYearLevel.setStyle("-fx-alignment: CENTER;");
        
        homeColExtra.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        homeColExtra.setStyle("-fx-alignment: CENTER;");
        
        homeColAction.setCellFactory(new Callback<TableColumn<EnrolleeForApproval, Void>, TableCell<EnrolleeForApproval, Void>>() {
            @Override
            public TableCell<EnrolleeForApproval, Void> call(TableColumn<EnrolleeForApproval, Void> param) {
                return new TableCell<EnrolleeForApproval, Void>() {
                    private final Button approveBtn = new Button("Approve");
                    private final Button rejectBtn = new Button("Reject");
                    private final HBox pane = new HBox(5);
                    
                    {
                        approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                        rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                        
                        approveBtn.setOnAction(event -> {
                            EnrolleeForApproval enrollee = getTableView().getItems().get(getIndex());
                            handleApproveEnrollee(enrollee);
                        });
                        
                        rejectBtn.setOnAction(event -> {
                            EnrolleeForApproval enrollee = getTableView().getItems().get(getIndex());
                            handleRejectEnrollee(enrollee);
                        });
                        
                        pane.getChildren().addAll(approveBtn, rejectBtn);
                        pane.setAlignment(Pos.CENTER);
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }
    
    private void loadEnrollees() {
        List<EnrolleeForApproval> enrollees = EnrolleeForApproval.loadPendingWithVerifiedPayment();
        enrolleesList = FXCollections.observableArrayList(enrollees);
        homeTable.setItems(enrolleesList);
        
        loadDashboardStatistics();
        
        System.out.println("Loaded " + enrollees.size() + " enrollees with verified payment");
    }
    
    private void openEnrolleeViewDialog(EnrolleeForApproval enrollee) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/enrollmentsystem/EnrolleeViewDialog.fxml")
            );
            Parent root = loader.load();
            
            EnrolleeViewDialogController controller = loader.getController();
            controller.setEnrolleeId(enrollee.getEnrolleeId());
            
            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(homeTable.getScene().getWindow());
            dialogStage.setTitle("View Enrollment - " + enrollee.getEnrolleeId());
            dialogStage.setScene(new Scene(root, 900, 700));
            dialogStage.setResizable(false);
            
            controller.loadAndDisplay();
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Error opening enrollee view: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to open enrollee details: " + e.getMessage());
        }
    }
   
    private void handleApproveEnrollee(EnrolleeForApproval enrollee) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Approve Enrollment");
        confirmation.setHeaderText("Confirm Enrollment Approval");
        confirmation.setContentText(
            "Are you sure you want to approve this enrollment?\n\n" +
            "Student: " + enrollee.getStudentName() + "\n" +
            "Enrollee ID: " + enrollee.getEnrolleeId() + "\n" +
            "Program: " + enrollee.getProgram() + "\n\n" +
            "This will:\n" +
            "• Create a student account\n" +
            "• Transfer data to student records\n" +
            "• Generate login credentials\n" +
            "• Assign to a section with schedule\n" +
            "• Enroll student in all section courses"
        );
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = approveEnrolleeAndCreateAccount(enrollee);
            
            if (success) {
                loadEnrollees();
            } else {
                showError("Error", "Failed to approve enrollment. Please try again.");
            }
        }
    }
    
    private boolean approveEnrolleeAndCreateAccount(EnrolleeForApproval enrollee) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            
            conn.setAutoCommit(false);
            
            Enrollee fullEnrollee = loadFullEnrolleeData(enrollee.getEnrolleeId(), conn);
            if (fullEnrollee == null) {
                conn.rollback();
                return false;
            }
            
            String studentId = generateStudentId();
            String username = studentId;
            String password = "1234" + fullEnrollee.getLastName();
            
            String universityEmail = generateUniqueEmail(
                fullEnrollee.getFirstName(), 
                fullEnrollee.getLastName(), 
                conn
            );
            
            int userId = createUserAccount(username, universityEmail, password, "Student", conn);
            if (userId == 0) {
                conn.rollback();
                return false;
            }
            
            String programId = getProgramId(enrollee.getProgram(), conn);
            
            if (!createStudentRecord(studentId, userId, programId, conn)) {
                conn.rollback();
                return false;
            }
            
            if (!copyToStudentRecord(fullEnrollee, studentId, programId, conn)) {
                conn.rollback();
                return false;
            }
            
            // CRITICAL FIX: Auto-enroll student in section courses
            int enrolledCourses = assignToSectionAndEnroll(studentId, programId, fullEnrollee.getYearLevel(), conn);
            
            String credentialsMessage;
            if (enrolledCourses > 0) {
                credentialsMessage = String.format(
                    "You are officially enrolled! Log in using your student account.\n\n" +
                    "Student ID: %s\n" +
                    "Username: %s\n" +
                    "Password: %s\n" +
                    "University Email: %s\n\n" +
                    "You have been enrolled in %d courses for this semester.",
                    studentId, username, password, universityEmail, enrolledCourses
                );
            } else {
                credentialsMessage = String.format(
                    "You are officially enrolled! Log in using your student account.\n\n" +
                    "Student ID: %s\n" +
                    "Username: %s\n" +
                    "Password: %s\n" +
                    "University Email: %s\n\n" +
                    "Note: No courses available for your section yet. Check back later.",
                    studentId, username, password, universityEmail
                );
            }
            
            if (!updateEnrolleeStatus(enrollee.getEnrolleeId(), "Enrolled", credentialsMessage, conn)) {
                conn.rollback();
                return false;
            }
            
            conn.commit();
            
            showCredentialsDialog(enrollee.getStudentName(), studentId, username, password, 
                                universityEmail, enrolledCourses);
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error approving enrollee: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showCredentialsDialog(String studentName, String studentId, String username, 
                                      String password, String universityEmail, int enrolledCourses) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Enrollment Approved");
        alert.setHeaderText("Student Account Created for " + studentName);
        
        String content = String.format(
            "=============================\n" +
            "        STUDENT LOGIN CREDENTIALS\n" +
            "=============================\n\n" +
            "Student ID: %s\n" +
            "Username: %s\n" +
            "Password: %s\n" +
            "University Email: %s\n" +
            "Courses Enrolled: %d\n\n" +
            "=============================\n\n" +
            "IMPORTANT INSTRUCTIONS:\n\n" +
            "1. These credentials have been sent to the\n" +
            "   enrollee's dashboard under Admin Comment.\n\n" +
            "2. The enrollee can now log in using their\n" +
            "   student account.\n\n" +
            "3. The student has been automatically enrolled\n" +
            "   in all courses for their assigned section.\n\n" +
            "4. The enrollee account will remain active\n" +
            "   until the student logs in for the first time.\n\n" +
            "==============================",
            studentId, username, password, universityEmail, enrolledCourses
        );
        
        alert.setContentText(content);
        alert.getDialogPane().setPrefWidth(550);
        
        alert.getButtonTypes().clear();
        ButtonType copyButton = new ButtonType("Copy Credentials", ButtonBar.ButtonData.LEFT);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().addAll(copyButton, okButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == copyButton) {
            String credentials = String.format(
                "Student ID: %s\nUsername: %s\nPassword: %s\nUniversity Email: %s\nCourses Enrolled: %d",
                studentId, username, password, universityEmail, enrolledCourses
            );
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content2 = new javafx.scene.input.ClipboardContent();
            content2.putString(credentials);
            clipboard.setContent(content2);
            
            showInfo("Copied", "Credentials copied to clipboard!");
            showCredentialsDialog(studentName, studentId, username, password, universityEmail, enrolledCourses);
        }
    }
 
    private void handleRejectEnrollee(EnrolleeForApproval enrollee) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Enrollment");
        dialog.setHeaderText("Reject Enrollment - Provide Reason");
        dialog.setContentText(
            "Student: " + enrollee.getStudentName() + "\n" +
            "Enrollee ID: " + enrollee.getEnrolleeId() + "\n\n" +
            "Please provide a reason for rejection:"
        );
        
        dialog.getDialogPane().setPrefWidth(450);
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String reason = result.get().trim();
            
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Rejection");
            confirmation.setHeaderText("Confirm Enrollment Rejection");
            confirmation.setContentText(
                "Are you sure you want to reject this enrollment?\n\n" +
                "Reason: " + reason + "\n\n" +
                "The enrollee will see this reason in their dashboard\n" +
                "and can resubmit their application."
            );
            
            Optional<ButtonType> confirmResult = confirmation.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                boolean success = rejectEnrollment(enrollee.getEnrolleeId(), reason);
                
                if (success) {
                    showInfo("Enrollment Rejected", 
                            "Enrollment has been rejected.\n" +
                            "The enrollee can see the reason and resubmit.");
                    
                    loadEnrollees();
                } else {
                    showError("Error", "Failed to reject enrollment. Please try again.");
                }
            }
        } else if (result.isPresent()) {
            showError("Invalid Input", "Please provide a reason for rejection.");
        }
    }
    
    private boolean rejectEnrollment(String enrolleeId, String reason) {
        String query = "UPDATE enrollees SET enrollment_status = 'Rejected', " +
                      "admin_rejection_reason = ?, reviewed_by = ?, reviewed_on = ? " +
                      "WHERE enrollee_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, reason);
            ps.setInt(2, SessionManager.getInstance().getUserId());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, enrolleeId);
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error rejecting enrollment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper Methods
    
    private Enrollee loadFullEnrolleeData(String enrolleeId, Connection conn) throws SQLException {
        String query = "SELECT e.*, u.username, u.email FROM enrollees e " +
                      "JOIN users u ON e.user_id = u.user_id WHERE e.enrollee_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, enrolleeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Enrollee enrollee = new Enrollee();
                enrollee.setEnrolleeId(rs.getString("enrollee_id"));
                enrollee.setUserId(rs.getInt("user_id"));
                enrollee.setFirstName(rs.getString("first_name"));
                enrollee.setMiddleName(rs.getString("middle_name"));
                enrollee.setLastName(rs.getString("last_name"));
                enrollee.setSuffix(rs.getString("suffix"));
                enrollee.setBirthDate(rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null);
                enrollee.setGender(rs.getString("gender"));
                enrollee.setAddress(rs.getString("address"));
                enrollee.setProvince(rs.getString("province"));
                enrollee.setCity(rs.getString("city"));
                enrollee.setContactNumber(rs.getString("contact_number"));
                enrollee.setEmailAddress(rs.getString("email_address"));
                enrollee.setGuardianName(rs.getString("guardian_name"));
                enrollee.setGuardianContact(rs.getString("guardian_contact"));
                enrollee.setYearLevel(rs.getString("year_level"));
                enrollee.setStudentType(rs.getString("student_type"));
                enrollee.setLastSchoolAttended(rs.getString("last_school_attended"));
                enrollee.setLastSchoolYear(rs.getString("school_year_to_enroll"));
                enrollee.setProgramAppliedFor(rs.getString("program_applied_for"));
                enrollee.setPhotoLink(rs.getString("photo_link"));
                enrollee.setBirthCertLink(rs.getString("birth_cert_link"));
                enrollee.setReportCardLink(rs.getString("report_card_link"));
                enrollee.setForm137Link(rs.getString("form_137_link"));
                
                return enrollee;
            }
        }
        
        return null;
    }
    
    private String generateStudentId() {
        int year = java.time.LocalDate.now().getYear() % 100;
        int random = (int)(Math.random() * 9000) + 1000;
        return String.format("STU%02d-%04d", year, random);
    }
  
    private String generateUniqueEmail(String firstName, String lastName, Connection conn) throws SQLException {
        String cleanFirstName = firstName.replaceAll("[^a-zA-Z]", "").toLowerCase();
        String cleanLastName = lastName.replaceAll("[^a-zA-Z]", "").toLowerCase();
        
        String baseEmail = cleanLastName + "." + cleanFirstName + "@abakadauni.edu.ph";
        String email = baseEmail;
        int counter = 2;
        
        String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        while (true) {
            try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Generated unique university email: " + email);
                    return email;
                }
                
                email = cleanLastName + "." + cleanFirstName + counter + "@abakadauni.edu.ph";
                counter++;
                
                if (counter > 100) {
                    throw new SQLException("Unable to generate unique email after 100 attempts");
                }
            }
        }
    }
    
    private int createUserAccount(String username, String email, String password, String access, Connection conn) throws SQLException {
        String query = "INSERT INTO users (username, email, password, access, created_at, is_active) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, access);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(6, true);
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    private String getProgramId(String programCode, Connection conn) throws SQLException {
        String query = "SELECT program_id FROM programs WHERE program_code = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, programCode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("program_id");
            }
        }
        
        return null;
    }
    
    private boolean createStudentRecord(String studentId, int userId, String programId, Connection conn) throws SQLException {
        String query = "INSERT INTO students (student_id, user_id, program_id, enrollment_status, date_enrolled) " +
                      "VALUES (?, ?, ?, 'Active', ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, studentId);
            ps.setInt(2, userId);
            ps.setString(3, programId);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            
            return ps.executeUpdate() > 0;
        }
    }
    
    private boolean copyToStudentRecord(Enrollee enrollee, String studentId, String programId, Connection conn) throws SQLException {
        String query = "INSERT INTO student_record (student_record_id, student_id, first_name, middle_name, " +
                      "last_name, suffix, birth_date, gender, address, province, city, contact_number, " +
                      "email_address, guardian_name, guardian_contact, last_school_attended, " +
                      "school_year_enrolled, program_id, year_level, student_type, photo_link, " +
                      "birth_cert_link, report_card_link, form_137_link, record_type, record_date) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Admission', ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            String recordId = "REC-" + studentId;
            
            ps.setString(1, recordId);
            ps.setString(2, studentId);
            ps.setString(3, enrollee.getFirstName());
            ps.setString(4, enrollee.getMiddleName());
            ps.setString(5, enrollee.getLastName());
            ps.setString(6, enrollee.getSuffix());
            ps.setDate(7, enrollee.getBirthDate() != null ? java.sql.Date.valueOf(enrollee.getBirthDate()) : null);
            ps.setString(8, enrollee.getGender());
            ps.setString(9, enrollee.getAddress());
            ps.setString(10, enrollee.getProvince());
            ps.setString(11, enrollee.getCity());
            ps.setString(12, enrollee.getContactNumber());
            ps.setString(13, enrollee.getEmailAddress());
            ps.setString(14, enrollee.getGuardianName());
            ps.setString(15, enrollee.getGuardianContact());
            ps.setString(16, enrollee.getLastSchoolAttended());
            ps.setString(17, enrollee.getLastSchoolYear());
            ps.setString(18, programId);
            ps.setString(19, enrollee.getYearLevel());
            ps.setString(20, enrollee.getStudentType());
            ps.setString(21, enrollee.getPhotoLink());
            ps.setString(22, enrollee.getBirthCertLink());
            ps.setString(23, enrollee.getReportCardLink());
            ps.setString(24, enrollee.getForm137Link());
            ps.setTimestamp(25, Timestamp.valueOf(LocalDateTime.now()));
            
            return ps.executeUpdate() > 0;
        }
    }
    
    private boolean updateEnrolleeStatus(String enrolleeId, String status, String adminComment, Connection conn) throws SQLException {
        String query = "UPDATE enrollees SET enrollment_status = ?, admin_approval_message = ?, " +
                      "reviewed_by = ?, reviewed_on = ? WHERE enrollee_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, adminComment);
            ps.setInt(3, SessionManager.getInstance().getUserId());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, enrolleeId);
            
            return ps.executeUpdate() > 0;
        }
    }
    
   private int assignToSectionAndEnroll(String studentId, String programId, String yearLevel, Connection conn) {
        int enrolledCount = 0;

        try {
            System.out.println("\n=== STARTING ENROLLMENT PROCESS ===");
            System.out.println("Student ID: " + studentId);
            System.out.println("Program ID: " + programId);
            System.out.println("Year Level: " + yearLevel);

            // Step 1: Extract year number from yearLevel (e.g., "1st Year" -> "1")
            String yearNumber = yearLevel.replaceAll("[^0-9]", "").trim();
            if (yearNumber.isEmpty()) {
                yearNumber = "1"; // Default to first year
            }
            System.out.println("Extracted year number: " + yearNumber);

            // Step 2: Get program code for section name matching
            String programCode = null;
            String programCodeQuery = "SELECT program_code FROM programs WHERE program_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(programCodeQuery)) {
                ps.setString(1, programId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    programCode = rs.getString("program_code");
                    System.out.println("Program code: " + programCode);
                }
            }

            if (programCode == null) {
                System.err.println("ERROR: Could not find program code for program_id: " + programId);
                return 0;
            }

            // Step 3: Find appropriate section (e.g., "BSIT 1-A" for BSIT 1st Year)
            String sectionQuery = "SELECT section_id, section_name FROM section " +
                                "WHERE program_id = ? " +
                                "AND section_name LIKE ? " +
                                "ORDER BY section_name " +
                                "LIMIT 1";

            String sectionPattern = programCode + " " + yearNumber + "-%"; // e.g., "BSIT 1-%"
            System.out.println("Searching for section with pattern: " + sectionPattern);

            String sectionId = null;
            String sectionName = null;

            try (PreparedStatement ps = conn.prepareStatement(sectionQuery)) {
                ps.setString(1, programId);
                ps.setString(2, sectionPattern);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    sectionId = rs.getString("section_id");
                    sectionName = rs.getString("section_name");
                    System.out.println("✓ Found section: " + sectionName + " (ID: " + sectionId + ")");
                } else {
                    System.err.println("ERROR: No section found for pattern: " + sectionPattern);
                    System.err.println("Please check if sections exist in the database for this program and year level.");
                    return 0;
                }
            }

            // Step 4: Get active semester
            String semesterQuery = "SELECT semester_id, name FROM semester WHERE is_active = 1 LIMIT 1";
            String semesterId = null;
            String semesterName = null;

            try (PreparedStatement ps = conn.prepareStatement(semesterQuery);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    semesterId = rs.getString("semester_id");
                    semesterName = rs.getString("name");
                    System.out.println("✓ Active semester: " + semesterName + " (ID: " + semesterId + ")");
                } else {
                    System.err.println("ERROR: No active semester found!");
                    return 0;
                }
            }

            // Step 5: Get all course offerings for this section in active semester
            String offeringsQuery = 
                "SELECT " +
                "  co.offering_id, " +
                "  co.course_id, " +
                "  c.course_code, " +
                "  c.course_title, " +
                "  co.capacity, " +
                "  co.enrolled_count, " +
                "  co.schedule_day, " +
                "  co.schedule_time, " +
                "  co.room " +
                "FROM course_offerings co " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "WHERE co.section_id = ? " +
                "  AND co.semester_id = ? " +
                "  AND co.status = 'Open' " +
                "  AND (co.enrolled_count < co.capacity OR co.capacity IS NULL) " +
                "ORDER BY c.course_code";

            System.out.println("\n--- AVAILABLE COURSE OFFERINGS ---");

            try (PreparedStatement ps = conn.prepareStatement(offeringsQuery)) {
                ps.setString(1, sectionId);
                ps.setString(2, semesterId);
                ResultSet rs = ps.executeQuery();

                boolean foundOfferings = false;

                while (rs.next()) {
                    foundOfferings = true;
                    String offeringId = rs.getString("offering_id");
                    String courseCode = rs.getString("course_code");
                    String courseTitle = rs.getString("course_title");
                    String scheduleDay = rs.getString("schedule_day");
                    String scheduleTime = rs.getString("schedule_time");
                    String room = rs.getString("room");
                    int capacity = rs.getInt("capacity");
                    int enrolled = rs.getInt("enrolled_count");

                    System.out.println("\nCourse: " + courseCode + " - " + courseTitle);
                    System.out.println("  Offering ID: " + offeringId);
                    System.out.println("  Schedule: " + scheduleDay + " " + scheduleTime + " @ " + room);
                    System.out.println("  Capacity: " + enrolled + "/" + capacity);

                    // Enroll student in this offering
                    if (enrollStudentInOffering(studentId, offeringId, conn)) {
                        enrolledCount++;
                        System.out.println("  ✓ ENROLLED SUCCESSFULLY");
                    } else {
                        System.err.println("  ✗ ENROLLMENT FAILED");
                    }
                }

                if (!foundOfferings) {
                    System.err.println("WARNING: No course offerings found for section " + sectionName);
                    System.err.println("Please check:");
                    System.err.println("  1. Section has courses assigned in course_offerings table");
                    System.err.println("  2. Courses are marked as 'Open' status");
                    System.err.println("  3. Courses belong to active semester: " + semesterName);
                }
            }

            System.out.println("\n=== ENROLLMENT COMPLETE ===");
            System.out.println("Total courses enrolled: " + enrolledCount);
            System.out.println("=============================\n");

        } catch (SQLException e) {
            System.err.println("ERROR during auto-enrollment: " + e.getMessage());
            e.printStackTrace();
        }

        return enrolledCount;
    }

    private boolean enrollStudentInOffering(String studentId, String offeringId, Connection conn) throws SQLException {
        // Check if already enrolled
        String checkQuery = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND offering_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            ps.setString(1, studentId);
            ps.setString(2, offeringId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("    (Already enrolled - skipping)");
                return true; // Already enrolled, count as success
            }
        }

        // Generate enrollment ID
        String enrollmentId = generateEnrollmentId();

        // Insert enrollment
        String insertQuery = 
            "INSERT INTO enrollments (enrollment_id, student_id, offering_id, enrollment_date, status) " +
            "VALUES (?, ?, ?, ?, 'Enrolled')";

        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, enrollmentId);
            ps.setString(2, studentId);
            ps.setString(3, offeringId);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                // Update enrolled count in course_offerings
                String updateQuery = "UPDATE course_offerings SET enrolled_count = enrolled_count + 1 WHERE offering_id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
                    updatePs.setString(1, offeringId);
                    updatePs.executeUpdate();
                }
                return true;
            }
        }

        return false;
    }

 
    private String generateEnrollmentId() {
        int year = java.time.LocalDate.now().getYear();
        int random = (int)(Math.random() * 9000) + 1000;
        return String.format("ENR-%d-%03d", year, random);
    }
    
    private void startAutoRefresh(int intervalSeconds) {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }

        refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(intervalSeconds), event -> {
                System.out.println("Auto-refreshing admin dashboard...");
                loadEnrollees();
            })
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
        
        System.out.println("Auto-refresh started: Every " + intervalSeconds + " seconds");
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            System.out.println("Auto-refresh stopped");
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}