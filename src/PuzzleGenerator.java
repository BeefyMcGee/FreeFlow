import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PuzzleGenerator {

    private String folderPath;

    PuzzleGenerator(String folderPath) {
        this.folderPath = folderPath;
    }

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

    private void addPathToBoard(int[] path, int colourId, PuzzleBoard board, int size) {
        if (path.length == 0) return;

        // First and last are the endpoints
        int startIndex = path[0];
        int endIndex = path[path.length - 1];

        int startRow = startIndex / size;
        int startCol = startIndex % size;
        int endRow = endIndex / size;
        int endCol = endIndex % size;

        String[] colorNames = {"red", "blue", "yellow", "green", "orange", "cyan", "magenta", "darkviolet", "deeppink", "white", "lime", "salmon", "tan", "brown", "gray", "lightseagreen", "dodgerblue"};

        // Color name could be anything or just use "color" + id
        board.addColour(colourId, colorNames[colourId - 1]);
        board.addStartEndPair(colourId, startRow, startCol, endRow, endCol);

        // (optional) If you want, you could also store the full path in the board for validation
    }
}