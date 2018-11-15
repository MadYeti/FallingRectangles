package application;
	
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.util.ArrayList;

import application.DBConnect;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

public class Main extends Application {
	
	private int score = 0;
	private Scene gameScene = null;
	private TextField textField = null;
	private EventHandler<ActionEvent> endEventHandler = null;
	private EventHandler<MouseEvent> eventHandler = null;
	private Timeline timeline = null;
	private String nickname = null;
	private Rectangle rectangle = null;
	private Figure figure = null;
	private ArrayList<Rectangle> al = null;
	private ArrayList<Timeline> tl = null;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = new BorderPane();
		VBox vBox = new VBox();
		Button newGameButton = new Button("New game");
		Button bestScoreButton = new Button("Best score");
		Button exitButton = new Button("Exit");
		textField = new TextField("Enter your nickname");
		textField.setMaxWidth(200);
		vBox.getChildren().addAll(textField, newGameButton, bestScoreButton, exitButton);
		vBox.setSpacing(20);
		vBox.setAlignment(Pos.CENTER);
		root.setCenter(vBox);
		
		Scene scene = new Scene(root, 700, 700);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		DBConnect db = new DBConnect();
		db.getDataBaseConnection();
		
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
		
		bestScoreButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				root.setVisible(false);
				BorderPane scorePane = new BorderPane();
				Label scoreLabel = new Label();
				scoreLabel.setMinSize(100, 50);
				scoreLabel.setText("Best score: " + db.getDataFromDB());
				scoreLabel.setTextAlignment(TextAlignment.CENTER);
				Button backButton = new Button("Back to main menu");
				VBox scoreVBox = new VBox();
				scoreVBox.setSpacing(20);
				scoreVBox.getChildren().addAll(scoreLabel, backButton);
				scoreVBox.setAlignment(Pos.CENTER);
				scorePane.setCenter(scoreVBox);
				Scene scoreScene = new Scene(scorePane, 700, 700);
				scoreScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scoreScene);
				primaryStage.show();
				
				backButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						
						scorePane.setVisible(false);
						root.setVisible(true);
						primaryStage.setScene(scene);
						primaryStage.show();
					}
					
				});
				
			}
		});
		
		newGameButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				nickname = textField.getText();
				root.setVisible(false);
				BorderPane gamePane = new BorderPane();
				Canvas canvas = new Canvas();
				canvas.setHeight(650);
				canvas.setWidth(700);
				GraphicsContext gc = canvas.getGraphicsContext2D();
				HBox gameBox = new HBox();
				gameBox.setSpacing(20);
				gameBox.setPadding(new Insets(10));
				Label scoreLabel = new Label("Score: " + score);
				gameBox.getChildren().addAll(scoreLabel);
				gc.strokeLine(0, 643, 700, 643);
				gamePane.setCenter(canvas);
				gamePane.setBottom(gameBox);
				eventHandler = new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent event) {
						int j = 0;
						double x = event.getSceneX();
						double y = event.getSceneY();
						
						for(int i = 0; i < al.size(); i++) {
							if((al.get(i).xProperty().doubleValue() <= x && (al.get(i).xProperty().doubleValue() + 50) >= x) && (al.get(i).yProperty().doubleValue() <= y && (al.get(i).yProperty().doubleValue() + 50) >= y)) {
								rectangle = al.get(i);
								j = i;
							}
						}

						score++;
						scoreLabel.setText("Score: " + score);
						tl.get(j).stop();
						gamePane.getChildren().remove(rectangle);
						al.remove(j);
						tl.remove(j);
						
						figure = new Figure();
						rectangle = figure.getRectangle();
						al.add(rectangle);
						timeline = figure.getTimeline();
						tl.add(timeline);
						timeline.setOnFinished(endEventHandler);
						gamePane.getChildren().add(rectangle);
						rectangle.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
						
					}
				};
				
				endEventHandler = new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								
								for(int i = 0; i < al.size(); i++) {
									al.get(i).removeEventFilter(MouseEvent.MOUSE_PRESSED, eventHandler);
									gamePane.getChildren().remove(al.get(i));
									tl.get(i).stop();
								}
								gamePane.setVisible(false);
								db.addResultToDB(score, nickname);
								BorderPane gameOverPane = new BorderPane();
								VBox vBox = new VBox();
								Label gameOverLabel = new Label("Game over! Your score is " + score);
								Button restartButton = new Button("Restart");
								vBox.getChildren().addAll(gameOverLabel, restartButton);
								vBox.setSpacing(20);
								vBox.setAlignment(Pos.CENTER);
								gameOverPane.setCenter(vBox);
								Scene gameOverScene = new Scene(gameOverPane, 700, 700);
								gameOverScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
								primaryStage.setScene(gameOverScene);
								primaryStage.setResizable(false);
								primaryStage.show();
								
								restartButton.setOnAction(new EventHandler<ActionEvent>() {

									@Override
									public void handle(ActionEvent event) {
										
										for(int i = 0; i < 5; i++) {
											figure = new Figure();
											synchronized (figure) {
												rectangle = figure.getRectangle();
												al.add(rectangle);
												rectangle.addEventFilter(MouseEvent.MOUSE_PRESSED, eventHandler);
												timeline = figure.getTimeline();
												tl.add(timeline);
												timeline.setOnFinished(endEventHandler);
												gamePane.getChildren().add(rectangle);
												rectangle = null;
												timeline = null;
											}
										}
										score = 0;
										scoreLabel.setText("Score: " + score);
										gameOverPane.setVisible(false);
										gamePane.setVisible(true);
										primaryStage.setScene(gameScene);
										primaryStage.setResizable(false);
										primaryStage.show();
									}
								});
							}
						});
						
					}
				};
				
				al = new ArrayList<Rectangle>();
				tl = new ArrayList<Timeline>();
				for(int i = 0; i < 5; i++) {
					figure = new Figure();
					synchronized (figure) {
						rectangle = figure.getRectangle();
						al.add(rectangle);
						rectangle.addEventFilter(MouseEvent.MOUSE_PRESSED, eventHandler);
						timeline = figure.getTimeline();
						tl.add(timeline);
						timeline.setOnFinished(endEventHandler);
						gamePane.getChildren().add(rectangle);
						rectangle = null;
						timeline = null;
					}
				}

				gameScene = new Scene(gamePane, 700, 700);
				gameScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(gameScene);
				primaryStage.setResizable(false);
				primaryStage.show();
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
