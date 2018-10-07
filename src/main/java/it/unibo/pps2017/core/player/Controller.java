package it.unibo.pps2017.core.player;

import it.unibo.pps2017.core.deck.cards.Card;
import it.unibo.pps2017.core.deck.cards.Seed.Seed;
import scala.collection.immutable.Set;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.Map;

public interface Controller {

    Map<Player,ListBuffer<Card>> getAllHands();

    boolean isCardOk(int cardIndex);

    void setTurn(Player player);

    void incSetHands();

    void setHandView(Set<Card> cardsPath);

    boolean isMyTurnToChooseBriscola(Player player);

    void setMyBriscola(Seed.Seed seed);

    void setCommandFromPlayer(Command.Command command, Player player);

    void addPlayer(Player player);

    void cleanField();

    void getRandCard();

    void setCommand(String command);

    void totalPoints(Integer pointsTeam1, Integer pointsTeam2);

    void isPlayerTurn(Player player);

}