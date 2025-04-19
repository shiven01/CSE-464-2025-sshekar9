package com.shivenshekar.graphparser;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import com.shivenshekar.graphparser.search.SearchContext;
import com.shivenshekar.graphparser.search.SearchStrategy;

public class Graph {

  private DefaultDirectedGraph<String, DefaultEdge> graph;
  private final SearchContext searchContext = new SearchContext();
  private Map<String, String> nodeLabels;

  public Graph() {
    this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    this.nodeLabels = new HashMap<>();
  }

  /**
   * Get the internal JGraphT graph (for testing purposes)
   *
   * @return The internal graph representation
   */
  DefaultDirectedGraph<String, DefaultEdge> getInternalGraph() {
    return this.graph;
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

    java.nio.file.Path path = Paths.get(filepath);
    List<String> lines = Files.readAllLines(path);

    // Parse the DOT file content
    GraphContent content = parseDotContent(lines);

    // Add nodes to the graph
    for (String node : content.nodes) {
      graph.graph.addVertex(node);
    }

    // Add edges to the graph
    for (String[] edge : content.edges) {
      graph.graph.addEdge(edge[0], edge[1]);
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
  private static class GraphContent {
    final List<String> nodes;
    final List<String[]> edges;

    GraphContent(List<String> nodes, List<String[]> edges) {
      this.nodes = nodes;
      this.edges = edges;
    }
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
   * Add an edge to the graph
   *
   * @param sourceLabel Source node label
   * @param targetLabel Target node label
   * @return true if edge was added, false if it already existed
   */
  public boolean addEdge(String sourceLabel, String targetLabel) {
    // Add nodes if they don't exist
    addNode(sourceLabel);
    addNode(targetLabel);

    // Check if edge already exists
    if (graph.containsEdge(sourceLabel, targetLabel)) {
      return false;
    }

    // Add the edge
    graph.addEdge(sourceLabel, targetLabel);
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
   * Get outgoing edges for a node
   *
   * @param nodeLabel Label of the node
   * @return Set of outgoing edges
   */
  public Set<DefaultEdge> getOutgoingEdges(String nodeLabel) {
    return graph.outgoingEdgesOf(nodeLabel);
  }

  /**
   * Get the target node of an edge
   *
   * @param edge Edge to get target for
   * @return Target node label
   */
  public String getEdgeTarget(DefaultEdge edge) {
    return graph.getEdgeTarget(edge);
  }

  /**
   * Get a list of all edges in the graph as strings
   *
   * @return List of edge strings (e.g., "A -> B")
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
   * Output the graph to a DOT file
   *
   * @param filepath Path to output file
   * @throws IOException If file couldn't be written
   */
  public void outputDOTGraph(String filepath) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
      writer.write("digraph G {\n");

      // Write all nodes
      for (String node : graph.vertexSet()) {
        writer.write("    " + node + ";\n");
      }

      // Write all edges
      for (DefaultEdge edge : graph.edgeSet()) {
        String source = graph.getEdgeSource(edge);
        String target = graph.getEdgeTarget(edge);
        writer.write("    " + source + " -> " + target + ";\n");
      }

      writer.write("}");
    }
  }

  /**
   * Legacy method for compatibility with tests
   * @param outputPath Path to output file
   * @throws IOException If file couldn't be written
   */
  public void outputGraph(String outputPath) throws IOException {
    outputDOTGraph(outputPath);
  }

  /**
   * Generate a graphical representation of the graph
   *
   * @param outputPath Path to output file
   * @param format Format to use (png, svg)
   * @throws IOException If file couldn't be written
   */
  public void outputGraphics(String outputPath, String format) throws IOException {
    // Create temporary DOT file
    File tempFile = File.createTempFile("graph_", ".dot");
    outputDOTGraph(tempFile.getAbsolutePath());

    // Determine the output format
    Format outputFormat;
    switch (format.toLowerCase()) {
      case "png":
        outputFormat = Format.PNG;
        break;
      case "svg":
        outputFormat = Format.SVG;
        break;
      default:
        throw new IllegalArgumentException("Unsupported format: " + format);
    }

    // Generate the graphical output
    try {
      MutableGraph g = new Parser().read(tempFile);
      Graphviz.fromGraph(g).width(700).render(outputFormat).toFile(new File(outputPath));
    } finally {
      // Clean up temporary file
      tempFile.delete();
    }
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
   * Search for a path between two nodes using the specified algorithm
   *
   * @param startNode Start node label
   * @param endNode End node label
   * @param algorithm Algorithm to use (BFS, DFS, RANDOM)
   * @return Path if found, null if no path exists
   */
  public Path graphSearch(String startNode, String endNode, Algorithm algorithm) {
    // Check if nodes exist
    if (!graph.containsVertex(startNode)) {
      throw new IllegalArgumentException("Source node doesn't exist: " + startNode);
    }
    if (!graph.containsVertex(endNode)) {
      throw new IllegalArgumentException("Destination node doesn't exist: " + endNode);
    }

    // If start and end are the same, return a path with just that node
    if (startNode.equals(endNode)) {
      Path path = new Path();
      path.addNode(startNode);
      return path;
    }

    // Use strategy context to set and execute the algorithm
    searchContext.setAlgorithm(algorithm);
    return searchContext.executeSearch(this, startNode, endNode);
  }

  /**
   * Search for a path using BFS (default algorithm)
   *
   * @param startNode Start node label
   * @param endNode End node label
   * @return Path if found, null if no path exists
   */
  public Path graphSearch(String startNode, String endNode) {
    return graphSearch(startNode, endNode, Algorithm.BFS);
  }

  /**
   * Get a string representation of the graph
   *
   * @return String representation
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