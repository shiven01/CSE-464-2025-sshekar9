package com.shivenshekar.graphparser.search;

import com.shivenshekar.graphparser.Graph;
import com.shivenshekar.graphparser.Path;

/**
 * Strategy interface for graph search algorithms
 */
public interface SearchStrategy {
    /**
     * Find a path from source to destination
     * @param graph The graph to search in
     * @param srcLabel Source node label
     * @param dstLabel Destination node label
     * @return A Path object if a path exists, null otherwise
     */
    Path findPath(Graph graph, String srcLabel, String dstLabel);
}