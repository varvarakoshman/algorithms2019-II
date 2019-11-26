package algorithms2019;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    public List<Integer> dijkstraAlgorithm(Integer source, Integer target) {
        if (source.equals(target)) {
            return Collections.emptyList();
        }
        List<Integer> sptSet = new ArrayList<>();
        List<Integer> notSptSet = new ArrayList<>();
        IntStream.range(0, adjMatrix.length).forEach(notSptSet::add);
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(adjMatrix.length, -1));
        List<Integer> distances = new ArrayList<>(Collections.nCopies(adjMatrix.length, Integer.MAX_VALUE));
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
        log.info(String.format("minimum distance from %d to %d is %d", source, target, distances.get(target)));
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

    // TODO: 11/26/2019 complete BFA
    public List<Integer> bellmanFordAlgorithm(Integer source, Integer target) {
        List<Integer> distances = new ArrayList<>(Collections.nCopies(adjMatrix.length, Integer.MAX_VALUE));
        distances.set(source, 0); //set dist(source, source)=0
        //...
        return null;
    }

    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < adjMatrix.length - 1; i++) {
            for (int j = i + 1; j < adjMatrix.length; j++) {
                if (adjMatrix[i][j] != 0){
                    edges.add(new Edge(i, j, adjMatrix[i][j]));
                }
            }
        }
        return edges;
    }
}
