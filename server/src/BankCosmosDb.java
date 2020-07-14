import java.util.Map;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseProperties;
import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
import com.azure.cosmos.models.CosmosDatabaseResponse;
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
    private static final String CONTAINER_IDENTITY = "Identity";
    private static final String CONTAINER_ACCOUNTS = "Accounts";
    private CosmosClient client;
    private CosmosDatabase database;
    private CosmosContainer container;

    /*
     * Generic methods
     */

    // Database Create
    private void createDatabaseIfNotExists(final String databaseName) {

        System.err
                .println(this.getClass().getName() + ": Get or create database "
                        + databaseName + " if not exists...");

        // Create database if not exists
        final CosmosDatabaseResponse databaseResponse = client
                .createDatabaseIfNotExists(databaseName);
        database = client.getDatabase(databaseResponse.getProperties().getId());

    }

    // Database read
    private void readDatabaseById(final String databaseName) {

        System.err.println(
                this.getClass().getName() + ": Read database " + databaseName);

        // Read database by ID
        database = client.getDatabase(databaseName);
    }

    // Database read all
    private void readAllDatabases() {

        // Read all databases in the account
        final CosmosPagedIterable<CosmosDatabaseProperties> databases = client
                .readAllDatabases();

        // Print
        String msg = "Databases found in account: ";
        for (final CosmosDatabaseProperties dbProps : databases) {
            msg += dbProps.getId() + " ";
        }
        System.err.println(this.getClass().getName() + ": " + msg);
    }

    // Database delete
    private void deleteADatabase(final String databaseName) {
        System.err.println(this.getClass().getName()
                + ": Last step: delete database " + databaseName);

        // Delete database
        final CosmosDatabaseResponse dbResp = client.getDatabase(databaseName)
                .delete(new CosmosDatabaseRequestOptions());
        System.err.println(this.getClass().getName()
                + ": Status code for database delete: {}"
                + dbResp.getStatusCode());

    }

    // Container create
    private void createContainerIfNotExists(final String containerName,
            final String key) {
        System.err.println(
                this.getClass().getName() + ": Get or create container "
                        + containerName + " if not exists.");

        // Create container if not exists
        final CosmosContainerProperties containerProperties = new CosmosContainerProperties(
                containerName, key);

        // Provision throughput -- not needed
        // ThroughputProperties throughputProperties = ThroughputProperties
        // .createManualThroughput(400);

        // Create container with 200 RU/s
        final CosmosContainerResponse containerResponse = database
                .createContainerIfNotExists(containerProperties);
        container = database
                .getContainer(containerResponse.getProperties().getId());

    }

    // Container read all
    private void readAllContainers(final String databaseName) {

        readDatabaseById(databaseName);

        // Read all containers in the account
        final CosmosPagedIterable<CosmosContainerProperties> databases = database
                .readAllContainers();

        // Print
        String msg = "Containers found in database " + databaseName + ": ";
        for (final CosmosContainerProperties dbProps : databases) {
            msg += dbProps.getId() + " ";
        }
        System.err.println(this.getClass().getName() + ": " + msg);
    }

    /*
     * NewBank Load identities
     */
    public void loadIdentities() {

        System.err.println(this.getClass().getName()
                + ": Using Azure Cosmos DB endpoint: " + BankCosmosDb.HOST);

        // Create sync client
        client = new CosmosClientBuilder().endpoint(BankCosmosDb.HOST)
                .key(BankCosmosDb.MASTER_KEY)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .contentResponseOnWriteEnabled(true)
                .buildClient();

        readAllDatabases();
        createDatabaseIfNotExists(DATABASE_NAME);
        readAllContainers(DATABASE_NAME);
        createContainerIfNotExists(CONTAINER_IDENTITY, "/userID");
        createContainerIfNotExists(CONTAINER_ACCOUNTS, "/accountID");

        client.close();
    }

    /*
     * loadBankCustomers method
     * 
     * Gets the customers from the database
     * 
     * Starts with mockup
     *
     */
    Map<String, Customer> loadBankCustomers() {

        new Customer("Bhagy", "hi").addAccount("Main", 1000.0);
        new Customer("Christina", "lol").addAccount("Savings", 1500.0);
        new Customer("John", "nhoj").addAccount("Checking", 250.0);

        return Customer.getCustomers();
    }

    /*
     * loadBankAccounts method
     * 
     * Gets the accounts from the database for the customers
     * 
     * Starts with mockup
     *
     */
    void loadBankAccounts(Map<String, Customer> customers) {

        // nothing yet
    }

}