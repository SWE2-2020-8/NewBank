
/**
 * 
 * Controller for the Account scene
 * 
 * Follows the MVC pattern, this is the controller that manages all the
 * interaction with the user
 * 
 * 
 */

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class AccountController implements Initializable {

    // Will hold the latest active account
    private AccountModel activeAccount;

    // The listview for the accounts
    @FXML
    private ListView<AccountModel> listViewAccounts;
    private final ObservableList<AccountModel> accountList = FXCollections
            .observableArrayList();

    // The tableview for the movements
    @FXML
    private TableView<AccountModel.Transaction> transactionTable;
    private final ObservableList<AccountModel.Transaction> transactionList = FXCollections
            .observableArrayList();
    @FXML
    private TableColumn<AccountModel.Transaction, String> dateColumn;
    @FXML
    private TableColumn<AccountModel.Transaction, String> amountColumn;
    @FXML
    private TableColumn<AccountModel.Transaction, String> balanceColumn;
    @FXML
    private TableColumn<AccountModel.Transaction, String> descriptionColumn;

    // Label with Account name
    @FXML
    private Label detailName;

    // Label with Account balance
    @FXML
    private Label detailBalance;

    // Label with logged user
    @FXML
    private Label detailOwner;

    // Button to create account
    @FXML
    private Button buttonAccount;

    // User menu, different for Admin or regular user
    @FXML
    private void handleUser(final MouseEvent event) {

        if (BankClient.isAdmin())
            adminMenu();
        else
            changePassword();
    }

    // Showing the Admin menu
    private void adminMenu() {

        // Create the custom dialog.
        final Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Admin menu");
        dialog.setHeaderText("NewBank Admin reserved functions");
        dialog.setContentText(
                "Choose the Admin function to perform by choosin a button or select Close to cancel");

        // Set the button types.
        final ButtonType changePasswordButtonType = new ButtonType(
                "Change Password", ButtonData.OTHER);
        final ButtonType addUserButtonType = new ButtonType("Add User",
                ButtonData.OTHER);
        final ButtonType listUsersButtonType = new ButtonType("List Users",
                ButtonData.OTHER);
        final ButtonType listAccountsType = new ButtonType("List Accounts",
                ButtonData.OTHER);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(changePasswordButtonType, addUserButtonType,
                        listUsersButtonType, listAccountsType,
                        ButtonType.CLOSE);

        // Do the proper thing depending on button clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changePasswordButtonType)
                changePassword();
            else if (dialogButton == addUserButtonType)
                addUser();
            else if (dialogButton == listUsersButtonType)
                listUsers();
            else if (dialogButton == listAccountsType)
                listAccounts();
            return null;
        });
        dialog.showAndWait();
    }

    // Changing the password
    private void changePassword() {

        // Create the custom dialog.
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Change NewBank password");
        dialog.setHeaderText("Change your NewBank password");
        dialog.setContentText(
                "To change the password you need to type the current password plus the new one twice below");

        // Set the button types.
        final ButtonType changePasswordButtonType = new ButtonType(
                "Change Password", ButtonData.OK_DONE);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(changePasswordButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields
        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final PasswordField passwordo = new PasswordField();
        passwordo.setPromptText("Current Password");
        final PasswordField passwordn1 = new PasswordField();
        passwordn1.setPromptText("New Password");
        final PasswordField passwordn2 = new PasswordField();
        passwordn2.setPromptText("Retype New Password");

        grid.add(new Label("Old Password:"), 0, 0);
        grid.add(passwordo, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(passwordn1, 1, 1);
        grid.add(new Label("Retype New Password:"), 0, 2);
        grid.add(passwordn2, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button
        // is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changePasswordButtonType
                    && passwordn1.getText().equals(passwordn2.getText())) {
                return new Pair<>(passwordo.getText(), passwordn1.getText());
            }
            return null;
        });

        // Wait for result and check outcome
        final Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent() && BankClient
                .changePassword(result.get().getKey(), result.get().getValue()))
            showMessage("Password has been successfully changed");
        else
            showError("There was an error and the password was not changed");
    }

    // Adding a user
    private void addUser() {

        // Create the custom dialog
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Create a new NewBank user");
        dialog.setHeaderText(
                "Admin reserved function: Create a new NewBank user");
        dialog.setContentText(
                "To create a new NewBank user you need to specify both Username and Password");

        // Set the button types.
        final ButtonType createUserButtonType = new ButtonType(
                "Create new NewBank User", ButtonData.OK_DONE);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(createUserButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields
        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final TextField username = new TextField();
        username.setPromptText("New NewBank User Username");
        final TextField password = new TextField();
        password.setPromptText("New NewBank User Password");

        grid.add(new Label("New NewBank User Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("New NewBank User Password:"), 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button
        // is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createUserButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        // Wait for result and check outcome
        final Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent() && BankClient.addUser(result.get().getKey(),
                result.get().getValue()))
            showMessage(
                    "New User " + result.get().getKey() + " has been created");
        else
            showError("New User could not be created");
    }

    // Listing all accounts
    private void listUsers() {

        // Create the alert information box
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("List of NewBank users");
        alert.setHeaderText("Admin reserved function: List all NewBank users");
        alert.setContentText(
                "Admin reserved function: Expand the control below to show the list of all users");
        final Label label = new Label(
                "The following are all the users that are active in NewBank with their password");

        final StringBuilder sb = new StringBuilder();
        BankClient.listUsers()
                .stream()
                .forEach(pair -> sb.append(
                        pair.getKey() + " (" + pair.getValue() + ")" + "\n"));

        final TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    // Listing all accounts
    private void listAccounts() {

        // Create the alert information box
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("List of NewBank accounts");
        alert.setHeaderText("List of accounts in NewBank");
        alert.setContentText(
                "Admin reserved function: Expand the control below to show the list of all accounts");

        final Label label = new Label(
                "The following are all the accounts that are active in NewBank with their balance");

        final StringBuilder sb = new StringBuilder();
        BankClient.listAccounts()
                .stream()
                .forEach(account -> sb
                        .append(account.getOwner() + ": " + account.getName()
                                + " (" + account.getBalance() + ")" + "\n"));

        final TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    // Withdrawing money
    @FXML
    private void handleWithdraw(final ActionEvent event) {

        if (Objects.isNull(activeAccount))
            showError(
                    "To withdraw money, you need to first create an account and have money in it");
        else {

            final TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Withdraw money from your NewBank account");
            dialog.setHeaderText("Withdraw money from your NewBank account "
                    + activeAccount);
            dialog.setContentText("Please enter the amount to withdraw:");
            final String amount = dialog.showAndWait().orElse("");

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

    // Paying another user
    @FXML
    private void handleTransfer(final ActionEvent event) {

        if (accountList.isEmpty())
            showError(
                    "To pay or transfer money to other users, please create an account and have money in it first");
        else {
            final TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Pay another NewBank user");
            dialog.setHeaderText(
                    "Pay or transfer money to another NewBank user using your account "
                            + activeAccount.getName());
            dialog.setContentText(
                    "Please enter the amount to pay to another user:");
            final String amount = dialog.showAndWait().orElse("");

            if (isPositiveNumber(amount)) {

                final List<String> choices = BankClient.listUsers()
                        .stream()
                        .map(Pair::getKey)
                        .filter(name -> !name.equals(BankClient.getUsername()))
                        .collect(Collectors.toList());

                final ChoiceDialog<String> dialog2 = new ChoiceDialog<>(
                        choices.get(0), choices);
                dialog2.setTitle("Pay another NewBank user");
                dialog2.setHeaderText("Pay " + amount + " from account "
                        + activeAccount.getName());
                dialog2.setContentText(
                        "Please choose the user to transfer the money to:");
                final Optional<String> toUser = dialog2.showAndWait();

                if (toUser.isPresent() && BankClient.pay(amount,
                        activeAccount.getName(), toUser.get())) {
                    populateAccounts();
                    showMessage("You have paid " + amount + " to the user "
                            + toUser.get() + " from the account "
                            + activeAccount.getName());
                } else
                    showError("");
            } else
                showError(ILLEGAL_AMOUNT_MSG);
        }
    }

    // Moving money between accounts
    @FXML
    private void handleMove(final ActionEvent event) {

        if (accountList.size() < 2)
            showError("Please create at least two accounts to move money");
        else {
            final TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Move money between NewBank accounts");
            dialog.setHeaderText(
                    "Move money from account " + activeAccount.getName());
            dialog.setContentText(
                    "Please enter the amount to move to another account:");
            final String amount = dialog.showAndWait().orElse("");

            if (isPositiveNumber(amount)) {

                final List<String> choices = accountList.stream()
                        .map(AccountModel::getName)
                        .filter(name -> !name.equals(activeAccount.getName()))
                        .collect(Collectors.toList());

                final ChoiceDialog<String> dialog2 = new ChoiceDialog<>(
                        choices.get(0), choices);
                dialog2.setTitle("Move money between accounts");
                dialog2.setHeaderText("Move " + amount + " from account "
                        + activeAccount.getName());
                dialog2.setContentText("Please choose destination account:");
                final Optional<String> toAccount = dialog2.showAndWait();

                if (toAccount.isPresent() && BankClient.move(amount,
                        activeAccount.getName(), toAccount.get())) {
                    populateAccounts();
                    showMessage("You have transferred " + amount
                            + " between account " + activeAccount.getName()
                            + " and account " + toAccount);
                } else
                    showError("");
            } else
                showError(ILLEGAL_AMOUNT_MSG);
        }
    }

    // Depositing money
    @FXML
    private void handleDeposit(final ActionEvent event) {

        if (Objects.isNull(activeAccount))
            showError("To deposit money, you must create an account first");
        else {

            final TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Deposit money into your NewBank account");
            dialog.setHeaderText(
                    "Deposit money into your NewBank account " + activeAccount);
            dialog.setContentText(
                    "Please enter the amount to deposit into the account:");
            final String amount = dialog.showAndWait().orElse("");

            if (!isPositiveNumber(amount))
                showError(ILLEGAL_AMOUNT_MSG);
            else if (BankClient.deposit(activeAccount.getName(), amount)) {
                populateAccounts();
                showMessage("You have now deposited " + amount
                        + " units of virtual currency from your pocket into your NewBank account"
                        + activeAccount.getName() + ". Thanks!");
            } else
                showError("");
        }
    }

    // Checking if number is positive
    private static boolean isPositiveNumber(final String strNum) {
        int d;
        if (strNum == null) {
            return false;
        }
        try {
            d = Integer.parseInt(strNum);
        } catch (final NumberFormatException nfe) {
            return false;
        }
        return d > 0;
    }

    // Creating a new account
    @FXML
    private void handleAccount(final ActionEvent event) {
        System.err.println(event);

        final TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Create a new NewBank account");
        dialog.setHeaderText("Creating a new NewBank account");
        dialog.setContentText("Please enter the name for the new account:");
        final String name = dialog.showAndWait().orElse("");

        if (name.equals(""))
            showError("Sorry, cannot create an account with an empty name");
        else if (name.length() < 4)
            showError("The name of the account is too short");
        else if (BankClient.newAccount(name)) {

            // We reload and make the new account active
            populateAccounts();
            activeAccount = accountList.stream()
                    .filter(account -> account.getName().equals(name))
                    .findFirst()
                    .orElseThrow();
            listViewAccounts.getSelectionModel()
                    .clearAndSelect(accountList.indexOf(activeAccount));
        } else
            showError("");
    }

    // Getting accounts from the server to populate the list
    private void populateAccounts() {

        // Populate the accounts list
        accountList.clear();
        accountList.addAll(BankClient.getAccounts());

        // Add it to the ListView
        listViewAccounts.setItems(accountList);

        // If there was an active account, keep it active!
        if (Objects.nonNull(activeAccount)) {

            final String persistName = activeAccount.getName();
            activeAccount = accountList.stream()
                    .filter(account -> account.getName().equals(persistName))
                    .findFirst()
                    .orElseThrow();
            listViewAccounts.getSelectionModel()
                    .clearAndSelect(accountList.indexOf(activeAccount));

        } else if (!accountList.isEmpty())
            // If no account is active, and the list is not empty, get the first
            // one by default
            listViewAccounts.getSelectionModel().select(0);
        else
            listViewAccounts.setPlaceholder(
                    new Label("No accounts to display, please create one"));
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {

        // For the TableColumn describing the transactions
        // Set the columns
        dateColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateProperty());
        amountColumn.setCellValueFactory(
                cellData -> cellData.getValue().amountProperty());
        balanceColumn.setCellValueFactory(
                cellData -> cellData.getValue().balanceProperty());
        descriptionColumn.setCellValueFactory(
                cellData -> cellData.getValue().descriptionProperty());

        // Set column alignment
        dateColumn.setStyle("-fx-alignment: BOTTOM-LEFT;");
        amountColumn.setStyle("-fx-alignment: BOTTOM-RIGHT;");
        balanceColumn.setStyle("-fx-alignment: BOTTOM-RIGHT;");
        descriptionColumn.setStyle("-fx-alignment: BOTTOM-LEFT;");

        // Display negative amounts in red
        amountColumn.setCellFactory(
                tableColumn -> new TableCell<AccountModel.Transaction, String>() {
                    @Override
                    protected void updateItem(final String item,
                            final boolean empty) {
                        if (item == null || empty || item.isEmpty()) {
                            setText(null);
                        } else if (item.matches("^\\-.*")) {
                            setText(item);
                            setTextFill(Color.RED);
                        } else {
                            setText(item);
                            setTextFill(Color.BLACK);
                        }
                    }
                });

        // Add a listener that will update the scene when an account is selected
        listViewAccounts.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    // Do not trigger when resetting the list
                    if (Objects.nonNull(newValue)) {

                        // Store active account object
                        activeAccount = newValue;

                        // Update account name and balance
                        detailName.setText(newValue.getName());
                        detailBalance.setText(newValue.getBalance());

                        // Update transaction list
                        transactionList.clear();
                        transactionList.addAll(newValue.getTransactions());
                        transactionTable.setItems(transactionList);
                    }
                });

        // Set the username in the scene
        detailOwner.setText(BankClient.getUsername());

        // Populate accounts list
        populateAccounts();

        // Placeholder if no account and/or transactions present
        transactionTable.setPlaceholder(new Label(
                "No transactions to display: please create an account"));

        // If no accounts are present, highlight the create account button
        if (accountList.isEmpty())
            Platform.runLater(() -> buttonAccount.requestFocus());

    }

    // Standard messages
    private static final String NEW_BANK_INFORMATION_TITLE = "NewBank Information Message";
    private static final String SUCCESSFUL_TRANSACTION_MSG = "The transaction was successful: the transaction was confirmed by the bank server";
    private static final String FAILED_TRANSACTION_TITLE = "Error in NewBank Transaction";
    private static final String FAILED_TRANSACTION_MSG = "We are sorry, but the transaction wasn't successful: the transaction was denied by the bank server";
    private static final String ILLEGAL_AMOUNT_MSG = "Sorry, an illegal amount has been entered";

    // Showing an error message
    private void showError(final String message) {
        final Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(FAILED_TRANSACTION_TITLE);
        alert.setHeaderText(FAILED_TRANSACTION_TITLE);
        alert.setContentText(
                message.equals("") ? FAILED_TRANSACTION_MSG : message);
        alert.showAndWait();
    }

    // Showing an information message
    private void showMessage(final String message) {
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(NEW_BANK_INFORMATION_TITLE);
        alert.setHeaderText(NEW_BANK_INFORMATION_TITLE);
        alert.setContentText(
                message.equals("") ? SUCCESSFUL_TRANSACTION_MSG : message);
        alert.showAndWait();
    }
}
