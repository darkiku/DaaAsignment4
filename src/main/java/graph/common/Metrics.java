package graph.common;

public class Metrics {
    private long dfsVisits = 0;
    private long edgesTraversed = 0;
    private long relaxations = 0;
    private long pushOperations = 0;
    private long popOperations = 0;
    private long startTime = 0;
    private long endTime = 0;

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedNanos() {
        return endTime - startTime;
    }

    public double getElapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public void incrementDfsVisits() {
        dfsVisits++;
    }

    public void incrementEdgesTraversed() {
        edgesTraversed++;
    }

    public void incrementRelaxations() {
        relaxations++;
    }

    public void incrementPushOperations() {
        pushOperations++;
    }

    public void incrementPopOperations() {
        popOperations++;
    }

    public long getDfsVisits() {
        return dfsVisits;
    }

    public long getEdgesTraversed() {
        return edgesTraversed;
    }

    public long getRelaxations() {
        return relaxations;
    }

    public long getPushOperations() {
        return pushOperations;
    }

    public long getPopOperations() {
        return popOperations;
    }

    public void reset() {
        dfsVisits = 0;
        edgesTraversed = 0;
        relaxations = 0;
        pushOperations = 0;
        popOperations = 0;
        startTime = 0;
        endTime = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Metrics: DFS visits=%d, Edges=%d, Relaxations=%d, Push=%d, Pop=%d, Time=%.3fms",
                dfsVisits, edgesTraversed, relaxations, pushOperations, popOperations, getElapsedMillis()
        );
    }
}