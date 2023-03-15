package src.Interfaces;

/**
 * The Museum has rooms inside of it. That rooms have paintings that can be stolen by the OrdinaryThiefs of the AssaultParty
 */

public interface MuseumInterface{

    /**
     * Called by OrdinaryThief to get a canvas
     * @return true if there are still paintings in the room, otherwise returns false
     */

    public boolean rollACanvas();

    /**
     * Called to awake the first member in the line of AssaultParty, by the last party member that rolled a canvas
     * , so that the assaultParty can crawls Out
     *  Synchronization Point -> between members of the AssaultParty
     */

    public void reverseDirection();

}