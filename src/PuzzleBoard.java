import java.util.*;

public class PuzzleBoard {
    private int[][] grid;
    private HashMap<Integer, int[]> startEndPairs; // Maps color -> [startX, startY, endX, endY]
    private HashMap<Integer, String> colourMap;
    private int size;
    
    public PuzzleBoard(int dim, HashMap<Integer, int[]> startEndPairs, HashMap<Integer, String> colourMap) {

        this.grid = new int[dim][dim];
        this.colourMap = colourMap;
        this.startEndPairs = startEndPairs;

        for (int colour: startEndPairs.keySet()) {
            int[] colourPos = startEndPairs.get(colour);
            this.grid[colourPos[0]][colourPos[1]] = this.grid[colourPos[2]][colourPos[3]] = colour;
        }

    }

    public PuzzleBoard() {

        this.colourMap = new HashMap<>();
        this.colourMap.put(0, "black");

        this.startEndPairs = new HashMap<>();

    }

    public void addColour(int colourId, String colourName) {

        colourMap.put(colourId, colourName);

    }

    public String getColour(int colourId) {

        return colourMap.get(colourId);

    }

    public void setSize(int size) {

        this.size = size;
        this.grid = new int[size][size];

    }

    public int getSize() {

        return size;

    }

    public int[][] getGrid() {

        return grid;

    }

    public void addStartEndPair(int colourId, int sourceX, int sourceY, int goalX, int goalY) {

        startEndPairs.put(colourId, new int[]{sourceX, sourceY, goalX, goalY});
        grid[sourceX][sourceY] = grid[goalX][goalY] = colourId;

    }

    public HashMap<Integer, int[]> getStartEndPairs() {

        return startEndPairs;

    }

    public boolean isValidMove(int x, int y) {

        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;

    }

    public void printBoard() {

        // Print each row
        for (int row = 0; row < getSize(); row++) {
            System.out.print("\n");
            // Print each cell in row
            for (int col = 0; col < getSize(); col++) {
                if (grid[row][col] == 0) {
                    System.out.print("□ ");
                } else {
                    System.out.print(grid[row][col] + " ");
                }
            }
        }

    }

}
