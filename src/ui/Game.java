package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import message.InitialCardsMessage;
import message.InitialCardsResponse;
import message.Sendable;
import message.SetSelectMessage;
import message.SetSelectResponse;
import server.PlayerThread;

import java.util.HashMap;
import java.util.HashSet;


public class Game extends BorderPane {

  private HashMap<String, Integer> location_to_card = new HashMap<String, Integer>();
  private HashMap<String, Node> location_to_node = new HashMap<String, Node>();
  private HashMap<String, Boolean> location_to_click_status = new HashMap<String, Boolean>();
  private HashSet<String> locations_clicked = new HashSet<String>();
  
  
  private class serverListenerThread implements Runnable {
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    
    public serverListenerThread(ObjectOutputStream toServer, ObjectInputStream fromServer)
    {
      this.toServer = toServer;
      this.fromServer = fromServer;
    }
    
    public void run(){
      Thread t = new Thread();
    }
  }
  
  private void load_initial_cards(GridPane grid)
  {
    for (int colindex = 0; colindex < 4; ++colindex)
    {
      for (int rowindex = 0; rowindex < 3; ++rowindex)
      {
        int card = colindex*3 + rowindex;
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
        final int card_row_ind = rowindex;
        final int card_col_ind = colindex;
        String location = ""+card_row_ind+""+card_col_ind;
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
   InitialCardsMessage start_msg = new InitialCardsMessage();
   start_msg.send(outToServer);
   try {
    InitialCardsResponse start_response = (InitialCardsResponse)inFromServer.readObject();
    
    for (int colindex = 0; colindex < 4; ++colindex)
    {
      for (int rowindex = 0; rowindex < 3; ++rowindex)
      {
        int card = start_response.cards[colindex*3 + rowindex];
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
        final int card_row_ind = rowindex;
        final int card_col_ind = colindex;
        String location = ""+card_row_ind+""+card_col_ind;
        location_to_card.put(location, card);
        location_to_node.put(location, setCard);
        location_to_click_status.put(location, false);
        
        setCard.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
          @Override
          public void handle(MouseEvent t) {
            if (location_to_click_status.get(location) == false)
            {
              setCard.setStrokeWidth(4);
              location_to_click_status.put(location, true);
            }
            else if (location_to_click_status.get(location) == true)
            {
              setCard.setStrokeWidth(0);
              location_to_click_status.put(location, false);
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
      int cards[] = new int[3];
      int index = 0;
      for (String location : locations_clicked)
      {
        cards[index] = location_to_card.get(location);
        System.out.println(location_to_card.get(location));
      }
    }
    set_correct.setText("Correct");
    return true;
  }
  
  private void submit_cards(GridPane grid, Text set_correct, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
  {
    if (locations_clicked.size() != 3)
    {
      set_correct.setText("Select 3 Cards");
    }
    
    else
    {
      int cards[] = new int[3];
      int index = 0;
      for (String location : locations_clicked)
      {
        cards[index] = location_to_card.get(location);
      }
      SetSelectMessage set_message = new SetSelectMessage(Launcher.username, cards);
      try {
        outToServer.writeObject(set_message);
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    } 
  }
  
  private void delete_cards(GridPane grid)
  {
    for (String location : locations_clicked)
    {
      /*
       * Since location stored as "colindex"+"rowindex"
       */
      int colindex = Character.getNumericValue(location.charAt(1));
      int rowindex = Character.getNumericValue(location.charAt(0));
      grid.getChildren().remove(location_to_node.get(location));
      location_to_node.remove(location);
      location_to_card.remove(location);
      location_to_click_status.remove(location);
    }
    locations_clicked.clear();
  }
  
  public Game(Stage primaryStage)
  {
    ToolBar toolbar = new ToolBar(
        new Button("Surrender"),
        new Button("Change Password"),
        new Button("Player Statistics")
        );
    
    VBox right_detail_pane = new VBox();
    
    GridPane center_pane = new GridPane();
    center_pane.setAlignment(Pos.CENTER);
    center_pane.setHgap(20);
    center_pane.setVgap(20);
    
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
  
  public Game(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
  {
    ToolBar toolbar = new ToolBar(
        new Button("Surrender"),
        new Button("Change Password"),
        new Button("Player Statistics")
        );
    
    VBox right_detail_pane = new VBox();
    
    GridPane center_pane = new GridPane();
    center_pane.setAlignment(Pos.CENTER);
    center_pane.setHgap(20);
    center_pane.setVgap(20);
    
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
        
        if (locations_clicked.size() != 3)
        {
          set_correct.setText("Select 3 Cards");
        }
        
        else
        {
          int cards[] = new int[3];
          int index = 0;
          for (String location : locations_clicked)
          {
            cards[index] = location_to_card.get(location);
          }
          Sendable set_select_message = new SetSelectMessage(Launcher.username, cards);
          set_select_message.send(outToServer);
          
          try {
            SetSelectResponse response = (SetSelectResponse)inFromServer.readObject();
            if (response.is_valid)
            {
              /*
               * Increase Score by one
               */
              
              set_correct.setText("Right!");
            }
            else
            {
              /*
               * Decrease Score by one
               */
              set_correct.setText("Wrong :(");
            }
            
          } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          
        }
      }
    });
    
    this.setTop(toolbar);
    this.setCenter(center_pane);
    this.setLeft(left_detail_pane);
    this.setRight(right_detail_pane);
    this.setMargin(center_pane, new Insets(10, 10, 10, 10));
    
  }     
}
