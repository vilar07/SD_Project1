package src.entities;

import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;

/**
 * Master Thief, the thief that commands the heist
 */
public class MasterThief extends Thread {
    /**
     * Current state of the Master Thief
     */
    private MasterThief.State state;

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
        PLANNING_THE_HEIST ("PLAN"),
        DECIDING_WHAT_TO_DO ("DECI"),
        ASSEMBLING_A_GROUP ("AGRP"),
        WAITING_FOR_ARRIVAL ("WAIT"),
        PRESENTING_THE_REPORT ("PRES");

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
     * Public constructor for Master Thief
     * Initializes the state as PLANNING_THE_HEIST and her perception that all rooms of the museum
     * are empty
     */
    public MasterThief(CollectionSiteInterface collectionSite,
            ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties, GeneralRepositoryInterface repository) {
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
     * Lifecycle of the Master Thief
     */
    @Override
    public void run() {
        System.out.println("startOperations");
        collectionSite.startOperations();
        char operation;
        while ((operation = collectionSite.appraiseSit()) != 'E') {
            switch (operation) {
                case 'P':
                int assaultPartyID = collectionSite.getNextAssaultPartyID();
                System.out.println("initiating prepareAssaultParty " + assaultPartyID);
                concentrationSite.prepareAssaultParty(assaultPartyID); 
                System.out.println("finished prepareAssaultParty " + assaultPartyID + "; initiating sendAssaultParty " + assaultPartyID);
                assaultParties[assaultPartyID].sendAssaultParty();
                System.out.println("finished sendAssaultParty " + assaultPartyID);
                break;
                case 'R':
                System.out.println("initiating takeARest");
                collectionSite.takeARest();
                System.out.println("finished takeARest; initiating collectACanvas");
                collectionSite.collectACanvas();
                System.out.println("finished collectACanvas");
                break;
            }
            System.out.println("appraiseSit");
        }
        System.out.println("sumUpResults");
        concentrationSite.sumUpResults(this.collectionSite.getPaintings());
        System.out.println("Terminated");
    }
}
