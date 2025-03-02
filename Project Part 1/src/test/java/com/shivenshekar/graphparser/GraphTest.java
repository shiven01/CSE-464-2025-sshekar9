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
}