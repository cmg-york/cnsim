package cmg.cnsim.tangle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import cmg.cnsim.engine.Reporter;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.transaction.Transaction;

public class TangleReporter extends Reporter {
	protected static ArrayList<String> structureLog = new ArrayList<String>();
	protected static ArrayList<Transaction> trackedTransactions; 
	protected static boolean TangleStateOpened;
	
	protected static FileWriter timeAdvancementwriter;
	private static OutputStreamWriter tipCountWriter;
	
	
	static {
		structureLog.add("Node, Transaction, Parent1, Parent2, SimTime, AddType");
		TangleStateOpened = false;
	}

	
	
	//
	//
	// EVENTS TO BE LOGGED
	//
	//
	
	public static void appendTangleTransaction(
			int nodeInvolved, 
			int txAdded, 
			int parent1, 
			int parent2, 
			float simTime,
			String addType) {
		structureLog.add(nodeInvolved + "," + 
						txAdded + "," + 
						parent1 + "," +
						parent2 + "," +
						simTime  + "," +
						addType
					);
	}
	
	public static void timeAdvancement(INode n) {
		try {
			// Write the number of tips
			tipCountWriter.write(n.getID() + "," + ((TangleNode) n).getTipCount() + "," + Simulation.currTime + System.lineSeparator());
			
			for (Transaction t : trackedTransactions) {
				timeAdvancementwriter.write(n.getID() + "," + 
											t.getID() + "," +
											((TangleNode) n).getTxWeight(t) + "," +
											Simulation.currTime + System.lineSeparator());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	
	
	//
	// SETUP 
	// Create files and close them.
	//
	
	public static void setUp() {
		try {
			timeAdvancementwriter = new FileWriter(Reporter.path + "TangleTimeAdvancement - " + Reporter.runId + ".csv", true);
			timeAdvancementwriter.write( "NodeID, TransID, Weight, SimTime" + System.lineSeparator());
		
			tipCountWriter = new FileWriter(Reporter.path + "TangleTipCounts - " + Reporter.runId + ".csv", true);
			tipCountWriter.write( "NodeID, TipNo, SimTime" + System.lineSeparator());
		} catch (IOException e) {e.printStackTrace();}
		
	}
	
	public static void closeReporting() {
		try {
			timeAdvancementwriter.close();
			tipCountWriter.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void setTrackedTransactions(ArrayList<Transaction> ts) {
		trackedTransactions = ts;
	} 

	
	
	//
	// FLUSH to file routines
	//
	
	public static void flushStructReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(Reporter.path + "TangleLog - " + Reporter.runId + ".csv");
			for(String str: structureLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void flushTangleState(NodeSet ns) {
		FileWriter writer;
		try {
			writer = new FileWriter(Reporter.path + "TangleState - " + Reporter.runId + ".csv", true);
			if (!TangleStateOpened) {
				writer.write( "NodeID, TransID, Weight, Parent1, Parent2, isTip, level, SimTime" + System.lineSeparator());
				TangleStateOpened = true;
			}
			for (INode n : ns.getNodes()) {
				ArrayList<String> tangleState = new ArrayList<String>(Arrays.asList(n.getStructure().printStructure()));
				for(String str: tangleState) {
					  writer.write(n.getID() +"," + str + "," + Simulation.currTime + System.lineSeparator());
				}
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	public static void flushNodesList(NodeSet ns) {
		FileWriter writer;
		try {
			writer = new FileWriter(Reporter.path + "Nodes - " + Reporter.runId + ".csv");
			writer.write( "NodeID, Power_Watts, HashPower_GHs, ElectricityCost_USDpkWh, CostperGH, Connectedness_bps, TotalGH" + System.lineSeparator());
			ArrayList<String> nodeData = new ArrayList<String>(Arrays.asList(ns.printNodeSet()));
			for(String str: nodeData) {
				writer.write(str + System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
