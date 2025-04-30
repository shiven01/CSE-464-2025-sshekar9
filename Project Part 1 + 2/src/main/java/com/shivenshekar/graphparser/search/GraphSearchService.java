package com.shivenshekar.graphparser.search;

import com.shivenshekar.graphparser.algorithm.Algorithm;
import com.shivenshekar.graphparser.core.Graph;
import com.shivenshekar.graphparser.core.Path;

/**
 * Service class for graph search operations
 */
public class GraphSearchService {
    private final SearchContext searchContext = new SearchContext();

    /**
     * Search for a path between two nodes using the specified algorithm
     *
     * @param graph The graph to search in
     * @param startNode Start node label
     * @param endNode End node label
     * @param algorithm Algorithm to use (BFS, DFS, RANDOM)
     * @return Path if found, null if no path exists
     * @throws IllegalArgumentException if nodes don't exist
     */
    public Path findPath(Graph graph, String startNode, String endNode, Algorithm algorithm) {
        // Check if nodes exist
        if (!graph.containsNode(startNode)) {
            throw new IllegalArgumentException("Source node doesn't exist: " + startNode);
        }
        if (!graph.containsNode(endNode)) {
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
        return searchContext.executeSearch(graph, startNode, endNode);
    }

    /**
     * Search for a path using BFS (default algorithm)
     *
     * @param graph The graph to search in
     * @param startNode Start node label
     * @param endNode End node label
     * @return Path if found, null if no path exists
     */
    public Path findPath(Graph graph, String startNode, String endNode) {
        return findPath(graph, startNode, endNode, Algorithm.BFS);
    }
}