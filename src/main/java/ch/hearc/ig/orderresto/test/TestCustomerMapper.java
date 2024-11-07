package ch.hearc.ig.orderresto.test;

import ch.hearc.ig.orderresto.presentation.CustomerCLI;

public class TestCustomerMapper {

    public static void main(String[] args) {
        CustomerCLI customerCLI = new CustomerCLI();

        // Test creating a new customer using CustomerCLI
        //System.out.println("--- Test CustomerCLI Create New Customer ---");
        //customerCLI.createNewCustomer();

        // Test using CustomerCLI to get existing customer
        System.out.println("--- Test CustomerCLI Get Existing Customer ---");
        customerCLI.getExistingCustomer();
    }
}
