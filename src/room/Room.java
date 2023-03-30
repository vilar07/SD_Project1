package src.room;

import src.interfaces.GeneralRepositoryInterface;

/**
 * Rooms contains paintings that can be stolen by the Thieves attacking the museum.
 */
 public class Room {
    /**
     * Room identification.
     */
    private final int id;

    /**
     * Room distance inside the museum.
     */
    private int distance;

    /**
     * Number of paintings currently inside the room.
     * This value is decreased every time a thief takes a painting from the room.
     */
    private int paintings;

    /**
     * Room constructor, the room stores its own position and the mumber of paintings inside.
     * @param id the room number
     * @param distance the distance to the room
     * @param paintings the number of paintings inside the room
     */
    public Room(int id, int distance, int paintings)
    {
        this.id = id;
        this.distance = distance;
        this.paintings = paintings;
    }

    /**
     * Room constructor, the room stores its own position and the mumber of paintings inside.
     * @param room the room number
     */
    public Room(int room) {
        id = room;
    }

    /**
     * Getter for the room identification
     * @return room identification
     */
    public int getID()
    {
        return this.id;
    }
    
    /**
     * Getter for the distance to the room
     * @return Room position inside the museum
     */
    public int getDistance()
    {
        return this.distance;
    }
    
    /**
     * Getter for the number of paintings inside the room
     * @return number of paintings inside the room
     */
    public int getPaintings()
    {
        return this.paintings;
    }

    /**
     * Setter for the distance
     * @param distance the distance of the room to the outside gathering site
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * Setter for the paintings
     * @param paintings the number of paintings present in the room
     */
    public void setPaintings(int paintings) {
        this.paintings = paintings;
    }

    /**
     * Remove a painting from the room.
     * Propagates to the General Repository
     * @param generalRepository the General Repository
     * @return True if there is still a painting and it is removed.
     */
    public boolean rollACanvas(GeneralRepositoryInterface generalRepository) {
        if (this.paintings > 0) {
            this.paintings--;
            generalRepository.setRoomState(id, paintings);
            return true;
        }
        return false;
    }

 }

