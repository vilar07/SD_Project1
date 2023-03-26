package tests;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.CollectionSiteInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.interfaces.MuseumInterface;
import src.sharedRegions.AssaultParty;
import src.sharedRegions.CollectionSite;
import src.sharedRegions.ConcentrationSite;
import src.sharedRegions.GeneralRepository;
import src.sharedRegions.Museum;

public class TestSorting {
    private static final List<OrdinaryThief> thieves = new LinkedList<>();
    
    public static void main(String[] args) {
        GeneralRepository generalRepository = new GeneralRepository();
        Museum museum = new Museum(generalRepository);
        CollectionSite collectionSite = new CollectionSite();
        ConcentrationSite concentrationSite = new ConcentrationSite();
        AssaultPartyInterface[] assaultParties = new AssaultPartyInterface[2];
        for (int i = 0; i < assaultParties.length; i++) {
            assaultParties[i] = (AssaultPartyInterface) new AssaultParty(i);
        }
        thieves.add(new OrdinaryThief(0, (MuseumInterface) museum, (CollectionSiteInterface) collectionSite, (ConcentrationSiteInterface) concentrationSite, assaultParties, (GeneralRepositoryInterface) generalRepository));
        thieves.add(new OrdinaryThief(1, (MuseumInterface) museum, (CollectionSiteInterface) collectionSite, (ConcentrationSiteInterface) concentrationSite, assaultParties, (GeneralRepositoryInterface) generalRepository));
        thieves.add(new OrdinaryThief(2, (MuseumInterface) museum, (CollectionSiteInterface) collectionSite, (ConcentrationSiteInterface) concentrationSite, assaultParties, (GeneralRepositoryInterface) generalRepository));
        for (OrdinaryThief thief: thieves) {
            System.out.printf("Thief %d: MD = %d\n", thief.getID(), thief.getMaxDisplacement());
        }
        thieves.get(0).setPosition(0, 1);
        thieves.get(2).setPosition(0, 4);
        updateLineIn();
        for (OrdinaryThief thief: thieves) {
            System.out.printf("Thief %d: Pos = %d\n", thief.getID(), thief.getPosition());
        }
    }

    /**
     * Updates the order of the line when crawling out
     */
    private static void updateLineIn() {
        thieves.sort(new Comparator<OrdinaryThief>() {
            @Override
            public int compare(OrdinaryThief t1, OrdinaryThief t2) {
                if (t1.getPosition() > t2.getPosition()) {
                    return -1;
                }
                if (t1.getPosition() < t2.getPosition()) {
                    return 1;
                }
                return 0;
            }
        });
    }
}
