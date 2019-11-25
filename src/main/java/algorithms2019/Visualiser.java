package algorithms2019;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class Visualiser {
    public static void drawGraph(Map<Integer, List<Integer>> adjLists, HashSet<Edge> allEdges) {
        Graph graph = new MultiGraph("Visualiser");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.stylesheet", "url('file:style.css')");
        adjLists.keySet().forEach(vertex -> graph.addNode(vertex.toString()));
        PrimitiveIterator.OfInt iterator = IntStream.range(0, allEdges.size()).iterator();
        allEdges.forEach(edge -> graph.addEdge(Integer.toString(iterator.next()), edge.getFrom(), edge.getTo(), false));
        graph.forEach(node -> node.addAttribute("ui.label", node.getId()));
        graph.display();
    }
}
