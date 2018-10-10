package it.unibo.pps2017.core.gui;

import java.util.List;

public interface PlayGame {

	/**
	 * This method is called to show the first hand (10 cards) to principal player.
	 * 
	 * @param firstPlayerCards
	 *            path's list of player cards.
	 */
	void getCardsFirstPlayer(final List<String> firstPlayerCards);

	/**
	 * This method is called to show the command of one of four players (busso,
	 * striscio and volo).
	 * 
	 * @param player
	 *            the player that has selected command.
	 * @param command
	 *            command selected by user.
	 */
	void getCommand(final String player, final String command);

	/**
	 * This method is called when the turn is over: all four players has been played
	 * ten cards.
	 * 
	 * @param actualScoreMyTeam
	 *            actual score of my team.
	 * @param actualScoreOpponentTeam
	 *            actual score of my opponent team.
	 * @param endedMatch
	 *            true if game is ended, false otherwise.
	 */
	void cleanFieldEndTotalTurn(final int actualScoreMyTeam, final int actualScoreOpponentTeam,
			final boolean endedMatch);

	/**
	 * This method is called every time to select the current player that must plays
	 * the card. His empty field will become yellow instead of black to signal that
	 * is his turn.
	 * 
	 * @param player
	 *            current player that must plays the card.
	 * @param partialTurnEnded
	 *            true if the partial turn is ended, false otherwise.
	 */
	void setCurrentPlayer(final String player, final boolean partialTurnEnded, final boolean isFirstPlayer);

	/**
	 * This method is called to show the played card from player.
	 * 
	 * @param player
	 *            player that has played the card.s
	 * @param cardPath
	 *            played card's path.
	 */
	void showOtherPlayersPlayedCard(final String player, final String cardPath);

	/**
	 * This method is called to inform view of the chosen briscola.
	 * 
	 * @param briscola
	 *            chosen briscola from other player.
	 */
	void getBriscolaChosen(final String briscola);

	/**
	 * This method is called to set timer (time remaining to play a card)
	 * @param timer timer
	 */
	void setTimer(final int timer);
}
