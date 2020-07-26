
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AccountController implements Initializable {

    @FXML
    private ListView<AccountModel> listViewAccounts;
    private ObservableList<AccountModel> accountList = FXCollections
            .observableArrayList();

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
    private void clickedClose(ActionEvent event) {
        System.err.println("Close");

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Populate the accounts list
        accountList.addAll(BankClient.getAccounts());

        // Add it to the ListView
        listViewAccounts.setItems(accountList);

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

                    // Details section
                    detailName.setText(newValue.getName());
                    detailBalance.setText(newValue.getBalance());
                    detailOwner.setText(newValue.getOwner());

                    // Transaction table
                    transactionList.clear();
                    transactionList.addAll(newValue.getTransactions());
                    transactionTable.setItems(transactionList);
                });
    }
}
