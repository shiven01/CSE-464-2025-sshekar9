package com.shivenshekar.graphparser;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) {
    try {
      String inputPath = "src/main/resources/sample.dot";

      System.out.println("Parsing graph from: " + inputPath);
      Graph graph = Graph.parseGraph(inputPath);

      System.out.println(graph);

      String outputPath = "output.dot";
      graph.outputGraph(outputPath);
      System.out.println("Graph output to: " + Paths.get(outputPath).toAbsolutePath());
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
