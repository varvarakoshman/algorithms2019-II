package algorithms2019;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Slf4j
public class Main {

    @SneakyThrows
    public static void displayAdjMatrix(Integer[][] randomAdjMatrix) {
        File file = new File("./src/main/resources/adjMatrix.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Integer[] adjMatrix : randomAdjMatrix) {
            bufferedWriter.write(("|"));
            for (int j = 0; j < randomAdjMatrix.length; j++) {
                bufferedWriter.write(adjMatrix[j].toString().concat(" "));
            }
            bufferedWriter.write("|\n");
        }
        bufferedWriter.close();
    }

    @SneakyThrows
    public static void displayAdjList(Map<Integer, List<Integer>> adjLists) {
        FileWriter writer = new FileWriter(new File("./src/main/resources/adjList.txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        adjLists.forEach((k, v) -> {
            try {
                bufferedWriter.write(k.toString().concat(" => "));
                bufferedWriter.write(String.format("%s\n", String.join(",", v.toString())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bufferedWriter.close();
    }

    public static void main(String[] args) {
        UndirectedGraph undirectedGraph = new UndirectedGraph(100, 2000);
        Integer[][] randomAdjMatrix = undirectedGraph.genRandomAdjMatrix();
        displayAdjMatrix(randomAdjMatrix);
        log.info("generated adjacency matrix is written to adjMatrix.txt");
        Map<Integer, List<Integer>> adjList = undirectedGraph.convertMatrixToLists(randomAdjMatrix);
        undirectedGraph.setAdjList(adjList);
        displayAdjList(adjList);
        log.info("converted adjacency matrix into adjacency list is written to adjList.txt");
        List<List<Integer>> components = undirectedGraph.findComponents();
        System.out.println("# of components found in graph: " + components.size());
        components.forEach(System.out::println);
        Random rnd = new Random();
        Integer randVertex1 = rnd.nextInt(adjList.size());
        Integer randVertex2 = rnd.nextInt(adjList.size());
        Optional<List<Integer>> shortestPath = undirectedGraph.findShortestPath(randVertex1, randVertex2);
        shortestPath.ifPresent(path -> System.out.printf("Shortest path found with bfs from %d to %d is %s",
                randVertex1, randVertex2, String.join(" =>", path.toString().split(","))));
        Visualiser.drawGraph(adjList, undirectedGraph.getAllEdges(adjList));
    }
}
