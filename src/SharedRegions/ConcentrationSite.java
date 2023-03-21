package src.SharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.Constants;
import src.Entities.OrdinaryThief;
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
     * Public constructor for the Concentration Site shared region
     */
    public ConcentrationSite() {
        thieves = new ArrayDeque<>(Constants.NUM_THIEVES - 1);
        finished = false;
    }

    public void prepareAssaultParty(OrdinaryThief[] thieves, int id, int room){

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