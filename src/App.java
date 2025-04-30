import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;

public class App extends Application {

    private static PuzzleBoard board;
    private GridPane grid;
    private static int currentPuzzleIndex;
    private static String puzzleFile;

    public static void main(String[] args) throws Exception {

        //testPuzzleFile("levelpack_10x10.txt");

        //board = gen.readFile("levelpack_1.txt", 1);
        currentPuzzleIndex = 127;
        puzzleFile = "levelpack_1.txt";
        launch(args);

    }

    public static ArrayList<Long> testPuzzleFile(String fileName) {

        PuzzleGenerator gen = new PuzzleGenerator("src/Puzzles");
        ArrayList<Long> times = new ArrayList<>();

        for (int i = 0; i < gen.getFileLength(fileName); i++) {
            PuzzleBoard currBoard = gen.readFile(fileName, i);

            System.out.println("Attempting puzzle " + i + "...");
            long start = System.currentTimeMillis();
            ConflictBasedSearch cbs = new ConflictBasedSearch(currBoard);
            cbs.solveWithWeights();
            long duration = System.currentTimeMillis() - start;
            times.add(duration);
            System.out.println("Puzzle " + i + " complete in " + duration + "ms.");
        }

        return times;

    }

    public static void setPuzzleBoard(PuzzleBoard puzzleboard) {

        board = puzzleboard;

    }

    @Override
    public void start(Stage primaryStage) {
        PuzzleGenerator gen = new PuzzleGenerator("src/Puzzles");
        board = gen.readFile(puzzleFile, currentPuzzleIndex);
        
        grid = new GridPane();
        drawStartingGrid();

        Button solveBtn = new Button("Solve (MA-CBS)");
        Button nextBtn = new Button("Next");
        Button prevBtn = new Button("Previous");
        Button solveSBS = new Button("Solve Step By Step");
        Button solveCBS = new Button("Solve (CBS)");

        solveBtn.setOnAction(e -> {
            long start = System.currentTimeMillis();

            MultiAgentConflictBasedSearch maCbs = new MultiAgentConflictBasedSearch(board);
            drawSolution(maCbs.solveWithWeights());

            System.out.println("MA-CBS Execution time: " + (System.currentTimeMillis() - start) + "ms");
        });

        solveCBS.setOnAction(e -> {
            long start = System.currentTimeMillis();

            ConflictBasedSearch cbs = new ConflictBasedSearch(board);
            drawSolution(cbs.solveWithWeights());

            System.out.println("CBS Execution time: " + (System.currentTimeMillis() - start) + "ms");
        });

        nextBtn.setOnAction(e -> {
            if (currentPuzzleIndex < 149) { // Assuming 150 puzzles (index from 0 to 149)
                currentPuzzleIndex++;
                board = gen.readFile(puzzleFile, currentPuzzleIndex);
                drawStartingGrid();
            }
        });

        prevBtn.setOnAction(e -> {
            if (currentPuzzleIndex > 0) { // Ensure we don't go below index 0
                currentPuzzleIndex--;
                board = gen.readFile(puzzleFile, currentPuzzleIndex);
                drawStartingGrid();
            }
        });

        solveSBS.setOnAction(e -> {
            board = gen.readFile(puzzleFile, currentPuzzleIndex);
            ConflictBasedSearch cbs = new ConflictBasedSearch(board);

            drawStartingGrid();
            drawSolution(cbs.setupStepByStep());

            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
                if (cbs.isSolved()) {
                    timeline.stop();
                    return;
                }

                Solution newSol = cbs.solveOneStep();
                if (newSol == null) {
                    timeline.stop();
                } else {
                    drawSolution(newSol);
                }
            });

            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        });

        VBox layout = new VBox(10, grid, solveBtn, solveCBS, prevBtn, nextBtn, solveSBS);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Flow Free Solver");
        primaryStage.show();
    }

    private void drawStartingGrid() {

        grid.getChildren().clear();

        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(50, 50);

                int cellColour = board.getGrid()[row][col];

                cell.setStyle("-fx-border-color: white; -fx-background-color: " + board.getColour(cellColour) + ";");
                grid.add(cell, col, row);
            }
        }

    }

    private void drawSolution(Solution solution) {

        for (int colourId: solution.getAgents()) {
            ArrayList<int[]> path = solution.getPath(colourId);

            for (int[] pathPos: path) {
                StackPane cell = (StackPane) getNodeFromGridPane(grid, pathPos[1], pathPos[0]);
                cell.setStyle("-fx-background-color: " + board.getColour(colourId) + "; -fx-border-color: black;");
            }
        }

    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

}
