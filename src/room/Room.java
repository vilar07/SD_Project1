package src.room;

import src.Constants;

/**
 * Rooms contains paintings that can be stolen by the Thieves attacking the museum.
 */

 public class Room{

    /**
     * Number of existing rooms, used to identify the next room that is created
     */
    private static int numRooms = 0;

    /**
     * Room identification.
     */
    private final int id;

    /**
     * Room distance inside the museum.
     */
    private final int distance;

    /**
     * Number of paintings currently inside the room.
     * This value is decreased every time a thief takes a painting from the room.
     */
    protected int paintings;

    /**
     * Room constructor, calculates and initialize the numberOfPaintings inside the room
     * and the room distance inside the museum
     */
    public Room() {
        id = numRooms++;
        paintings = (int) (Math.random() * (Constants.MAX_PAINTINGS + 1 - Constants.MIN_PAINTINGS)) + Constants.MIN_PAINTINGS;
        distance = (int) (Math.random() * (Constants.MAX_ROOM_DISTANCE + 1 - Constants.MIN_ROOM_DISTANCE)) + Constants.MIN_ROOM_DISTANCE;
    }

    /**
     * @return Room id.
     */
    public int getID()
    {
        return this.id;
    }
    
    /**
     * @return Room position inside the museum.
     */
    public int getDistance()
    {
        return this.distance;
    }
    
    /**
     * @return number Of paintings of the room.
     */
    public int getPaintings()
    {
        return this.paintings;
    }
 }

