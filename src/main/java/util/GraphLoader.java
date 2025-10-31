package util;

import graph.common.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphLoader {

    public static GraphData loadFromFile(String filepath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        return loadFromJson(content);
    }

    public static GraphData loadFromJson(String jsonString) {
        boolean directed = parseBoolean(jsonString, "directed");
        int n = parseInt(jsonString, "n");
        int source = parseInt(jsonString, "source", 0);
        String weightModel = parseString(jsonString, "weight_model", "edge");

        Graph graph = new Graph(n, directed);

        Pattern edgesPattern = Pattern.compile("\"edges\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher edgesMatcher = edgesPattern.matcher(jsonString);

        if (edgesMatcher.find()) {
            String edgesStr = edgesMatcher.group(1);
            Pattern edgePattern = Pattern.compile("\\{[^}]+\\}");
            Matcher edgeMatcher = edgePattern.matcher(edgesStr);

            while (edgeMatcher.find()) {
                String edgeStr = edgeMatcher.group();
                int u = parseInt(edgeStr, "u");
                int v = parseInt(edgeStr, "v");
                int w = parseInt(edgeStr, "w", 1);

                graph.addEdge(u, v, w);
            }
        }

        return new GraphData(graph, source, weightModel);
    }

    private static boolean parseBoolean(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false;
    }

    private static int parseInt(String json, String key) {
        return parseInt(json, key, 0);
    }

    private static int parseInt(String json, String key, int defaultValue) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return defaultValue;
    }

    private static String parseString(String json, String key, String defaultValue) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return defaultValue;
    }

    public static class GraphData {
        private final Graph graph;
        private final int source;
        private final String weightModel;

        public GraphData(Graph graph, int source, String weightModel) {
            this.graph = graph;
            this.source = source;
            this.weightModel = weightModel;
        }

        public Graph getGraph() {
            return graph;
        }

        public int getSource() {
            return source;
        }

        public String getWeightModel() {
            return weightModel;
        }
    }

    public static void saveToFile(Graph graph, int source, String filepath) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"directed\": ").append(graph.isDirected()).append(",\n");
        json.append("  \"n\": ").append(graph.getN()).append(",\n");
        json.append("  \"edges\": [\n");

        boolean first = true;
        for (int u = 0; u < graph.getN(); u++) {
            for (Edge edge : graph.getAdjacent(u)) {
                if (!first) {
                    json.append(",\n");
                }
                json.append("    {\"u\": ").append(edge.getFrom());
                json.append(", \"v\": ").append(edge.getTo());
                json.append(", \"w\": ").append(edge.getWeight()).append("}");
                first = false;
            }
        }

        json.append("\n  ],\n");
        json.append("  \"source\": ").append(source).append(",\n");
        json.append("  \"weight_model\": \"edge\"\n");
        json.append("}\n");

        Files.write(Paths.get(filepath), json.toString().getBytes());
    }
}