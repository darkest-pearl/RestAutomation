package application;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing logs in the application.
 * Handles insertion and retrieval of log entries from the SQLite database.
 *
 * @author Musab
 */
public class LogDAO {
    private Connection connect() {
        try {
            return DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }

    /**
     * Inserts a new log entry into the database.
     *
     * @param action the user action to log
     */
    public void insertLog(String action) {
    	// DB insertion code
        String sql = "INSERT INTO logs (action, timestamp) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            stmt.setString(2, LocalDateTime.now().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all log entries from the database.
     *
     * @return a list of log entries sorted by timestamp
     */
    public List<LogEntry> getAllLogs() {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT * FROM logs ORDER BY timestamp DESC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String action = rs.getString("action");
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime timestamp = LocalDateTime.parse(rs.getString("timestamp"), formatter);
                logs.add(new LogEntry(action, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
