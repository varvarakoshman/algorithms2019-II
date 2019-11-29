package algorithms2019;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Main {
    private static final int N_VERTICES = 100;
    private static final int N_EDGES = 2000;
    private static final int source = new Random().nextInt(N_VERTICES);
    private static final int target = new Random().nextInt(N_VERTICES);
    private static final int UPPER_BOUND = 100; // upper bound for weights in a graph
    /*
        as graph in task is undirected, its adjacency matrix is symmetric
        and as it doesn't have any loops, its matrix has zeros on the main diagonal
    */
    public static Integer[][] genRandomAdjMatrix(int nVertices, int nEdges) {
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

    @SneakyThrows
    public static void main(String[] args) {
        org.openjdk.jmh.Main.main(args);
        Graph graph = new Graph(genRandomAdjMatrix(N_VERTICES, N_EDGES));
        List<Integer> shortestPathDA = graph.dijkstraAlgorithm(source, target);
        System.out.printf("shortest path between %d and %d by DA is %s\n", source, target, shortestPathDA.toString());
        List<Edge> allEdges = graph.getAllEdges();
        Visualiser.drawGraph(graph.getAdjMatrix(), allEdges);
        List<Integer> shortestPathBFA = graph.bellmanFordAlgorithm(source, target);
        System.out.printf("shortest path between %d and %d by BFA is %s", source, target, shortestPathBFA.toString());
    }
}
