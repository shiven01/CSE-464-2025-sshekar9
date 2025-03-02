package com.shivenshekar.graphparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    @Test
    void parseGraph_shouldCreateGraphCorrectly() throws IOException {
        // When
        Graph graph = Graph.parseGraph(testDotFile.toString());

        // Then
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
        // Given
        Graph graph = Graph.parseGraph(testDotFile.toString());
        int initialNodeCount = graph.getNodeCount();
        String[] newNodes = {"A", "D", "B", "E"}; // "A" and "B" already exist in the test graph

        // When
        graph.addNodes(newNodes);

        // Then
        assertEquals(initialNodeCount + 2, graph.getNodeCount(), "Node count should increase by 2");

        Set<String> graphNodes = graph.getNodes();
        assertTrue(graphNodes.contains("D"), "Node D should be in the graph");
        assertTrue(graphNodes.contains("E"), "Node E should be in the graph");
    }
}