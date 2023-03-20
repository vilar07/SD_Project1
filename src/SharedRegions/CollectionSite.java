package src.SharedRegions;

import src.room.Room;
import src.Entities.MasterThief;
import src.Utils.Constants;

public class CollectionSite implements CollectionSiteInterface{

    /**
     * Number of paintings
     */
    private int paintings;

    /**
     * AssaultParties
     */
    private final AssaultParty[] Assaultparties;

    /**
     * Museum
     */
    private final Museum museum;

    /**
     * Queue for OrdinaryThieves waiting to deliver a canvas.
     */
    private final Queue<OrdinaryThief> canvasDeliverQueue;

    /**
     * Queue for OrdinaryThieves waiting to check if they are still needed.
     * OrdinaryThieves are needed until the Museum is completely empty.
     */
    private final Queue<OrdinaryThief> amINeededQueue;



    /**
     * Collection site constructor, creates a queue for OrdinaryThief.
     * @param AssaultParties AssaultParties
     * @param museum Museum
     */
    public SharedControlCollectionSite(AssaultParty[] AssaultParties, Museum museum, ){      
        this.AssaultParties = AssaultParties;
        this.museum = museum;
        
        this.canvasDeliverQueue = new ArrayQueue<>(Constants.NUM_THIEVES);
        this.amINeededQueue = new ArrayQueue<>(Constants.NUM_THIEVES);
        
        this.rooms = null;
        this.heistTerminated = false;
    }

    /**
     * This is the first state change in the MasterThief life cycle it changes the MasterThief state to deciding what to do. 
     * And is called by the master thief to verify where are the rooms inside the museum.
     */
    // ESTE É UM PONTO DE SINCRONIZAÇÃO? Flata implementar
    @Override
    public synchronized void startOperations() {


        Room[] museumRooms = this.museum.getRooms();
        
    }

    //implementar
    @Override
    public synchronized int appraiseSit() {
        
    }
    

}