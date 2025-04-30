package com.shivenshekar.graphparser.parser;

import com.shivenshekar.graphparser.core.Graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DOTParser {
    /**
     * Parse a DOT graph file and create a graph
     *
     * @param filepath Path to the DOT file
     * @return Graph instance containing the parsed graph
     * @throws IOException If file not found or couldn't be read
     */
    public static Graph parseGraph(String filepath) throws IOException {
        Graph graph = new Graph();

        java.nio.file.Path path = Paths.get(filepath);
        List<String> lines = Files.readAllLines(path);

        // Parse the DOT file content
        GraphContent content = parseDotContent(lines);

        // Add nodes to the graph
        for (String node : content.nodes) {
            graph.addNode(node);
        }

        // Add edges to the graph
        for (String[] edge : content.edges) {
            graph.addEdge(edge[0], edge[1]);
        }

        return graph;
    }

    /**
     * Parse DOT file content into graph components
     *
     * @param lines Lines from the DOT file
     * @return GraphContent containing nodes and edges
     */
    private static GraphContent parseDotContent(List<String> lines) {
        boolean inGraph = false;
        List<String> nodes = new ArrayList<>();
        List<String[]> edges = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();

            // Skip comments and empty lines
            if (line.startsWith("//") || line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            // Determine if we're entering or exiting the graph definition
            if (line.contains("{")) {
                inGraph = true;
                continue;
            } else if (line.contains("}")) {
                inGraph = false;
                continue;
            }

            // Process content if we're inside the graph definition
            if (inGraph) {
                if (line.contains("->") || line.contains("--")) {
                    // Process edge definition
                    String[] edgeData = parseEdge(line);
                    if (edgeData != null) {
                        edges.add(edgeData);

                        // Add nodes from the edge if they don't exist
                        if (!nodes.contains(edgeData[0])) {
                            nodes.add(edgeData[0]);
                        }
                        if (!nodes.contains(edgeData[1])) {
                            nodes.add(edgeData[1]);
                        }
                    }
                } else if (!isGraphMetadata(line)) {
                    // Process node definition
                    String node = parseNode(line);
                    if (node != null && !node.isEmpty() && !nodes.contains(node)) {
                        nodes.add(node);
                    }
                }
            }
        }

        return new GraphContent(nodes, edges);
    }

    /**
     * Parse an edge definition line
     *
     * @param line Line containing edge definition
     * @return Array with [source, target] or null if invalid
     */
    private static String[] parseEdge(String line) {
        String[] parts;
        if (line.contains("->")) {
            parts = line.split("->");
        } else {
            parts = line.split("--");
        }

        if (parts.length >= 2) {
            String source = parts[0].trim();
            String target = parts[1].trim();

            // Remove attributes
            if (source.contains("[")) {
                source = source.substring(0, source.indexOf("[")).trim();
            }
            if (target.contains("[")) {
                target = target.substring(0, target.indexOf("[")).trim();
            }

            // Remove semicolons
            if (target.endsWith(";")) {
                target = target.substring(0, target.length() - 1).trim();
            }

            return new String[] { source, target };
        }

        return null;
    }

    /**
     * Parse a node definition line
     *
     * @param line Line containing node definition
     * @return Node label or null if invalid
     */
    private static String parseNode(String line) {
        String node = line;

        // Remove attributes
        if (node.contains("[")) {
            node = node.substring(0, node.indexOf("[")).trim();
        }

        // Remove semicolons
        if (node.endsWith(";")) {
            node = node.substring(0, node.length() - 1).trim();
        }

        return node;
    }

    /**
     * Check if a line contains graph metadata rather than node/edge definitions
     *
     * @param line Line to check
     * @return true if the line contains graph metadata
     */
    private static boolean isGraphMetadata(String line) {
        return line.contains("=") ||
                line.contains("subgraph") ||
                line.contains("graph") ||
                line.contains("digraph");
    }

    /**
     * Inner class to hold parsed graph content
     */
    public static class GraphContent {
        public final List<String> nodes;
        public final List<String[]> edges;

        public GraphContent(List<String> nodes, List<String[]> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }
}