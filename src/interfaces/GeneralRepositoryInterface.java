package src.interfaces;

import src.room.Room;

/**
 * General Repository where logging occurs.
 */
public interface GeneralRepositoryInterface {
    /**
     * Prints the head of the logging file.
     */
    public void printHead();

    /**
     * Prints the state of the simulation to the logging file.
     */
    public void printState();

    /**
     * Prints the tail of the logging file.
     * @param total number of paintings acquired.
     */
    public void printTail(int total);

    /**
     * Sets the Master Thief state.
     * @param state the state code to change to.
     */
    public void setMasterThiefState(String state);

    /**
     * Sets the Ordinary Thief state.
     * @param id the identification of the thief.
     * @param state the state code to change to.
     * @param situation the situation of the thief.
     * @param maxDisplacement the maximum displacement of the thief.
     */
    public void setOrdinaryThiefState(int id, String state, char situation, int maxDisplacement);

    /**
     * Sets the Ordinary Thief state.
     * @param id the identification of the thief.
     * @param state the state code to change to.
     */
    public void setOrdinaryThiefState(int id, String state);

    /**
     * Sets the Assault Party room target.
     * @param party the party number.
     * @param room the room identification.
     */
    public void setAssaultPartyRoom(int party, int room);

    /**
     * Sets an Assault Party member.
     * @param party the party number.
     * @param thief the identification of the thief.
     * @param pos the present position of the thief.
     * @param cv 1 if the thief is carrying a canvas, 0 otherwise.
     */
    public void setAssaultPartyMember(int party, int thief, int pos, int cv);

    /**
     * Removes an Assault Party member.
     * @param party the identification of the Assault Party.
     * @param thief the identification of the Ordinary Thief.
     */
    public void removeAssaultPartyMember(int party, int thief);

    /**
     * Resets the Assault Party logging details.
     * @param party the party number.
     */
    public void disbandAssaultParty(int party);

    /**
     * Sets the room state.
     * @param id the room identification.
     * @param paintings the number of paintings.
     * @param distance the distance to the outside gathering site.
     */
    public void setRoomState(int id, int paintings, int distance);

    /**
     * Sets the room state.
     * @param id the room identification.
     * @param paintings the number of paintings.
     */
    public void setRoomState(int id, int paintings);

    /**
     * Sets the initial room states.
     * @param rooms an array with the rooms.
     */
    public void setInitialRoomStates(Room[] rooms);
}
