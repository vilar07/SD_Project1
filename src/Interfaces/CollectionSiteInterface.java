package src.Interfaces;

/**
 * Collection Site where Master Thief plans and paintings are stored
 */
public interface CollectionSiteInterface {
    
    /**
     * Called by Master Thief to initiate operations
     */
    public void startOperations();

    /**
     * Called by Master Thief to appraise situation: either to take a rest, prepare assault party or
     * sum up results
     * @param assaultPartyInterfaces an array with the Assault Parties
     * @return next situation
     */
    public char appraiseSit(AssaultPartyInterface[] assaultPartyInterfaces);

    /**
     * Master Thief waits while there are still Assault Parties in operation
     */
    public void takeARest();

    /**
     * Called by Master Thief to collect a canvas from an Ordinary Thief
     * - Synchronization point between Master Thief and each individual Ordinary Thief
     * @param thief Number of the Ordinary Thief
     * @return true if a canvas was collected or false if Ordinary Thief returned empty-handed
     */
    public boolean collectACanvas(int thief);

    /**
     * Called by Ordinary Thief to hand out a canvas to Master Thief
     * - Synchronization point between Ordinary Thief and Master Thief
     * @param canvas false if Ordinary Thief is empty-handed, true otherwise
     */
    public void handACanvas(boolean canvas);

    /**
     * Get the number of the next Assault Party and remove it from the queue
     * @return the Assault Party identification
     */
    public int getNextAssaultPartyID();
}