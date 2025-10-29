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
 * Controller for GCash Payment Window
 */
public class PaymentWindow2Controller implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private Label phoneNoError;
    @FXML private TextField phoneNoField;
    @FXML
    private TextField pinField;
    @FXML private Label pinError;
    @FXML private Button confirmBtn;
    @FXML private Button returnBtn;
    private Label amountLabel;

    private BigDecimal tuitionFee;
    private String enrolleeId;
    @FXML
    private Label phoneNoError1;
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearErrorLabels();
        
        enrolleeId = SessionManager.getInstance().getEnrolleeId();
        tuitionFee = calculateTuitionFee(enrolleeId);
        
        if (amountLabel != null) {
            amountLabel.setText("₱" + String.format("%,.2f", tuitionFee));
        }
        
        // Add phone number formatting
        phoneNoField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && newVal.length() > 11) {
                phoneNoField.setText(old);
            }
        });
        
        // PIN should be 6 digits
        if (pinField != null) {
            pinField.textProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && !newVal.matches("\\d*")) {
                    pinField.setText(old);
                } else if (newVal != null && newVal.length() > 6) {
                    pinField.setText(old);
                }
            });
        }
        
        System.out.println("GCash Payment Window initialized for enrollee: " + enrolleeId);
        System.out.println("Tuition fee to be paid: ₱" + tuitionFee);
    }

    @FXML
    private void handleConfirmAction(ActionEvent event) {
        clearErrorLabels();
        
        if (!validateAllFields()) {
            return;
        }
        
        String phoneNumber = phoneNoField.getText().trim();
        String accountName = fullNameField.getText().trim();
        String pin = pinField != null ? pinField.getText().trim() : "";
        
        try {
            GCashAccountInfo accountInfo = verifyGCashAccount(phoneNumber, accountName, pin);
            
            if (accountInfo == null) {
                showError("Account Verification Failed", 
                         "The GCash account information provided does not match our records.\n" +
                         "Please check your phone number, name, and PIN.");
                return;
            }
            
            if (accountInfo.getBalance().compareTo(tuitionFee) < 0) {
                showError("Insufficient Balance", 
                         "Your GCash balance (₱" + String.format("%,.2f", accountInfo.getBalance()) + 
                         ") is insufficient.\nRequired amount: ₱" + String.format("%,.2f", tuitionFee));
                return;
            }
            
            if (!"Active".equals(accountInfo.getStatus())) {
                showError("Account Not Active", 
                         "This GCash account is " + accountInfo.getStatus() + 
                         " and cannot be used for payment.");
                return;
            }
            
            if (!showConfirmation(accountInfo)) {
                return;
            }
            
            boolean success = processGCashPayment(accountInfo);
            
            if (success) {
                showSuccess("Payment Successful!", 
                           "Your tuition fee of ₱" + String.format("%,.2f", tuitionFee) + 
                           " has been paid via GCash.\n\n" +
                           "Your payment is now pending cashier verification.\n" +
                           "Please check your enrollee dashboard for updates.");
                closeDialog();
            } else {
                showError("Payment Failed", 
                         "An error occurred while processing your payment. Please try again.");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during GCash payment: " + e.getMessage());
            e.printStackTrace();
            showError("Database Error", "An error occurred while processing your payment.");
        }
    }

    @FXML
    private void handleReturnAction(ActionEvent event) {
        closeDialog();
    }
    
    private boolean validateAllFields() {
        boolean isValid = true;
        
        // Validate full name
        String name = fullNameField.getText().trim();
        if (name.isEmpty()) {
            showFieldError(phoneNoError, "Account holder name is required");
            isValid = false;
        } else if (!name.matches("^[a-zA-Z\\s.]+$")) {
            showFieldError(phoneNoError, "Invalid name format");
            isValid = false;
        }
        
        // Validate phone number
        String phone = phoneNoField.getText().trim();
        if (phone.isEmpty()) {
            showFieldError(phoneNoError, "Phone number is required");
            isValid = false;
        } else if (!phone.matches("^09\\d{9}$")) {
            showFieldError(phoneNoError, "Phone number must be 11 digits (09XXXXXXXXX)");
            isValid = false;
        }
        
        // Validate PIN
        if (pinField != null) {
            String pin = pinField.getText().trim();
            if (pin.isEmpty()) {
                showFieldError(pinError, "6-digit PIN is required");
                isValid = false;
            } else if (!pin.matches("^\\d{6}$")) {
                showFieldError(pinError, "PIN must be exactly 6 digits");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private GCashAccountInfo verifyGCashAccount(String phoneNumber, String accountName, String pin) 
            throws SQLException {
        String query = "SELECT account_id, account_holder_name, phone_number, balance, status " +
                      "FROM gcash_account " +
                      "WHERE phone_number = ? AND account_holder_name = ? AND pin_code = ? AND status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, phoneNumber);
            ps.setString(2, accountName);
            ps.setString(3, pin);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                GCashAccountInfo info = new GCashAccountInfo();
                info.setAccountId(rs.getInt("account_id"));
                info.setAccountHolderName(rs.getString("account_holder_name"));
                info.setPhoneNumber(rs.getString("phone_number"));
                info.setBalance(rs.getBigDecimal("balance"));
                info.setStatus(rs.getString("status"));
                info.setPin(pin);
                return info;
            }
        }
        
        return null;
    }
    
    private boolean showConfirmation(GCashAccountInfo accountInfo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm GCash Payment");
        alert.setHeaderText("Payment Confirmation");
        alert.setContentText(
            "GCash Account: " + accountInfo.getAccountHolderName() + "\n" +
            "Phone Number: " + maskPhoneNumber(accountInfo.getPhoneNumber()) + "\n" +
            "Current Balance: ₱" + String.format("%,.2f", accountInfo.getBalance()) + "\n\n" +
            "Amount to Deduct: ₱" + String.format("%,.2f", tuitionFee) + "\n" +
            "Remaining Balance: ₱" + String.format("%,.2f", 
                accountInfo.getBalance().subtract(tuitionFee)) + "\n\n" +
            "Do you want to proceed with this payment?"
        );
        
        return alert.showAndWait().filter(response -> 
            response == ButtonType.OK
        ).isPresent();
    }
    
    private boolean processGCashPayment(GCashAccountInfo accountInfo) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Create payment record
            String insertPayment = "INSERT INTO payment (cashier_id, enrollee_id, amount, payment_type, remarks) " +
                                  "VALUES (NULL, ?, ?, 'GCash', 'Pending cashier verification')";
            
            int paymentId;
            try (PreparedStatement ps = conn.prepareStatement(insertPayment, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, enrolleeId);
                ps.setBigDecimal(2, tuitionFee);
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    paymentId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get payment ID");
                }
            }
            
            // 2. Create GCash payment record
            String referenceNumber = generateReferenceNumber();
            String transactionId = generateTransactionId();
            
            String insertGCashPayment = "INSERT INTO gcash_payment " +
                                       "(payment_id, reference_number, sender_name, sender_number, " +
                                       "transaction_id, verification_status) " +
                                       "VALUES (?, ?, ?, ?, ?, 'Pending')";
            
            try (PreparedStatement ps = conn.prepareStatement(insertGCashPayment)) {
                ps.setInt(1, paymentId);
                ps.setString(2, referenceNumber);
                ps.setString(3, accountInfo.getAccountHolderName());
                ps.setString(4, accountInfo.getPhoneNumber());
                ps.setString(5, transactionId);
                ps.executeUpdate();
            }
            
            // 3. Deduct amount from GCash account
            String updateAccount = "UPDATE gcash_account SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateAccount)) {
                ps.setBigDecimal(1, tuitionFee);
                ps.setInt(2, accountInfo.getAccountId());
                ps.executeUpdate();
            }
            
            // 4. Update enrollee payment status
            String updateEnrollee = "UPDATE enrollees SET payment_status = 'Paid_Pending_Verification' " +
                                   "WHERE enrollee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateEnrollee)) {
                ps.setString(1, enrolleeId);
                ps.executeUpdate();
            }
            
            // 5. Create full tuition payment record
            String insertFullTuition = "INSERT INTO full_tuition_payment " +
                                      "(payment_id, enrollee_id, total_tuition, semester_id) " +
                                      "VALUES (?, ?, ?, (SELECT semester_id FROM semester WHERE is_active = 1 LIMIT 1))";
            
            try (PreparedStatement ps = conn.prepareStatement(insertFullTuition)) {
                ps.setInt(1, paymentId);
                ps.setString(2, enrolleeId);
                ps.setBigDecimal(3, tuitionFee);
                ps.executeUpdate();
            }
            
            conn.commit();
            System.out.println("GCash payment processed successfully. Payment ID: " + paymentId);
            System.out.println("Reference Number: " + referenceNumber);
            System.out.println("Transaction ID: " + transactionId);
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
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
    
    private String generateReferenceNumber() {
        return "GCASH-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
    
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return phoneNumber.substring(0, 4) + " *** " + phoneNumber.substring(phoneNumber.length() - 3);
    }
    
    private void clearErrorLabels() {
        if (phoneNoError != null) {
            phoneNoError.setText("");
            phoneNoError.setVisible(false);
        }
        if (pinError != null) {
            pinError.setText("");
            pinError.setVisible(false);
        }
    }
    
    private void showFieldError(Label errorLabel, String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
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
    
    private void closeDialog() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        stage.close();
    }
    
    // Helper class
    private static class GCashAccountInfo {
        private int accountId;
        private String accountHolderName;
        private String phoneNumber;
        private BigDecimal balance;
        private String status;
        private String pin;
        
        public int getAccountId() { return accountId; }
        public void setAccountId(int accountId) { this.accountId = accountId; }
        
        public String getAccountHolderName() { return accountHolderName; }
        public void setAccountHolderName(String name) { this.accountHolderName = name; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phone) { this.phoneNumber = phone; }
        
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }
}