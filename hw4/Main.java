import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java Main <initial-file> <H1|H2>");
            return;
        }

        String initialFile = args[0];
        Heuristic heuristic = Heuristic.valueOf(args[1]);

        System.out.println("=== Warehouse Robot Demo ===");

        // ----------------------------
        // Create initial state and call all public functions
        // ----------------------------
        State initial = GraderUtil.createInitialState(initialFile, heuristic);
        System.out.println("Initial state:");
        System.out.println("g=" + initial.getG() + ", f=" + initial.cost() + ", isGoal=" + initial.isGoal());

        // ----------------------------
        // Test a single move for demonstration
        // ----------------------------
        System.out.println("\nTesting successorState for all moves from initial state:");
        for (Move m : Move.values()) {
            try {
                State succ = GraderUtil.applyMove(initial, m);
                System.out.println(m + " -> g=" + succ.getG() + ", f=" + succ.cost() + ", isGoal=" + succ.isGoal());
            } catch (IllegalArgumentException e) {
                System.out.println(m + " -> invalid move");
            }
        }

        // ----------------------------
        // Call WarehouseRobot.solve()
        // ----------------------------
        List<Move> solution = GraderUtil.solve(initialFile, heuristic);
        if (solution == null || solution.isEmpty()) {
            System.out.println("\nNo solution found.");
            return;
        }

        System.out.println("\nSolution returned by solve(): " + solution.size() + " moves");
        for (Move m : solution) System.out.print(m + " ");
        System.out.println();

        // ----------------------------
        // Replay solution step by step
        // ----------------------------
        System.out.println("\nReplaying solution step by step:");
        List<State> states = GraderUtil.replayMoves(initialFile, solution, heuristic);

        int stepCounter = 1;
        for (State s : states) {
            System.out.println("Step " + stepCounter + ": g=" + s.getG() + ", f=" + s.cost() + ", isGoal=" + s.isGoal());
            stepCounter++;
        }

        System.out.println("\nFinal state reached goal: " + states.get(states.size() - 1).isGoal());
    }
}
