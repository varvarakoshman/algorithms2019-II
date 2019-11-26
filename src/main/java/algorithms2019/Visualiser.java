package algorithms2019;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/*
class needed for visualisation
 */
public class Visualiser {
    public static void drawGraph(Integer[][] adjMatrix, List<Edge> allEdges) {
        Graph graph = new MultiGraph("Visualiser");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.stylesheet", "url('file:style.css')");
        IntStream.range(0, adjMatrix.length).boxed()
                .collect(Collectors.toList())
                .forEach(vertex -> graph.addNode(vertex.toString()));
        PrimitiveIterator.OfInt iterator = IntStream.range(0, allEdges.size()).iterator();
        allEdges.forEach(edge -> graph.addEdge(iterator.next().toString(), edge.getFrom(), edge.getTo(), false));
        graph.getEachEdge().forEach(edge -> edge.setAttribute("weight", allEdges.stream()
                .filter(e -> e.getFrom().toString().equals(edge.getSourceNode().getId()) &&
                        e.getTo().toString().equals(edge.getTargetNode().getId()))
                .map(Edge::getWeight)
                .findFirst().get()));
        graph.getEachEdge().forEach(edge -> edge.addAttribute("ui.label", edge.getAttribute("weight").toString()));
        graph.forEach(node -> node.addAttribute("ui.label", node.getId()));
        graph.display();
    }
}
