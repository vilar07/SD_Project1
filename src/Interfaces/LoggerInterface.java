package src.Interfaces;

/**
 * Logger responsible for generating and filling the logging file of the simulation
 */
public interface LoggerInterface {
    /**
     * Prints the head of the logging file
     */
    public void printHead();

    /**
     * Prints the state of the simulation to the logging file
     */
    public void printState();

    /**
     * Prints the tail of the logging file
     * @param total number of paintings acquired
     */
    public void printTail(int total);
}
