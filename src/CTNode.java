import java.util.*;

public class CTNode {

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