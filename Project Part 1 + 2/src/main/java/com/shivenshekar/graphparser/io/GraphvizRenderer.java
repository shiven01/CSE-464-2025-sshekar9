package com.shivenshekar.graphparser.io;

import com.shivenshekar.graphparser.core.Graph;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;

/**
 * Renders a graph using Graphviz in various image formats
 */
public class GraphvizRenderer implements GraphRenderer {
    private final Format format;

    public GraphvizRenderer(String formatName) {
        switch (formatName.toLowerCase()) {
            case "png":
                this.format = Format.PNG;
                break;
            case "svg":
                this.format = Format.SVG;
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + formatName);
        }
    }

    @Override
    public void render(Graph graph, String outputPath) throws IOException {
        // Create temporary DOT file
        File tempFile = File.createTempFile("graph_", ".dot");
        new DOTRenderer().render(graph, tempFile.getAbsolutePath());

        try {
            // Generate the graphical output
            MutableGraph g = new Parser().read(tempFile);
            Graphviz.fromGraph(g).width(700).render(format).toFile(new File(outputPath));
        } finally {
            // Clean up temporary file
            tempFile.delete();
        }
    }
}