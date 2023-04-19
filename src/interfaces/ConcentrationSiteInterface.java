package src.interfaces;

/**
 * Concentration Site where ordinary thieves wait for orders.
 */
public interface ConcentrationSiteInterface {
    /**
     * Called by the master thief, when enough ordinary thieves are available and there is still a
     * room with paintings.
     * - Synchronization point between Master Thief and every Ordinary Thief constituting the Assault Party.
     * @param assaultParty the Assault Party identification.
     */
    public void prepareAssaultParty(int assaultParty);

    /**
     * The Master Thief announces the end of operations
     * and shares the number of paintings acquired in the heist.
     * @param paintings the number of paintings
     */
    public void sumUpResults(int paintings);

    /**
     * Called by an ordinary thief to wait for orders.
     * @return true if needed, false otherwise.
     */
    public boolean amINeeded();

    /**
     * Ordinary Thief waits for the Master Thief to dispatch the designed Assault Party.
     * @return the Assault Party identification.
     */
    public int prepareExcursion();
}
