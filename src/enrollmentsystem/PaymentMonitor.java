package enrollmentsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Monitors payment status changes in the database
 * Used by Cashier dashboard to load pending payments
 */
public class PaymentMonitor {
    
    /**
     * Get all pending payments that need cashier verification
     */
    public static List<PendingPayment> getPendingPayments() {
        List<PendingPayment> pendingPayments = new ArrayList<>();
        
        String query = "SELECT " +
                      "p.payment_id, " +
                      "p.enrollee_id, " +
                      "p.amount, " +
                      "p.payment_date, " +
                      "p.payment_type, " +
                      "e.first_name, " +
                      "e.middle_name, " +
                      "e.last_name, " +
                      "e.program_applied_for, " +
                      "e.year_level, " +
                      "e.payment_status, " +
                      "cp.card_holder_name, " +
                      "cp.bank_name, " +
                      "cp.card_no, " +
                      "cp.transaction_id " +
                      "FROM payment p " +
                      "JOIN enrollees e ON p.enrollee_id = e.enrollee_id " +
                      "LEFT JOIN card_payment cp ON p.payment_id = cp.payment_id " +
                      "WHERE e.payment_status = 'Paid_Pending_Verification' " +
                      "AND p.cashier_id IS NULL " +
                      "ORDER BY p.payment_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PendingPayment payment = new PendingPayment();
                
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setEnrolleeId(rs.getString("enrollee_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getTimestamp("payment_date"));
                payment.setPaymentType(rs.getString("payment_type"));
                
                // Student info
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                payment.setStudentName(buildFullName(firstName, middleName, lastName));
                
                payment.setProgramAppliedFor(rs.getString("program_applied_for"));
                payment.setYearLevel(rs.getString("year_level"));
                payment.setPaymentStatus(rs.getString("payment_status"));
                
                // Card payment info (if applicable)
                if ("Card".equals(payment.getPaymentType())) {
                    payment.setCardHolderName(rs.getString("card_holder_name"));
                    payment.setBankName(rs.getString("bank_name"));
                    payment.setCardNumber(maskCardNumber(rs.getString("card_no")));
                    payment.setTransactionId(rs.getString("transaction_id"));
                }
                
                pendingPayments.add(payment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading pending payments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pendingPayments;
    }
    
    /**
     * Get verified payments (accepted by cashier)
     */
    public static List<PendingPayment> getVerifiedPayments() {
        List<PendingPayment> verifiedPayments = new ArrayList<>();
        
        String query = "SELECT " +
                      "p.payment_id, " +
                      "p.enrollee_id, " +
                      "p.amount, " +
                      "p.payment_date, " +
                      "p.payment_type, " +
                      "p.cashier_id, " +
                      "e.first_name, " +
                      "e.middle_name, " +
                      "e.last_name, " +
                      "e.program_applied_for, " +
                      "e.year_level, " +
                      "e.payment_status, " +
                      "c.first_name as cashier_first_name, " +
                      "c.last_name as cashier_last_name " +
                      "FROM payment p " +
                      "JOIN enrollees e ON p.enrollee_id = e.enrollee_id " +
                      "LEFT JOIN cashier c ON p.cashier_id = c.cashier_id " +
                      "WHERE e.payment_status = 'Verified' " +
                      "ORDER BY p.payment_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PendingPayment payment = new PendingPayment();
                
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setEnrolleeId(rs.getString("enrollee_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getTimestamp("payment_date"));
                payment.setPaymentType(rs.getString("payment_type"));
                payment.setCashierId(rs.getString("cashier_id"));
                
                // Student info
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                payment.setStudentName(buildFullName(firstName, middleName, lastName));
                
                payment.setProgramAppliedFor(rs.getString("program_applied_for"));
                payment.setYearLevel(rs.getString("year_level"));
                payment.setPaymentStatus(rs.getString("payment_status"));
                
                // Cashier info
                String cashierFirstName = rs.getString("cashier_first_name");
                String cashierLastName = rs.getString("cashier_last_name");
                if (cashierFirstName != null && cashierLastName != null) {
                    payment.setCashierName(cashierFirstName + " " + cashierLastName);
                }
                
                verifiedPayments.add(payment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading verified payments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return verifiedPayments;
    }
    
    /**
     * Accept payment (cashier verification)
     */
    public static boolean acceptPayment(int paymentId, String cashierId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Update payment table with cashier ID
            String updatePayment = "UPDATE payment SET cashier_id = ?, remarks = 'Payment verified and accepted' " +
                                  "WHERE payment_id = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(updatePayment)) {
                ps.setString(1, cashierId);
                ps.setInt(2, paymentId);
                ps.executeUpdate();
            }
            
            // 2. Update enrollee payment status
            String updateEnrollee = "UPDATE enrollees e " +
                                   "JOIN payment p ON e.enrollee_id = p.enrollee_id " +
                                   "SET e.payment_status = 'Verified' " +
                                   "WHERE p.payment_id = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(updateEnrollee)) {
                ps.setInt(1, paymentId);
                ps.executeUpdate();
            }
            
            // 3. Create invoice/receipt
            createInvoice(conn, paymentId);
            
            conn.commit();
            System.out.println("Payment accepted successfully. Payment ID: " + paymentId);
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error accepting payment: " + e.getMessage());
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
    
    /**
     * Reject payment with reason
     */
    public static boolean rejectPayment(int paymentId, String cashierId, String reason) {
        String updatePayment = "UPDATE payment SET cashier_id = ?, remarks = ? WHERE payment_id = ?";
        String updateEnrollee = "UPDATE enrollees e " +
                               "JOIN payment p ON e.enrollee_id = p.enrollee_id " +
                               "SET e.payment_status = 'Rejected', e.cashier_rejection_reason = ? " +
                               "WHERE p.payment_id = ?";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Create column if it doesn't exist
            createRejectionReasonColumn(conn);
            
            // Update payment
            try (PreparedStatement ps = conn.prepareStatement(updatePayment)) {
                ps.setString(1, cashierId);
                ps.setString(2, "Payment rejected: " + reason);
                ps.setInt(3, paymentId);
                ps.executeUpdate();
            }
            
            // Update enrollee
            try (PreparedStatement ps = conn.prepareStatement(updateEnrollee)) {
                ps.setString(1, reason);
                ps.setInt(2, paymentId);
                ps.executeUpdate();
            }
            
            conn.commit();
            System.out.println("Payment rejected. Payment ID: " + paymentId + ", Reason: " + reason);
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error rejecting payment: " + e.getMessage());
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
    
    /**
     * Create invoice/receipt for verified payment
     */
    private static void createInvoice(Connection conn, int paymentId) throws SQLException {
        String getPaymentInfo = "SELECT p.enrollee_id, p.amount, p.payment_date " +
                               "FROM payment p WHERE p.payment_id = ?";
        
        String insertInvoice = "INSERT INTO invoice " +
                              "(payment_id, enrollee_id, invoice_number, invoice_date, total_amount, remarks) " +
                              "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement psGet = conn.prepareStatement(getPaymentInfo)) {
            psGet.setInt(1, paymentId);
            ResultSet rs = psGet.executeQuery();
            
            if (rs.next()) {
                String enrolleeId = rs.getString("enrollee_id");
                java.math.BigDecimal amount = rs.getBigDecimal("amount");
                Timestamp paymentDate = rs.getTimestamp("payment_date");
                
                // Generate unique invoice number
                String invoiceNumber = generateInvoiceNumber(enrolleeId);
                
                try (PreparedStatement psInsert = conn.prepareStatement(insertInvoice)) {
                    psInsert.setInt(1, paymentId);
                    psInsert.setString(2, enrolleeId);
                    psInsert.setString(3, invoiceNumber);
                    psInsert.setDate(4, new Date(paymentDate.getTime()));
                    psInsert.setBigDecimal(5, amount);
                    psInsert.setString(6, "Tuition Fee Payment");
                    psInsert.executeUpdate();
                    
                    System.out.println("Invoice created: " + invoiceNumber);
                }
            }
        }
    }
    
    /**
     * Generate unique invoice number
     */
    private static String generateInvoiceNumber(String enrolleeId) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new java.util.Date());
        String randomPart = String.format("%04d", (int)(Math.random() * 10000));
        return "INV-" + datePart + "-" + randomPart;
    }
    
    /**
     * Create cashier_rejection_reason column if it doesn't exist
     */
    private static void createRejectionReasonColumn(Connection conn) {
        String alterTable = "ALTER TABLE enrollees ADD COLUMN cashier_rejection_reason TEXT";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(alterTable);
            System.out.println("Created cashier_rejection_reason column");
        } catch (SQLException e) {
            // Column might already exist
            if (!e.getMessage().contains("Duplicate column")) {
                System.err.println("Error creating column: " + e.getMessage());
            }
        }
    }
    
    /**
     * Build full name from parts
     */
    private static String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder name = new StringBuilder();
        
        if (firstName != null && !firstName.isEmpty()) {
            name.append(firstName);
        }
        
        if (middleName != null && !middleName.isEmpty()) {
            if (name.length() > 0) name.append(" ");
            name.append(middleName);
        }
        
        if (lastName != null && !lastName.isEmpty()) {
            if (name.length() > 0) name.append(" ");
            name.append(lastName);
        }
        
        return name.toString();
    }
    
    /**
     * Mask card number for security
     */
    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
    
    /**
     * Get invoice for enrollee
     */
    public static InvoiceInfo getEnrolleeInvoice(String enrolleeId) {
        String query = "SELECT i.invoice_id, i.invoice_number, i.invoice_date, i.total_amount, " +
                      "p.payment_type, p.payment_date " +
                      "FROM invoice i " +
                      "JOIN payment p ON i.payment_id = p.payment_id " +
                      "WHERE i.enrollee_id = ? " +
                      "ORDER BY i.invoice_date DESC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrolleeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                InvoiceInfo invoice = new InvoiceInfo();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setInvoiceDate(rs.getDate("invoice_date"));
                invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
                invoice.setPaymentType(rs.getString("payment_type"));
                invoice.setPaymentDate(rs.getTimestamp("payment_date"));
                return invoice;
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading invoice: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}