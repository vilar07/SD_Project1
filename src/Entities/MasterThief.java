package src.Entities;

import java.util.Arrays;

import src.Constants;
import src.Interfaces.ConcentrationSiteInterface;
import src.Interfaces.GeneralRepositoryInterface;
import src.SharedRegions.GeneralRepository;
import src.room.Room;
import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.CollectionSiteInterface;

public class MasterThief extends Thread {
    /**
     * Current state of the Master Thief
     */
    private State state;

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
        setState(State.PLANNING_THE_HEIST);
        emptyRooms = new boolean[Constants.NUM_ROOMS];
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
        this.generalRepository = repository;
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
     * Lifecycle of the Master Thief
     */
    @Override
    public void run() {
        collectionSite.startOperations();
        char operation;
        while ((operation = collectionSite.appraiseSit(assaultParties)) != 'E') {
            switch (operation) {
                case 'P':
                int assaultPartyID = collectionSite.getNextAssaultPartyID();
                concentrationSite.prepareAssaultParty(assaultParties[assaultPartyID], getNextRoom());
                assaultParties[assaultPartyID].sendAssaultParty();
                break;
                case 'R':
                collectionSite.takeARest();
                collectionSite.collectACanvas();
                break;
            }
        }
        this.concentrationSite.sumUpResults(this.collectionSite.getPaintings());
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
}
