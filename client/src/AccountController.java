
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

public class AccountController implements Initializable {

    // Will hold the latest active account
    AccountModel activeAccount;

    // The listview
    @FXML
    private ListView<AccountModel> listViewAccounts;
    private ObservableList<AccountModel> accountList = FXCollections
            .observableArrayList();

    // The tableview and some params
    @FXML
    private TableView<AccountModel.Transaction> transactionTable;
    private ObservableList<AccountModel.Transaction> transactionList = FXCollections
            .observableArrayList();
    @FXML
    private TableColumn<AccountModel.Transaction, String> dateColumn;
    @FXML
    private TableColumn<AccountModel.Transaction, String> amountColumn;
    @FXML
    private TableColumn<AccountModel.Transaction, String> balanceColumn;
    @FXML
    private TableColumn<AccountModel.Transaction, String> descriptionColumn;

    @FXML
    private Label detailName;

    @FXML
    private Label detailBalance;

    @FXML
    private Label detailOwner;

    @FXML
    private void handleWithdraw(ActionEvent event) {
        System.err.println(event);

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Withdraw money from your account");
        dialog.setHeaderText(
                "Withdraw money from your account " + activeAccount);
        dialog.setContentText("Please enter the amount to withdraw:");
        String amount = dialog.showAndWait().orElse("");

        if (Objects.isNull(activeAccount))
            showError("Please select an account first");
        else if (!isPositiveNumber(amount))
            showError("Illegal amount entered");
        else if (BankClient.deposit(activeAccount.getName(), amount)) {
            populateAccounts();
            showMessage("You have now " + amount
                    + " units of virtual currency in your pocket. Enjoy!");

        } else
            showError("The transaction was denied");
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        System.err.println(event);
    }

    @FXML
    private void handleMove(ActionEvent event) {
        System.err.println(event);
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        System.err.println(event);

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Deposit money into your account");
        dialog.setHeaderText(
                "Deposit money into your account " + activeAccount);
        dialog.setContentText("Please enter the amount to deposit:");
        String amount = dialog.showAndWait().orElse("");

        if (Objects.isNull(activeAccount))
            showError("Please select an account first");
        else if (!isPositiveNumber(amount))
            showError("Illegal amount entered");
        else if (BankClient.deposit(activeAccount.getName(), amount))
            populateAccounts();
        else
            showError("The transaction was denied");
    }

    public static boolean isPositiveNumber(String strNum) {
        int d;
        if (strNum == null) {
            return false;
        }
        try {
            d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return d > 0;
    }

    @FXML
    private void handleAccount(ActionEvent event) {
        System.err.println(event);

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Create a new account");
        dialog.setHeaderText("Creating a NewBank account");
        dialog.setContentText("Please enter the new account name:");
        String name = dialog.showAndWait().orElse("");

        if (name.equals(""))
            showError("Cannot create an account with an empty name");
        else if (name.length() < 4)
            showError("The name of the account is too short");
        else if (BankClient.newAccount(name)) {
            populateAccounts();

            // Make the new account active
            activeAccount = accountList.stream()
                    .filter(account -> account.getName().equals(name))
                    .findFirst()
                    .get();

            listViewAccounts.getSelectionModel()
                    .clearAndSelect(accountList.indexOf(activeAccount));
        } else
            showError("The transaction was denied");

    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Operation error");
        alert.setHeaderText("Error in NewBank transaction");
        alert.setContentText(message.equals("")
                ? "We are sorry, but the transaction wasn't successful"
                : message);
        alert.showAndWait();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information message");
        alert.setHeaderText("Information message");
        alert.setContentText(
                message.equals("") ? "The transaction was successful"
                        : message);
        alert.showAndWait();
    }

    private void populateAccounts() {

        // Populate the accounts list in reverse order
        accountList.clear();
        accountList.addAll(BankClient.getAccounts());

        // Add it to the ListView
        listViewAccounts.setItems(accountList);

        // If there was an active account, keep it active!
        if (Objects.nonNull(activeAccount)) {
            String persistName = activeAccount.getName();
            activeAccount = accountList.stream()
                    .filter(account -> account.getName().equals(persistName))
                    .findFirst()
                    .get();

            listViewAccounts.getSelectionModel()
                    .clearAndSelect(accountList.indexOf(activeAccount));

        } else if (!accountList.isEmpty())
            // First one by default
            listViewAccounts.getSelectionModel().select(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Set the columns
        dateColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateProperty());
        amountColumn.setCellValueFactory(
                cellData -> cellData.getValue().amountProperty());
        balanceColumn.setCellValueFactory(
                cellData -> cellData.getValue().balanceProperty());
        descriptionColumn.setCellValueFactory(
                cellData -> cellData.getValue().descriptionProperty());

        // Add a listener for selection change
        listViewAccounts.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    System.err.println(newValue.toString());
                    activeAccount = newValue;

                    // Details section
                    detailName.setText(newValue.getName());
                    detailBalance.setText(newValue.getBalance());
                    detailOwner.setText(newValue.getOwner());

                    // Transaction table
                    transactionList.clear();
                    transactionList.addAll(newValue.getTransactions());
                    transactionTable.setItems(transactionList);

                    // Placeholder
                    transactionTable.setPlaceholder(
                            new Label("No transactions to display"));
                });

        // Populate accounts
        populateAccounts();

    }
}
