package application;

import java.time.LocalDateTime;

/**
 * Represents a log entry in the system.
 * Used to record user actions with timestamps.
 *
 * @author Musab
 */
public class LogEntry {
    private int id;
    private String action;
    private LocalDateTime timestamp;
    private static int idCounter;

    // Constructor, Getters, Setters
    
    public LogEntry (String action, LocalDateTime timestamp) {
    	this.id = ++idCounter;
    	this.action = action;
    	this.timestamp = timestamp;
    }
    
    public int getId() {
    	return id;
    }
    
    public String getAction() {
    	return action;
    }
    
    public LocalDateTime getTimeStamp() {
    	return timestamp;
    }
}
