package src.Interfaces;

/**
 * General Repository where all information is stored and logging occurs
 */
public interface GeneralRepositoryInterface {
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

    /**
     * don't know
     * @param state
     */
    public void setMasterThiefState(int state);
}
