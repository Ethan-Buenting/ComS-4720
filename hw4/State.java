/**
 * Ethan Buenting
 * COMS 4720 - Artificial Intelligence
 * Homework 4 
 * State class representing the current configuration of the grid, including the robot's position 
 * and the positions of crates and storage locations.
 */


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class State {

    private static class Point {
        final int r, c;
        Point(int r, int c) { this.r = r; this.c = c; }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return r == p.r && c == p.c;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(r, c);
        }
    }

    public static Heuristic heuristic;
    private boolean[][] PillerGrid;
    private static List<Point> storage;
    private List<Point> crates;
    private Point robotPoint;
    private static int n;
    private int g;

    public int getG() {
        return g;
    }

    /**
     * Construct the initial state from a file.
     */
    public State(String initialFile) throws FileNotFoundException, IllegalArgumentException {
        
        if (initialFile == null || initialFile.isEmpty()) {
            throw new FileNotFoundException("Initial file path cannot be null or empty.");
        }
        
        File file = new File(initialFile);
        Scanner sc = new Scanner(file);
        List<String> lines = new ArrayList<>();

        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        sc.close();

        State.n = lines.size();
        if (n == 0) throw new IllegalArgumentException("Empty input file.");

        this.PillerGrid = new boolean[n][n];
        State.storage = new ArrayList<>();
        this.crates = new ArrayList<>();

        // Counter to make sure there is exactly 1 robot
        int robotCount = 0;

        for (int i = 0; i < n; i++) {
            String line = lines.get(i);
            if (line.length() != n) {
                throw new IllegalArgumentException("Input file must be a square grid.");
            }
            for (int j = 0; j < n; j++) {
                char c = line.charAt(j);
                if (c != 'R' && c != 'C' && c != 'S' && c != 'P' && c != '.') {
                    throw new IllegalArgumentException("Invalid character in input file: " + c);
                }
                if (c == 'R') {
                    robotCount++;
                    robotPoint = new Point(i, j);
                }
                if (c == 'C') {
                    crates.add(new Point(i, j));
                }
                if (c == 'S') {
                    storage.add(new Point(i, j));
                }
                PillerGrid[i][j] = (c == 'P');
            }
        }
        if (robotCount != 1) {
            throw new IllegalArgumentException("There must be exactly one robot in the initial state.");
        }
        if (crates.size() > storage.size()) {
            throw new IllegalArgumentException("Number of crates must not exceed number of storage locations.");
        }
        this.g = 0;
    }

    public State(boolean[][] grid, List<Point> crates, Point robotPoint, int g) throws IllegalArgumentException  {
        this.PillerGrid = grid;
        this.crates = crates;
        this.robotPoint = robotPoint;
        this.g = g;
    }

    /**
     * Returns true if and only if all crates occupy distinct storage locations.
     * The robot's position is irrelevant for this test.
     */
    public boolean isGoal() {
        // Each crate must match a coordinate in the static storage list
        for (Point crate : this.crates) {
            if (!storage.contains(crate)) {
                return false;
            }
        }
        return true;
}

    public State successorState(Move m) throws IllegalArgumentException {
        Point robotNextPoint = robotPoint;

        switch (m) {
            case UP: robotNextPoint = new Point(robotNextPoint.r + 1, robotNextPoint.c); break;
            case DOWN: robotNextPoint = new Point(robotNextPoint.r - 1, robotNextPoint.c); break;
            case LEFT: robotNextPoint = new Point(robotNextPoint.r, robotNextPoint.c - 1); break;
            case RIGHT: robotNextPoint = new Point(robotNextPoint.r, robotNextPoint.c + 1); break;
            default: throw new IllegalArgumentException("Invalid move: " + m);
        }

        if (robotNextPoint.r < 0 || robotNextPoint.r >= n || robotNextPoint.c < 0 || robotNextPoint.c >= n) {
            throw new IllegalArgumentException("Move goes out of bounds.");
        } else if (PillerGrid[robotNextPoint.r][robotNextPoint.c]) {
            throw new IllegalArgumentException("Move goes into a piller.");
        } else if (crates.contains(robotNextPoint)) {
            // If the robot is trying to move into a crate, we need to check if the crate can be pushed
            Point crateNextPoint = null;
            switch (m) {
                case UP: crateNextPoint = new Point(robotNextPoint.r + 1, robotNextPoint.c); break;
                case DOWN: crateNextPoint = new Point(robotNextPoint.r - 1, robotNextPoint.c); break;
                case LEFT: crateNextPoint = new Point(robotNextPoint.r, robotNextPoint.c - 1); break;
                case RIGHT: crateNextPoint = new Point(robotNextPoint.r, robotNextPoint.c + 1); break;
            }
            if (crateNextPoint.r < 0 || crateNextPoint.r >= n || crateNextPoint.c < 0 || crateNextPoint.c >= n) {
                throw new IllegalArgumentException("Move pushes crate out of bounds.");
            } else if (PillerGrid[crateNextPoint.r][crateNextPoint.c]) {
                throw new IllegalArgumentException("Move pushes crate into a piller.");
            } else if (crates.contains(crateNextPoint)) {
                throw new IllegalArgumentException("Move pushes crate into another crate.");
            } else {
                // Move is valid, we can push the crate
                List<Point> newCrates = new ArrayList<>(crates);
                newCrates.remove(robotNextPoint);
                newCrates.add(crateNextPoint);
                return new State(PillerGrid, newCrates, robotNextPoint, g + 1);
            }
        }
        // Move is valid and does not push a crate
        return new State(PillerGrid, crates, robotNextPoint, g + 1);
    }

    public int cost() {
        if (heuristic == Heuristic.H1) {
            // H1: Sum of Manhattan distances from each crate to the nearest storage location
            int totalDistance = 0;
            for (Point crate : this.crates) {
                int minDistance = Integer.MAX_VALUE;
                for (Point s : storage) {
                    int distance = Math.abs(crate.r - s.r) + Math.abs(crate.c - s.c);
                    minDistance = Math.min(minDistance, distance);
                }
                totalDistance += minDistance;
            }
            return g + totalDistance;
        } else if (heuristic == Heuristic.H2) {
            // H2: Sum of Manhattan distances from robot to crate plus crate to nearest storage
            int totalDistance = 0;
            for (Point crate : this.crates) {
                totalDistance += Math.abs(robotPoint.r - crate.r) + Math.abs(robotPoint.c - crate.c);
                int minDistance = Integer.MAX_VALUE;
                for (Point s : storage) {
                    int distance = Math.abs(crate.r - s.r) + Math.abs(crate.c - s.c);
                    minDistance = Math.min(minDistance, distance);
                }
                totalDistance += minDistance;
            }
            return g + totalDistance;
        } else {
            throw new IllegalStateException("Invalid heuristic: " + heuristic);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State other = (State) obj;
        if (!this.robotPoint.equals(other.robotPoint)) return false;
        java.util.Set<Point> thisCrates = new java.util.HashSet<>(this.crates);
        java.util.Set<Point> otherCrates = new java.util.HashSet<>(other.crates);
        return thisCrates.equals(otherCrates);
    }

    @Override
    public int hashCode() {
        java.util.Set<Point> crateSet = new java.util.HashSet<>(this.crates);
        return java.util.Objects.hash(robotPoint, crateSet);
    }
}
