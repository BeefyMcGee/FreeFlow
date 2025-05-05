package freeflowapp;
import java.util.*;

public class ConflictBasedSearch {
    
    private PuzzleBoard board;
    private PriorityQueue<CTNode> nodeQueue;
    private HashSet<String> closedList;
    private boolean solved;
    private int nodesExpanded, nodesGenerated;

    public ConflictBasedSearch(PuzzleBoard board) {

        this.board = board;
        this.nodeQueue = new PriorityQueue<CTNode>(Comparator.comparingInt(a -> a.getSolutionCost()));
        this.closedList = new HashSet<String>();
        this.solved = false;
        this.nodesExpanded = 0;
        this.nodesGenerated = 0;

    }

    // Run the CBS algorithm to find a solution to the puzzle, using conflict numbers as weights to prioritise nodes
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
            // Get the node with the lowest cost from the queue
            CTNode current = nodeQueue.poll();
            ArrayList<Conflict> conflictList = current.getConflictList();
            // Record new node expansion
            nodesExpanded++;

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

                // Record new node generation
                nodesGenerated++;

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
                }
            };

        }

        // Puzzle is unsolvable
        System.out.println("Unsolvable!");
        return null;

    }

    // Perform only the first initialisation step of the CBS algorithm by creating the root node of the CT
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

    // Perform one step of the CBS algorithm by expanding the first node in the queue and generating its children
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

    // Return whether the puzzle has been solved or not
    public boolean isSolved() {

        return solved;

    }

    // Runs the low-level A* pathfinding algorithm to find a solution for the given node
    public Solution findPaths(CTNode node, Solution prevSolution) {

        PathFinder pf = new PathFinder(board, node.getConstraints());

        return pf.solve(prevSolution);

    }

    // Find all conflicts in the given solution
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

    // Convert a 2D coordinate to a single integer square id
    public int coordsToInteger(int[] pos) {

        return pos[0] * board.getSize() + pos[1];

    }

    // Convert a single integer square id to a 2D coordinate
    public int[] intToCoords(int intCoord) {

        return new int[]{Math.floorDiv(intCoord, board.getSize()), intCoord % board.getSize()};

    }

    // Return the number of nodes expanded (non-leaf nodes) in the search tree
    public int getNodesExpanded() {

        return nodesExpanded;

    }

    // Return the number of nodes generated in the search tree
    public int getNodesGenerated() {

        return nodesGenerated;

    }

    private class CTNode {

        private ArrayList<Constraint> constraints;
        private Solution solution;
        private ArrayList<Conflict> conflictList;
        int solutionCost;

        CTNode() {

            constraints = new ArrayList<Constraint>();

        }

        CTNode(ArrayList<Constraint> constraints) {

            this.constraints = constraints;

        }

        public void setConflictList(ArrayList<Conflict> conflictList) {

            this.conflictList = conflictList;

        }

        public ArrayList<Conflict> getConflictList() {

            return conflictList;

        }

        public void setConstraints(ArrayList<Constraint> constraints) {

            this.constraints = constraints;

        }

        public ArrayList<Constraint> getConstraints() {

            return constraints;

        }

        public String getConstraintSignature() {

            if (constraints.isEmpty()) return null;

            ArrayList<String> constraintStrings = new ArrayList<String>();
            for (Constraint constraint: constraints) {
                constraintStrings.add(constraint.getAgentId() + ":" + constraint.getPos()[0] + "," + constraint.getPos()[1]);
            }
            Collections.sort(constraintStrings);

            return String.join("|", constraintStrings);

        }
        
        public Solution getSolution() {

            return solution;

        }
        
        public void setSolution(Solution solution) {

            this.solution = solution;

        }
        
        public int getSolutionCost() {

            return solutionCost;

        }
        
        public void setSolutionCost(int solutionCost) {

            this.solutionCost = solutionCost;

        }

    }

    private class Conflict {
    
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

}
