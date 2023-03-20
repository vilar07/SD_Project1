package src;

import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.ConcentrationSiteInterface;
import src.Interfaces.CollectionSiteInterface;
import src.Interfaces.MuseumInterface;
import src.Interfaces.LoggerInterface;
import src.Constants;
/**
 * Concurrent version of the HeistToTheMuseum.
 * Runs on a single computer.
 */
public class HeistToTheMuseum
{
    public static void main(String[] args){

        GeneralRepository repository = new GeneralRepository();
        logger = repository.getLogger();
        
        AssaultParty[] assaultParties = new AssaultParty[Constants.ASSAULT_PARTIES_NUMBER];
        CollectionSite CollectionSite = new ControlCollectionSite();
        ConcentrationSite concentrationSite = new ConcentrationSite();
        Museum museum = new Museum((LoggerInterface) logger);
        OrdinaryThief ordinaryThieves[] = new OrdinaryThief[Constants.ASSAULT_PARTIES_NUMBER * Constants.ASSAULT_PARTY_SIZE];

        for(int i=0;i<assaultParties.length;i++){
            assaultParties[i] = new AssaultParty(i, (LoggerInterface) logger); //passámos o logger?
        }

        //passámos o museum também e o logger?
        MasterThief masterThief = new MasterThief((CollectionSiteInterface) controlCollectionSite, (ConcentrationSiteInterface) concentrationSite, (AssaultPartyInterface[]) assaultParties, (MuseumInterface) museum);

        //passámos o logger?
        for(int i=0;i<ordinaryThieves.length;i++){
            ordinaryThieves[i] = new OrdinaryThief(i, (MuseumInterface) museum, (CollectionSiteInterface) CollectionSite, (ConcentrationSiteInterface) concentrationSite, (AssaultPartyInterface[]) assaultParties);
        }

        masterThief.start();
        for(OrdinaryThief ot: ordinaryThieves){
            ot.start();
        }
    }
}