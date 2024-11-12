package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.ProductMapper;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;

import java.util.List;

public class RestaurantCLI extends AbstractCLI {

    private final RestaurantMapper restaurantMapper;
    private final ProductMapper productMapper;

    public RestaurantCLI() {
        this.restaurantMapper = new RestaurantMapper();
        this.productMapper = new ProductMapper();
    }

    public Restaurant getExistingRestaurant() {
        this.ln("Choisissez un restaurant:");

        // Récupération des restaurants depuis la base de données
        List<Restaurant> allRestaurants = restaurantMapper.findAll();

        if (allRestaurants.isEmpty()) {
            this.ln("Aucun restaurant disponible.");
            return null;
        }

        // Affichage des restaurants
        for (int i = 0; i < allRestaurants.size(); i++) {
            Restaurant restaurant = allRestaurants.get(i);
            this.ln(String.format("%d. %s.", i, restaurant.getName()));
        }

        // Demander à l'utilisateur de sélectionner un restaurant
        int index = this.readIntFromUser(allRestaurants.size() - 1);
        Restaurant selectedRestaurant = allRestaurants.get(index);

        this.ln(String.format("Vous avez sélectionné: %s", selectedRestaurant.getName()));

        // Retourner le restaurant pour continuer le processus
        return selectedRestaurant;
    }
}

