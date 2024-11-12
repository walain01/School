package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.ProductMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductCLI extends AbstractCLI {

    public Product getRestaurantProduct(Restaurant restaurant) {
        this.ln(String.format("Bienvenue chez %s. Choisissez un de nos produits:", restaurant.getName()));
        List<Product> products = new ProductMapper().findAllByRestaurant(restaurant.getId());

        if (products.isEmpty()) {
            this.ln("Aucun produit disponible pour ce restaurant.");
            return null;
        }

        for (int i = 0 ; i < products.size() ; i++) {
            Product product = products.get(i);
            this.ln(String.format("%d. %s", i, product));
        }

        int index = this.readIntFromUser(products.size() - 1);
        return products.get(index);
    }

}
