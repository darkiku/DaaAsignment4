package graph.common;

public class Edge {
    private final int from;
    private final int to;
    private final int weight;

    public Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Edge(int from, int to) {
        this(from, to, 1);
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("%d -> %d (w=%d)", from, to, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return from == edge.from && to == edge.to && weight == edge.weight;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        result = 31 * result + weight;
        return result;
    }
}