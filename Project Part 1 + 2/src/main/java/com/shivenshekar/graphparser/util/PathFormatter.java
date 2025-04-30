package com.shivenshekar.graphparser.util;

import com.shivenshekar.graphparser.core.Path;
import java.util.List;

/**
 * Utility class for formatting Path objects in different display formats
 */
public class PathFormatter {

    /**
     * Format a path as a simple string with arrow notation
     *
     * @param path The path to format
     * @return Formatted string (e.g., "A->B->C")
     */
    public static String formatSimple(Path path) {
        if (path == null || path.isEmpty()) {
            return "Empty path";
        }

        List<String> nodes = path.getNodes();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nodes.size(); i++) {
            sb.append(nodes.get(i));
            if (i < nodes.size() - 1) {
                sb.append("->");
            }
        }

        return sb.toString();
    }

    /**
     * Format a path according to Scheme A format
     * Path{nodes=[Node{A}, Node{B}, Node{C}]}
     *
     * @param path The path to format
     * @return Formatted string in Scheme A format
     */
    public static String formatSchemeA(Path path) {
        if (path == null || path.isEmpty()) {
            return "Path{nodes=[]}";
        }

        StringBuilder sb = new StringBuilder("Path{nodes=[");

        List<String> nodes = path.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            sb.append("Node{").append(nodes.get(i)).append("}");
            if (i < nodes.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}");
        return sb.toString();
    }

    /**
     * Compare two paths for equality based on their nodes
     *
     * @param path1 First path
     * @param path2 Second path
     * @return true if paths contain the same sequence of nodes
     */
    public static boolean areEqual(Path path1, Path path2) {
        if (path1 == null || path2 == null) {
            return path1 == path2; // Both null or one is null
        }

        if (path1.getLength() != path2.getLength()) {
            return false;
        }

        List<String> nodes1 = path1.getNodes();
        List<String> nodes2 = path2.getNodes();

        return nodes1.equals(nodes2);
    }
}