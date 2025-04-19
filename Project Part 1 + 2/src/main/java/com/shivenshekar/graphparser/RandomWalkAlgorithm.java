package com.shivenshekar.graphparser;

import java.util.*;
import org.jgrapht.graph.DefaultEdge;

/**
 * Random Walk Search algorithm implementation
 */
public class RandomWalkAlgorithm extends GraphSearchAlgorithm {
    // Maximum steps to take before giving up
    private static final int MAX_STEPS = 1000;
    private final Random random = new Random();

    @Override
    protected Path performSearch(Graph graph, String srcLabel, String dstLabel) {
        String currentNode = srcLabel;
        Path currentPath = new Path();
        currentPath.addNode(srcLabel);
        Set<String> visited = new HashSet<>();
        visited.add(srcLabel);
        int steps = 0;

        System.out.println("random testing");

        // Continue random walk until destination is found or max steps reached
        while (steps < MAX_STEPS) {
            // Print current path for demonstration
            System.out.println("visiting " + currentPath);

            // Get neighbors
            List<String> neighbors = new ArrayList<>();
            for (DefaultEdge edge : graph.getOutgoingEdges(currentNode)) {
                String neighbor = graph.getEdgeTarget(edge);

                // If destination is a direct neighbor, go there and return
                if (neighbor.equals(dstLabel)) {
                    Path finalPath = currentPath.copy();
                    finalPath.addNode(dstLabel);
                    return finalPath;
                }

                neighbors.add(neighbor);
            }

            // If no neighbors or stuck in a dead end
            if (neighbors.isEmpty()) {
                return null;
            }

            // Choose a random neighbor
            String nextNode = neighbors.get(random.nextInt(neighbors.size()));

            // Update current node and path
            currentNode = nextNode;
            currentPath.addNode(nextNode);
            visited.add(nextNode);

            // Check if we've reached the destination
            if (nextNode.equals(dstLabel)) {
                return currentPath;
            }

            steps++;
        }

        // No path found within max steps
        return null;
    }
}