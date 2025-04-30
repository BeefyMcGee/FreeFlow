import java.util.*;

public class ConflictBasedSearch {
    
    protected PuzzleBoard board;

    protected PriorityQueue<CTNode> nodeQueue;
    protected HashSet<String> closedList;
    protected boolean solved;

    public ConflictBasedSearch(PuzzleBoard board) {

        this.board = board;
        this.nodeQueue = new PriorityQueue<CTNode>(Comparator.comparingInt(a -> a.getSolutionCost()));
        this.closedList = new HashSet<String>();
        this.solved = false;

    }

    public Solution solveWithWeights() {

        CTNode root = new CTNode();

        // Create list of initial constraints disallowing colours being on the goal/source squares of other colours
        ArrayList<Constraint> initialConstraints = new ArrayList<Constraint>();
        for (int endpointColour: board.getStartEndPairs().keySet()) {
            for (int invaderColour: board.getStartEndPairs().keySet()) {
                if (endpointColour == invaderColour) continue;

                int[] endpointLocations = board.getStartEndPairs().get(endpointColour);

                initialConstraints.add(new Constraint(invaderColour, new int[]{endpointLocations[0], endpointLocations[1]}));
                initialConstraints.add(new Constraint(invaderColour, new int[]{endpointLocations[2], endpointLocations[3]}));
            }
        }

        root.setConstraints(initialConstraints);
        Solution rootSolution = findPaths(root, null);
        if (rootSolution == null) {
            // Invalid puzzle entered
            System.out.println("Invalid Puzzle");
            return null;
        }
        root.setSolution(rootSolution);
        ArrayList<Conflict> rootConflicts = findConflicts(rootSolution);
        if (rootConflicts.isEmpty()) return rootSolution;
        root.setConflictList(rootConflicts);
        nodeQueue.add(root);

        while (!nodeQueue.isEmpty()) {
            CTNode current = nodeQueue.poll();
            ArrayList<Conflict> conflictList = current.getConflictList();

            Conflict nextConflict = conflictList.get(0);
            for (int agentId: nextConflict.getViolatingAgents()) {
                // Make new node with additional constraint
                ArrayList<Constraint> newConstraints = new ArrayList<Constraint>(current.getConstraints());
                newConstraints.add(new Constraint(agentId, intToCoords(nextConflict.getPosition())));
                CTNode childNode = new CTNode(newConstraints);

                // Check if node has previously been visited
                String constraintSignature = childNode.getConstraintSignature();
                if (closedList.contains(constraintSignature)) continue;
                closedList.add(constraintSignature);

                // Generate paths with A*
                Solution childSolution = findPaths(childNode, current.getSolution());
                if (childSolution == null) continue; // No paths possible for this node

                // Find path conflicts
                ArrayList<Conflict> childConflicts = findConflicts(childSolution);
                if (childConflicts.isEmpty()) {
                    return childSolution; // Puzzle solution found
                } else {
                    childNode.setSolution(childSolution);
                    childNode.setConflictList(childConflicts);
                    childNode.setSolutionCost(childConflicts.size());
                    nodeQueue.add(childNode);

                    //printSolution(childSolution);
                }
            };

        }

        // Puzzle is unsolvable
        System.out.println("Unsolvable!");
        return null;

    }

    public Solution setupStepByStep() {

        CTNode root = new CTNode();

        // Create list of initial constraints disallowing colours being on the goal/source squares of other colours
        ArrayList<Constraint> initialConstraints = new ArrayList<Constraint>();
        for (int endpointColour: board.getStartEndPairs().keySet()) {
            for (int invaderColour: board.getStartEndPairs().keySet()) {
                if (endpointColour == invaderColour) continue;

                int[] endpointLocations = board.getStartEndPairs().get(endpointColour);

                initialConstraints.add(new Constraint(invaderColour, new int[]{endpointLocations[0], endpointLocations[1]}));
                initialConstraints.add(new Constraint(invaderColour, new int[]{endpointLocations[2], endpointLocations[3]}));
            }
        }

        root.setConstraints(initialConstraints);
        Solution rootSolution = findPaths(root, null);
        if (rootSolution == null) {
            // Invalid puzzle entered
            System.out.println("Invalid Puzzle");
            return null;
        }
        root.setSolution(rootSolution);
        ArrayList<Conflict> rootConflicts = findConflicts(rootSolution);
        if (rootConflicts.isEmpty()) {
            solved = true;
            return rootSolution;
        }
        root.setConflictList(rootConflicts);
        nodeQueue.add(root);

        return root.getSolution();

    }

    public Solution solveOneStep() {

        if (nodeQueue.isEmpty()) {
            System.out.println("Unsolvable!");
            return null;
        }

        CTNode current = nodeQueue.poll();
        ArrayList<Conflict> conflictList = current.getConflictList();

        Conflict nextConflict = conflictList.get(0);
        for (int agentId: nextConflict.getViolatingAgents()) {
            // Make new node with additional constraint
            ArrayList<Constraint> newConstraints = new ArrayList<Constraint>(current.getConstraints());
            newConstraints.add(new Constraint(agentId, intToCoords(nextConflict.getPosition())));
            CTNode childNode = new CTNode(newConstraints);

            // Check if node has previously been visited
            String constraintSignature = childNode.getConstraintSignature();
            if (closedList.contains(constraintSignature)) continue;
            closedList.add(constraintSignature);

            // Generate paths with A*
            Solution childSolution = findPaths(childNode, current.getSolution());
            if (childSolution == null) continue; // No paths possible for this node

            // Find path conflicts
            ArrayList<Conflict> childConflicts = findConflicts(childSolution);
            if (childConflicts.isEmpty()) {
                solved = true;
                return childSolution; // Puzzle solution found
            } else {
                childNode.setSolution(childSolution);
                childNode.setConflictList(childConflicts);
                childNode.setSolutionCost(childConflicts.size());
                nodeQueue.add(childNode);
            }
        };

        return current.getSolution();

    }

    public boolean isSolved() {

        return solved;

    }

    public Solution findPaths(CTNode node, Solution prevSolution) {

        PathFinder pf = new PathFinder(board, node.getConstraints());

        return pf.solve(prevSolution);

    }

    public ArrayList<Conflict> findConflicts(Solution solution) {

        HashMap<Integer, ArrayList<Integer>> gridOccupants = new HashMap<Integer, ArrayList<Integer>>();

        for (int agentId: solution.getAgents()) {
            for (int[] pathPos: solution.getPath(agentId)) {
                int gridSquare = coordsToInteger(pathPos);
                
                if (!gridOccupants.containsKey(gridSquare)) gridOccupants.put(gridSquare, new ArrayList<Integer>());
                gridOccupants.get(gridSquare).add(agentId);
            }
        }

        ArrayList<Conflict> conflictList = new ArrayList<Conflict>();

        int pathStep = 0;
        while (!gridOccupants.isEmpty()) {
            for (int pathId: solution.getAgents()) {
                ArrayList<int[]> path = solution.getPath(pathId);
                if (pathStep >= path.size()) continue;

                int intCoord = coordsToInteger(path.get(pathStep));

                if (gridOccupants.containsKey(intCoord)) {
                    // Add to conflict list if there are multiple colours on that square
                    if (gridOccupants.get(intCoord).size() > 1) conflictList.add(new Conflict(intCoord, gridOccupants.get(intCoord)));
                    gridOccupants.remove(intCoord);
                }   
            }

            pathStep++;
        }

        return conflictList;

    }

    public int coordsToInteger(int[] pos) {

        return pos[0] * board.getSize() + pos[1];

    }

    public int[] intToCoords(int intCoord) {

        return new int[]{Math.floorDiv(intCoord, board.getSize()), intCoord % board.getSize()};

    }

    public void printSolution(Solution solution) {

        System.out.println();

        String[][] grid = new String[board.getSize()][board.getSize()];

        for (int pathId: solution.getAgents()) {
            ArrayList<int[]> path = solution.getPath(pathId);

            for (int[] pos: path) {
                if (grid[pos[0]][pos[1]] == null) {
                    grid[pos[0]][pos[1]] = String.valueOf(pathId);
                } else {
                    grid[pos[0]][pos[1]] = "#";
                }
            }
        }

        for (int row = 0; row < board.getSize(); row++) {
            System.out.print("\n");
            for (int cell = 0; cell < board.getSize(); cell++) {
                if (grid[row][cell] == null) {
                    System.out.print("▢ ");
                } else {
                    System.out.print(grid[row][cell] + " ");
                }
            }
        }

    }

}
