package it.unibo.pps2017.core.player;

import it.unibo.pps2017.core.deck.cards.Card;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Controller {

    Map<Player,List<Card>> getAllHands();

    List<Card> getPlayerHand(Player player);

    void setHands(List<Card> hand);

    boolean isCardOk(Card c);

    void setTurn(Player player);

    void addPlayer(Player player);

    void cleanField();

    Card getRandCard();

    void setCommand(String command);

    void totalPoints(Integer pointsTeam1, Integer pointsTeam2);

   // boolean timeExpired();
}