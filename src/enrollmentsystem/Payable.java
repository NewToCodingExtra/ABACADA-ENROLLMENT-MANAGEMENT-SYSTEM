/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package enrollmentsystem;
import java.math.BigDecimal;

public interface Payable {
    void processPayment();      
    boolean isPaid();           
    BigDecimal getAmount();     
}