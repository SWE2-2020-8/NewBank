
import java.util.ArrayList;

public class Customer {

	private String password = "";
	private ArrayList<Account> accounts;

	public Customer(String pw) {
		password = pw;
		accounts = new ArrayList<>();
	}
	
	public String getPassword() {
		return this.password;
	}

	public String accountsToString() {
		String s = "";
		for (Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);
	}
}
