package util;

import graph.common.Graph;
import java.io.IOException;
import java.util.Random;

public class DatasetGenerator {
    private final Random random;

    public DatasetGenerator(long seed) {
        this.random = new Random(seed);
    }

    public Graph generateGraph(int n, double density, int minWeight, int maxWeight, boolean forceCycles) {
        Graph graph = new Graph(n, true);

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && random.nextDouble() < density) {
                    int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        if (forceCycles && n >= 3) {
            int cycleSize = Math.min(3 + random.nextInt(3), n);
            for (int i = 0; i < cycleSize - 1; i++) {
                int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                graph.addEdge(i, i + 1, weight);
            }
            int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
            graph.addEdge(cycleSize - 1, 0, weight);
        }

        return graph;
    }

    public Graph generateDAG(int n, double density, int minWeight, int maxWeight) {
        Graph graph = new Graph(n, true);

        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (random.nextDouble() < density) {
                    int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        return graph;
    }

    public Graph generateMultipleSCCs(int numSCCs, int nodesPerSCC, double densityIntra, double densityInter) {
        int totalNodes = numSCCs * nodesPerSCC;
        Graph graph = new Graph(totalNodes, true);

        for (int scc = 0; scc < numSCCs; scc++) {
            int start = scc * nodesPerSCC;
            int end = start + nodesPerSCC;

            for (int u = start; u < end; u++) {
                for (int v = start; v < end; v++) {
                    if (u != v && random.nextDouble() < densityIntra) {
                        int weight = 1 + random.nextInt(10);
                        graph.addEdge(u, v, weight);
                    }
                }
            }

            if (nodesPerSCC >= 2) {
                for (int i = 0; i < nodesPerSCC - 1; i++) {
                    graph.addEdge(start + i, start + i + 1, 1);
                }
                graph.addEdge(end - 1, start, 1);
            }
        }

        for (int scc1 = 0; scc1 < numSCCs - 1; scc1++) {
            for (int scc2 = scc1 + 1; scc2 < numSCCs; scc2++) {
                if (random.nextDouble() < densityInter) {
                    int u = scc1 * nodesPerSCC + random.nextInt(nodesPerSCC);
                    int v = scc2 * nodesPerSCC + random.nextInt(nodesPerSCC);
                    int weight = 1 + random.nextInt(10);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        return graph;
    }

    public void generateAllDatasets(String outputDir) throws IOException {
        System.out.println("Generating datasets...\n");

        System.out.println("Small datasets:");
        generateAndSave(outputDir + "/small/small_1.json",
                generateGraph(6, 0.3, 1, 5, true), 0, "6 nodes, sparse, with cycles");
        generateAndSave(outputDir + "/small/small_2.json",
                generateDAG(8, 0.4, 1, 10), 0, "8 nodes, pure DAG");
        generateAndSave(outputDir + "/small/small_3.json",
                generateGraph(10, 0.25, 1, 8, true), 0, "10 nodes, sparse, with cycles");

        System.out.println("\nMedium datasets:");
        generateAndSave(outputDir + "/medium/medium_1.json",
                generateMultipleSCCs(3, 4, 0.5, 0.2), 0, "12 nodes, 3 SCCs of size 4");
        generateAndSave(outputDir + "/medium/medium_2.json",
                generateGraph(15, 0.2, 1, 15, true), 0, "15 nodes, mixed structure");
        generateAndSave(outputDir + "/medium/medium_3.json",
                generateDAG(18, 0.3, 1, 12), 0, "18 nodes, dense DAG");

        System.out.println("\nLarge datasets:");
        generateAndSave(outputDir + "/large/large_1.json",
                generateMultipleSCCs(5, 5, 0.4, 0.15), 0, "25 nodes, 5 SCCs of size 5");
        generateAndSave(outputDir + "/large/large_2.json",
                generateGraph(35, 0.15, 1, 20, true), 0, "35 nodes, sparse with cycles");
        generateAndSave(outputDir + "/large/large_3.json",
                generateDAG(45, 0.2, 1, 25), 0, "45 nodes, large DAG");

        System.out.println("\nAll datasets generated successfully!");
    }

    private void generateAndSave(String filepath, Graph graph, int source, String description) throws IOException {
        GraphLoader.saveToFile(graph, source, filepath);
        System.out.printf("  ✓ %s - %s (edges: %d)\n", filepath, description, graph.getEdgeCount());
    }

    public static void main(String[] args) throws IOException {
        DatasetGenerator generator = new DatasetGenerator(42);
        generator.generateAllDatasets("data");
        System.out.println("All datasets generated successfully!");
    }
}