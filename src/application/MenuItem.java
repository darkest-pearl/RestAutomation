package application;

/**
 * Represents a menu item in the restaurant system.
 *
 * @author Musab
 */
public class MenuItem {
	private int id;
    private String name;
    private double price;
    private String type;
    private String category;
    
    public MenuItem(int id, String name, double price, String type, String category) {
    	this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.category = category;
    }
    
    public MenuItem() {
    	this.id = 0;
		this.name = "";
		this.price = 0;
		this.type = "";
		this.category = "";
    }
    
    public int getId() {
    	return id;
    }
    
    public void setId(int id) {
    	this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
    	this.price = price;
    }
    
    public String getType() {
    	return type;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public String getCategory() {
    	return category;
    }
    
    public void setCategory(String category) {
    	this.category = category;
    }
}
