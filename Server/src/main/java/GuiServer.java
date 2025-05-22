
import java.util.HashMap;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	HashMap<String, Scene> sceneMap;
	Server serverConnection;

	HBox titleBox;

	ListView<String> listItems;

	VBox serverBox;

	BorderPane mainPane;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {


		serverConnection = new Server(data -> {
			Platform.runLater(()->{
				listItems.getItems().add(data.toString());
			});
		});



		listItems = new ListView<String>();

		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("server",  createServerGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(sceneMap.get("server"));
		primaryStage.setTitle("Battleship Server");
		primaryStage.show();
		
	}
	
	public Scene createServerGui() {

		titleBox= new HBox(new Text("Battleship Server Log"));
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setStyle("-fx-font-size: 24");
		mainPane = new BorderPane();

		mainPane.setPadding(new Insets(70));
		mainPane.setStyle("-fx-background-color: #565353; -fx-font-family: 'Century Gothic';");

		serverBox = new VBox(20,titleBox, listItems);
		mainPane.setCenter(serverBox);
		return new Scene(mainPane, 500, 400);


	}

}
