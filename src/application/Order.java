package application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order containing menu items, timestamp, and tax flag.
 *
 * @author Musab
 */
public class Order {
    private int id;
    private LocalDateTime timestamp;
    private List<MenuItem> items;
    private boolean isTaxed;
    private static int idCounter;

    public Order() {
        items = new ArrayList<>();
        timestamp = LocalDateTime.now();
    }
    
    public Order(List<MenuItem> items) {
    	this.id = ++idCounter;
    	for (MenuItem item : items) {
    		this.items.add(item);
    	}
    	this.timestamp = LocalDateTime.now();
    }
    
    public int getId() {
    	return id;
    }
    
    public void setId(int id) {
    	this.id = id;
    }
    
    public LocalDateTime getTimestamp() {
    	return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
    	this.timestamp = timestamp;
    }
    
    public List<MenuItem> getOrdersList() {
    	return items;
    }
    
    public boolean getIsTaxed() {
    	return isTaxed;
    }
    
    public void setIsTaxed(boolean isTaxed) {
    	this.isTaxed = isTaxed;
    }
    
    
    @Override
    public String toString() {
    	String menuItemsList = null;
    	int i = 1;
    	for (MenuItem item : items) {
    		menuItemsList = menuItemsList + "\n" + i + " - " + item.getName() + " " + item.getPrice();
    		i++;
    	}
    	return "Time: " + timestamp + "\n" + menuItemsList;
    }
    
}
