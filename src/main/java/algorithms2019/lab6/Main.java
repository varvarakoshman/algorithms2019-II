package algorithms2019.lab6;

import algorithms2019.lab6.part1.Edge;
import algorithms2019.lab6.part1.GraphI;
import algorithms2019.lab6.part2.GraphII;
import algorithms2019.lab6.part2.Vertex;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static algorithms2019.lab6.part1.GraphI.*;


public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        org.openjdk.jmh.Main.main(args);
        List<Integer> shortestPathDA = GraphI.dijkstraAlgorithm();
        System.out.printf("shortest path between %d and %d by DA is %s\n", source, target, shortestPathDA.toString());
        List<Edge> allEdges = GraphI.getAllEdges();
        Visualiser.drawGraph(GraphI.adjMatrix, allEdges);
        List<Integer> shortestPathBFA = GraphI.bellmanFordAlgorithm();
        System.out.printf("shortest path between %d and %d by BFA is %s", source, target, shortestPathBFA.toString());
        Optional<List<Vertex>> shortestPathAstar = GraphII.aStarAlgorithm();
        shortestPathAstar.ifPresent(result -> {
            List<String> stringList = result.stream()
                    .map(vertex -> String.format("(%d, %d)", vertex.getRow(), vertex.getColumn()))
                    .collect(Collectors.toList());
            System.out.printf("shortest path between (%d, %d) and (%d, %d) by A* is %s\n",
                    GraphII.source.getRow(), GraphII.source.getColumn(),
                    GraphII.target.getRow(), GraphII.target.getColumn(), stringList.toString());
            GraphII.printGrid(result);
        });
    }
}
