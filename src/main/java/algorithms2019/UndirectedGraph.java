package algorithms2019;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Slf4j
public class UndirectedGraph {
    private int nVertices;
    private int nEdges;
    @Setter
    private Map<Integer, List<Integer>> adjList;

    public UndirectedGraph(int nVertices, int nEdges) {
        this.nVertices = nVertices;
        this.nEdges = nEdges;
    }

    private static List<Integer> apply(List<Integer> list) {
        int index = 0;
        List<Integer> indices = new ArrayList<>();
        for (Integer element : list) {
            if (element == 1) {
                indices.add(index);
            }
            index++;
        }
        return indices;
    }

    /*
    as graph in task is undirected, its adjacency matrix is symmetric
    and as it doesn't have any loops, its matrix has zeros on the main diagonal
     */
    public Integer[][] genRandomAdjMatrix() {
        Integer[][] adjMatrix = new Integer[nVertices][nVertices];
        List<Integer> allCells = new ArrayList<>(Collections.nCopies(nVertices * nVertices / 2 - (nVertices / 2), 0));
        for (int i = 0; i < nEdges; i++) {
            allCells.set(i, 1);
        }
        Random rnd = new Random(); // generate a random permutation of 2000 elements in 4950
        for (int i = allCells.size() - 1; i > 0; i--) {
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

    public Map<Integer, List<Integer>> convertMatrixToLists(Integer[][] adjMatrix) {
        List<List<Integer>> adjMatrixList = new ArrayList<>();
        for (Integer[] matrix : adjMatrix) {
            adjMatrixList.add(Arrays.asList(matrix));
        }
        PrimitiveIterator.OfInt iterator = IntStream.range(0, adjMatrixList.size()).iterator();
        return adjMatrixList.stream()
                .map(UndirectedGraph::apply)
                .collect(Collectors.toMap(obj -> iterator.next(), Function.identity()));
    }

    public HashSet<Edge> getAllEdges(Map<Integer, List<Integer>> adjList) {
        List<Edge> edges = new ArrayList<>();
        adjList.forEach((from, tos) -> tos.forEach(to -> edges.add(new Edge(from, to))));
        return new HashSet<>(edges);
    }

    public List<List<Integer>> findComponents() {
        List<List<Integer>> listOfComponents = new ArrayList<>();
        List<Integer> component;
        List<Boolean> visited = new ArrayList<>(Collections.nCopies(adjList.keySet().size(), false));
        for (Integer vertex : adjList.keySet()) {
            if (!visited.get(vertex)) {
                component = dfs(vertex, visited);
                listOfComponents.add(component);
            }
        }
        return listOfComponents;
    }

    public Optional<List<Integer>> findShortestPath(Integer source, Integer target) {
        List<Boolean> visited = new ArrayList<>(Collections.nCopies(adjList.keySet().size(), false));
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(adjList.keySet().size(), -1));
        if (bfs(source, target, visited, predecessors)) {
            return Optional.of(unrollPath(predecessors, source, target));
        } else {
            log.debug(String.format("SORRY! Given vertices (%d and %d) are not connected", source, target));
            return Optional.empty();
        }
    }

    private List<Integer> unrollPath(List<Integer> predecessors, Integer source, Integer target) {
        List<Integer> path = new ArrayList<>();
        Integer current = target;
        path.add(current);
        while (!current.equals(source)) {
            path.add(predecessors.get(current));
            current = predecessors.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private boolean bfs(Integer source, Integer target, List<Boolean> visited, List<Integer> predecessors) {
        LinkedList<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visited.set(source, true);
        while (!queue.isEmpty()) {
            Integer current = queue.pollFirst();
            List<Integer> neighbours = adjList.get(current);
            for (Integer neighbour : neighbours) {
                if (!visited.get(neighbour)) {
                    predecessors.set(neighbour, current);
                    queue.offer(neighbour);
                    visited.set(neighbour, true);
                    if (neighbour.equals(target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<Integer> dfs(Integer startVertex, List<Boolean> visited) {
        Random rnd = new Random();
        List<Integer> verticesInComponent = new ArrayList<>();
        LinkedList<Integer> stack = new LinkedList<>();
        stack.push(startVertex);
        verticesInComponent.add(startVertex);
        Integer topVertex;
        while (!stack.isEmpty()) {
            topVertex = stack.peek();
            visited.set(topVertex, true);
            List<Integer> unvisitedNeighbours = getUnvisitedNeighbours(topVertex, visited);
            if (unvisitedNeighbours.isEmpty()) {
                stack.pop();
            } else {
                Integer nextVertex = unvisitedNeighbours.get(rnd.nextInt(unvisitedNeighbours.size()));
                stack.push(nextVertex);
                verticesInComponent.add(nextVertex);
            }
        }
        return verticesInComponent;
    }

    private List<Integer> getUnvisitedNeighbours(Integer vertex, List<Boolean> visited) {
        return adjList.get(vertex).stream()
                .filter(index -> !visited.get(index))
                .collect(Collectors.toList());
    }
}