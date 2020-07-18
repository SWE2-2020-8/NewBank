import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
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

        List<CustomerRecord> retrieved = BankCosmosDb.getContainerIdentity()
                .queryItems("SELECT * FROM c", new CosmosQueryRequestOptions(),
                        CustomerRecord.class)
                .stream()
                .collect(Collectors.toList());

        retrieved.forEach(
                cr -> System.err.println(cr.getId() + "/" + cr.getPassword()));

        assertTrue(retrieved.size() > 0);
    }

    @Ignore("Only used to reset the user database, do not use!")
    @Test
    public void resetBankCustomers() {

        BankCosmosDb.deleteDatabase();
        BankCosmosDb.retrieveOrCreateDatabase();
        BankCosmosDb.retrieveOrCreateContainerIdentity();
        BankCosmosDb.retrieveOrCreateContainerAccounts();

        BankCosmosDb.createCustomerDocument(new Customer("Admin", "1234"));
        BankCosmosDb.createCustomerDocument(new Customer("Bhagy", "hi"));
        BankCosmosDb.createCustomerDocument(new Customer("Christina", "lol"));
        BankCosmosDb.createCustomerDocument(new Customer("John", "nhoj"));

        assertTrue(Customer.isCustomer("Admin"));
        assertTrue(Customer.isCustomer("Bhagy"));
        assertTrue(Customer.isCustomer("Christina"));
        assertTrue(Customer.isCustomer("John"));
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

    @Test
    public void addRandomAccountToRandomCustomerTest() {

        Random random = new Random();

        BankCosmosDb.loadBankCustomers();
        Object[] arrayCust = Customer.getAllCustomersList().toArray();
        Customer chosenCustomer = (Customer) arrayCust[random
                .nextInt(arrayCust.length)];
        System.err.println("Found " + chosenCustomer.getUserName());

        Account newAccount = chosenCustomer.addAccount(
                "Testaccount" + random.nextInt(99), +random.nextInt(9999));

        BankCosmosDb.createAccountDocument(newAccount);

        assertTrue(chosenCustomer.getAccounts().contains(newAccount));
    }

    @Test
    public void addRandomTransactionToRandomAccountToRandomCustomerTest() {

        Random random = new Random();
        BankCosmosDb.loadBankCustomers();
        BankCosmosDb.loadBankAccounts();

        // get a random customer
        Object[] arrayCust = Customer.getAllCustomersList().toArray();
        Customer chosenCustomer = (Customer) arrayCust[random
                .nextInt(arrayCust.length)];
        System.err.println("Found " + chosenCustomer.getUserName());

        // get a random account
        Object[] arrayAcc = chosenCustomer.getAccounts().toArray();
        if (arrayAcc.length == 0)
            System.err.println(
                    "Sorry, just selected a user with no accounts, test screwed");
        Account chosenAccount = (Account) arrayAcc[random
                .nextInt(arrayAcc.length)];
        System.err.println("Found " + chosenAccount.getAccountName());

        // make a new transaction
        Double amount = (double) Math.round(random.nextDouble() * 1000);
        chosenAccount.newTransaction(amount,
                "Random transaction " + Double.toString(amount));

        BankCosmosDb.replaceAccountDocument(chosenAccount);

        assertTrue(chosenAccount.getTransactions()
                .stream()
                .anyMatch(transaction -> transaction.getAmount() == amount));
    }

    @Test
    public void loadUsersAndAccountsTest() {

        BankCosmosDb.loadBankCustomers();
        BankCosmosDb.loadBankAccounts();

        // Assumes there's a customer named Christina and that she has accounts
        assertNotNull(Customer.getCustomerByName("Christina"));
        assertNotNull(Customer.getCustomerByName("Christina").getAccounts());
    }
}