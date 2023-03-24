package src.sharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.Constants;
import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.MuseumInterface;

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
    public void sumUpResults(int paintings) {
        MasterThief masterThief = (MasterThief) Thread.currentThread();
        finished = true;
        masterThief.setState(MasterThief.State.PRESENTING_THE_REPORT);
        masterThief.getGeneralRepository().printTail(paintings);
    }

    public synchronized boolean amINeeded() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.CONCENTRATION_SITE);
        if (finished) {
            return false;
        }
        thieves.add(thief);
        notifyAll();
        while (!finished && thieves.contains(thief)) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        return true;
    }

    /**
     * Ordinary Thief waits for the Master Thief to dispatch the designed Assault Party
     * @return the Assault Party identification 
     */
    public int prepareExcursion() {
        AssaultPartyInterface assaultParty = ((OrdinaryThief) Thread.currentThread())
                .getAssaultParties()[assaultPartyID];
        thieves.remove((OrdinaryThief) Thread.currentThread());
        synchronized (assaultParty) {
            assaultParty.notifyAll();
            while (!assaultParty.isInOperation()) {
                try {
                    assaultParty.wait();
                } catch (InterruptedException e) {

                }
            }
        }
        return assaultPartyID;
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