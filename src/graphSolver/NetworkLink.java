package graphSolver;

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

	public void addTransfer(Transfer transfer){
		transferList.add(transfer);
	}
	
	public int getFreeSlots(){
		int overall = 0;
		for (Transfer transfer : transferList) {
			overall += transfer.getAssignedSlot();
		}
		return numberOfSlots - overall;
	}
	
	
}
