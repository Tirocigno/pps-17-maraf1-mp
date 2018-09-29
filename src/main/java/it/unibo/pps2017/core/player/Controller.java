package it.unibo.pps2017.core.player;

import it.unibo.pps2017.core.deck.cards.Card;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Controller {

    Map<String,List<Card>> getAllHands();

    List<Card> getPlayerHand(String player);

    void setHands(List<Card> hand);

    boolean isCardOk(Card c);

    void setTurn(String player);

   // int getCardIndex(Card c);

   // void playCard(Card c);

    //  boolean isFirstPlayedCard(Card c);

   // boolean isLastPlayedCard(Card c);

    void addPlayer(Controller player);

    void cleanField();

   // int indexNextTurn();

   // Set<Card> getInitCards();

   // void setInitCards(Set<Card> cards);

    Card getRandCard();

    void setCommand(String command);

    void totalPoints(Integer pointsTeam1, Integer pointsTeam2);

   // boolean timeExpired();
}