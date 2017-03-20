package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import message.Sendable;
import ui.Login_Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/*
 * Login_Page
 * @author = Sahil Patel
 * Must make sure that you enter in the hostname and portnumber of where the server is running on
 * before you start the client.  It won't work otherwise.
 */
public class Login extends GridPane {
    
  public Login(Stage primaryStage, ObjectOutputStream outToServer, ObjectInputStream inFromServer)
  {
    this.setAlignment(Pos.CENTER);
    this.setHgap(10);
    this.setVgap(10);
    this.setPadding(new Insets(25, 25, 25, 25));
    
    Text scenetitle = new Text("Game of Set");
    this.add(scenetitle, 0, 0, 2, 1);
    
    Label userName = new Label("User Name:");
    this.add(userName, 0, 1);
    
    TextField userTextField = new TextField();
    this.add(userTextField, 1, 1);
    
    Label pw = new Label("Password:");
    this.add(pw, 0, 2);
      
    PasswordField pwBox = new PasswordField();
    this.add(pwBox, 1, 2);  
    
    
    Button sign_up_btn = new Button("Sign up");
    HBox hbSignUpBtn = new HBox(10);
    hbSignUpBtn.setAlignment(Pos.BOTTOM_LEFT);
    hbSignUpBtn.getChildren().add(sign_up_btn);
    this.add(hbSignUpBtn, 1, 4);
    
    Button sign_in_btn = new Button("Sign in");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(sign_in_btn);
    
    this.add(hbBtn, 1, 4);
    //this.setthisLinesVisible(true);
      
    Text actiontarget = new Text();
    actiontarget.setId("actiontarget");
    this.add(actiontarget, 1, 6);
              
    sign_in_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        Sendable send_msg = new Login_Message(userTextField.getText(), pwBox.getText());
        send_msg.send(outToServer);
        
        try {
        Login_Response response = (Login_Response)inFromServer.readObject();
        
        if (response.is_valid)
        {
          Launcher.openBrowser(primaryStage, outToServer, inFromServer);
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
  }
  
  public Login(Stage primaryStage)
  {
    this.setAlignment(Pos.CENTER);
    this.setHgap(10);
    this.setVgap(10);
    this.setPadding(new Insets(25, 25, 25, 25));
    
    Text scenetitle = new Text("Game of Set");
    this.add(scenetitle, 0, 0, 2, 1);
    
    Label userName = new Label("User Name:");
    this.add(userName, 0, 1);
    
    TextField userTextField = new TextField();
    this.add(userTextField, 1, 1);
    
    Label pw = new Label("Password:");
    this.add(pw, 0, 2);
      
    PasswordField pwBox = new PasswordField();
    this.add(pwBox, 1, 2);  
    
    
    Button sign_up_btn = new Button("Sign up");
    HBox hbSignUpBtn = new HBox(10);
    hbSignUpBtn.setAlignment(Pos.BOTTOM_LEFT);
    hbSignUpBtn.getChildren().add(sign_up_btn);
    this.add(hbSignUpBtn, 1, 4);
    
    Button sign_in_btn = new Button("Sign in");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(sign_in_btn);
    
    this.add(hbBtn, 1, 4);
    //this.setthisLinesVisible(true);
      
    Text actiontarget = new Text();
    actiontarget.setId("actiontarget");
    this.add(actiontarget, 1, 6);
    
    sign_in_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        Launcher.openGame(primaryStage);
      }
    });
  }
  
}
   
    