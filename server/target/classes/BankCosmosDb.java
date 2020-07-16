import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

public class BankCosmosDb {

    /*
     * Get keys from environment to avoid hardcoding
     * 
     * temporarily hardcoded though
     */
    // public static String MASTER_KEY = System.getProperty("ACCOUNT_KEY",
    // StringUtils.defaultString(
    // StringUtils.trimToNull(System.getenv().get("ACCOUNT_KEY")),
    // "gUdB3NYKNIa93PYdoEmsEQZvs3Vy0N7bXnf6WY2Ob0FSGKqpRCbz6WQfFx7BbbZiIp23kv7d5GtYY0dwUCaFEQ=="));

    // public static String HOST = System.getProperty("ACCOUNT_HOST",
    // StringUtils.defaultString(
    // StringUtils.trimToNull(System.getenv().get("ACCOUNT_HOST")),
    // "https://swe2-2020-8.documents.azure.com:443/"));

    private static final String MASTER_KEY = "gUdB3NYKNIa93PYdoEmsEQZvs3Vy0N7bXnf6WY2Ob0FSGKqpRCbz6WQfFx7BbbZiIp23kv7d5GtYY0dwUCaFEQ==";
    private static final String HOST = "https://swe2-2020-8.documents.azure.com:443/";
    private static final String DATABASE_NAME = "newBank";
    static final String CONTAINER_IDENTITY = "Identity";
    static final String CONTAINER_ACCOUNTS = "Accounts";
    private static CosmosClient client;
    private static CosmosDatabase database;
    private static CosmosContainer containerIdentity;
    private static CosmosContainer containerAccounts;

    /*
     * Initialisation
     */
    static {

        BankCosmosDb.client = new CosmosClientBuilder()
                .endpoint(BankCosmosDb.HOST)
                .key(BankCosmosDb.MASTER_KEY)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        printTrace("Init OK " + BankCosmosDb.client.toString());

        BankCosmosDb.database = BankCosmosDb.client
                .getDatabase(BankCosmosDb.DATABASE_NAME);
        printTrace("Init OK " + BankCosmosDb.database.getId());

        BankCosmosDb.containerIdentity = BankCosmosDb.database
                .getContainer(BankCosmosDb.CONTAINER_IDENTITY);
        printTrace("Init OK " + BankCosmosDb.containerIdentity.getId());

        BankCosmosDb.containerAccounts = BankCosmosDb.database
                .getContainer(BankCosmosDb.CONTAINER_ACCOUNTS);
        printTrace("Init OK " + BankCosmosDb.containerAccounts.getId());

    }

    // Hidden constructor
    private BankCosmosDb() {
    }

    // Container identity getter
    public static CosmosContainer getContainerIdentity() {
        printTrace("Identity container given away");
        return containerIdentity;
    }

    // Container account getter
    public static CosmosContainer getContainerAccounts() {
        printTrace("Account container given away");
        return containerAccounts;
    }

    // Database retrieve or create
    public static void retrieveOrCreateDatabase() {

        BankCosmosDb.client
                .createDatabaseIfNotExists(BankCosmosDb.DATABASE_NAME);
        BankCosmosDb.database = BankCosmosDb.client
                .getDatabase(BankCosmosDb.DATABASE_NAME);
    }

    // Database delete
    public static void deleteDatabase() {

        final CosmosDatabaseResponse dbResp = client
                .getDatabase(BankCosmosDb.DATABASE_NAME)
                .delete(new CosmosDatabaseRequestOptions());

        System.err.println(BankCosmosDb.class.getName()
                + ": Status code for database delete: "
                + dbResp.getStatusCode());
    }

    // Container retrieve or create
    public static void retrieveOrCreateContainerIdentity() {

        BankCosmosDb.database.createContainerIfNotExists(
                new CosmosContainerProperties(BankCosmosDb.CONTAINER_IDENTITY,
                        "/id"));
        BankCosmosDb.containerIdentity = BankCosmosDb.database
                .getContainer(BankCosmosDb.CONTAINER_IDENTITY);
    }

    public static void retrieveOrCreateContainerAccounts() {

        BankCosmosDb.database.createContainerIfNotExists(
                new CosmosContainerProperties(BankCosmosDb.CONTAINER_ACCOUNTS,
                        "/id"));
        BankCosmosDb.containerAccounts = BankCosmosDb.database
                .getContainer(BankCosmosDb.CONTAINER_ACCOUNTS);
    }

    // Container read all
    public static CosmosPagedIterable<CosmosContainerProperties> retrieveAllContainers() {

        return BankCosmosDb.database.readAllContainers();
    }

    // Create a customer document in container
    public static void createCustomerDocument(final Customer customer) {

        final CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setId(customer.getUserName());
        customerRecord.setPassword(customer.getPassword());

        BankCosmosDb.containerIdentity.createItem(customerRecord);
        printTrace("Created identity " + customerRecord);
    }

    // Replace a customer document in container
    public static void replaceCustomerDocument(final Customer customer) {

        final CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setId(customer.getUserName());
        customerRecord.setPassword(customer.getPassword());

        CosmosItemResponse<CustomerRecord> customerResponse = containerIdentity
                .replaceItem(customerRecord, customerRecord.getId(),
                        new PartitionKey(customerRecord.getId()),
                        new CosmosItemRequestOptions());

        printTrace("Identity change response code: "
                + customerResponse.getStatusCode());
    }

    // Create an account document in container
    public static void createAccountDocument(final Account account) {

        final AccountRecord accountRecord = new AccountRecord();
        accountRecord.setId(account.getAccountId());
        accountRecord.setAccountName(account.getAccountName());
        accountRecord.setUserName(account.getUserName());
        accountRecord.setBalance(account.getBalance());

        List<TransactionRecord> transactionRecordList = new ArrayList<>();
        account.getTransactions().forEach(transaction -> {

            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setDate(transaction.date);
            transactionRecord.setAmount(transaction.amount);
            transactionRecord.setBalance(transaction.balance);
            transactionRecord.setDate(transaction.date);
            transactionRecordList.add(transactionRecord);
        });

        accountRecord.setTransactions(transactionRecordList
                .toArray(new TransactionRecord[transactionRecordList.size()]));

        BankCosmosDb.containerAccounts.createItem(accountRecord);
        printTrace("Created account " + accountRecord);
    }

    /*
     * loadBankCustomers method
     * 
     * Gets the customers from the database
     * 
     * If the database is damaged, the test method resetBankCustomers() (by
     * default skipped) will create a default set of users and passwords
     *
     */
    public static void loadBankCustomers() {

        final List<CustomerRecord> retrieved = BankCosmosDb
                .getContainerIdentity()
                .queryItems("SELECT * FROM c", new CosmosQueryRequestOptions(),
                        CustomerRecord.class)
                .stream()
                .collect(Collectors.toList());

        retrieved.forEach(customerRecord -> new Customer(customerRecord.getId(),
                customerRecord.getPassword()));

        retrieved.forEach(cr -> printTrace(
                "Retrieved <" + cr.getId() + "/" + cr.getPassword() + ">"));
    }

    /*
     * loadBankAccounts method
     * 
     * Gets the accounts from the database for the customers
     * 
     * Starts with mockup
     *
     */
    public static void loadBankAccounts() {

        final List<AccountRecord> retrieved = BankCosmosDb
                .getContainerAccounts()
                .queryItems("SELECT * FROM c", new CosmosQueryRequestOptions(),
                        AccountRecord.class)
                .stream()
                .collect(Collectors.toList());

        retrieved.forEach(accountRecord -> {

            Customer owner = Customer
                    .getCustomerByUserName(accountRecord.getUserName());

            Account account = new Account(accountRecord.getUserName(),
                    accountRecord.getAccountName());

            Arrays.stream(accountRecord.getTransactions())
                    .forEach(transaction -> account.addTransaction(
                            new Account.Transaction(transaction.getAmount(),
                                    transaction.getBalance(),
                                    transaction.getDescription(),
                                    transaction.getDate())));

            owner.addAccount(account);
            printTrace("Retrieved <" + account.getUserName() + "/"
                    + account.getAccountName() + ">");
        });
    }

    /*
     * Auxiliary trace method to print in the console to correct errors
     */
    private static void printTrace(final String message) {

        System.err.println(BankCosmosDb.class.getName() + ": " + message);
    }

}