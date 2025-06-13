package application;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DAO for managing order persistence and retrieval.
 * Interacts with the orders and order_items tables.
 *
 * @author Musab
 */
public class OrderDAO {

	public int saveOrder(List<MenuItem> orderItems, boolean taxed) {
	    int orderId = -1;

	    if (orderItems == null || orderItems.isEmpty()) {
	        return orderId;
	    }

	    try (Connection conn = DatabaseConnection.getConnection()) {
	        conn.setAutoCommit(false);

	        // Insert a new order with the current timestamp
	        String insertOrder = "INSERT INTO orders(timestamp, taxed) VALUES (?, ?)";
	        try (PreparedStatement stmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
	            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
	            stmt.setBoolean(2, taxed);
	            stmt.executeUpdate();

	            ResultSet keys = stmt.getGeneratedKeys();
	            if (keys.next()) {
	                orderId = keys.getInt(1);
	            }
	        }

	        // Insert items into order_items table
	        String insertItem = "INSERT INTO order_items(order_id, menu_item_id, quantity) VALUES (?, ?, ?)";
	        try (PreparedStatement stmt = conn.prepareStatement(insertItem)) {
	            for (MenuItem item : orderItems) {
	            	int quantity = Collections.frequency(orderItems, item);
	                stmt.setInt(1, orderId);
	                stmt.setInt(2, item.getId());
	                stmt.setInt(3, quantity);
	                stmt.addBatch();
	            }
	            stmt.executeBatch();
	        }

	        conn.commit();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return orderId;
	}

    public List<Order> getTodayOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.id, o.timestamp, o.taxed, m.id AS menu_id, m.name, m.category, m.price
            FROM orders o
            JOIN order_items oi ON o.id = oi.order_id
            JOIN menu_items m ON m.id = oi.menu_item_id
            WHERE DATE(o.timestamp) = CURDATE()
            ORDER BY o.id DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Integer, Order> orderMap = new HashMap<>();

            while (rs.next()) {
                int orderId = rs.getInt("id");
                Order order = orderMap.getOrDefault(orderId, new Order());
                order.setId(orderId);
                order.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                order.setIsTaxed(rs.getBoolean("taxed"));

                MenuItem item = new MenuItem();
                item.setId(rs.getInt("menu_id"));
                item.setName(rs.getString("name"));
                item.setCategory(rs.getString("category"));
                item.setType(rs.getString("category"));
                item.setPrice(rs.getDouble("price"));
                

                order.getOrdersList().add(item);
                orderMap.put(orderId, order);
            }

            orders.addAll(orderMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.id, o.timestamp, o.taxed, m.id AS menu_id, m.name, m.category, m.price
            FROM orders o
            JOIN order_items oi ON o.id = oi.order_id
            JOIN menu_items m ON m.id = oi.menu_item_id
            ORDER BY o.id DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Integer, Order> orderMap = new HashMap<>();

            while (rs.next()) {
                int orderId = rs.getInt("id");
                Order order = orderMap.getOrDefault(orderId, new Order());
                order.setId(orderId);
                order.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                order.setIsTaxed(rs.getBoolean("taxed"));

                MenuItem item = new MenuItem();
                item.setId(rs.getInt("menu_id"));
                item.setName(rs.getString("name"));
                item.setCategory(rs.getString("category"));
                item.setType(rs.getString("category"));
                item.setPrice(rs.getDouble("price"));
                

                order.getOrdersList().add(item);
                orderMap.put(orderId, order);
            }

            orders.addAll(orderMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Boolean getIsTaxed(int orderId) {
        String sql = "SELECT taxed FROM orders WHERE id = ?";
        boolean isTaxed = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                 isTaxed =rs.getBoolean("taxed"); // or rs.getBoolean(1);
            } else {
                System.out.println("Order with ID " + orderId + " not found.");
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isTaxed;
        
    }

    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
