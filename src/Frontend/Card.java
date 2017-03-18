package Frontend;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class Card extends Button {
  private static String color;
  private static String number;
  private static String shape;
  private static String pattern;
  private static String imgpath;
  
  public Card() {
    super();
    this.getStyleClass().add("btn-card");
  }

  public Card(String text) {
    super(text);
    this.getStyleClass().add("btn-card");
    this.setMinSize(85, 120);
    this.setMaxSize(85, 120);
    this.setPrefSize(85, 150);
    this.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
          System.out.println("Hello World!");
      }
  });
  }

  public Card(String text, Node graphic) {
    super(text, graphic);
    this.getStyleClass().add("btn-card");
  }

}
