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

    // Add a colour with name and id to the colour map
    public void addColour(int colourId, String colourName) {

        colourMap.put(colourId, colourName);

    }

    // Get the colour name for a given colour id
    public String getColour(int colourId) {

        return colourMap.get(colourId);

    }

    // Set the dimensions of the puzzle board
    public void setSize(int size) {

        this.size = size;
        this.grid = new int[size][size];

    }

    // Get the dimensions of the puzzle board
    public int getSize() {

        return size;

    }

    // Get the grid of the puzzle board
    public int[][] getGrid() {

        return grid;

    }

    // Add source and goal positions for a specific colour to the puzzle board
    public void addStartEndPair(int colourId, int sourceX, int sourceY, int goalX, int goalY) {

        startEndPairs.put(colourId, new int[]{sourceX, sourceY, goalX, goalY});
        grid[sourceX][sourceY] = grid[goalX][goalY] = colourId;

    }

    // Retrieve the source and goal positions for all colours
    public HashMap<Integer, int[]> getStartEndPairs() {

        return startEndPairs;

    }

    // Check if an x, y coordinate is a valid position inside the puzzle board
    public boolean isValidMove(int x, int y) {

        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;

    }

    // Print the puzzle board to the console
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
