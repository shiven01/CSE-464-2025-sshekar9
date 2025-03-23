package com.shivenshekar.graphparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Path testDotFile;

    @BeforeEach
    void setup(@TempDir Path tempDir) throws IOException {
        // Creating test DOT file
        testDotFile = tempDir.resolve("test.dot");
        String dotContent =
                "digraph G {\n" +
                        "    A;\n" +
                        "    B;\n" +
                        "    C;\n" +
                        "    A -> B;\n" +
                        "    B -> C;\n" +
                        "    C -> A;\n" +
                        "}";
        Files.writeString(testDotFile, dotContent);
    }

    //
    // Feature 1: Parse a DOT Graph File
    //

    @Test
    void parseGraph_shouldCreateGraphCorrectly() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());

        assertEquals(3, graph.getNodeCount());
        assertEquals(3, graph.getEdgeCount());

        Set<String> nodes = graph.getNodes();
        assertTrue(nodes.contains("A"));
        assertTrue(nodes.contains("B"));
        assertTrue(nodes.contains("C"));

        var edges = graph.getEdges();
        assertTrue(edges.contains("A -> B"));
        assertTrue(edges.contains("B -> C"));
        assertTrue(edges.contains("C -> A"));
    }

    @Test
    void toString_shouldReturnCorrectRepresentation() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());

        String representation = graph.toString();

        assertTrue(representation.contains("Number of nodes: 3"));
        assertTrue(representation.contains("Number of edges: 3"));
        assertTrue(representation.contains("A -> B"));
        assertTrue(representation.contains("B -> C"));
        assertTrue(representation.contains("C -> A"));
    }

    @Test
    void outputGraph_shouldWriteCorrectDotFile(@TempDir Path tempDir) throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        Path outputPath = tempDir.resolve("output.dot");

        graph.outputGraph(outputPath.toString());

        assertTrue(Files.exists(outputPath));
        String content = Files.readString(outputPath);

        assertTrue(content.contains("digraph G {"));
        assertTrue(content.contains("A;"));
        assertTrue(content.contains("B;"));
        assertTrue(content.contains("C;"));
        assertTrue(content.contains("A -> B;"));
        assertTrue(content.contains("B -> C;"));
        assertTrue(content.contains("C -> A;"));
    }

    //
    // Feature 2: Adding Nodes
    //

    @Test
    void addNode_shouldAddNewNodeSuccessfully() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialNodeCount = graph.getNodeCount();

        boolean result = graph.addNode("D");

        assertTrue(result, "addNode should return true when adding a new node");
        assertEquals(initialNodeCount + 1, graph.getNodeCount(), "Node count should increase by 1");
        assertTrue(graph.getNodes().contains("D"), "The new node should be in the graph");
    }

    @Test
    void addNode_shouldReturnFalseForDuplicateNode() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialNodeCount = graph.getNodeCount();

        boolean result = graph.addNode("A"); // "A" already exists in the test graph

        assertFalse(result, "addNode should return false when adding a duplicate node");
        assertEquals(initialNodeCount, graph.getNodeCount(), "Node count should remain unchanged");
    }

    @Test
    void addNodes_shouldAddMultipleNodes() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialNodeCount = graph.getNodeCount();
        String[] newNodes = {"D", "E", "F"};

        graph.addNodes(newNodes);

        assertEquals(initialNodeCount + 3, graph.getNodeCount(), "Node count should increase by 3");

        Set<String> graphNodes = graph.getNodes();
        for (String node : newNodes) {
            assertTrue(graphNodes.contains(node), "Node " + node + " should be in the graph");
        }
    }

    @Test
    void addNodes_shouldSkipExistingNodes() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialNodeCount = graph.getNodeCount();
        String[] newNodes = {"A", "D", "B", "E"}; // "A" and "B" already exist in the test graph

        graph.addNodes(newNodes);

        assertEquals(initialNodeCount + 2, graph.getNodeCount(), "Node count should increase by 2");

        Set<String> graphNodes = graph.getNodes();
        assertTrue(graphNodes.contains("D"), "Node D should be in the graph");
        assertTrue(graphNodes.contains("E"), "Node E should be in the graph");
    }

    //
    // Feature 3: Adding Edges
    //

    @Test
    void addEdge_shouldAddNewEdgeSuccessfully() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialEdgeCount = graph.getEdgeCount();

        boolean result = graph.addEdge("A", "C"); // A and C exist but edge A->C doesn't

        assertTrue(result, "addEdge should return true when adding a new edge");
        assertEquals(initialEdgeCount + 1, graph.getEdgeCount(), "Edge count should increase by 1");
        assertTrue(graph.getEdges().contains("A -> C"), "The new edge should be in the graph");
    }

    @Test
    void addEdge_shouldReturnFalseForDuplicateEdge() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialEdgeCount = graph.getEdgeCount();

        boolean result = graph.addEdge("A", "B"); // A->B already exists in the test graph

        assertFalse(result, "addEdge should return false when adding a duplicate edge");
        assertEquals(initialEdgeCount, graph.getEdgeCount(), "Edge count should remain unchanged");
    }

    @Test
    void addEdge_shouldCreateNodesIfNeeded() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialNodeCount = graph.getNodeCount();
        int initialEdgeCount = graph.getEdgeCount();

        boolean result = graph.addEdge("D", "E"); // Both D and E don't exist yet

        assertTrue(result, "addEdge should return true when adding a new edge with new nodes");
        assertEquals(initialNodeCount + 2, graph.getNodeCount(), "Node count should increase by 2");
        assertEquals(initialEdgeCount + 1, graph.getEdgeCount(), "Edge count should increase by 1");
        assertTrue(graph.getNodes().contains("D"), "Node D should be in the graph");
        assertTrue(graph.getNodes().contains("E"), "Node E should be in the graph");
        assertTrue(graph.getEdges().contains("D -> E"), "The new edge should be in the graph");
    }

    //
    // Feature 4: Output the Graph
    //

    @Test
    void outputDOTGraph_shouldWriteCorrectDotFile(@TempDir Path tempDir) throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        Path outputPath = tempDir.resolve("output.dot");

        graph.outputDOTGraph(outputPath.toString());

        assertTrue(Files.exists(outputPath));
        String content = Files.readString(outputPath);

        assertTrue(content.contains("digraph G {"));
        assertTrue(content.contains("A;"));
        assertTrue(content.contains("B;"));
        assertTrue(content.contains("C;"));
        assertTrue(content.contains("A -> B;"));
        assertTrue(content.contains("B -> C;"));
        assertTrue(content.contains("C -> A;"));
    }

    @Test
    void outputGraphics_shouldCreatePNGFile(@TempDir Path tempDir) throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());
        Path outputPath = tempDir.resolve("output.png");

        graph.outputGraphics(outputPath.toString(), "png");

        File pngFile = outputPath.toFile();
        assertTrue(pngFile.exists());
        assertTrue(pngFile.length() > 0);
    }

    @Test
    void outputGraphics_shouldThrowExceptionForUnsupportedFormat(@TempDir Path tempDir) {
        Graph graph = new Graph();
        Path outputPath = tempDir.resolve("output.jpg");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            graph.outputGraphics(outputPath.toString(), "jpg");
        });

        assertTrue(exception.getMessage().contains("Unsupported format"));
        assertTrue(exception.getMessage().contains("jpg"));
    }
}