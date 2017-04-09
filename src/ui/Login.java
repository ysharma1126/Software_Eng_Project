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
import message.SetSelectResponse;
import message.SignUpMessage;
import message.SignUpResponse;
import message.StartGameMessage;
import message.StartGameResponse;
import message.CreateRoomMessage;
import message.LoginMessage;
import message.LoginResponse;
import message.CreateRoomResponse;
import message.GamesUpdateMessage;
import message.GamesUpdateResponse;
import message.LeaveRoomResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


/*
 * Login_Page
 * @author = Sahil Patel
 * Must make sure that you enter in the hostname and portnumber of where the server is running on
 * before you start the client.  It won't work otherwise.
 */
public class Login extends GridPane {
  public void handleLoginResponse (Stage primaryStage, ObjectOutputStream outToServer,
      ObjectInputStream inFromServer, LoginResponse resp) {
    if (resp.is_valid)
    {
      Launcher.username = resp.username;
      Launcher.openBrowser(primaryStage, outToServer, inFromServer);
    }
  }
  
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
    this.add(hbSignUpBtn, 0, 4);
    
    Button sign_in_btn = new Button("Sign in");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(sign_in_btn);
    this.add(hbBtn, 1, 4);
    //this.setthisLinesVisible(true);
      
    Text actiontarget = new Text();
    actiontarget.setId("actiontarget");
    this.add(actiontarget, 1, 6);
      
    sign_up_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        System.out.println("In event handler.");
        Sendable send_msg = new SignUpMessage(userTextField.getText(), pwBox.getText());
        send_msg.send(outToServer);
        System.out.println("Sent signup");
        try {
          SignUpResponse response = (SignUpResponse)inFromServer.readObject();
          
          if (response.is_valid)
          {
            System.out.println("Signed up!");
            Launcher.username = userTextField.getText();
            Launcher.openBrowser(primaryStage, outToServer, inFromServer);
          }
          else
          {
            System.out.println("another user with that username.");
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
      
    sign_in_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        System.out.println("In sign in event handler.");
        System.out.println("username: " + userTextField.getText() + " password: " + pwBox.getText());
        LoginMessage send_msg = new LoginMessage(userTextField.getText(), pwBox.getText());
        send_msg.send(outToServer);
//        System.out.println("Sent message!");
//        try {
//        LoginResponse response = (LoginResponse)inFromServer.readObject();
//        System.out.println("Received response!");
//        if (response.is_valid)
//        {
//          Launcher.username = userTextField.getText();
//          Launcher.openBrowser(primaryStage, outToServer, inFromServer);
//        }
//        
//        } catch (ClassNotFoundException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//  
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
    
    ArrayList<String> users = new ArrayList<String>();
    users.add("Sahil");
    
    sign_in_btn.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e) {
        Launcher.openGame(primaryStage, users);
      }
    });
  }
  
}
   
    
