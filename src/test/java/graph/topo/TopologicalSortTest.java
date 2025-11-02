package graph.topo;

import graph.common.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        KahnSort kahn = new KahnSort(graph);
        List<Integer> order = kahn.sort();

        assertNotNull(order);
        assertEquals(3, order.size());

        int pos0 = order.indexOf(0);
        int pos1 = order.indexOf(1);
        int pos2 = order.indexOf(2);

        assertTrue(pos0 < pos1);
        assertTrue(pos1 < pos2);
    }

    @Test
    void testDAGWithMultiplePaths() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);

        KahnSort kahn = new KahnSort(graph);
        List<Integer> order = kahn.sort();

        assertNotNull(order);
        assertEquals(4, order.size());

        assertEquals(0, order.get(0));
        assertEquals(3, order.get(3));
    }

    @Test
    void testCycleDetection() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        KahnSort kahn = new KahnSort(graph);
        List<Integer> order = kahn.sort();

        assertNull(order);
        assertFalse(kahn.isDAG());
    }

    @Test
    void testDisconnectedDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1);
        graph.addEdge(2, 3);

        KahnSort kahn = new KahnSort(graph);
        List<Integer> order = kahn.sort();

        assertNotNull(order);
        assertEquals(4, order.size());

        int pos0 = order.indexOf(0);
        int pos1 = order.indexOf(1);
        int pos2 = order.indexOf(2);
        int pos3 = order.indexOf(3);

        assertTrue(pos0 < pos1);
        assertTrue(pos2 < pos3);
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true);

        KahnSort kahn = new KahnSort(graph);
        List<Integer> order = kahn.sort();

        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, order.get(0));
    }

    @Test
    void testEmptyGraph() {
        Graph graph = new Graph(3, true);

        KahnSort kahn = new KahnSort(graph);
        List<Integer> order = kahn.sort();

        assertNotNull(order);
        assertEquals(3, order.size());
    }
}