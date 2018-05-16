package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import java.util.LinkedList;

public class Solver {
    private LinkedList<WorldState> sol;

    public Solver(WorldState initial) {
        MinPQ<SNode> queue = new MinPQ<>();
        sol = new LinkedList<>();
        SNode first = new SNode(initial, 0, null);
        queue.insert(first);

        while (!queue.min().worldstate.isGoal()) {
            SNode last = queue.delMin();
            for (WorldState w : last.worldstate.neighbors()) {

                SNode next = new SNode(w, last.moves + 1, last);
                if (last.prev == null) {
                    queue.insert(next);
                } else if (!last.prev.worldstate.equals(w)) {
                    queue.insert(next);

                }
            }
        }
        SNode node = queue.min();
        while (node != null) {
            sol.addFirst(node.worldstate);
            node = node.prev;
        }
    }



    // Returns the minimum number of moves to solve the puzzle starting
    // at the initial WorldState.
    public int moves() {
        return sol.size() - 1;
    }

    // Returns a sequence of WorldStates from the initial WorldState
    // to the solution.
    public Iterable<WorldState> solution() {
        return sol;
    }


    public class SNode implements Comparable<SNode> {
        WorldState worldstate;
        int moves;
        SNode prev;

        public SNode(WorldState ws, int m, SNode p) {
            worldstate = ws;
            moves = m;
            prev = p;
        }



        @Override
        public int compareTo(SNode node) {
            int p1 = node.moves + node.worldstate.estimatedDistanceToGoal();
            int p2 = this.moves + this.worldstate.estimatedDistanceToGoal();
            if (p1 < p2) {
                return 1;
            } else if (p1 > p2) {
                return -1;
            } else {
                return 0;
            }

        }
    }

}
