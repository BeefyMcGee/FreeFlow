import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PuzzleGenerator {
    
    private String folderPath;

    PuzzleGenerator(String folderPath) {

        this.folderPath = folderPath;

    }

    public PuzzleBoard readFile(String fileName) {

        PuzzleBoard newBoard = new PuzzleBoard();

        try {
            File puzzleFile = new File(folderPath + "/" + fileName);
            Scanner fileScanner = new Scanner(puzzleFile);

            while (fileScanner.hasNextLine()) {
                String[] splitLine = fileScanner.nextLine().split(" ");
                
                if (splitLine[0].equals("dim")) {
                    newBoard.setSize(Integer.valueOf(splitLine[1]));
                } else if (splitLine[0].equals("colour")) {
                    newBoard.addColour(Integer.valueOf(splitLine[1]), splitLine[2]);
                    newBoard.addStartEndPair(Integer.valueOf(splitLine[1]), Integer.valueOf(splitLine[3]), Integer.valueOf(splitLine[4]), Integer.valueOf(splitLine[5]), Integer.valueOf(splitLine[6]));
                }
            }

            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }

        return newBoard;

    }

}
