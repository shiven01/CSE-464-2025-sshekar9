package com.shivenshekar.graphparser.demo;

import com.shivenshekar.graphparser.core.Graph;
import com.shivenshekar.graphparser.io.DOTRenderer;
import com.shivenshekar.graphparser.io.GraphvizRenderer;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    try {
      String inputPath = "src/main/resources/sample.dot";

      System.out.println("Parsing graph from: " + inputPath);
      Graph graph = Graph.parseGraph(inputPath);

      System.out.println(graph);

      // Use the new DOTRenderer
      String outputPath = "output.dot";
      DOTRenderer dotRenderer = new DOTRenderer();
      dotRenderer.render(graph, outputPath);
      System.out.println("Graph output to: " + Paths.get(outputPath).toAbsolutePath());

      // Try the GraphvizRenderer too
      String pngOutputPath = "output.png";
      GraphvizRenderer pngRenderer = new GraphvizRenderer("png");
      pngRenderer.render(graph, pngOutputPath);
      System.out.println("PNG image output to: " + Paths.get(pngOutputPath).toAbsolutePath());

    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}