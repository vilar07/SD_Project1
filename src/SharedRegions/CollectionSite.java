package src.SharedRegions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import src.Constants;
import src.Entities.MasterThief;
import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.CollectionSiteInterface;
import src.room.Room;

public class CollectionSite implements CollectionSiteInterface {
    /**
     * Number of paintings acquired
     */
    private int paintings;

    /**
     * FIFO of the Assault Parties
     */
    private final Deque<Integer> assaultParties;

    /**
     * CollectionSite constructor
     */
    public CollectionSite() {
        paintings = 0;
        assaultParties = new ArrayDeque<>();
        for (int i = 0; i < Constants.ASSAULT_PARTIES_NUMBER; i++) {
            assaultParties.add(i);
        }
    }

    /**
    * This is the first state change in the MasterThief life cycle it changes the MasterThief state to deciding what to do. 
    */
    public void startOperations() {
        ((MasterThief) Thread.currentThread()).setState(MasterThief.State.DECIDING_WHAT_TO_DO);
    }

    /**
     * Called by Master Thief to appraise situation: either to take a rest, prepare assault party or
     * sum up results
     * @param assaultPartyInterfaces an array with the Assault Parties
     * @return next situation
     */
    public synchronized char appraiseSit(AssaultPartyInterface[] assaultPartyInterfaces) {
        boolean[] emptyRooms = ((MasterThief) Thread.currentThread()).getEmptyRooms();
        boolean empty = true;
        int nEmptyRooms = 0;
        for (boolean emptyRoom: emptyRooms) {
            empty = empty && emptyRoom;
            if (emptyRoom) {
                nEmptyRooms++;
            }
        }
        List<Integer> assaultPartyRooms = new ArrayList<>();
        Room room;
        for (AssaultPartyInterface assaultPartyInterface: assaultPartyInterfaces) {
            room = assaultPartyInterface.getRoom();
            if (room != null) {
                assaultPartyRooms.add(room.getID());
            }
        }
        if (empty && assaultPartyRooms.size() == 0) {
            return 'E';
        }
        if (assaultPartyRooms.size() == Constants.ASSAULT_PARTIES_NUMBER || 
                (assaultPartyRooms.size() == 1 && nEmptyRooms == Constants.NUM_ROOMS - 1 && !emptyRooms[assaultPartyRooms.get(0)])) {
            return 'W';
        }
        return 'P';
    }

    public synchronized void takeARest() {

    }

    public synchronized boolean collectACanvas(int thief) {
        return false;
    }

    public synchronized void handACanvas(boolean canvas) {

    }

    /**
     * Get the number of the next Assault Party
     * @return the Assault Party identification
     */
    public int getNextAssaultPartyID() {
        return assaultParties.peek();
    }
}