package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class messageController {

    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    String username;

    @FXML
    Button backBtn;

    @FXML
    Label error;

    @FXML
    TextField senderField, subjectField, receiverField;

    @FXML
    TextArea emailField;

    public void setSocket(Socket socket, String s,DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.dos = dos;
        this.dis = dis;
        username = s;

        senderField.setText("From: " + s + "@sendIt.gr");
    }


    public void sendMail() throws IOException {
        dos.writeUTF("NEW_EMAIL");
        dos.writeUTF(username);

        if(!subjectField.getText().isEmpty())
            dos.writeUTF(subjectField.getText());
        else
            dos.writeUTF("(NO SUBJECT)");

        dos.writeUTF(receiverField.getText());
        dos.writeUTF(emailField.getText());

        if(dis.readUTF().equals("USER_FOUND")){
            goBack();
            error.setVisible(false);
        }

        else{
            error.setText("User not found!");
            error.setVisible(true);
        }

    }
    public void goBack() throws IOException {
        dos.writeUTF("CANCEL_EMAIL");
        Stage stage = (Stage) backBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mailPage.fxml"));
        loader.load();

        mainController controller = loader.getController();
        controller.setSocket(socket);
        controller.setName(username);

        Scene scene = new Scene(loader.getRoot(), 800, 600);

        stage.setScene(scene);
        stage.show();



    }

}
