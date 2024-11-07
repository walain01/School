package ch.hearc.ig.orderresto.test;

import ch.hearc.ig.orderresto.presentation.RestaurantCLI;

public class TestRestaurantMapper {

    public static void main(String[] args) {
        RestaurantCLI restaurantCLI = new RestaurantCLI();

        // Test displaying all restaurants
        System.out.println("--- Test Display All Restaurants ---");
        restaurantCLI.getExistingRestaurant();
    }
}
