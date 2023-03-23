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
     * @param assaultParty the Assault Party
     * @param room number of the room in the museum
     */
    public void prepareAssaultParty(AssaultPartyInterface assaultParty, int room);

    /**
     * The Master Thief announces the end of operations
     * and shares the number of paintings acquired in the heist
     * @param paintings the number of paintings
     */
    public void sumUpResults(int paintings);

    /**
     * Called by an ordinary thief to wait for orders
     * @param thief ordinary thief
     * @return true if needed, false otherwise
     */
    public boolean amINeeded();

    /**
     * Ordinary Thief waits for the Master Thief to dispatch the designed Assault Party
     * @return the Assault Party identification 
     */
    public int prepareExcursion();
}
