//Originally created by Almas Baimagambetov
//Modified by Kate Panlilio

import java.util.HashMap;
import java.util.Random;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class BattleshipMain extends Application {
	private boolean running = false;
	private Board enemyBoard, playerBoard;


	private int shipsToPlace = 5;


	private boolean enemyTurn = false;


	Text directions, numShipsLeft, enemyShipsSunk, playerShipsSunk;
	HBox directionsBox, resultText, turnText;


	VBox rightBox, leftBox;


	Random random = new Random();


	HBox logo, playerBox, enemyBox;


	boolean playerWin = false;


	ListView playersList;
	BorderPane root, leftSide, rightSide, resultsPane;


	HashMap<String, Scene> sceneMap;


	PauseTransition pause = new PauseTransition(Duration.seconds(1.0));


	public Scene createContent(Stage primaryStage) {
		root = new BorderPane();
		root.setPadding(new Insets(10));
		leftSide = new BorderPane();
		rightSide = new BorderPane();


		directions = new Text("DIRECTIONS:\n" +
				"1) Place 5 of your ships on the bottom grid.\n" +
				"Left-click for vertical placement\n" +
				"Right-click for horizontal placement\n" +
				"2) Start attacking your opponent's ship by \n" +
				"clicking on the square in the top grid\n" +
				"3) Have fun!");
		directions.setStyle("-fx-fill: #b3c6da; -fx-font-size: 16");
		directionsBox = new HBox(directions);
		directionsBox.setStyle("-fx-background-color: #171748;");
		directionsBox.setPadding(new Insets(5));


		root.setStyle("-fx-background-color: #12293b;");
		ImageView battleshipLogo = new ImageView(new Image("battleship_logo.jpg"));
		battleshipLogo.setFitWidth(300);
		battleshipLogo.setFitHeight(70);
		logo = new HBox(battleshipLogo);
		logo.setAlignment(Pos.CENTER);
		root.setTop(logo);
		root.setRight(rightSide);
		root.setLeft(leftSide);
		enemyShipsSunk = new Text("Enemy ships: 5");
		enemyShipsSunk.setStyle("-fx-fill: #b3c6da; -fx-font-size: 18");
		playerShipsSunk = new Text("Your ships: 5");
		playerShipsSunk.setStyle("-fx-fill: #b3c6da; -fx-font-size: 18");


		enemyBoard = new Board(true, event -> {
			if (!running)
				return;


			Cell cell = (Cell) event.getSource();
			if (cell.wasShot)
				return;


			enemyTurn = !cell.shoot();


			enemyShipsSunk.setText("Enemy ships: " + enemyBoard.ships);


			if(enemyTurn){
				pause.play();
				clientConnection.send("Enemy Moved");
				enemyBoard.setDisable(true);
				numShipsLeft.setText("Opponent's turn!");
			}


			if (enemyBoard.ships == 0) {
				playerWin = true;
				clientConnection.send("Player won!");
				sceneMap.put("results", createResults());
				sceneMap.get("results");
				primaryStage.setScene(sceneMap.get("results"));
			}
			pause.setOnFinished(e->{
				enemyMove(primaryStage);


				enemyBoard.setDisable(false);
				clientConnection.send("Turn changed!");
				numShipsLeft.setText("Your turn!");

			});


		});


		playerBoard = new Board(false, event -> {
			if (running)
				return;


			Cell cell = (Cell) event.getSource();
			if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
				shipsToPlace--;
				numShipsLeft.setText("Ships left to place: " + shipsToPlace);
				if (shipsToPlace == 0) {
					clientConnection.send("Game Started");
					numShipsLeft.setText("Your turn!");


					startGame();
				}
			}
		});


		numShipsLeft = new Text("Ships left to place: 5");
		numShipsLeft.setStyle("-fx-fill: #b3c6da; -fx-font-size: 24");
		turnText = new HBox(numShipsLeft);
		turnText.setPadding(new Insets(50));
		turnText.setAlignment(Pos.CENTER);


		HBox shipInfo = new HBox(10, enemyShipsSunk, playerShipsSunk);


		Text enemy = new Text("Opponent's\n  board");
		enemy.setStyle("-fx-fill: #b3c6da; -fx-font-size: 16");
		Text player = new Text("Your board ");
		player.setStyle("-fx-fill: #b3c6da; -fx-font-size: 16");


		HBox enemyLabel = new HBox(enemy);
		enemyLabel.setAlignment(Pos.CENTER);
		HBox playerLabel = new HBox(player);
		playerLabel.setAlignment(Pos.CENTER);


		enemyBox = new HBox(10, enemyLabel, enemyBoard);
		playerBox = new HBox(10, playerLabel, playerBoard);


		leftBox = new VBox(25, enemyBox, playerBox);
		leftBox.setAlignment(Pos.CENTER);


		playersList = new ListView<>();


		rightBox = new VBox(25, directionsBox, shipInfo, playersList, turnText);
		leftSide.setCenter(leftBox);
		leftSide.setPadding(new Insets(10));
		rightSide.setPadding(new Insets(10));


		rightSide.setTop(rightBox);
		playersList.setStyle("-fx-max-height: 100; ");


		clientConnection = new Client(data->{
			Platform.runLater(()->{playersList.getItems().add(data.toString());
			});
		});
		clientConnection.start();


		return new Scene(root,800, 750);
	}


	public Scene createResults(){
		resultsPane = new BorderPane();
		resultsPane.setPadding(new Insets(50));
		Text result = new Text("");
		result.setStyle("-fx-fill: #ececec; -fx-font-size: 36");
		if(playerWin){
			result.setText("CONGRATULATIONS!\n YOU WON!");
			resultsPane.setStyle("-fx-background-color: #19504d");
		}
		else{
			result.setText("      YOU LOST!\nBETTER LUCK NEXT TIME!");
			resultsPane.setStyle("-fx-background-color: #861833");


		}


		resultText = new HBox(result);
		resultText.setAlignment(Pos.CENTER);
		resultsPane.setCenter(resultText);
		return new Scene(resultsPane, 500, 500);
	}
	public void enemyMove(Stage primaryStage) {
		while (enemyTurn) {
			int x = random.nextInt(10);
			int y = random.nextInt(10);


			Cell cell = playerBoard.getCell(x, y);


			if (cell.wasShot){
				continue;
			}
			enemyTurn = cell.shoot();


			if(cell.ship != null){//if it's ship cell
				int nextHitDirection = random.nextInt(2); //takes a 50-50 chance to either hit vertically or horizontally next
				if (nextHitDirection == 0){ //hitting horizontally
					int closestX = random.nextInt(1) + (x-1); //getting a random x near the ship that has been hit
					if(closestX < 0){
						closestX = x;
					}
					cell = playerBoard.getCell(closestX, y);
				}
				else{ //hitting vertically
					int closestY = random.nextInt(1) + (y-1);
					if(closestY < 0){
						closestY = y;
					}
					cell = playerBoard.getCell(x, closestY);
				}
				enemyTurn = cell.shoot();
			}


			playerShipsSunk.setText("Your ships: " + playerBoard.ships);


			if (playerBoard.ships == 0) {
				clientConnection.send("player lost");
				sceneMap.put("results", createResults());
				sceneMap.get("results");
				primaryStage.setScene(sceneMap.get("results"));
			}
		}


	}


	private void startGame() {
		// place enemy ships
		int type = 5;


		while (type > 0) {
			int x = random.nextInt(10);
			int y = random.nextInt(10);


			if (enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
				type--;
			}
		}
		running = true;
	}
	Client clientConnection;


	@Override
	public void start(Stage primaryStage) throws Exception {


		sceneMap = new HashMap<>();
		sceneMap.put("gameplay", createContent(primaryStage));
		primaryStage.setTitle("Battleship");
		primaryStage.setScene(sceneMap.get("gameplay"));
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
