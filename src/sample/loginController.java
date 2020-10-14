package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class loginController {


    DataInputStream dis;
    DataOutputStream dos;

    @FXML
    Button registerBtn, loginBtn;

    @FXML
    TextField username;

    @FXML
    PasswordField password;

    @FXML
    Label error;

    Socket currentSocket;

    public void setSocket(Socket x) throws IOException {
        dis = new DataInputStream(x.getInputStream());
        dos = new DataOutputStream(x.getOutputStream());
        this.currentSocket = x;

        registerBtn.setOnMouseEntered(t -> highlight(registerBtn));
        registerBtn.setOnMouseExited(t -> unFocus(registerBtn));

        password.setOnAction(t-> {
            try {
                login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void highlight(Button b){
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff");
    }

    public void unFocus(Button b){
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #012f6d");
    }

    public void loadRegister() throws IOException {
        Stage registerStage = (Stage) registerBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("register.fxml"));
        loader.load();

        registerController controller = loader.getController();
        controller.setSocket(currentSocket);

        Scene regScene = new Scene(loader.getRoot(), 800, 600);

        registerStage.setTitle("SendIt");
        registerStage.setScene(regScene);
        registerStage.setResizable(false);
        registerStage.show();
    }

    public void login() throws IOException {
        dos.writeUTF("USER_LOG_IN");
        dos.writeUTF(username.getText().toLowerCase());
        dos.writeUTF(password.getText());

        if(dis.readUTF().equals("SUCCESS")){
            loadMain();
        }
        else{
            error.setText("Wrong credentials!");
        }
    }

    public void loadMain() throws IOException {
        Stage mainStage = (Stage) registerBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mailPage.fxml"));
        loader.load();

        mainController controller = loader.getController();
        controller.setSocket(currentSocket);
        controller.setName(username.getText());

        Scene regScene = new Scene(loader.getRoot(), 800, 600);

        mainStage.setScene(regScene);
        mainStage.show();
    }

}
