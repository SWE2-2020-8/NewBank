import java.util.HashMap;

public class Authenticate {
	
	private HashMap<String, Customer> customers;

	public Authenticate(HashMap<String, Customer> customer) {
		customers = customer;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName,
			String password) {
		Customer user = customers.get(userName);
		if (customers.containsKey(userName) && password.equals(user.getPassword())) {
			return new CustomerID(userName);
		}
		return null;
	}
}