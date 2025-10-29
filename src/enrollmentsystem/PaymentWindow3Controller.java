package enrollmentsystem;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for Cash Payment Window
 * Students request cash payment and get a queue number
 * Cashier processes the queue
 */
public class PaymentWindow3Controller implements Initializable {

    @FXML private Label nameError;
    @FXML private TextField fullNameField;
    @FXML private Label programError;
    @FXML private TextField programField;
    @FXML private Label dateError;
    @FXML private TextField dateField;
    @FXML private Label amountPlaceholder;
    @FXML private Button returnBtn;
    @FXML private Button confirmBtn;

    private BigDecimal tuitionFee;
    private String enrolleeId;
    private Enrollee enrollee;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearErrorLabels();
        
        enrolleeId = SessionManager.getInstance().getEnrolleeId();
        
        // Load enrollee data
        enrollee = EnrolleeDataLoader.loadEnrolleeData();
        
        if (enrollee != null) {
            // Pre-fill form with enrollee data
            String fullName = buildFullName(enrollee);
            fullNameField.setText(fullName);
            fullNameField.setEditable(false); // Read-only
            
            programField.setText(enrollee.getProgramAppliedFor() + " - " + enrollee.getYearLevel());
            programField.setEditable(false); // Read-only
            
            // Set today's date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy");
            dateField.setText(sdf.format(new java.util.Date()));
            dateField.setEditable(false); // Read-only
            
            // Calculate and display tuition fee
            tuitionFee = calculateTuitionFee(enrolleeId);
            amountPlaceholder.setText("₱" + String.format("%,.2f", tuitionFee));
        }
        
        System.out.println("Cash Payment Window initialized for enrollee: " + enrolleeId);
        System.out.println("Tuition fee: ₱" + tuitionFee);
    }

    @FXML
    private void handleReturnAction(ActionEvent event) {
        closeDialog();
    }

    @FXML
    private void handleConfirmAction(ActionEvent event) {
        clearErrorLabels();
        
        if (enrollee == null) {
            showError("Error", "Enrollee data not found. Please try again.");
            return;
        }
        
        // Check if already in queue
        CashPaymentQueue queue = CashPaymentQueue.getInstance();
        if (queue.isInQueue(enrolleeId)) {
            int position = queue.getQueuePosition(enrolleeId);
            showInfo("Already in Queue", 
                    "You are already in the payment queue.\n" +
                    "Queue Position: #" + position + "\n\n" +
                    "Please wait for the cashier to process your payment.");
            return;
        }
        
        // Confirm cash payment
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Cash Payment");
        confirmation.setHeaderText("Cash Payment Request");
        confirmation.setContentText(
            "Student: " + fullNameField.getText() + "\n" +
            "Program: " + programField.getText() + "\n" +
            "Amount: ₱" + String.format("%,.2f", tuitionFee) + "\n\n" +
            "By confirming, you will be added to the cashier's payment queue.\n" +
            "You will receive a queue number and must proceed to the cashier's window.\n\n" +
            "Do you want to proceed?"
        );
        
        if (!confirmation.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
            return;
        }
        
        try {
            // Create cash payment request
            CashPaymentRequest request = new CashPaymentRequest(
                enrolleeId,
                fullNameField.getText(),
                enrollee.getProgramAppliedFor(),
                enrollee.getYearLevel(),
                tuitionFee
            );
            
            // Add to queue
            boolean added = queue.addToQueue(request);
            
            if (!added) {
                showError("Queue Error", "You are already in the payment queue.");
                return;
            }
            
            // Create pending payment record in database
            boolean recordCreated = createPendingCashPayment(request);
            
            if (!recordCreated) {
                queue.removeFromProcessing(enrolleeId);
                showError("Error", "Failed to create payment record. Please try again.");
                return;
            }
            
            // Show queue number
            showSuccess("Added to Queue!", 
                       "You have been added to the cashier's payment queue.\n\n" +
                       "Queue Number: #" + request.getQueueNumber() + "\n" +
                       "Amount: ₱" + String.format("%,.2f", tuitionFee) + "\n\n" +
                       "Please proceed to the Cashier's Window and present your queue number.\n" +
                       "The cashier will process your payment when it's your turn.");
            
            closeDialog();
            
        } catch (Exception e) {
            System.err.println("Error creating cash payment request: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "An error occurred while processing your request.");
        }
    }
    
    /**
     * Create pending cash payment record in database
     */
    private boolean createPendingCashPayment(CashPaymentRequest request) {
        String insertPayment = "INSERT INTO payment " +
                              "(cashier_id, enrollee_id, amount, payment_type, remarks) " +
                              "VALUES (NULL, ?, ?, 'Cash', ?)";
        
        String updateEnrollee = "UPDATE enrollees SET payment_status = 'Paid_Pending_Verification' " +
                               "WHERE enrollee_id = ?";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Create payment record
            int paymentId;
            try (PreparedStatement ps = conn.prepareStatement(insertPayment, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, request.getEnrolleeId());
                ps.setBigDecimal(2, request.getAmount());
                ps.setString(3, "Cash Payment - Queue #" + request.getQueueNumber() + 
                                " - Awaiting cashier processing");
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    paymentId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get payment ID");
                }
            }
            
            // Create cash_payment record
            String insertCashPayment = "INSERT INTO cash_payment " +
                                      "(payment_id, amount_tendered, change_amount) " +
                                      "VALUES (?, 0, 0)";
            
            try (PreparedStatement ps = conn.prepareStatement(insertCashPayment)) {
                ps.setInt(1, paymentId);
                ps.executeUpdate();
            }
            
            // Update enrollee status
            try (PreparedStatement ps = conn.prepareStatement(updateEnrollee)) {
                ps.setString(1, request.getEnrolleeId());
                ps.executeUpdate();
            }
            
            // Create full tuition payment record
            String insertFullTuition = "INSERT INTO full_tuition_payment " +
                                      "(payment_id, enrollee_id, total_tuition, semester_id) " +
                                      "VALUES (?, ?, ?, (SELECT semester_id FROM semester WHERE is_active = 1 LIMIT 1))";
            
            try (PreparedStatement ps = conn.prepareStatement(insertFullTuition)) {
                ps.setInt(1, paymentId);
                ps.setString(2, request.getEnrolleeId());
                ps.setBigDecimal(3, request.getAmount());
                ps.executeUpdate();
            }
            
            conn.commit();
            System.out.println("Cash payment request created. Payment ID: " + paymentId + 
                             ", Queue #" + request.getQueueNumber());
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error creating cash payment record: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private BigDecimal calculateTuitionFee(String enrolleeId) {
        String query = "SELECT e.program_applied_for, e.year_level " +
                      "FROM enrollees e WHERE e.enrollee_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrolleeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String yearLevel = rs.getString("year_level");
                BigDecimal baseFee = new BigDecimal("25000.00");
                
                if (yearLevel != null) {
                    switch(yearLevel) {
                        case "1st Year": baseFee = baseFee.add(new BigDecimal("5000.00")); break;
                        case "2nd Year": baseFee = baseFee.add(new BigDecimal("7000.00")); break;
                        case "3rd Year": baseFee = baseFee.add(new BigDecimal("9000.00")); break;
                        case "4th Year": baseFee = baseFee.add(new BigDecimal("11000.00")); break;
                    }
                }
                return baseFee;
            }
        } catch (SQLException e) {
            System.err.println("Error calculating tuition fee: " + e.getMessage());
        }
        return new BigDecimal("30000.00");
    }
    
    private String buildFullName(Enrollee e) {
        StringBuilder name = new StringBuilder();
        if (e.getFirstName() != null) name.append(e.getFirstName());
        if (e.getMiddleName() != null && !e.getMiddleName().isEmpty()) {
            if (name.length() > 0) name.append(" ");
            name.append(e.getMiddleName());
        }
        if (e.getLastName() != null) {
            if (name.length() > 0) name.append(" ");
            name.append(e.getLastName());
        }
        if (e.getSuffix() != null && !e.getSuffix().isEmpty()) {
            name.append(" ").append(e.getSuffix());
        }
        return name.toString();
    }
    
    private void clearErrorLabels() {
        if (nameError != null) {
            nameError.setText("");
            nameError.setVisible(false);
        }
        if (programError != null) {
            programError.setText("");
            programError.setVisible(false);
        }
        if (dateError != null) {
            dateError.setText("");
            dateError.setVisible(false);
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
    
    private void closeDialog() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        stage.close();
    }
}