package it.unibo.pps2017.core.gui;

import java.util.List;

public interface PlayGame {

	/**
	 * This method is called to show the first hand (10 cards) to principal user.
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

	/**
	 * This method is called when the last user has throw his card or at the
	 * beginning of the game. Clean the field from four played cards and replace the
	 * ImageView with the basic image for all users except who has take the last
	 * turn (or who takes four of denara). His field will be replaced by the special
	 * field card (yellow instead of black).
	 * 
	 * @param user
	 *            user that will be start next turn.
	 */
	void cleanField(final User user);

}
