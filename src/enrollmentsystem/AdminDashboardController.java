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
    
    // Dashboard Statistics Labels
    @FXML private Label enrolledNoLabel;
    @FXML private Label pendingEnrollmentLabel;
    @FXML private Label pendingPaymentLabel;
    @FXML private Label paidLabel;
    
    // Home Tab - All Enrollees with Verified Payment
    @FXML private TableView<EnrolleeForApproval> homeTable;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColEnrolleeID;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColStudentName;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColCourse;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColYearLevel;
    @FXML private TableColumn<EnrolleeForApproval, Void> homeColAction;
    @FXML private TableColumn<EnrolleeForApproval, String> homeColExtra;
    
    // Account Creation Tab
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
        loadDashboardStatistics(); // NEW: Load stats
        
        // Auto-refresh every 5 seconds
        startAutoRefresh(5);
        
        System.out.println("Admin Dashboard initialized for admin: " + adminId);
    }
    
    /**
     * Load dashboard statistics from database
     */
    private void loadDashboardStatistics() {
        try (Connection conn = DBConnection.getConnection()) {
            
            // 1. Total Enrolled Students (enrollment_status = 'Enrolled')
            int enrolledCount = getCountFromQuery(conn, 
                "SELECT COUNT(*) FROM enrollees WHERE enrollment_status = 'Enrolled'");
            enrolledNoLabel.setText(String.valueOf(enrolledCount));
            
            // 2. Pending Enrollments (enrollment_status = 'Pending')
            int pendingEnrollmentCount = getCountFromQuery(conn,
                "SELECT COUNT(*) FROM enrollees WHERE enrollment_status = 'Pending'");
            pendingEnrollmentLabel.setText(String.valueOf(pendingEnrollmentCount));
            
            // 3. Pending Payments (payment_status = 'Not_Paid' OR 'Paid_Pending_Verification')
            int pendingPaymentCount = getCountFromQuery(conn,
                "SELECT COUNT(*) FROM enrollees WHERE payment_status IN ('Not_Paid', 'Paid_Pending_Verification')");
            pendingPaymentLabel.setText(String.valueOf(pendingPaymentCount));
            
            // 4. Paid/Verified Payments (payment_status = 'Verified')
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
            
            // Set default values on error
            enrolledNoLabel.setText("0");
            pendingEnrollmentLabel.setText("0");
            pendingPaymentLabel.setText("0");
            paidLabel.setText("0");
        }
    }
    
    /**
     * Helper method to get count from query
     */
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
        // Enrollee ID Column
        homeColEnrolleeID.setCellValueFactory(new PropertyValueFactory<>("enrolleeId"));
        homeColEnrolleeID.setStyle("-fx-alignment: CENTER;");
        
        // Student Name Column - Clickable Hyperlink
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
        
        // Course Column
        homeColCourse.setCellValueFactory(new PropertyValueFactory<>("program"));
        homeColCourse.setStyle("-fx-alignment: CENTER;");
        
        // Year Level Column
        homeColYearLevel.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));
        homeColYearLevel.setStyle("-fx-alignment: CENTER;");
        
        // Extra Info Column (Payment Status)
        homeColExtra.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        homeColExtra.setStyle("-fx-alignment: CENTER;");
        
        // Action Column with Approve/Reject buttons
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
        
        // Refresh statistics when loading enrollees
        loadDashboardStatistics();
        
        System.out.println("Loaded " + enrollees.size() + " enrollees with verified payment");
    }
    
    /**
     * Opens a read-only view of enrollee's enrollment forms
     * FIXED: Now properly passes enrollee ID to the dialog
     */
    private void openEnrolleeViewDialog(EnrolleeForApproval enrollee) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/enrollmentsystem/EnrolleeViewDialog.fxml")
            );
            Parent root = loader.load();
            
            // Get controller and set enrollee ID
            EnrolleeViewDialogController controller = loader.getController();
            controller.setEnrolleeId(enrollee.getEnrolleeId());
            
            // Create dialog stage
            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(homeTable.getScene().getWindow());
            dialogStage.setTitle("View Enrollment - " + enrollee.getEnrolleeId());
            dialogStage.setScene(new Scene(root, 900, 700));
            dialogStage.setResizable(false);
            
            // Load data AFTER stage is set up
            controller.loadAndDisplay();
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Error opening enrollee view: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to open enrollee details: " + e.getMessage());
        }
    }
    
    /**
     * Handles enrollee approval process
     */
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
            "• Assign to a section with schedule"
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
    
    /**
     * Approves enrollee and creates student account
     */
    private boolean approveEnrolleeAndCreateAccount(EnrolleeForApproval enrollee) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            
            conn.setAutoCommit(false);
            
            // 1. Load full enrollee data
            Enrollee fullEnrollee = loadFullEnrolleeData(enrollee.getEnrolleeId(), conn);
            if (fullEnrollee == null) {
                conn.rollback();
                return false;
            }
            
            // 2. Generate student credentials
            String studentId = generateStudentId();
            String username = studentId;
            String password = "1234" + fullEnrollee.getLastName();
            
            // 3. Generate unique university email
            String universityEmail = generateUniqueEmail(
                fullEnrollee.getFirstName(), 
                fullEnrollee.getLastName(), 
                conn
            );
            
            // 4. Create user account with university email
            int userId = createUserAccount(username, universityEmail, password, "Student", conn);
            if (userId == 0) {
                conn.rollback();
                return false;
            }
            
            // 4. Get program_id from program code
            String programId = getProgramId(enrollee.getProgram(), conn);
            
            // 5. Create student record
            if (!createStudentRecord(studentId, userId, programId, conn)) {
                conn.rollback();
                return false;
            }
            
            // 6. Copy enrollee data to student_record
            if (!copyToStudentRecord(fullEnrollee, studentId, programId, conn)) {
                conn.rollback();
                return false;
            }
            
            // 7. Update enrollee status to 'Enrolled' and add credentials to admin comment
            String credentialsMessage = String.format(
                "You are officially enrolled! Log in using your student account.\n\n" +
                "Student ID: %s\n" +
                "Username: %s\n" +
                "Password: %s\n" +
                "University Email: %s",
                studentId, username, password, universityEmail
            );
            
            if (!updateEnrolleeStatus(enrollee.getEnrolleeId(), "Enrolled", credentialsMessage, conn)) {
                conn.rollback();
                return false;
            }
            
            // 8. Assign to section (for auto-scheduler)
            assignToSection(studentId, programId, fullEnrollee.getYearLevel(), conn);
            
            conn.commit();
            
            // Show success message with credentials
            showCredentialsDialog(enrollee.getStudentName(), studentId, username, password, universityEmail);
            
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
    
    /**
     * Shows credentials dialog with copy functionality
     */
    private void showCredentialsDialog(String studentName, String studentId, String username, String password, String universityEmail) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Enrollment Approved");
        alert.setHeaderText("Student Account Created for " + studentName);
        
        String content = String.format(
            "╔═══════════════════════════════════╗\n" +
            "        STUDENT LOGIN CREDENTIALS\n" +
            "╚═══════════════════════════════════╝\n\n" +
            "Student ID: %s\n" +
            "Username: %s\n" +
            "Password: %s\n" +
            "University Email: %s\n\n" +
            "═══════════════════════════════════════\n\n" +
            "IMPORTANT INSTRUCTIONS:\n\n" +
            "1. These credentials have been sent to the\n" +
            "   enrollee's dashboard under Admin Comment.\n\n" +
            "2. The enrollee can now log in using their\n" +
            "   student account.\n\n" +
            "3. The enrollee account will remain active\n" +
            "   until the student logs in for the first time.\n\n" +
            "═══════════════════════════════════════",
            studentId, username, password, universityEmail
        );
        
        alert.setContentText(content);
        alert.getDialogPane().setPrefWidth(550);
        
        // Remove default OK button and add custom buttons in order
        alert.getButtonTypes().clear();
        ButtonType copyButton = new ButtonType("Copy Credentials", ButtonBar.ButtonData.LEFT);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().addAll(copyButton, okButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == copyButton) {
            // Copy to clipboard
            String credentials = String.format(
                "Student ID: %s\nUsername: %s\nPassword: %s\nUniversity Email: %s",
                studentId, username, password, universityEmail
            );
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content2 = new javafx.scene.input.ClipboardContent();
            content2.putString(credentials);
            clipboard.setContent(content2);
            
            showInfo("Copied", "Credentials copied to clipboard!");
            showCredentialsDialog(studentName, studentId, username, password, universityEmail); // Show again
        }
    }
    
    /**
     * Handles enrollee rejection
     */
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
    
    /**
     * Rejects enrollment and stores reason
     */
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
    
    /**
     * Generate unique university email in format: LastName.FirstName@abakadauni.edu.ph
     * If exists, append number: LastName.FirstName2@abakadauni.edu.ph
     */
    private String generateUniqueEmail(String firstName, String lastName, Connection conn) throws SQLException {
        // Clean names (remove spaces, special chars, convert to lowercase)
        String cleanFirstName = firstName.replaceAll("[^a-zA-Z]", "").toLowerCase();
        String cleanLastName = lastName.replaceAll("[^a-zA-Z]", "").toLowerCase();
        
        String baseEmail = cleanLastName + "." + cleanFirstName + "@abakadauni.edu.ph";
        String email = baseEmail;
        int counter = 2;
        
        // Check if email exists
        String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        while (true) {
            try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next() && rs.getInt(1) == 0) {
                    // Email doesn't exist, we can use it
                    System.out.println("Generated unique university email: " + email);
                    return email;
                }
                
                // Email exists, try next number
                email = cleanLastName + "." + cleanFirstName + counter + "@abakadauni.edu.ph";
                counter++;
                
                // Safety limit to prevent infinite loop
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
    
    private void assignToSection(String studentId, String programId, String yearLevel, Connection conn) {
        // Auto-assign to section based on program and year level
        // This integrates with the AutoScheduler
        try {
            String query = "SELECT section_id FROM section WHERE program_id = ? " +
                          "AND section_name LIKE ? LIMIT 1";
            
            String yearPrefix = yearLevel.charAt(0) + "";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, programId);
                ps.setString(2, "%" + yearPrefix + "%");
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    String sectionId = rs.getString("section_id");
                    System.out.println("Assigned student " + studentId + " to section " + sectionId);
                    // Future: Enroll in section's scheduled courses
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error assigning to section: " + e.getMessage());
        }
    }
    
    private void startAutoRefresh(int intervalSeconds) {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }

        refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(intervalSeconds), event -> {
                System.out.println("Auto-refreshing admin dashboard...");
                loadEnrollees(); // This will also refresh statistics
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