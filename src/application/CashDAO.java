package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles cash operations including insert, update and retrieval
 * from the daily cash log table.
 *
 * @author Musab
 */
public class CashDAO {
	
	/**
     * Establishes a connection to the database.
     *
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/restaurant_db";
        String user = "root";
        String password = "root";
        return DriverManager.getConnection(url, user, password);
    }
    
    /**
     * Adds cash to today's log.
     *
     * @param amount the amount of cash to add
     */
    public void addCash(double amount) {
        String sql = "INSERT INTO cash_log (date, amount) VALUES (CURDATE(), ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the cash amount for the current day.
     *
     * @param amount the new cash amount
     */
    public void updateTodayCash(double amount) {
        String sql = "UPDATE cash_log SET amount = ? WHERE date = CURDATE()";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the amount of cash logged for today.
     *
     * @return today's logged cash amount
     */
    public double getTodayCash() {
        String sql = "SELECT amount FROM cash_log WHERE date = CURDATE()";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
