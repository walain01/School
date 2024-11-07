package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;

import java.util.List;

public class RestaurantCLI extends AbstractCLI {

    private final RestaurantMapper restaurantMapper;

    public RestaurantCLI() {
        this.restaurantMapper = new RestaurantMapper();
    }

    public Restaurant getExistingRestaurant() {
        this.ln("Choisissez un restaurant:");
        List<Restaurant> allRestaurants = restaurantMapper.findAll();
        for (int i = 0; i < allRestaurants.size(); i++) {
            Restaurant restaurant = allRestaurants.get(i);
            this.ln(String.format("%d. %s.", i, restaurant.getName()));
        }
        int index = this.readIntFromUser(allRestaurants.size() - 1);
        return allRestaurants.get(index);
    }
}
