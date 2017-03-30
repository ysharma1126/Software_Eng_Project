package ui;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.Browser;

public class SetUITest extends Application {
  
  @Override
  public void login(Stage primaryStage) {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    Text scenetitle = new Text("Welcome");
    scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    grid.add(scenetitle, 0, 0, 2, 1);

    Label userName = new Label("User Name:");
    grid.add(userName, 0, 1);

    TextField userTextField = new TextField();
    grid.add(userTextField, 1, 1);

    Label pw = new Label("Password:");
    grid.add(pw, 0, 2);

    PasswordField pwBox = new PasswordField();
    grid.add(pwBox, 1, 2);
    
    Button btn = new Button("Launch");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(btn);
    grid.add(hbBtn, 1, 4);
    
    btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
          openBrowser(primaryStage);
      }
    });
    
    Scene scene = new Scene(grid, 800, 600);
    primaryStage.setTitle("SET");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();
  }
  
  public void start(Stage primaryStage) {
    Browser browser = new Browser();
    Scene scene = new Scene(browser, 800, 600);
    scene.getStylesheets().add("ui/style.css"); 
    scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Montserrat:300,300i,400,400i,500,500i,600,600i,700,700i"); 
    primaryStage.setTitle("SET");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();
  }
  
  public void openGame(Stage primaryStage) {
    Game game = new Game();
    MenuBar menubar = new MenuBar();
    Board board = new Board();
    Controls controls = new Controls();
    
    Card cards[][] = new Card[3][7];
    for (int i = 0; i < cards.length; i++) {
      for (int j = 0; j < cards[0].length; j++) {
         cards[i][j] = new Card(i + ", " + j);
         board.getChildren().add(cards[i][j]);
      }
    }
    
    game.setTop(menubar);
    game.setLeft(controls);
    game.setCenter(board);

    
    Scene scene = new Scene(game, 800, 600);
    scene.getStylesheets().add("ui/style.css"); 
    scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Montserrat:300,300i,400,400i,500,500i,600,600i,700,700i"); 
    primaryStage.setTitle("SET");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();
  }
  
  public static void main(String[] args) {
      launch(args);
  }
}
