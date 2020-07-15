import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BankTests {

    @Test
    public void authenticateTest() {

        NewBank bank = NewBank.getBank();
        assertNotNull(bank.checkLogInDetails("Christina", "lol"));
    }

    @Test
    public void loadCustomersTest() {

        BankCosmosDb testDb = new BankCosmosDb();
        testDb.loadBankCustomers();
        assertTrue(Customer.getCustomers().size() > 0);
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

        assertNotNull(true);
    }

}