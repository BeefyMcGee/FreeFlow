import java.util.*;

public class PathFinder {
    
    private PuzzleBoard board;
    private ArrayList<Constraint> constraints;

    public PathFinder(PuzzleBoard puzzleBoard, ArrayList<Constraint> constraints) {

        this.board = puzzleBoard;
        this.constraints = constraints;

    }

    // Run A* algorithm for a single agent
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

    // Check if the position is valid for the agent
    public boolean isValid(int id, int[] pos) {

        // Check if off board
        if (pos[0] >= board.getSize() || pos[0] < 0 || pos[1] >= board.getSize() || pos[1] < 0) return false;
        
        // Check for constraint violations
        for (Constraint constraint: constraints) {
            if (constraint.getAgentId() == id && constraint.getPos()[0] == pos[0] && constraint.getPos()[1] == pos[1]) return false;
        }

        return true;

    }

    // Convert x, y coordinates to integer
    public int coordsToInteger(int[] pos) {

        return pos[0] * board.getSize() + pos[1];

    }

    // Solve the puzzle using A* algorithm
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

    private class PathFinderNode {
        
        private ArrayList<int[]> path;
        private int[] source, goal, currentPos;

        PathFinderNode(int[] startEndPair, int boardSize) {

            this.source = new int[]{startEndPair[0], startEndPair[1]};
            this.goal = new int[]{startEndPair[2], startEndPair[3]};

            this.currentPos = new int[]{startEndPair[0], startEndPair[1]};

            this.path = new ArrayList<int[]>();
            this.path.add(new int[]{startEndPair[0], startEndPair[1]});

        }

        @SuppressWarnings("unchecked")
        PathFinderNode(PathFinderNode parent, int[] moveOperation) {

            this.source = parent.source;
            this.goal = parent.goal;

            int newX = parent.currentPos[0] + moveOperation[0];
            int newY = parent.currentPos[1] + moveOperation[1];

            this.currentPos = new int[]{newX, newY};

            this.path = (ArrayList<int[]>) parent.path.clone();
            this.path.add(currentPos);

        }

        // Get the current position of the agent
        public int[] getCurrentPos() {

            return currentPos;

        }

        // Get the path taken by the agent
        public ArrayList<int[]> getPath() {

            return path;

        }

        // Calculate the fitness of the node using the function f(x) = h(x) + c(x)
        public int fitness() {

            // c(x) is the total length the current path
            int c = path.size();

            // h(x) is the total manhattan distance from current pos to goal
            int h = Math.abs(currentPos[0] - goal[0]) + Math.abs(currentPos[1] - goal[1]);

            // f(x) is h + c
            return h + c;

        }

        // Check if the agent's current position is at the goal
        public boolean isAtGoal() {

            return (currentPos[0] == goal[0] && currentPos[1] == goal[1]);

        }

    }


}