package src.room;

import src.Constants;

/**
 * Rooms contains paintings that can be stolen by the Thieves attacking the museum.
 */

 public class Room{


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
     * Room constructor, the room stores its own position and the mumber of paintings inside.
     * @param id Room id.
     * @param distance Room distance.
     * @param paintings Number of paintings inside the room.
     */
    public Room(int id, int distance, int paintings)
    {
        this.id = id;
        this.distance = distance;
        this.paintings = paintings;
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

    /**
     * Remove a painting from the room.
     * @return True if there is still a painting and it is removed.
     */
    public boolean rollACanvas(){
        if(this.paintings > 0){
            this.paintings--;
            return true;
        }
        return false;
    }

 }

