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
import message.EndGameResponse;
import message.LeaveGameResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * A thread for the client to get updates
 * From the server.
 */

public class GameThread implements Runnable {
    private Stage primaryStage = null;
    public Player player = null;
    private ObjectOutputStream outToServer = null;
    private ObjectInputStream inFromServer = null;
    private Text set_correct;
    private GridPane grid;
    
    private volatile HashMap<Location, Card> location_to_card;
    private volatile HashMap<Location, Node> location_to_node;
    private volatile HashMap<Location, Boolean> location_to_click_status;
    private volatile HashSet<Location> locations_clicked;
    private volatile HashMap<String, Text> username_to_score_field;
    
    /**
     * Initializes the ClientThread
     * takes in the outputstream and inputstream as arguments
     */
    public GameThread(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer, Text set_correct, 
        GridPane grid, HashMap<Location, Card> location_to_card, HashMap<Location, Node> location_to_node,
        HashMap<Location, Boolean> location_to_click_status, HashSet<Location> locations_clicked,
        HashMap<String, Text> username_to_score_field) throws IOException{
      this.primaryStage = primaryStage;
      this.outToServer = outToServer;
      this.inFromServer = inFromServer;
      this.set_correct = set_correct;
      this.grid = grid;
      this.location_to_card = location_to_card;
      this.location_to_node = location_to_node;
      this.location_to_click_status = location_to_click_status;
      this.locations_clicked = locations_clicked;
      this.username_to_score_field = username_to_score_field;
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
        //username_to_score_field.get(resp.username).setText(Integer.toString(resp.setcount));
      }
    }
    
    private void handleTableResponse(TableResponse resp)
    {
      int colindex = 0;
      int rowindex = 0;
      
      location_to_card.clear();
      location_to_node.clear();
      locations_clicked.clear();
      grid.getChildren().clear();
      
      for (Card card : resp.table)
      {
        if (card.hole == false)
        {
          Rectangle setCard = new Rectangle();
          setCard.setHeight(200);
          setCard.setWidth(100);
          setCard.setFill(Color.WHITE);
          setCard.setArcHeight(20);
          setCard.setArcWidth(20);
          setCard.setStrokeType(StrokeType.INSIDE);
          setCard.setStroke(Color.web("blue", 0.30));
          setCard.setStrokeWidth(0);
          grid.add(setCard, colindex, rowindex);
          Location location = new Location(rowindex, colindex);
          location_to_card.put(location, card);
          location_to_node.put(location, setCard);
          
          setCard.setOnMouseClicked(new EventHandler<MouseEvent>()
          {
            @Override
            public void handle(MouseEvent t) {
              if (locations_clicked.contains(location) == false)
              {
                setCard.setStrokeWidth(4);
                locations_clicked.add(location);
              }
              else if (locations_clicked.contains(location) == true)
              {
                setCard.setStrokeWidth(0);
                locations_clicked.add(location);
              }
              
            }
          });       
        }
        /*
         * Update row and column index
         */
        rowindex = rowindex + 1;
        if ((rowindex % 3) == 1)
        {
          colindex = colindex + 1;
          rowindex = 0;
        }
      }
    }
    
    private void handleLeaveGameResponse(LeaveGameResponse resp)
    {
      username_to_score_field.get(resp.uname).setText("Surrendered");
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
        
        if (obj instanceof TableResponse)
        {
          TableResponse resp = (TableResponse) obj;
          System.out.println("Got table response.");
          handleTableResponse(resp);
        }
        
        if (obj instanceof EndGameResponse)
        {
          EndGameResponse resp = (EndGameResponse) obj;
          /*
           * Maybe display scores of all users at end
           */
          
          grid.getChildren().clear();
          Button go_back = new Button("Back to Lobby");
          go_back.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
              Launcher.openBrowser(primaryStage, outToServer, inFromServer);
            }
          });
          return;
        }
        
        if (obj instanceof LeaveGameResponse)
        {
          /*
           * SurrenderResponse Message
           * Client sends SurrenderMessage to server
           * Server sends response to all clients, with the username
           * That is surrendering
           * Users all update guis
           */
          LeaveGameResponse resp = (LeaveGameResponse) obj;
          handleLeaveGameResponse(resp);
          return;        
        }
      }
    }
}