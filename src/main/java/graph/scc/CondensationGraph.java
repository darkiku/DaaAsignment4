package graph.scc;

import graph.common.*;
import java.util.*;

public class CondensationGraph {
    private final Graph originalGraph;
    private final List<List<Integer>> sccs;
    private final int[] componentIds;

    private Graph condensation;
    private Map<Integer, Integer> componentWeights;

    public CondensationGraph(Graph originalGraph, List<List<Integer>> sccs, int[] componentIds) {
        this.originalGraph = originalGraph;
        this.sccs = sccs;
        this.componentIds = componentIds;
        this.componentWeights = new HashMap<>();
    }

    public Graph buildCondensation() {
        int numComponents = sccs.size();
        condensation = new Graph(numComponents, true);

        Set<String> addedEdges = new HashSet<>();

        for (int u = 0; u < originalGraph.getN(); u++) {
            int compU = componentIds[u];

            for (Edge edge : originalGraph.getAdjacent(u)) {
                int v = edge.getTo();
                int compV = componentIds[v];

                if (compU != compV) {
                    String edgeKey = compU + "->" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(compU, compV, edge.getWeight());
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        return condensation;
    }

    public void calculateComponentWeights() {
        for (int compId = 0; compId < sccs.size(); compId++) {
            List<Integer> scc = sccs.get(compId);
            int weight = scc.size();
            componentWeights.put(compId, weight);
        }
    }

    public Graph getCondensation() {
        if (condensation == null) {
            buildCondensation();
        }
        return condensation;
    }

    public int getComponentWeight(int compId) {
        if (componentWeights.isEmpty()) {
            calculateComponentWeights();
        }
        return componentWeights.getOrDefault(compId, 1);
    }

    public List<List<Integer>> getSCCs() {
        return sccs;
    }

    public List<Integer> getComponentVertices(int compId) {
        return sccs.get(compId);
    }

    public boolean isDAG() {
        int n = condensation.getN();
        boolean[] visited = new boolean[n];
        boolean[] recStack = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (hasCycleDFS(i, visited, recStack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasCycleDFS(int u, boolean[] visited, boolean[] recStack) {
        visited[u] = true;
        recStack[u] = true;

        for (Edge edge : condensation.getAdjacent(u)) {
            int v = edge.getTo();
            if (!visited[v]) {
                if (hasCycleDFS(v, visited, recStack)) {
                    return true;
                }
            } else if (recStack[v]) {
                return true;
            }
        }

        recStack[u] = false;
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Condensation Graph:\n");
        sb.append(String.format("Components: %d\n", sccs.size()));
        sb.append(String.format("Edges: %d\n", condensation.getEdgeCount()));
        sb.append(String.format("Is DAG: %b\n", isDAG()));
        return sb.toString();
    }
}