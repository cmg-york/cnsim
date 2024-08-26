package ca.yorku.cmg.cnsim.engine.event;

import ca.yorku.cmg.cnsim.engine.Config;
import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.node.INode;

public class Event_Report_BeliefReport extends Event {
    public void happen(Simulation sim){
    	super.happen(sim);
    	long[] sampleTx = Config.parseStringToArray(Config.getPropertyString("workload.sampleTransaction"));
		for (INode n : sim.getNodeSet().getNodes()) {
			n.event_PrintBeliefReport(sampleTx,this.getTime());
		}
    }
}
