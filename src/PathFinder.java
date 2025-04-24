import java.util.*;

public class PathFinder {
    
    private PuzzleBoard board;
    private ArrayList<Constraint> constraints;

    public PathFinder(PuzzleBoard puzzleBoard, ArrayList<Constraint> constraints) {

        this.board = puzzleBoard;
        this.constraints = constraints;

    }

    public ArrayList<int[]> solveSingleAgent(int id) {

        int[] startEndPair = board.getStartEndPairs().get(id);
        PathFinderNode root = new PathFinderNode(startEndPair, board.getSize());

        PriorityQueue<PathFinderNode> openSet = new PriorityQueue<PathFinderNode>(Comparator.comparingInt(a -> a.fitness()));
        openSet.add(root);

        HashMap<Integer, Integer> bestCost = new HashMap<Integer, Integer>();
        bestCost.put(coordsToInteger(root.getCurrentPos()), 0);

        // Repeat until there are no possibilities to search
        while (!openSet.isEmpty()) {
            PathFinderNode current = openSet.poll();

            // If the agent is at the goal, return the path
            if (current.isAtGoal()) return current.getPath();

            // Search all 4 directions around the agent
            int[][] moveOperations = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int i = 0; i < 4; i++) {
                PathFinderNode neighbour = new PathFinderNode(current, moveOperations[i]);
                int neighbourPos = coordsToInteger(neighbour.getCurrentPos());

                // Check if neighbour is in a valid position
                if (!isValid(id, neighbour.getCurrentPos())) continue;

                // If this neighbour is a better solution to get to the current position, use it instead.
                if (bestCost.get(neighbourPos) == null || neighbour.getPath().size() < bestCost.get(neighbourPos)) {
                    bestCost.put(neighbourPos, neighbour.getPath().size());

                    openSet.add(neighbour);
                }
            }

        }

        // If there is no solution found when loop breaks, return failure
        return null;

    }

    public boolean isValid(int id, int[] pos) {

        // Check if off board
        if (pos[0] >= board.getSize() || pos[0] < 0 || pos[1] >= board.getSize() || pos[1] < 0) return false;
        
        // Check for constraint violations
        for (Constraint constraint: constraints) {
            if (constraint.getAgentId() == id && constraint.getPos()[0] == pos[0] && constraint.getPos()[1] == pos[1]) return false;
        }

        return true;

    }

    public int coordsToInteger(int[] pos) {

        return pos[0] * board.getSize() + pos[1];

    }

    public Solution solve(Solution prevSolution) {

        Solution solution = new Solution();

        if (prevSolution == null) {
            // Run A* algorithm for all agents
            for (int agent: board.getStartEndPairs().keySet()) {
                ArrayList<int[]> agentSolution = solveSingleAgent(agent);
                if (agentSolution == null) return null;
                solution.addPath(agent, agentSolution);
            }
        } else {
            // Run A* algorithm for only the agent that the constraint was applied to
            int changedAgent = constraints.getLast().getAgentId();
            ArrayList<int[]> agentSolution = solveSingleAgent(changedAgent);
            if (agentSolution == null) return null;

            for (int agent: board.getStartEndPairs().keySet()) {
                if (agent == changedAgent) {
                    // Add new path to changed agent
                    solution.addPath(changedAgent, agentSolution);
                } else {
                    // Add old paths for other agents
                    solution.addPath(agent, prevSolution.getPath(agent));
                }
            }
        }

        return solution;

    }

    // Checks if the solution is even possible
    // public boolean possibilityCheck(Solution solution) {

    //     int[][] moveOperations = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    //     ArrayList<Integer>[][] grid = new ArrayList<Integer>[board.getSize()][board.getSize()];

    //     for (int pathId: solution.getAgents()) {
    //         ArrayList<int[]> path = solution.getPath(pathId);

    //         for (int[] pos: path) {
    //             if (grid[pos[0]][pos[1]] == 0) {
    //                 grid[pos[0]][pos[1]] = String.valueOf(pathId);
    //             } else {
    //                 grid[pos[0]][pos[1]] = "#";
    //             }
    //         }
    //     }

    //     for (int agentId: board.getStartEndPairs().keySet()) {
    //         int[] endpointLocations = board.getStartEndPairs().get(agentId);
            
    //         boolean blocked = true;
    //         for (int[] direction: moveOperations) {
    //             if ()
    //         }

    //     }

    // }

}