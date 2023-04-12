package src.sharedRegions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;
import src.room.Room;
import src.utils.Constants;

/**
 * Collection Site where Master Thief plans and paintings are stored
 */
public class CollectionSite implements CollectionSiteInterface {
    /**
     * Number of paintings acquired
     */
    private int paintings;

    private boolean[] emptyRooms;

    /**
     * FIFO of the available Assault Parties
     */
    private final Deque<Integer> availableParties;

    /**
     * FIFOs of the arriving Ordinary Thieves (one for each Assault Party)
     */
    private final List<Deque<OrdinaryThief>> arrivingThieves;

    private final GeneralRepositoryInterface generalRepository;

    private final AssaultPartyInterface[] assaultParties;

    private final MuseumInterface museum;

    /**
     * Collection Site constructor
     */
    public CollectionSite(GeneralRepositoryInterface generalRepository, AssaultPartyInterface[] assaultParties, MuseumInterface museum) {
        this.generalRepository = generalRepository;
        this.assaultParties = assaultParties;
        this.museum = museum;
        paintings = 0;
        emptyRooms = new boolean[Constants.NUM_ROOMS];
        for (int i = 0; i < emptyRooms.length; i++) {
            emptyRooms[i] = false;
        }
        availableParties = new ArrayDeque<>();
        arrivingThieves = new LinkedList<>();
        for (int i = 0; i < Constants.ASSAULT_PARTIES_NUMBER; i++) {
            availableParties.add(i);
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
    public synchronized char appraiseSit() {
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
        for (AssaultPartyInterface assaultPartyInterface: assaultParties) {
            room = assaultPartyInterface.getRoom();
            if (room != null) {
                assaultPartyRooms.add(room.getID());
            }
        }
        if (empty && this.availableParties.size() >= Constants.ASSAULT_PARTIES_NUMBER) {
            return 'E';
        }
        if (availableParties.size() == 0 || 
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
        while (!partyHasArrived()) {
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
                        setEmptyRoom(assaultParties[i].getRoom().getID(), true);
                    }
                    arrivingThieves.get(i).remove(arrivingThief);
                }
                assaultParties[i].setInOperation(false);
                generalRepository.disbandAssaultParty(i);
                if (!availableParties.contains(i)) {
                    availableParties.add(i);
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
        return availableParties.poll();
    }

    /**
     * Adds an Assault Party to the end of the FIFO
     * Called by the last member of the party to crawl out
     * @param party the Assault Party identification
     */
    public void addAssaultParty(int party) {
        availableParties.add(party);
    }

    /**
     * Returns if all members of an Assault Party have arrived at the Collection Site
     * @param assaultParties the array with the Assault Parties
     * @return true if all members of at least 1 Assault Party have arrived, false otherwise
     */
    private boolean partyHasArrived() {
        for (Deque<OrdinaryThief> assaultParty: this.arrivingThieves) {
            if (assaultParty.size() >= Constants.ASSAULT_PARTY_SIZE) {
                return true;
            }
        }
        return false;
    }

    public Room getNextRoom() {
        for (int i = 0; i < emptyRooms.length; i++) {
            if (!emptyRooms[i]) {
                return museum.getRoom(i);
            }
        }
        return null;
    }

    /**
     * Getter for the perception of the empty rooms
     * @return an array with size equal to NUM_ROOMS with elements that are true if the rooms with the corresponding index are empty and false otherwise
     */
    public boolean[] getEmptyRooms() {
        return emptyRooms;
    }

    /**
     * Setter for the empty rooms
     * @param room the room identification
     * @param empty true if it is empty, false otherwise
     */
    public void setEmptyRoom(int room, boolean empty) {
        emptyRooms[room] = empty;
    }
}