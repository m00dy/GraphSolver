/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.project.maths.graph.graphsolver;

import java.util.ArrayList;
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

}
