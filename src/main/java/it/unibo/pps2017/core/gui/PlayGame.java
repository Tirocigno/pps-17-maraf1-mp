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
	void getCommand(final Player user, final Command command);

	/**
	 * This method is called when the turn is over: all four players has been played
	 * ten cards.
	 * 
	 * @param actualScoreMyTeam
	 *            actual score of my team.
	 * @param actualScoreOpponentTeam
	 *            actual score of my opponent team.
	 * @param endedMatch
	 *            true if game is ended, false otherwise
	 */
	void cleanFieldEndTotalTurn(final int actualScoreMyTeam, final int actualScoreOpponentTeam, final boolean endedMatch);


	/**
	 * This method is called every time to select the current player that must plays
	 * the card. His empty field will become yellow instead of black to signal that
	 * is his turn.
	 * 
	 * @param user
	 *            current player that must plays the card.
	 * @param partialTurnEnded
	 *            true if the partial turn is ended, false otherwise.
	 */
	void setCurrentPlayer(final Player user, final boolean partialTurnEnded);

	/**
	 * This method is called to show the played card from player.
	 * 
	 * @param user
	 *            user that has played the card.
	 * @param cardPath
	 *            played card's path.
	 */
	void showOtherPlayersPlayedCard(final Player user, final String cardPath);

}
