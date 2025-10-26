/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;

/**
 *
 * @author Joshua
 */
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class GCashPayment extends Payment {
    private String referenceNumber;
    private String senderName;
    private String senderNumber;
    private String transactionId;
    private String screenshotLink;
    private String verificationStatus; // Pending, Verified, Failed
    private String verifiedBy;
    private Timestamp verifiedOn;

    public GCashPayment(int paymentId, String cashierId, String enrolleeId, BigDecimal amount, String referenceNumber) {
        super(paymentId, cashierId, enrolleeId, amount, null);
        this.referenceNumber = referenceNumber;
        this.verificationStatus = "Pending";
        this.transactionId = generateTransactionId();
    }

    private String generateTransactionId() {
        // Example: GCASH-<timestamp>-<randomUUID>
        return "GCASH-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public void verifyPayment(String cashierId) {
        this.verificationStatus = "Verified";
        this.verifiedBy = cashierId;
        this.verifiedOn = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public void processPayment() {
        // Insert into gcash_payment table
        System.out.println("Processing GCash payment for " + enrolleeId + " with transaction ID: " + transactionId);
    }

    @Override
    public boolean isPaid() {
        return "Verified".equals(verificationStatus);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getScreenshotLink() {
        return screenshotLink;
    }

    public void setScreenshotLink(String screenshotLink) {
        this.screenshotLink = screenshotLink;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Timestamp getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(Timestamp verifiedOn) {
        this.verifiedOn = verifiedOn;
    }
    
}
