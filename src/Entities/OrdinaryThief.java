package src.Entities;

import java.util.Random;

import src.Constants;

public class OrdinaryThief extends Thread {
    /**
     * Current state of the Ordinary Thief
     */
    private State state;

    /**
     * Maximum displacement of the Ordinary Thief
     */
    private final int maxDisplacement;

    /**
     * Enumerated reference type with the possible states of the Ordinary Thief lifecycle
     */
    private enum State {
        CONCENTRATION_SITE (1000),
        COLLECTION_SITE (2000),
        CRAWLING_INWARDS (3000),
        AT_A_ROOM (4000),
        CRAWLING_OUTWARDS (5000);

        /**
         * Code associated with each state (to be used in logging)
         */
        private final int code;

        /**
         * State constructor
         * @param code code of the state
         */
        State(int code) {
            this.code = code;
        }
    }

    public OrdinaryThief() {
        state = State.CONCENTRATION_SITE;
        Random random = new Random();
        maxDisplacement = random.nextInt(
                Constants.MAX_THIEF_DISPLACEMENT - Constants.MIN_THIEF_DISPLACEMENT) 
                + Constants.MIN_THIEF_DISPLACEMENT;
    }
    
}
