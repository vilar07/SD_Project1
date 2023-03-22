package src.SharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;

import src.Constants;
import src.Entities.MasterThief;
import src.Interfaces.CollectionSiteInterface;

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

    public char appraiseSit(int[] assaultPartyRooms) {
        boolean[] emptyRooms = ((MasterThief) Thread.currentThread()).getEmptyRooms();
        boolean empty = true;
        int nEmptyRooms = 0;
        for (boolean emptyRoom: emptyRooms) {
            empty = empty && emptyRoom;
            if (emptyRoom) {
                nEmptyRooms++;
            }
        }
        if (empty && assaultPartyRooms.length == 0) {
            return 'E';
        }
        if (assaultPartyRooms.length == Constants.ASSAULT_PARTIES_NUMBER || 
                (assaultPartyRooms.length == 1 && nEmptyRooms == Constants.NUM_ROOMS - 1 && !emptyRooms[assaultPartyRooms[0]])) {
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
}