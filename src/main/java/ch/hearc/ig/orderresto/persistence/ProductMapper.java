package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductMapper {

    private final DatabaseConnection databaseConnection;

    public ProductMapper() {
        this.databaseConnection = new DatabaseConnection();
    }

    // Récupérer tous les produits associés à un restaurant
    public List<Product> findAllByRestaurant(Long restaurantId) {
        String sql = "SELECT * FROM PRODUIT WHERE fk_resto = ?";
        List<Product> products = new ArrayList<>();
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, restaurantId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getLong("numero"),
                        rs.getString("nom"),
                        rs.getBigDecimal("prix_unitaire"),
                        rs.getString("description"),
                        null // Le restaurant est nul car nous le connaissons déjà depuis le contexte du `RestaurantCLI`
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error finding products by restaurant: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }
}
