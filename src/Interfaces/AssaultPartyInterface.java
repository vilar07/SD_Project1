package src.Interfaces;

import src.Entities.OrdinaryThief;
import src.room.Room;

/**
 * Assault Party is constituted by Ordinary Thieves that are going to attack the museum.
 * Assault Party is shared by the thieves
 */

public interface AssaultPartyInterface {

    /**
     * Called by the Master Thief to send the Assault Party to the museum
     * After that call, Assault Party can start crawling
     */
    public void sendAssaultParty();

    /**
     * Called by the Ordinary Thief to crawl into the museum
     * @return true if Ordinary Thief is still crawling in, false otherwise
     */
    public boolean crawlIn();

    /**
     * Called by the Ordinary Thief to crawl out of the museum
     * @return true if Ordinary Thief is still crawling out, false otherwise
     */
    public boolean crawlOut();

    /**
     * Getter for the room destination
     * @return the room
     */
    public Room getRoom();

    /**
     * Getter for the assault party identification
     * @return the assault party number
     */
    public int getID();

    /**
     * Getter for the in operation attribute
     * @return true if Assault Party is operating, false otherwise
     */
    public boolean isInOperation();

    /**
     * Setter for the room destination
     * @param room the room identification
     */
    public void setRoomID(int room);

    /**
     * Sets the members of the Assault Party
     * @param thieves array with the Ordinary Thieves
     */
    public void setMembers(OrdinaryThief[] thieves);

    /**
     * Checks if given thief is in the Assault Party
     * @param thief the Ordinary Thief
     * @return true if they are part of the Assault Party, false otherwise
     */
    public boolean isMember(OrdinaryThief thief);
}
