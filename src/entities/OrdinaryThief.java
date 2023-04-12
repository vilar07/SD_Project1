package src.entities;

import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;

/**
 * Ordinary Thief, one of the thieves involved in the heist
 */
public class OrdinaryThief extends Thread {
    /**
     * Current state of the Ordinary Thief
     */
    private OrdinaryThief.State state;

    /**
     * Thief unique id.
     */
    private int id;

    /**
     * Maximum displacement of the Ordinary Thief
     */
    private final int maxDisplacement;

    /**
     * Boolean value which is true if the Ordinary Thief has a canvas in its possession or false otherwise
     */
    private boolean busyHands;

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
    public enum State {
        CONCENTRATION_SITE ("CONC"),
        COLLECTION_SITE ("COLL"),
        CRAWLING_INWARDS ("CRIN"),
        AT_A_ROOM ("ROOM"),
        CRAWLING_OUTWARDS ("COUT");

        /**
         * Code associated with each state (to be used in logging)
         */
        private final String code;

        /**
         * State constructor
         * @param code code of the state
         */
        State(String code) {
            this.code = code;
        }
    }

    /**
     * Ordinary Thief constructor
     * @param id the identification of the thief
     * @param museum the Museum
     * @param collectionSite the Collection Site
     * @param concentrationSite the Concentration Site
     * @param assaultParties the Assault Parties array
     * @param generalRepository the General Repository
     */
    public OrdinaryThief(int id, MuseumInterface museum, CollectionSiteInterface collectionSite, ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties, GeneralRepositoryInterface generalRepository, int maxDisplacement) {
        this.id = id;
        this.museum = museum;
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
        this.generalRepository = generalRepository;
        this.maxDisplacement = maxDisplacement;
        busyHands = false;
        setState(State.CONCENTRATION_SITE);
    }

    /**
     * Getter for the identification number of the Ordinary Thief
     * @return the identification number of the Ordinary Thief
     */
    public int getID() {
        return id;
    }

    /**
     * Getter for the maximum displacement of the Ordinary Thief
     * @return the maximum displacement of the Ordinary Thief
     */
    public int getMaxDisplacement() {
        return maxDisplacement;
    }

    /**
     * Getter for if the Ordinary Thief has busy hands
     * @return true if thief has rolled a canvas, false otherwise
     */
    public boolean hasBusyHands() {
        return busyHands;
    }

    
    /**
     * Returns the Assault Party the Ordinary Thief is a part of
     * @return the identification of the Assault Party the Ordinary Thief belongs to or -1 if none
     */
    public int getAssaultParty() {
        for (AssaultPartyInterface assaultParty: assaultParties) {
            if (assaultParty.isMember(this)) {
                return assaultParty.getID();
            }
        }
        return -1;
    }

    /**
     * Setter for the state of the thief
     * Propagates information to the GeneralRepository
     * @param state the state
     */
    public void setState(State state) {
        this.state = state;
        generalRepository.setOrdinaryThiefState(id, state.code, getSituation(), maxDisplacement);
    }

    /**
     * Setter for the busy hands attribute
     * Propagates information to the GeneralRepository
     * @param party the Assault Party the Ordinary Thief belongs to
     * @param busyHands true if carrying a canvas, false otherwise
     */
    public void setBusyHands(int party, boolean busyHands) {
        this.busyHands = busyHands;
        // generalRepository.setAssaultPartyMember(party, id, position, busyHands ? 1 : 0);
    }

    /**
     * Getter for the situation of the thief
     * @return 'W' if waiting or 'P' if in party
     */
    private char getSituation() {
        return (state == State.CONCENTRATION_SITE || state == State.COLLECTION_SITE) ? 'W' : 'P';
    }

    /**
     * Lifecycle of the Ordinary Thief
     */
    @Override
    public void run() {
        while((concentrationSite.amINeeded())){
            System.out.println(id + " - initiating prepareExcursion");
            int assaultPartyID = concentrationSite.prepareExcursion();
            System.out.println(id + " - finished prepareExcursion in party " + assaultPartyID + "; initiating crawlIn in party " + assaultPartyID);
            while(assaultParties[assaultPartyID].crawlIn());
            System.out.println(id + " - finished crawlIn in party " + assaultPartyID + "; initiating rollACanvas in party " + assaultPartyID);
            museum.rollACanvas(assaultPartyID);
            System.out.println(id + " - finished rollACanvas in party " + assaultPartyID + "; initiating reverseDirection in party " + assaultPartyID);
            assaultParties[assaultPartyID].reverseDirection();
            System.out.println(id + " - finished reverseDirection in party " + assaultPartyID + "; initiating crawlOut in party " + assaultPartyID);
            while(assaultParties[assaultPartyID].crawlOut());
            System.out.println(id + " - finished crawlOut in party " + assaultPartyID + "; initiating handACanvas in party " + assaultPartyID);
            collectionSite.handACanvas(assaultPartyID);
            System.out.println(id + " - finished handACanvas in party " + assaultPartyID);
        }
        System.out.println(id + " terminated");
    }    
}
