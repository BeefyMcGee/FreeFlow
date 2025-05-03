import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class PuzzleGenerator {

    private String folderPath;

    PuzzleGenerator(String folderPath) {

        this.folderPath = folderPath;

    }

    // Reads the entire puzzle file and returns the puzzle located at the specified line (puzzleIndex)
    public PuzzleBoard readFile(String fileName, int puzzleIndex) {

        PuzzleBoard newBoard = new PuzzleBoard();

        try {
            File puzzleFile = new File(folderPath + "/" + fileName);
            Scanner fileScanner = new Scanner(puzzleFile);

            // Read all puzzles line by line
            int currentLine = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (currentLine == puzzleIndex) {
                    // Parse this puzzle
                    parsePuzzleLine(line, newBoard);
                    break;
                }
                currentLine++;
            }

            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        return newBoard;

    }

    // Returns the longest path length in the specified puzzle's solution
    public int getLongestPathLength(String fileName, int puzzleIndex) {

        int longestPathLength = 0;

        try {
            File puzzleFile = new File(folderPath + "/" + fileName);
            Scanner fileScanner = new Scanner(puzzleFile);

            // Read all puzzles line by line
            int currentLine = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (currentLine == puzzleIndex) {
                    // Parse this puzzle
                    String[] sections = line.split(";");
                    for (int i = 1; i < sections.length; i++) {
                        String[] pathStr = sections[i].split(",");
                        longestPathLength = Math.max(longestPathLength, pathStr.length);
                    }
                    break;
                }
                currentLine++;
            }

            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        return longestPathLength;

    }

    // Reads the puzzle line from the file and parses it into a PuzzleBoard object
    private void parsePuzzleLine(String line, PuzzleBoard board) {

        String[] sections = line.split(";");
        String[] header = sections[0].split(",");

        int size = Integer.parseInt(header[0]);
        board.setSize(size);

        int colourId = 1; // Start assigning colours from 1

        // Parse the rest of the paths
        for (int i = 1; i < sections.length; i++) {
            String[] pathStr = sections[i].split(",");
            int[] path = new int[pathStr.length];
            for (int j = 0; j < pathStr.length; j++) {
                path[j] = Integer.parseInt(pathStr[j]);
            }
            addPathToBoard(path, colourId++, board, size);
        }

    }

    // Adds the two endpoints of a new colour to the puzzle board
    private void addPathToBoard(int[] path, int colourId, PuzzleBoard board, int size) {
        
        if (path.length == 0) return;

        // First and last are the endpoints
        int startIndex = path[0];
        int endIndex = path[path.length - 1];

        int startRow = startIndex / size;
        int startCol = startIndex % size;
        int endRow = endIndex / size;
        int endCol = endIndex % size;

        String[] colorNames = {"red", "green", "blue", "yellow", "orange", "cyan", "magenta", "maroon", "darkviolet", "white", "gray", "lime", "tan", "brown", "dodgerblue", "lightseagreen"};

        board.addColour(colourId, colorNames[colourId - 1]);
        board.addStartEndPair(colourId, startRow, startCol, endRow, endCol);

    }

    // Returns the number of lines in the file, which is the number of puzzles
    public long getFileLength(String fileName) {

        Path path = Paths.get(folderPath + "/" + fileName);

        try {
            return Files.lines(path).count();
        } catch (IOException e) {
            return 0;
        }

    }

}