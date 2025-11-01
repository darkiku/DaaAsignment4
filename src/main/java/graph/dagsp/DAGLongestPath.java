package graph.dagsp;

import graph.common.*;
import graph.topo.KahnSort;
import java.util.*;

public class DAGLongestPath {
    private final Graph graph;
    private final Metrics metrics;

    private int[] dist;
    private int[] parent;
    private List<Integer> topoOrder;

    public DAGLongestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public int[] findLongestPaths(int source) {
        int n = graph.getN();
        dist = new int[n];
        parent = new int[n];

        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        KahnSort kahn = new KahnSort(graph);
        topoOrder = kahn.sort();

        if (topoOrder == null) {
            throw new IllegalArgumentException("Graph is not a DAG");
        }

        metrics.startTimer();

        for (int u : topoOrder) {
            if (dist[u] != Integer.MIN_VALUE) {
                for (Edge edge : graph.getAdjacent(u)) {
                    metrics.incrementRelaxations();
                    int v = edge.getTo();
                    int newDist = dist[u] + edge.getWeight();

                    if (newDist > dist[v]) {
                        dist[v] = newDist;
                        parent[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();

        return dist;
    }

    public CriticalPathResult findCriticalPath(int source) {
        findLongestPaths(source);

        int maxDist = Integer.MIN_VALUE;
        int endVertex = -1;

        for (int v = 0; v < graph.getN(); v++) {
            if (dist[v] > maxDist) {
                maxDist = dist[v];
                endVertex = v;
            }
        }

        if (endVertex == -1 || maxDist == Integer.MIN_VALUE) {
            return new CriticalPathResult(new ArrayList<>(), 0);
        }

        List<Integer> path = getPath(endVertex);

        return new CriticalPathResult(path, maxDist);
    }

    public List<Integer> getPath(int target) {
        if (dist == null || dist[target] == Integer.MIN_VALUE) {
            return null;
        }

        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = parent[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public int[] getDistances() {
        return dist;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public static class CriticalPathResult {
        private final List<Integer> path;
        private final int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }

        public List<Integer> getPath() {
            return path;
        }

        public int getLength() {
            return length;
        }

        @Override
        public String toString() {
            return String.format("Critical Path: %s, Length: %d", path, length);
        }
    }

    public void printResults(int source) {
        System.out.println("\nLongest Paths from source " + source + ":");
        for (int v = 0; v < graph.getN(); v++) {
            if (dist[v] != Integer.MIN_VALUE) {
                System.out.printf("To vertex %d: distance=%d, path=%s\n",
                        v, dist[v], getPath(v));
            }
        }

        CriticalPathResult criticalPath = findCriticalPath(source);
        System.out.println("\n" + criticalPath);
    }
}