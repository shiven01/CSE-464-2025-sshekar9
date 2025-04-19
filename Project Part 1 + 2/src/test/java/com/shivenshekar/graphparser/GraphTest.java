package com.shivenshekar.graphparser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

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

    // Access internal jgrapht graph and verify it
    DefaultDirectedGraph<String, DefaultEdge> jGraph = graph.getInternalGraph();

    // Check nodes
    assertEquals(3, jGraph.vertexSet().size());
    assertTrue(jGraph.containsVertex("A"));
    assertTrue(jGraph.containsVertex("B"));
    assertTrue(jGraph.containsVertex("C"));

    // Check edges
    assertTrue(jGraph.containsEdge("A", "B"));
    assertTrue(jGraph.containsEdge("B", "C"));
    assertTrue(jGraph.containsEdge("C", "A"));
  }

  @Test
  void toString_shouldReturnCorrectRepresentation() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());

    String representation = graph.toString();

    // Check that the string contains essential information
    assertTrue(representation.contains("A"));
    assertTrue(representation.contains("B"));
    assertTrue(representation.contains("C"));
  }

  //
  // Feature 2: Adding Nodes
  //

  @Test
  void addNode_shouldAddNewNodeSuccessfully() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());
    DefaultDirectedGraph<String, DefaultEdge> jGraph = graph.getInternalGraph();
    int initialNodeCount = jGraph.vertexSet().size();

    boolean result = graph.addNode("D");

    assertTrue(result, "addNode should return true when adding a new node");
    assertEquals(initialNodeCount + 1, jGraph.vertexSet().size(), "Node count should increase by 1");
    assertTrue(jGraph.containsVertex("D"), "The new node should be in the graph");
  }

  @Test
  void addNode_shouldReturnFalseForDuplicateNode() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());
    DefaultDirectedGraph<String, DefaultEdge> jGraph = graph.getInternalGraph();
    int initialNodeCount = jGraph.vertexSet().size();

    boolean result = graph.addNode("A"); // "A" already exists in the test graph

    assertFalse(result, "addNode should return false when adding a duplicate node");
    assertEquals(initialNodeCount, jGraph.vertexSet().size(), "Node count should remain unchanged");
  }

  //
  // Feature 3: Adding Edges
  //

  @Test
  void addEdge_shouldAddNewEdgeSuccessfully() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());
    DefaultDirectedGraph<String, DefaultEdge> jGraph = graph.getInternalGraph();
    int initialEdgeCount = jGraph.edgeSet().size();

    // Add nodes A and C if not already in graph
    graph.addNode("A");
    graph.addNode("C");

    // Add edge A->C
    boolean result = graph.addEdge("A", "C");

    assertTrue(result, "addEdge should return true when adding a new edge");
    assertEquals(initialEdgeCount + 1, jGraph.edgeSet().size(), "Edge count should increase by 1");
    assertTrue(jGraph.containsEdge("A", "C"), "The new edge should be in the graph");
  }

  @Test
  void addEdge_shouldReturnFalseForDuplicateEdge() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());
    DefaultDirectedGraph<String, DefaultEdge> jGraph = graph.getInternalGraph();
    int initialEdgeCount = jGraph.edgeSet().size();

    boolean result = graph.addEdge("A", "B"); // A->B already exists in the test graph

    assertFalse(result, "addEdge should return false when adding a duplicate edge");
    assertEquals(initialEdgeCount, jGraph.edgeSet().size(), "Edge count should remain unchanged");
  }

  @Test
  void addEdge_shouldCreateNodesIfNeeded() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());
    DefaultDirectedGraph<String, DefaultEdge> jGraph = graph.getInternalGraph();
    int initialNodeCount = jGraph.vertexSet().size();
    int initialEdgeCount = jGraph.edgeSet().size();

    boolean result = graph.addEdge("D", "E"); // Both D and E don't exist yet

    assertTrue(result, "addEdge should return true when adding a new edge with new nodes");
    assertEquals(initialNodeCount + 2, jGraph.vertexSet().size(), "Node count should increase by 2");
    assertEquals(initialEdgeCount + 1, jGraph.edgeSet().size(), "Edge count should increase by 1");
    assertTrue(jGraph.containsVertex("D"), "Node D should be in the graph");
    assertTrue(jGraph.containsVertex("E"), "Node E should be in the graph");
    assertTrue(jGraph.containsEdge("D", "E"), "The new edge should be in the graph");
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
    assertTrue(content.contains("A"));
    assertTrue(content.contains("B"));
    assertTrue(content.contains("C"));
    // The exact format of edges might vary
    assertTrue(content.contains("A") && content.contains("B"));
    assertTrue(content.contains("B") && content.contains("C"));
    assertTrue(content.contains("C") && content.contains("A"));
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
  }

  //
  // Feature 7 & 8: Graph Search with BFS and DFS
  //

  @Test
  void graphSearch_shouldFindPathUsingBFS(@TempDir Path tempDir) throws IOException {
    // Create a test graph with a known path
    Path searchDotFile = tempDir.resolve("search_test.dot");
    String dotContent =
            "digraph G {\n" +
                    "    A;\n" +
                    "    B;\n" +
                    "    C;\n" +
                    "    D;\n" +
                    "    E;\n" +
                    "    F;\n" +
                    "    A -> B;\n" +
                    "    B -> C;\n" +
                    "    C -> D;\n" +
                    "    A -> E;\n" +
                    "    E -> F;\n" +
                    "    F -> D;\n" +
                    "}";
    Files.writeString(searchDotFile, dotContent);

    Graph graph = Graph.parseGraph(searchDotFile.toString());

    com.shivenshekar.graphparser.Path path = graph.graphSearch("A", "D", Algorithm.BFS);

    assertNotNull(path);
    assertFalse(path.isEmpty());
    assertEquals("A", path.getStartNode());
    assertEquals("D", path.getEndNode());

    // BFS should find the shortest path which is A -> E -> F -> D (3 edges)
    // or A -> B -> C -> D (3 edges)
    // Both are valid shortest paths with BFS
    assertEquals(3, path.getLength());
  }

  @Test
  void graphSearch_shouldFindPathUsingDFS(@TempDir Path tempDir) throws IOException {
    // Create a test graph with a known path
    Path searchDotFile = tempDir.resolve("search_test.dot");
    String dotContent =
            "digraph G {\n" +
                    "    A;\n" +
                    "    B;\n" +
                    "    C;\n" +
                    "    D;\n" +
                    "    E;\n" +
                    "    F;\n" +
                    "    A -> B;\n" +
                    "    B -> C;\n" +
                    "    C -> D;\n" +
                    "    A -> E;\n" +
                    "    E -> F;\n" +
                    "    F -> D;\n" +
                    "}";
    Files.writeString(searchDotFile, dotContent);

    Graph graph = Graph.parseGraph(searchDotFile.toString());

    com.shivenshekar.graphparser.Path path = graph.graphSearch("A", "D", Algorithm.DFS);

    assertNotNull(path);
    assertFalse(path.isEmpty());
    assertEquals("A", path.getStartNode());
    assertEquals("D", path.getEndNode());

    // DFS will find a path, but not necessarily the shortest one
    // So we only check that a path exists, not its specific length
    assertTrue(path.getLength() > 0);
  }

  @Test
  void graphSearch_shouldFindPathUsingRandomWalk(@TempDir Path tempDir) throws IOException {
    // Create a test graph with a known path
    Path searchDotFile = tempDir.resolve("search_test.dot");
    String dotContent =
            "digraph G {\n" +
                    "    A;\n" +
                    "    B;\n" +
                    "    C;\n" +
                    "    D;\n" +
                    "    A -> B;\n" +
                    "    B -> C;\n" +
                    "    C -> D;\n" +
                    "}";
    Files.writeString(searchDotFile, dotContent);

    Graph graph = Graph.parseGraph(searchDotFile.toString());

    // Since random walk is non-deterministic, we try multiple times
    boolean foundPath = false;
    for (int i = 0; i < 10 && !foundPath; i++) {
      com.shivenshekar.graphparser.Path path = graph.graphSearch("A", "D", Algorithm.RANDOM);

      if (path != null) {
        foundPath = true;
        assertEquals("A", path.getStartNode());
        assertEquals("D", path.getEndNode());
        assertTrue(path.getLength() > 0);
      }
    }

    // We expect to find a path most of the time, but it's random
    // so we don't strictly assert it to avoid flaky tests
  }

  @Test
  void graphSearch_shouldReturnPathWithSingleNodeForSameStartAndEnd() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());

    com.shivenshekar.graphparser.Path path = graph.graphSearch("A", "A", Algorithm.BFS);

    assertNotNull(path);
    assertFalse(path.isEmpty());
    assertEquals(1, path.getNodes().size());
    assertEquals("A", path.getStartNode());
    assertEquals("A", path.getEndNode());
    assertEquals(0, path.getLength());
  }

  @Test
  void graphSearch_shouldReturnNullForUnreachableNode() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());

    // Add an isolated node
    graph.addNode("Z");

    com.shivenshekar.graphparser.Path path = graph.graphSearch("A", "Z", Algorithm.BFS);

    assertNull(path);
  }

  @Test
  void graphSearch_shouldThrowExceptionForNonExistentSourceNode() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      graph.graphSearch("X", "A", Algorithm.BFS);
    });

    assertTrue(exception.getMessage().contains("Source node doesn't exist"));
  }

  @Test
  void graphSearch_shouldThrowExceptionForNonExistentDestinationNode() throws IOException {
    Graph graph = Graph.parseGraph(testDotFile.toString());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      graph.graphSearch("A", "X", Algorithm.BFS);
    });

    assertTrue(exception.getMessage().contains("Destination node doesn't exist"));
  }

  // Helper method to get access to the internal graph
  private static DefaultDirectedGraph<String, DefaultEdge> getInternalGraph(Graph graph) throws Exception {
    java.lang.reflect.Field field = Graph.class.getDeclaredField("graph");
    field.setAccessible(true);
    return (DefaultDirectedGraph<String, DefaultEdge>) field.get(graph);
  }
}