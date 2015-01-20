/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.project.maths.graph.brkga;

import edu.project.maths.graph.graphsolver.NetworkGraph;
import edu.project.maths.graph.graphsolver.NetworkLink;
import edu.project.maths.graph.graphsolver.Schedule;
import edu.project.maths.graph.graphsolver.Solution;
import edu.project.maths.graph.graphsolver.Transfer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.jgrapht.GraphPath;

/**
 *
 * @author orak
 */
public class GraphDecoder {

    private final NetworkGraph<String, NetworkLink> graph;
    private final ArrayList<Transfer> transfersList;
    private final Transfer demandTransfer;
    private final List<GraphPath<String, NetworkLink>> paths;
    private final int bmin;
    boolean printResult = false;

    public GraphDecoder(NetworkGraph<String, NetworkLink> graph, ArrayList<Transfer> transfersList, Transfer demandTransfer) {
        this.graph = graph;
        this.transfersList = transfersList;
        this.demandTransfer = demandTransfer;
        this.paths = graph.getKShortestPaths(demandTransfer.getOrigin(), demandTransfer.getDestination());
        this.bmin = demandTransfer.minimumSlotsRequired();
    }

    public double decode(ArrayList<Double> chromosome) {
        double decodedValue = 0.0;
        Solution solution = new Solution();
        solution.setDemand(demandTransfer);
        boolean solutionFound = false;

        // 1. for each K path
        // 2. sort chromosome in ascending by multiplying with no of hops transfer is in the path
        // 3. for each transfer
        // 4. squeeze and check if it fulfills the demand
        // 5. if yes its the solution, otherwise check another path
        // 6. solution found: higher the number of reshedules, lower the decoded value
        //      decoded value = number of remaining transfers not resheduled + 1
        for (GraphPath<String, NetworkLink> path : paths) {
            solution.setPath(path);

//            System.out.println("==========================");
//            System.out.println("Taking path: " + path);
            List<NetworkLink> edgeList = path.getEdgeList();
            List<NetworkLink> edgeListRemaining = new ArrayList<NetworkLink>(edgeList);
//            List<Pair<NetworkLink, Integer>> edgeListRemainingFreeSlots = new ArrayList<Pair<NetworkLink, Integer>>();
            HashMap<NetworkLink, Integer> edgeListRemainingFreeSlots = new HashMap<NetworkLink, Integer>();
            ArrayList<Transfer> solutionTransfers = new ArrayList<Transfer>();

            for (NetworkLink link : edgeListRemaining) {

                int freeSlots = link.getFreeSlots();
                if (link.getFreeSlots() < bmin) {
//                    Pair<NetworkLink, Integer> pair = new Pair<NetworkLink, Integer>(link, freeSlots);
//                    edgeListRemainingFreeSlots.add(pair);
                    edgeListRemainingFreeSlots.put(link, freeSlots);
                }
            }

            ArrayList<Transfer> sortedTransferList = getSortedChromosome(edgeList, chromosome);

            while (!sortedTransferList.isEmpty()) {
                Transfer transfer = sortedTransferList.remove(0);
                solutionTransfers.add(transfer);

                int numberOfSqueezableSlots = transfer.numberOfSqueezableSlots();

                Iterator<Map.Entry<NetworkLink, Integer>> entries = edgeListRemainingFreeSlots.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<NetworkLink, Integer> entry = entries.next();
                    NetworkLink link = entry.getKey();
                    Integer freeSlotsInLink = entry.getValue();

                    if (link.containsTransfer(transfer)) {
                        if (freeSlotsInLink + numberOfSqueezableSlots < bmin) {
                            edgeListRemainingFreeSlots.put(link, freeSlotsInLink + numberOfSqueezableSlots);
                        } else {
                            entries.remove();
                        }
                    }

                }

                if (edgeListRemainingFreeSlots.isEmpty()) {
                    // Found solution
                    solutionFound = true;
                    ArrayList<Schedule> reschedules = Solution.calculateReshedules(solutionTransfers, demandTransfer);
                    solution.setReschedules(reschedules);
                    decodedValue = sortedTransferList.size() + 1;
                    break;

                }

            }

            if (solutionFound) {
                break;
            }
        }

        if (printResult) {
            System.out.println("========================");
            System.out.println("BRKGA Solution: \n---------\n" + solution);
            System.out.println("========================");

        }

        return decodedValue;
    }

    private ArrayList<Transfer> getSortedChromosome(List<NetworkLink> edgeList, ArrayList<Double> chromosome) {
        ArrayList<Transfer> transferChromosome = new ArrayList<Transfer>();
        ArrayList<Pair<Transfer, Double>> transferPair = new ArrayList<Pair<Transfer, Double>>();

        for (int i = 0; i < transfersList.size(); i++) {
            Transfer transfer = transfersList.get(i);
            int numberOfAppearancesInTheRoute = NetworkGraph.getNumberOfAppearancesInTheRoute(edgeList, transfer);
            double value = chromosome.get(i) * numberOfAppearancesInTheRoute;

            Pair<Transfer, Double> pair = new Pair<Transfer, Double>(transfer, value);

            transferPair.add(pair);
        }

        Collections.sort(transferPair, new Comparator<Pair<Transfer, Double>>() {
            @Override
            public int compare(Pair<Transfer, Double> x, Pair<Transfer, Double> y) {
                double res = y.getValue() - x.getValue();
                int resInt = 0;

                if (res > 0.0) {
                    resInt = 1;
                } else if (res == 0.0) {
                    resInt = 0;
                } else {
                    resInt = -1;
                }

                return resInt;
            }
        });

        for (Pair<Transfer, Double> transferPair1 : transferPair) {
            transferChromosome.add(transferPair1.getKey());
        }

        return transferChromosome;
    }
}
