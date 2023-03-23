package src.Interfaces;

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
     * @param assaultPartyInterfaces an array with the Assault Parties
     * @return next situation
     */
    public char appraiseSit(AssaultPartyInterface[] assaultPartyInterfaces);

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