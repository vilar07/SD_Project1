package src.Interfaces;

/**
 * AssaultParty is constituted by OrdinaryThiefs that are going to attack the museum.
 * Assault party is shared by thieves
 */

public interface AssaultPartyInterface {

    /**
     * Called by the MasterThief to send the assaultParty to the museum
     * After that call, AssaultParty can start crawling
     */
    
    public void sendAssaultParty();

    /**
     * Called by the OrdinaryThief to crawlIn inside the museum
     * @param thief -> thief id that is crawling In
     * @return position of the thief after crawling In
     */

    public int crawlIn(int thief);

    /**
     * Called by the OrdinaryThief to crawlOut inside the museum
     * @param thief -> thief id that is crawling Out
     * @return position of the thief after crawling Out
     */

    public int crawlOut(int thief);

}
