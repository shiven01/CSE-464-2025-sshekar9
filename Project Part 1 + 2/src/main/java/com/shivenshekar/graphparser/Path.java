package com.shivenshekar.graphparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to represent a path in a graph
 * Represents a path like n1 -> n2 -> n3
 */
public class Path {

  private List<String> nodes;
  private String startNode;
  private String endNode;

  /**
   * Create an empty path with no defined start or end nodes
   * This is used when constructing paths programmatically
   */
  public Path() {
    this.nodes = new ArrayList<>();
    this.startNode = null;
    this.endNode = null;
  }

  /**
   * Create a path with specified start and end nodes
   *
   * @param startNode Start node of the path
   * @param endNode End node of the path
   */
  public Path(String startNode, String endNode) {
    this.nodes = new ArrayList<>();
    this.startNode = startNode;
    this.endNode = endNode;
  }

  /**
   * Create a path from a list of nodes
   *
   * @param nodes List of node labels in order of traversal
   */
  public Path(List<String> nodes) {
    this.nodes = new ArrayList<>(nodes);
    if (!nodes.isEmpty()) {
      this.startNode = nodes.get(0);
      this.endNode = nodes.get(nodes.size() - 1);
    }
  }

  /**
   * Add a node to the end of the path
   *
   * @param node Label of the node to add
   */
  public void addNode(String node) {
    nodes.add(node);
    // Update start/end nodes if this is the first node or a new end
    if (nodes.size() == 1) {
      this.startNode = node;
    }
    this.endNode = node;
  }

  /**
   * Get all nodes in the path
   *
   * @return Unmodifiable list of nodes in the path
   */
  public List<String> getNodes() {
    return Collections.unmodifiableList(nodes);
  }

  /**
   * Get the first node in the path
   *
   * @return Label of the first node
   * @throws IllegalStateException if the path is empty
   */
  public String getStartNode() {
    if (nodes.isEmpty()) {
      throw new IllegalStateException("Path is empty");
    }
    return nodes.get(0);
  }

  /**
   * Get the last node in the path
   *
   * @return Label of the last node
   * @throws IllegalStateException if the path is empty
   */
  public String getEndNode() {
    if (nodes.isEmpty()) {
      throw new IllegalStateException("Path is empty");
    }
    return nodes.get(nodes.size() - 1);
  }

  /**
   * Get the length of the path (number of edges)
   *
   * @return Number of edges in the path (nodes - 1)
   */
  public int getLength() {
    return Math.max(0, nodes.size() - 1);
  }

  /**
   * Check if the path is empty
   *
   * @return true if the path has no nodes
   */
  public boolean isEmpty() {
    return nodes.isEmpty();
  }

  /**
   * Create a copy of this path
   *
   * @return A new Path instance with the same nodes
   */
  public Path copy() {
    return new Path(new ArrayList<>(this.nodes));
  }

  @Override
  public String toString() {
    if (nodes.isEmpty()) {
      return "Empty path";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < nodes.size(); i++) {
      sb.append(nodes.get(i));
      if (i < nodes.size() - 1) {
        sb.append(" -> ");
      }
    }
    return sb.toString();
  }
}