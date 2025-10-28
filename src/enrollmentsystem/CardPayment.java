package enrollmentsystem;

import java.math.BigDecimal;

public class CardPayment extends Payment {
    private String cardNo;
    private String cardHolderName;
    private String expireMonth;
    private String expireYear;
    private String cvcNo;
    private String approvalCode;
    private String transactionId;
    private String bankName;

    public CardPayment(int paymentId, String cashierId, String enrolleeId, BigDecimal amount,
                       String cardNo, String cardHolderName, String expireMonth,
                       String expireYear, String cvcNo) {
        super(paymentId, cashierId, enrolleeId, amount, null);
        this.cardNo = cardNo;
        this.cardHolderName = cardHolderName;
        this.expireMonth = expireMonth;
        this.expireYear = expireYear;
        this.cvcNo = cvcNo;
    }

    @Override
    public void processPayment() {
        System.out.println("Processing card payment for " + enrolleeId);
    }

    @Override
    public boolean isPaid() {
        return approvalCode != null;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    public String getCvcNo() {
        return cvcNo;
    }

    public void setCvcNo(String cvcNo) {
        this.cvcNo = cvcNo;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
