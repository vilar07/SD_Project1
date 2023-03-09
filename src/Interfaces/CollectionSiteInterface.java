package src.Interfaces;

/**
 * Collection Site where Master Thief plans and paintings are stored
 */
public interface CollectionSiteInterface {
    
    /**
     * Called by Master Thief to initiate operations
     */
    void startOperations();

    /**
     * Called by Master Thief to appraise situation: either to take a rest, prepare assault party or
     * sum up results
     * @return next situation
     */
    char appraiseSit();

    /**
     * Called by Master Thief. Waits for Assault Party return.
     */
    void takeARest();

    /**
     * Called by Master Thief to collect a canvas from an Ordinary Thief
     * - Synchronization point between Master Thief and each individual Ordinary Thief
     * @param thief Number of the Ordinary Thief
     * @return true if a canvas was collected or false if Ordinary Thief returned empty-handed
     */
    boolean collectACanvas(int thief);

    /**
     * Called by Ordinary Thief to hand out a canvas to Master Thief
     * - Synchronization point between Ordinary Thief and Master Thief
     * @param canvas false if Ordinary Thief is empty-handed, true otherwise
     */
    void handACanvas(boolean canvas);
}