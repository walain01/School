package ch.hearc.ig.orderresto.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private final String url = "jdbc:oracle:thin:@db.ig.he-arc.ch:1521:ens";
    private final String user = "alain_walther";
    private final String password = "alain_walther";  // Remplacez par votre mot de passe

    public DatabaseConnection() {
        // Constructor without parameters since everything is defined in the class
    }

    public Connection connectToMyDB() {
        Properties props = new Properties();
        props.setProperty("user", this.user);
        props.setProperty("password", this.password);

        try {
            System.out.println("Tentative de connexion à la base de données...");
            Connection connection = DriverManager.getConnection(url, props);
            System.out.println("Connexion réussie avec succès !");
            return connection;
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}