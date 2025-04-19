package com.shivenshekar.graphparser.search;

import com.shivenshekar.graphparser.Algorithm;
import com.shivenshekar.graphparser.BFSAlgorithm;
import com.shivenshekar.graphparser.DFSAlgorithm;
import com.shivenshekar.graphparser.Graph;
import com.shivenshekar.graphparser.Path;
import com.shivenshekar.graphparser.RandomWalkAlgorithm;

/**
 * Context class for the Strategy Pattern
 */
public class SearchContext {
    private SearchStrategy strategy;

    /**
     * Set strategy based on algorithm type
     * @param algo Algorithm enum value
     */
    public void setAlgorithm(Algorithm algo) {
        switch (algo) {
            case BFS:
                this.strategy = new BFSAlgorithm();
                break;
            case DFS:
                this.strategy = new DFSAlgorithm();
                break;
            case RANDOM:
                this.strategy = new RandomWalkAlgorithm();
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algo);
        }
    }

    /**
     * Set a specific strategy
     * @param strategy The search strategy to use
     */
    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Execute the selected search strategy
     * @param graph The graph to search in
     * @param srcLabel Source node label
     * @param dstLabel Destination node label
     * @return A Path object if a path exists, null otherwise
     */
    public Path executeSearch(Graph graph, String srcLabel, String dstLabel) {
        if (strategy == null) {
            throw new IllegalStateException("Search strategy not set");
        }
        return strategy.findPath(graph, srcLabel, dstLabel);
    }
}