/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.project.maths.graph.grasp;

import edu.project.maths.graph.graphsolver.GraphSolver;
import edu.project.maths.graph.graphsolver.NetworkGraph;
import edu.project.maths.graph.graphsolver.NetworkLink;
import edu.project.maths.graph.graphsolver.Schedule;
import edu.project.maths.graph.graphsolver.Solution;
import edu.project.maths.graph.graphsolver.Transfer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jgrapht.GraphPath;

/**
 *
 * @author orak
 */
public class GraspSolver {

    private static final double alpha = 0.8;

    public static Solution grasp(NetworkGraph<String, NetworkLink> graph, Transfer demandTransfer) {
        int bmin = demandTransfer.minimumSlotsRequired();
        Solution solution = new Solution();
        solution.setDemand(demandTransfer);

        List<GraphPath<String, NetworkLink>> paths = graph.getKShortestPaths(demandTransfer.getOrigin(), demandTransfer.getDestination());
//        HashMap<Transfer, Integer> reschedules = null;

//        Date kShortEndTime = new Date();
//        System.out.println("****k Short Time End Time: " + kShortEndTime.getTime());

        ArrayList<Transfer> reschedules = null;

        System.out.println("K Shortest Paths: " + paths);
        for (GraphPath<String, NetworkLink> path : paths) {
            solution.setPath(path);

            System.out.println("==========================");
            System.out.println("Taking path: " + path);
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
            ArrayList<Schedule> reshedulesList = Solution.calculateReshedules(reschedules, demandTransfer);
            solution.setReschedules(reshedulesList);

            System.out.println("========================");
            System.out.println("Grasp Solution: \n---------\n" + solution);
            System.out.println("========================");
        }

        return solution;
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
            System.out.print(t.getName() + ", ");
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

            int numberOfAppearancesInTheRoute = NetworkGraph.getNumberOfAppearancesInTheRoute(edgeListRemaining, transfer);

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

    public static Solution localSearch(Transfer demandTransfer, Solution graspSolution, NetworkGraph<String, NetworkLink> graph) {
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
                    ArrayList<Schedule> newResehedules = Solution.calculateReshedules(newTransferList, demandTransfer);
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

                    for (int j = i + 1; j < transferListInSolution.size(); j++) {
                        Transfer removedTransfer2 = transferListInSolution.get(j);
                        newTransferList.remove(removedTransfer2);

                        if (Solution.isSolutionFeasible(graspSolution.getPath(), newTransferList, demandTransfer)
                                && graspSolution.getQualityOfSolution() >= Solution.qualtiyOfSolution(newTransferList)) {
                            //we found a good solution :D
                            ArrayList<Schedule> newResehedules = Solution.calculateReshedules(newTransferList, demandTransfer);
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

}
