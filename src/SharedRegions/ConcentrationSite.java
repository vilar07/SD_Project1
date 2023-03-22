package src.SharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.Constants;
import src.Entities.MasterThief;
import src.Entities.OrdinaryThief;
import src.Entities.MasterThief.State;
import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.ConcentrationSiteInterface;
import src.room.Room;

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
     * Public constructor for the Concentration Site shared region
     */
    public ConcentrationSite() {
        thieves = new ArrayDeque<>(Constants.NUM_THIEVES - 1);
        finished = false;
    }

    public void prepareAssaultParty(AssaultPartyInterface assaultParty, int room) {
        assaultParty.setRoomID(room);
        synchronized(this) {
            while (thieves.size() < Constants.ASSAULT_PARTY_SIZE) {
                try {
                    wait();
                } catch (InterruptedException e) {
    
                }
            }
        }
        OrdinaryThief[] thieves = new OrdinaryThief[Constants.ASSAULT_PARTY_SIZE];
        for (int i = 0; i < thieves.length; i++) {
            thieves[i] = this.thieves.poll();
        }
        assaultParty.setMembers(thieves);
        synchronized (this) {
            // rest of the method
            ((MasterThief) Thread.currentThread()).setState(MasterThief.State.ASSEMBLING_A_GROUP);
            notifyAll();
        }
    }

    public synchronized void sumUpResults(int paintings){

    }

    public synchronized boolean amINeeded(OrdinaryThief thief){
        return false;
    }

    public synchronized int prepareExcursion(){
        return 0;
    }
}