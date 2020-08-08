
/**
 * 
 * Main entry point to JavaFX
 * 
 * Creates the stage and calls the Login scene
 * 
 * 
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EntryPoint extends Application {

    @Override
    public void start(final Stage stage) throws Exception {
        final Parent root = FXMLLoader
                .load(getClass().getResource("fxml/LoginScene.fxml"));

        final Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/Styles.css");

        stage.setTitle("NewBank Client Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args
     *             the command line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

}
