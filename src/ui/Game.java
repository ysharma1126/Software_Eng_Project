package ui;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class Game extends BorderPane {

  public Game() {
    super();
    this.getStyleClass().add("game");
    this.setPadding(new Insets(0));
  }

}
