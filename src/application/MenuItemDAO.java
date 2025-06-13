package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for accessing and managing menu items in the database.
 *
 * @author Musab
 */
public class MenuItemDAO {
	
    /**
     * Retrieves all menu items from the database.
     *
     * @return list of all menu items
     */
    public List<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items")) {

            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setCategory(rs.getString("category"));
                item.setPrice(rs.getDouble("price"));
                item.setType(rs.getString("category"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Adds a new menu item to the database.
     *
     * @param item the menu item to be added
     */
    public void addItem(MenuItem item) {
        String sql = "INSERT INTO menu_items(name, category, size, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(4, item.getPrice());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
