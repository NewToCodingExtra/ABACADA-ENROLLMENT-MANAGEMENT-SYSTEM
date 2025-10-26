/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;

import java.math.BigDecimal;

public class CardPayment extends Payment {
    private String cardType;
    private String cardLastFour;
    private String cardHolderName;
    private String approvalCode;
    private String transactionId;
    private String bankName;

    public CardPayment(int paymentId, String cashierId, String enrolleeId, BigDecimal amount,
                       String cardType, String cardLastFour) {
        super(paymentId, cashierId, enrolleeId, amount, null);
        this.cardType = cardType;
        this.cardLastFour = cardLastFour;
    }

    @Override
    public void processPayment() {
        System.out.println("Processing card payment for " + enrolleeId);
    }

    @Override
    public boolean isPaid() {
        return approvalCode != null;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
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
