package newbank;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AccountController implements Initializable {

    private final ObservableList<AccountModel> accountList = FXCollections
            .observableArrayList(AccountModel.extractor);

    @FXML
    private ListView<AccountModel> listAccounts = new ListView<>(accountList);

    @FXML
    private Label detailName;

    @FXML
    private Label detailBalance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        BankClient bankClient = new BankClient(
                "swe2-2020-8.southeastasia.azurecontainer.io", 80);

        bankClient.bankLogin("Admin", "1234");

        accountList.add(new AccountModel("asas", "Main", 1000.0));
        accountList.add(new AccountModel("asasdf", "Savings", 3000.0));
        accountList.add(new AccountModel("asassdf", "Others", 50.0));

        listAccounts.setItems(accountList);
    }
}
