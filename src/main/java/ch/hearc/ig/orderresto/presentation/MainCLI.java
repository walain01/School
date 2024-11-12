package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.OrderMapper;

public class MainCLI extends AbstractCLI {
    private final OrderMapper orderMapper;

    public MainCLI() {
        this.orderMapper = new OrderMapper();
    }

    public void run() {
        this.ln("======================================================");
        this.ln("Que voulez-vous faire ?");
        this.ln("0. Quitter l'application");
        this.ln("1. Faire une nouvelle commande");
        this.ln("2. Consulter une commande");
        int userChoice = this.readIntFromUser(2);
        this.handleUserChoice(userChoice);
    }

    private void handleUserChoice(int userChoice) {
        if (userChoice == 0) {
            this.ln("Good bye!");
            return;
        }
        OrderCLI orderCLI = new OrderCLI();
        if (userChoice == 1) {
            Order newOrder = orderCLI.createNewOrder();
            if (newOrder != null) {
                // Insérer la nouvelle commande dans la base de données
                orderMapper.insert(newOrder);
            }
        } else {
            Order existingOrder = orderCLI.selectOrder();
            if (existingOrder != null) {
                orderCLI.displayOrder(existingOrder);
            }
        }
        this.run();
    }
}
