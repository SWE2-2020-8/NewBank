import java.util.ArrayList;
import java.util.Map;
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
    private Map<String, Customer> customers;

    /**
     * Private NewBank constructor
     * 
     * Loads the bank customers
     */
    private NewBank() {
        BankCosmosDb newBankDatabase = new BankCosmosDb();
        newBankDatabase.loadBankCustomers();
        this.customers = Customer.getCustomers();
        newBankDatabase.loadBankAccounts();
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

    /**
     * Get the user accounts for a customer
     * 
     * @param customer
     * @return
     */
    private String showMyAccounts(Customer customer) {
        return customer.accountsToString();
    }

    /*
     * commands from the NewBank customer are processed in this method
     */
    public synchronized String processRequest(Customer customer,
            String request) {

        String[] words = request.split(" ");

        System.err.println(this.getClass().getName() + ": "
                + customer.getUserName() + " REQUESTS \"" + request + "\"");

        switch (words[0]) {

        case "OPTIONS": // so customer can navigate through functions eaisly

            ArrayList<String> option = new ArrayList<>();
            option.add("Options avilable are:" + "\n");
            option.add("SHOWMYACCOUNTS : to view all Accounts under your name."
                    + "\n");
            option.add(
                    "NEWACCOUNT <Name> : to create new account e.g. creating a savings account."
                            + "\n");
            option.add(
                    "MOVE <Amount> <From> <To> : to move money from account to another account."
                            + "\n");
            option.add(
                    "PAY <Person/Company> <Amount> : to transfer money to others."
                            + "\n");

            String x = "";

            for (String options : option) {
                x = option + "\n\n";
            }
            return x;

        case "SHOWMYACCOUNTS":
            return showMyAccounts(customer);

        case "NEWACCOUNT": // created this so customer can call new account
            if (words.length == 2) {
                customer.addAccount(words[1], 0.0);

                System.err.println(this.getClass().getName() + ": "
                        + customer.getUserName() + " ADDED ACCOUNT "
                        + words[1]);

                return "SUCCESS";

            } else {

                System.err.println(this.getClass().getName() + ": "
                        + customer.getUserName() + " FAILED REQUEST \""
                        + request + "\"");

                return "FAIL";
            }

        default:
            System.err.println(this.getClass().getName() + ": "
                    + customer.getUserName() + " ILLEGAL REQUEST");
            return "FAIL";
        }

    }

}