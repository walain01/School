package ch.hearc.ig.orderresto.test;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.presentation.OrderCLI;

public class TestOrderCLI {

    public static void main(String[] args) {
        OrderCLI orderCLI = new OrderCLI();

        // Créer une nouvelle commande via l'interface CLI
       /* System.out.println("--- Creating a New Order ---");
        Order newOrder = orderCLI.createNewOrder();
        if (newOrder == null) {
            System.out.println("Order creation was canceled or failed.");
        } else {
            System.out.println("Order created successfully with ID: " + newOrder.getId());
        }*/

        // Sélectionner une commande existante via l'interface CLI
        System.out.println("--- Selecting an Existing Order ---");
        Order selectedOrder = orderCLI.selectOrder();
        if (selectedOrder == null) {
            System.out.println("No order selected or found.");
        } else {
            // Afficher les détails de la commande
            System.out.println("--- Displaying Selected Order ---");
            orderCLI.displayOrder(selectedOrder);
        }
    }
}
