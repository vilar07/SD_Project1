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
    public static final int CONCENTRATION_SITE = 1000;
    public static final int COLLECTION_SITE = 2000;
    public static final int CRAWLING_INWARDS = 3000;
    public static final int AT_A_ROOM = 4000;
    public static final int CRAWLING_OUTWARDS = 5000;

    /**
     * Current state of the Ordinary Thief
     */
    private int state;

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
     * Ordinary Thief constructor
     * @param id the identification of the thief
     * @param museum the Museum
     * @param collectionSite the Collection Site
     * @param concentrationSite the Concentration Site
     * @param assaultParties the Assault Parties array
     * @param generalRepository the General Repository
     * @param maxDisplacement the maximum displacement
     */
    public OrdinaryThief(int id, MuseumInterface museum, CollectionSiteInterface collectionSite, ConcentrationSiteInterface concentrationSite, AssaultPartyInterface[] assaultParties, GeneralRepositoryInterface generalRepository, int maxDisplacement) {
        this.id = id;
        this.museum = museum;
        this.collectionSite = collectionSite;
        this.concentrationSite = concentrationSite;
        this.assaultParties = assaultParties;
        this.generalRepository = generalRepository;
        this.maxDisplacement = maxDisplacement;
        setState(CONCENTRATION_SITE);
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
    public void setState(int state) {
        this.state = state;
        switch (state) {
            case CONCENTRATION_SITE:
            generalRepository.setOrdinaryThiefState(id, "CONC", getSituation(), maxDisplacement);
            break;
            case COLLECTION_SITE:
            generalRepository.setOrdinaryThiefState(id, "COLL", getSituation(), maxDisplacement);
            break;
            case CRAWLING_INWARDS:
            generalRepository.setOrdinaryThiefState(id, "CRIN", getSituation(), maxDisplacement);
            break;
            case AT_A_ROOM:
            generalRepository.setOrdinaryThiefState(id, "ROOM", getSituation(), maxDisplacement);
            break;
            case CRAWLING_OUTWARDS:
            generalRepository.setOrdinaryThiefState(id, "COUT", getSituation(), maxDisplacement);
            break;
        }
    }

    /**
     * Getter for the situation of the thief
     * @return 'W' if waiting or 'P' if in party
     */
    private char getSituation() {
        return (state == CONCENTRATION_SITE || state == COLLECTION_SITE) ? 'W' : 'P';
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
