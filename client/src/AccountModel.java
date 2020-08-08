
/**
 * 
 * Model for the Account scene
 * 
 * Follows the MVC pattern, this is the model that manages all the account
 * operations
 * 
 * 
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class AccountModel {

    private final StringProperty owner = new SimpleStringProperty(this, "owner",
            "");
    private final StringProperty name = new SimpleStringProperty(this, "name",
            "");
    private final StringProperty balance = new SimpleStringProperty(this,
            "balance", "");
    private final List<Transaction> transactions;

    static class Transaction {
        private final StringProperty amount = new SimpleStringProperty(this,
                "amount", "");
        private final StringProperty balance = new SimpleStringProperty(this,
                "balance", "");
        private final StringProperty description = new SimpleStringProperty(
                this, "description", "");
        private final StringProperty date = new SimpleStringProperty(this,
                "date", "");

        Transaction(final String amount, final String balance,
                final String description, final String date) {
            this.amount.setValue(amount);
            this.balance.setValue(balance);
            this.description.setValue(description);
            this.date.setValue(date);
        }

        @Override
        public String toString() {
            return "Transaction [amount=" + amount.getValue() + ", balance="
                    + balance.getValue() + ", date=" + date.getValue()
                    + ", description=" + description.getValue() + "]";
        }

        public String getAmount() {
            return amount.getValue();
        }

        public String getBalance() {
            return balance.getValue();
        }

        public String getDescription() {
            return description.getValue();
        }

        public String getDate() {
            return date.getValue();
        }

        public StringProperty amountProperty() {
            return amount;
        }

        public StringProperty balanceProperty() {
            return balance;
        }

        public StringProperty descriptionProperty() {
            return description;
        }

        public StringProperty dateProperty() {
            return date;
        }

    }

    // Constructor
    public AccountModel(final String owner, final String name,
            final String balance, final String transactions) {

        this.owner.set(owner);
        this.name.set(name);
        this.balance.set(balance);

        final Pattern p = Pattern.compile(
                "\\<(-?[\\d\\,]*.\\d*)#(-?[\\d\\,]*.\\d*)#([a-zA-Z0-9\\% \\.\\,-_\\+]+)#([a-zA-Z0-9\\:\\/ ]+)\\>");

        final Matcher m = p.matcher(transactions);

        this.transactions = m.results()
                .map(mr -> new AccountModel.Transaction(mr.group(1),
                        mr.group(2), mr.group(3), mr.group(4)))
                .collect(Collectors.toList());
    }

    // Extractor to provide observability
    public static Callback<AccountModel, Observable[]> extractor = p -> new Observable[] {
            p.ownerProperty(), p.nameProperty(), p.balanceProperty() };

    public String getName() {
        return this.name.get();
    }

    public StringProperty nameProperty() {
        return this.name;
    }

    public String getBalance() {
        return this.balance.get();
    }

    public void setBalance(final String balance) {
        this.balance.set(balance);
    }

    public StringProperty balanceProperty() {
        return this.balance;
    }

    public String getOwner() {
        return this.owner.get();
    }

    public StringProperty ownerProperty() {
        return this.owner;
    }

    public List<Transaction> getTransactions() {

        final List<Transaction> reversedTransactionList = new ArrayList<>();
        this.transactions.stream()
                .forEach(transaction -> reversedTransactionList.add(0,
                        transaction));
        return reversedTransactionList;
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final AccountModel accountModel = (AccountModel) obj;
        return Objects.equals(this.name, accountModel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}