import graph.common.*;
import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

public class Main {

    private static final List<ResultRow> results = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SMART CITY SCHEDULING - GRAPH ALGORITHMS");
        System.out.println("Assignment 4: SCC, Topological Sort, DAG Shortest Paths");
        System.out.println("=".repeat(80) + "\n");

        if (args.length > 0) {
            processFile(args[0]);
        } else {
            processAllDatasets("data");
        }

        if (!results.isEmpty()) {
            printSummaryTable();
            exportToCSV("results/results.csv");
        }
    }

    public static void processFile(String filepath) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Processing: " + filepath);
        System.out.println("=".repeat(80));

        try {
            GraphLoader.GraphData data = GraphLoader.loadFromFile(filepath);
            Graph graph = data.getGraph();
            int source = data.getSource();

            String datasetName = new File(filepath).getName().replace(".json", "");
            int nodes = graph.getN();
            int edges = graph.getEdgeCount();

            System.out.println(graph);

            System.out.println("\n--- STEP 1: Strongly Connected Components ---");
            TarjanSCC tarjan = new TarjanSCC(graph);
            List<List<Integer>> sccs = tarjan.findSCCs();
            tarjan.printSCCs();
            System.out.println(tarjan.getMetrics());

            System.out.println("\n--- STEP 2: Condensation Graph ---");
            int[] componentIds = tarjan.getComponentIds();
            CondensationGraph condensation = new CondensationGraph(graph, sccs, componentIds);
            Graph condGraph = condensation.buildCondensation();
            System.out.println(condensation);
            System.out.println(condGraph);

            System.out.println("\n--- STEP 3: Topological Sort ---");
            KahnSort kahn = new KahnSort(condGraph);
            List<Integer> topoOrder = kahn.sort();

            long sccTime = tarjan.getMetrics().getElapsedNanos();
            long topoTime = 0;
            long shortestTime = 0;
            long longestTime = 0;
            long dfsVisits = tarjan.getMetrics().getDfsVisits();
            long edgesTraversed = tarjan.getMetrics().getEdgesTraversed();
            long pushOps = 0;
            long popOps = 0;
            long relaxations = 0;
            int criticalPathLength = 0;

            if (topoOrder != null) {
                System.out.println("Topological order of components: " + topoOrder);

                List<Integer> expandedOrder = kahn.sortWithMapping(sccs);
                System.out.println("Topological order of original tasks: " + expandedOrder);
                System.out.println(kahn.getMetrics());

                topoTime = kahn.getMetrics().getElapsedNanos();
                pushOps = kahn.getMetrics().getPushOperations();
                popOps = kahn.getMetrics().getPopOperations();

                System.out.println("\n--- STEP 4: Shortest Paths in DAG ---");
                int condensedSource = componentIds[source];

                DAGShortestPath shortestPath = new DAGShortestPath(condGraph);
                shortestPath.findShortestPaths(condensedSource);
                shortestPath.printResults(condensedSource);
                System.out.println(shortestPath.getMetrics());

                shortestTime = shortestPath.getMetrics().getElapsedNanos();

                System.out.println("\n--- STEP 5: Longest Path (Critical Path) ---");
                DAGLongestPath longestPath = new DAGLongestPath(condGraph);
                DAGLongestPath.CriticalPathResult criticalPath =
                        longestPath.findCriticalPath(condensedSource);
                System.out.println(criticalPath);
                longestPath.printResults(condensedSource);
                System.out.println(longestPath.getMetrics());

                longestTime = longestPath.getMetrics().getElapsedNanos();
                relaxations = shortestPath.getMetrics().getRelaxations() +
                        longestPath.getMetrics().getRelaxations();
                criticalPathLength = criticalPath.getLength();

            } else {
                System.out.println("ERROR: Condensation graph contains a cycle! This should not happen.");
            }

            results.add(new ResultRow(datasetName, nodes, edges, sccs.size(),
                    sccTime, topoTime, shortestTime, longestTime,
                    dfsVisits, edgesTraversed, pushOps, popOps, relaxations,
                    criticalPathLength));

        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing graph: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n");
    }

    public static void processAllDatasets(String dataDir) {
        String[] categories = {"small", "medium", "large"};

        for (String category : categories) {
            String categoryPath = dataDir + "/" + category;
            File dir = new File(categoryPath);

            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
                if (files != null) {
                    for (File file : files) {
                        processFile(file.getPath());
                    }
                }
            } else {
                System.err.println("Warning: Directory not found: " + categoryPath);
            }
        }
    }

    private static void printSummaryTable() {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("SUMMARY RESULTS TABLE");
        System.out.println("=".repeat(120));

        System.out.printf("%-15s | %5s | %6s | %5s | %10s | %10s | %10s | %10s | %8s | %10s\n",
                "Dataset", "Nodes", "Edges", "SCCs", "SCC(μs)", "Topo(μs)", "Short(μs)", "Long(μs)", "DFSVisit", "Relax");
        System.out.println("-".repeat(120));

        for (ResultRow row : results) {
            System.out.printf("%-15s | %5d | %6d | %5d | %10.2f | %10.2f | %10.2f | %10.2f | %8d | %10d\n",
                    row.dataset, row.nodes, row.edges, row.sccs,
                    row.sccTime / 1000.0, row.topoTime / 1000.0,
                    row.shortestTime / 1000.0, row.longestTime / 1000.0,
                    row.dfsVisits, row.relaxations);
        }

        System.out.println("=".repeat(120));

        double avgSCC = results.stream().mapToLong(r -> r.sccTime).average().orElse(0) / 1000.0;
        double avgTopo = results.stream().mapToLong(r -> r.topoTime).average().orElse(0) / 1000.0;
        double avgShortest = results.stream().mapToLong(r -> r.shortestTime).average().orElse(0) / 1000.0;
        double avgLongest = results.stream().mapToLong(r -> r.longestTime).average().orElse(0) / 1000.0;

        System.out.printf("\nAVERAGES: SCC=%.2fμs, Topo=%.2fμs, Shortest=%.2fμs, Longest=%.2fμs\n",
                avgSCC, avgTopo, avgShortest, avgLongest);
        System.out.println();
    }

    private static void exportToCSV(String filepath) {
        try {
            File resultsDir = new File("results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            File csvFile = new File(filepath);
            if (csvFile.exists()) {
                csvFile.delete();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
                writer.println("Dataset,Nodes,Edges,SCCs,SCC_Time_ns,Topo_Time_ns,Shortest_Time_ns," +
                        "Longest_Time_ns,DFS_Visits,Edges_Traversed,Push_Ops,Pop_Ops,Relaxations," +
                        "Critical_Path_Length");

                for (ResultRow row : results) {
                    writer.printf("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n",
                            row.dataset, row.nodes, row.edges, row.sccs,
                            row.sccTime, row.topoTime, row.shortestTime, row.longestTime,
                            row.dfsVisits, row.edgesTraversed, row.pushOps, row.popOps,
                            row.relaxations, row.criticalPathLength);
                }
            }

            System.out.println("✓ Results exported to: " + filepath);

        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    private static class ResultRow {
        String dataset;
        int nodes;
        int edges;
        int sccs;
        long sccTime;
        long topoTime;
        long shortestTime;
        long longestTime;
        long dfsVisits;
        long edgesTraversed;
        long pushOps;
        long popOps;
        long relaxations;
        int criticalPathLength;

        ResultRow(String dataset, int nodes, int edges, int sccs,
                  long sccTime, long topoTime, long shortestTime, long longestTime,
                  long dfsVisits, long edgesTraversed, long pushOps, long popOps,
                  long relaxations, int criticalPathLength) {
            this.dataset = dataset;
            this.nodes = nodes;
            this.edges = edges;
            this.sccs = sccs;
            this.sccTime = sccTime;
            this.topoTime = topoTime;
            this.shortestTime = shortestTime;
            this.longestTime = longestTime;
            this.dfsVisits = dfsVisits;
            this.edgesTraversed = edgesTraversed;
            this.pushOps = pushOps;
            this.popOps = popOps;
            this.relaxations = relaxations;
            this.criticalPathLength = criticalPathLength;
        }
    }
}