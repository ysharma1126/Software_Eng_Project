package ui;

import java.io.*;
import java.net.*;
import gamelogic.*;
import gamelogic.Card;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import message.LoginMessage;
import message.LoginResponse;
import message.SetSelectResponse;
import message.TableResponse;
import message.CreateRoomResponse;
import message.EndGameResponse;
import message.JoinRoomResponse;
import message.LeaveGameResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * A thread for the client to get updates
 * From the server.
 */

public class BrowserThread implements Runnable {
    private Stage primaryStage = null;
    private ObjectOutputStream outToServer = null;
    private ObjectInputStream inFromServer = null;
    
    public BrowserThread(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer) throws IOException{
      this.primaryStage = primaryStage;
      this.outToServer = outToServer;
      this.inFromServer = inFromServer;
    }
      
    private void handleCreateRoomResponse(CreateRoomResponse resp)
    {
    }
    
    private void handleJoinRoomResponse(JoinRoomResponse resp)
    {
    }
    
    
    public void run() {
      while (true)
      {
        Object obj = null;
        try {
          obj = inFromServer.readObject();
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        /*
         * Handle other server responses.
         */
        
        if (obj instanceof CreateRoomResponse)
        {
          CreateRoomResponse resp = (CreateRoomResponse) obj;
          handleCreateRoomResponse(resp);
          //Launcher.openBrowser(primaryStage);
        }
        
        if (obj instanceof JoinRoomResponse)
        {
          JoinRoomResponse resp = (JoinRoomResponse) obj;
          handleJoinRoomResponse(resp);
          //Launcher.openBrowser(primaryStage);
        }
      }
    }
}