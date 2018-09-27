package it.unibo.pps2017.core.gui;

import java.util.List;

public interface PlayGame {

	/**
	 * This method is called to send the first hand (10 cards) to principal user.
	 * 
	 * @param firstUserCards
	 *            path's list of user cards.
	 */
	void getCardsFirstUser(final List<String> firstUserCards);

	/**
	 * This method is called to show the command of one of four users (busso,
	 * striscio and volo).
	 * 
	 * @param user
	 *            the user that has selected command.
	 * @param command
	 *            command selected by user.
	 */
	void getCommand(final User user, final Command command);
	
	

}
