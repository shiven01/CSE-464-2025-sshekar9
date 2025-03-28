package com.shivenshekar.graphparser;

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
     * 
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

                        edges.add(new String[] { source, target });

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
                else if (!line.contains("=") && !line.contains("subgraph") && !line.contains("graph")
                        && !line.contains("digraph")) {
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
     * 
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
     * 
     * @param labels Array of node labels to add
     */
    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    /**
     * Add an edge from source node to destination node
     * 
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
     * 
     * @return Number of nodes
     */
    public int getNodeCount() {
        return graph.vertexSet().size();
    }

    /**
     * Get the number of edges in the graph
     * 
     * @return Number of edges
     */
    public int getEdgeCount() {
        return graph.edgeSet().size();
    }

    /**
     * Get all node labels in the graph
     * 
     * @return Set of node labels
     */
    public Set<String> getNodes() {
        return graph.vertexSet();
    }

    /**
     * Get a string representation of all edges in the graph
     * 
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
     * 
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
     * 
     * @param path Path to save the DOT file
     * @throws IOException If file couldn't be written
     */
    public void outputDOTGraph(String path) throws IOException {
        // Using existing output method
        outputGraph(path);
    }

    /**
     * Output the graph to a graphics file
     * 
     * @param path   Path to save the graphics file
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

    /**
     * Remove a node from the graph
     * 
     * @param label Label of the node to remove
     * @return true if node was removed
     * @throws IllegalArgumentException if the node doesn't exist or has edges
     *                                  connected to it
     */
    public boolean removeNode(String label) {
        // Check if node exists
        if (!graph.containsVertex(label)) {
            throw new IllegalArgumentException("Node doesn't exist: " + label);
        }

        // Check if node has any edges
        if (graph.inDegreeOf(label) > 0 || graph.outDegreeOf(label) > 0) {
            throw new IllegalArgumentException("Cannot remove node with connected edges: " + label);
        }

        // Remove node
        return graph.removeVertex(label);
    }

    /**
     * Remove multiple nodes from the graph
     * 
     * @param labels Array of node labels to remove
     * @throws IllegalArgumentException if any node doesn't exist or has edges
     *                                  connected to it
     */
    public void removeNodes(String[] labels) {
        for (String label : labels) {
            removeNode(label);
        }
    }

    /**
     * Remove an edge from the graph
     * 
     * @param srcLabel Label of the source node
     * @param dstLabel Label of the destination node
     * @return true if edge was removed
     * @throws IllegalArgumentException if either node doesn't exist or the edge
     *                                  doesn't exist
     */
    public boolean removeEdge(String srcLabel, String dstLabel) {
        // Check if nodes exist
        if (!graph.containsVertex(srcLabel)) {
            throw new IllegalArgumentException("Source node doesn't exist: " + srcLabel);
        }
        if (!graph.containsVertex(dstLabel)) {
            throw new IllegalArgumentException("Destination node doesn't exist: " + dstLabel);
        }

        // Check if edge exists
        if (!graph.containsEdge(srcLabel, dstLabel)) {
            throw new IllegalArgumentException("Edge doesn't exist: " + srcLabel + " -> " + dstLabel);
        }

        // Remove edge
        DefaultEdge edge = graph.getEdge(srcLabel, dstLabel);
        return graph.removeEdge(edge);
    }

    /**
 * Search for a path from source node to destination node
 * @param srcLabel Label of the source node
 * @param dstLabel Label of the destination node
 * @param algo The algorithm to use (BFS or DFS)
 * @return A Path object if a path exists, null otherwise
 * @throws IllegalArgumentException if either node doesn't exist or the algorithm is not supported
 */
public com.shivenshekar.graphparser.Path graphSearch(String srcLabel, String dstLabel, Algorithm algo) {
    // Check if nodes exist
    if (!graph.containsVertex(srcLabel)) {
        throw new IllegalArgumentException("Source node doesn't exist: " + srcLabel);
    }
    if (!graph.containsVertex(dstLabel)) {
        throw new IllegalArgumentException("Destination node doesn't exist: " + dstLabel);
    }
    
    // If source and destination are the same, return a path with just that node
    if (srcLabel.equals(dstLabel)) {
        com.shivenshekar.graphparser.Path path = new com.shivenshekar.graphparser.Path();
        path.addNode(srcLabel);
        return path;
    }
    
    // Choose algorithm based on parameter
    switch (algo) {
        case BFS:
            return bfsSearch(srcLabel, dstLabel);
        case DFS:
            return dfsSearch(srcLabel, dstLabel);
        default:
            throw new IllegalArgumentException("Unsupported algorithm: " + algo);
    }
}

/**
 * Search for a path from source node to destination node using default algorithm (BFS)
 * @param srcLabel Label of the source node
 * @param dstLabel Label of the destination node
 * @return A Path object if a path exists, null otherwise
 * @throws IllegalArgumentException if either node doesn't exist
 */
public com.shivenshekar.graphparser.Path graphSearch(String srcLabel, String dstLabel) {
    // Call the three-parameter version with BFS as the default algorithm
    return graphSearch(srcLabel, dstLabel, Algorithm.BFS);
}

/**
 * Perform a Breadth-First Search for a path
 * @param srcLabel Label of the source node
 * @param dstLabel Label of the destination node
 * @return A Path object if a path exists, null otherwise
 */
private com.shivenshekar.graphparser.Path bfsSearch(String srcLabel, String dstLabel) {
    // Set to track visited nodes
    java.util.Set<String> visited = new java.util.HashSet<>();
    // Queue to track nodes to visit
    java.util.Queue<com.shivenshekar.graphparser.Path> queue = new java.util.LinkedList<>();
    
    // Initialize path with source node
    com.shivenshekar.graphparser.Path initialPath = new com.shivenshekar.graphparser.Path();
    initialPath.addNode(srcLabel);
    queue.add(initialPath);
    visited.add(srcLabel);
    
    // BFS traversal
    while (!queue.isEmpty()) {
        com.shivenshekar.graphparser.Path currentPath = queue.poll();
        String currentNode = currentPath.getEndNode();
        
        // Get neighbors (outgoing edges)
        for (DefaultEdge edge : graph.outgoingEdgesOf(currentNode)) {
            String neighbor = graph.getEdgeTarget(edge);
            
            // If we found the destination, return the path
            if (neighbor.equals(dstLabel)) {
                com.shivenshekar.graphparser.Path foundPath = currentPath.copy();
                foundPath.addNode(neighbor);
                return foundPath;
            }
            
            // If neighbor hasn't been visited, add it to the queue
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                com.shivenshekar.graphparser.Path newPath = currentPath.copy();
                newPath.addNode(neighbor);
                queue.add(newPath);
            }
        }
    }
    
    // No path found
    return null;
}

/**
 * Perform a Depth-First Search for a path
 * @param srcLabel Label of the source node
 * @param dstLabel Label of the destination node
 * @return A Path object if a path exists, null otherwise
 */
private com.shivenshekar.graphparser.Path dfsSearch(String srcLabel, String dstLabel) {
    // Set to track visited nodes
    java.util.Set<String> visited = new java.util.HashSet<>();
    
    // Start DFS from source node
    com.shivenshekar.graphparser.Path path = new com.shivenshekar.graphparser.Path();
    path.addNode(srcLabel);
    
    if (dfsRecursive(srcLabel, dstLabel, visited, path)) {
        return path;
    } else {
        return null;
    }
}

/**
 * Recursive helper method for DFS
 * @param currentNode Current node being visited
 * @param dstLabel Destination node we're looking for
 * @param visited Set of visited nodes
 * @param path Current path being explored
 * @return true if a path to destination is found, false otherwise
 */
private boolean dfsRecursive(String currentNode, String dstLabel, java.util.Set<String> visited, com.shivenshekar.graphparser.Path path) {
    // Mark current node as visited
    visited.add(currentNode);
    
    // If we reached the destination, return true
    if (currentNode.equals(dstLabel)) {
        return true;
    }
    
    // Visit all neighbors
    for (DefaultEdge edge : graph.outgoingEdgesOf(currentNode)) {
        String neighbor = graph.getEdgeTarget(edge);
        
        if (!visited.contains(neighbor)) {
            // Add neighbor to path
            path.addNode(neighbor);
            
            // Recursively visit neighbor
            if (dfsRecursive(neighbor, dstLabel, visited, path)) {
                return true;
            }
            
            // If this path doesn't lead to destination, remove the neighbor from path (backtrack)
            java.util.List<String> nodes = new java.util.ArrayList<>(path.getNodes());
            nodes.remove(nodes.size() - 1);
            path = new com.shivenshekar.graphparser.Path(nodes);
        }
    }
    
    // No path found from this node
    return false;
}
}