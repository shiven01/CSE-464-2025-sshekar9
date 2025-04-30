package com.shivenshekar.graphparser.core;

import com.shivenshekar.graphparser.algorithm.Algorithm;
import com.shivenshekar.graphparser.io.DOTRenderer;
import com.shivenshekar.graphparser.io.GraphvizRenderer;
import com.shivenshekar.graphparser.parser.DOTParser;
import com.shivenshekar.graphparser.search.GraphSearchService;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import com.shivenshekar.graphparser.search.SearchContext;

public class Graph {

  private DefaultDirectedGraph<String, DefaultEdge> graph;
  private final SearchContext searchContext = new SearchContext();
  private Map<String, String> nodeLabels;
  private final DOTRenderer dotRenderer = new DOTRenderer();
  private final GraphvizRenderer pngRenderer = new GraphvizRenderer("png");
  private final GraphvizRenderer svgRenderer = new GraphvizRenderer("svg");
  private final GraphSearchService searchService = new GraphSearchService();

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
    return DOTParser.parseGraph(filepath);
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
   * @deprecated Use DOTRenderer directly instead
   */
  @Deprecated
  public void outputDOTGraph(String filepath) throws IOException {
    dotRenderer.render(this, filepath);
  }

  /**
   * Legacy method for compatibility with tests
   * @deprecated Use DOTRenderer directly instead
   */
  @Deprecated
  public void outputGraph(String outputPath) throws IOException {
    outputDOTGraph(outputPath);
  }

  /**
   * @deprecated Use GraphvizRenderer directly instead
   */
  @Deprecated
  public void outputGraphics(String outputPath, String format) throws IOException {
    if ("png".equalsIgnoreCase(format)) {
      pngRenderer.render(this, outputPath);
    } else if ("svg".equalsIgnoreCase(format)) {
      svgRenderer.render(this, outputPath);
    } else {
      throw new IllegalArgumentException("Unsupported format: " + format);
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
   * @deprecated Use GraphSearchService.findPath instead
   */
  @Deprecated
  public Path graphSearch(String startNode, String endNode, Algorithm algorithm) {
    return searchService.findPath(this, startNode, endNode, algorithm);
  }

  /**
   * @deprecated Use GraphSearchService.findPath instead
   */
  @Deprecated
  public Path graphSearch(String startNode, String endNode) {
    return searchService.findPath(this, startNode, endNode);
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

  /**
   * Check if a node exists in the graph
   *
   * @param label Node label to check
   * @return true if the node exists
   */
  public boolean containsNode(String label) {
    return graph.containsVertex(label);
  }
}