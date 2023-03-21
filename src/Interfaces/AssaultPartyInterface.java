package src.Interfaces;

/**
 * Assault Party is constituted by Ordinary Thieves that are going to attack the museum.
 * Assault Party is shared by the thieves
 */

public interface AssaultPartyInterface {

    /**
     * Called by the Master Thief to send the assaultParty to the museum
     * After that call, Assault Party can start crawling
     */
    public void sendAssaultParty();

    /**
     * Called by the Ordinary Thief to crawl into the museum
     * @return true if Ordinary Thief is still crawling in, false otherwise
     */
    public boolean crawlIn();

    /**
     * Called by the Ordinary Thief to crawl out of the museum
     * @return true if Ordinary Thief is still crawling out, false otherwise
     */
    public boolean crawlOut();
}
