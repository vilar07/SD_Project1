package src.sharedRegions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import src.Constants;
import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;

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
     * FIFO of the Ordinary Thieves with canvas
     */
    private final Deque<OrdinaryThief> arrivingThieves;

    /**
     * CollectionSite constructor
     */
    public CollectionSite() {
        paintings = 0;
        assaultParties = new ArrayDeque<>();
        for (int i = 0; i < Constants.ASSAULT_PARTIES_NUMBER; i++) {
            assaultParties.add(i);
        }
        arrivingThieves = new ArrayDeque<>();
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
        if (empty && assaultParties.size() == Constants.ASSAULT_PARTIES_NUMBER) {
            return 'E';
        }
        if (assaultParties.size() == 0 || 
                (assaultPartyRooms.size() == 1 && nEmptyRooms == Constants.NUM_ROOMS - 1 && !emptyRooms[assaultPartyRooms.get(0)])) {
            return 'R';
        }
        return 'P';
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
        List<Integer> arrivingParties = getArrivingParties();
        MasterThief masterThief = (MasterThief) Thread.currentThread();
        for (OrdinaryThief arrivingThief: arrivingThieves) {
            if (arrivingParties.contains(arrivingThief.getAssaultParty())) {
                if (arrivingThief.hasBusyHands()) {
                    paintings++;
                    arrivingThief.setBusyHands(arrivingThief.getAssaultParty(), false);
                } else {
                    masterThief.setEmptyRoom(arrivingThief.getAssaultParties()[arrivingThief.getAssaultParty()].getRoom(), true);
                }
                arrivingThieves.remove(arrivingThief);
            }
        }
        notifyAll();
        for (int arrivingParty: arrivingParties) {
            masterThief.getAssaultParties()[arrivingParty].setInOperation(false);
            masterThief.getGeneralRepository().disbandAssaultParty(arrivingParty);
            addAssaultParty(arrivingParty);
        }
        masterThief.setState(MasterThief.State.DECIDING_WHAT_TO_DO);
    }

    /**
     * Called by the Ordinary Thief to hand a canvas to the Master Thief if they have any
     * - Synchronization point between each busy-handed Ordinary Thief and the Master Thief
     */
    public synchronized void handACanvas() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.COLLECTION_SITE);
        arrivingThieves.add(thief);
        notifyAll();
        while (arrivingThieves.contains(thief)) {
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
     * Returns a list with the identification of the arriving Assault Parties
     * @return an array with the identification of the arriving parties or null if none
     */
    private List<Integer> getArrivingParties() {
        if (arrivingThieves.size() < Constants.ASSAULT_PARTY_SIZE) {
            return null;
        }
        int[] tmp = new int[Constants.ASSAULT_PARTIES_NUMBER];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = 0;
        }
        for (OrdinaryThief arrivingThief: arrivingThieves) {
            int assaultParty = arrivingThief.getAssaultParty();
            if (assaultParty != -1) {
                tmp[assaultParty]++;
            }
        }
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] == Constants.ASSAULT_PARTY_SIZE) {
                res.add(i);
            }
        }
        return res;
    }

    /**
     * Returns if all members of an Assault Party have arrived at the Collection Site
     * @param assaultParties the array with the Assault Parties
     * @return true if all members of at least 1 Assault Party have arrived, false otherwise
     */
    public boolean partyHasArrived(AssaultPartyInterface[] assaultParties) {
        for (AssaultPartyInterface assaultParty: assaultParties) {
            int numArrivingThieves = 0;
            for (OrdinaryThief arrivingThief: arrivingThieves) {
                if (assaultParty.isMember(arrivingThief)) {
                    numArrivingThieves++;
                }
            }
            if (numArrivingThieves == Constants.ASSAULT_PARTY_SIZE) {
                return true;
            }
        }
        return false;
    }
}