import static org.junit.Assert.assertEquals;
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

        BankCosmosDb testDb = new BankCosmosDb();
        testDb.loadBankCustomers();
        System.err.println(Customer.getAllCustomersMap());
        assertTrue(Customer.getAllCustomersMap().size() > 0);
    }

    @Ignore("Only used to reset the user database")
    @Test
    public void resetBankCustomers() {

        BankCosmosDb testDb = new BankCosmosDb();
        testDb.deleteDatabase(BankCosmosDb.DATABASE_NAME);
        testDb.retrieveOrCreateDatabase(BankCosmosDb.DATABASE_NAME);
        testDb.retrieveOrCreateContainer(BankCosmosDb.CONTAINER_ACCOUNTS,
                "/id");

        testDb.createCustomerDocument(new Customer("Admin", "1234"));
        testDb.createCustomerDocument(
                new Customer("Bhagy", "hi").addAccount("Main", 1000.0));
        testDb.createCustomerDocument(
                new Customer("Christina", "lol").addAccount("Savings", 1500.0));
        testDb.createCustomerDocument(
                new Customer("John", "nhoj").addAccount("Checking", 250.0));
        assertTrue(Customer.getAllCustomersMap().size() > 0);
    }

    @Test
    public void databaseExistsTest() {

        BankCosmosDb testDb = new BankCosmosDb();
        testDb.retrieveDatabase(BankCosmosDb.DATABASE_NAME);
        assertEquals(BankCosmosDb.DATABASE_NAME, testDb.getDatabase().getId());
    }

    @Test
    public void containersExistTest() {

        BankCosmosDb testDb = new BankCosmosDb();
        testDb.retrieveDatabase(BankCosmosDb.DATABASE_NAME);
        CosmosPagedIterable<CosmosContainerProperties> listContainers = testDb
                .retrieveAllContainers();

        List<String> listIds = listContainers.stream()
                .map(CosmosContainerProperties::getId)
                .collect(Collectors.toList());

        assertTrue(listIds.contains(BankCosmosDb.CONTAINER_ACCOUNTS));
        assertTrue(listIds.contains(BankCosmosDb.CONTAINER_IDENTITY));
    }

}