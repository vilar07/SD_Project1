package src.sharedRegions;

import java.util.Random;
import src.Constants;
import src.entities.OrdinaryThief;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;
import src.room.Room;


public class Museum implements MuseumInterface{
    /**
     * Rooms inside the museum
     */
    private final Room[] rooms;

    /**
     * General Repository shared region
     */
    private final GeneralRepositoryInterface generalRepository;

    /**
     * Museum constructor, initializes rooms
     * @param generalRepository the General Repository
     */
    public Museum(GeneralRepositoryInterface generalRepository) {
        this.rooms = new Room[Constants.NUM_ROOMS];
        this.generalRepository = generalRepository;
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < this.rooms.length; i++){
            int distance = Constants.MIN_ROOM_DISTANCE + random.nextInt(Constants.MAX_ROOM_DISTANCE - Constants.MIN_ROOM_DISTANCE + 1);
            int paintings = Constants.MIN_PAINTINGS + random.nextInt(Constants.MAX_PAINTINGS - Constants.MIN_PAINTINGS + 1);
            this.rooms[i] = new Room(i, distance, paintings);
            this.generalRepository.setRoomState(i, paintings, distance);
        }
    }

    /**
     * Get room array
     * @return Array of Room objects
     */
    public Room[] getRooms()
    {
        return this.rooms;
    }

    /**
     * Count the number of paintings inside the museum.
     * @return Number of paintings inside the museum.
     */
    private synchronized int countPaintings(){
        int numPaintings = 0;
        
        for(int i = 0; i < this.rooms.length; i++){
            numPaintings += this.rooms[i].getPaintings();
        }
        
        return numPaintings;
    }

    /**
     * The Ordinary Thief tries to roll a canvas
     * @param party the party identification
     * @return true if the thief rolls a canvas, false if the room was already empty
     */
    public synchronized boolean rollACanvas(int party) {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        boolean res = this.rooms[thief.getAssaultParties()[party].getRoom().getID()]
                .rollACanvas(generalRepository);
        if (res) {
            thief.setBusyHands(party, res);
        }
        return res;
    }

    /**
     * Called to awake the first member in the line of Assault Party, by the last party member that rolled a canvas, 
     * so that the assaultParty can crawl out
     * - Synchronization Point between members of the Assault Party
     * @param party the Assault Party
     */
    public synchronized void reverseDirection(int party) {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        if (thief.getDirectionIn()) {
            thief.setDirectionIn(false);
            notifyAll();
        }
        while (!thief.getAssaultParties()[party].goingOut()) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        thief.setState(OrdinaryThief.State.CRAWLING_OUTWARDS);
    }

    /**
     * Getter for a specific room of the Museum
     * @param id the room identification
     * @return the room
     */
    public Room getRoom(int id) {
        return rooms[id];
    }
}