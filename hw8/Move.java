class Move {
    public final int row;   // 0-indexed, top row = 0
    public final int col;   // 0-indexed, leftmost column = 0
    public final int value; // assigned value in {1,...,N}

    // Constructor
    public Move(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ", " + value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move m = (Move) o;
        return row == m.row && col == m.col && value == m.value;
    }

    @Override
    public int hashCode() {
        return 31 * row + 37 * col + 41 * value;
    }
}
