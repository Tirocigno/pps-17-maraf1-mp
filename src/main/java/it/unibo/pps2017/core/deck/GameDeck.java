package it.unibo.pps2017.core.deck;

import it.unibo.pps2017.core.deck.cards.Card;
import scala.Int;
import scala.Tuple2;

import java.util.Collection;
import java.util.List;

/**
 * This interface is used to define the behaviour of the deck used in game.
 * A Deck should be able to split the cards inside among the players,
 * register the score of every set and every card played.
 */
public interface GameDeck {

    /**
     * This method is called before a set to shuffle the cards inside the deck.
     */
    void shuffle();

    /**
     * Splits the cards inside the deck into four hand of cards.
     *
     * @return a list containing the four hands as collections.
     */
    List<Collection<Card>> distribute();

    /**
     * This method signal the deck the set is over, compute the new score and it return it.
     *
     * @return a Scala Tuple containing two integers, the values of the team's current scores.
     */
    Tuple2<Int, Int> computeSetScore();

    /**
     * Register in the score storage all the card played in a turn.
     *
     * @param playedCards a Collection containing the four card played in a turn.
     * @param teamIndex   the index of the team which earned those cards.
     */
    void registerTurnPlayedCards(final List<Card> playedCards, final int teamIndex);
}
