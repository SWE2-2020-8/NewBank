import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account {

    static class Transaction {

        private double amount;
        private double balance;
        private String description;
        private String date;

        Transaction(double amount, double balance, String description) {

            this(amount, balance, description,
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                            .format(LocalDateTime.now()));
        }

        Transaction(double amount, double balance, String description,
                String date) {

            this.amount = amount;
            this.balance = balance;
            this.description = description;
            this.date = date;
        }

        @Override
        public String toString() {

            // Formatting doubles properly
            String strAmount = String.format("%.2f", this.amount);
            String strBalance = String.format("%.2f", this.balance);

            return "<" + strAmount + "#" + strBalance + "#" + this.description
                    + "#" + this.date + ">";
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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
     * Easily make a new transaction in this account
     */
    public void newTransaction(Double amount, String description) {

        // Roundings to avoid double arithmetic issues
        amount = Math.rint(amount * 100) / 100;
        double newBalance = Math.rint((this.getBalance() + amount) * 100) / 100;

        this.addTransaction(
                new Account.Transaction(+amount, newBalance, description));

    }

    /*
     * toString()
     */
    @Override
    public String toString() {

        // Formatting the balance properly
        String strBalance = String.format("%.2f", this.balance);

        return "<" + this.userName + "#" + this.accountName + "#" + strBalance
                + "#" + this.transactions + ">";
    }

}
