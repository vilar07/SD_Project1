package src.Entities;

import java.util.Random;

import src.Constants;
import src.Interfaces.ConcentrationSiteInterface;
import src.Interfaces.GeneralRepositoryInterface;
import src.SharedRegions.GeneralRepository;
import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.CollectionSiteInterface;

public class OrdinaryThief extends Thread {
    /**
     * Current state of the Ordinary Thief
     */
    private State state;

    /**
     * Thief unique id.
     */
    private int id;

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
     * Variable holding the General Repository shared region
     */
    private final GeneralRepositoryInterface generalRepository;

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

    //falta completar? (temos de passar o logger?)
    public OrdinaryThief(int id, MuseumInterface museum, CollectionSiteInterface collectionSite, ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties, GeneralRepositoryInterface repository ) {

        this.id = id;
        this.museum = museum;
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
        this.generalRepository = repository;

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
        while((operation=this.concentrationSite.amINeeded())!='E'){
            int assaultPartyID = this.concentrationSite.prepareExcursion();
            while(this.assaultParties[assaultPartyID].crawlIn());  //funçao na interface AssaultParty está a receber id do thief (metemos?)
            this.museum.rollACanvas();
            this.museum.reverseDirection();
            while(this.assaultParties[assaultPartyID].crawlOut()); //funçao na interface AssaultParty está a receber id do thief (metemos?)
            this.collectionSite.handACanvas();
        }
    }
    
}
