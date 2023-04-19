package src.entities;

import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;

/**
 * Master Thief, the thief that commands the heist
 */
public class MasterThief extends Thread {
    public static final int PLANNING_THE_HEIST = 1000;
    public static final int DECIDING_WHAT_TO_DO = 2000;
    public static final int ASSEMBLING_A_GROUP = 3000;
    public static final int WAITING_FOR_ARRIVAL = 4000;
    public static final int PRESENTING_THE_REPORT = 5000;

    /**
     * Current state of the Master Thief
     */
    private int state;

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
     * Public constructor for Master Thief
     * Initializes the state as PLANNING_THE_HEIST and her perception that all rooms of the museum
     * are empty
     * @param collectionSite the Collection Site
     * @param concentrationSite the Concentration Site
     * @param assaultParties the Assault Parties
     * @param repository the General Repository
     */
    public MasterThief(CollectionSiteInterface collectionSite,
            ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties, GeneralRepositoryInterface repository) {
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
        this.generalRepository = repository;
        setState(PLANNING_THE_HEIST);
    }

    /**
     * Sets the state of the Master Thief and propagates it to the General Repository
     * @param state the updated Master Thief state
     */
    public void setState(int state) {
        this.state = state;
        switch (state) {
            case PLANNING_THE_HEIST:
            generalRepository.setMasterThiefState("PLAN");
            break;
            case DECIDING_WHAT_TO_DO:
            generalRepository.setMasterThiefState("DECI");
            break;
            case ASSEMBLING_A_GROUP:
            generalRepository.setMasterThiefState("AGRP");
            break;
            case WAITING_FOR_ARRIVAL:
            generalRepository.setMasterThiefState("WAIT");
            break;
            case PRESENTING_THE_REPORT:
            generalRepository.setMasterThiefState("PRES");
            break;
            default:
            break;
        }
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
