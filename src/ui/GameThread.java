package ui;

import java.io.*;
import java.net.*;
import gamelogic.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import message.LoginMessage;
import message.LoginResponse;
import message.SetSelectResponse;
import message.SurrenderResponse;

import java.sql.*;

/*
 * A thread for the client to get updates
 * From the server.
 */

public class GameThread implements Runnable {
    private Socket socket = null;
    public Player player = null;
    private ObjectOutputStream outToServer = null;
    private ObjectInputStream inFromServer = null;
    private Text set_correct;
    private GridPane grid;
    
    /**
     * Initializes the ClientThread
     * takes in the outputstream and inputstream as arguments
     */
    public GameThread(Socket socket, ObjectOutputStream outToServer, ObjectInputStream inFromServer, Text set_correct, 
        GridPane grid) throws IOException{
      this.socket = socket;
      this.outToServer = outToServer;
      this.inFromServer = inFromServer;
      this.set_correct = set_correct;
      this.grid = grid;
    }
    
    private void handleSetResponse(SetSelectResponse resp)
    {
      if (!resp.is_valid)
      {
        set_correct.setText("Incorrect");
      }
      else
      {
        String username = resp.username;
        /*
         * Corresponds to current client
         */
        if (username == Launcher.username)
        {
          /*
           * Display correct
           */
          set_correct.setText("Correct");
        }
        /*
         * Increase the score in the GUI for the
         * corresponding user
         */
      }
    }

    /**
     * Obtained from the Runnable interface. Is called from Thread.start().
     * It is essentially the main method for the thread
     * The ClientListenerThread needs to listen to ServerResponses for:
     * Selecting a Set = all users must clear the 3 cards if one user got it right, must update scores for all users
     * No Set on the Board = all users must get 3 more cards
     * End of Game = all users must see who the winner is, and then go back to lobby
     * One User surrenders = all users must take out that user from their GUI, server needs to deattach that user.
     */
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
        
        if (obj instanceof SetSelectResponse)
        {
          /*
           * SetSelectResponse Message
           * Client sends SetSelectMessage to server
           * If its invalid, server only sends response to client
           * If its valid, server sends response to all clients, with the username
           * Of the user that got it right
           * Users all update guis
           */
          SetSelectResponse resp = (SetSelectResponse) obj;
          handleSetResponse(resp);
        }
        /*
         * Handle other server responses.
         */
        if (obj instanceof SurrenderResponse)
        {
          /*
           * SurrenderResponse Message
           * Client sends SurrenderMessage to server
           * Server sends response to all clients, with the username
           * That is surrendering
           * Users all update guis
           */
          try {
            this.terminate();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          break;
          
        }
      }
    }
    
    
    
    /**
     * Handles the cleanup when the thread closes.
     * This includes closing the socket and removing the player from the connect_players set
     * maintained by the main Server class.
     * @author Shalin
     */
    public void terminate() throws IOException{
        socket.close();
    }
}