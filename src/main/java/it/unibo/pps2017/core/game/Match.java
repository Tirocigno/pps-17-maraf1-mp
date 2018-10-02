package it.unibo.pps2017.core.game;


import it.unibo.pps2017.core.deck.cards.Card;
import it.unibo.pps2017.core.deck.cards.Seed;
import it.unibo.pps2017.core.player.Controller;
import it.unibo.pps2017.core.player.Player;

public interface Match {

    /**
     * Add a player to the match.
     * @param newPlayer
     *  new player to add.
     * @param team
     *  Team name to add the player. Not specify for random imputation.
     */
    void addPlayer(Player newPlayer, String team);

    /**
     * Starting the game.
     */
    void startGame();

    /**
     * Check if all operations concerning the previous set are closed.
     * If it's all right, it shuffle the deck and start a new set.
     */
    void playSet();

    /**
     * Setting the briscola for the current set.
     * @param seed
     *  Current briscola's seed.
     */
    void setBriscola(Seed.Seed seed);

    /**
     * Check if the played card is accepted.
     * The card may be refused if it is not the current suit but the player has one in his hand
     * @param card
     *  Played card.
     *
     * @return
     *  True if the card's suit is correct.
     *  False otherwise.
     */
    boolean isCardOk(Card card);

    /**
     * Play a random card in the hand of the player.
     * @param player
     *  Reference player.
     * @return
     *  A random card among those that the player can drop.
     */
    Card forcePlay(Player player);
}