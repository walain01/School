package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.OrderMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderCLI extends AbstractCLI {

    public Order createNewOrder() {
        this.ln("======================================================");
        Restaurant restaurant = (new RestaurantCLI()).getExistingRestaurant();

        if (restaurant == null) {
            this.ln("Aucun restaurant sélectionné. Retour au menu principal.");
            return null;
        }

        Product product = (new ProductCLI()).getRestaurantProduct(restaurant);

        if (product == null) {
            this.ln("Aucun produit sélectionné. Retour au menu principal.");
            return null;
        }

        this.ln("======================================================");
        this.ln("0. Annuler");
        this.ln("1. Je suis un client existant");
        this.ln("2. Je suis un nouveau client");

        int userChoice = this.readIntFromUser(2);
        if (userChoice == 0) {
            this.ln("Commande annulée. Retour au menu principal.");
            return null;
        }

        CustomerCLI customerCLI = new CustomerCLI();
        Customer customer = null;
        if (userChoice == 1) {
            customer = customerCLI.getExistingCustomer();
            if (customer == null) {
                this.ln("Client non trouvé. Retour au menu principal.");
                return null;
            }
        } else if (userChoice == 2) {
            customer = customerCLI.createNewCustomer();
            if (customer == null) {
                this.ln("Création du client annulée. Retour au menu principal.");
                return null;
            }
        }

        // Création de la commande
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());
        order.addProduct(product);

        // Insertion de la commande dans la base de données
        OrderMapper orderMapper = new OrderMapper();
        orderMapper.insert(order);

        this.ln("Merci pour votre commande !");
        return order;
    }

    public Order selectOrder() {
        Customer customer = (new CustomerCLI()).getExistingCustomer();
        if (customer == null) {
            this.ln(String.format("Désolé, nous ne connaissons pas cette personne."));
            return null;
        }

        // Récupérer les commandes du client depuis la base de données
        OrderMapper orderMapper = new OrderMapper();
        List<Order> orders = orderMapper.findOrdersByCustomerId(customer.getId());

        if (orders.isEmpty()) {
            this.ln(String.format("Désolé, il n'y a aucune commande pour %s", customer.getEmail()));
            return null;
        }

        this.ln("Choisissez une commande:");
        for (int i = 0 ; i < orders.size() ; i++) {
            Order order = orders.get(i);
            LocalDateTime when = order.getWhen();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
            this.ln(String.format("%d. %.2f, le %s chez %s.", i, order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        }

        int index = this.readIntFromUser(orders.size() - 1);
        return orders.get(index);
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
