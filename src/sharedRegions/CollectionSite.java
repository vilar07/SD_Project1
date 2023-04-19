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
 * Collection Site where intelligence and paintings are stored.
 */
public class CollectionSite implements CollectionSiteInterface {
    /**
     * Number of paintings acquired.
     */
    private int paintings;

    /**
     * Perception of the Master Thief about what rooms are empty.
     */
    private boolean[] emptyRooms;

    /**
     * FIFO of the available Assault Parties.
     */
    private final Deque<Integer> availableParties;

    /**
     * FIFOs of the arriving Ordinary Thieves (one for each Assault Party).
     */
    private final List<Deque<OrdinaryThief>> arrivingThieves;

    /**
     * The General Repository where logging occurs.
     */
    private final GeneralRepositoryInterface generalRepository;

    /**
     * The array holding the Assault Parties shared regions.
     */
    private final AssaultPartyInterface[] assaultParties;

    /** 
     * The Museum shared region.
     */
    private final MuseumInterface museum;

    /**
     * Collection Site constructor.
     * @param generalRepository the General Repository.
     * @param assaultParties the Assault Parties.
     * @param museum the Museum.
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
        ((MasterThief) Thread.currentThread()).setState(MasterThief.DECIDING_WHAT_TO_DO);
    }

    /**
     * Called by Master Thief to appraise situation: either to take a rest, prepare assault party or
     * sum up results
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
        masterThief.setState(MasterThief.WAITING_FOR_ARRIVAL);
        while (this.arrivingThieves.get(0).isEmpty() && this.arrivingThieves.get(1).isEmpty()) {
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
            for (OrdinaryThief arrivingThief: arrivingThieves.get(i)) {
                if (assaultParties[i].hasBusyHands(arrivingThief.getID())) {
                    paintings++;
                    assaultParties[i].setBusyHands(arrivingThief.getID(), false);
                } else {
                    setEmptyRoom(assaultParties[i].getRoom().getID(), true);
                }
                arrivingThieves.get(i).remove(arrivingThief); 
                synchronized (assaultParties[i]) {
                    assaultParties[i].removeMember(arrivingThief);
                    if (assaultParties[i].isEmpty()) {
                        assaultParties[i].setInOperation(false);
                        generalRepository.disbandAssaultParty(i);
                        if (!availableParties.contains(i)) {
                            availableParties.add(i);
                        }
                    }
                }
            }
        }
        notifyAll();
        masterThief.setState(MasterThief.DECIDING_WHAT_TO_DO);
    }

    /**
     * Called by the Ordinary Thief to hand a canvas to the Master Thief if they have any
     * - Synchronization point between each Ordinary Thief and the Master Thief.
     * @param party the identification of the Assault Party the thief belongs to.
     */
    public synchronized void handACanvas(int party) {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.COLLECTION_SITE);
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
     * Returns the next empty room. Uses the perception of the Master Thief, not the Museum information.
     * @return the room.
     */
    public Room getNextRoom() {
        for (int i = 0; i < emptyRooms.length; i++) {
            if (!emptyRooms[i]) {
                return museum.getRoom(i);
            }
        }
        return null;
    }

    /**
     * Setter for the empty rooms
     * @param room the room identification
     * @param empty true if it is empty, false otherwise
     */
    private void setEmptyRoom(int room, boolean empty) {
        emptyRooms[room] = empty;
    }
}