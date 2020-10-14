package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String host = getParameters().getUnnamed().get(0);
        int port = Integer.parseInt(getParameters().getUnnamed().get(1));

        Socket socket = new Socket(host, port);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        loader.load();

        loginController controller = loader.getController();
        controller.setSocket(socket);

        Scene regScene = new Scene(loader.getRoot(), 800, 600);

        primaryStage.setTitle("SendIt");
        primaryStage.setScene(regScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
