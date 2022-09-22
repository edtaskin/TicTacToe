package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Controller implements Initializable{
	@FXML
	private GridPane gridPane;
	@FXML
	private Label label;
	@FXML
	private Button playAgainButton;
	
	private Button[][] buttons = new Button[3][3];
	private String[] values = new String[9];
	
	private ArrayList<Integer[]> matches = new ArrayList<>();

	private BackgroundImage crossImage = new BackgroundImage(new Image(getClass().getResourceAsStream("cross.jpg")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(207, 159, true, true, true, false));
	private BackgroundImage circleImage = new BackgroundImage(new Image(getClass().getResourceAsStream("circle.jpg")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(207, 159, true, true, true, false));
	private BackgroundImage starImage = new BackgroundImage(new Image(getClass().getResourceAsStream("star.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(207, 159, true, true, true, false));
	
	private Background crossBackground = new Background(crossImage);
	private Background circleBackground = new Background(circleImage);
	private Background starBackground = new Background(starImage);
	
	private boolean P1Turn = true, gameOver = false; 
	
	private int turnCount = 1;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		int i = 0;
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				buttons[y][x] = (Button) gridPane.getChildren().get(i);
				buttons[y][x].setText("");
				buttons[y][x].setOnAction(this::playTurn);
				i++;
			}
		}
		
		for(Integer[] arr : new Integer[][]{{0,4,8}, {2,4,6}, {0,3,6}, {1,4,7}, {2,5,8}, {0,1,2}, {3,4,5}, {6,7,8}}) {
			matches.add(arr);
		}
	}
	
	public void playTurn(ActionEvent event) {
		Button currentButton = (Button) event.getSource();
		
		int row = 0, col = 0, index = 0;
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++){
				if(buttons[y][x] == currentButton) {
					row = y;
					col = x;
				}
			}
		}
		index = col + 3 * row;
		
		if(!gameOver) {
			if(P1Turn) {
				currentButton.setBackground(crossBackground);
				values[index] = "X";
			
				label.setText("P2 Turn");
				label.setStyle("-fx-text-fill: blue");
			}
			else {
				currentButton.setBackground(circleBackground);
				values[index] = "O";
				
				label.setText("P1 Turn");
				label.setStyle("-fx-text-fill: red");
			}
		}
		
		currentButton.setDisable(true);
		currentButton.setStyle("-fx-opacity: 1");
		
		if(turnCount >= 5) {
			checkForMatch(index);
		}
		else {
			P1Turn = !P1Turn;
		}
		turnCount++;
		
		if(!gameOver) {
			checkDraw();
		}
	}
	
	public void checkForMatch(int index) {
		for(Integer[] arr : matches) {
			List<Integer> list = Arrays.asList(arr);
			if(list.contains(index)) {
				boolean match = true;
				for(Integer val : list) {
					if(val != index && values[val] != values[index]) {
						match = false;
						break;
					}
				}
				if(match) {
					gameOver = true;
					showMatch(list);
					if(P1Turn) {
						label.setText("P1 WINS!");
					}
					else {
						label.setText("P2 WINS!");
					}
					label.setStyle("-fx-text-fill: #32CD32");
					
					FadeTransition fade = new FadeTransition();
					fade.setNode(label);
					fade.setDuration(Duration.seconds(2));
					fade.setCycleCount(2);
					fade.setInterpolator(Interpolator.LINEAR);
					fade.setFromValue(0);
					fade.setToValue(1);
					fade.play();
					
					ScaleTransition scale = new ScaleTransition();
					scale.setNode(label);
					scale.setDuration(Duration.seconds(2));
					scale.setCycleCount(2);
					scale.setInterpolator(Interpolator.LINEAR);
					scale.setByX(2);
					scale.setByY(2);
					scale.setAutoReverse(true);
					scale.play();
					
					scale.setOnFinished(e -> {
						playAgainButton.setDisable(false);
						playAgainButton.setVisible(true);
					});
					
					for(Node node : gridPane.getChildren()) {
						if(node instanceof Button) {
							Button button = (Button) node;
							if(!button.isDisable()) {
								button.setDisable(true);
								button.setStyle("-fx-opacity: 1");
							}
						}
					}
					
					return;
				}
			}
		}
		if(!gameOver) {
			P1Turn = !P1Turn;
		}
	}
	
	public void checkDraw() {
		for(Node node : gridPane.getChildren()) {
			if(node instanceof Button) {
				Button button = (Button) node;
				if(!button.isDisable()) {
					return;
				}
			}
		}
		gameOver = true;
		playAgainButton.setDisable(false);
		playAgainButton.setVisible(true);
		label.setText("DRAW");
		label.setStyle("-fx-text-fill: green");
	}
	
	public void showMatch(List<Integer> list) {
		for(Integer index : list) {
			int row = index % 3;
			int col = (index - row) / 3;

			Button currentButton = buttons[col][row];
			currentButton.setBackground(starBackground);
			
			RotateTransition rotate = new RotateTransition();
			rotate.setNode(currentButton);
			rotate.setDuration(Duration.seconds(2));
			rotate.setCycleCount(1);
			rotate.setInterpolator(Interpolator.LINEAR);
			rotate.setByAngle(360);
			rotate.setAxis(Rotate.Y_AXIS);
			rotate.play();
		}
		
	}
	
	public void playAgain(ActionEvent event) {
		for(Button[] arr : buttons) {
			for(Button button : arr) {
				button.setBackground(null);;
				button.setDisable(false);
			}
		}
		P1Turn = true;
		gameOver = false;
		label.setText("P1 Turn");
		label.setStyle("-fx-text-fill: red");
		values = new String[9];

		playAgainButton.setDisable(true);
		playAgainButton.setVisible(false);
	}
}
