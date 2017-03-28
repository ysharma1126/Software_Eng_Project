package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

  public static String username = null;
  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
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
    
  }
  
  public static void openGame(Stage primaryStage)
  {
    Game game = new Game(primaryStage);
    Scene scene = new Scene(game, 1200, 900);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  public static void openGame(Stage primaryStage, Socket socket, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
  {
    Game game = new Game(primaryStage, socket, outToServer, inFromServer);
    Scene scene = new Scene(game, 1200, 900);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  @Override
  public void start(Stage primaryStage)
  {
    
//    String hostname = "";
//    int portnumber = 0;
//    
//    try {
//      Socket connectSocket = new Socket(hostname, portnumber);
//      ObjectOutputStream outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
//      ObjectInputStream inFromServer = new ObjectInputStream(connectSocket.getInputStream());
//      
//      openLogin(primaryStage, outToServer, inFromServer);
//           
//    }
//    
//    catch (ConnectException e1) {
//      System.err.println("Did not enter in proper server address/portnumber!");
//    } catch (UnknownHostException e1) {
//    // TODO Auto-generated catch block
//      System.err.println("Did not enter in proper server address/portnumber!");
//    } catch (IOException e1) {
//      // TODO Auto-generated catch block
//          e1.printStackTrace();
//    }
    
    openLogin(primaryStage);
  }
}

