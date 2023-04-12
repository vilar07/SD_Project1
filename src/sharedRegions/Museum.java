package src.sharedRegions;

import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;
import src.room.Room;

/**
 * The Museum has rooms inside of it. Those rooms have paintings that can be stolen 
 * by the Ordinary Thieves of the Assault Party.
 */
public class Museum implements MuseumInterface {
    /**
     * Rooms inside the museum
     */
    private final Room[] rooms;

    /**
     * General Repository shared region
     */
    private final GeneralRepositoryInterface generalRepository;

    private final AssaultPartyInterface[] assaultParties;

    /**
     * Museum constructor, initializes rooms
     * @param generalRepository the General Repository
     */
    public Museum(GeneralRepositoryInterface generalRepository, AssaultPartyInterface[] assaultParties, Room[] rooms) {
        this.generalRepository = generalRepository;
        this.assaultParties = assaultParties;
        this.rooms = rooms;
        generalRepository.setInitialRoomStates(this.rooms);
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
     * The Ordinary Thief tries to roll a canvas
     * @param party the party identification
     * @return true if the thief rolls a canvas, false if the room was already empty
     */
    public boolean rollACanvas(int party) {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.AT_A_ROOM);
        boolean res = false;
        Room room = assaultParties[party].getRoom();
        synchronized (this) {
            res = room.rollACanvas();
            if (res) {
                thief.setBusyHands(party, res);
                generalRepository.setRoomState(room.getID(), room.getPaintings());
            }
        }
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
