package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.OrderMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderCLI extends AbstractCLI {

    public Order createNewOrder() {

        this.ln("======================================================");
        // Sélection du restaurant
        Restaurant restaurant = (new RestaurantCLI()).getExistingRestaurant();

        if (restaurant == null) {
            this.ln("Restaurant non trouvé. Annulation de la commande.");
            return null;
        }

        // Sélection du produit à partir du restaurant sélectionné
        Product product = (new ProductCLI()).getRestaurantProduct(restaurant);

        if (product == null) {
            this.ln("Produit non trouvé. Annulation de la commande.");
            return null;
        }

        this.ln("======================================================");
        this.ln("0. Annuler");
        this.ln("1. Je suis un client existant");
        this.ln("2. Je suis un nouveau client");

        int userChoice = this.readIntFromUser(2);
        if (userChoice == 0) {
            this.ln("Commande annulée.");
            return null;
        }

        CustomerCLI customerCLI = new CustomerCLI();
        Customer customer = null;

        if (userChoice == 1) {
            customer = customerCLI.getExistingCustomer();
            if (customer == null) {
                this.ln("Client non trouvé. Annulation de la commande.");
                return null;
            }
        } else {
            customer = customerCLI.createNewCustomer();
        }

        // Création de la commande avec les détails choisis
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());
        order.addProduct(product);

        // Persistance de la commande avec OrderMapper
        OrderMapper orderMapper = new OrderMapper();
        orderMapper.insert(order);

        this.ln("Merci pour votre commande!");

        // Afficher les détails de la commande
        displayOrder(order);

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
