package com.shivenshekar.graphparser;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.File;
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

        // Adding nodes
        for (String node : nodes) {
            graph.graph.addVertex(node);
        }

        // Adding edges
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
        // Checking if node exists
        if (graph.containsVertex(label)) {
            return false;
        }

        // Adding node
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
     * Add an edge from source node to destination node
     * @param srcLabel Label of the source node
     * @param dstLabel Label of the destination node
     * @return true if edge was added, false if it already existed
     */
    public boolean addEdge(String srcLabel, String dstLabel) {
        // Adding nodes
        addNode(srcLabel);
        addNode(dstLabel);

        // Checking edges
        if (graph.containsEdge(srcLabel, dstLabel)) {
            return false;
        }

        // Adding edge
        graph.addEdge(srcLabel, dstLabel);
        return true;
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

            // Writing nodes
            for (String node : graph.vertexSet()) {
                writer.write("    " + node + ";");
                writer.newLine();
            }

            // Writing edges
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
     * Output the graph to a DOT format file
     * @param path Path to save the DOT file
     * @throws IOException If file couldn't be written
     */
    public void outputDOTGraph(String path) throws IOException {
        // Using existing output method
        outputGraph(path);
    }

    /**
     * Output the graph to a graphics file
     * @param path Path to save the graphics file
     * @param format Format of the output file (png supported)
     * @throws IOException If file couldn't be written
     */
    public void outputGraphics(String path, String format) throws IOException {
        // Checking format support
        format = format.toLowerCase();
        if (!format.equals("png")) {
            throw new IllegalArgumentException("Unsupported format: " + format + ". Only 'png' is supported.");
        }

        // Creating temporary DOT file
        Path tempFile = Files.createTempFile("graph_", ".dot");
        outputDOTGraph(tempFile.toString());

        try {
            // Reading DOT content
            String dotContent = Files.readString(tempFile);

            // Parsing and rendering
            MutableGraph g = new Parser().read(dotContent);
            Graphviz.fromGraph(g)
                    .width(800)
                    .render(Format.PNG)
                    .toFile(new File(path));
        } finally {
            Files.deleteIfExists(tempFile);
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