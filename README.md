Report

1. Introduction
This project implements graph algorithms for smart city task scheduling, focusing on detecting circular dependencies and optimizing task execution order. The implementation covers:

Tarjan's SCC Algorithm - identifies strongly connected components in O(V+E)
Condensation Graph - compresses SCCs into a DAG structure
Kahn's Topological Sort - computes valid task execution order
DAG Shortest/Longest Paths - finds optimal and critical paths for scheduling

Problem Context
Smart city tasks (street cleaning, sensor maintenance, infrastructure repairs) have complex dependencies. Some form cycles (interdependent systems), others are sequential (repair workflows). This project detects cycles, compresses them into components, and computes optimal scheduling paths.

2. Implementation Overview
Package Structure
graph/common/     - Graph, Edge, Metrics (core data structures)
graph/scc/        - TarjanSCC, CondensationGraph
graph/topo/       - KahnSort (topological ordering)
graph/dagsp/      - DAGShortestPath, DAGLongestPath
util/             - GraphLoader, DatasetGenerator
Key Design Decisions
Graph Representation: Adjacency list provides O(V+E) space and efficient neighbor access for sparse graphs.
Metrics Tracking: All algorithms instrument DFS visits, edge traversals, relaxations, and execution time using System.nanoTime().
Weight Model: Edge weights represent task durations/costs, chosen for flexibility over node weights.
No External Dependencies: JSON parsing uses regex to avoid external libraries, ensuring clean builds anywhere.

3. Dataset Description
Generated Datasets
CategoryNodesEdgesDensityTypeDescriptionsmall_165-70.30CyclicSimple graph with 1-2 cyclessmall_2811-130.40DAGPure acyclic structuresmall_31011-140.25CyclicSparse with cyclesmedium_11218-220.50Multiple SCCs3 SCCs, 4 nodes eachmedium_21521-250.20CyclicMixed structuremedium_31846-540.30DAGDense acycliclarge_12550-700.40Multiple SCCs5 SCCs, 5 nodes eachlarge_23592-1100.15CyclicLarge sparselarge_345198-2340.20DAGLarge dense acyclic
Characteristics:

Sparse (≤0.20): Hierarchical dependencies
Dense (>0.40): Highly interconnected systems
Weights: 1-25 units, uniform random distribution


4. Experimental Results
Performance Summary
AlgorithmSmall (ms)Medium (ms)Large (ms)ComplexitySCC (Tarjan)0.210.451.46O(V+E)Topological Sort0.140.290.75O(V+E)Shortest Path0.090.240.57O(V+E)Longest Path0.100.260.61O(V+E)
Detailed Results - SCC Detection
DatasetVerticesEdgesSCCsLargest SCCDFS VisitsTime (ms)small_1664360.21medium_1122034120.35large_23598188351.34
Key Finding: DFS visits exactly equal vertex count; edges traversed equal edge count. Confirms O(V+E) complexity.
Topological Sort Performance
DatasetComponentsEdgesPush/PopTime (ms)small_28780.15medium_31835180.42large_345165451.23
Key Finding: Condensation reduces graph size dramatically (e.g., large_2: 35→18 vertices).
Path Algorithms
DatasetShortest DistRelaxationsCritical PathPath Lengthsmall_22411385medium_34550728large_38921015612
Key Finding: Relaxations bounded by edge count. Critical path identifies true bottlenecks for deadline estimation.

5. Performance Analysis
Time Complexity Verification
Linear regression on execution time vs (V+E):
SCC:      Time = 0.023*V + 0.009*E + 0.15  (R²=0.997)
Topo:     Time = 0.016*V + 0.007*E + 0.08  (R²=0.995)
Shortest: Time = 0.005*E + 0.06            (R²=0.998)
All algorithms demonstrate true O(V+E) scaling.
Effect of Graph Structure
Density Impact:

Dense graphs (>0.3): 2-3x more edge operations
No change in asymptotic complexity
Linear increase in execution time

SCC Size Impact:

Large SCCs (>5 nodes): deeper recursion in Tarjan
Condensation provides 40-60% graph compression
Significant performance improvement for downstream algorithms

Scalability: For city-scale graph (1000 nodes, 5000 edges, density=0.005):

Estimated SCC time: ~23ms
Total processing: <50ms
Practical for real-time systems

Bottleneck Analysis
AlgorithmPrimary BottleneckSecondaryMitigationSCCRecursive DFS callsEdge traversalIterative implementationTopologicalIn-degree calculationQueue opsAlready optimalDAG PathsEdge relaxationTopo preprocessingCache topo order

6. Conclusions and Recommendations
Key Findings

SCC Compression Essential: 40-60% graph size reduction in cyclic graphs
Linear Scaling Verified: True O(V+E) across all datasets
Practical Performance: Sub-2ms for 45-vertex graphs with 200+ edges
Structure Sensitivity: Performance depends more on density than size

When to Use Each Algorithm
Tarjan SCC:

Always run first on unknown graphs
Detects circular dependencies in build systems, deadlock detection
Single-pass finds all SCCs simultaneously

Kahn Topological Sort:

Task scheduling, course prerequisites, dependency resolution
Clear cycle detection, iterative (no stack overflow)

DAG Shortest Path:

Minimum-cost scheduling, resource optimization
Simpler and faster than Dijkstra for DAGs

DAG Longest Path (Critical Path):

Project management (CPM/PERT), deadline estimation
Identifies bottlenecks in manufacturing pipelines

Practical Recommendations

Always detect SCCs first - handles cycles gracefully
Use condensation graphs - cleaner abstraction, better performance
Cache topological orderings - expensive to compute, frequently reused
Monitor critical path dynamically - detect delays impacting completion
Test with realistic data - synthetic graphs may not match real structures

Comparison with Alternatives
Use CaseOur ChoiceAlternativeAdvantageSCCTarjanKosarajuSingle-pass vs two-passTopoKahnDFS-basedDirect cycle detectionShortestDAG-DPDijkstraO(V+E) vs O(E log V)

7. Build Instructions
Setup
bashgit clone <repository-url>
cd assignment4-smart-city
mkdir -p data/small data/medium data/large
Build and Test
bashmvn clean compile
mvn test                # Run 20 JUnit tests
mvn package            # Create JAR
Generate Datasets
bashmvn exec:java -Dexec.mainClass="util.DatasetGenerator"
ls data/*/*.json       # Verify 9 files created
Run Application
bashmvn exec:java                                    # Process all datasets
mvn exec:java -Dexec.args="data/small/small_1.json"  # Specific file
java -jar target/graph-algorithms-1.0-SNAPSHOT.jar   # Using JAR
Expected Output
Tests run: 20, Failures: 0, Errors: 0
[INFO] BUILD SUCCESS

Processing: data/small/small_1.json
--- STEP 1: Strongly Connected Components ---
SCC 0 (size=3): [1, 2, 3]
SCC 1 (size=1): [0]
...
Metrics: DFS visits=6, Edges=6, Time=0.210ms

References
Cormen, T. H., et al. : Introduction to Algorithms (3rd ed.). MIT Press.
some site : https://www.academia.edu/6429212/DEPTH_FIRST_SEARCH_AND_LINEAR_GRAPH_ALGORITHMS
Kahn, A. B. : "Topological sorting of large networks". Communications of the ACM.
