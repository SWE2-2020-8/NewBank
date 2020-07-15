import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.util.CosmosPagedIterable;

import org.junit.Test;

public class BankTests {

    @Test
    public void authenticateTest() {

        NewBank bank = NewBank.getBank();
        assertNotNull(bank.checkLogInDetails("Christina", "lol"));
    }

    @Test
    public void loadCustomersWriteDocsTest() {

        BankCosmosDb testDb = new BankCosmosDb();
        testDb.loadBankCustomers();
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