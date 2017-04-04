package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import gamelogic.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import message.ChangedHostResponse;
import message.CreateRoomResponse;
import message.GamesUpdateResponse;
import message.JoinRoomResponse;
import message.LeaveRoomResponse;
import message.LoginResponse;
import message.StartGameResponse;

public class Launcher extends Application {

  public static String username = null;
  public static Object current_page;

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println("Launched");
    launch(args);
  }

  public static void openLogin(Stage primaryStage) {
    Login login = new Login(primaryStage);
    Scene scene = new Scene(login, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void openLogin(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer) {
    Login login = new Login(primaryStage, outToServer, inFromServer);
    current_page = login;
    Scene scene = new Scene(login, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void openBrowser(Stage primaryStage) {
    Browser browser = new Browser(primaryStage);
    current_page = browser;
    Scene scene = new Scene(browser, 800, 600);
    scene.getStylesheets().add("https://fonts.googleapis.com/icon?family=Material+Icons");
    scene.getStylesheets().add(
        "https://fonts.googleapis.com/css?family=Montserrat:300,300i,400,400i,500,500i,600,600i,700,700i");
    scene.getStylesheets().add("ui/style.css");
    primaryStage.setTitle("SET");
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void openBrowser(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer) {
    Browser browser = new Browser(primaryStage, outToServer, inFromServer);
    current_page = browser;
    Scene scene = new Scene(browser, 800, 600);
    scene.getStylesheets().add("https://fonts.googleapis.com/icon?family=Material+Icons");
    scene.getStylesheets().add(
        "https://fonts.googleapis.com/css?family=Montserrat:300,300i,400,400i,500,500i,600,600i,700,700i");
    scene.getStylesheets().add("ui/style.css");
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    System.out.println(primaryStage.getScene());
    primaryStage.show();
  }

  public static void openRoom(Stage primaryStage, Long gid, Boolean isHost) {
    Room room = new Room(primaryStage, gid, isHost);
    Scene scene = new Scene(room, 800, 600);
    scene.getStylesheets().add("https://fonts.googleapis.com/icon?family=Material+Icons");
    scene.getStylesheets().add(
        "https://fonts.googleapis.com/css?family=Montserrat:300,300i,400,400i,500,500i,600,600i,700,700i");
    scene.getStylesheets().add("ui/style.css");
    primaryStage.setTitle("SET");
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void openRoom(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, Long gid, Boolean isHost) {
    Room room = new Room(primaryStage, outToServer, inFromServer, gid, isHost);
    current_page = room;
    Scene scene = new Scene(room, 800, 600);
    scene.getStylesheets().add("https://fonts.googleapis.com/icon?family=Material+Icons");
    scene.getStylesheets().add(
        "https://fonts.googleapis.com/css?family=Montserrat:300,300i,400,400i,500,500i,600,600i,700,700i");
    scene.getStylesheets().add("ui/style.css");
    primaryStage.setTitle("SET");
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void openGame(Stage primaryStage, ArrayList<String> users) {
    Game game = new Game(primaryStage, users);
    Scene scene = new Scene(game, 1200, 900);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void openGame(ObjectOutputStream outToServer, ObjectInputStream inFromServer,
      ArrayList<String> users, Stage primaryStage) {
    System.out.println("openGame 1");
    Game game = new Game(primaryStage, outToServer, inFromServer, users);
    current_page = game;
    System.out.println("openGame 2");
    Scene scene = new Scene(game, 1200, 900);
    System.out.println("openGame 3");
    primaryStage.setScene(scene);
    System.out.println("openGame 4");
    primaryStage.show();
  }

  public static void openGame(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, ArrayList<Player> users) {
    ArrayList<String> usernames = new ArrayList<String>();
    for (Player user : users) {
      usernames.add(user.username);
    }
    Game game = new Game(primaryStage, outToServer, inFromServer, usernames);
    current_page = game;
    Scene scene = new Scene(game, 1200, 900);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void start(Stage primaryStage) {
    String hostname = "199.98.20.114";
    int portnumber = 8080;

    try {
      Socket connectSocket = new Socket(hostname, portnumber);
      ObjectOutputStream outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
      ObjectInputStream inFromServer = new ObjectInputStream(connectSocket.getInputStream());
      System.out.println("Opening Login");
      openLogin(primaryStage, outToServer, inFromServer);
      
      Task task = new Task<Void>() {
        @Override
        public Void call() throws Exception {
          while (true) {
            
            if (this.isCancelled()) break;
            
            /*** Read one response from server ***/
            Object obj = null;
            try {
              obj = inFromServer.readObject();
              System.out.println("Browser: Object read " + obj);
            } catch (ClassNotFoundException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            } 
            System.out.println("Browser: Object read " + obj);
            
            /*** Handle responses as Login ***/
            if (current_page instanceof Login) {
              if (obj instanceof LoginResponse) {
                LoginResponse lresp = (LoginResponse) obj;
                Platform.runLater(() ->
                  ((Login) current_page).handleLoginResponse(primaryStage, outToServer, inFromServer, lresp));
              }
            }
            
            /*** Handle responses as Browser ***/
            if (current_page instanceof Browser) {
              if (obj instanceof GamesUpdateResponse) {
                GamesUpdateResponse gu_resp = (GamesUpdateResponse) obj;
                Platform.runLater(() -> 
                  ((Browser) current_page).handleGamesUpdateResponse(gu_resp));
              }

              if (obj instanceof CreateRoomResponse) {
                CreateRoomResponse cr_resp = (CreateRoomResponse) obj;
                Platform.runLater(() -> 
                  ((Browser) current_page).handleCreateRoomResponse(primaryStage, outToServer, inFromServer, cr_resp));
              }

              if (obj instanceof JoinRoomResponse) {
                JoinRoomResponse jr_resp = (JoinRoomResponse) obj;
                Platform.runLater(() -> 
                  ((Browser) current_page).handleJoinRoomResponse(primaryStage, outToServer, inFromServer, jr_resp));
              }
            }
            
            /*** Handle responses as Room ***/
            if (current_page instanceof Room) {
              if (obj instanceof JoinRoomResponse) {
                JoinRoomResponse jr_resp = (JoinRoomResponse) obj;
                Platform.runLater(() -> 
                  ((Room) current_page).handleJoinRoomResponse(jr_resp));
              }
  
              if (obj instanceof LeaveRoomResponse) {
                System.out.println("Got leave room response.");
                LeaveRoomResponse lr_resp = (LeaveRoomResponse) obj;
                Platform.runLater(() -> 
                  ((Room) current_page).handleLeaveRoomResponse(primaryStage, outToServer, inFromServer, lr_resp));
              }
  
              if (obj instanceof ChangedHostResponse) {
                ChangedHostResponse ch_resp = (ChangedHostResponse) obj;
                Platform.runLater(() -> 
                  ((Room) current_page).handleChangedHostResponse(ch_resp));
              }
  
              if (obj instanceof StartGameResponse) {
                StartGameResponse sg_resp = (StartGameResponse) obj;
                Platform.runLater(() -> 
                  ((Room) current_page).handleStartGameResponse(primaryStage, outToServer, inFromServer, sg_resp));
              }
            }
            
          }
          
          return null;
        }
      };

      Thread th = new Thread(task);
      th.setDaemon(true);
      th.start();
    }

    catch (ConnectException e1) {
      System.err.println("Connect Did not enter in proper server address/portnumber!");
    } catch (UnknownHostException e1) {
      // TODO Auto-generated catch block
      System.err.println("Did not enter in proper server address/portnumber!");
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    

  }
}
