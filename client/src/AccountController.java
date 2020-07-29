
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

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

    // To withdraw money (needs to be tested)
    @FXML
    private void handleWithdraw(ActionEvent event) {

        if (Objects.isNull(activeAccount))
            showError("Please create an account first");
        else {

            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Withdraw money from your account");
            dialog.setHeaderText(
                    "Withdraw money from your account " + activeAccount);
            dialog.setContentText("Please enter the amount to withdraw:");
            String amount = dialog.showAndWait().orElse("");

            if (!isPositiveNumber(amount))
                showError("Illegal amount entered");
            else if (BankClient.withdraw(activeAccount.getName(), amount)) {
                populateAccounts();
                showMessage("You have now an additional " + amount
                        + " units of virtual currency in your pocket. Enjoy!");
            } else
                showError("");
        }
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        System.err.println(event);
    }

    // Move between accounts
    @FXML
    private void handleMove(ActionEvent event) {

        if (accountList.size() < 2)
            showError("Please create at least two accounts to move money");
        else {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Move money between accounts");
            dialog.setHeaderText(
                    "Move money from account " + activeAccount.getName());
            dialog.setContentText("Please enter the amount to move:");
            String amount = dialog.showAndWait().orElse("");

            if (isPositiveNumber(amount)) {

                List<String> choices = accountList.stream()
                        .map(AccountModel::getName)
                        .filter(name -> !name.equals(activeAccount.getName()))
                        .collect(Collectors.toList());

                ChoiceDialog<String> dialog2 = new ChoiceDialog<>(
                        choices.get(0), choices);
                dialog2.setTitle("Move money between accounts");
                dialog2.setHeaderText("Move " + amount + " from account "
                        + activeAccount.getName());
                dialog2.setContentText("Please choose destination account:");

                String toAccount = dialog2.showAndWait().get();

                if (BankClient.move(amount, activeAccount.getName(),
                        toAccount)) {
                    populateAccounts();
                    showMessage("You have transferred " + amount
                            + " between account " + activeAccount.getName()
                            + " and account " + toAccount);
                } else
                    showError("");

            } else
                showError("Illegal amount entered");

        }
    }

    // To deposit money
    @FXML
    private void handleDeposit(ActionEvent event) {

        if (Objects.isNull(activeAccount))
            showError("Please create an account first");
        else {

            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Deposit money into your account");
            dialog.setHeaderText(
                    "Deposit money into your account " + activeAccount);
            dialog.setContentText("Please enter the amount to deposit:");
            String amount = dialog.showAndWait().orElse("");

            if (!isPositiveNumber(amount))
                showError("Illegal amount entered");
            else if (BankClient.deposit(activeAccount.getName(), amount)) {
                populateAccounts();
                showMessage("You have now deposited " + amount
                        + " units of virtual currency from your pocket into NewBank. Thanks!");
            } else
                showError("");
        }
    }

    // To check if number is positive
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

    // To create a new account
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
            showError("");

    }

    // Show an error message
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Operation error");
        alert.setHeaderText("Error in NewBank transaction");
        alert.setContentText(message.equals("")
                ? "We are sorry, but the transaction wasn't successful: the transaction was denied by the bank server"
                : message);
        alert.showAndWait();
    }

    // Show a message
    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information message");
        alert.setHeaderText("Information message");
        alert.setContentText(message.equals("")
                ? "The transaction was successful: the transaction was confirmed by the bank server"
                : message);
        alert.showAndWait();
    }

    // Get the accounts from the server and populate them
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

        // Set the format for amounts
        amountColumn.setCellFactory(
                tableColumn -> new TableCell<AccountModel.Transaction, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item == null || empty || item.isEmpty()) {
                            setText(null);
                        } else if (item.matches("^\\-.*")) {
                            setText(item);
                            setTextFill(Color.RED);
                            setAlignment(Pos.BASELINE_RIGHT);
                        } else {
                            setText(item);
                            setTextFill(Color.BLACK);
                            setAlignment(Pos.BASELINE_RIGHT);
                        }

                    }
                });

        // Set the format for balance
        balanceColumn.setCellFactory(
                tableColumn -> new TableCell<AccountModel.Transaction, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item == null || empty || item.isEmpty()) {
                            setText(null);
                        } else {
                            setText(item);
                            setTextFill(Color.BLACK);
                            setAlignment(Pos.BASELINE_RIGHT);

                        }

                    }
                });

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
