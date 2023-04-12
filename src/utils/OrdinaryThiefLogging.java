package src.utils;

/**
 * Util class used in GeneralRepository to represent each Ordinary Thief, its identification,
 * its situation and its maximum displacement
 */
public class OrdinaryThiefLogging {
    /**
     * State of the Ordinary Thief ("CONC", "COLL", "CRIN", "ROOM" or "COUT")
     */
    private String state;

    /**
     * Situation of the Ordinary Thief ('W' - waiting or 'P' - in party)
     */
    private char situation;

    /**
     * Maximum displacement of the Ordinary Thief (between '2' and '6')
     */
    private char maxDisplacement;

    /**
     * OrdinaryThief constructor
     * @param state the state
     * @param situation the situation
     * @param maxDisplacement the maximum displacement
     */
    public OrdinaryThiefLogging(String state, char situation, char maxDisplacement) {
        this.state = state;
        this.situation = situation;
        this.maxDisplacement = maxDisplacement;
    }

    /**
     * Setter for the state
     * @param state the state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Setter for the situation
     * @param situation the situation
     */
    public void setSituation(char situation) {
        this.situation = situation;
    }

    /**
     * Setter for the maximum displacement
     * @param maxDisplacement the maximum displacement
     */
    public void setMaxDisplacement(char maxDisplacement) {
        this.maxDisplacement = maxDisplacement;
    }

    /**
     * Getter for the state
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Getter for the situation
     * @return the situation
     */
    public char getSituation() {
        return situation;
    }

    /**
     * Getter for the maximum displacement
     * @return the maximum displacement
     */
    public char getMaxDisplacement() {
        return maxDisplacement;
    }
}
