package edu.project.maths.graph.graphsolver;

public class Transfer {

    String name;
    String origin;
    String destination;
    int volumeOfData;
    int completionTimeCommitted;
    int assignedSlot;

    public Transfer(String name, String origin, String destination,
            int volumeOfData, int completionTime, int assignedSlot) {
        super();
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.volumeOfData = volumeOfData;
        this.completionTimeCommitted = completionTime;
        this.assignedSlot = assignedSlot;
    }

    public int minimumSlotsRequired() {
        return (((volumeOfData / completionTimeCommitted) - 1) / NetworkLink.SLOT_SPEED) + 1;
    }

    public static int calculateTimeInterval(int volumeOfData, int assignedSlots) {
        int actualCompletionTime = 0;

        if (assignedSlots != 0) {
            actualCompletionTime = (int) Math.ceil(volumeOfData *1.0 / (assignedSlots * NetworkLink.SLOT_SPEED));
        }

        return actualCompletionTime;
    }

    public static int calculateVolumeOfDataRemaining(int assignedSlots, int timeInterval)
    {
        return assignedSlots * NetworkLink.SLOT_SPEED * timeInterval;
    }
    
    public int getActualCompletionTime()
    {
        return Transfer.calculateTimeInterval(volumeOfData, assignedSlot);
    }
    
    public boolean isSqueezable() {
        return assignedSlot > this.minimumSlotsRequired();
    }

    public int numberOfSqueezableSlots() {
        return assignedSlot - this.minimumSlotsRequired();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getVolumeOfData() {
        return volumeOfData;
    }

    public void setVolumeOfData(int volumeOfData) {
        this.volumeOfData = volumeOfData;
    }

    public int getCompletionTimeCommitted() {
        return completionTimeCommitted;
    }

    public void setCompletionTimeCommitted(int completionTime) {
        this.completionTimeCommitted = completionTime;
    }

    public int getAssignedSlot() {
        return assignedSlot;
    }

    public void setAssignedSlot(int assignedSlot) {
        this.assignedSlot = assignedSlot;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Transfer) && this.name.equalsIgnoreCase(((Transfer) obj).getName());
    }

    @Override
    protected Transfer clone() {
        Transfer t = new Transfer(name, origin, destination, volumeOfData, completionTimeCommitted, assignedSlot);
        return t;
    }

}
