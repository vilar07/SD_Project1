package src.entities;

import src.Constants;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;

public class MasterThief extends Thread {
    /**
     * Current state of the Master Thief
     */
    private MasterThief.State state;

    /**
     * Perception of the Master Thief about what rooms are empty
     */
    private boolean[] emptyRooms;

    /**
     * Variable holding the Collection Site shared region
     */
    private final CollectionSiteInterface collectionSite;

    /**
     * Variable holding the Concentration Site shared region
     */
    private final ConcentrationSiteInterface concentrationSite;

    /**
     * Array holding the Assault Parties shared regions
     */
    private final AssaultPartyInterface[] assaultParties;

    /**
     * Variable holding the General Repository shared region
     */
    private final GeneralRepositoryInterface generalRepository;

    /**
     * Enumerated reference type with the possible states of the Master Thief lifecycle
     */
    public enum State {
        PLANNING_THE_HEIST (1000),
        DECIDING_WHAT_TO_DO (2000),
        ASSEMBLING_A_GROUP (3000),
        WAITING_FOR_ARRIVAL (4000),
        PRESENTING_THE_REPORT (5000);

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
     * Public constructor for Master Thief
     * Initializes the state as PLANNING_THE_HEIST and her perception that all rooms of the museum
     * are empty
     */
    public MasterThief(CollectionSiteInterface collectionSite,
            ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties, GeneralRepositoryInterface repository) {
        
        emptyRooms = new boolean[Constants.NUM_ROOMS];
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
        this.generalRepository = repository;
        setState(State.PLANNING_THE_HEIST);
    }

    /**
     * Sets the state of the Master Thief and propagates it to the General Repository
     * @param state the updated Master Thief state
     */
    public void setState(State state) {
        this.state = state;
        generalRepository.setMasterThiefState(state.code);
    }

    /**
     * Getter for the perception of the empty rooms
     * @return an array with size equal to NUM_ROOMS with elements that are true if the rooms with the corresponding index are empty and false otherwise
     */
    public boolean[] getEmptyRooms() {
        return emptyRooms;
    }

    /**
     * Getter for the General Repository 
     * @return the General Repository
     */
    public GeneralRepositoryInterface getGeneralRepository() {
        return generalRepository;
    }

    /**
     * Getter for the Assault Parties
     * @return the Assault Parties
     */
    public AssaultPartyInterface[] getAssaultParties() {
        return assaultParties;
    }

    /**
     * Get next room that is not empty
     * @return the room identification
     */
    private int getNextRoom() {
        for (int i = 0; i < emptyRooms.length; i++) {
            if (!emptyRooms[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Lifecycle of the Master Thief
     */
    @Override
    public void run() {
        System.out.println("startOperations");
        collectionSite.startOperations();
        char operation;
        while ((operation = collectionSite.appraiseSit(assaultParties)) != 'E') {
            switch (operation) {
                case 'P':
                int assaultPartyID = collectionSite.getNextAssaultPartyID();
                System.out.println("prepareAssaultParty " + assaultPartyID);
                concentrationSite.prepareAssaultParty(assaultParties[assaultPartyID], getNextRoom());
                System.out.println("sendAssaultParty " + assaultPartyID);
                assaultParties[assaultPartyID].sendAssaultParty();
                break;
                case 'R':
                System.out.println("takeARest");
                collectionSite.takeARest();
                System.out.println("collectACanvas");
                collectionSite.collectACanvas();
                break;
            }
            System.out.println("appraiseSit");
        }
        System.out.println("sumUpResults");
        concentrationSite.sumUpResults(this.collectionSite.getPaintings());
    }
}
