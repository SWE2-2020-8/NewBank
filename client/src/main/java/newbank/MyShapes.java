package newbank;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MyShapes extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Gradient
        Stop[] stops = new Stop[] { new Stop(0, Color.DODGERBLUE),
                new Stop(0.5, Color.LIGHTBLUE),
                new Stop(1.0, Color.LIGHTGREEN) };

        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE, stops);

        // Create an Ellipse and set fill color
        Ellipse ellipse = new Ellipse(110, 70);
        ellipse.setFill(gradient);
        ellipse.setEffect(new DropShadow(30, 10, 10, Color.GRAY));
        ellipse.setOnMouseClicked(mouseEvent -> System.out
                .println(mouseEvent.getSource().getClass() + " clicked."));

        // Create a Text shape with font and size
        Text text = new Text("My Shapes");
        text.setFont(new Font("Arial Bold", 24));
        Reflection r = new Reflection();
        r.setFraction(.8);
        r.setTopOffset(1.0);
        text.setEffect(r);
        text.setOnMouseClicked(mouseEvent -> System.out
                .println(mouseEvent.getSource().getClass() + " clicked."));

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(ellipse, text);

        // Define RotateTransition
        RotateTransition rotate = new RotateTransition(Duration.millis(2500),
                text);
        rotate.setToAngle(360);
        rotate.setFromAngle(0);
        rotate.setInterpolator(Interpolator.LINEAR);

        // configure mouse click handler
        stackPane.setOnMouseClicked(mouseEvent -> {
            if (rotate.getStatus().equals(Animation.Status.RUNNING)) {
                rotate.pause();
            } else {
                rotate.play();
            }
        });

        Text text2 = new Text("Another text");
        text.setFont(new Font("Arial Bold", 24));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(stackPane, text2);

        rotate.statusProperty()
                .addListener(observable -> text2.setText("Animation status: "
                        + ((ObservableObjectValue<Animation.Status>) observable)
                                .getValue()));

        Scene scene = new Scene(vbox, 350, 230, Color.LIGHTYELLOW);
        stage.setTitle("MyShapes with JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
