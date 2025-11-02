package enrollmentsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.InetAddress;
import javax.swing.JOptionPane;

public class DBConnection {
    // Remote database configuration (for development)
    private static final String REMOTE_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12804580";
    private static final String REMOTE_USER = "sql12804580";
    private static final String REMOTE_PASSWORD = "JSrtCUQHCb";
    
    // Configuration flag - set to true for production (embedded), false for development (remote)
    private static final boolean USE_EMBEDDED = true;
    
    /**
     * Check if device has internet connection
     */
    private static boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return address.isReachable(3000);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get database connection - automatically chooses embedded or remote
     */
    public static Connection getConnection() {
        Connection conn = null;
        
        // Use embedded database for production
        if (USE_EMBEDDED) {
            try {
                conn = EmbeddedDatabaseManager.getEmbeddedConnection();
                System.out.println("Connected to embedded database!");
                return conn;
            } catch (SQLException e) {
                System.err.println("Embedded database connection failed: " + e.getMessage());
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to connect to local database!\n\n" +
                    "Error: " + e.getMessage() +
                    "\n\nPlease restart the application.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return null;
            }
        }
        
        // Use remote database for development
        return getRemoteConnection();
    }
    
    /**
     * Get connection to remote database (for development)
     */
    private static Connection getRemoteConnection() {
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
            conn = DriverManager.getConnection(REMOTE_URL, REMOTE_USER, REMOTE_PASSWORD);
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
    
    /**
     * Check if using embedded database
     */
    public static boolean isUsingEmbedded() {
        return USE_EMBEDDED;
    }
}