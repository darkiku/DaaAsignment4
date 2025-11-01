package graph.common;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    private final boolean directed;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        adj.get(u).add(new Edge(u, v, weight));
        if (!directed) {
            adj.get(v).add(new Edge(v, u, weight));
        }
    }

    public void addEdge(int u, int v) {
        addEdge(u, v, 1);
    }

    public int getN() {
        return n;
    }

    public List<Edge> getAdjacent(int u) {
        return adj.get(u);
    }

    public boolean isDirected() {
        return directed;
    }

    public Graph getReverse() {
        Graph rev = new Graph(n, true);
        for (int u = 0; u < n; u++) {
            for (Edge e : adj.get(u)) {
                rev.addEdge(e.getTo(), e.getFrom(), e.getWeight());
            }
        }
        return rev;
    }

    public int getEdgeCount() {
        int count = 0;
        for (int u = 0; u < n; u++) {
            count += adj.get(u).size();
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Graph: n=%d, edges=%d, directed=%b\n",
                n, getEdgeCount(), directed));
        for (int u = 0; u < n; u++) {
            if (!adj.get(u).isEmpty()) {
                sb.append(u).append(" -> ");
                for (Edge e : adj.get(u)) {
                    sb.append(String.format("%d(w=%d) ", e.getTo(), e.getWeight()));
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}