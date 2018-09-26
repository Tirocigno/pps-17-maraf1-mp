package it.unibo.pps2017.core.player;

import it.unibo.pps2017.core.deck.cards.Card;

import java.util.Set;

public interface Controller {

    Set<Card> getHand();

    void setHand(Set<Card> hand);
}
