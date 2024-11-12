package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.identitymap.IdentityMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CustomerMapper {

    private final DatabaseConnection databaseConnection;
    private final IdentityMap<String, Customer> identityMap = new IdentityMap<>();

    public CustomerMapper() {
        this.databaseConnection = new DatabaseConnection();
    }

    public void printIdentityMap() {
        identityMap.printCache(); // Appelle printCache() de l'IdentityMap pour afficher les clients en cache
    }

    // Insertion d'un client
    public void insert(Customer customer) {
        String sql = "INSERT INTO CLIENT (numero, email, telephone, code_postal, localite, rue, num_rue, pays, type, prenom, nom, forme_sociale, est_une_femme) " +
                "VALUES (SEQ_CLIENT.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"numero"})) {

            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getAddress().getPostalCode());
            stmt.setString(4, customer.getAddress().getLocality());
            stmt.setString(5, customer.getAddress().getStreet());
            stmt.setString(6, customer.getAddress().getStreetNumber());
            stmt.setString(7, customer.getAddress().getCountryCode());

            if (customer instanceof PrivateCustomer) {
                stmt.setString(8, "P");
                stmt.setString(9, ((PrivateCustomer) customer).getFirstName());
                stmt.setString(10, ((PrivateCustomer) customer).getLastName());
                stmt.setString(11, null);
                stmt.setString(12, ((PrivateCustomer) customer).getGender().equals("H") ? "N" : "O");
            } else if (customer instanceof OrganizationCustomer) {
                stmt.setString(8, "O");
                stmt.setString(9, null);
                stmt.setString(10, ((OrganizationCustomer) customer).getName());
                stmt.setString(11, ((OrganizationCustomer) customer).getLegalForm());
                stmt.setString(12, null);
            }

            stmt.executeUpdate();

            // Récupérer l'ID généré pour le client
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long generatedId = generatedKeys.getLong(1);
                customer.setId(generatedId); // Assigner l'ID généré au client
            } else {
                throw new SQLException("La création du client a échoué, aucun ID généré.");
            }

            System.out.println("Customer inserted successfully.");

        } catch (SQLException e) {
            System.err.println("Error inserting customer: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Rechercher un client par son mail
    public Optional<Customer> find(String email) {
        // Vérifier si le client est déjà dans l'Identity Map
        if (identityMap.contains(email)) {
            return Optional.of(identityMap.get(email));
        }

        String sql = "SELECT * FROM CLIENT WHERE email = ?";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Address address = new Address(
                        rs.getString("pays"),
                        rs.getString("code_postal"),
                        rs.getString("localite"),
                        rs.getString("rue"),
                        rs.getString("num_rue")
                );
                Customer customer;
                if ("P".equals(rs.getString("type"))) {
                    customer = new PrivateCustomer(
                            rs.getLong("numero"),
                            rs.getString("telephone"),
                            rs.getString("email"),
                            address,
                            rs.getString("est_une_femme"),
                            rs.getString("prenom"),
                            rs.getString("nom")
                    );
                } else {
                    customer = new OrganizationCustomer(
                            rs.getLong("numero"),
                            rs.getString("telephone"),
                            rs.getString("email"),
                            address,
                            rs.getString("nom"),
                            rs.getString("forme_sociale")
                    );
                }

                // Ajouter le client dans l'Identity Map
                identityMap.put(email, customer);

                return Optional.of(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }


    // Mise à jour d'un client
    public void update(Customer customer) {
        String sql = "UPDATE CLIENT SET email = ?, telephone = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ?, prenom = ?, nom = ?, forme_sociale = ?, est_une_femme = ? WHERE numero = ?";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getAddress().getPostalCode());
            stmt.setString(4, customer.getAddress().getLocality());
            stmt.setString(5, customer.getAddress().getStreet());
            stmt.setString(6, customer.getAddress().getStreetNumber());
            stmt.setString(7, customer.getAddress().getCountryCode());
            if (customer instanceof PrivateCustomer) {
                stmt.setString(8, ((PrivateCustomer) customer).getFirstName());
                stmt.setString(9, ((PrivateCustomer) customer).getLastName());
                stmt.setString(10, null);
                stmt.setString(11, ((PrivateCustomer) customer).getGender().equals("H") ? "N" : "O");
            } else if (customer instanceof OrganizationCustomer) {
                stmt.setString(8, null);
                stmt.setString(9, ((OrganizationCustomer) customer).getName());
                stmt.setString(10, ((OrganizationCustomer) customer).getLegalForm());
                stmt.setString(11, null);
            }
            stmt.setLong(12, customer.getId());

            stmt.executeUpdate();
            System.out.println("Customer updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Suppression d'un client par son ID
    public void delete(Long id) {
        String sql = "DELETE FROM CLIENT WHERE numero = ?";
        try (Connection conn = databaseConnection.connectToMyDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            System.out.println("Customer deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
