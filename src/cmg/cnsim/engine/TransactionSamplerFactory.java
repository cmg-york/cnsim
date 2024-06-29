package cmg.cnsim.engine;

public class TransactionSamplerFactory {
	
	public AbstractTransactionSampler getSampler(String path, Long seed, Sampler outerSampler) throws Exception {
		
		boolean hasWorkloadSeed = (seed != null);

		if (path != null) {
			Debug.p("Creating file-based workload sampler");
			if (hasWorkloadSeed) {
				return(new FileBasedTransactionSampler(path, new StandardTransactionSampler(outerSampler, seed)));
			} else {
				return(new FileBasedTransactionSampler(path, new StandardTransactionSampler(outerSampler)));
			}
		} else {
			Debug.p("Creating random workload sampler");
			if (hasWorkloadSeed) {
				return(new StandardTransactionSampler(outerSampler, seed));
			} else {
				return(new StandardTransactionSampler(outerSampler));
			}
		}
	}
}
