package ca.yorku.cmg.cnsim.engine.event;

import ca.yorku.cmg.cnsim.engine.Config;
import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.node.INode;

public class Event_Report_BeliefReport extends Event {
	
	private long[] sampleTx;
	
	public Event_Report_BeliefReport(long time){
		super.setTime(time);
		this.sampleTx = Config.parseStringToArray(Config.getPropertyString("workload.sampleTransaction"));
	}
	
    public void happen(Simulation sim){
    	super.happen(sim);
		for (INode n : sim.getNodeSet().getNodes()) {
			n.event_PrintBeliefReport(sampleTx,this.getTime());
		}
    }
}
