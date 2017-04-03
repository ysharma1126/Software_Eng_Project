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
  private Boolean isHost;
  private Task task;
  private Set<Player> players = new HashSet<Player>();
  // private MenuBar menubar;
  private GridPane content;
  private final TableView<UserData> user_tbl = new TableView<>();
  private final ObservableList<UserData> user_data = FXCollections.observableArrayList();

  // private void load_users(ObjectOutputStream outToServer, ObjectInputStream inFromServer, Long
  // gid) {
  // RefreshMessage msg = new RefreshMessage(Launcher.username);
  // System.out.println("Refresh message sent in Room");
  // msg.send(outToServer);
  // try {
  // this.user_data.clear();
  // GamesUpdateResponse response = (GamesUpdateResponse) inFromServer.readObject();
  // System.out.println("GamesUpdateResponse received in Room");
  // Set<Player> players = response.gameusernames.get(gid);
  // Player owner = response.gamehost.get(gid);
  // Iterator<Player> it = players.iterator();
  // while (it.hasNext()) {
  // String name = new String();
  // Player player = it.next();
  // if (player.username.equals(owner.username)) {
  // name = player.username + "*";
  // } else {
  // name = player.username;
  // }
  // this.user_data.add(new UserData(name));
  // it.remove();
  // }
  // System.out.println("user_data updated");
  // } catch (ClassNotFoundException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }

  // private void start_game(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream
  // inFromServer) {
  // StartGameMessage msg = new StartGameMessage();
  // msg.send(outToServer);
  // try {
  // StartGameResponse start = (StartGameResponse) inFromServer.readObject();
  // System.out.println("Game started");
  // Launcher.openGame(primaryStage, outToServer, inFromServer, new
  // ArrayList<Player>(this.players));
  // } catch (ClassNotFoundException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }

  // private void leave_room(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream
  // inFromServer) {
  // LeaveRoomMessage msg = new LeaveRoomMessage(Launcher.username);
  // msg.send(outToServer);
  // try {
  // CreateRoomResponse response = (CreateRoomResponse) inFromServer.readObject();
  // Launcher.openBrowser(primaryStage, outToServer, inFromServer);
  // } catch (ClassNotFoundException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }

  private void handleJoinRoomResponse(JoinRoomResponse resp) {
    this.players.add(new Player(resp.uname));
    this.user_data.add(new UserData(resp.uname));
    user_tbl.setItems(this.user_data);
  }

  private void handleLeaveRoomResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, LeaveRoomResponse resp) {
    System.out.println("left room");
    this.players.remove(new Player(resp.uname));
    this.user_data.remove(new UserData(resp.uname));
    System.out.println(resp.uname);
    System.out.println(Launcher.username);
    if (resp.uname.equals(Launcher.username)) {
      System.out.println("returning to launcher");
      this.task.cancel();
      Launcher.openBrowser(primaryStage, outToServer, inFromServer);
    }
    user_tbl.setItems(this.user_data);
  }

  private void handleChangedHostResponse(ChangedHostResponse resp) {
    System.out.println("handling changed host");
    this.user_data.get(0).setHost(true);
    user_tbl.setItems(this.user_data);
  }

  private void handleStartGameResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, StartGameResponse resp) {
    System.out.println("Game started");
    System.out.println(resp.gid);
    System.out.println(this.gid);
    if (resp.gid == this.gid) {
      this.task.cancel();
      Launcher.openGame(primaryStage, outToServer, inFromServer,
          new ArrayList<Player>(this.players));
    }
  }

  public Room(Stage primaryStage, Long gid, Boolean isHost) {
    this.gid = gid;
    // load_users(outToServer, inFromServer, gid);

    // menubar = new MenuBar();
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
    col_name.setCellValueFactory(new PropertyValueFactory<>("name"));


    // Thread server_response_handler = null;
    // try {
    // //server_response_handler = new Thread(new RoomThread(primaryStage, outToServer,
    // inFromServer));
    // } catch (IOException e2) {
    // // TODO Auto-generated catch block
    // e2.printStackTrace();
    // }
    // server_response_handler.start();

    // load_users(outToServer, inFromServer, gid);
    user_tbl.setItems(this.user_data);
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
      Long gid, Boolean isHost) {
    System.out.println(gid);
    this.gid = gid;
    this.isHost = isHost;
    if (isHost) {
      this.players.add(new Player(Launcher.username));
      UserData host = new UserData(Launcher.username);
      host.setHost(true);
      this.user_data.add(host);
    }

    // load_users(outToServer, inFromServer, gid);
    System.out.println("Users loaded");

    // menubar = new MenuBar();
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
    col_name.setCellValueFactory(new PropertyValueFactory<>("name"));


    // Thread server_response_handler = null;
    // try {
    // //server_response_handler = new Thread(new RoomThread(primaryStage, outToServer,
    // inFromServer));
    // } catch (IOException e2) {
    // // TODO Auto-generated catch block
    // e2.printStackTrace();
    // }
    // server_response_handler.start();

    // load_users(outToServer, inFromServer, gid);
    user_tbl.setItems(this.user_data);
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
        LeaveRoomMessage msg = new LeaveRoomMessage(Launcher.username);
        msg.send(outToServer);
        // leave_room(primaryStage, outToServer, inFromServer);
        System.out.println("Left room");
      }
    });
    content.add(leavegame_btn, 0, 2, 1, 1);

    // Start game button
    if (this.isHost) {
      Button startgame_btn = new Button("START");
      startgame_btn.getStyleClass().add("btn-newgame");
      startgame_btn.setPrefHeight(60);
      content.add(startgame_btn, 2, 2, 1, 1);
      GridPane.setHalignment(startgame_btn, HPos.RIGHT);
      startgame_btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          StartGameMessage startmsg = new StartGameMessage();
          startmsg.send(outToServer);
          // start_game(primaryStage, outToServer, inFromServer);
          System.out.println("start game message sent");
        }
      });
    } ;

    // // Refresh button
    // Button refresh_btn = new Button("*");
    // refresh_btn.getStyleClass().add("btn-refresh");
    // refresh_btn.setPrefWidth(60);
    // refresh_btn.setPrefHeight(60);
    // content.add(refresh_btn, 1, 2, 1, 1);
    // refresh_btn.setOnAction(new EventHandler<ActionEvent>(){
    // @Override
    // public void handle(ActionEvent e) {
    // load_users(outToServer, inFromServer, gid);
    // System.out.println("Users loaded");
    // }
    // });


    this.getChildren().addAll(content);
    this.getStyleClass().add("browser");
    this.setPadding(new Insets(0, 40, 0, 40));

    task = new Task<Void>() {
      @Override
      public Void call() throws Exception {
        System.out.println("Room task created");
        while (true) {
          if (isCancelled()) break;
          
          Object obj = null;
          try {
            obj = inFromServer.readObject();
            System.out.println(obj);
          } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

          if (obj instanceof JoinRoomResponse) {
            JoinRoomResponse jr_resp = (JoinRoomResponse) obj;
            System.out.println("Got join room");
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                handleJoinRoomResponse(jr_resp);
              }
            });
          }

          if (obj instanceof LeaveRoomResponse) {
            System.out.println("Got leave room response.");
            LeaveRoomResponse lr_resp = (LeaveRoomResponse) obj;
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                handleLeaveRoomResponse(primaryStage, outToServer, inFromServer, lr_resp);
              }
            });
          }

          if (obj instanceof ChangedHostResponse) {
            System.out.println("Got changedhost response.");
            ChangedHostResponse ch_resp = (ChangedHostResponse) obj;
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                handleChangedHostResponse(ch_resp);
              }
            });
          }

          if (obj instanceof StartGameResponse) {
            System.out.println("Got startgame response.");
            StartGameResponse sg_resp = (StartGameResponse) obj;
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                handleStartGameResponse(primaryStage, outToServer, inFromServer, sg_resp);
              }
            });
          }
        }

        return null;

      }
    };

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
