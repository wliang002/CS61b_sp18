package hw4.puzzle;
import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;


public class Board implements WorldState {
    private int size;
    private ArrayList<Integer> _board;
    private int manh;

    public Board(int[][] tiles) {
        size = tiles.length;
        _board = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                _board.add(tiles[i][j]);
            }
        }

        manh  = -1;


    }



    private int linearize(int r, int c) {
        return r * size + c;
    }

    public int tileAt(int i, int j) {
        if (i < 0 || i > size - 1 || j < 0 || j > size - 1) {
            throw new IndexOutOfBoundsException("index out of bound");
        }
        return _board.get(linearize(i, j));
    }

    public int size() {
        return size;
    }

    /**
     * @source hw4 spec
     * Returns neighbors of this board.
     * SPOILERZ: This is the answer.
     */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int s = size();
        int b = -1;
        int z = -1;
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (tileAt(i, j) == 0) {
                    b = i;
                    z = j;
                }
            }
        }
        int[][] board = new int[s][s];
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                board[i][j] = tileAt(i, j);
            }
        }
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (Math.abs(-b + i) + Math.abs(j - z) - 1 == 0) {
                    board[b][z] = board[i][j];
                    board[i][j] = 0;
                    Board neighbor = new Board(board);
                    neighbors.enqueue(neighbor);
                    board[i][j] = board[b][z];
                    board[b][z] = 0;
                }
            }
        }
        return neighbors;
    }


    public int hamming() {
        int hamm = 0;
        for (int i = 0; i < size * size - 1; i++) {
            if (_board.get(i) != i + 1) {
                hamm++;
            }
        }
        return hamm;
    }

    private int steps(int[] pos, int[] goal) {
        return Math.abs(goal[0] - pos[0]) + Math.abs(goal[1] - pos[1]);

    }

    private int[] positions(int k) {
        int[] p = new int[2];
        p[0] = k / size;
        p[1] = k % size;
        return p;
    }
    public int manhattan() {
        if (manh == -1) {
            manh = 0;
            for (int i = 0; i < size * size - 1; i++) {
                int[] pos = positions(_board.indexOf(i + 1));
                int[] goal = positions(i);
                manh += steps(pos, goal);
            }
        }
        return manh;
    }

    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    public boolean equals(Object y) {
        if (y == null || this.getClass() != y.getClass()) {
            return false;
        }

        if (this == y) {
            return true;
        }

        Board b = (Board) y;
        if (size != b.size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tileAt(i, j) != b.tileAt(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /** Returns the string representation of the board.
     * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
