package graphSolver;

import java.util.Iterator;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleGraph;

import graphSolver.NetworkLink;

public class NetworkGraph<V,E extends NetworkLink> extends SimpleGraph {

	
	public NetworkGraph(Class edgeClass) {
		super(edgeClass);
		// TODO Auto-generated constructor stub
	}

	public DijkstraShortestPath<String, NetworkLink> getCapacitadedShortestPath(String origin,String destination,int bmin){
		
		NetworkGraph<V, NetworkLink> clonedGraph = (NetworkGraph<V, NetworkLink>) this.clone();
		
		Set<NetworkLink> edges = clonedGraph.edgeSet();
		
		for (Iterator<NetworkLink> iterator = edges.iterator(); iterator
				.hasNext();) {
			NetworkLink edge = iterator.next();
			
			if (edge.getFreeSlots() < bmin)
				clonedGraph.removeEdge(edge);
		}
		
		DijkstraShortestPath<String, NetworkLink> path = new DijkstraShortestPath<String, NetworkLink>(clonedGraph, origin, destination);

		
		return path;
	}


}
