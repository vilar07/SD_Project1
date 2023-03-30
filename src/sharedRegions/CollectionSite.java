package src.sharedRegions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import src.Constants;
import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;

/**
 * Collection Site where Master Thief plans and paintings are stored
 */
public class CollectionSite implements CollectionSiteInterface {
    /**
     * Number of paintings acquired
     */
    private int paintings;

    /**
     * FIFO of the available Assault Parties
     */
    private final Deque<Integer> assaultParties;

    /**
     * FIFOs of the arriving Ordinary Thieves (one for each Assault Party)
     */
    private final List<Deque<OrdinaryThief>> arrivingThieves;

    /**
     * CollectionSite constructor
     */
    public CollectionSite() {
        paintings = 0;
        assaultParties = new ArrayDeque<>();
        arrivingThieves = new LinkedList<>();
        for (int i = 0; i < Constants.ASSAULT_PARTIES_NUMBER; i++) {
            assaultParties.add(i);
            arrivingThieves.add(new ArrayDeque<>(Constants.ASSAULT_PARTY_SIZE));
        }
    }

    /**
     * Getter for the number of paintings acquired
     * @return the number of paintings
     */
    public int getPaintings() {
        return paintings;
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
        int room;
        for (AssaultPartyInterface assaultPartyInterface: assaultPartyInterfaces) {
            room = assaultPartyInterface.getRoom();
            if (room != -1) {
                assaultPartyRooms.add(room);
            }
        }
        if (empty && this.assaultParties.size() >= Constants.ASSAULT_PARTIES_NUMBER) {
            return 'E';
        }
        if (assaultParties.size() == 0 || 
                (assaultPartyRooms.size() == 1 && nEmptyRooms == Constants.NUM_ROOMS - 1 && !emptyRooms[assaultPartyRooms.get(0)])) {
            return 'R';
        }
        if (!empty) {
            return 'P';
        }
        return 'R';
    }

    /**
     * Master Thief waits while there are still Assault Parties in operation
     */
    public synchronized void takeARest() {
        MasterThief masterThief = (MasterThief) Thread.currentThread();
        masterThief.setState(MasterThief.State.WAITING_FOR_ARRIVAL);
        while (!partyHasArrived(masterThief.getAssaultParties())) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Called by the Master Thief to collect all available canvas
     */
    public synchronized void collectACanvas() {
        MasterThief masterThief = (MasterThief) Thread.currentThread();
        for (int i = 0; i < arrivingThieves.size(); i++) {
            if (arrivingThieves.get(i).size() >= Constants.ASSAULT_PARTY_SIZE) {
                for (OrdinaryThief arrivingThief: arrivingThieves.get(i)) {
                    if (arrivingThief.hasBusyHands()) {
                        paintings++;
                        arrivingThief.setBusyHands(i, false);
                    } else {
                        masterThief.setEmptyRoom(masterThief.getAssaultParties()[i].getRoom(), true);
                    }
                    arrivingThieves.get(i).remove(arrivingThief);
                }
                masterThief.getAssaultParties()[i].setInOperation(false);
                masterThief.getGeneralRepository().disbandAssaultParty(i);
                if (!assaultParties.contains(i)) {
                    assaultParties.add(i);
                }
            }
        }
        notifyAll();
        masterThief.setState(MasterThief.State.DECIDING_WHAT_TO_DO);
    }

    /**
     * Called by the Ordinary Thief to hand a canvas to the Master Thief if they have any
     * - Synchronization point between each busy-handed Ordinary Thief and the Master Thief
     */
    public synchronized void handACanvas(int party) {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.COLLECTION_SITE);
        this.arrivingThieves.get(party).add(thief);
        notifyAll();
        while (this.arrivingThieves.get(party).contains(thief)) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Get the number of the next Assault Party and remove it from the queue
     * @return the Assault Party identification
     */
    public int getNextAssaultPartyID() {
        return assaultParties.poll();
    }

    /**
     * Adds an Assault Party to the end of the FIFO
     * Called by the last member of the party to crawl out
     * @param party the Assault Party identification
     */
    public void addAssaultParty(int party) {
        assaultParties.add(party);
    }

    /**
     * Returns if all members of an Assault Party have arrived at the Collection Site
     * @param assaultParties the array with the Assault Parties
     * @return true if all members of at least 1 Assault Party have arrived, false otherwise
     */
    private boolean partyHasArrived(AssaultPartyInterface[] assaultParties) {
        for (Deque<OrdinaryThief> assaultParty: this.arrivingThieves) {
            if (assaultParty.size() >= Constants.ASSAULT_PARTY_SIZE) {
                return true;
            }
        }
        return false;
    }
}