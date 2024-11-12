package ch.hearc.ig.orderresto.test;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.CustomerMapper;
import ch.hearc.ig.orderresto.persistence.OrderMapper;

import java.util.Optional;

import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.assertTrue;

public class TestOrderMapper {

    public static void main(String[] args) {
        TestOrderMapper tester = new TestOrderMapper();

        // Exécuter les tests
        tester.testFindOrderById();
        tester.testFindOrderAndCustomerFromCache();
    }

    public void testFindOrderById() {
        OrderMapper orderMapper = new OrderMapper();

        System.out.println("Début du test : testFindOrderById");

        // Utilisez un ID qui existe dans la base de données, par exemple 1 ou 2
        Long orderId = 5L;
        System.out.println("Tentative de recherche de la commande avec l'ID : " + orderId);

        Optional<Order> orderOpt1 = orderMapper.find(orderId);
        if (orderOpt1.isPresent()) {
            System.out.println("Commande trouvée avec succès, ID : " + orderOpt1.get().getId());
        } else {
            System.out.println("Commande avec l'ID " + orderId + " non trouvée.");
        }
        assertTrue(orderOpt1.isPresent());  // Vérifiez que la commande existe

        // Charger la même commande une deuxième fois (doit provenir du cache)
        System.out.println("Nouvelle tentative de recherche de la commande avec l'ID : " + orderId);
        Optional<Order> orderOpt2 = orderMapper.find(orderId);
        if (orderOpt2.isPresent()) {
            System.out.println("Commande trouvée avec succès depuis le cache, ID : " + orderOpt2.get().getId());
        } else {
            System.out.println("Commande avec l'ID " + orderId + " non trouvée.");
        }
        assertTrue(orderOpt2.isPresent());

        // Vérifiez qu'il s'agit bien de la même instance (preuve qu'elle vient du cache)
        assertSame(orderOpt1.get(), orderOpt2.get());

        System.out.println("testFindOrderById - Succès : La commande a été correctement récupérée du cache");
    }

    public void testFindOrderAndCustomerFromCache() {
        OrderMapper orderMapper = new OrderMapper();
        CustomerMapper customerMapper = new CustomerMapper();

        System.out.println("Début du test : testFindOrderAndCustomerFromCache");

        // Charger une commande et obtenir le client associé une première fois
        Long orderId = 5L;
        System.out.println("Tentative de recherche de la commande avec l'ID : " + orderId);
        Optional<Order> orderOpt1 = orderMapper.find(orderId);
        assertTrue(orderOpt1.isPresent());
        Order order1 = orderOpt1.get();
        Customer customer1 = order1.getCustomer();
        System.out.println("Client associé à la commande ID " + orderId + " : " + (customer1 != null ? customer1.getId() : "Aucun client trouvé"));

        // Charger le même client directement à partir du CustomerMapper
        System.out.println("Tentative de recherche du client avec l'ID : " + customer1.getId());
        Optional<Customer> customerOpt2 = customerMapper.find(String.valueOf(customer1.getId()));
        assertTrue(customerOpt2.isPresent());
        System.out.println("Client trouvé avec succès, ID : " + customerOpt2.get().getId());

        // Vérifiez qu'il s'agit bien de la même instance (preuve qu'elle vient de l'IdentityMap de CustomerMapper)
        assertSame(customer1, customerOpt2.get());

        System.out.println("testFindOrderAndCustomerFromCache - Succès : Le client a été correctement récupéré du cache");
    }
}

