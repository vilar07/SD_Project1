package src.sharedRegions;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import src.Constants;
import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.ConcentrationSiteInterface;
import src.interfaces.GeneralRepositoryInterface;

public class AssaultParty implements AssaultPartyInterface {
    /**
     * Queue with the identifications of the thieves in the party
     */
    private final List<OrdinaryThief> thieves;

    /**
     * Identification number of the Assault Party
     */
    private int id;

    /**
     * Room target of the Assault Party, contains identification of the room and the distance to it
     */
    private int room;

    /**
     * Boolean value which is true if the Assault Party is operating or false if it is not
     */
    private boolean inOperation;

    /**
     * Number of Ordinary Thieves ready to crawl out/reverse direction
     */
    private int thievesReadyToReverse;

    /**
     * Enumerate for the situation of the Ordinary Thief in the line, can be either front, mid or back
     */
    private enum Situation {
        FRONT,
        MID,
        BACK
    }

    /**
     * Public constructor for the Assault Party shared region
     * @param id the identification number of the Assault Party
     */
    public AssaultParty(int id) {
        this.id = id;
        thieves = new LinkedList<>();
        room = -1;
        inOperation = false;
        thievesReadyToReverse = 0;
    }

    /**
     * Called by the Master Thief to send the Assault Party to the museum
     * After that call, Assault Party can start crawling
     */
    @Override
    public synchronized void sendAssaultParty() {
        MasterThief masterThief = (MasterThief) Thread.currentThread();
        ConcentrationSiteInterface concentrationSite = masterThief.getConcentrationSite();
        synchronized (concentrationSite) {
            while (!readyThieves(concentrationSite)) {
                try {
                    concentrationSite.wait();
                } catch (InterruptedException e) {

                }
            }
        }
        inOperation = true;
        thievesReadyToReverse = 0;
        this.thieves.get(0).setNextToCrawl(true);
        notifyAll();
        masterThief.setState(MasterThief.State.DECIDING_WHAT_TO_DO);
    }

    public synchronized boolean crawlInInstant() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.CRAWLING_INWARDS);
        thieves.remove(thief);
        return false;
    }

    /**
     * Called by the Ordinary Thief to crawl in
     * @return false if they have finished the crawling
     */
    @Override
    public synchronized boolean crawlIn() {
        boolean instant = false;
        if (instant) {
            return crawlInInstant();
        }
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.CRAWLING_INWARDS);
        int roomDistance = thief.getMuseum().getRoom(room).getDistance();
        Situation situation;
        do {
            System.out.println(thief.getID() + ": pos=" + thief.getPosition());
            situation = whereAmI(thief);
            int movement = 0;
            switch (situation) {
                case FRONT:
                movement = crawlFront(thief);
                break;
                case MID:
                movement = crawlMid(thief);
                break;
                case BACK:
                movement = crawlBack(thief);
                break;
                default:
                break;
            }
            if (movement > 0) {
                thief.setPosition(this.id, Math.min(thief.getPosition() + movement, roomDistance));
                updateLineIn();
            } else {
                thief.setNextToCrawl(false);   
                updateLineIn();
                OrdinaryThief nextThief = getNextInLine(situation);
                nextThief.setNextToCrawl(true);
                notifyAll();
                while (!thief.isNextToCrawl()) {
                    try {
                        System.out.printf("Id: %d, here\n", thief.getID());
                        wait();
                    } catch (InterruptedException e) {

                    }
                }
            }
        } while (thief.getPosition() < roomDistance);
        thief.setNextToCrawl(false);
        OrdinaryThief nextThief = getNextInLine(Situation.FRONT);
        System.out.println(thief.getID() + " - Next thief: " + nextThief.getID());
        nextThief.setNextToCrawl(true);
        this.thieves.remove(thief);
        notifyAll();
        thief.setPosition(id, 0); // REMOVE THIS LINE WHEN CRAWLOUT IS WORKING
        return false;
    }

    /**
     * Called to awake the first member in the line of Assault Party, by the last party member that rolled a canvas,
     * so that the Assault Party can crawl out
     * - Synchronization Point between members of the Assault Party
     */
    public synchronized void reverseDirection() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        while (thievesReadyToReverse < Constants.ASSAULT_PARTY_SIZE) {
            try {
                System.out.println(thief.getID() + ": size=" + this.thieves.size());
                wait();
            } catch (InterruptedException e) {

            }
        }
        thieves.add(thief);
        thief.setState(OrdinaryThief.State.CRAWLING_OUTWARDS);
    }

    public synchronized boolean crawlOut() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.CRAWLING_OUTWARDS);
        return false;
    }
    
    /**
     * Called by the Ordinary Thief to crawl out
     * @param party the Assault Party
     * @return false if they have finished the crawling
     */
    /*
    @Override
    public synchronized boolean crawlOut() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.CRAWLING_OUTWARDS);
        do {
            Situation situation = outWhereAmI();
            int movement = 0;
            switch (situation) {
                case FRONT:
                movement = canICrawlFront();
                break;
                case MID:
                movement = canICrawlMid();
                break;
                case BACK:
                movement = canICrawlBack();
                break;
            }
            if (movement > 0) {
                thief.setPosition(this.id, thief.getPosition() - movement);
            } else {
                thieves.remove(thief);
                thieves.add(thief);
                notifyAll();
                while (!thieves.getFirst().equals(thief)) {
                    try {
                        wait();
                    } catch (InterruptedException e) {

                    }
                }
            }
        } while (thief.getPosition() > 0);
        thieves.remove(thief);
        if (thieves.isEmpty()) {
            inOperation = false;
            thief.getGeneralRepository().disbandAssaultParty(id);
            thief.getCollectionSite().addAssaultParty(id);
        }
        return false;
    }
    */
    
    /**
     * Private method to assess the situation of the Ordinary Thief in the line when going in
     * @return situation in the line (front, back or mid)
     */
    private Situation inWhereAmI() {
        int higher = 0;
        int lower = 0;
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
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

    /**
     * Private method to assess the situation of the Ordinary Thief in the line when going in
     * @return situation in the line (front, back or mid)
     */
    private Situation outWhereAmI() {
        int higher = 0;
        int lower = 0;
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
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
            return Situation.BACK;
        }
        if (lower == thieves.size() - 1) {
            return Situation.FRONT;
        }
        return Situation.MID;
    }

    /**
     * Returns whether or not the thief in front can crawl in further
     * @return  the maximum movement the Ordinary Thief can make
     */
    private int canICrawlFront() {
        Iterator<OrdinaryThief> it = thieves.iterator();
        int i = 0;
        while (i++ < 1) {
            it.next();
        }
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        if (it.hasNext()) {
            OrdinaryThief previousThief = it.next();
            return Constants.MAX_THIEF_SEPARATION - Math.abs(
                    thief.getPosition() - previousThief.getPosition()
            );
        }
        return Math.min(thief.getMuseum().getRoom(room).getDistance() - thief.getPosition(), thief.getMaxDisplacement());
    }

    /**
     * Returns whether or not the thief in the middle can crawl in further
     * @return the maximum movement the Ordinary Thief can make
     */
    private int canICrawlMid() {
        OrdinaryThief backThief = thieves.get(thieves.size() -1 );
        OrdinaryThief frontThief = thieves.get(0);
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        int backSeparation = Math.abs(thief.getPosition() - backThief.getPosition());
        if (Constants.MAX_THIEF_SEPARATION - backSeparation == 0) {
            return 0;
        }
        int frontSeparation = Math.abs(frontThief.getPosition() - thief.getPosition());
        int nextPosition = thief.getPosition() + Math.min(Constants.MAX_THIEF_SEPARATION - backSeparation, thief.getMaxDisplacement());
        if (nextPosition <= thief.getMuseum().getRoom(room).getDistance() && nextPosition != thief.getPosition() + frontSeparation) {
            return nextPosition;
        }
        return 0;
    }

    /**
     * Returns whether or not the thief in the back can crawl in further
     * @return the maximum movement the Ordinary Thief can make
     */
    private int canICrawlBack() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
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

    /**
     * Getter for the room destination
     * @return the room identification
     */
    public int getRoom() {
        return room;
    }

    /**
     * Getter for the assault party identification
     * @return the assault party number
     */
    public int getID() {
        return id;
    }

    /**
     * Getter for the in operation attribute
     * @return true if Assault Party is operating, false otherwise
     */
    public boolean isInOperation() {
        return inOperation;
    }

    /**
     * Setter for the inOperation attribute
     * @param inOperation true if Assault Party is operating, false if not
     */
    public void setInOperation(boolean inOperation) {
        this.inOperation = inOperation;
    }

    /**
     * Setter for the room destination
     * @param room the room identification
     * @param generalRepository the General Repository
     */
    public void setRoom(int room, GeneralRepositoryInterface generalRepository) {
        this.room = room;
        generalRepository.setAssaultPartyRoom(id, room);
    }

    /**
     * Sets the members of the Assault Party
     * @param thieves array with the Ordinary Thieves
     * @param generalRepository the General Repository
     */
    public void setMembers(OrdinaryThief[] thieves, GeneralRepositoryInterface generalRepository) {
        this.thieves.clear();
        for (OrdinaryThief thief: thieves) {
            this.thieves.add(thief);
            generalRepository.setAssaultPartyMember(id, thief.getID(), thief.getPosition(),
                    thief.hasBusyHands() ? 1 : 0);
        }
    }

    /**
     * Increments the number of thieves that are ready to crawl out
     */
    public void addThiefReadyToReverse() {
        thievesReadyToReverse++;
    }

    /**
     * Adds an Ordinary Thief to the end of the line
     * @param thief the Ordinary Thief
     */
    public void addThiefToLine(OrdinaryThief thief) {
        thieves.add(thief);
    }

    /**
     * Checks if given thief is in the Assault Party
     * @param thief the Ordinary Thief
     * @return true if they are part of the Assault Party, false otherwise
     */
    public boolean isMember(OrdinaryThief thief) {
        return thieves.contains(thief);
    }

    public boolean readyThieves(ConcentrationSiteInterface concentrationSite) {
        for (OrdinaryThief ordinaryThief: thieves) {
            if (concentrationSite.contains(ordinaryThief)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the order of the line when crawling in
     */
    private void updateLineOut() {
        this.thieves.sort(new Comparator<OrdinaryThief>() {
            @Override
            public int compare(OrdinaryThief t1, OrdinaryThief t2) {
                if (t1.getPosition() > t2.getPosition()) {
                    return 1;
                }
                if (t1.getPosition() < t2.getPosition()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    /**
     * Updates the order of the line when crawling out
     */
    private void updateLineIn() {
        this.thieves.sort(new Comparator<OrdinaryThief>() {
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

    /**
     * Returns the maximum possible movement for the Ordinary Thief in the front
     * @param thief the Ordinary Thief in the front
     * @return the maximum possible movement
     */
    private int crawlFront(OrdinaryThief thief) {
        OrdinaryThief nextThief = getNextInLine(Situation.FRONT);
        int thiefSeparation = Math.abs(thief.getPosition() - nextThief.getPosition());
        if (thiefSeparation >= Constants.MAX_THIEF_SEPARATION) {
            return 0;
        }
        return Math.min(thief.getMaxDisplacement(), Constants.MAX_THIEF_SEPARATION - thiefSeparation);
    }

    /**
     * Returns the maximum possible movement for the Ordinary Thief in the middle
     * @param thief the Ordinary Thief in the middle
     * @return the maximum possible movement
     */
    private int crawlMid(OrdinaryThief thief) {
        OrdinaryThief frontThief = this.thieves.get(0);
        OrdinaryThief backThief = this.thieves.get(this.thieves.size() - 1);
        int nextPosition, position = thief.getPosition(), frontPosition = frontThief.getPosition();
        for (int displacement = thief.getMaxDisplacement(); displacement > 0; displacement--) {
            nextPosition = position + displacement;
            if (nextPosition != frontPosition) {
                return Math.min(Math.max(Math.abs(position - frontPosition) - Constants.ASSAULT_PARTY_SIZE, displacement), 
                                Constants.ASSAULT_PARTY_SIZE - Math.abs(backThief.getPosition() - position));
            }
        }
        return 0;
    }

    /**
     * Returns the maximum possible movement for the Ordinary Thief in the back
     * @param thief the Ordinary Thief in the back
     * @return the maximum possible movement
     */
    private int crawlBack(OrdinaryThief thief) {
        OrdinaryThief frontThief;
        int nextPosition, frontThiefPosition, position = thief.getPosition();
        if (this.thieves.size() == Constants.ASSAULT_PARTY_SIZE) {
            frontThief = this.thieves.get(0);
            frontThiefPosition = frontThief.getPosition();
            OrdinaryThief midThief = this.thieves.get(1);
            for (int displacement = thief.getMaxDisplacement(); displacement > 0; displacement--) {
                nextPosition = position + displacement;
                if (nextPosition != frontThief.getPosition() && nextPosition != midThief.getPosition()) {
                    return Math.min(displacement, Constants.MAX_THIEF_SEPARATION + Math.abs(frontThiefPosition - position));
                }
            }
        } else {
            frontThief = this.thieves.get(0);
            frontThiefPosition = frontThief.getPosition();
            for (int displacement = thief.getMaxDisplacement(); displacement > 0; displacement--) {
                nextPosition = position + displacement;
                if (nextPosition != frontThiefPosition) {
                    return Math.min(displacement, Constants.MAX_THIEF_SEPARATION + Math.abs(frontThiefPosition - position));
                }
            }
        }
        return 0;
    }

    /**
     * Returns the next Ordinary Thief in line to crawl
     * @param situation the situation of the current Ordinary Thief
     * @return the next Ordinary Thief
     */
    private OrdinaryThief getNextInLine(Situation situation) {
        switch (situation) {
            case FRONT:
            if (this.thieves.size() == 1) {
                return this.thieves.get(0);
            }
            return this.thieves.get(1);
            case MID:
            return this.thieves.get(2);
            case BACK:
            return this.thieves.get(0);
            default:
            return null;
        }
    }

    private Situation whereAmI(OrdinaryThief currentThief) {
        if (currentThief.equals(this.thieves.get(0))) {
            return Situation.FRONT;
        }
        if (currentThief.equals(this.thieves.get(this.thieves.size() - 1))) {
            return Situation.BACK;
        }
        return Situation.MID;
    }
}
