package com.shivenshekar.graphparser;

import java.util.*;
import org.jgrapht.graph.DefaultEdge;

/**
 * Random Walk Search algorithm implementation with configurable parameters
 */
public class RandomWalkAlgorithm extends GraphSearchAlgorithm {
    // Default maximum steps to take before giving up
    private static final int DEFAULT_MAX_STEPS = 1000;
    // Default probability of backtracking when stuck
    private static final double DEFAULT_BACKTRACK_PROBABILITY = 0.3;

    private final Random random;
    private final int maxSteps;
    private final double backtrackProbability;
    private boolean verbose;

    /**
     * Creates a RandomWalkAlgorithm with default parameters
     */
    public RandomWalkAlgorithm() {
        this(DEFAULT_MAX_STEPS, DEFAULT_BACKTRACK_PROBABILITY, false, new Random());
    }

    /**
     * Creates a RandomWalkAlgorithm with custom parameters
     *
     * @param maxSteps Maximum number of steps before giving up
     * @param backtrackProbability Probability of backtracking when stuck
     * @param verbose Whether to print debug information
     * @param random Random number generator to use
     */
    public RandomWalkAlgorithm(int maxSteps, double backtrackProbability, boolean verbose, Random random) {
        this.maxSteps = maxSteps;
        this.backtrackProbability = backtrackProbability;
        this.verbose = verbose;
        this.random = random;
    }

    /**
     * Set verbose mode for debugging
     *
     * @param verbose true to enable debug output
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    protected Path performSearch(Graph graph, String srcLabel, String dstLabel) {
        String currentNode = srcLabel;
        Path currentPath = new Path();
        currentPath.addNode(srcLabel);

        // Track visited nodes and their visit counts
        Map<String, Integer> visitCounts = new HashMap<>();
        visitCounts.put(srcLabel, 1);

        int steps = 0;

        if (verbose) {
            System.out.println("Starting random walk from " + srcLabel + " to " + dstLabel);
        }

        // Continue random walk until destination is found or max steps reached
        while (steps < maxSteps) {
            if (verbose) {
                System.out.println("Step " + steps + ": " + currentPath);
            }

            // Get neighbors
            List<String> neighbors = new ArrayList<>();
            for (DefaultEdge edge : graph.getOutgoingEdges(currentNode)) {
                String neighbor = graph.getEdgeTarget(edge);

                // If destination is a direct neighbor, go there and return
                if (neighbor.equals(dstLabel)) {
                    Path finalPath = currentPath.copy();
                    finalPath.addNode(dstLabel);

                    if (verbose) {
                        System.out.println("Path found: " + finalPath);
                    }

                    return finalPath;
                }

                neighbors.add(neighbor);
            }

            // If no neighbors, we're stuck in a dead end
            if (neighbors.isEmpty()) {
                if (verbose) {
                    System.out.println("Dead end reached at " + currentNode);
                }
                return null;
            }

            // Choose next node with preference for less visited nodes
            String nextNode;

            // Possibly backtrack if we're revisiting nodes too much
            if (hasHighlyVisitedNeighbors(neighbors, visitCounts) && random.nextDouble() < backtrackProbability) {
                // Backtrack - remove current node and go back
                if (currentPath.getNodes().size() > 1) {
                    List<String> pathNodes = new ArrayList<>(currentPath.getNodes());
                    pathNodes.remove(pathNodes.size() - 1);
                    nextNode = pathNodes.get(pathNodes.size() - 1);

                    // Create new path after backtracking
                    currentPath = new Path(pathNodes);

                    if (verbose) {
                        System.out.println("Backtracking to " + nextNode);
                    }
                } else {
                    // Can't backtrack from start node, choose a random neighbor
                    nextNode = neighbors.get(random.nextInt(neighbors.size()));
                    currentPath.addNode(nextNode);
                }
            } else {
                // Choose next node with preference for less visited neighbors
                nextNode = selectNextNode(neighbors, visitCounts);
                currentPath.addNode(nextNode);
            }

            // Update current node and visit count
            currentNode = nextNode;
            visitCounts.put(currentNode, visitCounts.getOrDefault(currentNode, 0) + 1);

            // Check if we've reached the destination
            if (nextNode.equals(dstLabel)) {
                if (verbose) {
                    System.out.println("Path found: " + currentPath);
                }
                return currentPath;
            }

            steps++;
        }

        if (verbose) {
            System.out.println("No path found after " + maxSteps + " steps");
        }

        // No path found within max steps
        return null;
    }

    /**
     * Check if neighbors have been visited many times
     */
    private boolean hasHighlyVisitedNeighbors(List<String> neighbors, Map<String, Integer> visitCounts) {
        for (String neighbor : neighbors) {
            int visits = visitCounts.getOrDefault(neighbor, 0);
            if (visits > 2) {  // Threshold for "highly visited"
                return true;
            }
        }
        return false;
    }

    /**
     * Select the next node to visit with preference for less visited nodes
     */
    private String selectNextNode(List<String> neighbors, Map<String, Integer> visitCounts) {
        // Sort neighbors by visit count (ascending)
        neighbors.sort(Comparator.comparingInt(n -> visitCounts.getOrDefault(n, 0)));

        // 70% chance to pick from less visited nodes, 30% fully random
        if (random.nextDouble() < 0.7 && !neighbors.isEmpty()) {
            // Pick from the least visited half of neighbors
            int candidateCount = Math.max(1, neighbors.size() / 2);
            return neighbors.get(random.nextInt(candidateCount));
        } else {
            // Completely random choice
            return neighbors.get(random.nextInt(neighbors.size()));
        }
    }
}