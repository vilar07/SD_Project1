package src.utils;

/**
 * Util class used in GeneralRepository to represent each Assault Party element, the identification
 * of the thief, its position in the line to the room in the Museum and a number representing the
 * carrying of a canvas
 */
public class AssaultPartyElemLogging {
    /**
     * The member identification (from '1' to '6')
     */
    private char id;

    /**
     * The present position (from 0 to the distance to the room)
     */
    private String pos;

    /**
     * '1' if carrying a canvas, '0' if not
     */
    private char cv;

    /**
     * AssaultPartyElemLogging constructor
     * @param id the member identification
     * @param pos the present position
     * @param cv '1' if carrying a canvas, '0' if not
     */
    public AssaultPartyElemLogging(char id, String pos, char cv) {
        this.id = id;
        this.pos = pos;
        this.cv = cv;
    }

    /**
     * Setter for the member identification
     * @param id the member identification
     */
    public void setID(char id) {
        this.id = id;
    }

    /**
     * Setter for the present position
     * @param pos the present position
     */
    public void setPos(String pos) {
        this.pos = pos;
    }

    /**
     * Setter for the action of carrying a canvas
     * @param cv '1' if carrying a canvas, '0' if not
     */
    public void setCv(char cv) {
        this.cv = cv;
    }

    /**
     * Getter for the member identification
     * @return the member identification
     */
    public char getID() {
        return id;
    }

    /**
     * Getter for the present position
     * @return the present position
     */
    public String getPos() {
        return pos;
    }

    /**
     * Getter for the action of carrying a canvas
     * @return '1' if carrying a canvas, '0' if not
     */
    public char getCv() {
        return cv;
    }
}
