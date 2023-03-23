package src.sharedRegions;

import java.util.Random;
import src.Constants;
import src.entities.OrdinaryThief;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;
import src.room.Room;


public class Museum implements MuseumInterface {
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
        generalRepository.printHead();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < this.rooms.length; i++){
            int distance = Constants.MIN_ROOM_DISTANCE + random.nextInt(Constants.MAX_ROOM_DISTANCE - Constants.MIN_ROOM_DISTANCE + 1);
            int paintings = Constants.MIN_PAINTINGS + random.nextInt(Constants.MAX_PAINTINGS - Constants.MIN_PAINTINGS + 1);
            this.rooms[i] = new Room(i, distance, paintings);
            generalRepository.setRoomState(i, paintings, distance);
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
        thief.getAssaultParties()[party].addThiefToLine(thief);
        return res;
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
