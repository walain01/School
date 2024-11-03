package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.FakeDb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderCLI extends AbstractCLI {

    public Order createNewOrder() {

        this.ln("======================================================");
        Restaurant restaurant = (new RestaurantCLI()).getExistingRestaurant();

        Product product = (new ProductCLI()).getRestaurantProduct(restaurant);

        this.ln("======================================================");
        this.ln("0. Annuler");
        this.ln("1. Je suis un client existant");
        this.ln("2. Je suis un nouveau client");

        int userChoice = this.readIntFromUser(2);
        if (userChoice == 0) {
            (new MainCLI()).run();
            return null;
        }
        CustomerCLI customerCLI = new CustomerCLI();
        Customer customer = null;
        if (userChoice == 1) {
            customer = customerCLI.getExistingCustomer();
        } else {
            customer = customerCLI.createNewCustomer();
            FakeDb.addCustomer(customer);
        }

        // Possible improvements:
        // - ask whether it's a takeAway order or not?
        // - Ask user for multiple products?
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());
        order.addProduct(product);

        // Actually place the order (this could/should be in a different method?)
        product.addOrder(order);
        restaurant.addOrder(order);
        customer.addOrder(order);

        this.ln("Merci pour votre commande!");

        return order;
    }

    public Order selectOrder() {
        Customer customer = (new CustomerCLI()).getExistingCustomer();
	if (customer == null) {
            this.ln(String.format("Désolé, nous ne connaissons pas cette personne."));
            return null;
        }
        Object[] orders = customer.getOrders().toArray();
        if (orders.length == 0) {
            this.ln(String.format("Désolé, il n'y a aucune commande pour %s", customer.getEmail()));
            return null;
        }
        this.ln("Choisissez une commande:");
        for (int i = 0 ; i < orders.length ; i++) {
            Order order = (Order) orders[i];
            LocalDateTime when = order.getWhen();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
            this.ln(String.format("%d. %.2f, le %s chez %s.", i, order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        }
        int index = this.readIntFromUser(orders.length - 1);
        return (Order) orders[index];
    }

    public void displayOrder(Order order) {
        LocalDateTime when = order.getWhen();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
        this.ln(String.format("Commande %.2f, le %s chez %s.:", order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        int index = 1;
        for (Product product: order.getProducts()) {
            this.ln(String.format("%d. %s", index, product));
            index++;
        }
    }
}
