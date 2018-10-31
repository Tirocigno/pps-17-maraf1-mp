package it.unibo.pps2017.core.gui;

class PlayGameViewUtils {

    private static final String COMMANDS_PATH = "commands/";
    private static final String BACK = "cards/back.png";
    private static final String BACK_REVERSE = "cards/backReverse.png";
    private static final String EMPTY_FIELD = "cards/emptyField.png";
    private static final String EMPTY_FIELD_MY_TURN = "cards/emptyFieldMyTurn.png";
    private static final String WIN_MATCH = "images/win.png";
    private static final String LOSE_MATCH = "images/lose.png";
    private static final String BRISCOLA_CHOSEN = "Briscola chosen: ";
    private static final String MY_TEAM_SCORE = "My team's score: ";
    private static final String OPPONENT_TEAM_SCORE = "Opponent team's score: ";
    private static final String CHOOSE_YOUR_BRISCOLA = "Choose your briscola";
    private static final String FORMAT = ".png";
    private static final String PLAY_GAME_VIEW_FXML = "PlayGameView.fxml";
    private static final int DURATION_ANIMATION = 3;
    private static final int START_ANIMATION_POSITION = 1;
    private static final int END_ANIMATION_POSITION = 2;
    private static final int MAX_CARDS_IN_HAND = 10;
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 685;
    private static final int DISCOVERY_PORT = 2000;

    static int getMinWidth() { return MIN_WIDTH; }

    static int getMinHeight() {  return MIN_HEIGHT; }

    static int getDiscoveryPort() { return DISCOVERY_PORT; }

    static int getDurationAnimation() {
        return DURATION_ANIMATION;
    }

    static int getStartAnimationPosition() {
        return START_ANIMATION_POSITION;
    }

    static int getEndAnimationPosition() {
        return END_ANIMATION_POSITION;
    }

    static int getMaxCardsInHand() {
        return MAX_CARDS_IN_HAND;
    }

    static String getFormat() {
        return FORMAT;
    }

    static String getCommandsPath() {
        return COMMANDS_PATH;
    }

    static String getBack() {
        return BACK;
    }

    static String getBackReverse() {
        return BACK_REVERSE;
    }

    static String getEmptyField() {
        return EMPTY_FIELD;
    }

    static String getEmptyFieldMyTurn() {
        return EMPTY_FIELD_MY_TURN;
    }

    static String getWinMatch() {
        return WIN_MATCH;
    }

    static String getLoseMatch() {
        return LOSE_MATCH;
    }

    static String getBriscolaChosen() {
        return BRISCOLA_CHOSEN;
    }

    static String getMyTeamScore() {
        return MY_TEAM_SCORE;
    }

    static String getOpponentTeamScore() {
        return OPPONENT_TEAM_SCORE;
    }

    static String getChooseYourBriscola() {
        return CHOOSE_YOUR_BRISCOLA;
    }

    static String getPlayGameViewFxml() { return PLAY_GAME_VIEW_FXML; }
}
