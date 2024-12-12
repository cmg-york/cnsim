package ca.yorku.cmg.cnsim.engine;

public class TransactionSamplerFactory {
	
	public AbstractTransactionSampler getSampler(String path, 
			Sampler outerSampler, Simulation sim) throws Exception {

		if (path != null) {
			Debug.p("    Creating file-based workload sampler");
			return(new FileBasedTransactionSampler(path, new StandardTransactionSampler(outerSampler, sim.getSimID())));
		} else {
			Debug.p("    Creating random workload sampler");
				return(new StandardTransactionSampler(outerSampler, sim.getSimID()));
		}
	}
}
