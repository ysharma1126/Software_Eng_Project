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

import gamelogic.Card;
import gamelogic.Player;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
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
import message.CreateRoomMessage;
import message.CreateRoomResponse;
import message.EndGameResponse;
import message.GamesUpdateMessage;
import message.GamesUpdateResponse;
import message.InitialCardsResponse;
import message.JoinRoomMessage;
import message.JoinRoomResponse;
import message.LeaveGameResponse;
import message.NewCardsResponse;
import message.RefreshMessage;
import message.SetSelectResponse;
import message.StartGameResponse;
import message.TableResponse;

public class Browser extends VBox {

  private MenuBar menubar;
  private GridPane content;
  private Task task;
  private final TableView<GameData> game_tbl = new TableView<>();
  private final ObservableList<GameData> game_data = FXCollections.observableArrayList();
  private final TableView<UserData> user_tbl = new TableView<>();
  private final ObservableList<UserData> user_data = FXCollections.observableArrayList();

  private void handleGamesUpdateResponse(GamesUpdateResponse resp) {
    game_data.clear();
    user_data.clear();

    Iterator<Entry<Long, Set<Player>>> it1 = resp.gameusernames.entrySet().iterator();
    Iterator<Entry<Long, Player>> it2 = resp.gamehost.entrySet().iterator();
    while (it1.hasNext() && it2.hasNext()) {
      Map.Entry<Long, Set<Player>> room = (Map.Entry<Long, Set<Player>>) it1.next();
      Map.Entry<Long, Player> host = (Map.Entry<Long, Player>) it2.next();
      Long gid = room.getKey();
      String name = "Game " + room.getKey();
      String players = room.getValue().size() + "/10";
      String leader = host.getValue().username;
      game_data.add(new GameData(gid, name, players, leader));
      it1.remove();
      it2.remove();
    }

    Iterator<Player> it3 = resp.players.iterator();
    while (it3.hasNext()) {
      user_data.add(new UserData(it3.next().username));
      it3.remove();
    }
  }

  private void handleJoinRoomResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, JoinRoomResponse resp) {
    if (resp.uname.equals(Launcher.username)) {
      //task.cancel();
      Launcher.openRoom(primaryStage, outToServer, inFromServer, resp.gid, false);
    }
  }

  private void handleCreateRoomResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, CreateRoomResponse resp) {
    System.out.println("Created Room");
    System.out.println(resp.gid);
    System.out.println(resp.uname);
    System.out.println(Launcher.username);
    if (resp.uname.equals(Launcher.username)) {
      //task.cancel();
      Launcher.openRoom(primaryStage, outToServer, inFromServer, resp.gid, true);
    }
  }

  public Browser(Stage primaryStage) {

    this.game_data.clear();
    Player player1 = new Player("Kevin");
    Player player2 = new Player("Sahil");
    Player player3 = new Player("Yash");
    Set<Player> playerset = new HashSet<Player>();
    playerset.add(player1);
    playerset.add(player2);
    playerset.add(player3);
    Map<Long, Set<Player>> gameusernames = new HashMap<Long, Set<Player>>();
    gameusernames.put(1l, playerset);
    gameusernames.put(2l, playerset);
    gameusernames.put(3l, playerset);
    Map<Long, Player> gamehost = new HashMap<Long, Player>();
    gamehost.put(1l, player1);
    gamehost.put(2l, player2);
    gamehost.put(3l, player3);
    Set<Player> onlineplayers = new HashSet<Player>();
    for (int i = 1; i < 100; i++) {
      onlineplayers.add(new Player("Kevin"));
    }


    Iterator<Entry<Long, Set<Player>>> it1 = gameusernames.entrySet().iterator();
    Iterator<Entry<Long, Player>> it2 = gamehost.entrySet().iterator();
    while (it1.hasNext() && it2.hasNext()) {
      Map.Entry<Long, Set<Player>> room = (Map.Entry<Long, Set<Player>>) it1.next();
      Map.Entry<Long, Player> host = (Map.Entry<Long, Player>) it2.next();
      Long gid = room.getKey();
      String name = "Game " + room.getKey();
      String players = room.getValue().size() + "/10";
      String leader = host.getValue().username;
      this.game_data.add(new GameData(gid, name, players, leader));
      it1.remove();
      it2.remove();
    }

    Iterator<Player> it3 = onlineplayers.iterator();
    while (it3.hasNext()) {
      this.user_data.add(new UserData(it3.next().username));
      it3.remove();
    }

    game_tbl.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent event) {
        System.out
        .println("clicked on " + game_tbl.getSelectionModel().getSelectedItem().game_id.get());
        // join_room(primaryStage, outToServer, inFromServer,
        // game_tbl.getSelectionModel().getSelectedItem().game_id.get());
      }
    });
    // menubar = new MenuBar();

    content = new GridPane();

    content.setGridLinesVisible(true);
    content.setHgap(30);
    content.setVgap(30);
    content.setPadding(new Insets(45, 10, 45, 10));

    // Active game browser
    game_tbl.setEditable(false);
    game_tbl.setPrefWidth(460);
    game_tbl.setPrefHeight(420);
    game_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<GameData, String> col_name = new TableColumn<>("NAME");
    col_name.setPrefWidth(180);
    col_name.setResizable(false);
    col_name.setCellValueFactory(new PropertyValueFactory<>("Name"));

    TableColumn<GameData, String> col_players = new TableColumn<>("PLAYERS");
    col_players.setPrefWidth(120);
    col_players.setResizable(false);
    col_players.setCellValueFactory(new PropertyValueFactory<>("Players"));

    TableColumn<GameData, String> col_owner = new TableColumn<>("OWNER");
    col_owner.setPrefWidth(135);
    col_owner.setResizable(false);
    col_owner.setCellValueFactory(new PropertyValueFactory<>("Owner"));



    // Thread server_response_handler = null;
    // try {
    // server_response_handler = new Thread(new BrowserThread(primaryStage, outToServer,
    // inFromServer));
    // } catch (IOException e2) {
    // // TODO Auto-generated catch block
    // e2.printStackTrace();
    // }
    // server_response_handler.start();

    // Added retrieved room data to columns
    game_tbl.setItems(this.game_data);
    game_tbl.getColumns().addAll(col_name, col_players, col_owner);

    // Disable user reordering of columns at runtime
    game_tbl.widthProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> source, Number oldWidth,
          Number newWidth) {
        TableHeaderRow header = (TableHeaderRow) game_tbl.lookup("TableHeaderRow");
        header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
              Boolean newValue) {
            header.setReordering(false);
          }
        });
      }
    });

    game_tbl.getStyleClass().add("tbl-game");
    content.add(game_tbl, 0, 0, 3, 2);

    // Room name
    // TextField name_input = new TextField ();
    // content.add(name_input, 1, 2, 1, 1);

    // Create game button
    Button newgame_btn = new Button("NEW GAME");
    newgame_btn.getStyleClass().add("btn-newgame");
    newgame_btn.setPrefHeight(60);
    newgame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        // if ((name_input.getText() != null && !name_input.getText().isEmpty())) {
        // create_room(primaryStage, outToServer, inFromServer, name_input.getText());
        // }
        // create_room(primaryStage, outToServer, inFromServer);
      }
    });
    content.add(newgame_btn, 0, 2, 1, 1);

    // Create refresh button
    Button refresh_btn = new Button(Character.toString((char) 0xf1b9));
    refresh_btn.getStyleClass().add("btn-refresh");
    refresh_btn.setPrefWidth(60);
    refresh_btn.setPrefHeight(60);
    content.add(refresh_btn, 2, 2, 1, 1);
    GridPane.setHalignment(refresh_btn, HPos.RIGHT);
    refresh_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        // load_available_rooms(outToServer, inFromServer);
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

    // ImageView user_avatar = new ImageView();
    Label user_name = new Label(Launcher.username);
    // HBox user_wins = new HBox();
    //
    // Label ngold = new Label("4");
    // Label nsilver = new Label("2");
    // Label nbronze = new Label("0");
    //
    // user_wins.getChildren().addAll(ngold, nsilver, nbronze);
    // userinfo.add(user_avatar, 0, 0, 1, 1);
    userinfo.add(user_name, 1, 0, 1, 1);
    // userinfo.add(user_wins, 0, 1, 2, 1);
    userinfo.getStyleClass().add("user");
    content.add(userinfo, 3, 0, 1, 1);

    // Online Users / chat
    user_tbl.setEditable(false);
    user_tbl.setPrefWidth(230);
    user_tbl.setPrefHeight(240);
    user_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<UserData, String> col_online = new TableColumn<>("ACTIVE USERS");
    col_online.setPrefWidth(230);
    // Scroll bar is 15px
    col_online.setMaxWidth(215);
    col_online.setResizable(false);
    col_online.setCellValueFactory(new PropertyValueFactory<>("Name"));

    // Added retrieved room data to columns

    user_tbl.setItems(this.user_data);
    user_tbl.getColumns().addAll(col_online);

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
    content.add(user_tbl, 3, 1, 1, 2);

    this.getChildren().addAll(content);
    this.getStyleClass().add("browser");
    this.setPadding(new Insets(0, 40, 0, 40));
  }

  public Browser(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer) {

    // Get game rooms from server before opening browser
    GamesUpdateMessage init = new GamesUpdateMessage();
    init.send(outToServer);

    game_tbl.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        // System.out.println("clicked on " +
        // game_tbl.getSelectionModel().getSelectedItem().game_id.get());
        JoinRoomMessage msg = new JoinRoomMessage(Launcher.username,
            game_tbl.getSelectionModel().getSelectedItem().game_id.get());
        msg.send(outToServer);
        // join_room(primaryStage, outToServer, inFromServer,
        // game_tbl.getSelectionModel().getSelectedItem().game_id.get());
      }
    });
    // menubar = new MenuBar();

    content = new GridPane();

    content.setGridLinesVisible(true);
    content.setHgap(30);
    content.setVgap(30);
    content.setPadding(new Insets(45, 10, 45, 10));

    // Active game browser
    game_tbl.setEditable(false);
    game_tbl.setPrefWidth(460);
    game_tbl.setPrefHeight(420);
    game_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<GameData, String> col_name = new TableColumn<>("NAME");
    col_name.setPrefWidth(180);
    col_name.setResizable(false);
    col_name.setCellValueFactory(new PropertyValueFactory<>("Name"));

    TableColumn<GameData, String> col_players = new TableColumn<>("PLAYERS");
    col_players.setPrefWidth(120);
    col_players.setResizable(false);
    col_players.setCellValueFactory(new PropertyValueFactory<>("Players"));

    TableColumn<GameData, String> col_owner = new TableColumn<>("OWNER");
    col_owner.setPrefWidth(135);
    col_owner.setResizable(false);
    col_owner.setCellValueFactory(new PropertyValueFactory<>("Owner"));


    // Added retrieved room data to columns
    game_tbl.setItems(this.game_data);
    game_tbl.getColumns().addAll(col_name, col_players, col_owner);

    // Disable user reordering of columns at runtime
    game_tbl.widthProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> source, Number oldWidth,
          Number newWidth) {
        TableHeaderRow header = (TableHeaderRow) game_tbl.lookup("TableHeaderRow");
        header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
              Boolean newValue) {
            header.setReordering(false);
          }
        });
      }
    });

    game_tbl.getStyleClass().add("tbl-game");
    content.add(game_tbl, 0, 0, 3, 2);

    // Room name
    // TextField name_input = new TextField ();
    // content.add(name_input, 1, 2, 1, 1);

    // Create game button
    Button newgame_btn = new Button("NEW GAME");
    newgame_btn.getStyleClass().add("btn-newgame");
    newgame_btn.setPrefHeight(60);
    newgame_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        // if ((name_input.getText() != null && !name_input.getText().isEmpty())) {
        // create_room(primaryStage, outToServer, inFromServer, name_input.getText());
        // }
        CreateRoomMessage msg = new CreateRoomMessage(Launcher.username);
        msg.send(outToServer);
        // create_room(primaryStage, outToServer, inFromServer);
      }
    });
    content.add(newgame_btn, 0, 2, 1, 1);

    // Create refresh button
    Button refresh_btn = new Button(Character.toString((char) 0xf1b9));
    refresh_btn.getStyleClass().add("btn-refresh");
    refresh_btn.setPrefWidth(60);
    refresh_btn.setPrefHeight(60);
    content.add(refresh_btn, 2, 2, 1, 1);
    GridPane.setHalignment(refresh_btn, HPos.RIGHT);
    refresh_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        RefreshMessage msg = new RefreshMessage(Launcher.username);
        msg.send(outToServer);
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

    // ImageView user_avatar = new ImageView();
    Label user_name = new Label(Launcher.username);
    // HBox user_wins = new HBox();
    //
    // Label ngold = new Label("4");
    // Label nsilver = new Label("2");
    // Label nbronze = new Label("0");
    //
    // user_wins.getChildren().addAll(ngold, nsilver, nbronze);
    // userinfo.add(user_avatar, 0, 0, 1, 1);
    userinfo.add(user_name, 1, 0, 1, 1);
    // userinfo.add(user_wins, 0, 1, 2, 1);
    userinfo.getStyleClass().add("user");
    content.add(userinfo, 3, 0, 1, 1);

    // Online Users / chat
    user_tbl.setEditable(false);
    user_tbl.setPrefWidth(230);
    user_tbl.setPrefHeight(240);
    user_tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<UserData, String> col_online = new TableColumn<>("ACTIVE USERS");
    col_online.setPrefWidth(230);
    // Scroll bar is 15px
    col_online.setMaxWidth(215);
    col_online.setResizable(false);
    col_online.setCellValueFactory(new PropertyValueFactory<>("Name"));

    // Added retrieved room data to columns

    user_tbl.setItems(this.user_data);
    user_tbl.getColumns().addAll(col_online);

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
    content.add(user_tbl, 3, 1, 1, 2);

    this.getChildren().addAll(content);
    this.getStyleClass().add("browser");
    this.setPadding(new Insets(0, 40, 0, 40));


    task = new Task<Void>() {
      @Override
      public Void call() throws Exception {
        System.out.println("Browser: Task started.");
        while (true) {
          System.out.println("Browser: Task looped.");
          if (this.isCancelled()) {
            System.out.println("Browser: Task cancelled.");
            break;
          }
          Object obj = null;
          try {
            obj = inFromServer.readObject();
            System.out.println("Browser: Object read " + obj);
          } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          System.out.println("Browser: Object read " + obj);

          if (obj instanceof GamesUpdateResponse) {
            GamesUpdateResponse gu_resp = (GamesUpdateResponse) obj;
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                System.out.println("Browser: handling " + gu_resp);
                handleGamesUpdateResponse(gu_resp);
              }
            });
          }

          if (obj instanceof CreateRoomResponse) {
            CreateRoomResponse cr_resp = (CreateRoomResponse) obj;
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                System.out.println("Browser: handling " + cr_resp);
                handleCreateRoomResponse(primaryStage, outToServer, inFromServer, cr_resp);
              }
            });
          }

          if (obj instanceof JoinRoomResponse) {
            JoinRoomResponse jr_resp = (JoinRoomResponse) obj;
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                System.out.println("Browser: handling " + jr_resp);
                handleJoinRoomResponse(primaryStage, outToServer, inFromServer, jr_resp);
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

  public static class GameData {
    private final SimpleLongProperty game_id;
    private final SimpleStringProperty game_name;
    private final SimpleStringProperty game_players;
    private final SimpleStringProperty game_owner;


    private GameData(Long gid, String name, String players, String owner) {
      this.game_id = new SimpleLongProperty(gid);
      this.game_name = new SimpleStringProperty(name);
      this.game_players = new SimpleStringProperty(players);
      this.game_owner = new SimpleStringProperty(owner);
    }

    public Long getGid() {
      return game_id.get();
    }

    public void setGid(Long id) {
      game_id.set(id);
    }

    public String getName() {
      return game_name.get();
    }

    public void setName(String name) {
      game_name.set(name);
    }

    public String getPlayers() {
      return game_players.get();
    }

    public void setPlayers(String players) {
      game_players.set(players);
    }

    public String getOwner() {
      return game_owner.get();
    }

    public void setOwner(String owner) {
      game_owner.set(owner);
    }

  }

  public static class UserData {
    private final SimpleStringProperty user_name;

    private UserData(String name) {
      this.user_name = new SimpleStringProperty(name);
    }

    public String getName() {
      return user_name.get();
    }

    public void setName(String name) {
      user_name.set(name);
    }
  }
}
