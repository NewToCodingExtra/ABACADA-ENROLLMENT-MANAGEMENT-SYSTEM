package enrollmentsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.InetAddress;
import javax.swing.JOptionPane;

public class DBConnection {
    private static final String URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12802419";
    private static final String USER = "sql12802419";
    private static final String PASSWORD = "MTiIEPUBmh";
    
    /**
     * Check if device has internet connection
     */
    private static boolean isInternetAvailable() {
        try {
            // Try to reach Google's DNS server
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return address.isReachable(3000); // 3 second timeout
        } catch (Exception e) {
            return false;
        }
    }
    
    public static Connection getConnection() {
        Connection conn = null;
        
        // Check internet connection first
        if (!isInternetAvailable()) {
            JOptionPane.showMessageDialog(
                null,
                "No internet connection detected!\n\n" +
                "Please check your internet connection and try again.\n\n" +
                "The database requires an active internet connection to work.",
                "No Internet Connection",
                JOptionPane.ERROR_MESSAGE
            );
            System.out.println("No internet connection detected!");
            return null;
        }
        
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            String errorMsg = e.getMessage().toLowerCase();
            
            // Check if it's a network/connectivity issue
            if (errorMsg.contains("connection") || 
                errorMsg.contains("timeout") || 
                errorMsg.contains("network") ||
                errorMsg.contains("unreachable") ||
                errorMsg.contains("unknown host") ||
                errorMsg.contains("communications link") ||
                errorMsg.contains("connect timed out") ||
                errorMsg.contains("no route to host") ||
                errorMsg.contains("socket") ||
                errorMsg.contains("refused")) {
                
                JOptionPane.showMessageDialog(
                    null,
                    "Unable to connect to the database server.\n\n" +
                    "Please check your internet connection and try again.\n\n" +
                    "Error: " + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                // Other database errors (wrong credentials, etc.)
                JOptionPane.showMessageDialog(
                    null,
                    "Database connection failed!\n\n" +
                    "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            
            System.out.println("Database connection failed: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "An unexpected error occurred!\n\n" +
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.out.println("Unexpected error: " + e.getMessage());
        }
        return conn;
    }
}