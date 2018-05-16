package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private int times;
    private double[] experiments;
    private final double conf = 1.96;

    // perform T independent experiments on an N-by-N grid
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        times = T;
        experiments = experiment(N, pf);
    }

    private double[] experiment(int N, PercolationFactory pf) {
        double[] exp = new double[times];
        for (int i = 0; i < times; i++) {
            Percolation sim = pf.make(N);
            while (!sim.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                sim.open(row, col);
            }
            double t = (double) sim.numberOfOpenSites() / (N * N);
            exp[i] = t;
        }
        return exp;
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(experiments);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(experiments);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        return mean() - conf * stddev() / Math.sqrt(times);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        return mean() + conf * stddev() / Math.sqrt(times);
    }

}
