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

import gamelogic.Card;
import gamelogic.Player;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import message.ChangedHostResponse;
import message.CreateRoomMessage;
import message.CreateRoomResponse;
import message.EndGameResponse;
import message.GamesUpdateResponse;
import message.InitialCardsResponse;
import message.JoinRoomMessage;
import message.JoinRoomResponse;
import message.LeaveGameResponse;
import message.LeaveRoomMessage;
import message.LeaveRoomResponse;
import message.NewCardsResponse;
import message.RefreshMessage;
import message.SetSelectResponse;
import message.StartGameMessage;
import message.StartGameResponse;
import message.TableResponse;

public class Room extends VBox {

  private Long gid;
  private Boolean is_host;
  private Set<String> players = new HashSet<String>();
  private Task task;
  // private MenuBar menubar;
  private GridPane content;
  private Button startgame_btn;
  private final TableView<UserData> user_tbl = new TableView<>();
  private final ObservableList<UserData> user_data = FXCollections.observableArrayList();

  public void handleJoinRoomResponse(JoinRoomResponse resp) {
    System.out.println("Join room " + this.gid + " " + resp.gid);
    if (this.gid.equals(resp.gid)) {
      players.add(resp.uname);
      user_data.add(new UserData(resp.uname));
      user_tbl.setItems(user_data);  
      System.out.println("Added player");
    }
  }

  public void handleLeaveRoomResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, LeaveRoomResponse resp) {
    players.remove(resp.uname);
    for (UserData u : user_data) {
      if (u.getNameOnly().equals(resp.uname)) {
        user_data.remove(u);
        break;
      }
    }
    System.out.println("Room: Player leaving room");
    System.out.println(user_data.size());
    System.out.println(resp.uname);
    System.out.println(Launcher.username);
    if (resp.uname.equals(Launcher.username)) {
      System.out.println("returning to launcher");
      //task.cancel();
      Launcher.openBrowser(primaryStage, outToServer, inFromServer);
    }
    user_tbl.setItems(user_data);
  }

  public void handleChangedHostResponse(ChangedHostResponse resp) {
    System.out.println(user_data.size());
    System.out.println(resp.currenthost);
    for (UserData u : user_data) {
      System.out.println(u.getNameOnly());
      if (u.getNameOnly().equals(resp.currenthost)) {
        if (Launcher.username.equals(resp.currenthost)) {
          is_host = true;
          startgame_btn.setVisible(true);
        } 
        u.setHost(true);
        user_data.set(user_data.indexOf(u), u);
        break;
      }
    }
    user_tbl.setItems(user_data);
  }

  public void handleStartGameResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, StartGameResponse resp) {
    System.out.println("Room: Game started");
    System.out.println(resp.gid);
    System.out.println(this.gid);
    if (this.gid.equals(resp.gid)) {
      //task.cancel();
      for (String p : this.players) {
        System.out.println("Player: " + p);
      }
      Launcher.openGame(primaryStage, outToServer, inFromServer,
          new ArrayList<String>(players));
    }
  }

  public Room(Stage primaryStage, Long gid, Boolean is_host) {
    this.gid = gid;
    // load_users(outToServer, inFromServer, gid);

    // menubar = new MenuBar();
    content = new GridPane();

    content.setGridLinesVisible(false);
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
    col_name.setCellValueFactory(new PropertyValueFactory<>("name"));

    user_tbl.setItems(user_data);
    user_tbl.getColumns().addAll(col_name);

    // Disable user reordering of columns at runtime
    user_tbl.widthProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> source, Number oldWidth,
          Number newWidth) {
        TableHeaderRow header = (TableHeaderRow) user_tbl.lookup("TableHeaderRow");
        header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
              Boolean newValue) {
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
        // leave_room(primaryStage, outToServer, inFromServer);
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
    startgame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        // start_game(primaryStage, outToServer, inFromServer);
      }
    });

    // Refresh button
    Button refresh_btn = new Button("*");
    refresh_btn.getStyleClass().add("btn-refresh");
    refresh_btn.setPrefWidth(60);
    refresh_btn.setPrefHeight(60);
    content.add(refresh_btn, 1, 2, 1, 1);
    refresh_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        // load_users(outToServer, inFromServer, gid);
      }
    });


    this.getChildren().addAll(content);
    this.getStyleClass().add("browser");
    this.setPadding(new Insets(0, 40, 0, 40));
  }

  public Room(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer,
      Long gid, Player owner, Set<Player> playerlist) {

    this.gid = gid;
    this.getStyleClass().add("room");
    /*** You're the room creator ***/
    if (owner == null) {
      players.add(Launcher.username);
      UserData host = new UserData(Launcher.username);
      host.setHost(true);
      user_data.add(host);
      this.is_host = true;
    } else {
      Iterator<Player> iter = playerlist.iterator();
      String playername;
      while (iter.hasNext()) {
        playername = iter.next().username;
        UserData player = new UserData(playername);
        if (playername.equals(owner.username)) {
          player.setHost(true);
        }
        players.add(playername);
        user_data.add(player);
      }
      // Add yourself
      user_data.add(new UserData(Launcher.username));
      players.add(Launcher.username);
      this.is_host = false;
    }

    // menubar = new MenuBar();
    content = new GridPane();

    content.setGridLinesVisible(false);
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
    col_name.setCellValueFactory(new PropertyValueFactory<>("name"));

    // load_users(outToServer, inFromServer, gid);
    user_tbl.setItems(user_data);
    user_tbl.getColumns().addAll(col_name);

    // Disable user reordering of columns at runtime
    user_tbl.widthProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> source, Number oldWidth,
          Number newWidth) {
        TableHeaderRow header = (TableHeaderRow) user_tbl.lookup("TableHeaderRow");
        header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
              Boolean newValue) {
            header.setReordering(false);
          }
        });
      }
    });

    user_tbl.getStyleClass().add("tbl-user");
    content.add(user_tbl, 0, 0, 3, 2);

    // Leave game button
    Button leavegame_btn = new Button("LEAVE");
    leavegame_btn.getStyleClass().add("btn-leaveroom");
    leavegame_btn.setPrefHeight(60);
    leavegame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        LeaveRoomMessage msg = new LeaveRoomMessage(Launcher.username);
        msg.send(outToServer);
        System.out.println("Room: Leave room button pressed.");
      }
    });
    content.add(leavegame_btn, 0, 2, 1, 1);

    // Start game button
    
    startgame_btn = new Button("START");
    startgame_btn.getStyleClass().add("btn-startgame");
    startgame_btn.setPrefHeight(60);
    content.add(startgame_btn, 2, 2, 1, 1);
    GridPane.setHalignment(startgame_btn, HPos.RIGHT);
    startgame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        StartGameMessage startmsg = new StartGameMessage();
        startmsg.send(outToServer);
        System.out.println("Room: Start game button pressed.");
      }
    });
    
    if (is_host) {
      startgame_btn.setVisible(true);
    } else {
      startgame_btn.setVisible(false);
    }

    this.getChildren().addAll(content);
    this.setPadding(new Insets(0, 40, 0, 40));

//    task = new Task<Void>() {
//      @Override
//      public Void call() throws Exception {
//        System.out.println("Room: Task started.");
//        while (true) {
//          System.out.println("Room: Task looped.");
//          if (isCancelled()) {
//            System.out.println("Room: Task cancelled.");
//            break;
//          }
//          
//          Object obj = null;
//          try {
//            obj = inFromServer.readObject();
//            System.out.println("Room: Object read: " + obj);
//          } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          }
//          System.out.println("Browser: Object read " + obj);
//
//          if (obj instanceof JoinRoomResponse) {
//            JoinRoomResponse jr_resp = (JoinRoomResponse) obj;
//            Platform.runLater(new Runnable() {
//              @Override
//              public void run() {
//                System.out.println("Room: handling " + jr_resp);
//                handleJoinRoomResponse(jr_resp);
//              }
//            });
//          }
//
//          if (obj instanceof LeaveRoomResponse) {
//            System.out.println("Got leave room response.");
//            LeaveRoomResponse lr_resp = (LeaveRoomResponse) obj;
//            Platform.runLater(new Runnable() {
//              @Override
//              public void run() {
//                System.out.println("Room: handling " + lr_resp);
//                handleLeaveRoomResponse(primaryStage, outToServer, inFromServer, lr_resp);
//              }
//            });
//          }
//
//          if (obj instanceof ChangedHostResponse) {
//            ChangedHostResponse ch_resp = (ChangedHostResponse) obj;
//            Platform.runLater(new Runnable() {
//              @Override
//              public void run() {
//                System.out.println("Room: handling " + ch_resp);
//                handleChangedHostResponse(ch_resp);
//              }
//            });
//          }
//
//          if (obj instanceof StartGameResponse) {
//            StartGameResponse sg_resp = (StartGameResponse) obj;
//            Platform.runLater(new Runnable() {
//              @Override
//              public void run() {
//                System.out.println("Room: handling " + sg_resp);
//                handleStartGameResponse(primaryStage, outToServer, inFromServer, sg_resp);
//              }
//            });
//          }
//        }
//
//        return null;
//
//      }
//    };

    Thread th = new Thread(task);
    th.setDaemon(true);
    th.start();


  }

  public static class UserData {
    private final SimpleStringProperty user_name;
    private Boolean user_is_host;

    private UserData(String name) {
      this.user_name = new SimpleStringProperty(name);
      this.user_is_host = false;
    }

    public String getName() {
      if (this.user_is_host) {
        return user_name.get() + " [host]";
      } else {
        return user_name.get();
      }
    }
    
    public String getNameOnly() {
      return user_name.get();
    }

    public void setName(String name) {
      user_name.set(name);
    }

    public Boolean getHost() {
      return this.user_is_host;
    }

    public void setHost(Boolean is_host) {
      this.user_is_host = is_host;
    }
  }

}
