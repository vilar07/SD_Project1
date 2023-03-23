package src.interfaces;

/**
 * Logger responsible for generating and filling the logging file of the simulation
 */
public interface LoggerInterface {
    /**
     * Prints the message to the internal stream
     * @param message message to be logged
     */
    public void print(String message);

    /**
     * Closes the logger and associated streams
     */
    public void close();
}
