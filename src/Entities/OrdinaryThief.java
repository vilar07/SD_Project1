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
     * Array holding the Assault Parties shared regions
     */
    private final AssaultPartyInterface[] assaultParties;

    /**
     * Variable holding the Concentration Site shared region
     */
    private final ConcentrationSiteInterface concentrationSite;

    /**
     * Variable holding the Collection Site shared region
     */
    private final MuseumInterface museum;

    /**
     * Variable holding the Collection Site shared region
     */
    private final CollectionSiteInterface collectionSite;


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

    //falta completar
    public OrdinaryThief() {
        state = State.CONCENTRATION_SITE;
        Random random = new Random();
        maxDisplacement = random.nextInt(
                Constants.MAX_THIEF_DISPLACEMENT - Constants.MIN_THIEF_DISPLACEMENT) 
                + Constants.MIN_THIEF_DISPLACEMENT;
    }


    /**
     * Lifecycle of the Ordinay Thief
     */
    @Override
    public void run() {
        char operation;
        while((operation=concentrationSite.amINeeded())!='E'){
            int assaultPartyID = concentrationSite.prepareExcursion();
            while(assaultParties[assaultPartyID].crawlIn());  //funçao na interface AssaultParty está a receber id do thief (metemos?)
            museum.rollACanvas();
            museum.reverseDirection();
            while(assaultParties[assaultPartyID].crawlOut()); //funçao na interface AssaultParty está a receber id do thief (metemos?)
            collectionSite.handACanvas();
        }
    }
    
}
