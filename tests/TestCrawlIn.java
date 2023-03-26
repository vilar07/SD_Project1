package tests;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import src.Constants;
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

public class TestCrawlIn {
    private static int roomDistance = 25;
    private static final List<OrdinaryThief> thieves = new LinkedList<>();

    private enum Situation {
        FRONT,
        MID,
        BACK
    }

    public static boolean crawlIn() {
        OrdinaryThief thief;
        while (!thieves.isEmpty()) {
            thief = thieves.get(0);
            do {
                System.out.println(thief.getID() + ": Pos = " + thief.getPosition());
                Situation situation = inWhereAmI(thief);
                int movement = 0;
                switch (situation) {
                    case FRONT:
                    movement = canICrawlFront(thief);
                    thieves.remove(thief);
                    thieves.add(0, thief);
                    break;
                    case MID:
                    movement = canICrawlMid(thief);
                    thieves.remove(thief);
                    thieves.add(1, thief);
                    break;
                    case BACK:
                    movement = canICrawlBack(thief);
                    thieves.remove(thief);
                    thieves.add(2, thief);
                    break;
                }
                if (movement > 0) {
                    thief.setPosition(0, Math.min(thief.getPosition() + movement, roomDistance));
                } else {
                    thieves.remove(thief);
                    thieves.add(thief);
                    break;
                }
            } while (thief.getPosition() < roomDistance);
            if (thief.getPosition() >= roomDistance) {
                thieves.remove(thief);
            }
        }
        return false;
    }

    private static Situation inWhereAmI(OrdinaryThief thief) {
        int higher = 0;
        int lower = 0;
        for (OrdinaryThief ordinaryThief: thieves) {
            if (ordinaryThief.getID() == thief.getID()) {
                continue;
            }
            if (ordinaryThief.getPosition() > thief.getPosition()) {
                lower++;
            } else {
                higher++;
            }
        }
        if (higher == thieves.size() - 1) {
            return Situation.FRONT;
        }
        if (lower == thieves.size() - 1) {
            return Situation.BACK;
        }
        return Situation.MID;
    }

    private static int canICrawlFront(OrdinaryThief thief) {
        Iterator<OrdinaryThief> it = thieves.iterator();
        int i = 0;
        while (i++ < 1) {
            it.next();
        }
        if (it.hasNext()) {
            OrdinaryThief previousThief = it.next();
            return Constants.MAX_THIEF_SEPARATION - Math.abs(
                    thief.getPosition() - previousThief.getPosition()
            );
        }
        return Math.min(roomDistance - thief.getPosition(), thief.getMaxDisplacement());
    }

    private static int canICrawlMid(OrdinaryThief thief) {
        OrdinaryThief backThief = thieves.get(2);
        OrdinaryThief frontThief = thieves.get(0);
        int backSeparation = Math.abs(thief.getPosition() - backThief.getPosition());
        if (Constants.MAX_THIEF_SEPARATION - backSeparation == 0) {
            return 0;
        }
        int frontSeparation = Math.abs(frontThief.getPosition() - thief.getPosition());
        int nextPosition = thief.getPosition() + Math.min(Constants.MAX_THIEF_SEPARATION - backSeparation, thief.getMaxDisplacement());
        if (nextPosition <= roomDistance && nextPosition != thief.getPosition() + frontSeparation) {
            return nextPosition;
        }
        return 0;
    }

    private static int canICrawlBack(OrdinaryThief thief) {
        int nextPosition = thief.getPosition() + thief.getMaxDisplacement();
        Iterator<OrdinaryThief> it = thieves.iterator();
        OrdinaryThief frontThief = it.next();
        if (it.hasNext()) {
            OrdinaryThief midThief = it.next();
            if (nextPosition != midThief.getPosition() && nextPosition != frontThief.getPosition()) {
                return thief.getMaxDisplacement();
            }
        } else {
            if (nextPosition != frontThief.getPosition()) {
                return thief.getMaxDisplacement();
            }
        }
        return 0;
    }

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
        crawlIn();
    }
}
