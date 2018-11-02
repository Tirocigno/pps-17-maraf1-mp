package it.unibo.pps2017.core.gui;

public interface BasicPlayerOptions {

    /**
     * Method that can be called from both logged in and non-logged in player, to play a non competitive match
     * which will have no effect on ranking
     * @param competitive if match is competitive or not
     */
    void playMatch(boolean competitive);

    /**
     * Method that can be called from both logged in and non-logged in player, to watch a match without playing
     */
    void watchMatch();
}
