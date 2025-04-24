public class Constraint {

    private int agentId;
    private int[] pos;

    Constraint(int agentId, int[] pos) {

        this.agentId = agentId;
        this.pos = pos;

    }
    
    public int getAgentId() {

        return agentId;

    }

    public int[] getPos() {

        return pos;

    }

}