import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.util.CosmosPagedIterable;

import org.junit.Ignore;
import org.junit.Test;

public class BankTests {

    @Test
    public void authenticateTest() {

        NewBank bank = NewBank.getBank();
        assertNotNull(bank.checkLogInDetails("Christina", "lol"));
    }

    @Test
    public void loadBankCustomersTest() {

        BankCosmosDb.loadBankCustomers();

        System.err.println(Customer.getAllCustomersMap());
        assertTrue(Customer.getAllCustomersMap().size() > 0);
    }

    @Ignore("Only used to reset the user database")
    @Test
    public void resetBankCustomers() {

        BankCosmosDb.deleteDatabase();
        BankCosmosDb.retrieveOrCreateDatabase();
        BankCosmosDb.retrieveOrCreateContainerIdentity();
        BankCosmosDb.loadBankCustomers();

        BankCosmosDb.createCustomerDocument(new Customer("Admin", "1234"));
        BankCosmosDb.createCustomerDocument(
                new Customer("Bhagy", "hi").addAccount("Main", 1000.0));
        BankCosmosDb.createCustomerDocument(
                new Customer("Christina", "lol").addAccount("Savings", 1500.0));
        BankCosmosDb.createCustomerDocument(
                new Customer("John", "nhoj").addAccount("Checking", 250.0));

        assertTrue(Customer.getAllCustomersMap().size() > 0);
    }

    @Test
    public void containersExistTest() {

        CosmosPagedIterable<CosmosContainerProperties> listContainers = BankCosmosDb
                .retrieveAllContainers();

        List<String> listIds = listContainers.stream()
                .map(CosmosContainerProperties::getId)
                .collect(Collectors.toList());

        System.err.println(listIds);

        assertTrue(listIds.contains(BankCosmosDb.CONTAINER_ACCOUNTS));
        assertTrue(listIds.contains(BankCosmosDb.CONTAINER_IDENTITY));
    }

}