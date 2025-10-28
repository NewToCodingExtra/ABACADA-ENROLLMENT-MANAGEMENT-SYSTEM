package enrollmentsystem;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javafx.beans.property.*;

/**
 * Data class for pending payment information
 * Used in Cashier dashboard table
 */
public class PendingPayment {
    private final IntegerProperty paymentId;
    private final StringProperty enrolleeId;
    private final StringProperty studentName;
    private final StringProperty programAppliedFor;
    private final StringProperty yearLevel;
    private final ObjectProperty<BigDecimal> amount;
    private final ObjectProperty<Timestamp> paymentDate;
    private final StringProperty paymentType;
    private final StringProperty paymentStatus;
    private final StringProperty cardHolderName;
    private final StringProperty bankName;
    private final StringProperty cardNumber;
    private final StringProperty transactionId;
    private final StringProperty cashierId;
    private final StringProperty cashierName;
    
    public PendingPayment() {
        this.paymentId = new SimpleIntegerProperty();
        this.enrolleeId = new SimpleStringProperty();
        this.studentName = new SimpleStringProperty();
        this.programAppliedFor = new SimpleStringProperty();
        this.yearLevel = new SimpleStringProperty();
        this.amount = new SimpleObjectProperty<>();
        this.paymentDate = new SimpleObjectProperty<>();
        this.paymentType = new SimpleStringProperty();
        this.paymentStatus = new SimpleStringProperty();
        this.cardHolderName = new SimpleStringProperty();
        this.bankName = new SimpleStringProperty();
        this.cardNumber = new SimpleStringProperty();
        this.transactionId = new SimpleStringProperty();
        this.cashierId = new SimpleStringProperty();
        this.cashierName = new SimpleStringProperty();
    }
    
    // Property getters for TableView
    public IntegerProperty paymentIdProperty() { return paymentId; }
    public StringProperty enrolleeIdProperty() { return enrolleeId; }
    public StringProperty studentNameProperty() { return studentName; }
    public StringProperty programAppliedForProperty() { return programAppliedFor; }
    public StringProperty yearLevelProperty() { return yearLevel; }
    public ObjectProperty<BigDecimal> amountProperty() { return amount; }
    public ObjectProperty<Timestamp> paymentDateProperty() { return paymentDate; }
    public StringProperty paymentTypeProperty() { return paymentType; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }
    public StringProperty cardHolderNameProperty() { return cardHolderName; }
    public StringProperty bankNameProperty() { return bankName; }
    public StringProperty cardNumberProperty() { return cardNumber; }
    public StringProperty transactionIdProperty() { return transactionId; }
    public StringProperty cashierIdProperty() { return cashierId; }
    public StringProperty cashierNameProperty() { return cashierName; }
    
    // Getters and setters
    public int getPaymentId() { return paymentId.get(); }
    public void setPaymentId(int value) { paymentId.set(value); }
    
    public String getEnrolleeId() { return enrolleeId.get(); }
    public void setEnrolleeId(String value) { enrolleeId.set(value); }
    
    public String getStudentName() { return studentName.get(); }
    public void setStudentName(String value) { studentName.set(value); }
    
    public String getProgramAppliedFor() { return programAppliedFor.get(); }
    public void setProgramAppliedFor(String value) { programAppliedFor.set(value); }
    
    public String getYearLevel() { return yearLevel.get(); }
    public void setYearLevel(String value) { yearLevel.set(value); }
    
    public BigDecimal getAmount() { return amount.get(); }
    public void setAmount(BigDecimal value) { amount.set(value); }
    
    public Timestamp getPaymentDate() { return paymentDate.get(); }
    public void setPaymentDate(Timestamp value) { paymentDate.set(value); }
    
    public String getPaymentType() { return paymentType.get(); }
    public void setPaymentType(String value) { paymentType.set(value); }
    
    public String getPaymentStatus() { return paymentStatus.get(); }
    public void setPaymentStatus(String value) { paymentStatus.set(value); }
    
    public String getCardHolderName() { return cardHolderName.get(); }
    public void setCardHolderName(String value) { cardHolderName.set(value); }
    
    public String getBankName() { return bankName.get(); }
    public void setBankName(String value) { bankName.set(value); }
    
    public String getCardNumber() { return cardNumber.get(); }
    public void setCardNumber(String value) { cardNumber.set(value); }
    
    public String getTransactionId() { return transactionId.get(); }
    public void setTransactionId(String value) { transactionId.set(value); }
    
    public String getCashierId() { return cashierId.get(); }
    public void setCashierId(String value) { cashierId.set(value); }
    
    public String getCashierName() { return cashierName.get(); }
    public void setCashierName(String value) { cashierName.set(value); }
}