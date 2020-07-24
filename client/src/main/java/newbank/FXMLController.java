package newbank;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FXMLController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private TextField textField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        BankClient bankClient = new BankClient(
                "swe2-2020-8.southeastasia.azurecontainer.io", 80);

        boolean success = bankClient.bankLogin(textField.getText(),
                passwordField.getText());

        label.setText(success ? "Connected" : "Login failed");

        if (success)
            bankClient.getAccounts();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
