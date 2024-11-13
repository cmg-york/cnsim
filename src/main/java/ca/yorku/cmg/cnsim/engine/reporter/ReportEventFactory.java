package ca.yorku.cmg.cnsim.engine.reporter;

import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.event.Event_Report_BeliefReport;

public class ReportEventFactory {
	public void scheduleBeliefReports_Interval(long interval, Simulation sim, long offset) {
		long t, max;
		t = interval;
		max = sim.getLatestKnownEventTime() + offset;
		while (t <= max) {
			sim.schedule(new Event_Report_BeliefReport(t));
			t += interval;
		}
	}

	public void scheduleBeliefReports_Count(long count, Simulation sim, long offset) {
		long interval = (sim.getLatestKnownEventTime() + offset)/count;
		scheduleBeliefReports_Interval(interval, sim, offset);
	}
	
}
