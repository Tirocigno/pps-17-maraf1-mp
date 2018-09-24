package it.unibo.pps2017.core.gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class MainController {

	@FXML
	ImageView wallpaper;
	
	@FXML
	ImageView firstCard;
	
	@FXML
	ImageView secondCard;
	
	@FXML
	Button button;

	
	public void getNumber(ActionEvent e) {

		firstCard.setVisible(true);
		secondCard.setVisible(true); 
	}
}
