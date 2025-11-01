package graph.scc;

import graph.common.*;
import java.util.*;

public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;

    private int[] ids;
    private int[] low;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private int id;

    private List<List<Integer>> sccs;

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public List<List<Integer>> findSCCs() {
        int n = graph.getN();
        ids = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        id = 0;

        Arrays.fill(ids, -1);

        metrics.startTimer();

        for (int i = 0; i < n; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTimer();

        return sccs;
    }

    private void dfs(int at) {
        metrics.incrementDfsVisits();

        ids[at] = low[at] = id++;
        stack.push(at);
        onStack[at] = true;

        for (Edge edge : graph.getAdjacent(at)) {
            metrics.incrementEdgesTraversed();
            int to = edge.getTo();

            if (ids[to] == -1) {
                dfs(to);
                low[at] = Math.min(low[at], low[to]);
            } else if (onStack[to]) {
                low[at] = Math.min(low[at], ids[to]);
            }
        }

        if (ids[at] == low[at]) {
            List<Integer> scc = new ArrayList<>();

            while (true) {
                int node = stack.pop();
                onStack[node] = false;
                scc.add(node);
                if (node == at) break;
            }

            sccs.add(scc);
        }
    }

    public int[] getComponentIds() {
        if (sccs == null) {
            findSCCs();
        }

        int n = graph.getN();
        int[] componentIds = new int[n];

        for (int compId = 0; compId < sccs.size(); compId++) {
            for (int vertex : sccs.get(compId)) {
                componentIds[vertex] = compId;
            }
        }

        return componentIds;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void printSCCs() {
        System.out.println("Strongly Connected Components:");
        for (int i = 0; i < sccs.size(); i++) {
            System.out.printf("SCC %d (size=%d): %s\n",
                    i, sccs.get(i).size(), sccs.get(i));
        }
    }
}