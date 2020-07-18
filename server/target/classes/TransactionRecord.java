/*
 * TransactionRecord class
 * 
 * CosmosDB use
 * 
 * This class is only to work with the database documents, do not modify
 */

public class TransactionRecord {

    private String date = "";
    private double amount = 0.0;
    private double balance = 0.0;
    private String description = "";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

}