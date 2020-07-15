import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosDatabaseProperties;
import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosItemRequestOptions;
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

    static final String MASTER_KEY = "gUdB3NYKNIa93PYdoEmsEQZvs3Vy0N7bXnf6WY2Ob0FSGKqpRCbz6WQfFx7BbbZiIp23kv7d5GtYY0dwUCaFEQ==";
    static final String HOST = "https://swe2-2020-8.documents.azure.com:443/";

    static final String DATABASE_NAME = "newBank";
    static final String CONTAINER_IDENTITY = "Identity";
    static final String CONTAINER_ACCOUNTS = "Accounts";
    private CosmosClient client;
    private CosmosDatabase database;
    private CosmosContainer container;

    /*
     * Constructor
     */
    public BankCosmosDb() {

        this.client = new CosmosClientBuilder().endpoint(BankCosmosDb.HOST)
                .key(BankCosmosDb.MASTER_KEY)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
    }

    /*
     * Generic methods
     */

    // Database getter
    CosmosDatabase getDatabase() {

        return this.database;
    }

    // Database retrieve
    void retrieveDatabase(final String databaseName) {

        this.database = this.client.getDatabase(databaseName);
    }

    // Database retrieve or create
    void retrieveOrCreateDatabase(final String databaseName) {

        final CosmosDatabaseResponse databaseResponse = client
                .createDatabaseIfNotExists(databaseName);
        retrieveDatabase(databaseResponse.getProperties().getId());
    }

    // Database retrieve all
    CosmosPagedIterable<CosmosDatabaseProperties> retrieveAllDatabases() {

        return this.client.readAllDatabases();
    }

    // Database delete
    void deleteDatabase(final String databaseName) {

        final CosmosDatabaseResponse dbResp = client.getDatabase(databaseName)
                .delete(new CosmosDatabaseRequestOptions());
        System.err.println(this.getClass().getName()
                + ": Status code for database delete: {}"
                + dbResp.getStatusCode());
    }

    // Container getter
    CosmosContainer getContainer() {

        return this.container;
    }

    // Container retrieve
    void retrieveContainer(final String containerName) {

        this.container = this.database.getContainer(containerName);
    }

    // Container retrieve or create
    void retrieveOrCreateContainer(final String containerName,
            final String key) {

        this.database.createContainerIfNotExists(
                new CosmosContainerProperties(containerName, key));
        retrieveContainer(containerName);
    }

    // Create a customer document in container
    private void createDocument(Customer customer) {

        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setUserName(customer.getUserName());
        customerRecord.setPassword(customer.getPassword());

        container.createItem(customerRecord,
                new PartitionKey(customerRecord.getUserName()),
                new CosmosItemRequestOptions());
    }

    // Container read all
    CosmosPagedIterable<CosmosContainerProperties> retrieveAllContainers() {

        return this.database.readAllContainers();
    }

    /*
     * loadBankCustomers method
     * 
     * Gets the customers from the database
     * 
     * Starts with mockup
     *
     */
    void loadBankCustomers() {

        retrieveOrCreateDatabase(BankCosmosDb.DATABASE_NAME);
        retrieveOrCreateContainer(BankCosmosDb.CONTAINER_ACCOUNTS, "/userName");

        if (!Customer.isCustomer("Bhagy"))
            this.createDocument(
                    new Customer("Bhagy", "hi").addAccount("Main", 1000.0));

        if (!Customer.isCustomer("Christina"))
            this.createDocument(new Customer("Christina", "lol")
                    .addAccount("Savings", 1500.0));

        if (!Customer.isCustomer("John"))
            this.createDocument(
                    new Customer("John", "nhoj").addAccount("Checking", 250.0));

    }

    /*
     * loadBankAccounts method
     * 
     * Gets the accounts from the database for the customers
     * 
     * Starts with mockup
     *
     */
    void loadBankAccounts() {

        // nothing yet
    }

}