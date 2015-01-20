/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.project.maths.graph.graphsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.GraphPath;

/**
 *
 * @author orak
 */
public class Solution {

    private Transfer demand;
    private GraphPath<String, NetworkLink> path;
    ArrayList<Schedule> reschedules;

    public Solution() {
    }

<<<<<<< HEAD
    /**
     *
     * @return quality 0 is very good; 10 is bad
     */
    public int getQualityOfSolution() {
        int quality = 0;
        Set<String> transferSet = new HashSet();

        for (Schedule schedule : reschedules) {
            if (!transferSet.contains(schedule.getTransferName())) {
                transferSet.add(schedule.getTransferName());
                quality++;
            }
        }

=======
/**
 *  
 * @return quality 0 is very good; 10 is bad
 */
    public int getQualityOfSolution()
    {
        int quality = 0;
        Set<String> transferSet = new HashSet();
        
        for (Schedule schedule : reschedules)
        {
            if (!transferSet.contains(schedule.getTransferName()))
            {
                transferSet.add(schedule.getTransferName());
                quality++;
            }
        }
        
>>>>>>> origin/master
        return quality;
    }

    public Transfer getDemand() {
        return demand;
    }

    public void setDemand(Transfer demand) {
        this.demand = demand;
    }

    public GraphPath<String, NetworkLink> getPath() {
        return path;
    }

    public void setPath(GraphPath<String, NetworkLink> path) {
        this.path = path;
    }

    public ArrayList<Schedule> getReschedules() {
        return reschedules;
    }

    public void setReschedules(ArrayList<Schedule> reschedules) {
        this.reschedules = reschedules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (path == null) {
            sb.append("No path");
        } else {
            sb.append("Path: \t").append(path.toString()).append("\n");
            sb.append("Demand: \t").append(" (Assigned slots, Actual Completion Time) - (");
            sb.append(demand.getAssignedSlot()).append(",").append(demand.getActualCompletionTime()).append(")\n");
            sb.append("Schedules: \n");

            for (Schedule schedule : this.getReschedules()) {
                sb.append("(Transfer Name, Reassigned slots, fromTime, toTime) - (").append(schedule.getTransferName()).append(", ").append(schedule.getReassignedSlot());
                sb.append(", ").append(schedule.getStartTime()).append(", ").append(schedule.getEndTime()).append(")\n");
            }
        }
        return sb.toString();

    }

<<<<<<< HEAD
    public boolean containsTransferReshedule(Transfer transfer) {
        for (Schedule s : reschedules) {
            if (s.getTransferName().equalsIgnoreCase(transfer.getName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSolutionFeasible(GraphPath<String, NetworkLink> path, ArrayList<Transfer> newTransferList, Transfer demandTransfer) {

        int bmin = demandTransfer.minimumSlotsRequired();

        for (NetworkLink link : path.getEdgeList()) {
            int squeezedValue = link.getFreeSlots();

            for (Transfer transfer : newTransferList) {
                if (link.getTransferList().contains(transfer)) {
                    squeezedValue += transfer.numberOfSqueezableSlots();
                }
            }

            if (squeezedValue < bmin) {
                return false;
            }

        }

        return true;
    }

    public static int qualtiyOfSolution(ArrayList<Transfer> newTransferList) {
        return newTransferList.size();
    }

    public static ArrayList<Schedule> calculateReshedules(ArrayList<Transfer> transfersToBeRescheduled, Transfer demandTransfer) {
        ArrayList<Schedule> reshedulesList = new ArrayList<Schedule>();
        for (Transfer t : transfersToBeRescheduled) {
            Integer squeezedSlots = t.numberOfSqueezableSlots();
            int transferActualTime = Transfer.calculateTimeInterval(t.getVolumeOfData(), t.getAssignedSlot() - squeezedSlots);
            int demandActualTime = demandTransfer.getActualCompletionTime();
            if (transferActualTime <= demandActualTime) {
                Schedule s = new Schedule(t.getName(), t.getAssignedSlot() - squeezedSlots, 0, transferActualTime);
                reshedulesList.add(s);
            } else {
                Schedule s1 = new Schedule(t.getName(), t.getAssignedSlot() - squeezedSlots, 0, demandActualTime);
                int volumeOfDataAfterS1 = Transfer.calculateVolumeOfDataRemaining(t.getAssignedSlot() - squeezedSlots, demandActualTime - 0);
                int s2FinishTime = demandActualTime + Transfer.calculateTimeInterval(volumeOfDataAfterS1, t.getAssignedSlot());
                Schedule s2 = new Schedule(t.getName(), t.getAssignedSlot(), demandTransfer.getActualCompletionTime(), s2FinishTime);
                reshedulesList.add(s1);
                reshedulesList.add(s2);
            }
        }
        return reshedulesList;
=======
    boolean containsTransferReshedule(Transfer transfer) {
        for (Schedule s : reschedules)
        {
            if (s.getTransferName().equalsIgnoreCase(transfer.getName()))
                return true;
        }
        
        return false;
    }
    
    static boolean isSolutionFeasible(GraphPath<String, NetworkLink> path, ArrayList<Transfer> newTransferList, Transfer demandTransfer) {
        
        int bmin = demandTransfer.minimumSlotsRequired();
        
        for (NetworkLink link : path.getEdgeList())
        {
            int squeezedValue = link.getFreeSlots();
            
            for (Transfer transfer: newTransferList)
            {
                if (link.getTransferList().contains(transfer))
                {
                    squeezedValue += transfer.numberOfSqueezableSlots();
                }
            }
            
            if (squeezedValue < bmin)
                return false;
            
        }
        
        return true;
    }
    
    static int qualtiyOfSolution(ArrayList<Transfer> newTransferList)
    {
        return newTransferList.size();
>>>>>>> origin/master
    }

}
