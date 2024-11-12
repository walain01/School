package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.business.Customer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OrderMapper {

    private final DatabaseConnection databaseConnection;

    public OrderMapper() {
        this.databaseConnection = new DatabaseConnection();
    }

    // Insertion d'une commande
    public void insert(Order order) {
        String sqlOrder = "INSERT INTO COMMANDE (numero, fk_client, fk_resto, a_emporter, quand) " +
                "VALUES (SEQ_COMMANDE.NEXTVAL, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder)) {
            stmtOrder.setLong(1, order.getCustomer().getId());
            stmtOrder.setLong(2, order.getRestaurant().getId());
            stmtOrder.setString(3, order.getTakeAway() ? "O" : "N");
            stmtOrder.setTimestamp(4, java.sql.Timestamp.valueOf(order.getWhen()));

            stmtOrder.executeUpdate();

            // Insertion dans la table d'association PRODUIT_COMMANDE
            String sqlProductOrder = "INSERT INTO PRODUIT_COMMANDE (fk_commande, fk_produit) VALUES (?, ?)";
            try (PreparedStatement stmtProductOrder = conn.prepareStatement(sqlProductOrder)) {
                Product product = order.getProducts().iterator().next();
                stmtProductOrder.setLong(1, order.getId());
                stmtProductOrder.setLong(2, product.getId());
                stmtProductOrder.executeUpdate();
            }

            System.out.println("Order inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Rechercher une commande par son ID
    public Optional<Order> find(Long id) {
        String sqlOrder = "SELECT * FROM COMMANDE WHERE numero = ?";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder)) {
            stmtOrder.setLong(1, id);
            ResultSet rsOrder = stmtOrder.executeQuery();
            if (rsOrder.next()) {
                CustomerMapper customerMapper = new CustomerMapper();
                RestaurantMapper restaurantMapper = new RestaurantMapper();
                Customer customer = customerMapper.find(String.valueOf(rsOrder.getLong("fk_client"))).orElse(null);
                Restaurant restaurant = restaurantMapper.findAll().stream().filter(r -> {
                    try {
                        return r.getId().equals(rsOrder.getLong("fk_resto"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().orElse(null);
                boolean isTakeAway = "O".equals(rsOrder.getString("a_emporter"));
                Order order = new Order(
                        rsOrder.getLong("numero"),
                        customer,
                        restaurant,
                        isTakeAway,
                        rsOrder.getTimestamp("quand").toLocalDateTime()
                );

                // Récupérer le produit associé à la commande
                String sqlProducts = "SELECT p.numero, p.nom, p.prix_unitaire, p.description, p.fk_resto FROM PRODUIT p " +
                        "JOIN PRODUIT_COMMANDE pc ON p.numero = pc.fk_produit WHERE pc.fk_commande = ?";
                try (PreparedStatement stmtProducts = conn.prepareStatement(sqlProducts)) {
                    stmtProducts.setLong(1, order.getId());
                    ResultSet rsProducts = stmtProducts.executeQuery();
                    if (rsProducts.next()) {
                        Product product = new Product(
                                rsProducts.getLong("numero"),
                                rsProducts.getString("nom"),
                                rsProducts.getBigDecimal("prix_unitaire"),
                                rsProducts.getString("description"),
                                restaurant
                        );
                        order.addProduct(product);
                    }
                }

                return Optional.of(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding order: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
