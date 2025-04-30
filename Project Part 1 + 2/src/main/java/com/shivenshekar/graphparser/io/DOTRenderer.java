package com.shivenshekar.graphparser.io;

import com.shivenshekar.graphparser.core.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Renders a graph in DOT format
 */
public class DOTRenderer implements GraphRenderer {
    @Override
    public void render(Graph graph, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("digraph G {\n");

            // Write all nodes
            for (String node : graph.getNodes()) {
                writer.write("    " + node + ";\n");
            }

            // Write all edges
            for (String edge : graph.getEdges()) {
                String[] parts = edge.split(" -> ");
                writer.write("    " + parts[0] + " -> " + parts[1] + ";\n");
            }

            writer.write("}");
        }
    }
}