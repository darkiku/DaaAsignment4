package graph.dagsp;

import graph.common.*;
import graph.topo.KahnSort;
import java.util.*;

public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;

    private int[] dist;
    private int[] parent;
    private List<Integer> topoOrder;

    public DAGShortestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public int[] findShortestPaths(int source) {
        int n = graph.getN();
        dist = new int[n];
        parent = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        KahnSort kahn = new KahnSort(graph);
        topoOrder = kahn.sort();

        if (topoOrder == null) {
            throw new IllegalArgumentException("Graph is not a DAG");
        }

        metrics.startTimer();

        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Edge edge : graph.getAdjacent(u)) {
                    metrics.incrementRelaxations();
                    int v = edge.getTo();
                    int newDist = dist[u] + edge.getWeight();

                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        parent[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();

        return dist;
    }

    public List<Integer> getPath(int target) {
        if (dist == null || dist[target] == Integer.MAX_VALUE) {
            return null;
        }

        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = parent[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public Map<Integer, List<Integer>> getAllPaths(int source) {
        if (dist == null) {
            findShortestPaths(source);
        }

        Map<Integer, List<Integer>> paths = new HashMap<>();
        for (int v = 0; v < graph.getN(); v++) {
            if (dist[v] != Integer.MAX_VALUE) {
                paths.put(v, getPath(v));
            }
        }
        return paths;
    }

    public int[] getDistances() {
        return dist;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void printResults(int source) {
        System.out.println("\nShortest Paths from source " + source + ":");
        for (int v = 0; v < graph.getN(); v++) {
            if (dist[v] != Integer.MAX_VALUE) {
                System.out.printf("To vertex %d: distance=%d, path=%s\n",
                        v, dist[v], getPath(v));
            } else {
                System.out.printf("To vertex %d: unreachable\n", v);
            }
        }
    }
}