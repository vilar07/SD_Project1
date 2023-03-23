package src.interfaces;

import src.room.Room;

/**
 * The Museum has rooms inside of it. That rooms have paintings that can be stolen by the OrdinaryThiefs of the AssaultParty
 */

public interface MuseumInterface {
    /**
     * Roll a canvas.
     * @param id the room identification
     * @return true if the thief rolls a canvas, false if the room was already empty (There were no more paintings in the room)
     */
    public boolean rollACanvas(int id);

    /**
     * Called to awake the first member in the line of Assault Party, by the last party member that rolled a canvas, 
     * so that the assaultParty can crawl out
     * - Synchronization Point between members of the Assault Party
     * @param party the Assault Party
     */
    public void reverseDirection(int party);

    /**
     * Getter for a specific room of the Museum
     * @param id the room identification
     * @return the room
     */
    public Room getRoom(int id);
}