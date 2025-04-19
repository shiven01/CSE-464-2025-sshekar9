package com.shivenshekar.graphparser;

/**
 * Abstract base class for graph search algorithms using Template Method pattern
 */
public abstract class GraphSearchAlgorithm {

  /**
   * Template method that defines the skeleton of the search algorithm
   * @param graph The graph to search in
   * @param srcLabel Source node label
   * @param dstLabel Destination node label
   * @return A Path object if a path exists, null otherwise
   */
  public final Path findPath(Graph graph, String srcLabel, String dstLabel) {
    // Handle special case: source and destination are the same
    if (srcLabel.equals(dstLabel)) {
      Path path = new Path();
      path.addNode(srcLabel);
      return path;
    }

    // Execute algorithm-specific search
    return performSearch(graph, srcLabel, dstLabel);
  }

  /**
   * Abstract method to be implemented by concrete algorithm classes
   * @param graph The graph to search in
   * @param srcLabel Source node label
   * @param dstLabel Destination node label
   * @return A Path object if a path exists, null otherwise
   */
  protected abstract Path performSearch(Graph graph, String srcLabel, String dstLabel);
}
