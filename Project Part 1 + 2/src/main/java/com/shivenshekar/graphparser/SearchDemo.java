package com.shivenshekar.graphparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.jgrapht.graph.DefaultEdge;

public class SearchDemo {

    public static void main(String[] args) {
        try {
            // Create the demo graph file with the specified structure
            String dotFilePath = "demo_graph.dot";
            String dotContent = "digraph {\n" +
                    "    a -> b;\n" +
                    "    b -> c;\n" +
                    "    c -> d;\n" +
                    "    d -> a;\n" +
                    "    a -> e;\n" +
                    "    e -> f;\n" +
                    "    e -> g;\n" +
                    "    f -> h;\n" +
                    "    g -> h;\n" +
                    "}";
            Files.writeString(Paths.get(dotFilePath), dotContent);
            System.out.println("Created demo graph file: " + dotFilePath);

            // Parse the graph
            Graph graph = Graph.parseGraph(dotFilePath);
            System.out.println("Parsed graph with " + graph.getNodeCount() + " nodes and " +
                    graph.getEdgeCount() + " edges");

            // Demonstrate BFS with path tracing (Scheme A)
            System.out.println("\n=== BFS Demonstration (Scheme A) ===\n");
            Path bfsPath = demonstrateBFS(graph, "a", "c");
            if (bfsPath != null) {
                System.out.println(formatPathForSchemeA(bfsPath));
            } else {
                System.out.println("No path found using BFS.");
            }

            // Demonstrate DFS with path tracing (Scheme A)
            System.out.println("\n=== DFS Demonstration (Scheme A) ===\n");
            Path dfsPath = demonstrateDFS(graph, "a", "c");
            if (dfsPath != null) {
                System.out.println(formatPathForSchemeA(dfsPath));
            } else {
                System.out.println("No path found using DFS.");
            }

            // Demonstrate Random Walk
            System.out.println("\n=== Random Walk Demonstration ===\n");
            demonstrateRandomWalk(graph, "a", "h");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Custom BFS implementation that prints visited paths
    private static Path demonstrateBFS(Graph graph, String srcLabel, String dstLabel) {
        // Set to track visited nodes
        Set<String> visited = new HashSet<>();
        // Queue to track nodes to visit
        Queue<Path> queue = new LinkedList<>();

        // Initialize path with source node
        Path initialPath = new Path();
        initialPath.addNode(srcLabel);
        queue.add(initialPath);
        visited.add(srcLabel);

        System.out.println("visiting " + formatPathForSchemeA(initialPath));

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
                    System.out.println("visiting " + formatPathForSchemeA(newPath));
                    queue.add(newPath);
                }
            }
        }

        // No path found
        return null;
    }

    // Custom DFS implementation that prints visited paths
    private static Path demonstrateDFS(Graph graph, String srcLabel, String dstLabel) {
        // Set to track visited nodes
        Set<String> visited = new HashSet<>();

        // Start DFS from source node
        Path initialPath = new Path();
        initialPath.addNode(srcLabel);
        System.out.println("visiting " + formatPathForSchemeA(initialPath));

        return dfsRecursive(graph, srcLabel, dstLabel, visited, initialPath);
    }

    private static Path dfsRecursive(
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
                System.out.println("visiting " + formatPathForSchemeA(newPath));

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

    // Demonstrate Random Walk with multiple attempts
    private static void demonstrateRandomWalk(Graph graph, String startNode, String endNode) {
        System.out.println("Random Walk from " + startNode + " to " + endNode + ":");

        List<Path> successfulPaths = new ArrayList<>();
        int requiredAttempts = 5;
        int maxAttempts = 20;  // Set a reasonable upper limit

        // Run random walk multiple times
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            // Create a random walk algorithm with fixed seed for reproducibility
            RandomWalkAlgorithm randomWalk = new RandomWalkAlgorithm(
                1000, // maxSteps
                0.3,  // backtrackProbability
                false, // verbose - we'll handle output manually
                new Random(System.nanoTime() + attempt) // random seed
            );

            // Run the search
            Path result = randomWalk.findPath(graph, startNode, endNode);

            // Format and print the result
            StringBuilder pathStr = new StringBuilder("Attempt " + attempt + ": ");
            if (result != null) {
                pathStr.append(formatPathForDisplay(result));
                pathStr.append(" (target node!)");

                // Check if this is a new path
                boolean isNewPath = true;
                for (Path existingPath : successfulPaths) {
                    if (pathsEqual(existingPath, result)) {
                        isNewPath = false;
                        break;
                    }
                }

                if (isNewPath) {
                    successfulPaths.add(result);
                }
            } else {
                pathStr.append("(Dead end)");
            }
            System.out.println(pathStr.toString());

            // Check if we have at least 2 different paths and at least 5 attempts
            if (successfulPaths.size() >= 2 && attempt >= requiredAttempts) {
                break;
            }
        }

        // Summarize results
        System.out.println("\nRandom Walk Summary:");
        System.out.println("Found " + successfulPaths.size() + " different successful paths out of at least " +
                          requiredAttempts + " attempts.");
        for (int i = 0; i < successfulPaths.size(); i++) {
            System.out.println((i + 1) + ": " + formatPathForDisplay(successfulPaths.get(i)));
        }
    }

    // Helper method to compare paths
    private static boolean pathsEqual(Path path1, Path path2) {
        if (path1.getLength() != path2.getLength()) {
            return false;
        }

        List<String> nodes1 = path1.getNodes();
        List<String> nodes2 = path2.getNodes();

        return nodes1.equals(nodes2);
    }

    // Helper to format path for display (Random Walk)
    private static String formatPathForDisplay(Path path) {
        List<String> nodes = path.getNodes();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nodes.size(); i++) {
            sb.append(nodes.get(i));
            if (i < nodes.size() - 1) {
                sb.append("->");
            }
        }

        return sb.toString();
    }

    // Format paths according to Scheme A format (for BFS and DFS)
    private static String formatPathForSchemeA(Path path) {
        StringBuilder sb = new StringBuilder("Path{nodes=[");

        List<String> nodes = path.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            sb.append("Node{").append(nodes.get(i)).append("}");
            if (i < nodes.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}");
        return sb.toString();
    }
}