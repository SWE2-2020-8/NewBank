
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Customer class
 * 
 * Keeps track of all the customers and their information
 */
public class Customer {

    private static int nextCustomerId = 1;
    private static Map<String, Customer> customers = new HashMap<>();

    private Integer customerID;
    private String userName;
    private String password;
    private ArrayList<Account> accounts;

    /**
     * Constructor for a Customer object
     * 
     * @param userName
     * @param password
     */
    public Customer(String userName, String password) {

        this.customerID = nextCustomerId++;
        this.userName = userName;
        this.password = password;
        this.accounts = new ArrayList<>();
        Customer.customers.put(userName, this);

    }

    /**
     * Method to create an account to an existing user
     * 
     * @param accountName
     * @param openingBalance
     */
    public void addAccount(String accountName, double openingBalance) {
        this.accounts.add(new Account(accountName, openingBalance));
    }

    /**
     * Static method to get the Customer object that corresponds to a userName
     * 
     * @param userName
     * @return
     */
    public static Customer getCustomer(String userName) {
        return customers.get(userName);
    }

    /*
     * Get the username
     * 
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Method to check if password is ok
     * 
     * @param password
     */
    boolean isPasswordOK(String password) {
        return this.password.equals(password);
    }

    /**
     * Get the accounts from the user
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /*
     * Add an account to a user
     */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    /*
     * Add list of accounts to a user
     */
    public void addAccounts(List<Account> accounts) {
        this.accounts.addAll(accounts);
    }

    /*
     * Get the full customer list
     */
    public static Map<String, Customer> getCustomers() {
        return customers;
    }

    /*
     * To change the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /*
     * Returns the accounts in string format
     */
    public String accountsToString() {

        StringBuilder s = new StringBuilder();

        for (Account a : this.accounts)
            s.append(a.toString() + " ");

        return s.toString();
    }

}
