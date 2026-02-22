import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    // Database Configuration
    private static final String URL = "jdbc:postgresql://localhost:5432/promanage_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Greed";

    // Static block to load PostgreSQL Driver
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    // Method to get database connection
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    // Optional: Close connection safely
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection.");
                e.printStackTrace();
            }
        }
    }
}