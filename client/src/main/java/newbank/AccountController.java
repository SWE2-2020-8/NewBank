package newbank;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class AccountController implements Initializable {

    @FXML
    private ListView<AccountModel> listViewAccounts;
    private ObservableList<AccountModel> accountList = FXCollections
            .observableArrayList();

    @FXML
    private Label detailName;

    @FXML
    private Label detailBalance;

    @FXML
    private void clickedClose(ActionEvent event) {
        System.err.println("Close");

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Login
        BankClient bankClient = new BankClient(
                "swe2-2020-8.southeastasia.azurecontainer.io", 80);

        bankClient.bankLogin("Admin", "1234");

        // Populate the accountList
        accountList.add(new AccountModel("asas", "Main", 1000.0));
        accountList.add(new AccountModel("asasdf", "Savings", 3000.0));
        accountList.add(new AccountModel("asassdf", "Others", 50.0));

        // Add it to the ListView
        listViewAccounts.setItems(accountList);

        // Add a listener for selection change
        listViewAccounts.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    System.err.println(newValue.toString());
                    detailName.setText(newValue.getName());
                    detailBalance.setText(newValue.getBalance().toString());

                });

    }
}
