package src.Entities;

import src.Constants;
import src.Interfaces.ConcentrationSiteInterface;
import src.Interfaces.GeneralRepositoryInterface;
import src.SharedRegions.GeneralRepository;
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
    private enum State {
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
            ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties) {
        state = State.PLANNING_THE_HEIST;
        emptyRooms = new boolean[Constants.NUM_ROOMS];
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
    }

    /**
     * Lifecycle of the Master Thief
     */
    @Override
    public void run() {
        char operation;
        collectionSite.startOperations();
        while ((operation = collectionSite.appraiseSit()) != 'E') {
            switch (operation) {
                case 'P':
                concentrationSite.prepareAssaultParty(null, collectionSite.getAssaultID(),
                        collectionSite.getRoomID());
                assaultParties[collectionSite.getAssaultID()].sendAssaultParty();
                break;
                case 'R':
                collectionSite.takeARest();
                collectionSite.collectACanvas();
                break;
            }
        }
        concentrationSite.sumUpResults(collectionSite.getNumberOfCanvas());
    }
}