package ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import message.EndGameResponse;
import message.InitialCardsMessage;
import message.InitialCardsResponse;
import message.LeaveGameResponse;
import message.NewCardsResponse;
import message.SetSelectMessage;
import message.SetSelectResponse;
import message.TableResponse;
import gamelogic.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Game extends BorderPane {

  private GridPane center_pane;
  private Text set_correct;

  private volatile HashMap<Location, Card> location_to_card = new HashMap<Location, Card>();
  private volatile HashMap<Location, Node> location_to_node = new HashMap<Location, Node>();
  private volatile HashMap<Location, Boolean> location_to_click_status =
      new HashMap<Location, Boolean>();
  private volatile HashSet<Location> locations_clicked = new HashSet<Location>();
  private volatile HashMap<String, Text> username_to_score_field = new HashMap<String, Text>();


  public void handleSetResponse(SetSelectResponse resp) {
    if (!resp.is_valid) {
      System.out.println("Incorrect");
      set_correct.setText("Incorrect");
    } else {
      String username = resp.username;
      System.out.println(resp.username + " Got a set correct");
      if (username == Launcher.username) {
        set_correct.setText("Correct");
      }
      username_to_score_field.get(resp.username)
          .setText(resp.username + ": " + Integer.toString(resp.setcount));
    }
  }

  public void handleNewCardsResponse(NewCardsResponse n_resp) {
    for (Card card : n_resp.table1) {
      System.out.println(card.toImageFile());
      System.out.println(card.randomnum);
    }
  }

  public void handleTableResponse(TableResponse t_resp) {
    System.out.println("RESP num: " + t_resp.randomnum);
    int colindex = 0;
    int rowindex = 0;

    location_to_card.clear();
    location_to_node.clear();
    locations_clicked.clear();
    center_pane.getChildren().clear();

    for (Card card : t_resp.table1) {
      if (card.hole == false) {
        Rectangle setCard = new Rectangle();
        String imagesrc = card.toImageFile();
        imagesrc = "ui/resources/images/cards/" + imagesrc;
        // System.out.println(imagesrc);
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
        // System.out.println("Colindex: " + colindex);
        // System.out.println("Rowindex: " + rowindex);
        // System.out.println("");
        center_pane.add(setCard, colindex, rowindex);
        Location location = new Location(rowindex, colindex);
        location_to_card.put(location, card);
        location_to_node.put(location, setCard);

        setCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent t) {
            if (locations_clicked.contains(location) == false) {
              setCard.setStrokeWidth(4);
              locations_clicked.add(location);
            } else if (locations_clicked.contains(location) == true) {
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
      if ((rowindex % 3) == 0) {
        colindex = colindex + 1;
        rowindex = 0;
      }
    }
  }

  public void handleLeaveGameResponse(LeaveGameResponse resp) {
    username_to_score_field.get(resp.uname).setText("Surrendered");
  }

  public void handleEndGameResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, EndGameResponse resp) {
    center_pane.getChildren().clear();
    Button go_back = new Button("Back to Lobby");
    go_back.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        Launcher.openBrowser(primaryStage, outToServer, inFromServer);
      }
    });
    center_pane.add(go_back, 0, 0);
  }

  public void handleInitialCardsResponse(ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, InitialCardsResponse resp) {
    for (int colindex = 0; colindex < 4; ++colindex) {
      for (int rowindex = 0; rowindex < 3; ++rowindex) {
        Card card = resp.table.get(colindex * 3 + rowindex);
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
        center_pane.add(setCard, colindex, rowindex);
        Location location = new Location(rowindex, colindex);
        location_to_card.put(location, card);
        location_to_node.put(location, setCard);

        setCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent t) {
            if (locations_clicked.contains(location) == false) {
              setCard.setStrokeWidth(4);
              locations_clicked.add(location);
            } else if (locations_clicked.contains(location) == true) {
              setCard.setStrokeWidth(0);
              locations_clicked.remove(location);
            }

          }
        });

      }
    }
  }


  /*
   * Real code Submit cards to server GameThread will handle the response
   */
  private void submit_cards(ObjectOutputStream outToServer, ObjectInputStream inFromServer) {
    if (locations_clicked.size() != 3) {
      set_correct.setText("Select 3 Cards");
    }

    else {
      ArrayList<Card> cards = new ArrayList<Card>();
      for (Location location : locations_clicked) {
        cards.add(location_to_card.get(location));
      }
      SetSelectMessage set_message = new SetSelectMessage(Launcher.username, cards);
      System.out.println("sent set select message");
      set_message.send(outToServer);
    }
  }


  public Game(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer,
      ArrayList<String> users) {
    ToolBar toolbar = new ToolBar(new Button("Surrender"), new Button("Change Password"),
        new Button("Player Statistics"));

    VBox right_detail_pane = new VBox();
    for (String user : users) {
      Text text = new Text(user);
      right_detail_pane.getChildren().add(text);
      username_to_score_field.put(user, text);
    }
    /*
     * Add in user score boxes to right detail pane
     */

    center_pane = new GridPane();
    center_pane.setAlignment(Pos.CENTER);
    // center_pane.setHgap(20);
    // center_pane.setVgap(20);

    VBox left_detail_pane = new VBox();
    Button set_btn = new Button("Set!");
    set_correct = new Text("");

    left_detail_pane.getChildren().add(set_btn);
    left_detail_pane.getChildren().add(set_correct);
    left_detail_pane.setMargin(set_btn, new Insets(10, 10, 10, 10));

    set_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        submit_cards(outToServer, inFromServer);
      }
    });

    this.setTop(toolbar);
    this.setCenter(center_pane);
    this.setLeft(left_detail_pane);
    this.setRight(right_detail_pane);
    this.setMargin(center_pane, new Insets(10, 10, 10, 10));

    InitialCardsMessage start_msg = new InitialCardsMessage(Launcher.username);
    start_msg.send(outToServer);
    System.out.println("Sent initial cards message");

  }
}
