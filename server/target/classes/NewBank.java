package newbank.server;

<<<<<<< Updated upstream
import java.util.HashMap;
=======
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
        this.customers = Customer.getAllCustomersMap();
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
>>>>>>> Stashed changes

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
