package freeflowapp;
public class Constraint {

    private int agentId;
    private int[] pos;

    Constraint(int agentId, int[] pos) {

        this.agentId = agentId;
        this.pos = pos;

    }
    
    // Return the agent id of the constraint
    public int getAgentId() {

        return agentId;

    }

    // Return the position of the constraint
    public int[] getPos() {

        return pos;

    }

}