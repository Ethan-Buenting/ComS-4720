import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Usage: java Main <input-file>");
            return;
        }

        String file = args[0];

        State s = GraderUtil.createInitialState(file);

        System.out.println("Initial state:");
        System.out.println("isGoal: " + s.isGoal());
        System.out.println("isTerminal: " + s.isTerminal());

        if (s.isTerminal()) {
            System.out.println("utility: " + s.utility());
        }

        System.out.println("Locally legal moves: " +
            s.getLocallyLegalMoves().size());

        List<Move> legalMoves = s.getLegalMoves();
        System.out.println("AC-3 legal moves: " + legalMoves.size());

        // Apply one move (if exists)
        if (!legalMoves.isEmpty()) {
            Move m = legalMoves.get(0);
            State next = GraderUtil.applyMove(s, m);

            System.out.println("\nAfter applying move: (" +
                m.row + "," + m.col + ")=" + m.value);

            System.out.println("isGoal: " + next.isGoal());
            System.out.println("isTerminal: " + next.isTerminal());

            if (next.isTerminal()) {
                System.out.println("utility: " + next.utility());
            }
        }

        // Solve using Minimax
        Result result = GraderUtil.solveMinimax(s);
        Move best = result.move;
        System.out.println("\nTotal nodes explored (Minimax): " + result.nodes);

        if (best != null) {
            System.out.println("Best move (Minimax): (" +
                    best.row + "," + best.col + ")=" + best.value);
        } else {
            System.out.println("Best move (Minimax): null");
        }

        // Solve using Alpha-Beta (for COMS 5720)
        Result resultAB = GraderUtil.solveAlphaBeta(s);
        Move bestAB = resultAB.move;
        System.out.println("\nTotal nodes explored (Alpha-Beta): " + resultAB.nodes);

        if (bestAB != null) {
            System.out.println("Best move (Alpha-Beta): (" +
                    bestAB.row + "," + bestAB.col + ") = " + bestAB.value);
        } else {
            System.out.println("Best move (Alpha-Beta): null");
        }
    }
}
