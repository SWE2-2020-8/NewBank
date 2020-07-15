package newbank.server;

import java.util.ArrayList;

public class Customer {
<<<<<<< Updated upstream
	
	private ArrayList<Account> accounts;
	
	public Customer() {
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
=======

    private static Map<String, Customer> allCustomersMap = new HashMap<>();
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

        this.userName = userName;
        this.password = password;
        this.accounts = new ArrayList<>();
        Customer.allCustomersMap.put(userName, this);
    }

    /**
     * Method to create an account to an existing user
     * 
     * @param accountName
     * @param openingBalance
     */
    public Customer addAccount(String accountName, double openingBalance) {

        this.accounts.add(new Account(accountName, openingBalance));
        return this;
    }

    /**
     * Static method to get the if an userName already exists
     * 
     * @param userName
     * @return
     */
    public static boolean isCustomer(String userName) {

        return allCustomersMap.keySet().contains(userName);
    }

    /**
     * Static method to get the Customer object that corresponds to a userName
     * 
     * @param userName
     * @return
     */
    public static Customer getCustomer(String userName) {

        return allCustomersMap.get(userName);
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
    public static Map<String, Customer> getAllCustomersMap() {

        return allCustomersMap;
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

>>>>>>> Stashed changes
}
