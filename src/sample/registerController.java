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

public class registerController {

    DataInputStream dis;
    DataOutputStream dos;
    Socket currentSocket;

    @FXML
    Button backBtn, saveBtn;

    @FXML
    TextField username, email;

    @FXML
    PasswordField pwdField, confField;


    @FXML
    Label userError, pwdError, confPwdError;


    public void setSocket(Socket x) throws IOException {

        dis = new DataInputStream(x.getInputStream());
        dos = new DataOutputStream(x.getOutputStream());

        this.currentSocket = x;

        username.textProperty().addListener(t -> generateMail());
        pwdField.textProperty().addListener(t -> checkPass());
        confField.textProperty().addListener(t -> checkConf());
    }

    public void generateMail(){
        email.setText(username.getText().toLowerCase() + "@sendit.gr");

        if(username.getText().length() < 3){
            userError.setVisible(true);
            userError.setText("Name cant be less than 3 characters!");
        }else
            userError.setVisible(false);
    }

    public void loadHome() throws IOException {
        Stage stage = (Stage) backBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        loader.load();

        loginController controller = loader.getController();
        controller.setSocket(currentSocket);

        Scene homeScene = new Scene(loader.getRoot(), 800, 600);

        stage.setScene(homeScene);
        stage.show();
    }

    public void checkPass(){
        if(pwdField.getText().length() < 4){
            pwdError.setText("Password can't be less than 4 characters!");
            pwdError.setVisible(true);
        }else{
            pwdError.setVisible(false);
        }
    }

    public void checkConf(){
        if(!confField.getText().equals(pwdField.getText())){
            confPwdError.setText("Passwords do not match!");
            confPwdError.setVisible(true);
        }else{
            confPwdError.setVisible(false);
        }
    }

    public boolean validate(){
        if(username.getText().length() < 3){
            return false;
        }

        if(pwdField.getText().length() < 4){
            return false;
        }

        if(!confField.getText().equals(pwdField.getText())){
            return false;
        }

        return true;
    }

    public void createAccount() throws IOException {

        if(validate()){
            dos.writeUTF("NEW_ACCOUNT");

            String name = username.getText();
            String password = pwdField.getText();
            String address = email.getText();

            dos.writeUTF(name);
            dos.writeUTF(address);
            dos.writeUTF(password);
            procceed();
        }
        else{
            System.out.println("Waiting for retry");
        }

    }

    public void procceed() throws IOException {
        Stage mainStage = (Stage) saveBtn.getScene().getWindow();

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
