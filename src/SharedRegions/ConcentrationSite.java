package src.SharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.Constants;
import src.Entities.MasterThief;
import src.Entities.OrdinaryThief;
import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.ConcentrationSiteInterface;

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
        synchronized(this) {
            while (thieves.size() < Constants.ASSAULT_PARTY_SIZE) {
                try {
                    wait();
                } catch (InterruptedException e) {
    
                }
            }
        }
        MasterThief master = (MasterThief) Thread.currentThread();
        master.setState(MasterThief.State.ASSEMBLING_A_GROUP);
        assaultParty.setRoomID(room);
        assaultPartyID = assaultParty.getID();
        OrdinaryThief[] thieves = new OrdinaryThief[Constants.ASSAULT_PARTY_SIZE];
        for (int i = 0; i < thieves.length; i++) {
            thieves[i] = this.thieves.poll();
        }
        assaultParty.setMembers(thieves, master.getGeneralRepository());
        synchronized (this) {
            notifyAll();
        }
    }

    public synchronized void sumUpResults(int paintings){

    }

    public synchronized boolean amINeeded() {
        if (finished) {
            return false;
        }
        OrdinaryThief thief = ((OrdinaryThief) Thread.currentThread());
        thieves.add(thief);
        notifyAll();
        thief.setState(OrdinaryThief.State.CONCENTRATION_SITE);
        while (thieves.contains(thief)) {
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
        synchronized (assaultParty) {
            while (!assaultParty.isInOperation()) {
                try {
                    wait();
                } catch (InterruptedException e) {

                }
            }
        }
        return assaultPartyID;
    }
}