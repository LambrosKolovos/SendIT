package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.Socket;


public class mainController {

    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket currentSocket;
    private String username = "";
    private Timeline timeline;


    @FXML
    VBox emailBox;

    @FXML
    ListView<HBox> displayList;

    @FXML
    Label welcome,fromLabel, subLabel, dateLabel, helpLabel;

    @FXML
    TextArea fullMessage;

    @FXML
    Button newBtn, readBtn, delBtn, logoutBtn, closeMailBtn, helpBtn;

    public void setSocket(Socket x) throws IOException {

        dis = new DataInputStream(x.getInputStream());
        dos = new DataOutputStream(x.getOutputStream());

        this.currentSocket = x;
        makeButtons();
        displayEmails();
    }

    private void makeButtons(){
        newBtn.setOnMouseEntered(t -> highlight(newBtn));
        readBtn.setOnMouseEntered(t -> highlight(readBtn));
        delBtn.setOnMouseEntered(t -> highlight(delBtn));
        logoutBtn.setOnMouseEntered(t -> highlight(logoutBtn));
        helpBtn.setOnMouseEntered(t-> helpLabel.setVisible(true));

        newBtn.setOnMouseExited(t -> unFocus(newBtn));
        readBtn.setOnMouseExited(t -> unFocus(readBtn));
        delBtn.setOnMouseExited(t -> unFocus(delBtn));
        logoutBtn.setOnMouseExited(t -> unFocus(logoutBtn));
        helpBtn.setOnMouseExited(t -> helpLabel.setVisible(false));
    }

    public void displayEmails() {

        displayList.setPlaceholder(new Label("There are no emails."));
        displayList.setOnMouseClicked(click -> {
            if(click.getClickCount() == 2) {
                HBox selectedMail  = displayList.getSelectionModel().getSelectedItem();
                try {
                    showEmail(selectedMail);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), event ->{
                    try {
                        while (dis.available() > 0) {
                            boolean isNew;
                            String sender = dis.readUTF();
                            String subject = dis.readUTF();
                            String body = dis.readUTF();
                            String date = dis.readUTF();
                            String status = dis.readUTF();

                            if (status.equals("NEW"))
                                isNew = true;
                            else
                                isNew = false;

                            HBox b = createBox(sender, subject, body, date, isNew);
                            displayList.getItems().add(0,b);
                        }
                        checkEmpty();
                    }
                    catch (IOException ex){
                        System.out.println("Error: " + ex);
                    }
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


    }

    public void setName(String s){
        welcome.setText("  Welcome, " + s);
        username = s;
    }

    private void highlight(Button b){
        b.setEffect(new Glow(0.5));
        b.setUnderline(true);
        b.setTextFill(Color.YELLOW);
    }

    private void unFocus(Button b){
        b.setEffect(null);
        b.setUnderline(false);
        b.setTextFill(Color.WHITE);
    }

    public void logout() throws IOException {
        timeline.stop();

        Stage stage = (Stage) logoutBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        loader.load();

        loginController controller = loader.getController();
        controller.setSocket(currentSocket);

        Scene homeScene = new Scene(loader.getRoot(), 800, 600);

        stage.setScene(homeScene);
        stage.show();
    }

    private HBox createBox(String sender, String subject, String body, String date, Boolean isNew){
        HBox box = new HBox();

        box.setPrefHeight(35);
        box.setPrefWidth(510);

        box.setAlignment(Pos.CENTER_RIGHT);

        Label senderLabel = new Label(sender);
        Label subjectLabel = new Label(subject);
        Label bodyLabel = new Label(body);
        Label dateLabel = new Label(date.substring(0,5));

        senderLabel.setAlignment(Pos.CENTER_LEFT);
        subjectLabel.setAlignment(Pos.CENTER_LEFT);
        bodyLabel.setAlignment(Pos.CENTER_LEFT);
        bodyLabel.setWrapText(false);
        dateLabel.setAlignment(Pos.CENTER);

        if(isNew){
            senderLabel.setStyle("-fx-font-weight: bold");
            subjectLabel.setStyle("-fx-font-weight: bold");
            bodyLabel.setStyle("-fx-font-weight: bold");
            dateLabel.setStyle("-fx-font-weight: bold");
        }

        senderLabel.setPrefWidth(125);
        subjectLabel.setPrefWidth(105);
        bodyLabel.setPrefWidth(245);
        dateLabel.setPrefWidth(55);

        box.getChildren().addAll(senderLabel, subjectLabel, bodyLabel, dateLabel);

        return box;
    }

    private void showEmail(HBox selectedMail) throws IOException {
        emailBox.setVisible(true);
        closeMailBtn.setVisible(true);
        fromLabel.setVisible(true);

        Label sender = (Label) selectedMail.getChildren().get(0);
        Label subject = (Label) selectedMail.getChildren().get(1);
        Label email =  (Label) selectedMail.getChildren().get(2);
        Label date = (Label) selectedMail.getChildren().get(3);

        fromLabel.setText(sender.getText());
        subLabel.setText("  " + subject.getText());
        fullMessage.setText("\n" + email.getText());
        dateLabel.setText(date.getText() + "  ");

        readEmail();
    }

    public void readEmail() throws IOException {
        dos.writeUTF("READ_EMAIL");

        String emailId = Integer.toString(displayList.getSelectionModel().getSelectedIndex());
        dos.writeUTF(emailId);

        Label label;
        for(int i=0; i<4; i++){
            label = (Label) displayList.getSelectionModel().getSelectedItem().getChildren().get(i);
            label.setStyle(null);
        }
    }

    public void deleteEmail() throws IOException {

        dos.writeUTF("DELETE_EMAIL");

        String emailId = Integer.toString(displayList.getSelectionModel().getSelectedIndex());
        dos.writeUTF(emailId);

        HBox box = displayList.getSelectionModel().getSelectedItem();
        displayList.getItems().remove(box);
    }

    public void newMessage() throws IOException {
        timeline.stop();

        Stage stage = (Stage) newBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("messagePage.fxml"));
        loader.load();

        messageController controller = loader.getController();
        controller.setSocket(currentSocket, username, dis, dos);

        Scene homeScene = new Scene(loader.getRoot(), 800, 600);

        stage.setScene(homeScene);
        stage.show();
    }

    public void hideEmail(){
        fromLabel.setVisible(false);
        closeMailBtn.setVisible(false);
        emailBox.setVisible(false);
    }

    private void checkEmpty(){
        if(displayList.getItems().size() == 0){
            readBtn.setDisable(true);
            delBtn.setDisable(true);
        }
        else{
            readBtn.setDisable(false);
            delBtn.setDisable(false);
        }
    }


}
