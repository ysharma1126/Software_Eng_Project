package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
import javafx.geometry.HPos;
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
import message.LeaveRoomMessage;
import message.RefreshMessage;
import message.StartGameMessage;
import message.StartGameResponse;

public class Room extends VBox {
  
  private Integer gid;
  private Set<Player> players = new HashSet<Player>();
  // private MenuBar menubar;
  private GridPane content;
  private final TableView<UserData> user_tbl = new TableView<>();
  private final ObservableList<UserData> user_data = FXCollections.observableArrayList ();
  
  private void load_users(ObjectOutputStream outToServer, ObjectInputStream inFromServer, Integer gid) {
    RefreshMessage msg = new RefreshMessage(Launcher.username);
    System.out.println("Refresh message sent in Room");
    msg.send(outToServer);
    try {
      this.user_data.clear();
      GamesUpdateResponse response = (GamesUpdateResponse) inFromServer.readObject();
      System.out.println("GamesUpdateResponse received in Room");
      Set<Player> players = response.gameusernames.get(gid);
      Player owner = response.gamehost.get(gid);
      Iterator<Player> it = players.iterator();
      while (it.hasNext()) {
        String name = new String();
        Player player = it.next();
        if (player.username.equals(owner.username)) {
          name = player.username + "*";
        } else {
          name = player.username;
        }
        this.user_data.add(new UserData(name));
        it.remove();
      }
      System.out.println("user_data updated");
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void start_game(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer) {
    // RefreshMessage msg = new RefreshMessage(Launcher.username);
    // msg.send(outToServer);
    
    StartGameMessage msg2 = new StartGameMessage();
    msg2.send(outToServer);

    try {  
      // GamesUpdateResponse response = (GamesUpdateResponse) inFromServer.readObject();
      // Set<Player> players = response.gameusernames.get(gid);
      StartGameResponse start = (StartGameResponse) inFromServer.readObject();
      Launcher.openGame(primaryStage, outToServer, inFromServer, new ArrayList<Player>(players));
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void leave_room(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer) {
    LeaveRoomMessage msg = new LeaveRoomMessage(Launcher.username);
    msg.send(outToServer);
    try {
      CreateRoomResponse response = (CreateRoomResponse) inFromServer.readObject();
      Launcher.openBrowser(primaryStage, outToServer, inFromServer);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public Room(Stage primaryStage, Integer gid) {
    this.gid = gid;
    // load_users(outToServer, inFromServer, gid);
    
    //menubar = new MenuBar();
    content = new GridPane();
    
    content.setGridLinesVisible(true);
    content.setHgap(30);
    content.setVgap(30);
    content.setPadding(new Insets(45, 10, 45, 10));
    
    // Active game browser        
    user_tbl.setEditable(false);
    user_tbl.setPrefWidth(700);
    user_tbl.setPrefHeight(420);
    user_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
    TableColumn<UserData, String> col_name = new TableColumn<>("PLAYER");
    col_name.setPrefWidth(685);
    col_name.setResizable(false);
    col_name.setCellValueFactory(
        new PropertyValueFactory<>("name"));
    
    
//    Thread server_response_handler = null;
//    try {
//      //server_response_handler = new Thread(new RoomThread(primaryStage, outToServer, inFromServer));
//    } catch (IOException e2) {
//      // TODO Auto-generated catch block
//      e2.printStackTrace();
//    }
//    server_response_handler.start();
    
    //load_users(outToServer, inFromServer, gid);
    user_tbl.setItems(this.user_data);
    user_tbl.getColumns().addAll(col_name);
    
    // Disable user reordering of columns at runtime
    user_tbl.widthProperty().addListener(new ChangeListener<Number>()
    {
        @Override
        public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
        {
            TableHeaderRow header = (TableHeaderRow) user_tbl.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    header.setReordering(false);
                }
            });
        }
    });
    
    user_tbl.getStyleClass().add("tbl-user");
    
    content.add(user_tbl, 0, 0, 3, 2);
    
    // Leave game button
    Button leavegame_btn = new Button("LEAVE");
    leavegame_btn.getStyleClass().add("btn-newgame");
    leavegame_btn.setPrefHeight(60);
    leavegame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
//        leave_room(primaryStage, outToServer, inFromServer);
      }
    });
    content.add(leavegame_btn, 0, 2, 1, 1);
   
    // Start game button
    Button startgame_btn = new Button("START");
    startgame_btn.setPrefWidth(120);
    startgame_btn.setPrefHeight(60);
    startgame_btn.getStyleClass().add("btn-newgame");
    content.add(startgame_btn, 2, 2, 1, 1);
    GridPane.setHalignment(startgame_btn, HPos.RIGHT);
    startgame_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
//        start_game(primaryStage, outToServer, inFromServer);
      }
    });
    
    // Refresh button
    Button refresh_btn = new Button("*");
    refresh_btn.getStyleClass().add("btn-refresh");
    refresh_btn.setPrefWidth(60);
    refresh_btn.setPrefHeight(60);
    content.add(refresh_btn, 1, 2, 1, 1);
    refresh_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
//        load_users(outToServer, inFromServer, gid);
      }
    });
    
    
    this.getChildren().addAll(content);
    this.getStyleClass().add("browser");
    this.setPadding(new Insets(0, 40, 0, 40));
  }
  
public Room(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer, Integer gid) {
    System.out.println(gid);
    this.gid = gid;
    this.players.add(new Player(Launcher.username));
    
    //load_users(outToServer, inFromServer, gid);
    System.out.println("Users loaded");
    
    //menubar = new MenuBar();
    content = new GridPane();
    
    content.setGridLinesVisible(true);
    content.setHgap(30);
    content.setVgap(30);
    content.setPadding(new Insets(45, 10, 45, 10));
    
    // Active game browser        
    user_tbl.setEditable(false);
    user_tbl.setPrefWidth(700);
    user_tbl.setPrefHeight(420);
    user_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
    TableColumn<UserData, String> col_name = new TableColumn<>("PLAYER");
    col_name.setPrefWidth(685);
    col_name.setResizable(false);
    col_name.setCellValueFactory(
        new PropertyValueFactory<>("name"));
    
    
//    Thread server_response_handler = null;
//    try {
//      //server_response_handler = new Thread(new RoomThread(primaryStage, outToServer, inFromServer));
//    } catch (IOException e2) {
//      // TODO Auto-generated catch block
//      e2.printStackTrace();
//    }
//    server_response_handler.start();
    
    //load_users(outToServer, inFromServer, gid);
    user_tbl.setItems(this.user_data);
    user_tbl.getColumns().addAll(col_name);
    
    // Disable user reordering of columns at runtime
    user_tbl.widthProperty().addListener(new ChangeListener<Number>()
    {
        @Override
        public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
        {
            TableHeaderRow header = (TableHeaderRow) user_tbl.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    header.setReordering(false);
                }
            });
        }
    });
    
    user_tbl.getStyleClass().add("tbl-user");
    
    content.add(user_tbl, 0, 0, 3, 2);
    
    // Leave game button
    Button leavegame_btn = new Button("LEAVE");
    leavegame_btn.getStyleClass().add("btn-newgame");
    leavegame_btn.setPrefHeight(60);
    leavegame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        leave_room(primaryStage, outToServer, inFromServer);
        System.out.println("Left room");
      }
    });
    content.add(leavegame_btn, 0, 2, 1, 1);
   
    // Start game button
    Button startgame_btn = new Button("START");
    startgame_btn.getStyleClass().add("btn-newgame");
    startgame_btn.setPrefHeight(60);
    content.add(startgame_btn, 2, 2, 1, 1);
    GridPane.setHalignment(startgame_btn, HPos.RIGHT);
    startgame_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        start_game(primaryStage, outToServer, inFromServer);
        System.out.println("Game started");
      }
    });
    
    // Refresh button
    Button refresh_btn = new Button("*");
    refresh_btn.getStyleClass().add("btn-refresh");
    refresh_btn.setPrefWidth(60);
    refresh_btn.setPrefHeight(60);
    content.add(refresh_btn, 1, 2, 1, 1);
    refresh_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        load_users(outToServer, inFromServer, gid);
        System.out.println("Users loaded");
      }
    });
    
    
    this.getChildren().addAll(content);
    this.getStyleClass().add("browser");
    this.setPadding(new Insets(0, 40, 0, 40));
  }

  public static class UserData {
    private final SimpleStringProperty user_name;
    
    private UserData(String name) {
      this.user_name = new SimpleStringProperty(name);
    }
    
    public String getName()                {return user_name.get();}
    public void setName(String name)       {user_name.set(name);}
  }
  
}
