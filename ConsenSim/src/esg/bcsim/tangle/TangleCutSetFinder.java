package esg.bcsim.tangle;

import java.util.TreeMap;
import java.util.Map.Entry;

public class TangleCutSetFinder {
	
	private int totalEntries = 0;
	private float lowerPercent;
	private float upperPercent;

	private int lower = 0;
	private int upper = 0;

	private TreeMap<Integer,Integer> hist = new TreeMap<Integer,Integer>();
	
	public TreeMap<Integer,Integer> getTreeMap() {
		return(hist);
	}
	
	public int getTotalEntries() {
		return(totalEntries);
	}
	
	public TangleCutSetFinder(float lowerPercent, float upperPercent){
		if (lowerPercent >= upperPercent) 
			throw new IllegalArgumentException("TangleCutSetFinder (Constructor): lowerPercent must be lower than upperPercent");
		if ((lowerPercent > 1.0) || (lowerPercent < 0.0))  
			throw new IllegalArgumentException("TangleCutSetFinder (Constructor): lowerPercent must be in the interval [0,1]");
		if ((upperPercent > 1.0) || (upperPercent < 0.0))  
			throw new IllegalArgumentException("TangleCutSetFinder (Constructor): upperPercent must be in the interval [0,1]");
		this.lowerPercent = lowerPercent; //Shallow
		this.upperPercent = upperPercent; //Deep
	}
	
	public void setBounds(float lowerPercent, float upperPercent){
		if (lowerPercent >= upperPercent) 
			throw new IllegalArgumentException("TangleCutSetFinder (Constructor): lowerPercent must be lower than upperPercent");
		if ((lowerPercent > 1.0) || (lowerPercent < 0.0))  
			throw new IllegalArgumentException("TangleCutSetFinder (Constructor): lowerPercent must be in the interval [0,1]");
		if ((upperPercent > 1.0) || (upperPercent < 0.0))  
			throw new IllegalArgumentException("TangleCutSetFinder (Constructor): upperPercent must be in the interval [0,1]");
		this.lowerPercent = lowerPercent; //Shallow
		this.upperPercent = upperPercent; //Deep
		if (totalEntries>0) calcPercentiles(totalEntries*lowerPercent, totalEntries*upperPercent);
	} 
	
	private void reduceFrequency(int weight, int by) throws IllegalStateException {
		Integer freq;
		
		if (weight == 0) return; //Happens only for genesis.
		
		freq = hist.get(weight);
		
		if (freq != null) {
			if (freq==0) throw new IllegalStateException("TangleCutSetFinder (reduceFrequency): entry with zero frequency.");
			if (freq-by == 0) {
				hist.remove(weight);
			} else {
				hist.put(weight,freq-by);
			}
			totalEntries--;
		} else {
			throw new IllegalStateException("TangleCutSetFinder (reduceFrequency): reducing the frequency of a weight that does not exist");  
		}
	}

	
	public void increaseFrequency(int weight) {
		Integer freq = hist.get(weight);
		if (freq != null) {
			hist.put(weight,freq+1);
		} else {
			hist.put(weight,1);
		}
		totalEntries++;
	}
	
	public void updateFrequencies (int newWeight, int oldWeight) throws IllegalStateException {
		Integer freq;

		freq = hist.get(newWeight);
		if (freq != null) {
			hist.put(newWeight,freq+1);
		} else {
			hist.put(newWeight,1);
		}
		try {
			reduceFrequency(oldWeight,1);
		} catch(Exception e) {e.printStackTrace();System.exit(-1);}
		
		totalEntries++;
		//System.out.println("Calc percentiles: " + totalEntries*lowerPercent + " to " + totalEntries*upperPercent);
		//System.out.println("Total Entries: " + totalEntries);
		if (totalEntries==1) {System.out.println("Hist is only 1 entry:"); printTreeMap();}
		calcPercentiles(totalEntries*lowerPercent, totalEntries*upperPercent);
	}
	
	public int getLower() {
		return(lower);
	}

	public int getUpper() {
		if (upper <= lower) {
			//System.out.println("LBound: " + lower + " UBound: " + upper);
			return (-1);
		} else {
			return(upper);	
		}
	}
	
	public int[] calcPercentiles(float low, float high) {
		if (low >= high) throw new IllegalStateException("TangleCutSetFinder (calcPercentiles): lower is higher than upper cutset bound");
		int[] res = {-1,-1};
		Integer weight;
		Integer count;
		int sum = 0;
		
		for (Entry<Integer, Integer> entry : hist.entrySet()) {
			weight = entry.getKey();
			count = entry.getValue();
			sum+= count;
		    if ((sum >= low) && (res[0] == -1)) {
		    	res[0] = weight;
		    }
		    if ((sum >= high) && (res[1] == -1)) {
		    	res[1] = weight;
		    }
		}
		
		this.lower = res[0];
		this.upper = res[1];
		return (res);
	}
	
	public void printTreeMap() {
		for (Entry<Integer, Integer> entry : hist.entrySet()) {
			int key = entry.getKey();
			int value = entry.getValue();
			System.out.println("Key: " + key + ". Value: " + value);
		}
	}
	
}
