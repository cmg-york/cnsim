package cmg.cnsim.engine.event;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;

public class Event_Report_BeliefReport extends Event {
    public void happen(Simulation sim){
    	super.happen(sim);
    	long[] sampleTx = Config.parseStringToArray(Config.getPropertyString("workload.sampleTransaction"));
		for (INode n : sim.getNodeSet().getNodes()) {
			n.event_PrintBeliefReport(sampleTx,this.getTime());
		}
    }
}
