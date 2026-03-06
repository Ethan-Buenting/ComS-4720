/*
 * Ethan Buenting
 * COMS 4720 - Artificial Intelligence
 * Homework 4 
 * WarehouseRobot class representing the robot that will be moving crates in the warehouse.
 */
import java.util.*;

public class WarehouseRobot {
    
    private static class Node implements Comparable<Node> {
        State state;
        Node parent;
        Move move;

        Node(State state, Node parent, Move move) {
            this.state = state;
            this.parent = parent;
            this.move = move;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.state.cost(), other.state.cost());
        }
    }

    public static List<Move> solve(String initialFile, Heuristic heuristic) {
        State initialState;
        try {
            initialState = new State(initialFile);
        } catch (Exception e) {
            System.err.println("Error creating initial state: " + e.getMessage());
            return null;
        }
        State.heuristic = heuristic;

        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<State> explored = new HashSet<>();

        frontier.add(new Node(initialState, null, null));

        while (!frontier.isEmpty()) {
            Node currentNode = frontier.poll();
            State currentState = currentNode.state;

            if (currentState.isGoal()) {
                return reconstructPath(currentNode);
            }

            if (explored.contains(currentState)) {
                continue;
            }
            explored.add(currentState);

            for (Move move : Move.values()) {
                try {
                    State nextState = currentState.successorState(move);
                    if (!explored.contains(nextState)) {
                        frontier.add(new Node(nextState, currentNode, move));
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid move, skip
                }
            }
        }
        return null; // No solution found
    }

    private static List<Move> reconstructPath(Node node) {
        List<Move> path = new ArrayList<>();
        Node current = node;
        
        // Trace backward until we hit the initial state
        while (current.move != null) {
            path.add(current.move);
            current = current.parent;
        }
        
        // The path was built backwards, so reverse it
        Collections.reverse(path);
        return path;
    }
}
