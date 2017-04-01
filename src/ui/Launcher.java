package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

  public static String username = null;
  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println("Launched");
    launch(args);
  }
  
  public static void openLogin(Stage primaryStage)
  {
    Login login = new Login(primaryStage);
    Scene scene = new Scene(login, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void openLogin(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
  {
    Login login = new Login(primaryStage, outToServer, inFromServer);
    Scene scene = new  Scene(login, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  public static void openBrowser(Stage primaryStage)
  {
    
  }
  
  public static void openBrowser(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
  {
    System.out.println("Logged in!");
  }
  
  public static void openGame(Stage primaryStage, ArrayList<String> users)
  {
    Game game = new Game(primaryStage, users);
    Scene scene = new Scene(game, 1200, 900);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  public static void openGame(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer, ArrayList<String> users)
  {
    Game game = new Game(primaryStage, outToServer, inFromServer, users);
    Scene scene = new Scene(game, 1200, 900);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  @Override
  public void start(Stage primaryStage)
  {
    //openLogin(primaryStage);
    String hostname = "199.98.20.114";
    int portnumber = 8080;
    
    try {
      Socket connectSocket = new Socket(hostname, portnumber);
      ObjectOutputStream outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
      ObjectInputStream inFromServer = new ObjectInputStream(connectSocket.getInputStream());
      System.out.println("Opening Login");
      openLogin(primaryStage, outToServer, inFromServer);       
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

