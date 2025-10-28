package enrollmentsystem;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for Credit Card Payment Window
 */
public class PaymentWindowController implements Initializable {

    @FXML
    private TextField cardHolderName;
    @FXML
    private TextField cardNo;
    @FXML
    private TextField expireMonth;
    @FXML
    private TextField expireDay;
    @FXML
    private TextField cvcNo;
    @FXML
    private Button confirmBtn;
    @FXML
    private Button returnBtn;
    @FXML
    private Label nameError;
    @FXML
    private Label creditCardNoError;
    @FXML
    private Label expirationError;
    @FXML
    private Label cvcError;

    private BigDecimal tuitionFee;
    private String enrolleeId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Clear all error labels initially
        clearErrorLabels();
        
        // Load enrollee ID from session
        enrolleeId = SessionManager.getInstance().getEnrolleeId();
        
        // Calculate tuition fee
        tuitionFee = calculateTuitionFee(enrolleeId);
        
        System.out.println("Payment Window initialized for enrollee: " + enrolleeId);
        System.out.println("Tuition fee to be paid: ₱" + tuitionFee);
    }
    
    /**
     * Calculate tuition fee based on enrollee's program and year level
     */
    private BigDecimal calculateTuitionFee(String enrolleeId) {
        String query = "SELECT e.program_applied_for, e.year_level " +
                      "FROM enrollees e WHERE e.enrollee_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrolleeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String program = rs.getString("program_applied_for");
                String yearLevel = rs.getString("year_level");
                
                BigDecimal baseFee = new BigDecimal("25000.00"); // Base fee
                
                if (yearLevel != null) {
                    switch(yearLevel) {
                        case "1st Year":
                            baseFee = baseFee.add(new BigDecimal("5000.00"));
                            break;
                        case "2nd Year":
                            baseFee = baseFee.add(new BigDecimal("7000.00"));
                            break;
                        case "3rd Year":
                            baseFee = baseFee.add(new BigDecimal("9000.00"));
                            break;
                        case "4th Year":
                            baseFee = baseFee.add(new BigDecimal("11000.00"));
                            break;
                    }
                }
                
                return baseFee;
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating tuition fee: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Default tuition fee if calculation fails
        return new BigDecimal("30000.00");
    }

    @FXML
    private void confirmBtnAction(ActionEvent event) {
        // Clear previous errors
        clearErrorLabels();
        
        // Validate all fields
        if (!validateAllFields()) {
            return;
        }
        
        // Get card details
        String cardNumber = cardNo.getText().trim().replace(" ", "");
        String holderName = cardHolderName.getText().trim();
        String expMonth = expireMonth.getText().trim();
        String expYear = expireDay.getText().trim();
        String cvc = cvcNo.getText().trim();
        
        // Process payment
        try {
            // Verify card exists and has sufficient balance
            CreditCardInfo cardInfo = verifyCreditCard(cardNumber, holderName, expMonth, expYear, cvc);
            
            if (cardInfo == null) {
                showError("Card Verification Failed", 
                         "The credit card information provided does not match our records.");
                return;
            }
            
            // Check if card has sufficient balance
            if (cardInfo.getMoney().compareTo(tuitionFee) < 0) {
                showError("Insufficient Balance", 
                         "Your card balance (₱" + cardInfo.getMoney() + ") is insufficient.\n" +
                         "Required amount: ₱" + tuitionFee);
                return;
            }
            
            // Check card status
            if (!cardInfo.getStatus().equals("Active")) {
                showError("Card Not Active", 
                         "This card is " + cardInfo.getStatus() + " and cannot be used for payment.");
                return;
            }
            
            // Show confirmation dialog
            if (!showConfirmation(cardInfo)) {
                return;
            }
            
            // Process the payment
            boolean success = processCardPayment(cardInfo);
            
            if (success) {
                showSuccess("Payment Successful!", 
                           "Your tuition fee of ₱" + tuitionFee + " has been paid.\n" +
                           "Your payment is now pending cashier verification.");
                closeDialog();
            } else {
                showError("Payment Failed", 
                         "An error occurred while processing your payment. Please try again.");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during payment: " + e.getMessage());
            e.printStackTrace();
            showError("Database Error", "An error occurred while processing your payment.");
        }
    }
    
    /**
     * Validate all input fields
     */
    private boolean validateAllFields() {
        boolean isValid = true;
        
        // Validate card holder name
        String holderName = cardHolderName.getText().trim();
        if (holderName.isEmpty()) {
            nameError.setText("Card holder name is required");
            nameError.setVisible(true);
            isValid = false;
        } else if (!holderName.matches("^[a-zA-Z\\s.]+$")) {
            nameError.setText("Invalid name format");
            nameError.setVisible(true);
            isValid = false;
        }
        
        // Validate card number
        String cardNumber = cardNo.getText().trim().replace(" ", "");
        if (cardNumber.isEmpty()) {
            creditCardNoError.setText("Card number is required");
            creditCardNoError.setVisible(true);
            isValid = false;
        } else if (!cardNumber.matches("^\\d{16}$")) {
            creditCardNoError.setText("Card number must be 16 digits");
            creditCardNoError.setVisible(true);
            isValid = false;
        }
        
        // Validate expiration date
        String expMonth = expireMonth.getText().trim();
        String expYear = expireDay.getText().trim();
        if (expMonth.isEmpty() || expYear.isEmpty()) {
            expirationError.setText("Expiration date is required");
            expirationError.setVisible(true);
            isValid = false;
        } else if (!expMonth.matches("^(0[1-9]|1[0-2])$")) {
            expirationError.setText("Month must be 01-12");
            expirationError.setVisible(true);
            isValid = false;
        } else if (!expYear.matches("^\\d{2}$")) {
            expirationError.setText("Year must be 2 digits");
            expirationError.setVisible(true);
            isValid = false;
        } else {
            // Check if card is expired
            int month = Integer.parseInt(expMonth);
            int year = 2000 + Integer.parseInt(expYear);
            java.util.Calendar now = java.util.Calendar.getInstance();
            int currentYear = now.get(java.util.Calendar.YEAR);
            int currentMonth = now.get(java.util.Calendar.MONTH) + 1;
            
            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                expirationError.setText("Card has expired");
                expirationError.setVisible(true);
                isValid = false;
            }
        }
        
        // Validate CVC
        String cvc = cvcNo.getText().trim();
        if (cvc.isEmpty()) {
            cvcError.setText("CVC is required");
            cvcError.setVisible(true);
            isValid = false;
        } else if (!cvc.matches("^\\d{3}$")) {
            cvcError.setText("CVC must be 3 digits");
            cvcError.setVisible(true);
            isValid = false;
        }
        
        return isValid;
    }

    private CreditCardInfo verifyCreditCard(String cardNumber, String holderName, 
                                           String expMonth, String expYear, String cvc) throws SQLException {
        String query = "SELECT card_id, card_holder_name, bank_name, money, status " +
                      "FROM credit_card " +
                      "WHERE card_no = ? AND card_holder_name = ? AND expire_month = ? " +
                      "AND expire_year = ? AND cvc_no = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, cardNumber);
            ps.setString(2, holderName);
            ps.setString(3, expMonth);
            ps.setString(4, expYear);
            ps.setString(5, cvc);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                CreditCardInfo cardInfo = new CreditCardInfo();
                cardInfo.setCardId(rs.getInt("card_id"));
                cardInfo.setCardNumber(cardNumber);
                cardInfo.setCardHolderName(rs.getString("card_holder_name"));
                cardInfo.setBankName(rs.getString("bank_name"));
                cardInfo.setMoney(rs.getBigDecimal("money"));
                cardInfo.setStatus(rs.getString("status"));
                cardInfo.setExpireMonth(expMonth);
                cardInfo.setExpireYear(expYear);
                cardInfo.setCvc(cvc);
                return cardInfo;
            }
        }
        
        return null;
    }
    
    /**
     * Show confirmation dialog
     */
    private boolean showConfirmation(CreditCardInfo cardInfo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Payment");
        alert.setHeaderText("Payment Confirmation");
        alert.setContentText(
            "Card Holder: " + cardInfo.getCardHolderName() + "\n" +
            "Bank: " + cardInfo.getBankName() + "\n" +
            "Card Number: **** **** **** " + cardInfo.getCardNumber().substring(12) + "\n" +
            "Current Balance: ₱" + cardInfo.getMoney() + "\n\n" +
            "Amount to Deduct: ₱" + tuitionFee + "\n" +
            "Remaining Balance: ₱" + cardInfo.getMoney().subtract(tuitionFee) + "\n\n" +
            "Do you want to proceed with this payment?"
        );
        
        return alert.showAndWait().filter(response -> 
            response == javafx.scene.control.ButtonType.OK
        ).isPresent();
    }
    
    /**
     * Process card payment and create payment record
     */
    private boolean processCardPayment(CreditCardInfo cardInfo) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Create payment record
            String insertPayment = "INSERT INTO payment (cashier_id, enrollee_id, amount, payment_type, remarks) " +
                                  "VALUES (NULL, ?, ?, 'Card', 'Pending cashier verification')";
            
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
            
            // 2. Create card payment record
            String insertCardPayment = "INSERT INTO card_payment " +
                                      "(payment_id, card_holder_name, card_no, expire_month, expire_year, cvc_no, bank_name, transaction_id) " +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            String transactionId = generateTransactionId();
            
            try (PreparedStatement ps = conn.prepareStatement(insertCardPayment)) {
                ps.setInt(1, paymentId);
                ps.setString(2, cardInfo.getCardHolderName());
                ps.setString(3, cardInfo.getCardNumber());
                ps.setString(4, cardInfo.getExpireMonth());
                ps.setString(5, cardInfo.getExpireYear());
                ps.setString(6, cardInfo.getCvc());
                ps.setString(7, cardInfo.getBankName());
                ps.setString(8, transactionId);
                ps.executeUpdate();
            }
            
            // 3. Deduct amount from credit card
            String updateCard = "UPDATE credit_card SET money = money - ? WHERE card_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateCard)) {
                ps.setBigDecimal(1, tuitionFee);
                ps.setInt(2, cardInfo.getCardId());
                ps.executeUpdate();
            }
            
            // 4. Update enrollee payment status (add column if needed)
            String updateEnrollee = "UPDATE enrollees SET payment_status = 'Paid_Pending_Verification' WHERE enrollee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateEnrollee)) {
                ps.setString(1, enrolleeId);
                ps.executeUpdate();
            } catch (SQLException e) {
                // Column might not exist, create it
                if (e.getMessage().contains("Unknown column")) {
                    createPaymentStatusColumn(conn);
                    // Retry update
                    try (PreparedStatement ps = conn.prepareStatement(updateEnrollee)) {
                        ps.setString(1, enrolleeId);
                        ps.executeUpdate();
                    }
                } else {
                    throw e;
                }
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
            System.out.println("Payment processed successfully. Payment ID: " + paymentId);
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
    
    /**
     * Create payment_status column if it doesn't exist
     */
    private void createPaymentStatusColumn(Connection conn) throws SQLException {
        String alterTable = "ALTER TABLE enrollees ADD COLUMN payment_status " +
                           "ENUM('Not_Paid', 'Paid_Pending_Verification', 'Verified', 'Rejected') " +
                           "DEFAULT 'Not_Paid'";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(alterTable);
            System.out.println("Created payment_status column in enrollees table");
        }
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    /**
     * Clear all error labels
     */
    private void clearErrorLabels() {
        nameError.setText("");
        nameError.setVisible(false);
        creditCardNoError.setText("");
        creditCardNoError.setVisible(false);
        expirationError.setText("");
        expirationError.setVisible(false);
        cvcError.setText("");
        cvcError.setVisible(false);
    }
    
    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show success dialog
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void returnBtnAction(ActionEvent event) {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Helper class to store credit card information
     */
    private static class CreditCardInfo {
        private int cardId;
        private String cardNumber;
        private String cardHolderName;
        private String bankName;
        private BigDecimal money;
        private String status;
        private String expireMonth;
        private String expireYear;
        private String cvc;
        
        public int getCardId() { return cardId; }
        public void setCardId(int cardId) { this.cardId = cardId; }
        
        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        
        public String getCardHolderName() { return cardHolderName; }
        public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
        
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        
        public BigDecimal getMoney() { return money; }
        public void setMoney(BigDecimal money) { this.money = money; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getExpireMonth() { return expireMonth; }
        public void setExpireMonth(String expireMonth) { this.expireMonth = expireMonth; }
        
        public String getExpireYear() { return expireYear; }
        public void setExpireYear(String expireYear) { this.expireYear = expireYear; }
        
        public String getCvc() { return cvc; }
        public void setCvc(String cvc) { this.cvc = cvc; }
    }
}