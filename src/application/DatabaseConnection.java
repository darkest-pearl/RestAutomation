package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * Utility class to handle database connections.
 *
 * @author Musab
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Provides a connection to the MySQL database.
     *
     * @return the database connection
     * @throws SQLException if the connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

