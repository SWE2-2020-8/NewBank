
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
    private Button buttonAccount;

    @FXML
    private void handleUser(MouseEvent event) {

        if (BankClient.isAdmin())
            adminMenu();
        else
            changePassword();
    }

    private void adminMenu() {

        // Create the custom dialog.
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Admin menu");
        dialog.setHeaderText("NewBank Admin reserved functions");
        dialog.setContentText(
                "Choose the Admin function to perform by choosin a button or select Close to cancel");

        // Set the button types.
        ButtonType changePasswordButtonType = new ButtonType("Change Password",
                ButtonData.OTHER);
        ButtonType addUserButtonType = new ButtonType("Add User",
                ButtonData.OTHER);
        ButtonType listUsersButtonType = new ButtonType("List Users",
                ButtonData.OTHER);
        ButtonType listAccountsType = new ButtonType("List Accounts",
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
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Change NewBank password");
        dialog.setHeaderText("Change your NewBank password");
        dialog.setContentText(
                "To change the password you need to type the current password plus the new one twice below");

        // Set the button types.
        ButtonType changePasswordButtonType = new ButtonType("Change Password",
                ButtonData.OK_DONE);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(changePasswordButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField passwordo = new PasswordField();
        passwordo.setPromptText("Current Password");
        PasswordField passwordn1 = new PasswordField();
        passwordn1.setPromptText("New Password");
        PasswordField passwordn2 = new PasswordField();
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
        Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent() && BankClient
                .changePassword(result.get().getKey(), result.get().getValue()))
            showMessage("Password has been successfully changed");
        else
            showError("There was an error and the password was not changed");
    }

    // To add a user
    // Changing the password
    private void addUser() {

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Create a new NewBank user");
        dialog.setHeaderText(
                "Admin reserved function: Create a new NewBank user");
        dialog.setContentText(
                "To create a new NewBank user you need to specify both Username and Password");

        // Set the button types.
        ButtonType createUserButtonType = new ButtonType(
                "Create new NewBank User", ButtonData.OK_DONE);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(createUserButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("New NewBank User Username");
        TextField password = new TextField();
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
        Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent() && BankClient.addUser(result.get().getKey(),
                result.get().getValue()))
            showMessage(
                    "New User " + result.get().getKey() + " has been created");
        else
            showError("New User could not be created");
    }

    // List all accounts
    private void listUsers() {

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("List of NewBank users");
        alert.setHeaderText("Admin reserved function: List all NewBank users");
        alert.setContentText(
                "Admin reserved function: Expand the control below to show the list of all users");

        Label label = new Label(
                "The following are all the users that are active in NewBank with their password");

        StringBuilder sb = new StringBuilder();
        BankClient.listUsers()
                .stream()
                .forEach(pair -> sb.append(
                        pair.getKey() + " (" + pair.getValue() + ")" + "\n"));

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    // List all accounts
    private void listAccounts() {

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("List of NewBank accounts");
        alert.setHeaderText("List of accounts in NewBank");
        alert.setContentText(
                "Admin reserved function: Expand the control below to show the list of all accounts");

        Label label = new Label(
                "The following are all the accounts that are active in NewBank with their balance");

        StringBuilder sb = new StringBuilder();
        BankClient.listAccounts()
                .stream()
                .forEach(account -> sb
                        .append(account.getOwner() + ": " + account.getName()
                                + " (" + account.getBalance() + ")" + "\n"));

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    // To withdraw money (needs to be tested)
    @FXML
    private void handleWithdraw(ActionEvent event) {

        if (Objects.isNull(activeAccount))
            showError(
                    "To withdraw money, you need to first create an account and have money in it");
        else {

            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Withdraw money from your NewBank account");
            dialog.setHeaderText("Withdraw money from your NewBank account "
                    + activeAccount);
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

        if (accountList.size() < 1)
            showError(
                    "To pay or transfer money to other users, please create an account and have money in it first");
        else {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Pay another NewBank user");
            dialog.setHeaderText(
                    "Pay or transfer money to another NewBank user using your account "
                            + activeAccount.getName());
            dialog.setContentText(
                    "Please enter the amount to pay to another user:");
            String amount = dialog.showAndWait().orElse("");

            if (isPositiveNumber(amount)) {

                List<String> choices = BankClient.listUsers()
                        .stream()
                        .map(Pair::getKey)
                        .filter(name -> !name.equals(BankClient.getUsername()))
                        .collect(Collectors.toList());

                ChoiceDialog<String> dialog2 = new ChoiceDialog<>(
                        choices.get(0), choices);
                dialog2.setTitle("Pay another NewBank user");
                dialog2.setHeaderText("Pay " + amount + " from account "
                        + activeAccount.getName());
                dialog2.setContentText(
                        "Please choose the user to transfer the money to:");
                String toUser = dialog2.showAndWait().get();

                if (BankClient.pay(amount, activeAccount.getName(), toUser)) {
                    populateAccounts();
                    showMessage("You have transferred " + amount
                            + " between account " + activeAccount.getName()
                            + " and user " + toUser);
                } else
                    showError("");
            } else
                showError("Sorry, an illegal amount has been entered");
        }
    }

    // Move between accounts
    @FXML
    private void handleMove(ActionEvent event) {

        if (accountList.size() < 2)
            showError("Please create at least two accounts to move money");
        else {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Move money between NewBank accounts");
            dialog.setHeaderText(
                    "Move money from account " + activeAccount.getName());
            dialog.setContentText(
                    "Please enter the amount to move to another account:");
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
                showError("Sorry, an illegal amount has been entered");
        }
    }

    // To deposit money
    @FXML
    private void handleDeposit(ActionEvent event) {

        if (Objects.isNull(activeAccount))
            showError("To deposit money, you must create an account first");
        else {

            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Deposit money into your NewBank account");
            dialog.setHeaderText(
                    "Deposit money into your NewBank account " + activeAccount);
            dialog.setContentText(
                    "Please enter the amount to deposit into the account:");
            String amount = dialog.showAndWait().orElse("");

            if (!isPositiveNumber(amount))
                showError("Sorry, an illegal amount has been entered");
            else if (BankClient.deposit(activeAccount.getName(), amount)) {
                populateAccounts();
                showMessage("You have now deposited " + amount
                        + " units of virtual currency from your pocket into your NewBank account"
                        + activeAccount.getName() + ". Thanks!");
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
        dialog.setTitle("Create a new NewBank account");
        dialog.setHeaderText("Creating a new NewBank account");
        dialog.setContentText("Please enter the name for the new account:");
        String name = dialog.showAndWait().orElse("");

        if (name.equals(""))
            showError("Sorry, cannot create an account with an empty name");
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
        alert.setTitle("NewBank Operation error");
        alert.setHeaderText("Error in NewBank transaction");
        alert.setContentText(message.equals("")
                ? "We are sorry, but the transaction wasn't successful: the transaction was denied by the bank server"
                : message);
        alert.showAndWait();
    }

    // Show a message
    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("NewBank Information Message");
        alert.setHeaderText("NewBank Information Message");
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
        dateColumn.setStyle("-fx-alignment: BOTTOM-LEFT;");
        amountColumn.setCellValueFactory(
                cellData -> cellData.getValue().amountProperty());
        amountColumn.setStyle("-fx-alignment: BOTTOM-RIGHT;");
        balanceColumn.setCellValueFactory(
                cellData -> cellData.getValue().balanceProperty());
        balanceColumn.setStyle("-fx-alignment: BOTTOM-RIGHT;");
        descriptionColumn.setCellValueFactory(
                cellData -> cellData.getValue().descriptionProperty());
        descriptionColumn.setStyle("-fx-alignment: BOTTOM-LEFT;");

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
                        } else {
                            setText(item);
                            setTextFill(Color.BLACK);
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

                    // Transaction table
                    transactionList.clear();
                    transactionList.addAll(newValue.getTransactions());
                    transactionTable.setItems(transactionList);

                    // Placeholder
                    transactionTable.setPlaceholder(
                            new Label("No transactions to display"));
                });

        // Write the username
        detailOwner.setText(BankClient.getUsername());

        // Populate accounts
        populateAccounts();

        // If no accounts, highlight the new account button:
        if (accountList.isEmpty())
            Platform.runLater(() -> buttonAccount.requestFocus());
    }
}
