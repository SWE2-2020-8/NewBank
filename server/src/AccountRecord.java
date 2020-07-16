/*
 * AccountRecord class
 * 
 * CosmosDB use
 * 
 * This class is only to work with the database documents, do not modify
 */

public class AccountRecord {

    private String id;
    private String userName;
    private String accountName;
    private Double balance = 0.0;
    private TransactionRecord[] transactions = {};

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public TransactionRecord[] getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionRecord[] transactions) {
        this.transactions = transactions;
    }

}