package ch.hearc.ig.orderresto.test;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.CustomerMapper;

import java.util.Optional;

import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.assertTrue;

public class TestCustomerMapper {

    public static void main(String[] args) {
        TestCustomerMapper tester = new TestCustomerMapper();

        // Exécuter le test
        tester.testFindCustomerByEmail();
    }

    public void testFindCustomerByEmail() {
        CustomerMapper customerMapper = new CustomerMapper();

        String email = "vincent.pazeller@he-arc.ch";

        // Charger un client une première fois
        System.out.println("Tentative de recherche du client par email : " + email);
        Optional<Customer> customerOpt1 = customerMapper.find(email);
        assertTrue(customerOpt1.isPresent());
        System.out.println("Client trouvé la première fois, email : " + customerOpt1.get().getEmail());

        // Charger le même client une deuxième fois (doit provenir du cache)
        System.out.println("Nouvelle tentative de recherche du client par email : " + email);
        Optional<Customer> customerOpt2 = customerMapper.find(email);
        assertTrue(customerOpt2.isPresent());
        System.out.println("Client trouvé la deuxième fois, email : " + customerOpt2.get().getEmail());

        // Vérifiez qu'il s'agit bien de la même instance (preuve qu'elle vient du cache)
        assertSame(customerOpt1.get(), customerOpt2.get());

        System.out.println("testFindCustomerByEmail - Succès : Le client a été correctement récupéré du cache");
    }
}

