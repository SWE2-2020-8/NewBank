import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account {

    static class Transaction {

        String date;
        double amount;
        double balance;
        String description;

        Transaction(double amount, double balance, String description) {

            this(amount, balance, description,
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                            .format(LocalDateTime.now()));
        }

        Transaction(double amount, double balance, String description,
                String date) {

            this.date = date;
            this.amount = amount;
            this.balance = balance;
            this.description = description;
        }
    }

    private String accountId;
    private String userName;
    private String accountName;
    private double balance;
    private List<Transaction> transactions;

    public Account(String userName, String accountName) {

        this.accountId = userName + "-" + accountName;
        this.userName = userName;
        this.accountName = accountName;
        this.transactions = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.balance = transaction.balance;
    }

    /*
     * toString()
     */
    @Override
    public String toString() {

        return "<" + this.userName + "/" + this.accountName + "/"
                + this.transactions + ">";
    }
}
