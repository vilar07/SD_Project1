package src.entities;

import java.util.Random;

import src.Constants;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;

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
     * Boolean value which is true if Ordinary Thief has a canvas in its possession or false otherwise
     */
    private boolean busyHands;

    /**
     * Position of the Ordinary Thief in relation to the room target
     */
    private int position;

    /**
     * Boolean value that is true if going into the museum or false if going in the opposite direction
     */
    private boolean directionIn;

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

    /**
     * Ordinary Thief constructor
     * @param id the identification of the thief
     * @param museum the Museum
     * @param collectionSite the Collection Site
     * @param concentrationSite the Concentration Site
     * @param assaultParties the Assault Parties array
     * @param generalRepository the General Repository
     */
    public OrdinaryThief(int id, MuseumInterface museum, CollectionSiteInterface collectionSite, ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties, GeneralRepositoryInterface generalRepository) {
        this.id = id;
        this.museum = museum;
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
        this.generalRepository = generalRepository;
        setState(State.CONCENTRATION_SITE);
        Random random = new Random(System.currentTimeMillis());
        maxDisplacement = random.nextInt(
                Constants.MAX_THIEF_DISPLACEMENT - Constants.MIN_THIEF_DISPLACEMENT + 1)
                + Constants.MIN_THIEF_DISPLACEMENT;
        busyHands = false;
        position = 0;
        directionIn = true;
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
     * Getter for the position of the Ordinary Thief relative to the room target
     * @return the position (from 0 up to room distance)
     */
    public int getPosition() {
        return position;
    }

    /**
     * Getter for the direction of the Ordinary Thief
     * @return true if going in, false if going out
     */
    public boolean getDirectionIn() {
        return directionIn;
    }

    /**
     * Getter for Assault Parties
     * @return array with all Assault Parties
     */
    public AssaultPartyInterface[] getAssaultParties() {
        return assaultParties;
    }

    /**
     * Getter for the General Repository
     * @return the General Repository
     */
    public GeneralRepositoryInterface getGeneralRepository() {
        return generalRepository;
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
     * Setter for the position of the thief in relation to the room of the museum
     * Propagates information to the GeneralRepository
     * @param party the Assault Party the Ordinary Thief belongs to
     * @param position the position
     */
    public void setPosition(int party, int position) {
        this.position = position;
        generalRepository.setAssaultPartyMember(party, id, position, hasBusyHands() ? 1 : 0);
    }

    /**
     * Setter for the busy hands attribute
     * Propagates information to the GeneralRepository
     * @param party the Assault Party the Ordinary Thief belongs to
     * @param busyHands true if carrying a canvas, false otherwise
     */
    public void setBusyHands(int party, boolean busyHands) {
        this.busyHands = busyHands;
        generalRepository.setAssaultPartyMember(party, id, position, busyHands ? 1 : 0);
    }

    /**
     * Setter for the direction of the Ordinary Thief
     * @param directionIn true if going in, false if going out
     */
    public void setDirectionIn(boolean directionIn) {
        this.directionIn = directionIn;
    }

    /**
     * Getter for the situation of the thief
     * @return 'W' if waiting or 'P' if in party
     */
    private char getSituation() {
        if (state == State.CONCENTRATION_SITE || state == State.COLLECTION_SITE) {
            return 'W';
        }
        return 'P';
    }

    /**
     * Lifecycle of the Ordinary Thief
     */
    @Override
    public void run() {
        while((concentrationSite.amINeeded())){
            int assaultPartyID = concentrationSite.prepareExcursion();
            while(assaultParties[assaultPartyID].crawlIn());  //funçao na interface AssaultParty está a receber id do thief (metemos?) R.: Nao sei ainda
            museum.rollACanvas(assaultPartyID);
            museum.reverseDirection(assaultPartyID);
            while(assaultParties[assaultPartyID].crawlOut()); //funçao na interface AssaultParty está a receber id do thief (metemos?) R.: Nao sei ainda
            collectionSite.handACanvas();
        }
    }    
}