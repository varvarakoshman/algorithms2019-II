import algorithms2019.Graph;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;


@RunWith(JUnit4.class)
public class GraphTest {
    private static final int N_VERTICES = 6;

    private static Graph graph;

    @BeforeClass
    public static void init() {
        Integer[][] adjMatrix = new Integer[N_VERTICES][N_VERTICES];
        for (int i = 0; i < N_VERTICES; i++) {
            for (int j = 0; j < N_VERTICES; j++) {
                adjMatrix[i][j] = 0;
            }
        }
        adjMatrix[0][4] = 20;
        adjMatrix[4][0] = 20;
        adjMatrix[2][4] = 20;
        adjMatrix[4][2] = 20;
        adjMatrix[0][3] = 7;
        adjMatrix[3][0] = 7;
        adjMatrix[1][4] = 98;
        adjMatrix[4][1] = 98;
        adjMatrix[2][3] = 87;
        adjMatrix[3][2] = 87;
        adjMatrix[3][4] = 83;
        adjMatrix[4][3] = 83;
        adjMatrix[4][5] = 17;
        adjMatrix[5][4] = 17;
        graph = new Graph(adjMatrix);
    }

    @Test
    public void dijkstraAlgorithmTest() {
        Assert.assertEquals(graph.dijkstraAlgorithm(5, 3), Arrays.asList(5, 4, 0, 3));
        Assert.assertEquals(graph.dijkstraAlgorithm(0, 4), Arrays.asList(0, 4));
        Assert.assertEquals(graph.dijkstraAlgorithm(2, 5), Arrays.asList(2, 4, 5));
    }

    @Test
    public void bellmanFordAlgorithmTest() {
        Assert.assertEquals(graph.bellmanFordAlgorithm(5, 3), Arrays.asList(5, 4, 0, 3));
        Assert.assertEquals(graph.bellmanFordAlgorithm(0, 4), Arrays.asList(0, 4));
        Assert.assertEquals(graph.bellmanFordAlgorithm(2, 5), Arrays.asList(2, 4, 5));
    }
}
