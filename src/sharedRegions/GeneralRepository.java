package src.sharedRegions;

import src.Constants;
import src.interfaces.GeneralRepositoryInterface;
import src.room.Room;
import src.utils.AssaultPartyElemLogging;
import src.utils.AssaultPartyLogging;
import src.utils.Logger;
import src.utils.OrdinaryThiefLogging;
import src.utils.RoomLogging;

/**
 * General Repository where all information is stored and logging occurs
 */
public class GeneralRepository implements GeneralRepositoryInterface {
    /**
     * Logger that handles the writing of the internal state of the simulation to the logging file
     */
    private Logger logger;

    /**
     * State of the Master Thief
     */
    private int masterThiefState;

    /**
     * Information of the Ordinary Thieves
     */
    private OrdinaryThiefLogging[] ordinaryThieves;

    /**
     * Information of the Assault Parties
     */
    private AssaultPartyLogging[] assaultParties;

    /**
     * Information of the rooms in the Museum
     */
    private RoomLogging[] rooms;

    /**
     * Constructor for the General Repository
     */
    public GeneralRepository() {
        logger = new Logger();
        masterThiefState = 0;
        ordinaryThieves = new OrdinaryThiefLogging[Constants.NUM_THIEVES - 1];
        for (int i = 0; i < ordinaryThieves.length; i++) {
            ordinaryThieves[i] = new OrdinaryThiefLogging(0, 'W', 0);
        }
        assaultParties = new AssaultPartyLogging[Constants.ASSAULT_PARTIES_NUMBER];
        for (int i = 0; i < assaultParties.length; i++) {
            AssaultPartyElemLogging[] elems = new AssaultPartyElemLogging[Constants.ASSAULT_PARTY_SIZE];
            for (int j = 0; j < elems.length; j++) {
                elems[j] = new AssaultPartyElemLogging(0, 0, 0);
            }
            assaultParties[i] = new AssaultPartyLogging(0, elems);
        }
        rooms = new RoomLogging[Constants.NUM_ROOMS];
        for (int i = 0; i < rooms.length; i++) {
            rooms[i] = new RoomLogging(0, 0);
        }
    }
    
    /**
     * Prints the head of the logging file
     */
    public void printHead() {
        StringBuilder stringBuilder = new StringBuilder(
                         "                              Heist to the Museum - Description of the internal state\n\n"
                ).append("MstT    Thief 1      Thief 2      Thief 3      Thief 4      Thief 5      Thief 6\n")
                 .append("Stat   Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD\n")
                 .append("                    Assault party 1                       Assault party 2                       Museum\n")
                 .append("            Elem 1     Elem 2     Elem 3          Elem 1     Elem 2     Elem 3   Room 1  Room 2  Room 3  Room 4  Room 5\n")
                 .append("     RId  Id Pos Cv  Id Pos Cv  Id Pos Cv  RId  Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT\n");
        logger.print(stringBuilder.toString());
    }

    /**
     * Prints the state of the simulation to the logging file
     */
    public synchronized void printState() {
        StringBuilder stringBuilder = new StringBuilder(
                 String.format("%4d   %4d %c  %d    %4d %c  %d    %4d %c  %d    %4d %c  %d    %4d %c  %d    %4d %c  %d\n",
                        masterThiefState, 
                        ordinaryThieves[0].getState(), ordinaryThieves[0].getSituation(), ordinaryThieves[0].getMaxDisplacement(),
                        ordinaryThieves[1].getState(), ordinaryThieves[1].getSituation(), ordinaryThieves[1].getMaxDisplacement(),
                        ordinaryThieves[2].getState(), ordinaryThieves[2].getSituation(), ordinaryThieves[2].getMaxDisplacement(),
                        ordinaryThieves[3].getState(), ordinaryThieves[3].getSituation(), ordinaryThieves[3].getMaxDisplacement(),
                        ordinaryThieves[4].getState(), ordinaryThieves[4].getSituation(), ordinaryThieves[4].getMaxDisplacement(),
                        ordinaryThieves[5].getState(), ordinaryThieves[5].getSituation(), ordinaryThieves[5].getMaxDisplacement()
                )
        ).append(String.format("      %d    %d  %2d  %d   %d  %2d  %d   %d  %2d  %d   %d   %d   %2d  %d   %d  %2d  %d   %d  %2d  %d   %2d %2d   %2d %2d   %2d %2d   %2d %2d   %2d %2d\n",
                        assaultParties[0].getRoom(), 
                        assaultParties[0].getElems()[0].getID(), assaultParties[0].getElems()[0].getPos(), assaultParties[0].getElems()[0].getCv(),
                        assaultParties[0].getElems()[1].getID(), assaultParties[0].getElems()[1].getPos(), assaultParties[0].getElems()[1].getCv(),
                        assaultParties[0].getElems()[2].getID(), assaultParties[0].getElems()[2].getPos(), assaultParties[0].getElems()[2].getCv(),
                        assaultParties[1].getRoom(), 
                        assaultParties[1].getElems()[0].getID(), assaultParties[1].getElems()[0].getPos(), assaultParties[1].getElems()[0].getCv(),
                        assaultParties[1].getElems()[1].getID(), assaultParties[1].getElems()[1].getPos(), assaultParties[1].getElems()[1].getCv(),
                        assaultParties[1].getElems()[2].getID(), assaultParties[1].getElems()[2].getPos(), assaultParties[1].getElems()[2].getCv(),
                        rooms[0].getPaintings(), rooms[0].getDistance(),
                        rooms[1].getPaintings(), rooms[1].getDistance(),
                        rooms[2].getPaintings(), rooms[2].getDistance(),
                        rooms[3].getPaintings(), rooms[3].getDistance(),
                        rooms[4].getPaintings(), rooms[4].getDistance()
                )
        );
        logger.print(stringBuilder.toString());
    }

    /**
     * Prints the tail of the logging file
     * @param total number of paintings acquired
     */
    public void printTail(int total) {
        StringBuilder stringBuilder = new StringBuilder(
                String.format("My friends, tonight's effort produced %2d priceless paintings!\n\n", total)
                        ).append("Legend:\n")
                         .append("MstT Stat    - state of the master thief\n")
                         .append("Thief # Stat - state of the ordinary thief # (# - 1 .. 6)\n")
                         .append("Thief # S    - situation of the ordinary thief # (# - 1 .. 6) either 'W' (waiting to join a party) or 'P' (in party)\n")
                         .append("Thief # MD   - maximum displacement of the ordinary thief # (# - 1 .. 6) a random number between 2 and 6\n")
                         .append("Assault party # RId        - assault party # (# - 1,2) elem # (# - 1 .. 3) room identification (1 .. 5)\n")
                         .append("Assault party # Elem # Id  - assault party # (# - 1,2) elem # (# - 1 .. 3) member identification (1 .. 6)\n")
                         .append("Assault party # Elem # Pos - assault party # (# - 1,2) elem # (# - 1 .. 3) present position (0 .. DT RId)\n")
                         .append("Assault party # Elem # Cv  - assault party # (# - 1,2) elem # (# - 1 .. 3) carrying a canvas (0,1)\n")
                         .append("Museum Room # NP - room identification (1 .. 5) number of paintings presently hanging on the walls\n")
                         .append("Museum Room # DT - room identification (1 .. 5) distance from outside gathering site, a random number between 15 and 30\n");
        logger.print(stringBuilder.toString());
        logger.close();
    }

    /**
     * Sets the Master Thief state
     * @param state the state code to change to
     */
    public void setMasterThiefState(int state) {
        masterThiefState = state;
        printState();
    }

    /**
     * Sets the Ordinary Thief state
     * @param id the identification of the thief
     * @param state the state code to change to
     * @param situation the situation of the thief
     * @param maxDisplacement the maximum displacement of the thief
     */
    public void setOrdinaryThiefState(int id, int state, char situation, int maxDisplacement) {
        ordinaryThieves[id].setState(state);
        ordinaryThieves[id].setSituation(situation);
        ordinaryThieves[id].setMaxDisplacement(maxDisplacement);
        printState();
    }

    /**
     * Sets the Ordinary Thief state
     * @param id the identification of the thief
     * @param state the state code to change to
     */
    public void setOrdinaryThiefState(int id, int state) {
        setOrdinaryThiefState(id, state, ordinaryThieves[id].getSituation(), ordinaryThieves[id].getMaxDisplacement());
    }

    /**
     * Sets the Assault Party room target
     * @param party the party number
     * @param room the room identification
     */
    public void setAssaultPartyRoom(int party, int room) {
        assaultParties[party].setRoom(room + 1);
        printState();
    }

    /**
     * Sets an Assault Party member
     * @param party the party number
     * @param thief the identification of the thief
     * @param pos the present position of the thief
     * @param cv 1 if the thief is carrying a canvas, 0 otherwise
     */
    public void setAssaultPartyMember(int party, int thief, int pos, int cv) {
        AssaultPartyElemLogging[] elems = assaultParties[party].getElems();
        int idx = 0;
        for (int i = elems.length - 1; i >= 0; i--) {
            if (elems[i].getID() == thief + 1) {
                elems[i].setPos(pos);
                elems[i].setCv(cv);
                printState();
                return;
            }
            if (elems[i].getID() == 0) {
                idx = i;
            }
        }
        elems[idx].setID(thief + 1);
        elems[idx].setPos(pos);
        elems[idx].setCv(cv);
        printState();
    }

    /**
     * Resets the Assault Party logging details
     * @param party the party number
     */
    public void disbandAssaultParty(int party) {
        assaultParties[party].setRoom(0);
        AssaultPartyElemLogging[] elems = assaultParties[party].getElems();
        for (int i = 0; i < elems.length; i++) {
            elems[i].setID(0);
            elems[i].setPos(0);
            elems[i].setCv(0);
        }
    }

    /**
     * Sets the room state
     * @param id the room identification
     * @param paintings the number of paintings
     * @param distance the distance to the outside gathering site
     */
    public void setRoomState(int id, int paintings, int distance) {
        rooms[id].setPaintings(paintings);
        rooms[id].setDistance(distance);
        printState();
    }

    /**
     * Sets the room state
     * @param id the room identification
     * @param paintings the number of paintings
     */
    public void setRoomState(int id, int paintings) {
        setRoomState(id, paintings, rooms[id].getDistance());
    }

    /**
     * Sets the initial room states
     * @param rooms an array with the rooms
     */
    public void setInitialRoomStates(Room[] rooms) {
        for (int i = 0; i < this.rooms.length; i++) {
            this.rooms[i].setDistance(rooms[i].getDistance());
            this.rooms[i].setPaintings(rooms[i].getPaintings());
        }
        printState();
    }
}
