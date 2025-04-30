package com.shivenshekar.graphparser.algorithm;

import java.util.*;

import com.shivenshekar.graphparser.core.Graph;
import com.shivenshekar.graphparser.core.Path;
import org.jgrapht.graph.DefaultEdge;

/**
 * Breadth-First Search algorithm implementation
 */
public class BFSAlgorithm extends GraphSearchAlgorithm {

  @Override
  protected Path performSearch(Graph graph, String srcLabel, String dstLabel) {
    // Set to track visited nodes
    Set<String> visited = new HashSet<>();
    // Queue to track nodes to visit
    Queue<Path> queue = new LinkedList<>();

    // Initialize path with source node
    Path initialPath = new Path();
    initialPath.addNode(srcLabel);
    queue.add(initialPath);
    visited.add(srcLabel);

    // BFS traversal
    while (!queue.isEmpty()) {
      Path currentPath = queue.poll();
      String currentNode = currentPath.getEndNode();

      // Get neighbors (outgoing edges)
      for (DefaultEdge edge : graph.getOutgoingEdges(currentNode)) {
        String neighbor = graph.getEdgeTarget(edge);

        // If we found the destination, return the path
        if (neighbor.equals(dstLabel)) {
          Path foundPath = currentPath.copy();
          foundPath.addNode(neighbor);
          return foundPath;
        }

        // If neighbor hasn't been visited, add it to the queue
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          Path newPath = currentPath.copy();
          newPath.addNode(neighbor);
          queue.add(newPath);
        }
      }
    }

    // No path found
    return null;
  }
}
