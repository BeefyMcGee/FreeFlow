import java.util.*;

public class PathFinderNode {
    
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

    public int[] getSource() {

        return source;

    }

    public int[] getGoal() {

        return goal;

    }

    public int[] getCurrentPos() {

        return currentPos;

    }

    public ArrayList<int[]> getPath() {

        return path;

    }

    public int fitness() {

        // c(x) is the total length the current path
        int c = path.size();

        // h(x) is the total manhattan distance from current pos to goal
        int h = Math.abs(currentPos[0] - goal[0]) + Math.abs(currentPos[1] - goal[1]);

        // f(x) is h + c
        return h + c;

    }

    public boolean isAtGoal() {

        return (currentPos[0] == goal[0] && currentPos[1] == goal[1]);

    }

}
