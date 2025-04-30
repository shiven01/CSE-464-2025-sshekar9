package com.shivenshekar.graphparser.io;

import com.shivenshekar.graphparser.core.Graph;
import java.io.IOException;

/**
 * Interface for rendering a graph in different formats
 */
public interface GraphRenderer {
    /**
     * Render a graph to a file
     * @param graph The graph to render
     * @param outputPath Path to the output file
     * @throws IOException If the file cannot be written
     */
    void render(Graph graph, String outputPath) throws IOException;
}