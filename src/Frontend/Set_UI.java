package Frontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Set_UI extends Application {

/*	public static void main(String[] args)
	{
		launch(args);
	}*/
	
	@Override
	public void start(Stage primaryStage)
	{
		BorderPane root = new BorderPane();
		ToolBar toolbar = new ToolBar(
				new Button("Surrender")
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
				setCard.setFill(Color.BLACK);
				setCard.setArcHeight(20);
				setCard.setArcWidth(20);
				center_pane.add(setCard, colindex, rowindex);
				System.out.println(""+colindex + "   " + rowindex);
			}
		}
		
		VBox left_detail_pane = new VBox();
		
		root.setTop(toolbar);
		root.setCenter(center_pane);
		root.setLeft(left_detail_pane);
		root.setRight(right_detail_pane);
		root.setMargin(center_pane, new Insets(10, 10, 10, 10));
		//root.setAlignment(center_pane, Pos.CENTER);
		
		Scene scene = new Scene(root, 1200, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
}
