package tests;

import src.SharedRegions.GeneralRepository;

public class TestLogger {
    public static void main(String[] args) {
        GeneralRepository generalRepository = new GeneralRepository();
        generalRepository.printHead();
        generalRepository.printState();
        generalRepository.printTail(0);
    }
}
