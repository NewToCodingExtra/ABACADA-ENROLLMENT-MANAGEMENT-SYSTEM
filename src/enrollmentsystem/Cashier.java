/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Random;

public class Cashier extends User implements UniqueIDGenerator {

    private String cashierId;
    private String firstName;
    private String lastName;

    public Cashier() {
        super();
    }

    public Cashier(int userId, String username, String email, String password,
                   String access, LocalDateTime createdAt, boolean isActive, String cashierId) {
        super(userId, username, email, password, access, createdAt, isActive);
        this.cashierId = cashierId;
    }

    public Cashier(int userId, String username, String email, String password,
                   LocalDateTime createdAt, boolean isActive) {
        super(userId, username, email, password, "Cashier", createdAt, isActive);
    }

    // Getters
    public String getCashierId() { return cashierId; }
    public String getFName() { return firstName; }
    public String getLName() { return lastName; }

    // Setters
    public void setCashierId(String cashierId) { 
        this.cashierId = cashierId; 
        SessionManager.getInstance().setCashierId(cashierId);
    }
    public void setFirstName(String fn) { firstName = fn; }
    public void setLastName(String ln) { lastName = ln; }

    @Override
    public String generateID() {
        String prefix = "CSH";
        int year = LocalDate.now().getYear() % 100;
        int randomNum = new Random().nextInt(9000) + 1000;
        return String.format("%s%02d-%04d", prefix, year, randomNum);
    }
    @Override
    public String toString() {
        return "Cashier{" +
               "cashierId='" + cashierId + '\'' +
               ", username='" + getUsername() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", access='" + getAccess() + '\'' +
               '}';
    }
}
