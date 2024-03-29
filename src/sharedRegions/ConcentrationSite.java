package src.sharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.utils.Constants;

/**
 * Concentration Site where ordinary thieves wait for orders.
 */
public class ConcentrationSite implements ConcentrationSiteInterface {
    /**
     * FIFO with the thieves waiting for instructions.
     */
    private final Deque<OrdinaryThief> thieves;

    /**
     * Boolean variable that is false until the Master Thief announces the end of the heist.
     */
    private boolean finished;

    /**
     * The General Repository where logging occurs.
     */
    private final GeneralRepositoryInterface generalRepository;

    /**
     * The Assault Party shared regions.
     */
    private final AssaultPartyInterface[] assaultParties;

    /**
     * The Collection Site shared region.
     */
    private final CollectionSiteInterface collectionSite;

    /**
     * Public constructor for the Concentration Site shared region.
     * @param generalRepository the General Repository.
     * @param assaultParties the Assault Parties.
     * @param collectionSite the Collection Site.
     */
    public ConcentrationSite(GeneralRepositoryInterface generalRepository, AssaultPartyInterface[] assaultParties, CollectionSiteInterface collectionSite) {
        this.generalRepository = generalRepository;
        this.assaultParties = assaultParties;
        this.collectionSite = collectionSite;
        thieves = new ArrayDeque<>(Constants.NUM_THIEVES - 1);
        finished = false;
    }

    /**
     * Called by the master thief, when enough ordinary thieves are available and there is still a
     * room with paintings.
     * - Synchronization point between Master Thief and every Ordinary Thief constituting the Assault Party.
     * @param assaultParty the Assault Party identification
     */
    public void prepareAssaultParty(int assaultParty) {
        MasterThief master = (MasterThief) Thread.currentThread();
        master.setState(MasterThief.ASSEMBLING_A_GROUP);
        synchronized (this) {
            while (thieves.size() < Constants.ASSAULT_PARTY_SIZE) {
                try {
                    this.wait();
                } catch (InterruptedException e) {

                }
            } 
        }
        assaultParties[assaultParty].setRoom(this.collectionSite.getNextRoom(), generalRepository);
        OrdinaryThief[] ordinaryThieves = new OrdinaryThief[Constants.ASSAULT_PARTY_SIZE];
        for (int i = 0; i < ordinaryThieves.length; i++) {
            ordinaryThieves[i] = this.thieves.poll();
        }
        assaultParties[assaultParty].setMembers(ordinaryThieves, generalRepository);
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * The Master Thief announces the end of operations
     * and shares the number of paintings acquired in the heist
     * @param paintings the number of paintings
     */
    public synchronized void sumUpResults(int paintings) {
        finished = true;
        notifyAll();
        ((MasterThief) Thread.currentThread()).setState(MasterThief.PRESENTING_THE_REPORT);
        generalRepository.printTail(paintings);
    }

    /**
     * Called by an ordinary thief to wait for orders
     * @return true if needed, false otherwise
     */
    public synchronized boolean amINeeded() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.CONCENTRATION_SITE);
        thieves.add(thief);
        this.notifyAll();
        if (finished) {
            return false;
        }
        while (!finished && thief.getAssaultParty() == -1) {
            try {
                this.wait();
            } catch (InterruptedException e) {

            } finally {
                if (finished) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Ordinary Thief waits for the Master Thief to dispatch the designed Assault Party
     * @return the Assault Party identification 
     */
    public int prepareExcursion() {
        OrdinaryThief ordinaryThief = (OrdinaryThief) Thread.currentThread();
        AssaultPartyInterface assaultParty = assaultParties[ordinaryThief.getAssaultParty()];
        synchronized (assaultParty) {
            while (!assaultParty.isInOperation()) {
                try {
                    assaultParty.wait();
                } catch (InterruptedException e) {

                }
            }
        }
        return assaultParty.getID();
    }
}
