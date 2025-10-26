package enrollmentsystem;

import java.math.BigDecimal;
import java.sql.Timestamp;

public abstract class Payment implements Payable {
    protected int paymentId;
    protected String cashierId;
    protected String enrolleeId;

    
    protected BigDecimal amount;
    protected Timestamp paymentDate;
    protected String remarks;

    public Payment(int paymentId, String cashierId, String enrolleeId, BigDecimal amount, String remarks) {
        this.paymentId = paymentId;
        this.cashierId = cashierId;
        this.enrolleeId = enrolleeId;
        this.amount = amount;
        this.remarks = remarks;
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getCashierId() {
        return cashierId;
    }

    public void setCashierId(String cashierId) {
        this.cashierId = cashierId;
    }

    public String getEnrolleeId() {
        return enrolleeId;
    }

    public void setEnrolleeId(String enrolleeId) {
        this.enrolleeId = enrolleeId;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    @Override
    public BigDecimal getAmount() {
        return amount;
    }
}