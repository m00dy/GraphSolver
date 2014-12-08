package edu.project.maths.graph.graphsolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.SimpleGraph;

public class NetworkGraph<V, E extends NetworkLink> extends SimpleGraph {

    public NetworkGraph(Class edgeClass) {
        super(edgeClass);
        // TODO Auto-generated constructor stub
    }

    public DijkstraShortestPath<String, NetworkLink> getCapacitadedShortestPath(String origin, String destination, int bmin) {

        NetworkGraph<V, NetworkLink> clonedGraph = (NetworkGraph<V, NetworkLink>) this.clone();

        Set<NetworkLink> edges = clonedGraph.edgeSet();
        Set<NetworkLink> edgesToRemove = new HashSet<NetworkLink>();

        for (NetworkLink edge : edges) {
            if (edge.getFreeSlots() < bmin) {
                edgesToRemove.add(edge);
            }
        }

        clonedGraph.removeAllEdges(edgesToRemove);

        DijkstraShortestPath<String, NetworkLink> path = new DijkstraShortestPath<String, NetworkLink>(clonedGraph, origin, destination);

        return path;
    }

    List<GraphPath<String, NetworkLink>> getKShortestPaths(String orign, String destination) {
        KShortestPaths<String, NetworkLink> kShortestPaths = new KShortestPaths<String, NetworkLink>(this, orign, 5);
        List<GraphPath<String, NetworkLink>> paths = kShortestPaths.getPaths(destination);
        return paths;
    }

}
