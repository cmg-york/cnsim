package ca.yorku.cmg.cnsim.engine;

import ca.yorku.cmg.cnsim.engine.event.Event_SeedUpdate;

public class NodeSamplerFactory {
	
	
	public AbstractNodeSampler getSampler(
			String path,
			String seedChain,
			String changeTimes,
			Sampler sampler,
			Simulation sim
			) throws Exception {
		
        //Check requirements
        
		boolean hasPath = (path != null);
	    	
		
        boolean hasNodeSeeds = false;
        long seeds[] = null;
        if ((seedChain != null && !seedChain.isEmpty())) {
        	seeds = Config.parseStringToArray(seedChain);
        	hasNodeSeeds = true;
        }
        
        boolean hasSwitchTimes = false;  
        long switchTimes[] = null;
        if ((seedChain != null && !seedChain.isEmpty())) {
        	hasSwitchTimes = true;
        	switchTimes = Config.parseStringToArray(Config.getPropertyString("node.sampler.seedUpdateTimes"));
        }


        //TODO: Validation code
    	
    	AbstractNodeSampler nodeSampler;
        
        if (hasPath) {
        	if (hasNodeSeeds) {
        		nodeSampler = new FileBasedNodeSampler(path, new StandardNodeSampler(sampler,seeds));
        	} else {
        		nodeSampler = new FileBasedNodeSampler(path, new StandardNodeSampler(sampler));
        	}
        } else {
        	if (hasNodeSeeds) {
        		nodeSampler = new StandardNodeSampler(sampler,seeds);
        	} else {
        		nodeSampler = new StandardNodeSampler(sampler);
        	}
        }
        
        //Schedule the switchover events
    	if (hasSwitchTimes) {
    		if (!hasNodeSeeds) {
    			throw new Exception("Error in NodeSamplerFactory: seed switch times given (" + Config.getPropertyString("node.sampler.seedUpdateTimes") +  ") but not seeds to switch around.");
    		} else {
    	        //Schedule seed change events
    	        for (int i = 0; i < switchTimes.length; i++) {
    	        	Debug.p("Scheduling sampler with chain [...] to swich to next seed at " + switchTimes[i]);
    	            sim.schedule(new Event_SeedUpdate(nodeSampler, switchTimes[i]));
    	        }
    		}
    	}
    	
    	return(nodeSampler);
        
	}
}
