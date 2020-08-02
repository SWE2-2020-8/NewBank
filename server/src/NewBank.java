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

    // Creating the singleton
    private static final NewBank bank = new NewBank();

    // Admin username
    private static final String ADMIN = "Admin";

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
    public Customer checkLogInDetails(final String userName,
            final String password) {

        final Customer foundUser = Customer.getCustomerByName(userName);
        return Objects.nonNull(foundUser) && foundUser.isPasswordOK(password)
                ? foundUser
                : null;
    }

    /*
     * commands from the NewBank customer are processed in this method
     */
    public synchronized String processRequest(final Customer customer,
            final String request) {

        final String[] words = request.split(" ");
        final String command = words[0];
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
            return move(customer, param1, param2, param3);

        case "PAY":
            return pay(customer, param1, param2, param3);

        case "CHANGEPASSWORD":
            return changePassword(customer, param1, param2);

        case "ADDUSER":
            return addUser(customer, param1, param2);

        case "LISTUSERS":
            return listUsers(customer);

        case "LISTACCOUNTS":
            return listAccounts(customer);

        case "WITHDRAW":
            return withdraw(customer, param1, param2);

        default:
            printTrace(customer, "Invalid input");
            return "// Invalid input (try OPTIONS)\n" + NewBank.FAIL;
        }
    }

    /*
     * Capabilities follow, just create the method below
     *
     * Use the constants to return success and fail to avoid typos
     * 
     * Changes in the database happen here using the following methods:
     * BankCosmosDb.createCustomerDocument,
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
    private String options(final Customer customer) {

        printTrace(customer, "Options listed");
        String s;
        s = "// Options available are:\n";
        s += "// SHOWMYACCOUNTS : to view all Accounts under your name.\n";
        s += "// ADDACCOUNT <Account Name> : to create new account \n";
        s += "// DEPOSIT <Account Name> <Amount> : deposit money\n";
        s += "// MOVE <Amount> <From Account> <To Account> : to move money\n";
        s += "// PAY <Amount> <From Account> <To User> : to pay money\n";
        s += "// WITHDRAW <Account Name> <Amount> : to withdraw money\n";
        s += "// CHANGEPASSWORD <Old Password> <New Password> : to change password\n";
        s += "// ADDUSER <UserID> <Password> : to create user (only admin)\n";
        s += "// LISTUSERS : to list all users (only admin can see passwords)\n";
        s += "// LISTACCOUNTS : to list all accounts (only admin)\n";
        return s + NewBank.SUCCESS;
    }

    /*
     * Get the user accounts for a customer
     * 
     */
    private String showMyAccounts(final Customer customer) {

        printTrace(customer, "Accounts listed");
        return customer.accountsToString() + NewBank.SUCCESS;
    }

    /*
     * Open a new account for a customer
     * 
     */
    private String addAccount(final Customer customer,
            final String accountName) {

        if (isAccountNameValid(accountName)
                && !customer.hasAccountByName(accountName)) {

            final Account newAccount = customer.addAccount(accountName, 0.0);
            BankCosmosDb.createAccountDocument(newAccount);

            printTrace(customer, "Account added");
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
    private String deposit(final Customer customer, final String accountName,
            final String amountString) {

        if (customer.hasAccountByName(accountName)
                && isValidNumber(amountString)) {

            final Account account = customer.getAccountByName(accountName);
            account.newTransaction(toNumber(amountString), "Deposit");
            BankCosmosDb.replaceAccountDocument(account);

            printTrace(customer, "Deposit added");
            return NewBank.SUCCESS;

        } else {

            printTrace(customer, "Issue with the deposit");
            return NewBank.FAIL;
        }
    }

    /*
     * Withdraw money from an account
     * 
     */
    private String withdraw(final Customer customer, final String accountName,
            final String amountString) {

        if (customer.hasAccountByName(accountName)
                && isValidNumber(amountString)
                && toNumber(amountString) <= customer
                        .getAccountByName(accountName)
                        .getBalance()) {

            final Account account = customer.getAccountByName(accountName);
            account.newTransaction(-toNumber(amountString), "Withdrawal");
            BankCosmosDb.replaceAccountDocument(account);

            printTrace(customer, "Withdrawal");
            return NewBank.SUCCESS;

        } else {
            printTrace(customer, "Issue with the withdrawal");
            return NewBank.FAIL;
        }
    }

    /*
     * Move money from an account to a different account
     * 
     */
    private String move(final Customer customer, final String amountString,
            final String accountNameOrigin,
            final String accountNameDestination) {

        if (customer.hasAccountByName(accountNameOrigin)
                && customer.hasAccountByName(accountNameDestination)
                && isValidNumber(amountString)
                && toNumber(amountString) <= customer
                        .getAccountByName(accountNameOrigin)
                        .getBalance()) {

            final Account accountOrigin = customer
                    .getAccountByName(accountNameOrigin);
            final Account accountDestination = customer
                    .getAccountByName(accountNameDestination);
            final Double amount = toNumber(amountString);

            accountOrigin.newTransaction(-amount,
                    "Amount moved to account " + accountNameDestination);
            accountDestination.newTransaction(+amount,
                    "Amount moved from account " + accountNameOrigin);
            BankCosmosDb.replaceAccountDocument(accountOrigin);
            BankCosmosDb.replaceAccountDocument(accountDestination);

            printTrace(customer, "Moved money");
            return NewBank.SUCCESS;

        } else {
            printTrace(customer, "Issue with move");
            return NewBank.FAIL;
        }
    }

    /*
     * Pay another client
     * 
     * (From selected account to first account)
     * 
     */
    private String pay(final Customer customer, final String amountString,
            final String originAccountName, final String receiverCustomerName) {

        if (Customer.isCustomer(receiverCustomerName)
                && Customer.getCustomerByName(receiverCustomerName).hasAccount()
                && customer.hasAccountByName(originAccountName)
                && isValidNumber(amountString)
                && toNumber(amountString) <= customer
                        .getAccountByName(originAccountName)
                        .getBalance()) {

            final Account senderAccount = customer
                    .getAccountByName(originAccountName);
            final Account receiverAccount = Customer
                    .getCustomerByName(receiverCustomerName)
                    .getFirstAccount();
            Double amount = toNumber(amountString);

            senderAccount.newTransaction(-amount,
                    "Paid to " + receiverCustomerName);
            receiverAccount.newTransaction(+amount,
                    "Transfer received from " + customer.getUserName());

            BankCosmosDb.replaceAccountDocument(senderAccount);
            BankCosmosDb.replaceAccountDocument(receiverAccount);

            printTrace(customer, "Paid user");
            return NewBank.SUCCESS;

        } else {
            printTrace(customer, "Issue with payment");
            return NewBank.FAIL;
        }
    }

    /*
     * Change user's password
     * 
     */
    private String changePassword(final Customer customer, final String oldPass,
            final String newPass) {

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
    private String addUser(final Customer customer, final String userName,
            final String password) {

        if (isUserNameValid(userName) && password.length() > 3
                && customer.getUserName().equals(ADMIN)
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
     * List all users
     * 
     * (only Admin in client, but allowing the rest of the users to see the
     * usernames so that they can see a transfer list)
     * 
     */
    private String listUsers(final Customer customer) {

        if (customer.getUserName().equals(ADMIN)) {

            printTrace(customer, "Users listed with passwords");
            return Customer.getAllCustomersList()
                    .stream()
                    .map(Customer::toString)
                    .collect(Collectors.joining("\n", "", "\n"))
                    + NewBank.SUCCESS;

        } else {

            printTrace(customer, "Users listed without password");
            return Customer.getAllCustomersList()
                    .stream()
                    .map(Customer::toStringNoPassword)
                    .collect(Collectors.joining("\n", "", "\n"))
                    + NewBank.SUCCESS;
        }
    }

    /*
     * List all accounts (only Admin)
     * 
     */
    private String listAccounts(final Customer customer) {

        if (customer.getUserName().equals(ADMIN)) {

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
    private void printTrace(final Customer customer, final String message) {

        System.err.println(this.getClass().getName() + ": "
                + customer.getUserName() + " " + message);
    }

    /*
     * Auxiliary method to check quantity validity
     */
    private boolean isValidNumber(final String amountString) {

        return !toNumber(amountString).isNaN();
    }

    /*
     * Auxiliary method to get quantity as double from string
     */
    private Double toNumber(final String amountString) {

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (final Exception e) {
            return Double.NaN;
        }
        return amount;
    }

    /*
     * Auxiliary method to check username validity
     */
    private boolean isUserNameValid(final String str) {
        return str.matches("^[a-zA-Z0-9]{4,}$");
    }

    /*
     * Auxiliary method to check account validity
     */
    private boolean isAccountNameValid(final String str) {
        return str.matches("^[a-zA-Z0-9\\.\\,\\-\\+ _]{4,}$");
    }

    /*
     * Auxiliary method to check password validity
     */
    // private boolean isPasswordValid(String str) {
    // return
    // str.matches("^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[!#$%&'()*+,-./:;<=>?@[]^_`{|}~])[a-zA-Z0-9!#$%&'()*+,-./:;<=>?@[]^_`{|}~]{8,}$");
    // }
}