
/**
 * 
 * Controller for the Login scene
 * 
 * Follows the MVC pattern, this is the controller that manages the login
 * interaction
 * 
 * It calls the Account scene on successful login
 * 
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
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

public class LoginController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private TextField textField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleButtonAction(final ActionEvent event) {

        // Make the connection with the bank server
        BankClient.connect("swe2-2020-8.southeastasia.azurecontainer.io", 80);

        // Login attempt
        final boolean success = BankClient.bankLogin(textField.getText(),
                passwordField.getText());
        label.setText(success ? "Connected" : "Login failed, try again");

        // If successful go to new Stage
        if (success) {
            Parent root;
            try {
                root = FXMLLoader.load(
                        getClass().getResource("/fxml/AccountScene.fxml"));
                final Stage stage = new Stage();
                stage.setTitle("Your accounts");
                stage.setScene(new Scene(root));
                stage.show();
                // Hide this window
                ((Node) (event.getSource())).getScene().getWindow().hide();

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void passwordAction(final ActionEvent event) {

        handleButtonAction(event);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {

        // Request focus on the username field by default
        Platform.runLater(() -> textField.requestFocus());
    }
}
