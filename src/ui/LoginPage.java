package ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import message.Sendable;
import message.LoginMessage;
import message.LoginResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;


/*
 * Login_Page
 * @author = Sahil Patel
 * Must make sure that you enter in the hostname and portnumber of where the server is running on
 * before you start the client.  It won't work otherwise.
 */
public class LoginPage extends Application {

	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage)
	{
	    /*
	     * Make hostname = the server address
	     * Make portnumber = the port that the server is listening to
	     */
	  
	    String hostname = "";
	    int portnumber = 0;
	    
	    try {
	      Socket connectSocket = new Socket(hostname, portnumber);
	      ObjectOutputStream outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
	      ObjectInputStream inFromServer = new ObjectInputStream(connectSocket.getInputStream());    
	      
	      /* 
	       * Create the Message Object to send
	       * The Message object will be of type Login_Message
	       */
	      
	      LinkedList<Sendable> outToServerList = new LinkedList<Sendable>();
	      LinkedList<LoginMessage> inFromServerList = new LinkedList<LoginMessage>();
	      
	      primaryStage.setTitle("JavaFX Welcome");
	        
	      GridPane grid = new GridPane();
	      grid.setAlignment(Pos.CENTER);
	      grid.setHgap(10);
	      grid.setVgap(10);
	      grid.setPadding(new Insets(25, 25, 25, 25));
	      
	      Text scenetitle = new Text("Game of Set");
	      scenetitle.setId("welcome-text");
	      grid.add(scenetitle, 0, 0, 2, 1);
	        
	      Label userName = new Label("User Name:");
	      grid.add(userName, 0, 1);
	        
	      TextField userTextField = new TextField();
	      grid.add(userTextField, 1, 1);
	        
	      Label pw = new Label("Password:");
	      grid.add(pw, 0, 2);
	        
	      PasswordField pwBox = new PasswordField();
	      grid.add(pwBox, 1, 2);  
	      
	      
	      Button sign_up_btn = new Button("Sign up");
	      HBox hbSignUpBtn = new HBox(10);
	      hbSignUpBtn.setAlignment(Pos.BOTTOM_LEFT);
	      hbSignUpBtn.getChildren().add(sign_up_btn);
	      grid.add(hbSignUpBtn, 1, 4);
	      
	      Button sign_in_btn = new Button("Sign in");
	      HBox hbBtn = new HBox(10);
	      hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	      hbBtn.getChildren().add(sign_in_btn);
	        
	      grid.add(hbBtn, 1, 4);
	      //grid.setGridLinesVisible(true);
	        
	      Text actiontarget = new Text();
	      actiontarget.setId("actiontarget");
	      grid.add(actiontarget, 1, 6);
	                
	      sign_in_btn.setOnAction(new EventHandler<ActionEvent>(){
	        @Override
	        public void handle(ActionEvent e) {
	          Sendable send_msg = new LoginMessage(userTextField.getText(), pwBox.getText());
	          send_msg.send(outToServer);
	          
	          try {
              LoginResponse response = (LoginResponse)inFromServer.readObject();
              
              if (response.is_valid)
              {
                /*
                 * GO TO LOBBY PAGE THEN, STORE USERNAME.
                 */
              }
              
	          } catch (ClassNotFoundException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
              } catch (IOException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
              }

	        }
	      });
	        
	      Scene scene = new Scene(grid, 1200, 800);
	        
	      primaryStage.setScene(scene);
	      scene.getStylesheets().add
	          (ui.LoginPage.class.getResource("Login.css").toExternalForm());
	      primaryStage.show();     
	    } catch (ConnectException e1) {
          System.err.println("Did not enter in proper server address/portnumber!");
        } catch (UnknownHostException e1) {
        // TODO Auto-generated catch block
          System.err.println("Did not enter in proper server address/portnumber!");
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
	}
	
}
