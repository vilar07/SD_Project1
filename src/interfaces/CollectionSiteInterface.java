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
     * - Synchronization point between each busy-handed Ordinary Thief and the Master Thief
     */
    public void handACanvas(int party);

    /**
     * Get the number of the next Assault Party and remove it from the queue
     * @return the Assault Party identification
     */
    public int getNextAssaultPartyID();

    public Room getNextRoom();

    /**
     * Getter for the perception of the empty rooms
     * @return an array with size equal to NUM_ROOMS with elements that are true if the rooms with the corresponding index are empty and false otherwise
     */
    public boolean[] getEmptyRooms();

    /**
     * Setter for the empty rooms
     * @param room the room identification
     * @param empty true if it is empty, false otherwise
     */
    public void setEmptyRoom(int room, boolean empty);
}