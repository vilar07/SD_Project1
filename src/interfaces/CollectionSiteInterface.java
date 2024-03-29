package src.interfaces;

import src.room.Room;

/**
 * Collection Site where Master Thief plans and paintings are stored
 */
public interface CollectionSiteInterface {
    /**
     * Getter for the number of paintings acquired
     * @return the number of paintings
     */
    public int getPaintings();
    
    /**
     * Called by Master Thief to initiate operations
     */
    public void startOperations();

    /**
     * Called by Master Thief to appraise situation: either to take a rest, prepare assault party or
     * sum up results
     * @return next situation
     */
    public char appraiseSit();

    /**
     * Master Thief waits while there are still Assault Parties in operation
     */
    public void takeARest();

    /**
     * Called by Master Thief to collect all available canvas
     * - Synchronization point between Master Thief and each individual Ordinary Thief with a canvas
     */
    public void collectACanvas();

    /**
     * Called by the Ordinary Thief to hand a canvas to the Master Thief if they have any
     * - Synchronization point between each Ordinary Thief and the Master Thief.
     * @param party the identification of the Assault Party the thief belongs to.
     */
    public void handACanvas(int party);

    /**
     * Get the number of the next Assault Party and remove it from the queue
     * @return the Assault Party identification
     */
    public int getNextAssaultPartyID();

    /**
     * Returns the next empty room. Uses the perception of the Master Thief, not the Museum information.
     * @return the room.
     */
    public Room getNextRoom();
}