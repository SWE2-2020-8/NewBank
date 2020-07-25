package newbank;

import java.util.Objects;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class AccountModel {

    private StringProperty id = new SimpleStringProperty(this, "id", "");
    private StringProperty user = new SimpleStringProperty(this, "user", "");
    private StringProperty name = new SimpleStringProperty(this, "name", "");
    private DoubleProperty balance = new SimpleDoubleProperty(this, "balance",
            0.0);

    static class Transaction {
        private double amount;
        private double balance;
        private String description;
        private String date;
    }

    private Transaction[] transactions = {};

    // Constructor
    public AccountModel(String id, String name, Double balance) {
        this.id.set(id);
        this.name.set(name);
        this.balance.set(balance);
    }

    // Extractor to provide observability
    public static Callback<AccountModel, Observable[]> extractor = p -> new Observable[] {
            p.idProperty(), p.balanceProperty() };

    public Double getBalance() {
        return this.balance.get();
    }

    public void setBalance(Double balance) {
        this.balance.set(balance);
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public String getId() {
        return this.id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    @Override
    public String toString() {
        return name.get() + " " + balance.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        AccountModel accountModel = (AccountModel) obj;
        return Objects.equals(this.id, accountModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}