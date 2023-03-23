package src.SharedRegions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import src.Constants;
import src.Entities.MasterThief;
import src.Entities.OrdinaryThief;
import src.Interfaces.AssaultPartyInterface;
import src.Interfaces.GeneralRepositoryInterface;
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

    @Override
    public synchronized boolean crawlIn() {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        do {
            Situation situation = whereAmI(thief.getID());
            boolean canICrawl = true;
            switch (situation) {
                case FRONT:
                if (canICrawlFront(thief)) {
                    crawlFront(thief);
                } else {
                    canICrawl = false;
                }
                break;
                case MID:
                if (canICrawlMid(thief)) {
                    crawlMid(thief);
                } else {
                    canICrawl = false;
                }
                break;
                case BACK:
                if (canICrawlBack(thief)) {
                    crawlBack(thief);
                } else {
                    canICrawl = false;
                }
                break;
                if (!canICrawl) {
                    try {
                        wait();
                    } catch (InterruptedException e) {

                    }
                }
            }
        } while (thief.getPosition() < room.getDistance());
    }

    @Override
    public synchronized boolean crawlOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'crawlOut'");
    }
    
    /**
     * Private method to assess the situation of the Ordinary Thief in the line
     * @param thief identification number of the Ordinary Thief
     * @return situation in the line (front, back or mid)
     */
    private Situation whereAmI(int thief) {
        if (thief == thieves.getFirst().getID()) {
            return Situation.FRONT;
        }
        if (thief == thieves.getLast().getID()) {
            return Situation.BACK;
        }
        return Situation.MID;
    }

    /**
     * Returns whether or not the thief in front can crawl in further
     * @param thief the Ordinary Thief in front
     * @return false if the max separation between them and the previous thief in the line is equal to the maximum displacement between thieves, otherwise true
     */
    private boolean canICrawlFront(OrdinaryThief thief) {
        Iterator<OrdinaryThief> it = thieves.iterator();
        int i = 0;
        while (i++ < 1) {
            it.next();
        }
        OrdinaryThief previousThief = it.next();
        if (Constants.MAX_THIEF_SEPARATION - Math.abs(thief.getPosition() - previousThief.getPosition()) == 0) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether or not the thief in the middle can crawl in further
     * @param thief the Ordinary Thief in the middle
     * @return false if the separation between the 2 last thieves is equal to the maximum distance between thieves and true if the next position doesn't coincide with the
     * position of the Ordinary Thief in the front. In all other cases returns false
     */
    private boolean canICrawlMid(OrdinaryThief thief) {
        OrdinaryThief backThief = thieves.getLast();
        OrdinaryThief frontThief = thieves.getFirst();
        int backSeparation = Math.abs(thief.getPosition() - backThief.getPosition());
        if (Constants.MAX_THIEF_SEPARATION - backSeparation == 0) {
            return false;
        }
        int frontSeparation = Math.abs(frontThief.getPosition() - thief.getPosition());
        int nextPosition = thief.getPosition() + Math.min(Constants.MAX_THIEF_SEPARATION - backSeparation, thief.getMaxDisplacement())
        if (nextPosition <= room.getDistance() && nextPosition != thief.getPosition() + frontSeparation) {
            return true;
        }
        return false;
    }

    /**
     * Returns whether or not the thief in the back can crawl in further
     * @param thief the Ordinary Thief in the back
     * @return true if the thief's next position doesn't coincide with any of the other thieves, false otherwise
     */
    private boolean canICrawlBack(OrdinaryThief thief) {
        Iterator<OrdinaryThief> it = thieves.iterator();
        OrdinaryThief frontThief = it.next();
        OrdinaryThief midThief = it.next();
        int nextPosition = thief.getPosition() + thief.getMaxDisplacement();
        if (nextPosition != midThief.getPosition() && nextPosition != frontThief.getPosition()) {
            return true;
        }
        return false;
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
}
