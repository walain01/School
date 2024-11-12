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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderMapper {

    private final DatabaseConnection databaseConnection;

    public OrderMapper() {
        this.databaseConnection = new DatabaseConnection();
    }

    // Insertion d'une commande
    public void insert(Order order) {
        if (order.getCustomer() == null || order.getRestaurant() == null) {
            throw new IllegalArgumentException("Le client ou le restaurant de la commande ne peut pas être null.");
        }

        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins un produit.");
        }

        String sqlOrder = "INSERT INTO COMMANDE (fk_client, fk_resto, a_emporter, quand) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder)) {

            // Préparer et exécuter l'insertion de la commande
            stmtOrder.setLong(1, order.getCustomer().getId());
            stmtOrder.setLong(2, order.getRestaurant().getId());
            stmtOrder.setString(3, order.getTakeAway() ? "O" : "N");
            stmtOrder.setTimestamp(4, java.sql.Timestamp.valueOf(order.getWhen()));
            stmtOrder.executeUpdate();

            // Récupérer l'ID généré pour la commande en utilisant la séquence et l'ID maximum
            String sqlGetId = "SELECT MAX(numero) AS max_id FROM COMMANDE";
            try (PreparedStatement stmtGetId = conn.prepareStatement(sqlGetId);
                 ResultSet rs = stmtGetId.executeQuery()) {
                if (rs.next()) {
                    Long generatedId = rs.getLong("max_id");
                    order.setId(generatedId); // Assigner l'ID généré à la commande
                } else {
                    throw new SQLException("La récupération de l'ID de la commande a échoué.");
                }
            }

            // Insertion dans la table d'association PRODUIT_COMMANDE après récupération de l'ID de la commande
            String sqlProductOrder = "INSERT INTO PRODUIT_COMMANDE (fk_commande, fk_produit) VALUES (?, ?)";
            try (PreparedStatement stmtProductOrder = conn.prepareStatement(sqlProductOrder)) {
                for (Product product : order.getProducts()) {
                    stmtProductOrder.setLong(1, order.getId());  // Utiliser l'ID de la commande récupéré
                    stmtProductOrder.setLong(2, product.getId());
                    stmtProductOrder.executeUpdate();
                }
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

    public List<Order> findOrdersByCustomerId(Long customerId) {
        String sql = "SELECT * FROM COMMANDE WHERE fk_client = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CustomerMapper customerMapper = new CustomerMapper();
                RestaurantMapper restaurantMapper = new RestaurantMapper();
                Customer customer = customerMapper.find(String.valueOf(customerId)).orElse(null);
                Restaurant restaurant = restaurantMapper.findAll().stream().filter(r -> {
                    try {
                        return r.getId().equals(rs.getLong("fk_resto"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().orElse(null);
                boolean isTakeAway = "O".equals(rs.getString("a_emporter"));
                Order order = new Order(
                        rs.getLong("numero"),
                        customer,
                        restaurant,
                        isTakeAway,
                        rs.getTimestamp("quand").toLocalDateTime()
                );

                // Récupérer les produits associés à la commande
                String sqlProducts = "SELECT p.numero, p.nom, p.prix_unitaire, p.description, p.fk_resto FROM PRODUIT p " +
                        "JOIN PRODUIT_COMMANDE pc ON p.numero = pc.fk_produit WHERE pc.fk_commande = ?";
                try (PreparedStatement stmtProducts = conn.prepareStatement(sqlProducts)) {
                    stmtProducts.setLong(1, order.getId());
                    ResultSet rsProducts = stmtProducts.executeQuery();
                    while (rsProducts.next()) {
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

                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by customer ID: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

}
