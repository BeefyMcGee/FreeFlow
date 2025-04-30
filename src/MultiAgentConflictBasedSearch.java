import java.util.*;

public class MultiAgentConflictBasedSearch extends ConflictBasedSearch {
    
    private HashMap<Set<Integer>, ArrayList<Constraint>> metaConstraints;
    private HashMap<Set<Integer>, Solution> metaSolutions;

    public MultiAgentConflictBasedSearch(PuzzleBoard board) {
        super(board);
        this.metaConstraints = new HashMap<>();
        this.metaSolutions = new HashMap<>();
    }

    @Override
    public Solution solveWithWeights() {
        CTNode root = new CTNode();
        
        // Initialize meta-agents (each color pair is a meta-agent)
        Set<Set<Integer>> metaAgents = new HashSet<>();
        for (int color : board.getStartEndPairs().keySet()) {
            Set<Integer> metaAgent = new HashSet<>();
            metaAgent.add(color);
            metaAgents.add(metaAgent);
        }

        // Create initial constraints for meta-agents
        ArrayList<Constraint> initialConstraints = new ArrayList<>();
        for (Set<Integer> metaAgent : metaAgents) {
            for (int endpointColor : board.getStartEndPairs().keySet()) {
                if (metaAgent.contains(endpointColor)) continue;
                
                int[] endpointLocations = board.getStartEndPairs().get(endpointColor);
                for (int agentId : metaAgent) {
                    initialConstraints.add(new Constraint(agentId, new int[]{endpointLocations[0], endpointLocations[1]}));
                    initialConstraints.add(new Constraint(agentId, new int[]{endpointLocations[2], endpointLocations[3]}));
                }
            }
        }

        root.setConstraints(initialConstraints);
        Solution rootSolution = findPaths(root, null);
        if (rootSolution == null) {
            System.out.println("Invalid Puzzle");
            return null;
        }

        root.setSolution(rootSolution);
        ArrayList<Conflict> rootConflicts = findConflicts(rootSolution);
        if (rootConflicts.isEmpty()) return rootSolution;

        // Group conflicts by meta-agents
        HashMap<Set<Integer>, ArrayList<Conflict>> metaConflicts = groupConflictsByMetaAgents(rootConflicts, metaAgents);
        root.setConflictList(rootConflicts);
        nodeQueue.add(root);

        while (!nodeQueue.isEmpty()) {
            CTNode current = nodeQueue.poll();
            ArrayList<Conflict> conflictList = current.getConflictList();

            // Find the most constrained meta-agent
            Set<Integer> mostConstrainedMetaAgent = findMostConstrainedMetaAgent(metaConflicts);
            ArrayList<Conflict> metaAgentConflicts = metaConflicts.get(mostConstrainedMetaAgent);

            if (metaAgentConflicts != null && !metaAgentConflicts.isEmpty()) {
                Conflict nextConflict = metaAgentConflicts.get(0);
                
                // Create new constraints for the meta-agent
                ArrayList<Constraint> newConstraints = new ArrayList<>(current.getConstraints());
                for (int agentId : mostConstrainedMetaAgent) {
                    newConstraints.add(new Constraint(agentId, intToCoords(nextConflict.getPosition())));
                }

                CTNode childNode = new CTNode(newConstraints);
                String constraintSignature = childNode.getConstraintSignature();
                
                if (closedList.contains(constraintSignature)) continue;
                closedList.add(constraintSignature);

                Solution childSolution = findPaths(childNode, current.getSolution());
                if (childSolution == null) continue;

                ArrayList<Conflict> childConflicts = findConflicts(childSolution);
                if (childConflicts.isEmpty()) {
                    return childSolution;
                }

                // Update meta-conflicts
                metaConflicts = groupConflictsByMetaAgents(childConflicts, metaAgents);
                childNode.setSolution(childSolution);
                childNode.setConflictList(childConflicts);
                childNode.setSolutionCost(childConflicts.size());
                nodeQueue.add(childNode);
            }
        }

        System.out.println("Unsolvable!");
        return null;
    }

    private HashMap<Set<Integer>, ArrayList<Conflict>> groupConflictsByMetaAgents(
            ArrayList<Conflict> conflicts, Set<Set<Integer>> metaAgents) {
        HashMap<Set<Integer>, ArrayList<Conflict>> metaConflicts = new HashMap<>();
        
        for (Conflict conflict : conflicts) {
            Set<Integer> violatingAgents = new HashSet<>(conflict.getViolatingAgents());
            
            // Find the smallest meta-agent that contains all violating agents
            Set<Integer> containingMetaAgent = null;
            for (Set<Integer> metaAgent : metaAgents) {
                if (metaAgent.containsAll(violatingAgents)) {
                    if (containingMetaAgent == null || metaAgent.size() < containingMetaAgent.size()) {
                        containingMetaAgent = metaAgent;
                    }
                }
            }
            
            if (containingMetaAgent != null) {
                metaConflicts.computeIfAbsent(containingMetaAgent, k -> new ArrayList<>()).add(conflict);
            }
        }
        
        return metaConflicts;
    }

    private Set<Integer> findMostConstrainedMetaAgent(
            HashMap<Set<Integer>, ArrayList<Conflict>> metaConflicts) {
        Set<Integer> mostConstrained = null;
        int maxConflicts = -1;
        
        for (Map.Entry<Set<Integer>, ArrayList<Conflict>> entry : metaConflicts.entrySet()) {
            int numConflicts = entry.getValue().size();
            if (numConflicts > maxConflicts) {
                maxConflicts = numConflicts;
                mostConstrained = entry.getKey();
            }
        }
        
        return mostConstrained;
    }
} 