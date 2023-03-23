package src.interfaces;

import src.entities.OrdinaryThief;
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
     * Called by the Ordinary Thief to crawl in
     * @param party the Assault Party
     * @return false if they have finished the crawling
     */
    public boolean crawlIn(int party);

    /**
     * Called by the Ordinary Thief to crawl out
     * @param party the Assault Party
     * @return false if they have finished the crawling
     */
    public boolean crawlOut(int party);

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
    public void setRoomID(int room, GeneralRepositoryInterface generalRepository);

    /**
     * Sets the members of the Assault Party
     * @param thieves array with the Ordinary Thieves
     * @param generalRepository the General Repository
     */
    public void setMembers(OrdinaryThief[] thieves, GeneralRepositoryInterface generalRepository);

    /**
     * Checks if given thief is in the Assault Party
     * @param thief the Ordinary Thief
     * @return true if they are part of the Assault Party, false otherwise
     */
    public boolean isMember(OrdinaryThief thief);

    /**
     * Returns true if the Assault Party is ready to go out
     * @return true if all members are ready to go out, false otherwise
     */
    public boolean goingOut();
}