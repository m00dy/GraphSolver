package edu.project.maths.graph.graphsolver;

import com.sun.jmx.remote.util.OrderClassLoaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;

public class GraphSolver {

    public static void main(String[] argv) {

        System.out.println("Fuck me! I'm famous");

        NetworkGraph<String, NetworkLink> graph = createStringGraph();
        Transfer t5 = new Transfer("t5", "a", "c", 40, 5, 0);
        int bmin = t5.minimumSlotsRequired();

        DijkstraShortestPath<String, NetworkLink> freeRoute = graph.getCapacitadedShortestPath("a", "c", 5);

        if (freeRoute.getPath() != null) {
            System.out.println("Path for this transfer " + freeRoute.getPath());
            return;
        }

        List<GraphPath<String, NetworkLink>> paths = graph.getKShortestPaths("a", "c");

        System.out.println("K Shortest Paths: " + paths);
        for (GraphPath<String, NetworkLink> path : paths) {
            System.out.println("==========================");
            System.out.println("Taking path: " + path);
            Set<HashMap<Transfer, Integer>> partialSolution = new HashSet<HashMap<Transfer, Integer>>();

            List<NetworkLink> edgeList = path.getEdgeList();
            List<NetworkLink> edgeListRemaining = new ArrayList(edgeList);
            //Collections.copy(edgeListRemaining, edgeList);

            for (NetworkLink link : edgeList) {
                System.out.println("Edge List:  " + edgeListRemaining);
                if (link.getFreeSlots() < bmin) {
                    // quality function here
                    Set<HashMap<Transfer, Integer>> listOfTransfer = generateCandidateSet(edgeListRemaining, partialSolution, bmin);

                }

                edgeListRemaining.remove(link);
            }
        }

        //System.out.println(graph.toString());
    }

    private static NetworkGraph<String, NetworkLink> createStringGraph() {
        NetworkGraph<String, NetworkLink> g
                = new NetworkGraph<String, NetworkLink>(NetworkLink.class);

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
        g.addEdge(c, d);

        //adding transfers to the demandlink
        Transfer t1 = new Transfer("t1", "a", "c", 40, 5, 5);

        Transfer t2 = new Transfer("t2", "c", "a", 40, 15, 3);

        Transfer t3 = new Transfer("t3", "a", "d", 40, 5, 6);

        Transfer t4 = new Transfer("t4", "b", "d", 40, 20, 2);

        ((NetworkLink) g.getEdge("a", "d")).addTransfer(t1);
        ((NetworkLink) g.getEdge("a", "d")).addTransfer(t2);

        ((NetworkLink) g.getEdge("a", "b")).addTransfer(t3);

        ((NetworkLink) g.getEdge("b", "c")).addTransfer(t4);

        ((NetworkLink) g.getEdge("c", "d")).addTransfer(t1);
        ((NetworkLink) g.getEdge("c", "d")).addTransfer(t2);
        ((NetworkLink) g.getEdge("c", "d")).addTransfer(t4);

        ((NetworkLink) g.getEdge("b", "d")).addTransfer(t3);

        return g;
    }

    private static Set<HashMap<Transfer, Integer>> generateCandidateSet(List<NetworkLink> edgeListRemaining, Set<HashMap<Transfer, Integer>> partialSolution, int bmin) {
        Set<HashMap<Transfer, Integer>> transferSet = new HashSet<HashMap<Transfer, Integer>>();

        NetworkLink currentLink = edgeListRemaining.get(0);
        ArrayList<Transfer> transferList = currentLink.getTransferList();

        for (Transfer transfer : transferList) {
//            if (transfer.numberOfSqueezableSlots() > 0) {
            int numberOfAppearancesInTheRoute = getNumberOfAppearancesInTheRoute(edgeListRemaining, transfer);
            HashMap<Transfer, Integer> transferEntry = new HashMap<Transfer, Integer>();
            
            transferEntry.put(transfer, transfer.numberOfSqueezableSlots() * numberOfAppearancesInTheRoute); 
            
            transferSet.add(transferEntry);
        
            System.out.println("Select this transfer, we have only one chance.." + transfer.getName());
        }

        return transferSet;
    }

    private static int getNumberOfAppearancesInTheRoute(List<NetworkLink> edgeListRemaining, Transfer currentTransfer) {
        int appearances = 0;
        for (NetworkLink edge : edgeListRemaining) {
            for (Transfer transfer : edge.getTransferList())
            {
                if (transfer.equals(currentTransfer))
                {
                    appearances++;
                    break;
                }
            }
        }
        
        return appearances;
    }
    
    

}
