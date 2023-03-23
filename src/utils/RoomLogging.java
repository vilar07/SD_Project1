package src.utils;

/**
 * Util class used in GeneralRepository to represent the rooms, their number of paintings
 * and the distance to them
 */
public class RoomLogging {
    /**
     * Number of paintings in the room
     */
    private int paintings;

    /**
     * Distance to the room from the outside gathering site (between 15 and 30)
     */
    private int distance;

    /**
     * RoomLogging Constructor
     * @param paintings the number of paintings
     * @param distance the distance
     */
    public RoomLogging(int paintings, int distance) {
        this.paintings = paintings;
        this.distance = distance;
    }

    /**
     * Setter for the number of paintings
     * @param paintings the number of paintings
     */
    public void setPaintings(int paintings) {
        this.paintings = paintings;
    }

    /**
     * Setter for the distance
     * @param distance the distance
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * Getter for the number of paintings
     * @return the number of paintings
     */
    public int getPaintings() {
        return paintings;
    }

    /**
     * Getter for the distance
     * @return the distance
     */
    public int getDistance() {
        return distance;
    }
}
