
package it.unibo.pps2017.core.gui;

import java.util.List;

/**
 * Interface for PlayGameController.
 */
public interface PlayGame {

  /**
   * This method is called to show the first hand (10 cards) to principal player.
   *
   * @param firstPlayerCards path's list of player cards.
   */
  void getCardsFirstPlayer(List<String> firstPlayerCards);

  /**
   * This method is called to show the command of one of four players (busso,
   * striscio and volo).
   *
   * @param player  the player that has selected command.
   * @param command command selected by user.
   */
  void getCommand(String player, String command);

  /**
   * This method is called when the turn is over: all four players has been played
   * ten cards.
   *
   * @param actualScoreMyTeam       actual score of my team.
   * @param actualScoreOpponentTeam actual score of my opponent team.
   * @param endedMatch              true if game is ended, false otherwise.
   */
  void cleanFieldEndTotalTurn(int actualScoreMyTeam,
                              int actualScoreOpponentTeam, boolean endedMatch);

  /**
   * This method is called every time to select the current player that must plays
   * the card. His empty field will become yellow instead of black to signal that
   * is his turn.
   *
   * @param player           current player that must plays the card.
   * @param partialTurnEnded true if the partial turn is ended, false otherwise.
   * @param isFirstPlayer    true if the player is the first of the set.
   * @param isReplay         true if the message is sent from ReplayActor.
   */
  void setCurrentPlayer(String player, boolean partialTurnEnded,
                        boolean isFirstPlayer, boolean isReplay);

  /**
   * This method is called to show the played card from player.
   *
   * @param player   player that has played the card.
   * @param cardPath played card's path.
   */
  void showPlayersPlayedCard(String player, String cardPath);

  /**
   * This method is called to inform view of the chosen briscola.
   *
   * @param briscola chosen briscola from other player.
   */
  void getBriscolaChosen(String briscola);

  /**
   * This method is called to show played card if is ok.
   */
  void showPlayedCardOk();

  /**
   * This method is called if the played card isn't ok to show error's label.
   */
  void showPlayedCardError();

  /**
   * This method is called to show commands for choose briscola.
   */
  void showBriscolaCommands();

  /**
   * This method is called to set four players in GUI.
   *
   * @param playersList players' list.
   */
  void setPlayersList(List<String> playersList);
}
