package graphSolver;

import org.jgrapht.*;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphSolver {

	public static void main(String[] argv){
		
		System.out.println("Fuck me! I'm famous");
		
		NetworkGraph<String, NetworkLink> graph = createStringGraph();
		
//		DijkstraShortestPath<String, NetworkLink> path = new DijkstraShortestPath<String, NetworkLink>(graph, "a", "c");
		DijkstraShortestPath<String, NetworkLink> path = graph.getCapacitadedShortestPath("a", "c", 5);
		System.out.println(path.getPath());
		
		//System.out.println(graph.toString());
		
	}
	
	 private static NetworkGraph<String, NetworkLink> createStringGraph()
	    {
	        NetworkGraph<String, NetworkLink> g =
	            new NetworkGraph<String, NetworkLink>(NetworkLink.class);

	        String a = "a";
	        String b = "b";
	        String c = "c";
	        String d = "d";

	        // add the vertices
	        g.addVertex(a);
	        g.addVertex(b);
	        g.addVertex(c);
	        g.addVertex(d);

	        // add edges to create a circuit
	        g.addEdge(a, b);
	        g.addEdge(a, d);
	        g.addEdge(d, b);
	        g.addEdge(b, c);
	        g.addEdge(c,d);
	        
	        //adding transfers to the demandlink
	        
	        Transfer t1 = new Transfer("t1","a","c",40,5,5);
	        
	        Transfer t2 = new Transfer("t2","c","a",40,5,3);
	        
	        Transfer t3 = new Transfer("t3","a","d",40,5,2);
	        
	        Transfer t4 = new Transfer("t4","b","d",40,5,2);
	        
	        Transfer t5 = new Transfer("t5","a","c",40,5,0);

	        
	        ((NetworkLink)g.getEdge("a", "d")).addTransfer(t1);
	        ((NetworkLink)g.getEdge("a", "d")).addTransfer(t2);
	        
	        ((NetworkLink)g.getEdge("a", "b")).addTransfer(t3);
	        
	        ((NetworkLink)g.getEdge("b", "c")).addTransfer(t4);
	        
	        ((NetworkLink)g.getEdge("c", "d")).addTransfer(t1);
	        ((NetworkLink)g.getEdge("c", "d")).addTransfer(t2);
	        ((NetworkLink)g.getEdge("c", "d")).addTransfer(t4);
	        
	        ((NetworkLink)g.getEdge("b", "d")).addTransfer(t3);
	        
	        
	        
	        return g;
	    }
}
