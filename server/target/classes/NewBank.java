import java.util.Objects;

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

        Customer foundUser = Customer.getCustomer(userName);
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
            param2 = words[3];

        switch (command) {

        case "OPTIONS": // so customer can navigate through functions eaisly
            return options(customer);

        case "SHOWMYACCOUNTS":
            return showMyAccounts(customer);

        case "NEWACCOUNT": // created this so customer can call new account
            return newAccount(customer, param1);

        case "MOVE":
            return NewBank.FAIL;

        case "PAY":
            return NewBank.FAIL;

        case "CHANGEPASSWORD":
            return changePassword(customer, param1, param2);

        case "ADDUSER": // created this so customer can call new account
            return addUser(customer, param1, param2);

        case "LISTUSERS": // created this so customer can call new account
            return listUsers(customer);

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
     * Use printTrace to output a message in the console for traceability
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
        s += "NEWACCOUNT <Account Name> : to create new account \n";
        s += "MOVE <Amount> <From Account> <To Account> : to move money\n";
        s += "PAY <UserID> <Amount> : to transfer money\n";
        s += "CHANGEPASSWORD <Old Password> <New Password> : change password\n";
        s += "ADDUSER <UserID> <Password> : create user (only admin)\n";
        s += "LISTUSERS : list users (only admin)\n";
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
    private String newAccount(Customer customer, String accountName) {

        if (accountName.length() > 3) {

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
                && !Customer.getAllCustomersMap().containsKey(userName)) {

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
     * List users
     * 
     */
    private String listUsers(Customer customer) {

        if (customer.getUserName().equals("Admin")) {

            StringBuilder sb = new StringBuilder();
            Customer.getAllCustomersMap()
                    .entrySet()
                    .forEach(es -> sb.append(es.getKey() + "\t"
                            + es.getValue().getPassword() + "\n"));
            printTrace(customer, "Users listed");
            return sb.toString() + NewBank.SUCCESS;

        } else {
            printTrace(customer, "Users not listed");
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