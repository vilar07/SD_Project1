package src.utils;

/**
 * Util class used in GeneralRepository to represent each Assault Party, its room target
 * and the constituting elements
 */
public class AssaultPartyLogging {
    /**
     * The room identification (between '1' and '5')
     */
    private char room;

    /**
     * Array holding the 3 elements of the party
     */
    private AssaultPartyElemLogging[] elems;

    /**
     * AssaultPartyLogging constructor
     * @param room the room identification
     * @param elems the array with the elements
     */
    public AssaultPartyLogging(char room, AssaultPartyElemLogging[] elems) {
        this.room = room;
        this.elems = elems;
    }

    /**
     * Setter for the room identification
     * @param room the room identification
     */
    public void setRoom(char room) {
        this.room = room;
    }

    /**
     * Setter for the elements of the party
     * @param elems the elements of the party
     */
    public void setElems(AssaultPartyElemLogging[] elems) {
        this.elems = elems;
    }

    /**
     * Getter for the room identification
     * @return the room identification
     */
    public char getRoom() {
        return room;
    }

    /**
     * Getter for the elements of the party
     * @return the elements of the party
     */
    public AssaultPartyElemLogging[] getElems() {
        return elems;
    }
}
