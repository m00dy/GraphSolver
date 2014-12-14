/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.project.maths.graph.graphsolver;

/**
 *
 * @author orak
 */
public class Schedule {
    private String transferName;
    private int reassignedSlot;
    private int startTime;
    private int endTime;

    public Schedule(String transferName, int reassignedSlot, int startTime, int endTime) {
        this.transferName = transferName;
        this.reassignedSlot = reassignedSlot;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public int getReassignedSlot() {
        return reassignedSlot;
    }

    public void setReassignedSlot(int reassignedSlot) {
        this.reassignedSlot = reassignedSlot;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
    
}
