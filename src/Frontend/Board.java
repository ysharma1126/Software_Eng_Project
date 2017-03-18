package Frontend;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;

public class Board extends TilePane {
  
  public Board() {
    super();
    this.setVgap(20);
    this.setHgap(20);
    this.setPadding(new Insets(20, 10, 20, 20));
    this.setPrefColumns(7);
    this.setTileAlignment(Pos.CENTER);
    this.getStyleClass().add("board");
  }
}
