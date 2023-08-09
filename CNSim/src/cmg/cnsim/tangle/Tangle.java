package cmg.cnsim.tangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;


/**
 * 
 * Represents a DAG under Tangle 1.0
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Tangle implements IStructure {

	// The tangle is an array of integers representing sites. 
	// 0 is ID site, 1 and 2 are the IDs of the parents of the site.
	private int[][] tangle = new int[Config.getPropertyInt("sim.maxTransactions") + 1][3];
	
	//Used for processing
	private boolean[] marked = new boolean[Config.getPropertyInt("sim.maxTransactions") + 1]; 
	
	//Marks if each transaction is a tip.
	protected boolean[] isTip = new boolean[Config.getPropertyInt("sim.maxTransactions") + 1];
	
	//The height of the DAG
	protected int[] height = new int[Config.getPropertyInt("sim.maxTransactions") + 1];
	
	
	protected ArrayList<ArrayList<Integer>> children = new  ArrayList<ArrayList<Integer>>(Config.getPropertyInt("sim.maxTransactions") + 1);

	//A temporary structure for orphans
	protected Queue<TangleTx> orphans = new LinkedList<TangleTx>();
	//An alternative structure for orphans
	protected Queue<TangleSite> orphanList = new LinkedList<TangleSite>();
	
	
	protected int defaultWeight = 1;
	protected int maxTransaction = 0;
	protected int maxWeight = 0;
	protected int noTips = 0;

	//A random number generator.
	protected Random rand = new Random(Config.getPropertyLong("tangle.randomSeed"));
	
	//A TanglePerofmanceStats object
	protected TanglePerformanceStats perf = new TanglePerformanceStats();

	//An object that finds a cutset in the DAG (see documentation for that).
	protected TangleCutSetFinder cutSetFinder = new TangleCutSetFinder(Config.getPropertyFloat("tangle.wMinPercentile"), 
			Config.getPropertyFloat("tangle.wMaxPercentile"));

	
	/**
	 * A structure collecting statistics from handling the DAG.
	 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
	 */
	protected class TanglePerformanceStats {
		protected long totalUpdateWeightsTime = 0;
		protected long totalfindTipsTime = 0;
		protected long totalgetStartingParticles = 0;
		protected long totalTime;
	}


	/**
	 * A process that starts from a random particle in the cutset and navigates towards a tip.
	 * The system will run as many of these as the defined particles.
	 * @author Enterprise Systems Group (ESG) @ York University
	 *
	 */
	private class TangleTipFinder extends Thread {
		private Thread t;
		private int threadID;
		private String threadName;
		private CountDownLatch countdown;
		private int tip = -1;
		private Random rand;

		

		/**
		 * Constructor 
		 * @param name The name of the process on which instances will run.
		 * @param countDownLatch For synchronization.
		 */
		TangleTipFinder(String name, CountDownLatch countDownLatch) {
			threadName = "Thread-" + name;
			threadID = Integer.parseInt(name);
			countdown = countDownLatch;
			//rand = new Random(Parameters.randomSeed + threadID);
			rand = new Random(Config.getPropertyLong("tangle.randomSeed") + threadID);
		}


		private int getTip() {
			return(tip);
		}
		
		/**
		 * From the current position of the particle, find a tip, by recursively calling {@link #findTip(int) findTip(int)}
		 * until a tip is found.
		 * @param currPart The starting position (Transaction ID) where the particle is.
		 */
		private void findTip(int currPart) {
			//out(threadName + ": Moving a step with particle: " + currPart);
			if (Tangle.this.isTip(currPart)) {
				tip = currPart;
				// out(threadName + ": Particle " + currPart + " is tip.");
			} else {
				// out(threadName + ": Particle " + currPart + " is NOT a tip. Moving on.");
				try {
					findTip(takeOneMCMCStep(currPart));
				} catch (Exception e) {e.printStackTrace();}
			}
		}

		/**
		 * Particles takes on MCMC step towards the tip. It is assumed that the transaction is not a tip. 
		 * If there is only one child, return that.
		 * @param t The Transaction ID.
		 * @return -1 if the list of children is empty
		 * @throws Exception if the list of children is empty
		 */
		private int takeOneMCMCStep(int t) throws Exception {
			ArrayList<Integer> children;
			
			children  = getChildren(t);

			int nextHop = -1;

			if (!children.isEmpty()) {
				//pick a random child
				if (children.size()>1) {
					//out("Performing MCMC choice for " + t + " with children " + children);
					nextHop = getMCMCChildChoice(t, children);
					//nextHop = rand.nextInt(children.size() - 1);
				} else
					nextHop= children.get(0);
			}

			if (nextHop == -1) throw new Exception (threadName + ": MCMC step led to -1. Transaction is " + t +
					" with info (" + tangle[t][0] + ")[" + tangle[t][1] + "," + tangle[t][2] + "]" + 
					" and children size " + children.size() + " maxTransaction " + maxTransaction);

			return nextHop;
		}

		/**
		 * Randomly select a child among a list of candidate children. 
		 * TODO: Needs Testing
		 * @param parent The parent transaction ID
		 * @param children The children transaction IDs
		 * @return The Transaction ID of the chosen child.
		 * @throws Exception An unspecified problem, probably the children were empty?
		 */
		private int getMCMCChildChoice(int parent, ArrayList<Integer> children) throws Exception{
			ArrayList<Double> result = new ArrayList<Double>();
			int i=0;
			double sum = 0;
			int foundIndex = -1;
			//outnl("Prioritizing children:" + children);
			
			if (children.size() == 0) throw new Exception ("Trangle (getMCMCChildChoice)" + threadName + ": children list empty");
			
			i=0;
			
			// 1. Find the sum of the weights.
			for (int child:children) {
				//result.add(i,Math.exp(-TangleParameters.ALPHA * (tangle[parent][0] - tangle[child][0])));
				result.add(i,Math.exp(-Config.getPropertyFloat("tangle.alpha") * (tangle[parent][0] - tangle[child][0])));
				sum += result.get(i);
				i++;
			}
			//out(" Weights of children:" + result);
			
			// 2. Get a random proportion of that sum.
			double standard = rand.nextDouble() * sum;
			
			//3. Start adding children weights until you exceed the random proportion.
			sum = 0;
			for (i = 0;(i < result.size())&&(foundIndex == -1);i++) {
				sum += result.get(i);
				if (sum >= standard) {
					foundIndex = i;
				}
			}
			// out("Pick child index:" + (foundIndex) + " out of of [0," + (result.size()-1) + "] for " + standard);
			if (children.get(foundIndex) == -1) throw new Exception ("Trangle (getMCMCChildChoice)" + threadName + ": MCMC getChild led to -1");
			return(children.get(foundIndex));
		}

		/**
		 * Find a starting transaction where to place the particle. 
		 * If the maximum transaction has not reached StartingCutset, just start from genesis.
		 * Otherwise: find a random transaction such that its weight is 
		 * more than maxWeight*TangleParameters.WminPercentile (so: deep enough) AND
		 * less than maxWeight*TangleParameters.WaxPercentile (so: but not too deep)
		 * @return
		 * @throws Exception
		 */
		private int findStartingTx() throws Exception {
			int i;
			long count = 0;
			//if (maxTransaction < TangleParameters.StartingCutset) 
			if (maxTransaction < Config.getPropertyLong("tangle.startingCutset"))
				i = 0; // For a small tangle start from genesis.
			else {
				long temp = System.currentTimeMillis();
				do {
					i = rand.nextInt(maxTransaction);
					if (count++ > 500) {
						//throw new Exception ("Stuck trying to find startingTx within " + 
					//maxWeight*TangleParameters.WminPercentile + " and " + maxWeight*TangleParameters.WmaxPercentile);
						throw new Exception ("Stuck trying to find startingTx within " + 
								cutSetFinder.getLower() + " and " + cutSetFinder.getUpper());

					}
					} while ((tangle[i][0] < cutSetFinder.getLower()) || 
					(tangle[i][0] > cutSetFinder.getUpper())) ;

//				} while ((tangle[i][0] < maxWeight*TangleParameters.WminPercentile) || 
//						(tangle[i][0] > maxWeight*TangleParameters.WmaxPercentile)) ;
				perf.totalgetStartingParticles += System.currentTimeMillis() - temp;
			}

			if (i == -1) throw new Exception (threadName + ": starting transaction is -1");

			return i;
		}

		public void run() {
			int startTx;
			try {
				startTx = findStartingTx();
				findTip(startTx);
				countdown.countDown();
			} catch (Exception e) {e.printStackTrace();printTangleLiteral();System.exit(-1);}
			//out(threadName + ": Found starting particle: " + startTx + " starting MCMC process.");

		}

		public void start () {
			if (t == null) {
				t = new Thread (this, threadName);
				t.start ();
			}
		}

	}


	
	
	
	//
	//
	//
	// P U B L I C     S E R V I C E S  
	//
	//
	//

	
	/**
	 * Constructor. Creates the Genesis node and initializes lists.
	 */
	public Tangle(){
		setupGenesis();
		//for (int i = 0;i < Parameters.maxTransactions;i++) {
		for (int i = 0;i < Config.getPropertyInt("sim.maxTransactions");i++) {
			children.add(new ArrayList<Integer>());
		}
	}
	

	/**
	 * Get the 2-dimension integer array that represents the tangle (e.g., for testing purposes)
	 * @return The 2-dimension integer array that represents the tangle
	 * @author Sotirios Liaskos
	 */
	protected int[][] getTangle() {
		return(tangle);
	}


	
	@Override
	public String[] printStructure() {
		ArrayList<String> result = new ArrayList<String>();

		for(int i=0;i<= maxTransaction;i++) {
			if (tangle[i][0] != 0) {
				result.add(i + "," + tangle[i][0] + "," + tangle[i][1] + "," + tangle[i][2] + "," + isTip[i] + "," + height[i]);
			}
		}

		for (TangleTx tx:  orphans) {
			result.add(tx.transaction + "o," + 0 + "," + tx.parent1 + "," + tx.parent2 + "," + false + "," + 0);
		}

		return(result.toArray(new String[0]));
	}

	
	public void printTangleLiteral() {
		System.out.print("{");
		for(int i=0;i<= maxTransaction;i++) {
			System.out.println("{" + getTangle()[i][0] + "," + getTangle()[i][1] + "," + getTangle()[i][2] + "},");
		}
		System.out.println("}");
	}
	
	

	/**
	 * The current number of tips in the Tangle
	 * @return The number of tips in the Tangle.
	 */
	public int getTipCount() {
		return noTips;
	}

	/**
	 * Returns the height of a transaction in the Tangle. It is zero if the transaction does not exist or is in the orphans, non-zero otherwise.  
	 * @param tx The transaction in question.
	 * @return The weight of the transaction. 
	 * @throws Exception When exceeding max ID.
	 */
	public int getTxHeight(Transaction tx) throws Exception {
		if (tx.getID() > maxTransaction) throw new Exception ("Tangle (getTxHeight(Transaction)): Transaction ID (" + tx.getID() + ") exceeds max ID");
		return height[tx.getID()];
	}


	/**
	 * Returns the weight of a transaction in the Tangle. It is zero if the transaction does not exist or is in the orphans, non-zero otherwise.  
	 * @param tx The transaction in question. 
	 * @return The weight of the transaction. 
	 */
	public int getTxWeight(Transaction tx) { //throws Exception{
		//if (tx.getID() > maxTransaction) throw new Exception ("Tangle (getTxWeight(Transaction)): Transaction ID (" + tx.getID() + ") exceeds max ID");
		return tangle[tx.getID()][0];
	}


	/**
	 * Adds brand new client transaction to the tangle. 
	 * @param t The Transaction object to be added.
	 * @return The TangleSite object that results from the addition of the Transaction to the Tangle.  
	 */
	public TangleSite addNewTransaction(Transaction t) {
		try {
			return(new TangleSite(t, addToTangleInt(t.getID())));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return(null);
		
	}


	/**
	 * Adds a validated transaction to Tangle. Validated transactions come in the form of TangleSites objects (i.e., transactions augmented with two parents).
	 * If both identified parents have been appended in the Tangle, the transaction is appended as well, otherwise the Site is added in the Orphans list. 
	 * @param t Requires a {@link TangleSite TangleSite} object
	 * @return true if the transaction was appended and false if it was added in the orphans.
	 */
	public boolean addValidatedTransaction(ITxContainer t) {
		return receiveValidatedTransaction(t.getID(),
				((TangleSite) t).getParents()[0],
				((TangleSite) t).getParents()[1]
				);
	}


	/**
	 * The number of transactions currently in the tangle, EXCLUDING orphans 
	 * @return The number of transactions that have successfully been added in the tangle 
	 */
	public int getTransactionCount() {
		return (maxTransaction + 1);
	}


	public String getPerformanceStatsHeaders() {
		return("TotalTime" + "," +
				"UpdateWeightsTime" + "," +
				"FindingTipsTime" + "," +
				"FindingStartingParticlesTime"
				);
	}

	
	public String getPerformanceStats() {
		return(perf.totalTime + "," +
				perf.totalUpdateWeightsTime + "," +
				perf.totalfindTipsTime + "," +
				perf.totalgetStartingParticles
				);
	}
	

	

	/**
	 * Create the initial transaction.
	 */
	private void setupGenesis() {
		// Genesis referred to with 0
		// -1 is the abyss...
		tangle[0][0] = defaultWeight; //weight
		tangle[0][1] = -1; //parent 1
		tangle[0][2] = -1; // parent 2

		isTip[0] = true;
		height[0] = 0;
		cutSetFinder.updateFrequencies(tangle[0][0],0);
		this.noTips++;

		maxTransaction = 0;
		maxWeight = tangle[0][0];
	}


	/**
	 * See {@link #addNewTransaction(Transaction) addNewTransaction(Transaction)} for logic.
	 * @param trans The ID of the transaction to be considered added to the Tangle
	 * @return The parents of the transactions as identified by the corresponding procedures.
	 * @throws Exception Found tips that are not tips.
	 */
	private int[] addToTangleInt(int trans) throws Exception {
		long temp = System.currentTimeMillis();
		
		int[] parents;

		// Is it a founder?
		if (trans <= Config.getPropertyInt("tangle.numOfFounders")) {
		//if (trans <= TangleParameters.NumofFounders) {
			//out("Founder transaction: " + trans);
			
			placeInTangle(trans, 0,0);

			parents = new int[2];
			parents[0] = 0;
			parents[1] = 0;
		} else {
			//out("New transaction: " + trans);

			long temp1 = System.currentTimeMillis();
			parents = getTwoTips();
			if ((!isTip[parents[0]])||(!isTip[parents[1]])) {
				throw new Exception("Tangle (addToTangleInt): Found two 'tips' one or both of which are not marked as tips");
			}
			perf.totalfindTipsTime += System.currentTimeMillis() - temp1;
			placeInTangle(trans, parents[0], parents[1]);
		}
		perf.totalTime +=  System.currentTimeMillis() - temp;
		
		
		
		return(parents);
	}

	
	/**
	 * An eligible transaction with parents identified is placed in the Tangle. 
	 * 
	 * @param trans The transaction ID
	 * @param parent1 The first parent's transaction ID
	 * @param parent2 The second parent's transaction ID
	 * @throws Exception The new transaction is marked as a tip.
	 */
	private void placeInTangle(int trans, int parent1, int parent2) throws Exception {

		//System.out.println("Adding transaction: " + trans + " with parents " + parent1 + " and " + parent2);

		maxWeight += defaultWeight;

		if (trans > maxTransaction)
			maxTransaction = trans;

		tangle[trans][0] = defaultWeight;
		tangle[trans][1] = parent1;
		tangle[trans][2] = parent2;
		
		cutSetFinder.increaseFrequency(defaultWeight);

		height[trans] = Math.max(height[parent1], height[parent2]) + 1;
		
		if (isTip[parent1]) {
			isTip[parent1] = false;
			noTips--;
		} 

		if (isTip[parent2]) {
			isTip[parent2] = false;
			noTips--;
		}

		if (!isTip[trans]) {
			isTip[trans] = true;
			noTips++;
		} else {
			throw new Exception("Tangle (placeInTangle): newly added tip (" + trans + ") already marked as tip?");
		}

		children.get(parent1).add(trans);
		children.get(parent2).add(trans);

		long temp = System.currentTimeMillis();

		markNodesforWeightUpdate(trans);
		updateWeights(defaultWeight);
		perf.totalUpdateWeightsTime += System.currentTimeMillis() - temp;
		processOrphans();
	}

	
	/**
	 * Updates the weights of all those transactions that have been marked for update.
	 * @param weight The weight by which to increase the weights.
	 * @throws Exception If the transaction marked for change does not exist in the tangle.
	 */
	private void updateWeights(int weight) throws Exception {
		int i;

		// TODO: Can you safely parallelize this???

		
		for (i = 0; i <= maxTransaction;i++) {
			if (marked[i]) {
				if (tangle[i][0] == 0) throw new Exception ("Tangle (updateWeights): marked for update but not existing transaction:" + i);
				tangle[i][0] += weight;
				marked[i] = false;
				//System.out.println("Updating weights for transation: " + i + " with maxTransacton " + maxTransaction);
				try {
					cutSetFinder.updateFrequencies(tangle[i][0], tangle[i][0] - weight);
				} catch (Exception e) {e.printStackTrace();}
			}
		}
	}


	/**
	 * [Recursive] Beginning from every node whose weight is less than 
	 * the maximum weight in the Tangle times mark it for a weight update
	 * and proceed to do the same to the parents. Ignore if each of them has already been visited.
	 * @param currTrans The transaction whose weight may need to be updated.
	 */
	private void markNodesforWeightUpdate(int currTrans) {

		// TODO: Can you safely parallelize this??? 

		//Proceed with the update if any of the following is true: 
		// (a) the transaction's weight is small enough that a particle may be set on it 
		// (b) We have not reached the threshold 'StartingCutset' below which all particles start from the genesis
		//if ((tangle[currTrans][0] < maxWeight*TangleParameters.WmaxPercentile) ||
		if ((tangle[currTrans][0] < cutSetFinder.getUpper()) ||
				//currTrans < TangleParameters.StartingCutset) {
				currTrans < Config.getPropertyLong("tangle.startingCutset")) {
			//If the parent has not been visited already
			if (!marked[ tangle[currTrans][1] ]) {
				marked[ tangle[currTrans][1] ] = true; //mark it!
				if (tangle[currTrans][1] > 0)
					markNodesforWeightUpdate(tangle[currTrans][1]);
			}

			if (!marked[ tangle[currTrans][2] ]) {
				marked[ tangle[currTrans][2] ] = true; //mark it!
				if (tangle[currTrans][2] > 0)
					markNodesforWeightUpdate(tangle[currTrans][2]);
			}
		}

	}


	//
	// R E C E I V E  V A L I D A T E D  T R A N S A C T I O N 
	//


	/**
	 * Helper structure for organizing orphans. Avoids TangleSite which is heavier.  
	 * @author Enterprise Systems Group (ESG) @ York University
	 *
	 */
	class TangleTx {
		public int transaction;
		public int parent1;
		public int parent2;
		TangleTx(int t, int p1,int p2){
			transaction = t;
			parent1 = p1;
			parent2 = p2;
		}
	}

	
	/**
	 * See {@link Tangle#addValidatedTransaction(ITxContainer) Tangle#addValidatedTransaction(ITxContainer)} for logic 
	 * @param trans Transaction ID to be added.
	 * @param parent1 The first parent of the transaction 
	 * @param parent2 The second parent of the transaction 
	 * @return true if it was successfully appended to the Tangle, false if it joined the orphans.
	 */
	private boolean receiveValidatedTransaction(int trans, int parent1, int parent2) {
		long temp = System.currentTimeMillis();
		//outnl("Considering Arrived Transaction: (" + trans + ")[" + parent1 +"," + parent2 + "]: " );

		// Issue 1: Transaction already exists: ignore.
		if (tangle[trans][0] != 0) {
			//out("Already exists ignoring.");
			return false;
		}

		// Issue 2: One of the children does not exist and is not a founder -- add to orphans
		//if(((tangle[parent1][0] == 0) || (tangle[parent2][0] == 0)) && (trans> TangleParameters.NumofFounders)) {
		if(((tangle[parent1][0] == 0) || (tangle[parent2][0] == 0)) && (trans> Config.getPropertyInt("tangle.numOfFounders"))) {
			//out("Missing a child. Adding to/ keeping in orphans.");
			TangleTx tx = new TangleTx(trans,parent1,parent2);
			if (!orphans.contains(tx))
				orphans.add(tx);
			return false;
		}

		//Otherwise: the transaction can just be placed in the Tangle.
		//out("Placing Normally.");
		try {
			placeInTangle(trans, parent1, parent2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		perf.totalTime +=  System.currentTimeMillis() - temp;
		return(true);
	}


	private void processOrphans() {
		Queue<TangleTx> orphanCopy;

		boolean changing;

		do {
			changing = false;
			orphanCopy = new LinkedList<TangleTx>(orphans);

			//System.out.println("Processing: " + orphans.size() + " orphans.");

			for (TangleTx tx:  orphanCopy) {

				//Case One - Transaction in Tangle already with the correct children: remove from orphans
				if ((tangle[tx.transaction][0] != 0) && (tangle[tx.transaction][1] == tx.parent1) && (tangle[tx.transaction][0] == tx.parent2)) {
					orphans.remove(tx);
				}

				//Case Two - Transaction not in tangle but has children in place (incl. genesis)
				if ((tangle[tx.transaction][0] == 0) && (tangle[tx.parent1][0] != 0) && (tangle[tx.parent2][0] != 0)) {
					//System.out.println("Placing orphan " + tx.transaction);
					try {
						placeInTangle(tx.transaction, tx.parent1, tx.parent2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					changing = true;
					//Orphan removal will take care of itself. But just in case:
					orphans.remove(tx);
				}
			}
		} while (changing);

	}




	//
	// T R A N S A C T I O N   A P P E N D 
	//


	/**
	 * Identifies two candidate tips. For each particle a separate {@link TangleTipFinder TangleTipFinder} is instantiated (each in a separate thread).
	 * The particles then independently follow the route to the tip. From the result two are selected randomly. 
	 * If it is only one, the transaction has only one parent.  
	 * @return The parent(s) 
	 * @throws Exception When no tips are found.
	 */
	private int[] getTwoTips() throws Exception {

		ArrayList<Integer> tips = new ArrayList<Integer>();
		Set<Integer> tipSet = new HashSet<Integer>();
		ArrayList<TangleTipFinder> tipFinders = new ArrayList<TangleTipFinder>();
		int[] p  = new int[2];

		//out("Begin trying to find tips.");

		//Create as many TangleTipFinders as the required particles.
		//CountDownLatch cD = new CountDownLatch(TangleParameters.NumofParticles);
		CountDownLatch cD = new CountDownLatch(Config.getPropertyInt("tangle.numOfParticles"));
		int o;
		//for (o = 0;o < TangleParameters.NumofParticles;o++) {
		for (o = 0;o < Config.getPropertyInt("tangle.numOfParticles");o++) {
			tipFinders.add(new TangleTipFinder("" + o,cD));
		}

		// Start the threads
		for (TangleTipFinder f : tipFinders) {
			f.start();
		}

		// Wait for the threads to finish
		try {
			cD.await();
		} catch (InterruptedException e) {e.printStackTrace();}

		// Get the tip that each TangleTipFinder returns
		for (TangleTipFinder f : tipFinders) {
			f.join();
			tips.add(f.getTip());
		}


		Collections.shuffle(tips);//Shuffle the entire list to pick the first two/
		tipSet = new HashSet<Integer>(tips);// Get the unique values

		//out(" Threads ended. Resulting tipset: " + tipSet + " from " + tips);


		if (tipSet.size()>=2) { // There are 2 or more tips pick randomly two 
			//p = tipSet.stream().mapToInt(i->i).toArray();
			p[0] = tips.get(0);
			p[1] = tips.get(1);
		} else if (tipSet.size()==1) { // Only one tip found
			p[0] = tips.get(0);
			p[1] = tips.get(0);

			//				Pick random for second one. 
			//		    	int randPick;
			//		    	do {
			//		    		randPick = rand.nextInt(maxTransaction);
			//		    	} while ((tangle[randPick][0] == 0)||(randPick == p[0])); // transaction exists and is different
			//		    	
			//		    	//out("Picking 2nd tip randomly: " + randPick);
			//		    	p[1] = randPick;

		} else {
			//out("Problem, no tips!");
			p[0] = -1;
			p[1] = -1;
			throw new Exception("Tangle#getTwoTips(): no tips found.");
		}
		return(p);
	}



	/**
	 * Returns a list of IDs of all the children (Sites that point at the Transaction) of a transaction with id t. 
	 * @param t The ID of the transaction in question
	 * @return An integer ArrayList of the children of t.
	 */
	private ArrayList<Integer> getChildren(int t) {
		return (children.get(t));
	}


	private boolean isTip(int t) {
		if (t>=0) 
			return (isTip[t]);
		else 
			return (false);
	}












}






//
//
// D E B U G     H E L P E R S
//
//

//private void hold(int ms) {
//	try {
//		Thread.sleep(ms);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
//}
//
//private void out(String s) {
//	//System.out.println(s);
//	//hold(0);
//}
//
//private void outnl(String s) {
//	//System.out.print(s);
//}

