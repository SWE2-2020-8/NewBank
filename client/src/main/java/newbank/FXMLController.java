package newbank;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private TextField textField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        boolean success = BankClient.bankLogin(textField.getText(),
                passwordField.getText());

        label.setText(success ? "Connected" : "Login failed, try again");

        if (success) {

            Parent root;
            try {
                root = FXMLLoader.load(
                        getClass().getResource("/fxml/AccountScene.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Your accounts");
                stage.setScene(new Scene(root));
                stage.show();
                // Hide this window
                ((Node) (event.getSource())).getScene().getWindow().hide();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    private void passwordAction(ActionEvent event) {

        handleButtonAction(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        BankClient.connect("swe2-2020-8.southeastasia.azurecontainer.io", 80);
    }
}
