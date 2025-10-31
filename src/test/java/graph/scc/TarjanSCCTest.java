package graph.scc;

import graph.common.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TarjanSCCTest {

    @Test
    void testSimpleCycle() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
    }

    @Test
    void testMultipleSCCs() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(3, 4);
        graph.addEdge(4, 3);
        graph.addEdge(2, 3);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(2, sccs.size());
    }

    @Test
    void testDAG() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(3, sccs.size());
        for (List<Integer> scc : sccs) {
            assertEquals(1, scc.size());
        }
    }

    @Test
    void testDisconnectedGraph() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1);
        graph.addEdge(2, 3);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(4, sccs.size());
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());
    }

    @Test
    void testComplexGraph() {
        Graph graph = new Graph(8, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 1, 1);
        graph.addEdge(4, 5, 2);
        graph.addEdge(5, 6, 5);
        graph.addEdge(6, 7, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertTrue(sccs.size() >= 5);

        int[] componentIds = tarjan.getComponentIds();
        assertEquals(componentIds[1], componentIds[2]);
        assertEquals(componentIds[2], componentIds[3]);
    }

    @Test
    void testSelfLoop() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 0);
        graph.addEdge(1, 2);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(3, sccs.size());
    }
}