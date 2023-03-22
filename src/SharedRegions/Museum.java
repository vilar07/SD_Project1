package src.SharedRegions;

import java.util.Random;
import src.Constants;
import src.Interfaces.MuseumInterface;
import src.Interfaces.GeneralRepositoryInterface;
import src.SharedRegions.GeneralRepository;
import src.room.Room;


public class Museum implements MuseumInterface{

    /**
     * Variable holding the General Repository shared region
     */
    private final GeneralRepositoryInterface generalRepository;

    /**
     * Rooms inside the museum.
     */
    private final Room[] rooms;

    /**
     * Museum constructor, initializes rooms .
     * @param Repository
     */
    public Museum(GeneralRepositoryInterface repository){  //Não sei se é preciso passar o repositório para o Museum, dúvida se pode existir rooms com a mesma distância, considerei que não
        this.generalRepository = repository;
        this.rooms = new Room[Constants.NUM_ROOMS];
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < this.rooms.length; i++){
            int distance = Constants.MIN_ROOM_DISTANCE + random.nextInt(Constants.MAX_ROOM_DISTANCE - Constants.MIN_ROOM_DISTANCE + 1);
            int paintings = Constants.MIN_PAINTINGS + random.nextInt(Constants.MAX_PAINTINGS - Constants.MIN_PAINTINGS + 1);
            this.rooms[i] = new Room(i, distance, paintings);
        }
        //Para Debug
        System.out.println("Info: Museum has " + this.countPaintings() + " paintings!");
    }

    /**
     * Get room array
     * @return Array of Room objects
     */
    public Room[] getRooms()
    {
        return this.rooms;
    }

    /**
     * Count the number of paintings inside the museum.
     * @return Number of paintings inside the museum.
     */
    private synchronized int countPaintings(){
        int numPaintings = 0;
        
        for(int i = 0; i < this.rooms.length; i++){
            numPaintings += this.rooms[i].getPaintings();
        }
        
        return numPaintings;
    }

    /**
     * Roll a canvas.
     * @param id Room id
     * @return True if the thief remove a canvas, False if the room was already empty (There were no more paintings in the room)
     */
    public synchronized boolean rollACanvas(int id){ //adicionei o id aqui e na interface, porque acho q é preciso para remover uma pintura de uma certa room. 
        return this.rooms[id].rollACanvas();
    }

    //falta implementar
    public synchronized void reverseDirection(){
        
    }
}