package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.business.Address;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RestaurantMapper {

    private final DatabaseConnection databaseConnection;

    public RestaurantMapper() {
        this.databaseConnection = new DatabaseConnection();
    }

    // Method to insert a restaurant into the database
    public void insert(Restaurant restaurant) {
        String sql = "INSERT INTO RESTAURANT (numero, nom, code_postal, localite, rue, num_rue, pays) VALUES (SEQ_RESTAURANT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getAddress().getPostalCode());
            stmt.setString(3, restaurant.getAddress().getLocality());
            stmt.setString(4, restaurant.getAddress().getStreet());
            stmt.setString(5, restaurant.getAddress().getStreetNumber());
            stmt.setString(6, restaurant.getAddress().getCountryCode());
            stmt.executeUpdate();
            System.out.println("Restaurant inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting restaurant: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to find a restaurant by ID
    public Optional<Restaurant> find(Long id) {
        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Restaurant restaurant = new Restaurant(
                        rs.getLong("numero"),
                        rs.getString("nom"),
                        new Address(
                                rs.getString("pays"),
                                rs.getString("code_postal"),
                                rs.getString("localite"),
                                rs.getString("rue"),
                                rs.getString("num_rue")
                        )
                );
                return Optional.of(restaurant);
            }
        } catch (SQLException e) {
            System.err.println("Error finding restaurant: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Method to update a restaurant
    public void update(Restaurant restaurant) {
        String sql = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getAddress().getPostalCode());
            stmt.setString(3, restaurant.getAddress().getLocality());
            stmt.setString(4, restaurant.getAddress().getStreet());
            stmt.setString(5, restaurant.getAddress().getStreetNumber());
            stmt.setString(6, restaurant.getAddress().getCountryCode());
            stmt.setLong(7, restaurant.getId());
            stmt.executeUpdate();
            System.out.println("Restaurant updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating restaurant: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to delete a restaurant by ID
    public void delete(Long id) {
        String sql = "DELETE FROM RESTAURANT WHERE numero = ?";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            System.out.println("Restaurant deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting restaurant: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
