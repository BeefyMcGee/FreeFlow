import java.util.*;
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

    public static void main(String[] args) throws Exception {

        PuzzleGenerator gen = new PuzzleGenerator("src/Puzzles");
        ArrayList<Long> times = new ArrayList<>();

        for (int i = 0; i < 150; i++) {
            PuzzleBoard currBoard = gen.readFile("levelpack_1.txt", i);

            System.out.println("Attempting puzzle " + i + "...");
            long start = System.currentTimeMillis();
            ConflictBasedSearch cbs = new ConflictBasedSearch(currBoard);
            cbs.solveWithWeights();
            long duration = System.currentTimeMillis() - start;
            times.add(duration);
            System.out.println("Puzzle " + i + " complete in " + duration + "ms.");
        }


        //board = gen.readFile("levelpack_1.txt", 1);
        //launch(args);

    }

    public static void setPuzzleBoard(PuzzleBoard puzzleboard) {

        board = puzzleboard;

    }

    @Override
    public void start(Stage primaryStage) {
        PuzzleGenerator gen = new PuzzleGenerator("src/Puzzles");
        board = gen.readFile("levelpack_1.txt", 0);
        
        grid = new GridPane();
        drawStartingGrid();

        Button solveBtn = new Button("Solve");
        Button nextBtn = new Button("Next");
        Button prevBtn = new Button("Previous");

        solveBtn.setOnAction(e -> {
            long start = System.currentTimeMillis();

            ConflictBasedSearch cbs = new ConflictBasedSearch(board);
            drawSolution(cbs.solveWithWeights());

            System.out.println("Execution time: " + (System.currentTimeMillis() - start) + "ms");
        });

        VBox layout = new VBox(10, grid, prevBtn, nextBtn);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Flow Free Solver");
        primaryStage.show();
    }

    private void drawStartingGrid() {

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
