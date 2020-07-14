
import java.util.HashMap;
import java.util.Objects;

public class NewBank {

    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;
    private Authenticate auth;

    private NewBank() {
        customers = new HashMap<>();
        addTestData();
        auth = new Authenticate(customers);
    }

    private void addTestData() {
        Customer bhagy = new Customer("hi");
        bhagy.addAccount(new Account("Main", 1000.0));
        customers.put("Bhagy", bhagy);

        Customer christina = new Customer("lol");
        christina.addAccount(new Account("Savings", 1500.0));
        customers.put("Christina", christina);

        Customer john = new Customer("nhoj");
        john.addAccount(new Account("Checking", 250.0));
        customers.put("John", john);
    }

    public static NewBank getBank() {
        return bank;
    }

    public synchronized CustomerID checkLogInDetails(String userName,
            String password) {
        return auth.checkLogInDetails(userName, password);

    }

    /*
     * commands from the NewBank customer are processed in this method
     */
    public synchronized String processRequest(CustomerID customer,
            String request) {

        String[] words = request.split(" ");

        if (customers.containsKey(customer.getKey())) {

            System.err.println(this.getClass().getName() + ": "
                    + customer.getKey() + " REQUESTS \"" + request + "\"");
            switch (request) {
            case "SHOWMYACCOUNTS":
                return showMyAccounts(customer);
            case "NEWACCOUNT": // created this so customer can call new account
                if (words.length == 2) {
                    return newAccount(customer, words[1]);

                } else {
                    System.err.println(
                            this.getClass().getName() + ": " + customer.getKey()
                                    + " FAILED REQUEST \"" + request + "\"");
                    return "FAIL";
                }

            default:
                System.err.println(this.getClass().getName() + ": "
                        + customer.getKey() + " ILLEGAL REQUEST");
                return "FAIL";
            }
        }
        return "FAIL";
    }

    private String showMyAccounts(CustomerID customer) {
        return (customers.get(customer.getKey())).accountsToString();
    }

    // added newAccount so customer can create new accounts
    private String newAccount(CustomerID customer, String accountName) {
        Customer Customer = customers.get(customer.getKey());
        if (Customer.addAccount(new Account(accountName, 0.0))) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }
}
