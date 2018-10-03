package it.unibo.pps2017.core.player;

import it.unibo.pps2017.core.deck.cards.Card;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Controller {

    Map<Player,List<Card>> getAllHands();

    boolean isCardOk(int cardIndex);

    void setTurn(Player player);

    void incSetHands();

    void addPlayer(Player player);

    void cleanField();

    Card getRandCard();

    void setCommand(String command);

    void totalPoints(Integer pointsTeam1, Integer pointsTeam2);

    void isPlayerTurn(Player player);

   // boolean timeExpired();
}