package com.shivenshekar.graphparser;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    private DefaultDirectedGraph<String, DefaultEdge> graph;
    private Map<String, String> nodeLabels;

    public Graph() {
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    /**
     * Parse a DOT graph file and create a graph
     * @param filepath Path to the DOT file
     * @return Graph instance containing the parsed graph
     * @throws IOException If file not found or couldn't be read
     */
    public static Graph parseGraph(String filepath) throws IOException {
        Graph graph = new Graph();

        Path path = Paths.get(filepath);
        List<String> lines = Files.readAllLines(path);

        boolean inGraph = false;
        List<String> nodes = new ArrayList<>();
        List<String[]> edges = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();

            // Skipping comments and empty lines
            if (line.startsWith("//") || line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            // Checking if entering graph
            if (line.contains("{")) {
                inGraph = true;
                continue;
            }

            // Checking if exiting graph
            if (line.contains("}")) {
                inGraph = false;
                continue;
            }

            // Processing graph
            if (inGraph) {
                // Handling edge definitions
                if (line.contains("->") || line.contains("--")) {
                    String[] parts;
                    if (line.contains("->")) {
                        parts = line.split("->");
                    } else {
                        parts = line.split("--");
                    }

                    if (parts.length >= 2) {
                        String source = parts[0].trim();
                        String target = parts[1].trim();

                        // Removing attributes
                        if (source.contains("[")) {
                            source = source.substring(0, source.indexOf("[")).trim();
                        }
                        if (target.contains("[")) {
                            target = target.substring(0, target.indexOf("[")).trim();
                        }

                        // Removing semicolons
                        if (target.endsWith(";")) {
                            target = target.substring(0, target.length() - 1).trim();
                        }

                        edges.add(new String[]{source, target});

                        // Adding nodes
                        if (!nodes.contains(source)) {
                            nodes.add(source);
                        }
                        if (!nodes.contains(target)) {
                            nodes.add(target);
                        }
                    }
                }
                // Handle node definitions
                else if (!line.contains("=") && !line.contains("subgraph") && !line.contains("graph") && !line.contains("digraph")) {
                    String node = line;

                    // Removing attributes
                    if (node.contains("[")) {
                        node = node.substring(0, node.indexOf("[")).trim();
                    }

                    // Removing semicolons
                    if (node.endsWith(";")) {
                        node = node.substring(0, node.length() - 1).trim();
                    }

                    if (!node.isEmpty() && !nodes.contains(node)) {
                        nodes.add(node);
                    }
                }
            }
        }

        // Adding all nodes to graph
        for (String node : nodes) {
            graph.graph.addVertex(node);
        }

        // Adding all edges to graph
        for (String[] edge : edges) {
            graph.graph.addEdge(edge[0], edge[1]);
        }

        return graph;
    }

    /**
     * Add a node to the graph
     * @param label Label of the node to add
     * @return true if node was added, false if it already existed
     */
    public boolean addNode(String label) {
        // Checking if node already exists
        if (graph.containsVertex(label)) {
            return false;
        }

        // Adding node to graph
        return graph.addVertex(label);
    }

    /**
     * Add multiple nodes to the graph
     * @param labels Array of node labels to add
     */
    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    /**
     * Get the number of nodes in the graph
     * @return Number of nodes
     */
    public int getNodeCount() {
        return graph.vertexSet().size();
    }

    /**
     * Get the number of edges in the graph
     * @return Number of edges
     */
    public int getEdgeCount() {
        return graph.edgeSet().size();
    }

    /**
     * Get all node labels in the graph
     * @return Set of node labels
     */
    public Set<String> getNodes() {
        return graph.vertexSet();
    }

    /**
     * Get a string representation of all edges in the graph
     * @return List of edge strings (e.g., "a -> b")
     */
    public List<String> getEdges() {
        List<String> edgeStrings = new ArrayList<>();

        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            edgeStrings.add(source + " -> " + target);
        }

        return edgeStrings;
    }

    /**
     * Output the graph to a DOT format file
     * @param filepath Path to save the DOT file
     * @throws IOException If file couldn't be written
     */
    public void outputGraph(String filepath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write("digraph G {");
            writer.newLine();

            // Node definitions
            for (String node : graph.vertexSet()) {
                writer.write("    " + node + ";");
                writer.newLine();
            }

            // Edge definitions
            for (DefaultEdge edge : graph.edgeSet()) {
                String source = graph.getEdgeSource(edge);
                String target = graph.getEdgeTarget(edge);
                writer.write("    " + source + " -> " + target + ";");
                writer.newLine();
            }

            writer.write("}");
        }
    }

    /**
     * Return a string representation of the graph
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Graph:\n");
        sb.append("Number of nodes: ").append(getNodeCount()).append("\n");
        sb.append("Nodes: ").append(getNodes()).append("\n");
        sb.append("Number of edges: ").append(getEdgeCount()).append("\n");
        sb.append("Edges:\n");

        for (String edge : getEdges()) {
            sb.append("  ").append(edge).append("\n");
        }

        return sb.toString();
    }
}