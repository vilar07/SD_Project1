package src.Interfaces;

/**
 * Concentration Site where ordinary thieves wait for orders
 */

public interface ConcentrationSiteInterface {

    /**
     * Called by the master thief, when enough ordinary thieves are available and there is still a
     * room with paintings
     * - Synchronization point between Master Thief and every Ordinary Thief constituting the Assault
     * Party
     * @param thieves array with the ordinary thieves that make up the assault party
     * @param id number of the assault party
     * @param room number of the room in the museum
     */
    void prepareAssaultParty(OrdinaryThief[] thieves, int id, int room);

    /**
     * Called by the master thief to signal the end of the heist
     * - Synchronization point between Master Thief and all Ordinary Thieves
     * @param paintings number of paintings acquired in the heist
     */
    void sumUpResults(int paintings);

    /**
     * Called by an ordinary thief to wait for orders
     * @param thief ordinary thief
     * @return true if needed, false otherwise
     */
    boolean amINeeded(OrdinaryThief thief);

    /**
     * Called by an ordinary thief. Waits for the other party members
     * @return number of the assault party the ordinary thief belongs to
     */
    int prepareExcursion();
}
