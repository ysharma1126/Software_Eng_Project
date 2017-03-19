package Frontend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
import java.util.HashMap;


public class Set_UI extends Application {

    private HashMap<String, Integer> location_to_card = new HashMap<String, Integer>();
    private HashMap<String, Node> location_to_node = new HashMap<String, Node>();
    private HashMap<String, Boolean> location_to_click_status = new HashMap<String, Boolean>();
 
	public static void main(String[] args)
	{
		launch(args);
	}
	
	private void load_initial_cards(ObjectOutputStream outToServer, ObjectInputStream inFromServer, GridPane grid)
	{
	 Initial_Cards_Message start_msg = new Initial_Cards_Message();
	 start_msg.send(outToServer);
	 try {
      Initial_Cards_Response start_response = (Initial_Cards_Response)inFromServer.readObject();
      
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
          grid.getChildren().add(setCard);
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
                location_to_click_status.put(key, false);
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
	
	
	
	
	@Override
	public void start(Stage primaryStage)
	{
	  
	    HashMap<String, Boolean> card_click_status = new HashMap<String, Boolean>();
	  
	    Color def = Color.web("0xf4f4f4", 1.0);
        final ObjectProperty<Color> warningColor = new SimpleObjectProperty<>(def);
        final StringProperty colorStringProperty = createWarningColorStringProperty(warningColor);
	  
		BorderPane root = new BorderPane();
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
		/*
		 * Here we will fetch the cards from the deck.  Server needs to have stored
		 * the order of the deck.  When the game first starts, we will fetch the
		 * first twelve cards from the deck and then populate these rectangles.
		 */
		
		
		
		
		
		for (int colindex = 0; colindex < 4; colindex++)
		{
			for (int rowindex = 0; rowindex < 3; rowindex++)
			{
				Rectangle setCard = new Rectangle();
				setCard.setHeight(200);
				setCard.setWidth(100);
				setCard.setFill(Color.WHITE);
				setCard.setArcHeight(20);
				setCard.setArcWidth(20);
				setCard.setId(""+colindex+"_"+"rowindex");
				center_pane.add(setCard, colindex, rowindex);
				setCard.setStrokeType(StrokeType.INSIDE);
				setCard.setStroke(Color.web("blue", 0.30));
				setCard.setStrokeWidth(0);
                final int card_row_ind = rowindex;
                final int card_col_ind = colindex;
				setCard.setOnMouseClicked(new EventHandler<MouseEvent>()
				{
				  @Override
				  public void handle(MouseEvent t) {
				    String key = ""+card_row_ind+""+card_col_ind;
				    if (card_click_status.containsKey(key) == false)
				    {
				      setCard.setStrokeWidth(4);
				      System.out.println("CLicked!");
				      card_click_status.put(key, true);
				    }
				    else if (card_click_status.get(key) == false)
				    {
                      setCard.setStrokeWidth(4);
                      System.out.println("CLicked!");
                      card_click_status.put(key, true);
				    }
				    else if (card_click_status.get(key) == true)
				    {
				      setCard.setStrokeWidth(0);
				      System.out.println("Unclicked");
				      card_click_status.put(key, false);
				    }
				    
				  }
				});
			}
		}
		
		VBox left_detail_pane = new VBox();
		Button set_btn = new Button("Set!");
		left_detail_pane.getChildren().add(set_btn);
        left_detail_pane.setMargin(set_btn, new Insets(10, 10, 10, 10));
        
        center_pane.styleProperty().bind(
            new SimpleStringProperty("-fx-background-color: ")
              .concat(colorStringProperty)
              .concat(";")
        );
        
        set_btn.setOnAction(new EventHandler<ActionEvent>(){
          @Override
          public void handle(ActionEvent e) {
              Timeline flash = new Timeline(
                  new KeyFrame(Duration.seconds(0),    new KeyValue(warningColor, def, Interpolator.LINEAR)),
                  new KeyFrame(Duration.seconds(0.25), new KeyValue(warningColor, def, Interpolator.LINEAR)),
                  new KeyFrame(Duration.seconds(1),    new KeyValue(warningColor, Color.ALICEBLUE,  Interpolator.LINEAR)),
                  new KeyFrame(Duration.seconds(1.25), new KeyValue(warningColor, def,  Interpolator.LINEAR))
              );
              flash.setAutoReverse(true);
              flash.play();

          }
        });
        	
		root.setTop(toolbar);
		root.setCenter(center_pane);
		root.setLeft(left_detail_pane);
		root.setRight(right_detail_pane);
		root.setMargin(center_pane, new Insets(10, 10, 10, 10));
		//root.setAlignment(center_pane, Pos.CENTER);
		
		Scene scene = new Scene(root, 1200, 800);
		primaryStage.setScene(scene);
//        scene.getStylesheets().add
//          (Frontend.Set_UI.class.getResource("Set_UI.css").toExternalForm());
		primaryStage.show();
	}
	
    private StringProperty createWarningColorStringProperty(final ObjectProperty<Color> warningColor) {
      final StringProperty colorStringProperty = new SimpleStringProperty();
      setColorStringFromColor(colorStringProperty, warningColor);
      warningColor.addListener(new ChangeListener<Color>() {
          @Override
          public void changed(ObservableValue<? extends Color> observableValue, Color oldColor, Color newColor) {
              setColorStringFromColor(colorStringProperty, warningColor);
          }
      });

      return colorStringProperty;
    }
    
    private void setColorStringFromColor(StringProperty colorStringProperty, ObjectProperty<Color> color) {
      System.out.println("Setted!");
      colorStringProperty.set(
              "rgba("
                      + ((int) (color.get().getRed()   * 255)) + ","
                      + ((int) (color.get().getGreen() * 255)) + ","
                      + ((int) (color.get().getBlue()  * 255)) + ","
                      + color.get().getOpacity() +
              ")"
      );
    }
    
    
	
	
}
