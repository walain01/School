package ch.hearc.ig.orderresto.test;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.CustomerMapper;

import java.util.Optional;

import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.assertTrue;

public class TestCustomerMapper {

    public static void main(String[] args) {
        TestCustomerMapper tester = new TestCustomerMapper();

        // Exécuter le test pour vérifier l'utilisation de l'IdentityMap
        tester.testFindCustomerByEmail();
    }

    public void testFindCustomerByEmail() {
        CustomerMapper customerMapper = new CustomerMapper();

        // Charger un client une première fois
        Optional<Customer> customerOpt1 = customerMapper.find("vincent.pazeller@he-arc.ch");
        assertTrue(customerOpt1.isPresent());

        // Charger le même client une deuxième fois (doit provenir du cache)
        Optional<Customer> customerOpt2 = customerMapper.find("vincent.pazeller@he-arc.ch");
        assertTrue(customerOpt2.isPresent());

        // Vérifiez qu'il s'agit bien de la même instance (preuve qu'elle vient du cache)
        assertSame(customerOpt1.get(), customerOpt2.get());

        // Afficher le contenu de l'Identity Map après le deuxième chargement
        customerMapper.printIdentityMap();
    }
}
