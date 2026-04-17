import java.util.*;
import java.io.*;

/**
 * GraderUtil provides helper functions that exercise all required student public APIs.
 * Students see this file.
 */

public final class GraderUtil {

    private GraderUtil() {}

    /**
     * Construct the initial state from a file.
     */
    public static State createInitialState(String inputFile) throws Exception {
        return new State(inputFile);
    }

    /**
     * Applies a move to the given state and returns the resulting state.
     */
    public static State applyMove(State s, Move m) {
        return s.successorState(m);
    }

    /**
     * Solves using plain Minimax.
     * Returns a Result object containing the best move and nodes explored.
     */
    public static Result solveMinimax(State s) {
        return SudokuGame.solve(s, false);
    }

    /**
     * Solves using Minimax with alpha-beta pruning.
     * Returns a Result object containing the best move and nodes explored.
     */
    public static Result solveAlphaBeta(State s) {
        return SudokuGame.solve(s, true);
    }
}
