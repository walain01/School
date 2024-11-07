package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMapper {

    private final DatabaseConnection databaseConnection;

    public RestaurantMapper() {
        this.databaseConnection = new DatabaseConnection();
    }

    // Method to find all restaurants
    public List<Restaurant> findAll() {
        String sql = "SELECT * FROM RESTAURANT";
        List<Restaurant> restaurants = new ArrayList<>();
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
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
                restaurants.add(restaurant);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all restaurants: " + e.getMessage());
            e.printStackTrace();
        }
        return restaurants;
    }
}