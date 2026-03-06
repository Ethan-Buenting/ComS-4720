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
    public static State createInitialState(String initialFile, Heuristic heuristic) throws Exception {
        State.heuristic = heuristic;
        return new State(initialFile);
    }

    /**
     * Apply a single move to a state, returns the successor state.
     * Also calls all required functions to ensure they are exercised.
     */
    public static State applyMove(State current, Move m) throws Exception {
        State next = current.successorState(m);

        // exercise all required public functions
        int g = next.getG();
        int f = next.cost();
        boolean goal = next.isGoal();

        return next;
    }

    /**
     * Replay a sequence of moves starting from initial state.
     * Returns a list of states after each move.
     * All public functions are called at each step.
     */
    public static List<State> replayMoves(String initialFile, List<Move> moves, Heuristic heuristic) throws Exception {
        State s = createInitialState(initialFile, heuristic);
        List<State> states = new ArrayList<>();

        for (Move m : moves) {
            s = applyMove(s, m);
            states.add(s);
        }

        return states;
    }

    /**
     * Call WarehouseRobot.solve() for a given initial file and heuristic.
     * Returns the solution moves.
     */
    public static List<Move> solve(String initialFile, Heuristic heuristic) throws Exception {
        State.heuristic = heuristic;
        return WarehouseRobot.solve(initialFile, heuristic);
    }
}
