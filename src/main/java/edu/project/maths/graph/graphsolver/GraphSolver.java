package edu.project.maths.graph.graphsolver;

import edu.project.maths.graph.brkga.BrkgaSolver;
import edu.project.maths.graph.grasp.GraspSolver;
import java.util.ArrayList;
import java.util.Date;
import org.jgrapht.alg.DijkstraShortestPath;

public class GraphSolver {

    public static void main(String[] argv) {

        Date initStartTime = new Date();
        System.out.println("****Init Start Time: " + initStartTime.getTime());
        System.out.println("I'm famous");

        NetworkGraph<String, NetworkLink> graph = new NetworkGraph<String, NetworkLink>(NetworkLink.class);
        ArrayList<Transfer> transfersList = new ArrayList<Transfer>();
        generateProblemGraph(graph, transfersList);
        
        Transfer demandTransfer = new Transfer("t0", "a", "c", 250, 15, 0);
        int bmin = demandTransfer.minimumSlotsRequired();
        System.out.println("Graph: " + graph);
        System.out.println("bmin required: " + bmin);

        if (findSolution(graph, demandTransfer, bmin)) {
            return;
        }

        /*We could not find a solution, now we need to reschedule paths 
         * using Grasp, local search and BRKGA*/
        
        Date graspStartTime = new Date();
        System.out.println("****Grasp Start Time: " + graspStartTime.getTime());
        Solution graspSolution = GraspSolver.grasp(graph, demandTransfer);
        
        Date graspEndTime = new Date();
        System.out.println("****Grasp End/Local Search Start Time: " + graspEndTime.getTime());
        
        GraspSolver.localSearch(demandTransfer, graspSolution, graph);
        
        Date localSearchEndTime = new Date();
        System.out.println("****Local Search End Time: " + localSearchEndTime.getTime());
        
        BrkgaSolver.solve(graph, transfersList, demandTransfer);
        Date brkgaEndTime = new Date();
        System.out.println("****BRKGA End Time: " + brkgaEndTime.getTime());
        
        
        System.out.println("******\n******\n******\nResults: "
                + "\nGRASP: " + (graspEndTime.getTime() - graspStartTime.getTime())
                + "\nLocal Search: " + (localSearchEndTime.getTime() - graspEndTime.getTime())
                + "\nBRKGA: " + (brkgaEndTime.getTime() - localSearchEndTime.getTime()));
    }

    private static boolean findSolution(NetworkGraph<String, NetworkLink> graph, Transfer demandTransfer, int bmin) {
        DijkstraShortestPath<String, NetworkLink> freeRoute = graph.getCapacitadedShortestPath(demandTransfer.getOrigin(),
                demandTransfer.getDestination(), bmin);
        Solution solution = new Solution();
        solution.setDemand(demandTransfer);

        if (freeRoute.getPath() != null) {
            solution.setPath(freeRoute.getPath());
            demandTransfer.setAssignedSlot(bmin);
            solution.setReschedules(null);
            System.out.println("Found Capacitated Path" + freeRoute.getPath());
            return true;
        }
        return false;
    }

    private static void generateProblemGraph(NetworkGraph<String, NetworkLink> g, ArrayList<Transfer> t) {

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

        Transfer t4 = new Transfer("t4", "b", "d", 240, 20, 6);

        Transfer t5 = new Transfer("t5", "a", "c", 280, 15 , 4);

        t.add(t1);
        t.add(t2);
        t.add(t3);
        t.add(t4);
        t.add(t5);
                
        ((NetworkLink) g.getEdge("a", "d")).addTransfer(t1);
        ((NetworkLink) g.getEdge("a", "d")).addTransfer(t2);

        ((NetworkLink) g.getEdge("a", "b")).addTransfer(t3);
        ((NetworkLink) g.getEdge("a", "b")).addTransfer(t5);

        ((NetworkLink) g.getEdge("b", "c")).addTransfer(t4);
        ((NetworkLink) g.getEdge("b", "c")).addTransfer(t5);
        
        ((NetworkLink) g.getEdge("c", "d")).addTransfer(t1);
        ((NetworkLink) g.getEdge("c", "d")).addTransfer(t2);
        ((NetworkLink) g.getEdge("c", "d")).addTransfer(t4);

        ((NetworkLink) g.getEdge("b", "d")).addTransfer(t3);

    }

}
