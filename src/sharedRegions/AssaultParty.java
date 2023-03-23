package src.sharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import src.Constants;
import src.entities.MasterThief;
import src.entities.OrdinaryThief;
import src.interfaces.AssaultPartyInterface;
import src.interfaces.GeneralRepositoryInterface;
import src.room.Room;

public class AssaultParty implements AssaultPartyInterface {
    /**
     * Queue with the identifications of the thieves in the party
     */
    private final Deque<OrdinaryThief> thieves;

    /**
     * Identification number of the Assault Party
     */
    private int id;

    /**
     * Room target of the Assault Party, contains identification of the room and the distance to it
     */
    private Room room;

    /**
     * Boolean value which is true if the Assault Party is operating or false if it is not
     */
    private boolean inOperation;

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
        thieves = new ArrayDeque<>(Constants.ASSAULT_PARTY_SIZE);
        room = null;
        inOperation = false;
    }

    /**
     * Called by the Master Thief to send the Assault Party to the museum
     * After that call, Assault Party can start crawling
     */
    @Override
    public synchronized void sendAssaultParty() {
        ((MasterThief) Thread.currentThread()).setState(MasterThief.State.DECIDING_WHAT_TO_DO);
        inOperation = true;
        notifyAll();
    }

    /**
     * Called by the Ordinary Thief to crawl in
     * @param party the Assault Party
     * @return false if they have finished the crawling
     */
    @Override
    public synchronized boolean crawlIn(int party) {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setState(OrdinaryThief.State.CRAWLING_INWARDS);
        do {
            Situation situation = inWhereAmI();
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
                thief.setPosition(party, thief.getPosition() + movement);
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
        } while (thief.getPosition() < room.getDistance());
        return false;
    }

    /**
     * Called by the Ordinary Thief to crawl out
     * @param party the Assault Party
     * @return false if they have finished the crawling
     */
    @Override
    public synchronized boolean crawlOut(int party) {
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
                thief.setPosition(party, thief.getPosition() - movement);
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
        return false;
    }
    
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
        if (higher == 2) {
            return Situation.FRONT;
        }
        if (lower == 2) {
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
        if (higher == 2) {
            return Situation.BACK;
        }
        if (lower == 2) {
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
        OrdinaryThief previousThief = it.next();
        return Constants.MAX_THIEF_SEPARATION - Math.abs(
                ((OrdinaryThief) Thread.currentThread()).getPosition() - previousThief.getPosition()
        );
    }

    /**
     * Returns whether or not the thief in the middle can crawl in further
     * @return the maximum movement the Ordinary Thief can make
     */
    private int canICrawlMid() {
        OrdinaryThief backThief = thieves.getLast();
        OrdinaryThief frontThief = thieves.getFirst();
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        int backSeparation = Math.abs(thief.getPosition() - backThief.getPosition());
        if (Constants.MAX_THIEF_SEPARATION - backSeparation == 0) {
            return 0;
        }
        int frontSeparation = Math.abs(frontThief.getPosition() - thief.getPosition());
        int nextPosition = thief.getPosition() + Math.min(Constants.MAX_THIEF_SEPARATION - backSeparation, thief.getMaxDisplacement());
        if (nextPosition <= room.getDistance() && nextPosition != thief.getPosition() + frontSeparation) {
            return nextPosition;
        }
        return 0;
    }

    /**
     * Returns whether or not the thief in the back can crawl in further
     * @return the maximum movement the Ordinary Thief can make
     */
    private int canICrawlBack() {
        Iterator<OrdinaryThief> it = thieves.iterator();
        OrdinaryThief frontThief = it.next();
        OrdinaryThief midThief = it.next();
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        int nextPosition = thief.getPosition() + thief.getMaxDisplacement();
        if (nextPosition != midThief.getPosition() && nextPosition != frontThief.getPosition()) {
            return thief.getMaxDisplacement();
        }
        return 0;
    }

    /**
     * Getter for the room destination
     * @return the room
     */
    public Room getRoom() {
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
     * Setter for the room destination
     * @param room the room identification
     */
    public void setRoomID(int room, GeneralRepositoryInterface generalRepository) {
        this.room = new Room(room);
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
     * Checks if given thief is in the Assault Party
     * @param thief the Ordinary Thief
     * @return true if they are part of the Assault Party, false otherwise
     */
    public boolean isMember(OrdinaryThief thief) {
        return thieves.contains(thief);
    }

    /**
     * Returns true if the Assault Party is ready to go out
     * @return true if all members are ready to go out, false otherwise
     */
    public boolean goingOut() {
        for (OrdinaryThief thief: thieves) {
            if (thief.getDirectionIn()) {
                return false;
            }
        }
        return true;
    }
}