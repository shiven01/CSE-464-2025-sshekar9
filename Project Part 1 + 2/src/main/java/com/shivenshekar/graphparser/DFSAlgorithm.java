package com.shivenshekar.graphparser;

import java.util.*;
import org.jgrapht.graph.DefaultEdge;

/**
 * Depth-First Search algorithm implementation
 */
public class DFSAlgorithm extends GraphSearchAlgorithm {

  @Override
  protected Path performSearch(Graph graph, String srcLabel, String dstLabel) {
    // Set to track visited nodes
    Set<String> visited = new HashSet<>();

    // Start DFS from source node
    Path initialPath = new Path();
    initialPath.addNode(srcLabel);

    return dfsRecursive(graph, srcLabel, dstLabel, visited, initialPath);
  }

  /**
   * Recursive helper method for DFS
   * @param graph The graph
   * @param currentNode Current node being visited
   * @param dstLabel Destination node we're looking for
   * @param visited Set of visited nodes
   * @param path Current path being explored
   * @return A Path object if a path exists, null otherwise
   */
  private Path dfsRecursive(
    Graph graph,
    String currentNode,
    String dstLabel,
    Set<String> visited,
    Path path
  ) {
    // Mark current node as visited
    visited.add(currentNode);

    // If we reached the destination, return the path
    if (currentNode.equals(dstLabel)) {
      return path;
    }

    // Visit all neighbors
    for (DefaultEdge edge : graph.getOutgoingEdges(currentNode)) {
      String neighbor = graph.getEdgeTarget(edge);

      if (!visited.contains(neighbor)) {
        // Add neighbor to path
        Path newPath = path.copy();
        newPath.addNode(neighbor);

        // Recursively visit neighbor
        Path resultPath = dfsRecursive(graph, neighbor, dstLabel, visited, newPath);
        if (resultPath != null) {
          return resultPath;
        }
      }
    }

    // No path found from this node
    return null;
  }
}
