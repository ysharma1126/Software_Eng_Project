package Frontend;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Shows how you can modify css styles dynamically using a timeline. */
public class Warning extends Application {

    private static final String BACKGROUND = "http://bobgreiner.tripod.com/1cc2ce10.jpg"; 

    @Override
    public void start(Stage stage) throws Exception{
        final ObjectProperty<Color> warningColor = new SimpleObjectProperty<>(Color.GRAY);
        final StringProperty colorStringProperty = createWarningColorStringProperty(warningColor);

        StackPane layout = new StackPane();
        layout.getChildren().addAll(
                new ImageView(new Image(BACKGROUND)),
                createWarningButton(
                        warningColor, 
                        colorStringProperty
                )
        );
        stage.setScene(new Scene(layout));
        stage.show();
    }

    private StringProperty createWarningColorStringProperty(final ObjectProperty<Color> warningColor) {
        final StringProperty colorStringProperty = new SimpleStringProperty();
        setColorStringFromColor(colorStringProperty, warningColor);
        warningColor.addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observableValue, Color oldColor, Color newColor) {
                setColorStringFromColor(colorStringProperty, warningColor);
            }
        });

        return colorStringProperty;
    }

    private Button createWarningButton(final ObjectProperty<Color> warningColor, StringProperty colorStringProperty) {
        final Button warningButton = new Button("Warning! Warning!");
        warningButton.styleProperty().bind(
                new SimpleStringProperty("-fx-base: ")
                        .concat(colorStringProperty)
                        .concat(";")
                        .concat("-fx-font-size: 20px;")
        );

        warningButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Timeline flash = new Timeline(
                    new KeyFrame(Duration.seconds(0),    new KeyValue(warningColor, Color.GRAY, Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.25), new KeyValue(warningColor, Color.GRAY, Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(1),    new KeyValue(warningColor, Color.RED,  Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(1.25), new KeyValue(warningColor, Color.RED,  Interpolator.LINEAR))
                );
                flash.setCycleCount(6);
                flash.setAutoReverse(true);
                flash.play();
            }
        });

        return warningButton;
    }

    private void setColorStringFromColor(StringProperty colorStringProperty, ObjectProperty<Color> color) {
        colorStringProperty.set(
                "rgba("
                        + ((int) (color.get().getRed()   * 255)) + ","
                        + ((int) (color.get().getGreen() * 255)) + ","
                        + ((int) (color.get().getBlue()  * 255)) + ","
                        + color.get().getOpacity() +
                ")"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}