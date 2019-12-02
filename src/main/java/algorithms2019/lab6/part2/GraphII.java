package algorithms2019.lab6.part2;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class GraphII {
    private static final int N_CELLS = 10;
    private static final int N_BARRIERS = 30;
    private static final Random rnd = new Random();

    public static final Integer[][] adjMatrix = genRandomGrid();
    public static final List<Vertex> allVertices = getAllVertices();

    public static final Vertex source = pickRandomVertex();
    public static final Vertex target = pickRandomVertex();

    @Benchmark
    public static Optional<List<Vertex>> aStarAlgorithm() {
        Map<Vertex, Vertex> predecessors = new HashMap<>();
        allVertices.forEach(vertex -> predecessors.put(vertex, null));
        List<Vertex> sptSet = new ArrayList<>();// visited (U)
        List<Vertex> notSptSet = new ArrayList<>(); //to be visited (Q)
        notSptSet.add(source);
        source.setG(0); //set dist(source, source)=0
        source.setF(source.getG() + h(source));
        while (!notSptSet.isEmpty()) {
            Vertex current = getNextVertex(notSptSet);
            if (target.equals(current)) {
                return Optional.of(unrollPath(predecessors));
            }
            notSptSet.remove(current);
            sptSet.add(current);
            List<Vertex> successors = getSuccessors(current);
            for (Vertex v : successors) {
                int tentativeScore = current.getG() + v.getCost();
                if (!sptSet.contains(v) || tentativeScore < v.getG()) {
                    predecessors.put(v, current);
                    v.setG(tentativeScore);
                    v.setF(v.getG() + h(v));
                    if (!notSptSet.contains(v)) {
                        notSptSet.add(v);
                    }
                }
            }
        }
        return Optional.empty();
    }
    /*
    Euclidean distance is an approximation heuristic
     */
    private static double h(Vertex vertex) {
        return Math.sqrt(Math.pow(vertex.getRow() - target.getRow(), 2)
                + Math.pow(vertex.getColumn() - target.getColumn(), 2));
    }

    /*
    assuming we can move in 8 directions (we're going to use Euclidean distance, if Manhattan was chosen,
    then it would be only 4), this function returns a list of possible Vertex-s to go next excluding barriers.
     */
    private static List<Vertex> getSuccessors(Vertex vertex) {
        Set<Vertex> neighbours = new HashSet<>();
        int row = vertex.getRow();
        int col = vertex.getColumn();
        if (row == N_CELLS - 2 || col == N_CELLS - 2 || row == 0 || col == 0) {
            if (row + 1 <= N_CELLS - 1) {
                getVertexByij(row + 1, col).ifPresent(neighbours::add);
                if (col + 1 <= N_CELLS - 1) {
                    getVertexByij(row + 1, col + 1).ifPresent(neighbours::add);
                    getVertexByij(row, col + 1).ifPresent(neighbours::add);
                }
                if (col - 1 >= 0) {
                    getVertexByij(row + 1, col - 1).ifPresent(neighbours::add);
                    getVertexByij(row, col - 1).ifPresent(neighbours::add);
                }
            }
            if (row - 1 >= 0) {
                getVertexByij(row - 1, col).ifPresent(neighbours::add);
                if (col + 1 <= N_CELLS - 1) {
                    getVertexByij(row - 1, col + 1).ifPresent(neighbours::add);
                    getVertexByij(row, col + 1).ifPresent(neighbours::add);
                }
                if (col - 1 >= 0) {
                    getVertexByij(row - 1, col - 1).ifPresent(neighbours::add);
                    getVertexByij(row, col - 1).ifPresent(neighbours::add);
                }
            }
        } else {
            getVertexByij(row, col + 1).ifPresent(neighbours::add);
            getVertexByij(row, col - 1).ifPresent(neighbours::add);
            getVertexByij(row + 1, col).ifPresent(neighbours::add);
            getVertexByij(row - 1, col).ifPresent(neighbours::add);
            getVertexByij(row + 1, col + 1).ifPresent(neighbours::add);
            getVertexByij(row - 1, col + 1).ifPresent(neighbours::add);
            getVertexByij(row - 1, col - 1).ifPresent(neighbours::add);
            getVertexByij(row + 1, col - 1).ifPresent(neighbours::add);
        }
        return new ArrayList<>(neighbours);
    }

    /*
    get a reversed list of found path
     */
    private static List<Vertex> unrollPath(Map<Vertex, Vertex> predecessors) {
        List<Vertex> path = new ArrayList<>();
        Vertex current = target;
        path.add(current);
        while (predecessors.get(current) != null) {
            Vertex pred = predecessors.get(current);
            path.add(pred);
            current = pred;
        }
        Collections.reverse(path);
        return path;
    }

    /*
    choose next vertex from not visited yet with the smallest value of heuristic f
     */
    private static Vertex getNextVertex(List<Vertex> notSptSet) {
        return notSptSet.stream()
                .min(Comparator.comparingDouble(Vertex::getF))
                .get();
    }

    /*
    get a Vertex object from list of all vertices by square's position (i,j)
     */
    private static Optional<Vertex> getVertexByij(int row, int col) {
        return allVertices.stream()
                .filter(v -> v.getRow() == row && v.getColumn() == col)
                .filter(v -> Objects.nonNull(v.getCost()))
                .findFirst();
    }
    /*
    function that picks random available vertex, which is not barrier
     */
    private static Vertex pickRandomVertex() {
        List<Vertex> vertices = allVertices.stream()
                .filter(vertex -> Objects.nonNull(vertex.getCost()))
                .collect(Collectors.toList());
        return vertices.get(rnd.nextInt(vertices.size()));
    }

    /*
    function to get a list of all existing vertices
     */
    private static List<Vertex> getAllVertices() {
        List<Vertex> allVertices = new ArrayList<>();
        for (int i = 0; i < N_CELLS; i++) {
            for (int j = 0; j < N_CELLS; j++) {
                allVertices.add(new Vertex(i, j, adjMatrix[i][j]));
            }
        }
        return allVertices;
    }

    /*
    generate a grid of a given size and fill it with ones and nulls(with are considered as barriers)
     */
    private static Integer[][] genRandomGrid() {
        Integer[][] adjMatrix = new Integer[N_CELLS][N_CELLS];
        List<Integer> allCells = new ArrayList<>(Collections.nCopies(N_CELLS * N_CELLS, 1));
        for (int i = 0; i < N_BARRIERS; i++) {
            allCells.set(i, null); // fill with barriers (barrier here is null-cell)
        }
        for (int i = allCells.size() - 1; i > 0; i--) { // generate a random permutation
            Collections.swap(allCells, i, rnd.nextInt(i));
        }
        int currentIndex = 0;
        for (int i = 0; i < N_CELLS; i++) {
            for (int j = 0; j < N_CELLS; j++) {
                adjMatrix[i][j] = allCells.get(currentIndex);
                currentIndex++;
            }
        }
        return adjMatrix;
    }

    /*
    function for printing out found path visually understandable
     */
    public static void printGrid(List<Vertex> path){
        for (int i = 0; i < N_CELLS; i++) {
            System.out.print("|");
            for (int j = 0; j < N_CELLS; j++) {
                if (adjMatrix[i][j] == null){
                    System.out.printf("%s|", adjMatrix[i][j]);
                }else if (path.contains(getVertexByij(i, j).get())){
                    System.out.print("  * |");
                }else{
                    System.out.print("    |");
                }
            }
            System.out.println();
        }
    }
}
