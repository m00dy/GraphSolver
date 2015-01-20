package edu.project.maths.graph.graphsolver;

import edu.project.maths.graph.brkga.BrkgaSolver;
import edu.project.maths.graph.grasp.GraspSolver;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Date;
=======
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jgrapht.GraphPath;
>>>>>>> origin/master
import org.jgrapht.alg.DijkstraShortestPath;

public class GraphSolver {

<<<<<<< HEAD
=======
    private static final double alpha = 0.8;

>>>>>>> origin/master
    public static void main(String[] argv) {

        Date initStartTime = new Date();
        System.out.println("****Init Start Time: " + initStartTime.getTime());
        System.out.println("I'm famous");

<<<<<<< HEAD
        NetworkGraph<String, NetworkLink> graph = new NetworkGraph<String, NetworkLink>(NetworkLink.class);
        ArrayList<Transfer> transfersList = new ArrayList<Transfer>();

        // Different Topologies and Transfers
        //generateProblemGraph1(graph, transfersList);
        // generateProblemGraph2(graph, transfersList);
         generateProblemGraph3(graph, transfersList);
        
        Transfer demandTransfer = new Transfer("t0", "a", "d", 750, 15, 0);
        
=======
        NetworkGraph<String, NetworkLink> graph = createStringGraph();
        Transfer demandTransfer = new Transfer("t0", "a", "c", 250, 15, 0);
>>>>>>> origin/master
        int bmin = demandTransfer.minimumSlotsRequired();
        System.out.println("Graph: " + graph);
        System.out.println("bmin required: " + bmin);

        if (findSolution(graph, demandTransfer, bmin)) {
            return;
<<<<<<< HEAD
=======
        }

        /*We could not find a solution, now we need to reschedule paths*/
        Solution graspSolution = grasp(graph, demandTransfer);
        localSearch(demandTransfer, graspSolution, graph);
    }

    private static Solution localSearch(Transfer demandTransfer, Solution graspSolution, NetworkGraph<String, NetworkLink> graph) {
        Solution newSolution = new Solution();
        newSolution.setDemand(demandTransfer);
        newSolution.setPath(graspSolution.getPath());

        if (graspSolution.getPath() != null) {
            // Do local search if the grasp found a solution
            ArrayList<Transfer> transferListInPath = graph.getTransferListInPath(graspSolution.getPath());

            ArrayList<Transfer> transferListInSolution = new ArrayList<Transfer>();
            ArrayList<Transfer> transferListNotInSolution = new ArrayList<Transfer>();
            ArrayList<Transfer> newTransferList = new ArrayList<Transfer>(transferListInSolution);

            for (Transfer transfer : transferListInPath) {
                if (graspSolution.containsTransferReshedule(transfer)) {
                    transferListInSolution.add(transfer);
                } else {
                    transferListNotInSolution.add(transfer);
                }
            }

            for (Transfer transfer : transferListInSolution) {
                newTransferList.remove(transfer);

                if (Solution.isSolutionFeasible(graspSolution.getPath(), newTransferList, demandTransfer)
                        && graspSolution.getQualityOfSolution() >= Solution.qualtiyOfSolution(newTransferList)) {
                    //we found a good solution :D
                    ArrayList<Schedule> newResehedules = calculateReshedules(newTransferList, demandTransfer);
                    newSolution.setReschedules(newResehedules);
                    System.out.println("Solution after local search: \n" + newSolution);
                    return newSolution;
//                    break;
                }

                newTransferList.add(transfer);
            }

            for (Transfer incomingTransfer : transferListNotInSolution) {
                newTransferList.add(incomingTransfer);

                for (int i = 0; i < transferListInSolution.size() - 1; i++) {
                    Transfer removedTransfer1 = transferListInSolution.get(i);
                    newTransferList.remove(removedTransfer1);
                    
                    for (int j = i; j < transferListInSolution.size(); j++) {
                        Transfer removedTransfer2 = transferListInSolution.get(j);
                        newTransferList.remove(removedTransfer2);

                        if (Solution.isSolutionFeasible(graspSolution.getPath(), newTransferList, demandTransfer)
                                && graspSolution.getQualityOfSolution() >= Solution.qualtiyOfSolution(newTransferList)) {
                            //we found a good solution :D
                            ArrayList<Schedule> newResehedules = calculateReshedules(newTransferList, demandTransfer);
                            newSolution.setReschedules(newResehedules);
                     System.out.println("Solution after local search2: \n" + newSolution);
                           return newSolution;
//                    break;
                        }

                        newTransferList.add(removedTransfer2);
                    }
                    newTransferList.add(removedTransfer1);
                }

                newTransferList.remove(incomingTransfer);
            }

>>>>>>> origin/master
        }

<<<<<<< HEAD
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
        
        
        System.out.println("======\n======\n======\nResults: "
                + "\nGRASP: " + (graspEndTime.getTime() - graspStartTime.getTime() + " ms")
                + "\nLocal Search: " + (localSearchEndTime.getTime() - graspEndTime.getTime() + " ms")
                + "\nBRKGA: " + (brkgaEndTime.getTime() - localSearchEndTime.getTime()) + " ms");
=======
        return newSolution;
>>>>>>> origin/master
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

<<<<<<< HEAD
    private static void generateProblemGraph1(NetworkGraph<String, NetworkLink> g, ArrayList<Transfer> t) {
=======
    private static Solution grasp(NetworkGraph<String, NetworkLink> graph, Transfer demandTransfer) {
        int bmin = demandTransfer.minimumSlotsRequired();
        Solution solution = new Solution();
        solution.setDemand(demandTransfer);

        List<GraphPath<String, NetworkLink>> paths = graph.getKShortestPaths(demandTransfer.getOrigin(), demandTransfer.getDestination());
//        HashMap<Transfer, Integer> reschedules = null;
        ArrayList<Transfer> reschedules = null;

        System.out.println("K Shortest Paths: " + paths);
        for (GraphPath<String, NetworkLink> path : paths) {
            solution.setPath(path);

            System.out.println("==========================");
            System.out.println("Taking path: " + path);
//            HashMap<Transfer, Integer> partialReschedules = new HashMap<Transfer, Integer>();
            ArrayList<Transfer> partialRescheduleList = new ArrayList<Transfer>();

            List<NetworkLink> edgeList = path.getEdgeList();
            List<NetworkLink> edgeListRemaining = new ArrayList(edgeList);

            for (NetworkLink link : edgeList) {
                int squeezedValue = link.getFreeSlots();
                System.out.println("Edge List:  " + edgeListRemaining);
                if (squeezedValue < bmin) {
                    /*
                     1. Generate RCL here
                     2. then select a transfer at random
                     3. Check if transfer fulfills demand, if not select another at random
                     in addtion to the previously selected
                     4. If transfers can be found which fulfils demand add it to the partial solution
                     5. Otherwise there is no solution in this path, try next path
                     */
                    HashMap<Transfer, Integer> candidateSet = getCandidateSetWithQuality(edgeListRemaining, partialRescheduleList, bmin);
                    List<Transfer> rcl = getRCL(candidateSet);
                    List<Transfer> transferSetInThisLink = new ArrayList<Transfer>();
                    Random r = new Random();

                    while (!rcl.isEmpty() && squeezedValue < bmin) {

                        Transfer luckyTransfer = rcl.get(r.nextInt(rcl.size()));
                        squeezedValue += luckyTransfer.numberOfSqueezableSlots();
                        rcl.remove(luckyTransfer);
                        transferSetInThisLink.add(luckyTransfer);
                        System.out.println("Selecting Transfer in the solution: " + luckyTransfer.getName() + luckyTransfer.numberOfSqueezableSlots());
                    }

                    if (squeezedValue >= bmin) {
                        if (solution.getDemand().getAssignedSlot() == 0 || solution.getDemand().getAssignedSlot() > squeezedValue) {
                            solution.getDemand().setAssignedSlot(squeezedValue);
                        }

//                        partialRescheduleList.add(transferSetInThisLink);
                        partialRescheduleList.addAll(transferSetInThisLink);
                        Set s = new HashSet(partialRescheduleList);
                        partialRescheduleList.clear();
                        partialRescheduleList.addAll(s);
                                
//                        for (Transfer t : transferSetInThisLink) {
//                            partialReschedules.put(t, t.numberOfSqueezableSlots());
//                        }
                    } else {
                        solution.getDemand().setAssignedSlot(0);
                        solution.setPath(null);
                        break;
                    }

                }

                edgeListRemaining.remove(link);
            }

            if (solution.getPath() != null) {
                //found solution, assign it and stop here
                reschedules = partialRescheduleList;

                break;
            }
        }

        /*
         if solution found, calculate the reschedules times and print the solution
         */
        if (reschedules != null) {
            ArrayList<Schedule> reshedulesList = calculateReshedules(reschedules, demandTransfer);
            solution.setReschedules(reshedulesList);

            System.out.println("========================");
            System.out.println("Grasp Solution: \n---------\n" + solution);
            System.out.println("========================");
        }

        return solution;
    }

    private static ArrayList<Schedule> calculateReshedules(ArrayList<Transfer> reschedules, Transfer demandTransfer) {
        ArrayList<Schedule> reshedulesList = new ArrayList<Schedule>();
        for (Transfer t : reschedules) {
//            Transfer t = entrySet.getKey();
            Integer squeezedSlots = t.numberOfSqueezableSlots();

            int transferActualTime = Transfer.calculateTimeInterval(t.getVolumeOfData(), t.getAssignedSlot() - squeezedSlots);
            int demandActualTime = demandTransfer.getActualCompletionTime();

            if (transferActualTime <= demandActualTime) {
                //no need to assing resources, since the transfer finishes early
                Schedule s = new Schedule(t.getName(), t.getAssignedSlot() - squeezedSlots, 0, transferActualTime);
                reshedulesList.add(s);
            } else {
                // we need to re-assign the resources freed
                //no need to assing resources, since the transfer finishes early
                Schedule s1 = new Schedule(t.getName(), t.getAssignedSlot() - squeezedSlots, 0, demandActualTime);
                int volumeOfDataAfterS1 = Transfer.calculateVolumeOfDataRemaining(t.getAssignedSlot() - squeezedSlots, demandActualTime - 0);
                int s2FinishTime = demandActualTime + Transfer.calculateTimeInterval(volumeOfDataAfterS1, t.getAssignedSlot());
                Schedule s2 = new Schedule(t.getName(), t.getAssignedSlot(), demandTransfer.getActualCompletionTime(), s2FinishTime);
                reshedulesList.add(s1);
                reshedulesList.add(s2);

            }
        }
        return reshedulesList;
    }

    private static NetworkGraph<String, NetworkLink> createStringGraph() {
        NetworkGraph<String, NetworkLink> g
                = new NetworkGraph<String, NetworkLink>(NetworkLink.class);
>>>>>>> origin/master

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

<<<<<<< HEAD
        t.add(t1);
        t.add(t2);
        t.add(t3);
        t.add(t4);
        t.add(t5);
=======
>>>>>>> origin/master
                
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

        private static void generateProblemGraph2(NetworkGraph<String, NetworkLink> g, ArrayList<Transfer> t) {

        String a = "a";
        String b = "b";
        String c = "c";
        String d = "d";
        String e = "e";

        // add the vertices
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);

        // add edges to create a circuit
        g.addEdge(a, c);
        g.addEdge(a, d);
        g.addEdge(b, e);
        g.addEdge(b, d);
        g.addEdge(c, e);

        //adding transfers to the demandlink
        Transfer t1 = new Transfer("t1", "e", "d", 20, 5, 3);

        Transfer t2 = new Transfer("t2", "c", "a", 20, 5, 3);

        Transfer t3 = new Transfer("t3", "a", "d", 20, 15, 2);
//
        Transfer t4 = new Transfer("t4", "b", "d", 20, 10, 2);
//
        Transfer t5 = new Transfer("t5", "a", "c", 20, 15 , 2);
        
        Transfer t6 = new Transfer("t6", "a", "c", 20, 15 , 2);
        
        Transfer t7 = new Transfer("t7", "a", "c", 20, 15 , 3);
        
        Transfer t8 = new Transfer("t8", "a", "c", 20, 10 , 2);
        
        Transfer t9 = new Transfer("t9", "a", "c", 20, 10 , 2);
        
        Transfer t10 = new Transfer("t10", "a", "c", 20, 10 , 3);
        
        Transfer t11 = new Transfer("t11", "a", "c", 20, 10 , 3);
        
        t.add(t1);
        t.add(t2);
        t.add(t3);
        t.add(t4);
        t.add(t5);
        t.add(t6);
        t.add(t7);
        t.add(t8);
        t.add(t9);
        t.add(t10);
        t.add(t11);
        
//            //All ongoing transfers (slices, volume, max time)
//All ongoing transfers (slices, volume, max time)
//All ongoing transfers (slices, volume, max time)
//transfers = [
//	[3, 20, 5]  //t1
//	[3, 20, 5]  //t2
// 	[2, 20, 15]  //t3
//	[2, 20, 10]  //t4
//	[2, 20, 15]  //t5
//	[2, 20, 15]  //t6
//	[3, 20, 15]  //t7
//	[2, 20, 10]  //t8
//	[2, 20, 10]  //t9
//	[3, 20, 10]  //t10
//	[3, 20, 10] //t11
//];
////Existing transfers using links
////ac, ce, eb, bd, da
//tPaths = [
//	[0, 0, 1, 1, 0]
//	[1, 0, 0, 0, 0]
//	[0, 1, 1, 0, 0]
//	[0, 0, 0, 0, 1]
//	[1, 0, 0, 0, 0]
//	[0, 0, 0, 1, 1]
//	[0, 1, 0, 0, 0]
//	[1, 0, 0, 0, 1]
//	[1, 1, 1, 0, 0]
//	[0, 0, 1, 1, 0]
//	[0, 0, 0, 1, 1]
//];

        ((NetworkLink) g.getEdge("b", "e")).addTransfer(t1);
        ((NetworkLink) g.getEdge("b", "d")).addTransfer(t1);
        
        ((NetworkLink) g.getEdge("a", "c")).addTransfer(t2);

<<<<<<< HEAD
        ((NetworkLink) g.getEdge("c", "e")).addTransfer(t3);
        ((NetworkLink) g.getEdge("e", "b")).addTransfer(t3);
        
        ((NetworkLink) g.getEdge("a", "d")).addTransfer(t4);
        
        ((NetworkLink) g.getEdge("a", "c")).addTransfer(t5);
=======
        }
        System.out.print("RCL: {");

        for (Transfer t : returnList) {
            System.out.print(t.getName() + ", " );
        }

        System.out.println("}");
>>>>>>> origin/master

        ((NetworkLink) g.getEdge("b", "d")).addTransfer(t6);
        ((NetworkLink) g.getEdge("d", "a")).addTransfer(t6);

        ((NetworkLink) g.getEdge("c", "e")).addTransfer(t7);
        
        ((NetworkLink) g.getEdge("a", "c")).addTransfer(t8);
        ((NetworkLink) g.getEdge("a", "d")).addTransfer(t8);
        
        ((NetworkLink) g.getEdge("a", "c")).addTransfer(t9);
        ((NetworkLink) g.getEdge("e", "c")).addTransfer(t9);
        ((NetworkLink) g.getEdge("e", "b")).addTransfer(t9);
        
        ((NetworkLink) g.getEdge("e", "b")).addTransfer(t10);
        ((NetworkLink) g.getEdge("b", "d")).addTransfer(t10);
        
        ((NetworkLink) g.getEdge("b", "d")).addTransfer(t11);
        ((NetworkLink) g.getEdge("d", "a")).addTransfer(t11);


        




    }
        
           private static void generateProblemGraph3(NetworkGraph<String, NetworkLink> g, ArrayList<Transfer> t) {

<<<<<<< HEAD
        String a = "a";
        String b = "b";
        String c = "c";
        String d = "d";
        String e = "e";
        String f = "f";
        
=======
    private static HashMap<Transfer, Integer> getCandidateSetWithQuality(List<NetworkLink> edgeListRemaining, ArrayList<Transfer> partialRescheduleList, int bmin) {
>>>>>>> origin/master

        // add the vertices
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);
        g.addVertex(f);
        // add edges to create a circuit
        g.addEdge(a, b);
        g.addEdge(a, f);
        g.addEdge(b, c);
        g.addEdge(b, e);
        g.addEdge(c, d);
        g.addEdge(c, f);
        g.addEdge(d, e);
        g.addEdge(e, f);
        
        //adding transfers to the demandlink
        Transfer t1 = new Transfer("t1", "e", "d", 100, 5, 5);

        Transfer t2 = new Transfer("t2", "c", "a", 100, 5, 3);

        Transfer t3 = new Transfer("t3", "a", "d", 100, 15, 3);
//100
        Transfer t4 = new Transfer("t4", "b", "d", 100, 10, 5);
//100
        Transfer t5 = new Transfer("t5", "a", "c", 100, 15 , 6);
        //
        Transfer t6 = new Transfer("t6", "a", "c", 100, 15 , 4);
        //100
        Transfer t7 = new Transfer("t7", "a", "c", 100, 15 , 10);
        //100
//        Transfer t8 = new Transfer("t8", "a", "c", 100, 10 , 10);
//        //100
//        Transfer t9 = new Transfer("t9", "a", "c", 100, 10 , 6);
//        
//        Transfer t10 = new Transfer("t10", "a", "c", 100, 10 , 2);
//        //100
//        Transfer t11 = new Transfer("t11", "a", "c", 100, 10 , 4);
        
        t.add(t1);
        t.add(t2);
        t.add(t3);
        t.add(t4);
        t.add(t5);
        t.add(t6);
        t.add(t7);
//        t.add(t8);
//        t.add(t9);
//        t.add(t10);
//        t.add(t11);
        

        ((NetworkLink) g.getEdge("a", "b")).addTransfer(t1);
       
        ((NetworkLink) g.getEdge("a", "b")).addTransfer(t2);
        ((NetworkLink) g.getEdge("c", "b")).addTransfer(t2);

<<<<<<< HEAD
        ((NetworkLink) g.getEdge("c", "b")).addTransfer(t3);
        
        ((NetworkLink) g.getEdge("c", "d")).addTransfer(t4);
        
        ((NetworkLink) g.getEdge("e", "d")).addTransfer(t5);
=======
            if (partialRescheduleList.contains(transfer)) {
                continue;
            }
>>>>>>> origin/master

        ((NetworkLink) g.getEdge("f", "e")).addTransfer(t6);
        ((NetworkLink) g.getEdge("d", "e")).addTransfer(t6);

        ((NetworkLink) g.getEdge("c", "f")).addTransfer(t7);
//        
//        ((NetworkLink) g.getEdge("b", "e")).addTransfer(t8);
//         
//        ((NetworkLink) g.getEdge("a", "f")).addTransfer(t9);
//        ((NetworkLink) g.getEdge("e", "f")).addTransfer(t9);
//        
//        ((NetworkLink) g.getEdge("a", "b")).addTransfer(t10);
//        ((NetworkLink) g.getEdge("b", "c")).addTransfer(t10);
//                ((NetworkLink) g.getEdge("c", "d")).addTransfer(t10);
//
//               ((NetworkLink) g.getEdge("f", "a")).addTransfer(t11);


    }

}
