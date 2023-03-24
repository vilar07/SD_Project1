package src.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import src.interfaces.LoggerInterface;

/**
 * Logger responsible for generating and filling the logging file of the simulation
 */
public class Logger implements LoggerInterface {
    /**
     * FileWriter class to open the logging file
     */
    private FileWriter fileWriter;

    /**
     * PrintWriter class to write/append to the logging file
     */
    private PrintWriter printWriter;

    /**
     * Logger constructor
     * @param fileName path to the logging file
     */
    public Logger(String fileName) {
        try {
            fileWriter = new FileWriter(fileName);
            printWriter = new PrintWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Default logger constructor
     */
    public Logger() {
        this("heist.log");
    }

    /**
     * Prints the message to the internal stream
     * @param message message to be logged
     */
    @Override
    public void print(String message) {
        printWriter.append(message);
        printWriter.flush();
    }

    /**
     * Closes the logger and associated streams
     */
    @Override
    public void close() {
        printWriter.close();
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
