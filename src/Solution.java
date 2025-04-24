import java.util.*;

public class Solution {
    
    private HashMap<Integer, ArrayList<int[]>> paths;
    // private HashMap<Integer, ArrayList<Integer>> gridMap;
    // private int gridSize;

    Solution() {

        this.paths = new HashMap<Integer, ArrayList<int[]>>();
        // this.gridMap = new HashMap<Integer, ArrayList<Integer>>();

    }

    public void addPath(int agentId, ArrayList<int[]> path) {

        this.paths.put(agentId, path);

    }

    public ArrayList<int[]> getPath(int agentId) {

        return this.paths.get(agentId);

    }

    public ArrayList<Integer> getAgents() {

        return new ArrayList<Integer>(paths.keySet());

    }

}
