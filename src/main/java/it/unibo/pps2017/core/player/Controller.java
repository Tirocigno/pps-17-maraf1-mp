package it.unibo.pps2017.core.player;

import it.unibo.pps2017.core.deck.cards.Card;
import it.unibo.pps2017.core.deck.cards.Seed;
import scala.collection.immutable.Set;

import java.util.List;
import java.util.Map;

public interface Controller {

    Map<Player,List<Card>> getAllHands();

    boolean isCardOk(int cardIndex);

    void setTurn(Player player);

    void incSetHands();

    void setHandView(Set<Card> cardsPath);

    boolean isMyTurnToChooseBriscola(Player player);

    void setMyBriscola(Seed seed);

    void setCommandFromPlayer(Command command, Player player);

    void addPlayer(Player player);

    void cleanField();

    void getRandCard();

    void setCommand(String command);

    void totalPoints(Integer pointsTeam1, Integer pointsTeam2);

    void isPlayerTurn(Player player);

   // boolean timeExpired();
}