package enrollmentsystem;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Cash Payment Queue Manager
 * Handles multiple cash payment requests using a queue system
 */
public class CashPaymentQueue {
    
    private static CashPaymentQueue instance;
    private final Queue<CashPaymentRequest> paymentQueue;
    private final Set<String> processingEnrollees;
    
    private CashPaymentQueue() {
        this.paymentQueue = new ConcurrentLinkedQueue<>();
        this.processingEnrollees = Collections.synchronizedSet(new HashSet<>());
    }
    
    public static synchronized CashPaymentQueue getInstance() {
        if (instance == null) {
            instance = new CashPaymentQueue();
        }
        return instance;
    }
    
    /**
     * Add payment request to queue
     */
    public synchronized boolean addToQueue(CashPaymentRequest request) {
        // Check if enrollee already in queue or being processed
        if (processingEnrollees.contains(request.getEnrolleeId())) {
            System.out.println("Enrollee " + request.getEnrolleeId() + " already in queue");
            return false;
        }
        
        // Add to queue
        processingEnrollees.add(request.getEnrolleeId());
        paymentQueue.offer(request);
        
        // Assign queue number
        request.setQueueNumber(getNextQueueNumber());
        
        System.out.println("Added to queue: " + request.getEnrolleeId() + 
                         " - Queue #" + request.getQueueNumber());
        return true;
    }
    
    /**
     * Get next payment from queue
     */
    public synchronized CashPaymentRequest getNextPayment() {
        CashPaymentRequest request = paymentQueue.poll();
        if (request != null) {
            System.out.println("Processing payment for: " + request.getEnrolleeId() + 
                             " - Queue #" + request.getQueueNumber());
        }
        return request;
    }
    
    /**
     * Remove enrollee from processing set (when payment complete/cancelled)
     */
    public synchronized void removeFromProcessing(String enrolleeId) {
        processingEnrollees.remove(enrolleeId);
        System.out.println("Removed from processing: " + enrolleeId);
    }
    
    /**
     * Get current queue position for enrollee
     */
    public synchronized int getQueuePosition(String enrolleeId) {
        int position = 1;
        for (CashPaymentRequest request : paymentQueue) {
            if (request.getEnrolleeId().equals(enrolleeId)) {
                return position;
            }
            position++;
        }
        return -1; // Not in queue
    }
    
    /**
     * Get total queue size
     */
    public synchronized int getQueueSize() {
        return paymentQueue.size();
    }
    
    /**
     * Check if enrollee is in queue
     */
    public synchronized boolean isInQueue(String enrolleeId) {
        return processingEnrollees.contains(enrolleeId);
    }
    
    /**
     * Generate next queue number
     */
    private int getNextQueueNumber() {
        // Get today's queue count from database
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT COUNT(*) as count FROM payment " +
                          "WHERE payment_type = 'Cash' " +
                          "AND DATE(payment_date) = CURDATE()";
            
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") + 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting queue number: " + e.getMessage());
        }
        
        return paymentQueue.size() + 1;
    }
    
    /**
     * Get all queued payments (for display)
     */
    public synchronized List<CashPaymentRequest> getAllQueued() {
        return new ArrayList<>(paymentQueue);
    }
}

/**
 * Cash Payment Request Data Class
 */
class CashPaymentRequest {
    private String enrolleeId;
    private String studentName;
    private String program;
    private String yearLevel;
    private java.math.BigDecimal amount;
    private int queueNumber;
    private Timestamp requestTime;
    
    public CashPaymentRequest(String enrolleeId, String studentName, String program,
                             String yearLevel, java.math.BigDecimal amount) {
        this.enrolleeId = enrolleeId;
        this.studentName = studentName;
        this.program = program;
        this.yearLevel = yearLevel;
        this.amount = amount;
        this.requestTime = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public String getEnrolleeId() { return enrolleeId; }
    public void setEnrolleeId(String enrolleeId) { this.enrolleeId = enrolleeId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String name) { this.studentName = name; }
    
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    
    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }
    
    public java.math.BigDecimal getAmount() { return amount; }
    public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
    
    public int getQueueNumber() { return queueNumber; }
    public void setQueueNumber(int queueNumber) { this.queueNumber = queueNumber; }
    
    public Timestamp getRequestTime() { return requestTime; }
    public void setRequestTime(Timestamp time) { this.requestTime = time; }
}