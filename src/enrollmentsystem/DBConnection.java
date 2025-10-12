package enrollmentsystem;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12802419";
    private static final String USER = "sql12802419";
    private static final String PASSWORD = "MTiIEPUBmh";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected!");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }
}