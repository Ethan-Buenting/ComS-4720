import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class State {

    private int n;
    private int k;
    private int[][] state_grid;
    private List<Integer>[][] domain;

    public State (String fileName) throws FileNotFoundException, IllegalArgumentException {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        List<String> cells = new ArrayList<>();

        while (sc.hasNext()) {
            cells.add(sc.next());
        }
        sc.close();

        int totalCells = cells.size();
        this.n = (int) Math.sqrt(totalCells);
        this.k = (int) Math.sqrt(n);
        if (n * n != totalCells) {
            throw new IllegalArgumentException("Not a valid input file");
        } else if (totalCells == 0) {
            throw new IllegalArgumentException("Input file is empty");
        } else if (k * k != n) {
            throw new IllegalArgumentException("n must be perfect square");
        }

        this.state_grid = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                domain[i][j] = new ArrayList<>();
                for (int l = 1; l < n + 1; l++) {
                    domain[i][j].add(l);
                }
            }
        }

        for(int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String cell = cells.get(i*n+j);
                if (cell.equals(".")) {
                    this.state_grid[i][j] = 0; // Maybe should be NULL, but 0 for simplicity sake now
                } else {
                    try {
                        this.state_grid[i][j] = Integer.parseInt(cell);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Input file contains invalid cell input at " + i + ", " + j);
                    }
                    updateDomains(i, j, state_grid[i][j]);
                }
            }
        }
    }

    public State (int n, int k, int[][]state_grid, List<Integer>[][] domain) {
        this.n = n;
        this.k = k;
        this.state_grid = state_grid;
        this.domain = domain;

    }

    public boolean isGoal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int temp = state_grid[i][j];
                if (temp == 0) {
                    return false;
                }
                this.state_grid[i][j] = 0;
                if (isValid(i, j, temp) == false) {
                    this.state_grid[i][j] = temp;
                    return false;
                }
                this.state_grid[i][j] = temp;
            }
        }
        return true;
    }

    public boolean isTerminal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (domain[i][j].size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public int utility() {
        if (!isTerminal()) throw new IllegalStateException("State is not terminal");
        if (isGoal()) return 1;
        return 0;
    }

    public State successorState(Move m) throws IllegalArgumentException {

    }

    // Checks to see if a value placed in a cell at row and column would be valid
    private boolean isValid(int row, int col, int value) {
        for (int i = 0; i < n; i++) {
            if (state_grid[row][i] == value) return false;
        }

        for (int i = 0; i < n; i++) {
            if (state_grid[i][col] == value) return false;
        }

        int subGridRowStart = row - row % k;
        int subGridColStart = col - col % k;

        for (int i = subGridRowStart; i < subGridRowStart + k; i++) {
            for (int j = subGridColStart; j < subGridColStart + k; j++) {
                if (state_grid[i][j] == value) return false;
            }
        }

        return true;
    }

    private void updateDomains(int row, int col, int value) {
        // Remove value from domains of row and column
        for (int i = 0; i < n; i++) {
            domain[row][i].remove(Integer.valueOf(value));
            domain[i][col].remove(Integer.valueOf(value));
        }

        // Remove value from domains of subgrid
        int subGridRowStart = row - row % k;
        int subGridColStart = col - col % k;

        for (int i = subGridRowStart; i < subGridRowStart + k; i++) {
            for (int j = subGridColStart; j < subGridColStart + k; j++) {
                domain[i][j].remove(Integer.valueOf(value));
            }
        }

        // Update domain of input cell to only hold value
        domain[row][col] = new ArrayList<>();
        domain[row][col].add(value);
    } 
}

/*
    Note to self: Maybe look at bitwise operations for domain instead of ArrayList for better space complexity
    Note to self: boolean arrays for faster validity checks?
 */