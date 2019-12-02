package algorithms2019.lab6.part1;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
// NB logging should be commented when benchmarking

@Getter
@Slf4j
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class GraphI {
    public static final int UPPER_BOUND = 100; // upper bound for weights in a graph
    public static final int N_VERTICES = 100;
    public static final int N_EDGES = 4950;
    public static final Integer source = new Random().nextInt(N_VERTICES);
    public static final Integer target = new Random().nextInt(N_VERTICES);
    public static final Integer[][] adjMatrix = genRandomAdjMatrix(N_VERTICES, N_EDGES);

    @Benchmark
    public static List<Integer> dijkstraAlgorithm() {
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
        return unrollPath(predecessors);
    }

    private static List<Integer> unrollPath(List<Integer> predecessors) {
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

    private static List<Integer> getNeighbours(Integer vertex) {
        return IntStream.range(0, adjMatrix.length)
                .filter(otherVertex -> adjMatrix[vertex][otherVertex] != 0)
                .boxed()
                .collect(Collectors.toList());
    }

    @Benchmark
    public static List<Integer> bellmanFordAlgorithm() {
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
        return unrollPath(predecessors);
    }

    public static List<Edge> getAllEdges() {
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

    /*
       as graph in task is undirected, its adjacency matrix is symmetric
       and as it doesn't have any loops, its matrix has zeros on the main diagonal
   */
    private static Integer[][] genRandomAdjMatrix(int nVertices, int nEdges) {
        Integer[][] adjMatrix = new Integer[nVertices][nVertices];
        List<Integer> allCells = new ArrayList<>(Collections.nCopies(nVertices * nVertices / 2 - (nVertices / 2), 0));
        Random rnd = new Random();
        for (int i = 0; i < nEdges; i++) {
            allCells.set(i, rnd.nextInt(UPPER_BOUND));
        }
        for (int i = allCells.size() - 1; i > 0; i--) { // generate a random permutation
            Collections.swap(allCells, i, rnd.nextInt(i));
        }
        int currentIndex = 0;     // fill the adj adjMatrix with random permutation
        outer:
        for (int i = 0; i < nVertices - 1; i++) {
            adjMatrix[i][i] = 0;
            for (int j = i + 1; j < nVertices; j++) {
                if (i != j) {
                    adjMatrix[i][j] = allCells.get(currentIndex);
                    adjMatrix[j][i] = allCells.get(currentIndex);
                    currentIndex++;
                }
                if (currentIndex == allCells.size()) {
                    break outer;
                }
            }
        }
        adjMatrix[nVertices - 1][nVertices - 1] = 0;
        return adjMatrix;
    }
}
