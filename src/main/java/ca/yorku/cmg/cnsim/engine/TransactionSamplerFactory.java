package ca.yorku.cmg.cnsim.engine;

public class TransactionSamplerFactory {
	
	public AbstractTransactionSampler getSampler(String path, 
			Long seed, Boolean updateFlag, 
			Sampler outerSampler, Simulation sim) throws Exception {
		
		boolean hasWorkloadSeed = (seed != null);

		if (path != null) {
			Debug.p("    Creating file-based workload sampler");
			if (hasWorkloadSeed) {
				return(new FileBasedTransactionSampler(path, new StandardTransactionSampler(outerSampler, seed, updateFlag, sim.getSimID())));
			} else {
				return(new FileBasedTransactionSampler(path, new StandardTransactionSampler(outerSampler)));
			}
		} else {
			Debug.p("    Creating random workload sampler");
			if (hasWorkloadSeed) {
				return(new StandardTransactionSampler(outerSampler, seed, updateFlag, sim.getSimID()));
			} else {
				return(new StandardTransactionSampler(outerSampler));
			}
		}
	}
}
