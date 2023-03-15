package tests;

import src.SharedRegions.Logger;

public class TestLogger {
    public static void main(String[] args) {
        Logger logger = new Logger();
        logger.printHead();
        logger.printState();
        logger.printTail(0);
    }
}
