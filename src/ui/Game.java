package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import message.EndGameResponse;
import message.InitialCardsMessage;
import message.InitialCardsResponse;
import message.LeaveGameResponse;
import message.NewCardsResponse;
import message.Sendable;
import message.SetSelectMessage;
import message.SetSelectResponse;
import message.TableResponse;
import server.PlayerThread;
import gamelogic.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Game extends BorderPane {

  private volatile HashMap<Location, Card> location_to_card = new HashMap<Location, Card>();
  private volatile HashMap<Location, Node> location_to_node = new HashMap<Location, Node>();
  private volatile HashMap<Location, Boolean> location_to_click_status = new HashMap<Location, Boolean>();
  private volatile HashSet<Location> locations_clicked = new HashSet<Location>();
  private volatile HashMap<String, Text> username_to_score_field = new HashMap<String, Text>();
    
  private void handleSetResponse(SetSelectResponse resp, Text set_correct)
  {
    if (!resp.is_valid)
    {
      System.out.println("Incorrect");
      set_correct.setText("Incorrect");
    }
    else
    {
      String username = resp.username;
      System.out.println(resp.username + " Got a set correct");
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
      username_to_score_field.get(resp.username).setText(resp.username + ": " + Integer.toString(resp.setcount));
    }
  }
  
  private void handleNewCardsResponse(NewCardsResponse n_resp, GridPane grid)
  {
    for (Card card : n_resp.table1)
    {
      System.out.println(card.toImageFile());
      System.out.println(card.randomnum);
    }
  }
  
  private void handleTableResponse(TableResponse t_resp, GridPane grid)
  {
    System.out.println("RESP num: " + t_resp.randomnum);
    int colindex = 0;
    int rowindex = 0;
    
    location_to_card.clear();
    location_to_node.clear();
    locations_clicked.clear();
    grid.getChildren().clear();
    
    for (Card card : t_resp.table1)
    {
      if (card.hole == false)
      {
        Rectangle setCard = new Rectangle();
        String imagesrc = card.toImageFile();
        imagesrc = "ui/resources/images/cards/" + imagesrc;
        //System.out.println(imagesrc);
        System.out.println(card.randomnum);
        Image image = new Image(imagesrc);
        ImagePattern imagePattern = new ImagePattern(image);
        setCard.setHeight(200);
        setCard.setWidth(100);
        setCard.setFill(imagePattern);
        setCard.setArcHeight(20);
        setCard.setArcWidth(20);
        setCard.setStrokeType(StrokeType.INSIDE);
        setCard.setStroke(Color.web("blue", 0.30));
        setCard.setStrokeWidth(0);
        //System.out.println("Colindex: " + colindex);
        //System.out.println("Rowindex: " + rowindex);
        //System.out.println("");
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
              locations_clicked.remove(location);
            }
            
          }
        });       
      }
      /*
       * Update row and column index
       */
      rowindex = rowindex + 1;
      if ((rowindex % 3) == 0)
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
  
  
  
  private void load_initial_cards(GridPane grid)
  {
    for (int colindex = 0; colindex < 5; ++colindex)
    {
      for (int rowindex = 0; rowindex < 3; ++rowindex)
      {
        Card card = new Card(colindex % 3, rowindex % 3, 0, 0, true);
        String imagesrc = card.toImageFile();
        imagesrc = "ui/resources/images/cards/" + imagesrc;
        System.out.println(imagesrc);
        Image image = new Image(imagesrc);
        ImagePattern imagePattern = new ImagePattern(image);
        Rectangle setCard = new Rectangle();
        setCard.setHeight(200);
        setCard.setWidth(100);
        setCard.setFill(imagePattern);
        setCard.setArcHeight(20);
        setCard.setArcWidth(20);
        setCard.setStrokeType(StrokeType.INSIDE);
        setCard.setStroke(Color.web("blue", 0.30));
        setCard.setStrokeWidth(0);
        grid.add(setCard, colindex, rowindex);
        Location location = new Location(rowindex, colindex);
        location_to_card.put(location, card);
        location_to_node.put(location, setCard);
        location_to_click_status.put(location, false);
        
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
              locations_clicked.remove(location);
            }
            
          }
        });
        
      }
    }
  }
  
  private void load_initial_cards(ObjectOutputStream outToServer, ObjectInputStream inFromServer, GridPane grid)
  {
   InitialCardsMessage start_msg = new InitialCardsMessage(Launcher.username);
   start_msg.send(outToServer);
   System.out.println("Sent initial cards message");
   
   try {
    InitialCardsResponse start_response = (InitialCardsResponse)inFromServer.readObject();
    System.out.println("Received card response");
    
    for (int colindex = 0; colindex < 4; ++colindex)
    {
      for (int rowindex = 0; rowindex < 3; ++rowindex)
      {
        Card card = start_response.table.get(colindex*3 + rowindex);
        String imagesrc = card.toImageFile();
        imagesrc = "ui/resources/images/cards/" + imagesrc;
        System.out.println(imagesrc);
        Image image = new Image(imagesrc);
        ImagePattern imagePattern = new ImagePattern(image);
        Rectangle setCard = new Rectangle();
        setCard.setHeight(200);
        setCard.setWidth(100);
        setCard.setFill(imagePattern);
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
              locations_clicked.remove(location);
            }
            
          }
        });
        
      }
    }   
    
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /*
   * Submits the set to the server
   * Gets response from the server
   * Returns whether valid set or not
   */
  private boolean submit_cards(GridPane grid, Text set_correct)
  {
    if (locations_clicked.size() != 3)
    {
      set_correct.setText("Select 3 Cards");
      return false;
    }
    
    else
    {
      ArrayList<Card> cards = new ArrayList<Card>();
      for (Location location : locations_clicked)
      {
        cards.add(location_to_card.get(location));
        System.out.println(location_to_card.get(location));
      }
    }
    set_correct.setText("Correct");
    return true;
  }
  
  /*
   * Real code
   * Submit cards to server
   * GameThread will handle the response
   */
  private void submit_cards(GridPane grid, Text set_correct, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
  {
    if (locations_clicked.size() != 3)
    {
      set_correct.setText("Select 3 Cards");
    }
    
    else
    {
      ArrayList<Card> cards = new ArrayList<Card>();
      for (Location location : locations_clicked)
      {
        cards.add(location_to_card.get(location));
      }
      SetSelectMessage set_message = new SetSelectMessage(Launcher.username, cards);
      set_message.send(outToServer);
    } 
  }
  
  /*
   * Only for test code
   */
  private void delete_cards(GridPane grid)
  {
    for (Location location : locations_clicked)
    {
      grid.getChildren().remove(location_to_node.get(location));
      location_to_node.remove(location);
      location_to_card.remove(location);
      location_to_click_status.remove(location);
    }
    locations_clicked.clear();
  }
  
  public Game(Stage primaryStage, ArrayList<String> users)
  {
    ToolBar toolbar = new ToolBar(
        new Button("Surrender"),
        new Button("Change Password"),
        new Button("Player Statistics")
        );
    
    VBox right_detail_pane = new VBox();
    for (String user : users)
    {
      Text text = new Text(user);
      right_detail_pane.getChildren().add(text);
      username_to_score_field.put(user, text);
    }
    
    GridPane center_pane = new GridPane();
    center_pane.setAlignment(Pos.CENTER);
//    center_pane.setHgap(20);
//    center_pane.setVgap(20);
    
    VBox left_detail_pane = new VBox();
    Button set_btn = new Button("Set!");
    Text set_correct = new Text("");
    
    left_detail_pane.getChildren().add(set_btn);
    left_detail_pane.getChildren().add(set_correct);
    left_detail_pane.setMargin(set_btn, new Insets(10, 10, 10, 10));
    
    load_initial_cards(center_pane);
    
    set_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        boolean valid_set = submit_cards(center_pane, set_correct);
        if (valid_set)
        {
          delete_cards(center_pane);
        }
      }
    });
    
    this.setTop(toolbar);
    this.setCenter(center_pane);
    this.setLeft(left_detail_pane);
    this.setRight(right_detail_pane);
    this.setMargin(center_pane, new Insets(10, 10, 10, 10));
  }
  
  public Game(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer, ArrayList<String> users)
  {    
    ToolBar toolbar = new ToolBar(
      new Button("Surrender"),
      new Button("Change Password"),
      new Button("Player Statistics")
        );
    
    VBox right_detail_pane = new VBox();
    for (String user : users)
    {
      Text text = new Text();
      right_detail_pane.getChildren().add(text);
      username_to_score_field.put(user, text);
    }
    /*
     * Add in user score boxes to right detail pane
     */
    
    GridPane center_pane = new GridPane();
    center_pane.setAlignment(Pos.CENTER);
    //center_pane.setHgap(20);
    //center_pane.setVgap(20);
    
    VBox left_detail_pane = new VBox();
    Button set_btn = new Button("Set!");
    Text set_correct = new Text("");
    
    left_detail_pane.getChildren().add(set_btn);
    left_detail_pane.getChildren().add(set_correct);
    left_detail_pane.setMargin(set_btn, new Insets(10, 10, 10, 10));
    
    load_initial_cards(outToServer, inFromServer, center_pane);
    
    set_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        submit_cards(center_pane, set_correct, outToServer, inFromServer);
      }
    });
    
    this.setTop(toolbar);
    this.setCenter(center_pane);
    this.setLeft(left_detail_pane);
    this.setRight(right_detail_pane);
    this.setMargin(center_pane, new Insets(10, 10, 10, 10));
    
    Task task = new Task<Void>() {
      @Override
      public Void call() throws Exception {
        
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
          
          if (obj instanceof NewCardsResponse)
          {
            NewCardsResponse n_resp = (NewCardsResponse) obj;
            System.out.println("Got new cards response. ");
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                handleNewCardsResponse(n_resp, center_pane);
              }
            });
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
            System.out.println("Got set select response.");
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                handleSetResponse(resp, set_correct);
              }
            });
                
          }
          /*
           * Handle other server responses.
           */
          
          if (obj instanceof TableResponse)
          {
            TableResponse t_resp = (TableResponse) obj;
            System.out.println("Got table response.");
            for (Card card : t_resp.table1)
            {
             System.out.println(card.toImageFile());
            }
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                handleTableResponse(t_resp, center_pane);
              }
            });
          }
      
          if (obj instanceof EndGameResponse)
          {
            EndGameResponse resp = (EndGameResponse) obj;
            System.out.println("Got end game response.");
            /*
             * Maybe display scores of all users at end
             */
            
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                center_pane.getChildren().clear();
                Button go_back = new Button("Back to Lobby");
                go_back.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent e){
                    Launcher.openBrowser(primaryStage, outToServer, inFromServer);
                  }
                });
                center_pane.add(go_back, 0, 0);
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
            System.out.println("Got leave game response.");
            LeaveGameResponse resp = (LeaveGameResponse) obj;
            handleLeaveGameResponse(resp);
            break;        
          }
        }
        
        return null;
        
      }
    };
    
    Thread th = new Thread(task);
    th.setDaemon(true);
    th.start();
    
    
//    Thread server_response_handler = null;
//    try {
//      server_response_handler = new Thread(new GameThread(primaryStage, outToServer, inFromServer, set_correct, center_pane,
//          location_to_card, location_to_node, location_to_click_status, locations_clicked, username_to_score_field));
//    } catch (IOException e2) {
//      // TODO Auto-generated catch block
//      e2.printStackTrace();
//    }
//    
//    server_response_handler.start();   
    
  }     
}
