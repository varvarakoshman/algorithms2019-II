package algorithms2019;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@AllArgsConstructor
@Slf4j
public class Graph {
    private Integer[][] adjMatrix;

    /**
     * finds shortest paths from source vertex to all others, returns the one to one target (picked randomly)
     *
     * @param source
     * @param target
     * @return shortest path from source to target
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 10, warmups = 2)
    public List<Integer> dijkstraAlgorithm(final Integer source, final Integer target) {
        if (source.equals(target)) {
            return Collections.emptyList();
        }
        List<Integer> sptSet = new ArrayList<>();
        List<Integer> notSptSet = new ArrayList<>();
        IntStream.range(0, adjMatrix.length).forEach(notSptSet::add);
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(adjMatrix.length, -1));
        List<Integer> distances = new ArrayList<>(Collections.nCopies(adjMatrix.length, Integer.MAX_VALUE / 2));
        distances.set(source, 0); //set dist(source, source)=0
        while (sptSet.size() != adjMatrix.length) {
            Integer newVertex = notSptSet.stream()
                    .min(Comparator.comparingInt(distances::get)).get(); //find new vertex from notSptSet, so that distance from source to it is min
            sptSet.add(newVertex);
            notSptSet.remove(newVertex);
            List<Integer> neighbours = getNeighbours(newVertex);
            neighbours.forEach(neighbour -> {
                if (distances.get(newVertex) + adjMatrix[newVertex][neighbour] < distances.get(neighbour)) {
                    distances.set(neighbour, distances.get(newVertex) + adjMatrix[newVertex][neighbour]);
                    predecessors.set(neighbour, newVertex);
                }
            });
        }
        log.info(String.format("minimum distance from %d to %d with DA is %d", source, target, distances.get(target)));
        return unrollPath(predecessors, target);
    }

    private List<Integer> unrollPath(List<Integer> predecessors, Integer target) {
        List<Integer> path = new ArrayList<>();
        Integer current = target;
        path.add(current);
        while (predecessors.get(current) != -1) {
            Integer pred = predecessors.get(current);
            path.add(pred);
            current = pred;
        }
        Collections.reverse(path);
        return path;
    }

    private List<Integer> getNeighbours(Integer vertex) {
        return IntStream.range(0, adjMatrix.length)
                .filter(otherVertex -> adjMatrix[vertex][otherVertex] != 0)
                .boxed()
                .collect(Collectors.toList());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 10, warmups = 2)
    public List<Integer> bellmanFordAlgorithm(Integer source, Integer target) {
        List<Integer> distances = new ArrayList<>(Collections.nCopies(adjMatrix.length, Integer.MAX_VALUE / 2));
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(adjMatrix.length, -1));
        distances.set(source, 0); //set dist(source, source)=0
        List<Edge> allEdges = getAllEdges();
        for (int i = 0; i < adjMatrix.length - 1; i++) {
            for (Edge edge : allEdges) {
                if (distances.get(edge.getFrom()) + edge.getWeight() < distances.get(edge.getTo())) {
                    distances.set(edge.getTo(), distances.get(edge.getFrom()) + edge.getWeight());
                    predecessors.set(edge.getTo(), edge.getFrom());
                }
                // as graph is undirected, so check paths an edge in both directions
                if (distances.get(edge.getTo()) + edge.getWeight() < distances.get(edge.getFrom())) {
                    distances.set(edge.getFrom(), distances.get(edge.getTo()) + edge.getWeight());
                    predecessors.set(edge.getFrom(), edge.getTo());
                }
            }
        }
        for (Edge edge : allEdges) { //the last walk through to find negative cycle
            if (distances.get(edge.getFrom()) + edge.getWeight() < distances.get(edge.getTo())) {
                log.warn("Negative weight cycle found");
            }
        }
        log.info(String.format("minimum distance from %d to %d with BFA is %d", source, target, distances.get(target)));
        return unrollPath(predecessors, target);
    }

    // for visualization purposes
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < adjMatrix.length - 1; i++) {
            for (int j = i + 1; j < adjMatrix.length; j++) {
                if (adjMatrix[i][j] != 0) {
                    edges.add(new Edge(i, j, adjMatrix[i][j]));
                }
            }
        }
        return edges;
    }
}
