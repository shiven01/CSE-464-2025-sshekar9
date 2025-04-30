package com.shivenshekar.graphparser.demo;

import com.shivenshekar.graphparser.algorithm.Algorithm;
import com.shivenshekar.graphparser.core.Graph;
import com.shivenshekar.graphparser.core.Path;

import java.io.IOException;
import java.nio.file.Files;
// Import Path as FilePath using a full reference
import java.nio.file.Paths;

public class RandomWalkDemo {

    public static void main(String[] args) {
        try {
            // Create a demo graph file if it doesn't exist
            String dotFilePath = "random_walk_demo.dot";
            java.nio.file.Path dotFile = Paths.get(dotFilePath);  // Use full qualification

            if (!Files.exists(dotFile)) {
                String dotContent = "digraph G {\n" +
                        "    a;\n" +
                        "    b;\n" +
                        "    c;\n" +
                        "    d;\n" +
                        "    e;\n" +
                        "    f;\n" +
                        "    g;\n" +
                        "    h;\n" +
                        "    a -> b;\n" +
                        "    b -> c;\n" +
                        "    a -> e;\n" +
                        "    e -> f;\n" +
                        "    e -> g;\n" +
                        "    g -> h;\n" +
                        "}";
                Files.writeString(dotFile, dotContent);
                System.out.println("Created demo graph file: " + dotFile.toAbsolutePath());
            }

            // Parse the graph
            Graph graph = Graph.parseGraph(dotFilePath);
            System.out.println("Parsed graph with " + graph.getNodeCount() + " nodes and " +
                    graph.getEdgeCount() + " edges");

            // Run multiple random walk searches to demonstrate randomness
            System.out.println("\nRunning multiple random walk searches from 'a' to 'c'...\n");

            for (int i = 0; i < 3; i++) {
                System.out.println("Search attempt #" + (i+1) + ":");
                // Use a different variable name for our custom Path
                Path graphPath = graph.graphSearch("a", "c", Algorithm.RANDOM);

                if (graphPath != null) {
                    System.out.println("Path found: " + graphPath);
                } else {
                    System.out.println("No path found in this attempt");
                }
                System.out.println();
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}