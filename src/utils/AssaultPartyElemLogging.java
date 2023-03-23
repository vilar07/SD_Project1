package src.utils;

/**
 * Util class used in GeneralRepository to represent each Assault Party element, the identification
 * of the thief, its position in the line to the room in the Museum and a number representing the
 * carrying of a canvas
 */
public class AssaultPartyElemLogging {
    /**
     * The member identification (from 1 to 6)
     */
    private int id;

    /**
     * The present position (from 0 to the distance to the room)
     */
    private int pos;

    /**
     * 1 if carrying a canvas, 0 if not
     */
    private int cv;

    /**
     * AssaultPartyElemLogging constructor
     * @param id the member identification
     * @param pos the present position
     * @param cv 1 if carrying a canvas, 0 if not
     */
    public AssaultPartyElemLogging(int id, int pos, int cv) {
        this.id = id;
        this.pos = pos;
        this.cv = cv;
    }

    /**
     * Setter for the member identification
     * @param id the member identification
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Setter for the present position
     * @param pos the present position
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     * Setter for the action of carrying a canvas
     * @param cv 1 if carrying a canvas, 0 if not
     */
    public void setCv(int cv) {
        this.cv = cv;
    }

    /**
     * Getter for the member identification
     * @return the member identification
     */
    public int getID() {
        return id;
    }

    /**
     * Getter for the present position
     * @return the present position
     */
    public int getPos() {
        return pos;
    }

    /**
     * Getter for the action of carrying a canvas
     * @return 1 if carrying a canvas, 0 if not
     */
    public int getCv() {
        return cv;
    }
}
