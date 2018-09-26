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
	ImageView thirdCard;
	
	@FXML
	ImageView fourthCard;
	
	@FXML
	ImageView fifthCard;
	
	@FXML
	ImageView sixthCard;
	
	@FXML
	ImageView seventhCard;
	
	@FXML
	ImageView eighthCard;
	
	@FXML
	ImageView ninthCard;
	
	@FXML
	ImageView tenthCard;
	
	@FXML
	Button button;
	
	@FXML
	Button bussoButton;
	
	@FXML
	Button voloButton;
	
	@FXML
	Button striscioButton;

	@FXML
	ImageView bussoNotify;
	
	@FXML
	ImageView striscioNotify;
	
	@FXML
	ImageView voloNotify;


	
	/**
	 * This metod permits to view the command that principal user selected.
	 * Possibilities: busso, striscio, volo
	 * 
	 * @param buttonPressed button pressed from principal user
	 */
	public void signalMyCommands(ActionEvent buttonPressed) {
		if (buttonPressed.getSource().equals(bussoButton)) {
			bussoNotify.setVisible(true);
		} else if (buttonPressed.getSource().equals(striscioButton)) {
			striscioNotify.setVisible(true);
		} else {
			voloNotify.setVisible(true);
		}	
	}
}
