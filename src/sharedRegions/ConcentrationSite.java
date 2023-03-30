package src.sharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.Constants;
import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.ConcentrationSiteInterface;

/**
 * Concentration Site where ordinary thieves wait for orders
 */
public class ConcentrationSite implements ConcentrationSiteInterface {
    /**
     * FIFO with the thieves waiting for instructions
     */
    private final Deque<OrdinaryThief> thieves;

    /**
     * Boolean variable that is false until the Master Thief announces the end of the heist
     */
    private boolean finished;

    /**
     * Identification of the next Assault Party
     */
    private int assaultPartyID;

    /**
     * Public constructor for the Concentration Site shared region
     */
    public ConcentrationSite() {
        thieves = new ArrayDeque<>(Constants.NUM_THIEVES - 1);
        finished = false;
    }

    /**
     * Called by the master thief, when enough ordinary thieves are available and there is still a
     * room with paintings
     * - Synchronization point between Master Thief and every Ordinary Thief constituting the Assault
     * Party
     * @param assaultParty the Assault Party
     * @param room number of the room in the museum
     */
    public void prepareAssaultParty(AssaultPartyInterface assaultParty, int room) {
        MasterThief master = (MasterThief) Thread.currentThread();
        master.setState(MasterThief.State.ASSEMBLING_A_GROUP);
        synchronized(this) {
            while (thieves.size() < Constants.ASSAULT_PARTY_SIZE) {
                try {
                    wait();
                } catch (InterruptedException e) {
    
                }
            }
        }
        assaultParty.setRoom(room, master.getGeneralRepository());
        assaultPartyID = assaultParty.getID();
        OrdinaryThief[] ordinaryThieves = new OrdinaryThief[Constants.ASSAULT_PARTY_SIZE];
        for (int i = 0; i < ordinaryThieves.length; i++) {
            ordinaryThieves[i] = this.thieves.poll();
        }
        assaultParty.setMembers(ordinaryThieves, master.getGeneralRepository());
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
        MasterThief masterThief = (MasterThief) Thread.currentThread();
        finished = true;
        notifyAll();
        masterThief.setState(MasterThief.State.PRESENTING_THE_REPORT);
        masterThief.getGeneralRepository().printTail(paintings);
    }

    /**
     * Called by an ordinary thief to wait for orders
     * @return true if needed, false otherwise
     */
    public synchronized boolean amINeeded() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.CONCENTRATION_SITE);
        thieves.add(thief);
        notifyAll();
        if (finished) {
            return false;
        }
        while (!finished && thieves.contains(thief)) {
            try {
                wait();
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
        synchronized (this) {
            while (ordinaryThief.getAssaultParty() == -1) {
                try {
                    wait();
                } catch (InterruptedException e) {

                }
            }
            thieves.remove(ordinaryThief);
            notifyAll();
        }
        AssaultPartyInterface assaultParty = ordinaryThief.getAssaultParties()[ordinaryThief.getAssaultParty()];
        synchronized (assaultParty) {
            while (!assaultParty.isInOperation()) {
                try {
                    assaultParty.wait();
                } catch (InterruptedException e) {

                }
            }
        }
        ordinaryThief.setState(OrdinaryThief.State.CRAWLING_INWARDS);
        return assaultParty.getID();
    }

    /**
     * Returns true if an Ordinary Thief is in the Concentration Site
     * @param thief the Ordinary Thief
     * @return true if the thief is in the Concentration Site, false otherwise
     */
    public boolean contains(OrdinaryThief thief) {
        return thieves.contains(thief);
    }
}