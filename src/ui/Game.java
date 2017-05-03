package ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import message.EndGameMessage;
import message.EndGameResponse;
import message.InitialCardsMessage;
import message.InitialCardsResponse;
import message.LeaveGameMessage;
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
  private volatile HashMap<String, BorderPane> username_to_score_field = new HashMap<String, BorderPane>();


  public void handleSetResponse(SetSelectResponse resp) {
    if (!resp.is_valid) {
      System.out.println("Incorrect");
      set_correct.setText("Incorrect!");
    } else {
      String username = resp.username;
      System.out.println(resp.username + " Got a set correct");
      if (username.equals(Launcher.username)) {
        set_correct.setText("Correct!");
      }
      ((Label) username_to_score_field.get(resp.username).getRight())
          .setText(Integer.toString(resp.setcount));
    }
  }

  public void handleNewCardsResponse(NewCardsResponse n_resp) {
    for (Card card : n_resp.table1) {
      System.out.println(card.toImageFile());
      System.out.println(card.randomnum);
    }
  }

  public void handleTableResponse(TableResponse t_resp) {
    //System.out.println("RESP num: " + t_resp.randomnum);
    System.out.println("Received table response");
    int colindex = 0;
    int rowindex = 0;

    location_to_card.clear();
    location_to_node.clear();
    locations_clicked.clear();
    center_pane.getChildren().clear();

    for (Card card : t_resp.table1) {
      System.out.println(card.getDescription());
      if (card.hole == false) {
        Button setCard = new Button();
        setCard.getStyleClass().add("card");
        String imagesrc = card.toImageFile();
        imagesrc = "ui/resources/images/cards/" + imagesrc;
        // System.out.println(imagesrc);
        //System.out.println(card.randomnum);
        Image image = new Image(imagesrc);
        setCard.setGraphic(new ImageView(image));
        //ImagePattern imagePattern = new ImageView(image);
//        setCard.setHeight(200);
//        setCard.setWidth(100);
//        setCard.setFill(imagePattern);
//        setCard.setArcHeight(20);
//        setCard.setArcWidth(20);
//        setCard.setStrokeType(StrokeType.INSIDE);
//        setCard.setStroke(Color.web("blue", 0.30));
//        setCard.setStrokeWidth(0);
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
              setCard.getStyleClass().add("card-selected");
              locations_clicked.add(location);
            } else if (locations_clicked.contains(location) == true) {
              setCard.getStyleClass().remove("card-selected");
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

  public void handleLeaveGameResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, LeaveGameResponse resp) {
    
	System.out.println("Response uname: " + resp.uname);
	System.out.println("Launcher uname: " + Launcher.username);
    if (resp.uname.equals(Launcher.username))
    {
//      center_pane.getChildren().clear();
//      Button go_back = new Button("Back to Lobby");
//      go_back.setOnAction(new EventHandler<ActionEvent>() {
//        @Override
//        public void handle(ActionEvent e) {
//          Launcher.openBrowser(primaryStage, outToServer, inFromServer);
//        }
//      });
//      center_pane.add(go_back, 0, 0);
      Launcher.openBrowser(primaryStage, outToServer, inFromServer);
    }
    else {
      //username_to_score_field.get(resp.uname).setText("Surrendered");
      ((Label) username_to_score_field.get(resp.uname).getRight())
      .setText("SURRENDERED");
    }
  }

  public void handleEndGameResponse(Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, EndGameResponse resp) {
//    center_pane.getChildren().clear();
//    Button go_back = new Button("Back to Lobby");
//    go_back.setOnAction(new EventHandler<ActionEvent>() {
//      @Override
//      public void handle(ActionEvent e) {
//        Launcher.openBrowser(primaryStage, outToServer, inFromServer);
//      }
//    });
//    center_pane.add(go_back, 0, 0);
    Launcher.openBrowser(primaryStage, outToServer, inFromServer);
    EndGameMessage e_msg = new EndGameMessage();
    e_msg.send(outToServer);
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

        Button setCard = new Button();
        setCard.getStyleClass().add("card");
        // System.out.println(imagesrc);
        System.out.println(card.randomnum);
        setCard.setGraphic(new ImageView(image));
        center_pane.add(setCard, colindex, rowindex);
        Location location = new Location(rowindex, colindex);
        location_to_card.put(location, card);
        location_to_node.put(location, setCard);

        setCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent t) {
            if (locations_clicked.contains(location) == false) {
              setCard.getStyleClass().add("card-selected");
              locations_clicked.add(location);
            } else if (locations_clicked.contains(location) == true) {
              setCard.getStyleClass().remove("card-selected");
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
    this.getStyleClass().add("game");
    
    Button surrender_btn = new Button("SURRENDER");
    surrender_btn.getStyleClass().add("btn-surrender");
    surrender_btn.setPrefWidth(200);
    Button set_btn = new Button("SET");
    set_btn.getStyleClass().add("btn-set");
    set_btn.setPrefWidth(200);
    set_correct = new Text("");
    set_correct.getStyleClass().add("status");


    set_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        submit_cards(outToServer, inFromServer);
      }
    });
    
    final Pane leftSpacer = new Pane();
    HBox.setHgrow(leftSpacer, Priority.SOMETIMES);

    final Pane rightSpacer = new Pane();
    HBox.setHgrow(rightSpacer, Priority.SOMETIMES);

    
    ToolBar toolbar = new ToolBar(surrender_btn, leftSpacer, set_correct, rightSpacer, set_btn);
    surrender_btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        LeaveGameMessage l_mesg = new LeaveGameMessage(Launcher.username);
        l_mesg.send(outToServer);
      }
      
    });
    toolbar.getStyleClass().add("toolbar");

    Label scoreboard_title = new Label("SCORE");
    scoreboard_title.setAlignment(Pos.CENTER);
    VBox scoreboard_wrapper = new VBox();
    VBox scoreboard_body = new VBox();
    
    
    for (String user : users) {
      BorderPane scoreboard_entry = new BorderPane();
      Label username = new Label(user);
      Label score = new Label("0");
      scoreboard_entry.setLeft(username);
      scoreboard_entry.setRight(score);
      //Text text = new Text(user);
      username.getStyleClass().add("scoreboard-entry");
      score.getStyleClass().add("scoreboard-entry");
      scoreboard_body.getChildren().add(scoreboard_entry);
      username_to_score_field.put(user, scoreboard_entry);
    }
    
    scoreboard_wrapper.getStyleClass().add("scoreboard-wrapper");
    scoreboard_wrapper.getChildren().add(scoreboard_title);
    scoreboard_wrapper.getChildren().add(scoreboard_body);
    scoreboard_title.setPrefWidth(200);
    scoreboard_body.setPrefWidth(200);
    scoreboard_title.setMaxWidth(200);
    scoreboard_body.setMaxWidth(200);
    scoreboard_title.getStyleClass().add("scoreboard-title");
    scoreboard_body.getStyleClass().add("scoreboard-body");
    

    
    /*
     * Add in user score boxes to right detail pane
     */

    center_pane = new GridPane();
    center_pane.setGridLinesVisible(false);
    center_pane.getStyleClass().add("gameboard");
    center_pane.setAlignment(Pos.CENTER);
    //center_pane.setHgap(30);
    //center_pane.setVgap(20);



    this.setBottom(toolbar);
    this.setCenter(center_pane);
    //this.setLeft(left_detail_pane);
    this.setRight(scoreboard_wrapper);
    this.setMargin(center_pane, new Insets(10, 10, 10, 10));

    InitialCardsMessage start_msg = new InitialCardsMessage(Launcher.username);
    start_msg.send(outToServer);
    System.out.println("Sent initial cards message");

  }
}
