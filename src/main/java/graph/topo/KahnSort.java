package graph.topo;

import graph.common.*;
import java.util.*;

public class KahnSort {
    private final Graph graph;
    private final Metrics metrics;

    public KahnSort(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public List<Integer> sort() {
        int n = graph.getN();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Edge edge : graph.getAdjacent(u)) {
                inDegree[edge.getTo()]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementPushOperations();
            }
        }

        List<Integer> topoOrder = new ArrayList<>();

        metrics.startTimer();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementPopOperations();
            topoOrder.add(u);

            for (Edge edge : graph.getAdjacent(u)) {
                metrics.incrementEdgesTraversed();
                int v = edge.getTo();
                inDegree[v]--;

                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementPushOperations();
                }
            }
        }

        metrics.stopTimer();

        if (topoOrder.size() != n) {
            return null;
        }

        return topoOrder;
    }

    public List<Integer> sortWithMapping(List<List<Integer>> sccs) {
        List<Integer> order = sort();
        if (order == null) return null;

        List<Integer> expandedOrder = new ArrayList<>();
        for (int compId : order) {
            if (compId < sccs.size()) {
                expandedOrder.addAll(sccs.get(compId));
            }
        }

        return expandedOrder;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public boolean isDAG() {
        return sort() != null;
    }
}