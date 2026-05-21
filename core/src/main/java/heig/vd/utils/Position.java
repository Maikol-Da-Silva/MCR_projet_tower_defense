package heig.vd.utils;

/**
 * Represents a grid position (column, row).
 */
public class Position {
    private float col;
    private float row;

    public Position(float col, float row) {
        this.col = col;
        this.row = row;
    }

    /**
     * Calculate Euclidean distance to another position.
     */
    public double distanceTo(Position other) {
        double dx = this.col - other.col;
        double dy = this.row - other.row;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /* Getters / Setters */
    public float getCol() { return col; }
    public void setCol(float col) { this.col = col; }

    public float getRow() { return row; }
    public void setRow(float row) { this.row = row; }

    @Override
    public String toString() {
        return String.format("Position(%.1f, %.1f)", col, row);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) return false;
        Position p = (Position) obj;
        return Float.compare(this.col, p.col) == 0 && Float.compare(this.row, p.row) == 0;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(col) ^ Float.floatToIntBits(row);
    }
}
