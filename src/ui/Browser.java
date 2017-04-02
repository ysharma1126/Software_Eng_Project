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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import message.CreateRoomMessage;
import message.CreateRoomResponse;
import message.GamesUpdateResponse;
import message.InitialCardsResponse;
import message.JoinRoomMessage;
import message.JoinRoomResponse;
import message.RefreshMessage;

public class Browser extends VBox {
  
  private MenuBar menubar;
  private GridPane content;
  private final TableView<GameData> game_tbl = new TableView<>();
  private final ObservableList<GameData> game_data = FXCollections.observableArrayList ();
  
  private void load_available_rooms(ObjectOutputStream outToServer, ObjectInputStream inFromServer) {
    RefreshMessage msg = new RefreshMessage(Launcher.username);
    msg.send(outToServer);
    try {
      this.game_data.clear();
      GamesUpdateResponse response = (GamesUpdateResponse) inFromServer.readObject();
      Iterator<Entry<Integer, Set<Player>>> it1 = response.gameusernames.entrySet().iterator();
      Iterator<Entry<Integer, Player>> it2 = response.gamehost.entrySet().iterator();
      while (it1.hasNext() && it2.hasNext()) {
        Map.Entry<Integer, Set<Player>> room = (Map.Entry<Integer, Set<Player>>) it1.next();
        Map.Entry<Integer, Player> host = (Map.Entry<Integer, Player>) it2.next();
        String gid = "Game " + room.getKey();
        String leader = host.getValue().username;
        String players = room.getValue().size() + "/10";
        this.game_data.add(new GameData(gid, leader, players));
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
  
  private void join_room(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer, Integer gid) {
    JoinRoomMessage msg = new JoinRoomMessage(Launcher.username, gid);
    msg.send(outToServer);
    try {
      JoinRoomResponse response = (JoinRoomResponse) inFromServer.readObject();
      Launcher.openRoom(primaryStage, outToServer, inFromServer, gid);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void create_room(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer, String name) {
    CreateRoomMessage msg = new CreateRoomMessage(Launcher.username, name);
    msg.send(outToServer);
    try {
      CreateRoomResponse response = (CreateRoomResponse) inFromServer.readObject();
      Launcher.openRoom(primaryStage, outToServer, inFromServer, response.gid);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

//  public Browser(Stage primaryStage) {
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
    
//    Thread server_response_handler = null;
//    try {
//      server_response_handler = new Thread(new BrowserThread(primaryStage, outToServer, inFromServer));
//    } catch (IOException e2) {
//      // TODO Auto-generated catch block
//      e2.printStackTrace();
//    }
//    server_response_handler.start();
    
    //load_available_rooms(outToServer, inFromServer);
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
    
    content.add(game_tbl, 0, 0, 3, 2);
    
    // Room name
    TextField name_input = new TextField ();
    content.add(name_input, 1, 2, 1, 1);
    
    // Create game button
    Button newgame_btn = new Button("NEW GAME");
    newgame_btn.getStyleClass().add("btn-newgame");
    newgame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        if ((name_input.getText() != null && !name_input.getText().isEmpty())) {
            create_room(primaryStage, outToServer, inFromServer, name_input.getText());
        }
      }
    });
    content.add(newgame_btn, 0, 2, 1, 1);
    

    
    // Create refresh button
    Button refresh_btn = new Button("*");
    refresh_btn.getStyleClass().add("btn-refresh");
    content.add(refresh_btn, 2, 2, 1, 1);
    refresh_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        load_available_rooms(outToServer, inFromServer);
      }
    });
    
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
    content.add(userinfo, 3, 0, 1, 1);
        
    // Online Users / chat
    
    this.getChildren().addAll(content);
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
