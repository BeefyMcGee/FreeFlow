import java.util.*;

public class Conflict {
    
    private int pos;
    private ArrayList<Integer> violatingAgents;

    Conflict(int pos, ArrayList<Integer> violatingAgents) {

        this.pos = pos;

        this.violatingAgents = violatingAgents;

    }

    public int getPosition() {

        return pos;

    }

    public ArrayList<Integer> getViolatingAgents() {

        return violatingAgents;

    }

}
