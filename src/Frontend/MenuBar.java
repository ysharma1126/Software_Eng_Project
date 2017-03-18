package Frontend;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class MenuBar extends HBox {

  public MenuBar() {
    this.getStyleClass().add("menubar");
    Button button1 = new Button("+");
    Button button2 = new Button("hello");
    button1.getStyleClass().add("btn-menu");
    button2.getStyleClass().add("btn-menu");
    this.setSpacing(10);
    this.setPadding(new Insets(20,20,10,20));
    this.getChildren().addAll(button1, button2);
  }

/*  public MenuBar(double arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  public MenuBar(Node... arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  public MenuBar(double arg0, Node... arg1) {
    super(arg0, arg1);
    // TODO Auto-generated constructor stub
  }*/

}
