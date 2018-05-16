package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][] grid;
    private int openSites;
    private int size;
    private int maxIndex;
    private WeightedQuickUnionUF percSet;
    private WeightedQuickUnionUF avoidBackWash;
    private int top;
    private int bottom;

    // create N-by-N grid, with all sites initially blocked
    public Percolation(int N) {
        if (N <= 0) {
            throw new java.lang.IllegalArgumentException("N must be greater than 0");
        }
        size = N;
        maxIndex = N * N - 1;
        openSites = 0;
        grid = new boolean[size][size];

        percSet = new WeightedQuickUnionUF(N * N + 2);
        //avoid backwash
        avoidBackWash = new WeightedQuickUnionUF(N * N + 1);
        top = N * N;
        bottom = N * N + 1;

    }

    // open the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (toIndex(row, col) > maxIndex || toIndex(row, col) < 0) {
            throw new java.lang.IndexOutOfBoundsException("out of bounds");
        }
        if (!isOpen(row, col)) {
            grid[row][col] = true;
            openSites += 1;
        }

        int item = toIndex(row, col);

        if (row == 0) {
            percSet.union(top, item);
            avoidBackWash.union(top, item);
        }
        if (row == size - 1) {
            percSet.union(bottom, item);
        }

        if (col > 0 && isOpen(row, col - 1)) {
            percSet.union(toIndex(row, col - 1), item);
            avoidBackWash.union(toIndex(row, col - 1), item);
        }
        if (col < size - 1 && isOpen(row, col + 1)) {
            percSet.union(toIndex(row, col + 1), item);
            avoidBackWash.union(toIndex(row, col + 1), item);
        }

        if (row < size - 1 && isOpen(row + 1, col)) {
            percSet.union(toIndex(row + 1, col), item);
            avoidBackWash.union(toIndex(row + 1, col), item);
        }
        if (row > 0 && isOpen(row - 1, col)) {
            percSet.union(toIndex(row - 1, col), item);
            avoidBackWash.union(toIndex(row - 1, col), item);
        }


    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (toIndex(row, col) > maxIndex || toIndex(row, col) < 0) {
            throw new java.lang.IndexOutOfBoundsException("out of bounds");
        }
        return grid[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (toIndex(row, col) > maxIndex || toIndex(row, col) < 0) {
            throw new java.lang.IndexOutOfBoundsException("out of bounds");
        }
        int item = toIndex(row, col);
        return avoidBackWash.connected(top, item);
    }

    // number of open sites
    public int numberOfOpenSites() {
        return openSites;

    }

    // does the system percolate?
    public boolean percolates() {
        return percSet.connected(top, bottom);
    }

    private int toIndex(int r, int c) {
        return  r * size + c;
    }

    // use for unit testing (not required)
    public static void main(String[] args) {

    }
}
