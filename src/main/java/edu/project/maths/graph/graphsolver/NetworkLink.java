package edu.project.maths.graph.graphsolver;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultEdge;

public class NetworkLink extends DefaultEdge {

    private ArrayList<Transfer> transferList = new ArrayList<Transfer>();
    final int numberOfSlots = 10;
    public final static int SLOT_SPEED = 10;

    public NetworkLink() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void addTransfer(Transfer transfer) {
        transferList.add(transfer);
    }

    public int getFreeSlots() {
        int overall = 0;
        for (Transfer transfer : transferList) {
            overall += transfer.getAssignedSlot();
        }
        return numberOfSlots - overall;
    }

    public ArrayList<Transfer> getTransferList() {
        return transferList;
    }

    public void setTransferList(ArrayList<Transfer> transferList) {
        this.transferList = transferList;
    }
    
    public boolean containsTransfer(Transfer t)
    {
        boolean contains = false;
        
        for (Transfer transfer : transferList)
            if (t.equals(transfer))
            {
                contains = true;
                break;
            }
        
        return contains;
    }

    @Override
    public NetworkLink clone() {
        NetworkLink o = (NetworkLink) super.clone();
        ArrayList<Transfer> transferListCopy = new ArrayList<Transfer>(this.transferList);
        //Collections.copy(transferListCopy, this.transferList);
        o.setTransferList(transferListCopy);
        return o;
    }

}
