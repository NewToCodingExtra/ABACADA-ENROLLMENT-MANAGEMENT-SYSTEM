/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;

import java.math.BigDecimal;

public class CashPayment extends Payment {
    
    private BigDecimal amountTendered;
    private BigDecimal changeAmount;

    public CashPayment(int paymentId, String cashierId, String enrolleeId, BigDecimal amount) {
        super(paymentId, cashierId, enrolleeId, amount, null);
    }

    public void setTenderedAmount(BigDecimal tendered) {
        this.amountTendered = tendered;
        this.changeAmount = tendered.subtract(amount);
    }

    public BigDecimal getChange() {
        return changeAmount;
    }

    public BigDecimal getAmountTendered() {
        return amountTendered;
    }

    public void setAmountTendered(BigDecimal amountTendered) {
        this.amountTendered = amountTendered;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    @Override
    public void processPayment() {
        // Here you can call your DB insert for cash_payment and mark payment as paid
        System.out.println("Processing cash payment for " + enrolleeId);
    }

    @Override
    public boolean isPaid() {
        return amountTendered != null && amountTendered.compareTo(amount) >= 0;
    }
}