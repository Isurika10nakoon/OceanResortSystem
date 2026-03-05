package resort;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // ─── CHANGE THESE TO MATCH YOUR MYSQL SETUP ───────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/ocean_resort_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";          // your MySQL password
    // ───────────────────────────────────────────────────────────────

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Add mysql-connector-j.jar to your project.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
