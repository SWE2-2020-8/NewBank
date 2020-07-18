import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Customer class
 * 
 * Keeps track of all the customers and their information
 */
public class Customer {

    private static List<Customer> allCustomers = new ArrayList<>();
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
        Customer.allCustomers.add(this);
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
     * Static method the full customer list
     * 
     */
    public static List<Customer> getAllCustomersList() {

        return Customer.allCustomers;
    }

    /*
     * Static method to get the if an userName already exists
     * 
     */
    public static boolean isCustomer(String userName) {

        return Customer.allCustomers.stream()
                .anyMatch(customer -> customer.userName.equals(userName));
    }

    /*
     * Static method to get the Customer object that corresponds to a userName
     * 
     */
    public static Customer getCustomerByName(String userName) {

        return Customer.allCustomers.stream()
                .filter(customer -> customer.userName.equals(userName))
                .findAny()
                .orElse(null);
    }

    /*
     * Get the username for a customer instance
     * 
     */
    public String getUserName() {
        return this.userName;
    }

    /*
     * Get the password for a customer instance
     * 
     */
    public String getPassword() {

        return this.password;
    }

    /*
     * Method to check if password is ok
     * 
     */
    boolean isPasswordOK(String password) {
        return this.password.equals(password);
    }

    /*
     * Get the accounts from the user
     * 
     */
    public List<Account> getAccounts() {
        return this.accounts;
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
     * Get a specific account from the user by name
     * 
     */
    public Account getAccountByName(String accountName) {

        return this.getAccounts()
                .stream()
                .filter(account -> account.getAccountName().equals(accountName))
                .findFirst()
                .orElse(null);
    }

    /*
     * Check if the user has an account by that name
     * 
     */
    public boolean hasAccountByName(String accountName) {

        return Objects.nonNull(this.getAccountByName(accountName));
    }

    /*
     * Add list of accounts to a user
     */
    public void addAccounts(List<Account> accounts) {
        this.accounts.addAll(accounts);
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

        return "<" + this.userName + "#" + this.password + ">";
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
