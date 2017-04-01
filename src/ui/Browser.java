package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import gamelogic.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import message.InitialCardsResponse;
import message.LoadRoomsMessage;
import message.LoadRoomsResponse;

public class Browser extends VBox {
  
  private MenuBar menubar;
  private GridPane content;
  private final TableView<GameData> game_tbl = new TableView<>();
  private final ObservableList<GameData> game_data = FXCollections.observableArrayList ();
  
  private void load_available_rooms(ObjectOutputStream outToServer, ObjectInputStream inFromServer) {
    LoadRoomsMessage msg = new LoadRoomsMessage();
    msg.send(outToServer);
    try {
      // Read existing rooms as ArrayList of strings
      LoadRoomsResponse response = (LoadRoomsResponse) inFromServer.readObject();
      Iterator<Entry<Integer, Set<Player>>> it1 = response.rooms.entrySet().iterator();
      Iterator<Entry<Integer, Player>> it2 = response.hosts.entrySet().iterator();
      while (it1.hasNext() && it2.hasNext()) {
        Map.Entry room = (Map.Entry) it1.next();
        Map.Entry host = (Map.Entry) it1.next();
        String gid = "Game " + room.getKey();
        String players = ((Set) room.getValue()).size() + "/10";
        String leader = ((Player) host.getValue()).username;
        game_data.add(new GameData(gid, players, leader));
        it1.remove();
        it2.remove();
      }
      
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public Browser(Stage primaryStage) {
    
    menubar = new MenuBar();
    content = new GridPane();
    
    content.setGridLinesVisible(true);
    content.setHgap(30);
    content.setVgap(30);
    content.setPadding(new Insets(0, 10, 0, 10));
    
    // Active game browser        
    game_tbl.setEditable(false);
    game_tbl.setPrefWidth(460);
    game_tbl.setPrefHeight(420);
    game_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
    TableColumn<GameData, String> col_name = new TableColumn<>("NAME");
    col_name.setPrefWidth(160);
    col_name.setResizable(false);
    col_name.setCellValueFactory(
        new PropertyValueFactory<>("Name"));
    
    TableColumn<GameData, String> col_owner = new TableColumn<>("OWNER");
    col_owner.setPrefWidth(120);
    col_owner.setResizable(false);
    col_owner.setCellValueFactory(
        new PropertyValueFactory<>("Owner"));
    
    TableColumn<GameData, String> col_players = new TableColumn<>("PLAYERS");
    col_players.setPrefWidth(80);
    col_players.setResizable(false);
    col_players.setCellValueFactory(
        new PropertyValueFactory<>("Players"));
    
//    /* Local testing */
//    HashMap<Integer, Set<Player>> rooms = new HashMap<Integer, Set<Player>>();
//    HashMap<Integer, Player> hosts = new HashMap<Integer, Player>();
//    
//    Set<Player> testset = new HashSet<Player>();
//    Player user1 = new Player("User 1");
//    Player user2 = new Player("User 2");
//    Player user3 = new Player("User 3");
//    
//    testset.add(user1);
//    testset.add(user2);
//    testset.add(user3);
//    rooms.put(1, testset);
//    rooms.put(4, testset);
//    rooms.put(2, testset);
//    rooms.put(3, testset);
//    rooms.put(5, testset);
//    
//    hosts.put(1, user1);
//    hosts.put(4, user2);
//    hosts.put(2, user3);
//    hosts.put(3, user2);
//    hosts.put(5, user1);
//    
//    Iterator<Entry<Integer, Set<Player>>> it1 = rooms.entrySet().iterator();
//    Iterator<Entry<Integer, Player>> it2 = hosts.entrySet().iterator();
//    while (it1.hasNext() && it2.hasNext()) {
//      Map.Entry<Integer, Set<Player>> room = (Map.Entry<Integer, Set<Player>>) it1.next();
//      Map.Entry<Integer, Player> host = (Map.Entry<Integer, Player>) it2.next();
//      String gid = "Game " + room.getKey();
//      String leader = host.getValue().username;
//      String players = room.getValue().size() + "/10";
//      this.game_data.add(new GameData(gid, leader, players));
//      it1.remove();
//      it2.remove();
//    }
    
    game_tbl.setItems(this.game_data);
    game_tbl.getColumns().addAll(col_name, col_owner, col_players);
    
    // Disable user reordering of columns at runtime
    game_tbl.widthProperty().addListener(new ChangeListener<Number>()
    {
        @Override
        public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
        {
            TableHeaderRow header = (TableHeaderRow) game_tbl.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    header.setReordering(false);
                }
            });
        }
    });
    
    game_tbl.getStyleClass().add("tbl-game");
    
    content.add(game_tbl, 0, 0, 1, 2);
    
    final Popup popup = new Popup(); 
    popup.setX(1000); 
    popup.setY(1000);
    popup.getContent().addAll(new Label("ASDASDASD"));
    // Create game button
    Button newgame_btn = new Button("NEW GAME");
    newgame_btn.getStyleClass().add("btn-newgame");
    newgame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        popup.show(primaryStage);
      }
    });
    
    
    content.add(newgame_btn, 0, 2, 1, 1);
    
    // User info
    GridPane userinfo = new GridPane();
    userinfo.setGridLinesVisible(true);
    userinfo.setPrefWidth(230);
    userinfo.setPrefHeight(120);
    userinfo.setHgap(10);
    userinfo.setVgap(10);
    userinfo.setPadding(new Insets(0, 10, 0, 10));
    
    ImageView user_avatar = new ImageView();
    Label user_name = new Label(Launcher.username);
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
    this.setPadding(new Insets(0, 40, 0, 40));
  }
  
  public Browser(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer) {
    
    menubar = new MenuBar();
    content = new GridPane();
    
    content.setGridLinesVisible(true);
    content.setHgap(30);
    content.setVgap(30);
    content.setPadding(new Insets(0, 10, 0, 10));
    
    // Active game browser        
    game_tbl.setEditable(false);
    game_tbl.setPrefWidth(460);
    game_tbl.setPrefHeight(420);
    game_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
    TableColumn<GameData, String> col_name = new TableColumn<>("NAME");
    col_name.setPrefWidth(160);
    col_name.setResizable(false);
    col_name.setCellValueFactory(
        new PropertyValueFactory<>("Name"));
    
    TableColumn<GameData, String> col_owner = new TableColumn<>("OWNER");
    col_owner.setPrefWidth(120);
    col_owner.setResizable(false);
    col_owner.setCellValueFactory(
        new PropertyValueFactory<>("Owner"));
    
    TableColumn<GameData, String> col_players = new TableColumn<>("PLAYERS");
    col_players.setPrefWidth(80);
    col_players.setResizable(false);
    col_players.setCellValueFactory(
        new PropertyValueFactory<>("Players"));
    
    Thread server_response_handler = null;
    try {
      server_response_handler = new Thread(new BrowserThread(primaryStage, outToServer, inFromServer));
    } catch (IOException e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }
    server_response_handler.start();
    
    load_available_rooms(outToServer, inFromServer);
    game_tbl.setItems(game_data);
    game_tbl.getColumns().addAll(col_name, col_owner, col_players);
    
    // Disable user reordering of columns at runtime
    game_tbl.widthProperty().addListener(new ChangeListener<Number>()
    {
        @Override
        public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
        {
            TableHeaderRow header = (TableHeaderRow) game_tbl.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    header.setReordering(false);
                }
            });
        }
    });
    
    game_tbl.getStyleClass().add("tbl-game");
    
    content.add(game_tbl, 0, 0, 1, 2);
    
    // Create game button
    Button newgame_btn = new Button("NEW GAME");
    newgame_btn.getStyleClass().add("btn-newgame");
    content.add(newgame_btn, 0, 2, 1, 1);
    
    // User info
    GridPane userinfo = new GridPane();
    userinfo.setGridLinesVisible(true);
    userinfo.setPrefWidth(230);
    userinfo.setPrefHeight(120);
    userinfo.setHgap(10);
    userinfo.setVgap(10);
    userinfo.setPadding(new Insets(0, 10, 0, 10));
    
//    ImageView user_avatar = new ImageView();
    Label user_name = new Label(Launcher.username);
//    HBox user_wins = new HBox();
//    
//    Label ngold = new Label("4");
//    Label nsilver = new Label("2");
//    Label nbronze = new Label("0");
//    
//    user_wins.getChildren().addAll(ngold, nsilver, nbronze);
//    userinfo.add(user_avatar, 0, 0, 1, 1);
    userinfo.add(user_name, 1, 0, 1, 1);
//    userinfo.add(user_wins, 0, 1, 2, 1);
    userinfo.getStyleClass().add("user");
    content.add(userinfo, 1, 0, 1, 1);
        
    // Online Users / chat
    
    this.getChildren().addAll(menubar, content);
    this.getStyleClass().add("browser");
    this.setPadding(new Insets(0, 40, 0, 40));
  }

  public static class GameData {
    private final SimpleStringProperty game_name;
    private final SimpleStringProperty game_owner;
    private final SimpleStringProperty game_players;
    
    private GameData(String name, String owner, String players) {
      this.game_name = new SimpleStringProperty(name);
      this.game_owner = new SimpleStringProperty(owner);
      this.game_players = new SimpleStringProperty(players);
    }
    
    public String getName()                {return game_name.get();}
    public void setName(String name)       {game_name.set(name);}
    public String getOwner()               {return game_owner.get();}
    public void setOwner(String owner)     {game_owner.set(owner);}
    public String getPlayers()             {return game_players.get();}
    public void setPlayers(String players) {game_players.set(players);}
  }
  
}
