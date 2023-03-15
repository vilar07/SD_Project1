package src.SharedRegions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import src.Interfaces.LoggerInterface;

/**
 * Logger responsible for generating and filling the logging file of the simulation
 */
public class Logger implements LoggerInterface {
    private FileWriter fileWriter;
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
     * Prints the head of the logging file
     */
    @Override
    public void printHead() {
        StringBuilder stringBuilder = new StringBuilder(
                         "                              Heist to the Museum - Description of the internal state\n\n"
                ).append("MstT    Thief 1      Thief 2      Thief 3      Thief 4      Thief 5      Thief 6\n")
                 .append("Stat   Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD\n")
                 .append("                    Assault party 1                       Assault party 2                       Museum\n")
                 .append("            Elem 1     Elem 2     Elem 3          Elem 1     Elem 2     Elem 3   Room 1  Room 2  Room 3  Room 4  Room 5\n")
                 .append("     RId  Id Pos Cv  Id Pos Cv  Id Pos Cv  RId  Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT\n");
        printWriter.append(stringBuilder.toString());
    }

    /**
     * Prints the state of the simulation to the logging file
     */
    @Override
    public void printState() {
        StringBuilder stringBuilder = new StringBuilder(
                         "####   #### #  #    #### #  #    #### #  #    #### #  #    #### #  #    #### #  #\n"
                ).append("      #    #  ##  #   #  ##  #   #  ##  #   #   #   ##  #   #  ##  #   #  ##  #   ## ##   ## ##   ## ##   ## ##   ## ##\n");
        printWriter.append(stringBuilder.toString());
    }

    /**
     * Prints the tail of the logging file
     */
    @Override
    public void printTail(int total) {
        StringBuilder stringBuilder = new StringBuilder(
                String.format("My friends, tonight's effort produced %2d priceless paintings!\n\n", total)
                        ).append("Legend:\n")
                         .append("MstT Stat    - state of the master thief\n")
                         .append("Thief # Stat - state of the ordinary thief # (# - 1 .. 6)\n")
                         .append("Thief # S    - situation of the ordinary thief # (# - 1 .. 6) either 'W' (waiting to join a party) or 'P' (in party)\n")
                         .append("Thief # MD   - maximum displacement of the ordinary thief # (# - 1 .. 6) a random number between 2 and 6\n")
                         .append("Assault party # RId        - assault party # (# - 1,2) elem # (# - 1 .. 3) room identification (1 .. 5)\n")
                         .append("Assault party # Elem # Id  - assault party # (# - 1,2) elem # (# - 1 .. 3) member identification (1 .. 6)\n")
                         .append("Assault party # Elem # Pos - assault party # (# - 1,2) elem # (# - 1 .. 3) present position (0 .. DT RId)\n")
                         .append("Assault party # Elem # Cv  - assault party # (# - 1,2) elem # (# - 1 .. 3) carrying a canvas (0,1)\n")
                         .append("Museum Room # NP - room identification (1 .. 5) number of paintings presently hanging on the walls\n")
                         .append("Museum Room # DT - room identification (1 .. 5) distance from outside gathering site, a random number between 15 and 30\n");
        printWriter.append(stringBuilder.toString());
        printWriter.close();
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
