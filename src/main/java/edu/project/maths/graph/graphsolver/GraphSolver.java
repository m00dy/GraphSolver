package edu.project.maths.graph.graphsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;

public class GraphSolver {

    private static final double alpha = 0.8;

    public static void main(String[] argv) {

        System.out.println("I'm famous");

        NetworkGraph<String, NetworkLink> graph = createStringGraph();
        Transfer demandTransfer = new Transfer("t0", "a", "c", 250, 15, 0);
        int bmin = demandTransfer.minimumSlotsRequired();
        System.out.println("Graph: " + graph);
        System.out.println("bmin required: " + bmin);

        if (findSolution(graph, demandTransfer, bmin)) {
            return;
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

        }

        return newSolution;
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

        return g;
    }

    private static List<Transfer> getRCL(HashMap<Transfer, Integer> tempRCL) {
        int qmax = 0, qmin = 0;

        List<Transfer> returnList = new ArrayList<Transfer>();

        for (Map.Entry<Transfer, Integer> entrySet : tempRCL.entrySet()) {

            Transfer key = entrySet.getKey();
            Integer value = entrySet.getValue();

            if (value > qmax) {
                qmax = value;
            }
            if (value < qmin) {
                qmin = value;
            }
        }

        double q = qmax - alpha * (qmax - qmin);

        for (Map.Entry<Transfer, Integer> entrySet : tempRCL.entrySet()) {
            Transfer key = entrySet.getKey();
            Integer value = entrySet.getValue();

            if (value > q) {
                returnList.add(key);
            }

        }
        System.out.print("RCL: {");

        for (Transfer t : returnList) {
            System.out.print(t.getName() + ", " );
        }

        System.out.println("}");

        return returnList;
    }

    private static HashMap<Transfer, Integer> getCandidateSetWithQuality(List<NetworkLink> edgeListRemaining, ArrayList<Transfer> partialRescheduleList, int bmin) {

        HashMap<Transfer, Integer> candidateSet = new HashMap<Transfer, Integer>();

        NetworkLink currentLink = edgeListRemaining.get(0);

        ArrayList<Transfer> transferList = currentLink.getTransferList();

        for (Transfer transfer : transferList) {

            if (partialRescheduleList.contains(transfer)) {
                continue;
            }

            int numberOfAppearancesInTheRoute = getNumberOfAppearancesInTheRoute(edgeListRemaining, transfer);

            //HashMap<Transfer, Integer> transferEntry = new HashMap<Transfer, Integer>();
            candidateSet.put(transfer, transfer.numberOfSqueezableSlots() * numberOfAppearancesInTheRoute);

            //System.out.println("Select this transfer, we have only one chance.." + transfer.getName());
        }

        for (Map.Entry<Transfer, Integer> entrySet : candidateSet.entrySet()) {
            Transfer key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println("Transfer is " + key.getName() + " Value is " + value);
        }

        System.out.print("Candidate Set with quality is: {");

        for (Map.Entry<Transfer, Integer> entrySet : candidateSet.entrySet()) {
            Transfer key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.print(" (" + key.getName() + ", " + value + ")");
        }

        System.out.println("}");

        return candidateSet;
    }

    private static int getNumberOfAppearancesInTheRoute(List<NetworkLink> edgeListRemaining, Transfer currentTransfer) {
        int appearances = 0;
        for (NetworkLink edge : edgeListRemaining) {
            for (Transfer transfer : edge.getTransferList()) {
                if (transfer.equals(currentTransfer)) {
                    appearances++;
                    break;
                }
            }
        }

        return appearances;
    }

}
