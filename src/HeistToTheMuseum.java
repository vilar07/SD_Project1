package src;

import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.ConcentrationSiteInterface;
import src.Interfaces.CollectionSiteInterface;
import src.Interfaces.MuseumInterface;
import src.Interfaces.GeneralRepositoryInterface;
import src.SharedRegions.GeneralRepository;
import src.SharedRegions.CollectionSite;
import src.SharedRegions.ConcentrationSite;
import src.SharedRegions.Museum;

import src.Constants;

/**
 * Concurrent version of the HeistToTheMuseum.
 * Runs on a single computer.
 */
public class HeistToTheMuseum
{
    public static void main(String[] args){
        GeneralRepository repository = new GeneralRepository();
        CollectionSite collectionSite = new CollectionSite();
        ConcentrationSite concentrationSite = new ConcentrationSite();
        Museum museum = new Museum(repository);
        AssaultParty[] assaultParties = new AssaultParty[Constants.ASSAULT_PARTIES_NUMBER];
        for(int i=0;i<assaultParties.length;i++){
            assaultParties[i] = new AssaultParty(i);
        }

        MasterThief masterThief = new MasterThief((CollectionSiteInterface) collectionSite, (ConcentrationSiteInterface) concentrationSite, (AssaultPartyInterface[]) assaultParties, (GeneralRepositoryInterface) repository);

        OrdinaryThief ordinaryThieves[] = new OrdinaryThief[Constants.NUM_THIEVES - 1];
        for(int i=0;i<ordinaryThieves.length;i++){
            ordinaryThieves[i] = new OrdinaryThief(i, (MuseumInterface) museum, (CollectionSiteInterface) collectionSite, (ConcentrationSiteInterface) concentrationSite, (AssaultPartyInterface[]) assaultParties, (GeneralRepositoryInterface) repository);
        }

        masterThief.start();
        for(OrdinaryThief ot: ordinaryThieves){
            ot.start();
        }
    }
}