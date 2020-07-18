import java.util.Objects;
import java.util.stream.Collectors;

/**
 * NewBank class
 * 
 * Singleton pattern, just one. Class to manage the bank, loading the database,
 * indentifying customers, processing requests etc.
 * 
 */
public class NewBank {

    private static final NewBank bank = new NewBank(); // Creating the singleton

    /**
     * Private NewBank constructor to prevent instantiation of additional banks
     */
    private NewBank() {
    }

    /**
     * Serving the NewBank singleton
     * 
     * @return the singleton
     */
    public static NewBank getBank() {
        return bank;
    }

    /**
     * Method to get the Customer object if the user exists and the username and
     * password are correct
     * 
     * @param userName
     * @param password
     * @return the Customer
     */
    public Customer checkLogInDetails(String userName, String password) {

        Customer foundUser = Customer.getCustomerByName(userName);
        return Objects.nonNull(foundUser) && foundUser.isPasswordOK(password)
                ? foundUser
                : null;
    }

    /*
     * commands from the NewBank customer are processed in this method
     */
    public synchronized String processRequest(Customer customer,
            String request) {

        String[] words = request.split(" ");
        String command = words[0];
        String param1 = "";
        if (words.length > 1)
            param1 = words[1];
        String param2 = "";
        if (words.length > 2)
            param2 = words[2];
        String param3 = "";
        if (words.length > 3)
            param3 = words[3];
            

        switch (command) {

        case "OPTIONS": // so customer can navigate through functions eaisly
            return options(customer);

        case "SHOWMYACCOUNTS":
            return showMyAccounts(customer);

        case "ADDACCOUNT":
            return addAccount(customer, param1);

        case "DEPOSIT":
            return deposit(customer, param1, param2);

        case "MOVE":
            // to do
            return NewBank.FAIL;

        case "PAY":
            // to do
            return NewBank.FAIL;

        case "CHANGEPASSWORD":
            return changePassword(customer, param1, param2);

        case "ADDUSER":
            return addUser(customer, param1, param2);

        case "LISTUSERS":
            return listUsers(customer);

        case "LISTACCOUNTS":
            return listAccounts(customer);

        default:
            printTrace(customer, "Invalid input (try OPTIONS)");
            return NewBank.FAIL;
        }

    }

    /*
     * Capabilities follow, just create the method below
     *
     * Use the constants to return success and fail to avoid typos
     * 
     * Changes in the database happen here using the following methods:
     * BankCosmosDb .createCustomerDocument,
     * BankCosmosDb.replaceCustomerDocument, BankCosmosDb.createAccountDocument
     * & BankCosmosDb.replaceAccountDocument
     * 
     * Use printTrace to output a message in the console for traceability
     * 
     * 
     */

    static final String SUCCESS = "SUCCESS";
    static final String FAIL = "FAIL";

    /*
     * List the options
     * 
     */
    private String options(Customer customer) {

        printTrace(customer, "Options listed");
        String s;
        s = "Options avilable are:\n";
        s += "SHOWMYACCOUNTS : to view all Accounts under your name.\n";
        s += "ADDACCOUNT <Account Name> : to create new account \n";
        s += "DEPOSIT <Account Name> <Amount> : deposit money\n";
        s += "MOVE <Amount> <From Account> <To Account> : to move money\n";
        s += "PAY <UserID> <Amount> : to transfer money\n";
        s += "CHANGEPASSWORD <Old Password> <New Password> : change password\n";
        s += "ADDUSER <UserID> <Password> : create user (only admin)\n";
        s += "LISTUSERS : list users (only admin)\n";
        s += "LISTACCOUNTS : list all accounts (only admin)\n";
        return s + NewBank.SUCCESS;
    }

    /*
     * Get the user accounts for a customer
     * 
     */
    private String showMyAccounts(Customer customer) {

        printTrace(customer, "Accounts listed");
        return customer.accountsToString() + NewBank.SUCCESS;
    }

    /*
     * Open a new account for a customer
     * 
     */
    private String addAccount(Customer customer, String accountName) {

        if (accountName.length() > 3
                && !customer.hasAccountByName(accountName)) {

            BankCosmosDb.createAccountDocument(
                    customer.addAccount(accountName, 0.0));
            printTrace(customer, "Account added " + accountName);
            return NewBank.SUCCESS;

        } else {
            printTrace(customer, "Account not added");
            return NewBank.FAIL;
        }
    }

    /*
     * Deposit money into an account
     * 
     */
    private String deposit(Customer customer, String accountName,
            String amountString) {

        Double amount = 0.0;
        try {
            amount = Double.parseDouble(amountString);
        } catch (Exception e) {
            return NewBank.FAIL;
        }

        if (customer.hasAccountByName(accountName) && amount > 0) {

            Account account = customer.getAccountByName(accountName);

            // The transaction itself
            account.newTransaction(amount, "Deposit");

            BankCosmosDb.replaceAccountDocument(account);
            return NewBank.SUCCESS;

        } else {
            printTrace(customer, "Issue with the deposit");
            return NewBank.FAIL;
        }
    }

    /*
     * Change user's password
     * 
     */
    private String changePassword(Customer customer, String oldPass,
            String newPass) {

        if (customer.getPassword().equals(oldPass)) {

            customer.setPassword(newPass);
            BankCosmosDb.replaceCustomerDocument(customer);
            printTrace(customer, "Password changed");
            return NewBank.SUCCESS;

        } else {
            printTrace(customer, "Password not changed");
            return NewBank.FAIL;
        }
    }

    /*
     * Add a new user
     * 
     */
    private String addUser(Customer customer, String userName,
            String password) {

        if (userName.length() > 3 && password.length() > 3
                && customer.getUserName().equals("Admin")
                && !Customer.isCustomer(userName)) {

            BankCosmosDb
                    .createCustomerDocument(new Customer(userName, password));

            printTrace(customer, "User added " + userName);
            return NewBank.SUCCESS;

        } else {
            printTrace(customer, "Account not added");
            return NewBank.FAIL;
        }
    }

    /*
     * List all users (only Admin)
     * 
     */
    private String listUsers(Customer customer) {

        if (customer.getUserName().equals("Admin")) {

            printTrace(customer, "Users listed");
            return Customer.getAllCustomersList()
                    .stream()
                    .map(Customer::toString)
                    .collect(Collectors.joining("\n", "", "\n"))
                    + NewBank.SUCCESS;

        } else {
            printTrace(customer, "Users not listed");
            return NewBank.FAIL;
        }
    }

    /*
     * List all accounts (only Admin)
     * 
     */
    private String listAccounts(Customer customer) {

        if (customer.getUserName().equals("Admin")) {

            printTrace(customer, "Accounts listed");
            return Customer.getAllCustomersList()
                    .stream()
                    .flatMap(Customer::getAccountStream)
                    .map(Account::toString)
                    .collect(Collectors.joining("\n", "", "\n"))
                    + NewBank.SUCCESS;

        } else {
            printTrace(customer, "Accounts not listed");
            return NewBank.FAIL;
        }
    }

    /*
     * Auxiliary trace method to print in the console to correct errors
     */
    private void printTrace(Customer customer, String message) {

        System.err.println(this.getClass().getName() + ": "
                + customer.getUserName() + " " + message);
    }

}