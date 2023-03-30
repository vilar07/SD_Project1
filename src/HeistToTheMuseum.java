/**
 * Root package
 */
package src;

import src.sharedRegions.AssaultParty;
import src.sharedRegions.CollectionSite;
import src.sharedRegions.ConcentrationSite;
import src.sharedRegions.GeneralRepository;
import src.sharedRegions.Museum;
import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;

/**
 * Concurrent version of the Heist To The Museum.
 * Runs on a single computer.
 */
public class HeistToTheMuseum
{   
    /**
     * Main method to start the Heist To The Museum
     * @param args not used
     */
    public static void main(String[] args) {
        GeneralRepository repository = new GeneralRepository();
        CollectionSite collectionSite = new CollectionSite();
        ConcentrationSite concentrationSite = new ConcentrationSite();
        Museum museum = new Museum(repository);
        AssaultParty[] assaultParties = new AssaultParty[Constants.ASSAULT_PARTIES_NUMBER];
        for(int i = 0; i < assaultParties.length; i++) {
            assaultParties[i] = new AssaultParty(i);
        }

        MasterThief masterThief = new MasterThief((CollectionSiteInterface) collectionSite, (ConcentrationSiteInterface) concentrationSite, (AssaultPartyInterface[]) assaultParties, (GeneralRepositoryInterface) repository);

        OrdinaryThief ordinaryThieves[] = new OrdinaryThief[Constants.NUM_THIEVES - 1];
        for(int i = 0; i < ordinaryThieves.length; i++) {
            ordinaryThieves[i] = new OrdinaryThief(i, (MuseumInterface) museum, (CollectionSiteInterface) collectionSite, (ConcentrationSiteInterface) concentrationSite, (AssaultPartyInterface[]) assaultParties, (GeneralRepositoryInterface) repository);
        }

        masterThief.start();
        for(OrdinaryThief ot: ordinaryThieves) {
            ot.start();
        }
    }
}
