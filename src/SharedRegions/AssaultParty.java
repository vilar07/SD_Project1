package src.SharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.Constants;
import src.Entities.MasterThief;
import src.Entities.OrdinaryThief;
import src.Interfaces.AssaultPartyInterface;

public class AssaultParty implements AssaultPartyInterface {
    /**
     * Queue with the identifications of the thieves in the party
     */
    private final Deque<Integer> thieves;

    /**
     * Identification number of the Assault Party
     */
    private int id;

    /**
     * Room target of the Assault Party, contains identification of the room and the distance to it
     */
    private Room room;

    /**
     * Enumerate for the situation of the Ordinary Thief in the line, can be either front, mid or back
     */
    private enum Situation {
        FRONT,
        MID,
        BACK
    }

    /**
     * Public constructor for the Assault Party shared region
     * @param id the identification number of the Assault Party
     */
    public AssaultParty(int id) {
        this.id = id;
        thieves = new ArrayDeque<>(Constants.ASSAULT_PARTY_SIZE);
        room = null;
    }

    @Override
    public void sendAssaultParty() {
        ((MasterThief) Thread.currentThread()).setState(MasterThief.State.DECIDING_WHAT_TO_DO);
    }

    @Override
    public boolean crawlIn() {
        int thief = ((OrdinaryThief) Thread.currentThread()).getID();
        do {
            Situation situation = whereAmI(thief);
            switch (situation) {
                case Situation.FRONT:
                crawlFront();
                break;
                case Situation.MID:
                crawlMid();
                break;
                case Situation.BACK:
                crawlBack();
                break;
            }
        } while (canICrawl());
    }

    @Override
    public boolean crawlOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'crawlOut'");
    }
    
    /**
     * Private method to assess the situation of the Ordinary Thief in the line
     * @param thief identification number of the Ordinary Thief
     * @return situation in the line (front, back or mid)
     */
    private Situation whereAmI(int thief) {
        if (thief == thieves.getFirst()) {
            return Situation.FRONT;
        }
        if (thief == thieves.getLast()) {
            return Situation.BACK;
        }
        return Situation.MID;
    }

    /**
     * 
     * @return
     */
    private boolean canICrawl() {
        return canICrawlFront() || canICrawlMid() || canICrawlBack();
    }
}
