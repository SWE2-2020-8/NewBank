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

    private static Map<String, Customer> allCustomersMap = new HashMap<>();
    private String userName;
    private String password;
    private ArrayList<Account> accounts;

    /*
     * Constructor for a Customer object
     * 
     */
    public Customer(String userName, String password) {

        this.userName = userName;
        this.password = password;
        this.accounts = new ArrayList<>();
        Customer.allCustomersMap.put(userName, this);
    }

    /*
     * Method to create a new account for an existing user
     * 
     * 
     */
    public Account addAccount(String accountName, double openingBalance) {

        Account newAccount = new Account(this.getUserName(), accountName);
        newAccount.addTransaction(new Account.Transaction(openingBalance,
                openingBalance, "Account opening"));

        this.accounts.add(newAccount);
        return newAccount;
    }

    /*
     * Method to add an account for an existing user
     * 
     */
    public Account addAccount(Account account) {

        this.accounts.add(account);
        return account;
    }

    /*
     * Static method to get the if an userName already exists
     * 
     */
    public static boolean isCustomer(String userName) {

        return Customer.allCustomersMap.keySet().contains(userName);
    }

    /*
     * Static method to get the Customer object that corresponds to a userName
     * 
     */
    public static Customer getCustomer(String userName) {

        return Customer.allCustomersMap.get(userName);
    }

    /*
     * Get the username
     * 
     */
    public String getUserName() {
        return this.userName;
    }

    /*
     * Get the password
     * 
     */
    public String getPassword() {

        return this.password;
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
        return this.accounts;
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
    public static Map<String, Customer> getAllCustomersMap() {

        return Customer.allCustomersMap;
    }

    /*
     * Get the customer by
     */
    public static Customer getCustomerByUserName(String userName) {

        return Customer.allCustomersMap.get(userName);
    }

    /*
     * To change the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /*
     * toString()
     */
    @Override
    public String toString() {

        return "<" + this.userName + "/" + this.password + ">";
    }

    /*
     * Returns the accounts in string format
     */
    public String accountsToString() {

        StringBuilder s = new StringBuilder();

        for (Account a : this.accounts)
            s.append(a.toString() + "\n");
        return s.toString();
    }

}
