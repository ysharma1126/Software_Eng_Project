package Frontend;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;


public class Browser extends VBox {
  
  private MenuBar menubar;
  private GridPane content;
  
  public Browser() {
    
    menubar = new MenuBar();
    content = new GridPane();
    
    content.setGridLinesVisible(true);
    content.setHgap(10);
    content.setVgap(10);
    content.setPadding(new Insets(0, 10, 0, 10));
    
    // Active game browser
    TableView<GameData> game_tbl = new TableView<>();
    ObservableList<GameData> game_data = 
        FXCollections.observableArrayList (
            new GameData("game1", "poop", "8/10", "2 hours ago"),
            new GameData("game2", "oop", "8/10", "3 hours ago"),
            new GameData("game3", "asd", "8/10", "4 hours ago"),
            new GameData("game4", "231", "8/10", "5 hours ago"),
            new GameData("game5", "asdfa", "8/10", "6 hours ago"),
            new GameData("game6", "123", "8/10", "7 hours ago")
         );
        
    game_tbl.setEditable(true);


    TableColumn col_name = new TableColumn("Name");
    col_name.setMinWidth(200);
    col_name.setMaxWidth(200);
    col_name.setResizable(false);
    col_name.setCellValueFactory(
        new PropertyValueFactory<GameData, String>("game_name"));
    
    TableColumn col_owner = new TableColumn("Owner");
    col_name.setMinWidth(200);
    col_name.setMaxWidth(200);
    col_name.setCellValueFactory(
        new PropertyValueFactory<>("game_owner"));
    
    TableColumn col_players = new TableColumn("Players");
    col_name.setMinWidth(200);
    col_name.setMaxWidth(200);
    col_name.setCellValueFactory(
        new PropertyValueFactory<>("game_players"));
    
    TableColumn col_time = new TableColumn("Created");
    col_name.setMinWidth(200);
    col_name.setMaxWidth(200);
    col_name.setCellValueFactory(
        new PropertyValueFactory<>("game_time"));
    
    game_tbl.setItems(game_data);
    game_tbl.getColumns().addAll(col_name, col_owner, col_players, col_time);
    
    game_tbl.getStyleClass().add("tbl-game");
    
    content.add(game_tbl, 0, 0, 1, 1);
    
    // Create game button
    Button newgame_btn = new Button("NEW GAME");
    newgame_btn.getStyleClass().add("btn-newgame");
    content.add(newgame_btn, 0, 1, 1, 1);
    // User info
    GridPane userinfo = new GridPane();
    userinfo.setHgap(10);
    userinfo.setVgap(10);
    userinfo.setPadding(new Insets(0, 10, 0, 10));
    
    ImageView user_avatar = new ImageView();
    Label user_name = new Label("username");
    HBox user_wins = new HBox();
    
    Label ngold = new Label("4");
    Label nsilver = new Label("2");
    Label nbronze = new Label("0");
    
    user_wins.getChildren().addAll(ngold, nsilver, nbronze);

    userinfo.add(user_avatar, 0, 0, 1, 1);
    userinfo.add(user_name, 1, 0, 1, 1);
    userinfo.add(user_wins, 0, 1, 2, 1);
    userinfo.getStyleClass().add("user");
    content.add(userinfo, 1, 0, 1, 1);
        
    // Online Users / chat
    
    this.getChildren().addAll(menubar, content);
    this.getStyleClass().add("browser");
  }
  
  public static class GameData {
    private final SimpleStringProperty game_name;
    private final SimpleStringProperty game_owner;
    private final SimpleStringProperty game_players;
    private final SimpleStringProperty game_time;
    
    private GameData(String name, String owner, String players, String time) {
      this.game_name = new SimpleStringProperty(name);
      this.game_owner = new SimpleStringProperty(owner);
      this.game_players = new SimpleStringProperty(players);
      this.game_time = new SimpleStringProperty(time);
    }
    
    public String getName() {
      return game_name.get();
    }
    
    public void setName(String name) {
      game_name.set(name);
    }
    
    public String getOwner() {
      return game_owner.get();
    }
    
    public void setOwner(String owner) {
      game_owner.set(owner);
    }
    
    public String getPlayers() {
      return game_players.get();
    }
    
    public void setPlayers(String players) {
      game_players.set(players);
    }
    
    public String getTime() {
      return game_time.get();
    }
    
    public void setTime(String time) {
      game_time.set(time);
    }
  }
  
}
