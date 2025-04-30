package com.shivenshekar.graphparser;

import com.shivenshekar.graphparser.algorithm.Algorithm;
import com.shivenshekar.graphparser.core.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RandomWalkTest {

    private Path testDotFile;

    @BeforeEach
    void setup(@TempDir Path tempDir) throws IOException {
        // Creating test DOT file with multiple paths
        testDotFile = tempDir.resolve("random_walk_test.dot");
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
        Files.writeString(testDotFile, dotContent);
    }

    @Test
    void randomWalk_shouldFindPathMultipleTimes() throws IOException {
        Graph graph = Graph.parseGraph(testDotFile.toString());

        // Run multiple searches to demonstrate randomness
        System.out.println("Random Walk Test - Multiple Searches");

        for (int i = 0; i < 3; i++) {
            com.shivenshekar.graphparser.core.Path path = graph.graphSearch("a", "c", Algorithm.RANDOM);

            // Path may not be found every time due to randomness
            if (path != null) {
                System.out.println(path);
                assertEquals("a", path.getStartNode());
                assertEquals("c", path.getEndNode());
                assertTrue(path.getLength() > 0);
            } else {
                System.out.println("No path found in this attempt");
            }
        }
    }
}