import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class BankTests {

    @Test
    public void authenticateTest() {

        NewBank bank = NewBank.getBank();
        assertNotNull(bank.checkLogInDetails("Christina", "lol"));
    }

    @Test
    public void loadIdentitiesTest() {

        BankCosmosDb testDb = new BankCosmosDb();
        testDb.loadIdentities();

        assertNotNull(true);
    }

}