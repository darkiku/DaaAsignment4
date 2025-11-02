package graph.dagsp;

import graph.common.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathTest {

    @Test
    void testShortestPathSimpleDAG() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);
        graph.addEdge(0, 2, 10);

        DAGShortestPath sp = new DAGShortestPath(graph);
        int[] dist = sp.findShortestPaths(0);

        assertEquals(0, dist[0]);
        assertEquals(5, dist[1]);
        assertEquals(8, dist[2]);
    }

    @Test
    void testShortestPathWithUnreachable() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);

        DAGShortestPath sp = new DAGShortestPath(graph);
        int[] dist = sp.findShortestPaths(0);

        assertEquals(0, dist[0]);
        assertEquals(5, dist[1]);
        assertEquals(Integer.MAX_VALUE, dist[2]);
    }

    @Test
    void testPathReconstruction() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 1);

        DAGShortestPath sp = new DAGShortestPath(graph);
        sp.findShortestPaths(0);

        List<Integer> path = sp.getPath(3);
        assertNotNull(path);
        assertEquals(List.of(0, 1, 2, 3), path);
    }

    @Test
    void testLongestPathSimpleDAG() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);
        graph.addEdge(0, 2, 2);

        DAGLongestPath lp = new DAGLongestPath(graph);
        int[] dist = lp.findLongestPaths(0);

        assertEquals(0, dist[0]);
        assertEquals(5, dist[1]);
        assertEquals(8, dist[2]);
    }

    @Test
    void testCriticalPath() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 2);

        DAGLongestPath lp = new DAGLongestPath(graph);
        DAGLongestPath.CriticalPathResult result = lp.findCriticalPath(0);

        assertNotNull(result);
        assertEquals(9, result.getLength());
        assertNotNull(result.getPath());
        assertTrue(result.getPath().size() > 0);
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true);

        DAGShortestPath sp = new DAGShortestPath(graph);
        int[] dist = sp.findShortestPaths(0);

        assertEquals(0, dist[0]);
    }

    @Test
    void testNonDAGThrowsException() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        DAGShortestPath sp = new DAGShortestPath(graph);

        assertThrows(IllegalArgumentException.class, () -> {
            sp.findShortestPaths(0);
        });
    }
}