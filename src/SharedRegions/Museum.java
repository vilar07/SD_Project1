package src.SharedRegions;

import src.Interfaces.MuseumInterface;
import src.Interfaces.GeneralRepositoryInterface;
import src.SharedRegions.GeneralRepository;

public Museum implements MuseumInterface{

    /**
     * Variable holding the General Repository shared region
     */
    private final GeneralRepositoryInterface generalRepository;

    public Museum(GeneralRepositoryInterface repository){
        this.repository = repository;
    }

    public synchronized boolean rollACanvas(){

    }

    public synchronized void reverseDirection(){
        
    }
}