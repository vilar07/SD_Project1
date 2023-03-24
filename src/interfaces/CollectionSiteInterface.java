package src.interfaces;

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
     * Called by the Ordinary Thief to hand a canvas to the Master Thief if they have any
     * - Synchronization point between each busy-handed Ordinary Thief and the Master Thief
     */
    public void handACanvas();

    /**
     * Get the number of the next Assault Party and remove it from the queue
     * @return the Assault Party identification
     */
    public int getNextAssaultPartyID();

    /**
     * Adds an Assault Party to the end of the FIFO
     * Called by the last member of the party to crawl out
     * @param party the Assault Party identification
     */
    public void addAssaultParty(int party);

    /**
     * Returns if all members of an Assault Party have arrived at the Collection Site
     * @param assaultParties the array with the Assault Parties
     * @return true if all members of at least 1 Assault Party have arrived, false otherwise
     */
    public boolean partyHasArrived(AssaultPartyInterface[] assaultParties);
}