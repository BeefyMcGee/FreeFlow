package freeflowapp;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AlgorithmTester { 

    AlgorithmTester() {

    }

    // Runs the test for the specified puzzle file and returns the results
    private ArrayList<TestResult> runTest(String fileName, int numTrials) {

        PuzzleGenerator gen = new PuzzleGenerator("src/Puzzles/Test Data");
        ArrayList<TestResult> results = new ArrayList<>();

        for (int i = 0; i < gen.getFileLength(fileName); i++) {
            PuzzleBoard currBoard = gen.readFile(fileName, i);

            boolean timedOut = false;
            for (int attempt = 1; attempt <= numTrials; attempt++) {
                System.out.println("Attempt " + attempt + " for puzzle " + i + "...");

                ConflictBasedSearch cbs = new ConflictBasedSearch(currBoard);
                Solution solution = null;
                long duration = 0;

                ExecutorService executor = Executors.newSingleThreadExecutor();

                long start = System.currentTimeMillis();
                // Set a 5 minute timeout for the solver
                Future<Solution> future = executor.submit(() -> {
                    return cbs.solveWithWeights();
                });
                try {
                    solution = future.get(3, TimeUnit.MINUTES); // Wait for the solver to finish or timeout
                } catch (TimeoutException e) {
                    System.out.println("Solver timed out for puzzle " + i + " on attempt " + attempt);
                    timedOut = true;
                    future.cancel(true); // Interrupt the solver thread
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Solver interrupted for puzzle " + i + " on attempt " + attempt);
                    timedOut = true;
                } catch (OutOfMemoryError e) {
                    System.out.println("Solver ran out of memory for puzzle " + i + " on attempt " + attempt);
                    timedOut = true;
                }  finally {
                    // Shutdown the executor service
                    executor.shutdownNow(); 
                }
                // Measure execution time
                duration = System.currentTimeMillis() - start;

                // Record test metrics
                TestResult result = new TestResult();
                result.puzzleId = i;
                result.executionTime = duration;
                result.success = solution != null;
                result.nodesExpanded = cbs.getNodesExpanded();
                result.nodesGenerated = cbs.getNodesGenerated();
                result.size = currBoard.getSize();
                result.longestPathLength = gen.getLongestPathLength(fileName, i);
                result.numAgents = currBoard.getStartEndPairs().size(); // Assuming this method exists in PuzzleBoard
                results.add(result);

                if (timedOut) break; // Exit the loop if timed out
            }
        }

        return results;

    }

    // Writes the test results to a CSV file
    private void writeResultsToCSVFile(ArrayList<TestResult> results, String fileName) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Puzzles/" + fileName + ".csv"))) {
            writer.write("PUZZLE_ID,EXECUTION_TIME,SUCCESS,NODES_EXPANDED,NODES_GENERATED,SIZE,MAX_PATH_LENGTH,NUM_AGENTS\n");
            for (TestResult result : results) {
            writer.write(result.puzzleId + "," + result.executionTime + "," + result.success + "," +
                result.nodesExpanded + "," + result.nodesGenerated + "," + result.size + "," +
                result.longestPathLength + "," + result.numAgents + "\n");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the CSV file: " + e.getMessage());
        }

    };

    private void runTestAndWriteToCSV(String fileName, int startIndex, int numTrials) {

        PuzzleGenerator gen = new PuzzleGenerator("src/Puzzles/Test Data");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Puzzles/test_results.csv", true))) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                writer.flush(); // Ensure all data is written to the file before the program exits
            } catch (IOException e) {
                System.out.println("An error occurred while flushing the writer during shutdown: " + e.getMessage());
            }
            }));
            
            // Write the header only if the file is empty
            if (new java.io.File("src/Puzzles/test_results.csv").length() == 0) {
                writer.write("PUZZLE_ID,EXECUTION_TIME,SUCCESS,NODES_EXPANDED,NODES_GENERATED,SIZE,MAX_PATH_LENGTH,NUM_AGENTS\n");
            }

            PuzzleBoard currBoard = gen.readFile(fileName, startIndex);

            boolean timedOut = false;
            for (int attempt = 1; attempt <= numTrials; attempt++) {
                System.out.println("Attempt " + attempt + " for puzzle " + startIndex + "...");

                ConflictBasedSearch cbs = new ConflictBasedSearch(currBoard);
                Solution solution = null;
                long duration = 0;
                System.gc();

                ExecutorService executor = Executors.newSingleThreadExecutor();

                long start = System.currentTimeMillis();
                // Set a 5 minute timeout for the solver
                Future<Solution> future = executor.submit(() -> {
                    return cbs.solveWithWeights();
                });
                try {
                    solution = future.get(3, TimeUnit.MINUTES); // Wait for the solver to finish or timeout
                } catch (TimeoutException e) {
                    System.out.println("Solver timed out for puzzle " + startIndex + " on attempt " + attempt);
                    timedOut = true;
                    future.cancel(true); // Interrupt the solver thread
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Solver interrupted for puzzle " + startIndex + " on attempt " + attempt);
                    timedOut = true;
                } catch (OutOfMemoryError e) {
                    System.out.println("Solver ran out of memory for puzzle " + startIndex + " on attempt " + attempt);
                    timedOut = true;
                } finally {
                    // Shutdown the executor service
                    executor.shutdownNow();
                }
                // Measure execution time
                duration = System.currentTimeMillis() - start;

                // Record test metrics
                TestResult result = new TestResult();
                result.puzzleId = startIndex;
                result.executionTime = duration;
                result.success = solution != null;
                result.nodesExpanded = cbs.getNodesExpanded();
                result.nodesGenerated = cbs.getNodesGenerated();
                result.size = currBoard.getSize();
                result.longestPathLength = gen.getLongestPathLength(fileName, startIndex);
                result.numAgents = currBoard.getStartEndPairs().size(); // Assuming this method exists in PuzzleBoard

                // Write the result to the CSV file
                writer.write(result.puzzleId + "," + result.executionTime + "," + result.success + "," +
                    result.nodesExpanded + "," + result.nodesGenerated + "," + result.size + "," +
                    result.longestPathLength + "," + result.numAgents + "\n");

                if (timedOut) break;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the CSV file: " + e.getMessage());
        }

    }

    // Main method to run the algorithm tester
    public void testAlgorithm(String fileName, String outputFileName, int numTrials) {

        ArrayList<TestResult> results = runTest(fileName, numTrials);
        writeResultsToCSVFile(results, outputFileName);

        System.out.println("Test results written to " + outputFileName + ".csv");

    }

    // Main method to run the algorithm tester
    public void testSinglePuzzle(String fileName, int startIndex, int numTrials) {

        runTestAndWriteToCSV(fileName, startIndex, numTrials);

    }

    private class TestResult {
    
        public long executionTime;
        public boolean success;
        public int nodesExpanded, nodesGenerated, size, longestPathLength, puzzleId, numAgents;

    }

}
