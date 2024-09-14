package cmg.cnsim.bitcoin;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cmg.cnsim.engine.reporter.Reporter;

/**
 * Handles reporting for Bitcoin simulators in addition to the reports generated by the engine's Reporter, which it extends.
 * This specialized reporter reports also on:
 * 1. The state of the blockchain (block IDs sequences and orphans).
 * 2. The state of the block (various information).
 * To be used from e.g., within BitcoinBlock for more specific reporting. 
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 * 
 */
public class BitcoinReporter extends Reporter {
	protected static ArrayList<String> blockLog = new ArrayList<String>();
	protected static ArrayList<String> structureLog = new ArrayList<String>();
	protected static FileWriter blockWriter;

	
	static {
		blockLog.add("SimID, SimTime,SysTime,NodeID,"
				+ "BlockID,ParentID,Height,BlockContent,"
				+ "EvtType,Difficulty,Cycles");
		structureLog.add("SimID, SimTime, SysTime, NodeID, BlockID, ParentBlockID, Height, Content, Place");
	}
	
	
	/**
	 * Adds a line to the blockhain state report.
	 * @param blockchain A string showing the state of the blockchain (e.g., a list of block IDs).
	 * @param orphans A string showing the state of the orphans (e.g., a list of block IDs).
	 * @author Sotirios Liaskos
	 */
	public static void reportBlockChainState(String[] blockchain, String[] orphans) {
		for (String s :blockchain) {
			//s = SimTime + "," + SysTime + "," + blockID + "," + s + ",blockchain";
			structureLog.add(s);
		}
		for (String s :orphans) {
			//s = SimTime + "," + SysTime + "," + blockID + "," + s + ",orphans";
			structureLog.add(s);
		}
	}
	

	/**
	 * Adds a line to the blockhain state report. TODO: complete this.
	 * @param simTime ...
	 * @param sysTime ...
	 * @param nodeID ...
	 * @param blockID ...
	 * @param parentID ...
	 * @param height ...
	 * @param txInvolved ...
	 * @param blockEvt ...
	 * @param difficulty ...
	 * @param cycles ...
	 * @author Sotirios Liaskos
	 */
	public static void reportBlockEvent(
			int simID,
			long simTime, long sysTime, int nodeID,
			int blockID, int parentID, int height, String txInvolved,
			String blockEvt,
			double difficulty, //Difficulty: the difficulty under which the block was validated.
			double cycles) { //Cycles: the number of cycles dedicated to validate the block. 
		blockLog.add(simID + "," +
				simTime + "," + 
				sysTime + "," +
				nodeID + "," +
				blockID + "," +
				parentID + "," +
				height + "," +
				txInvolved + "," +
				blockEvt + "," + //AppendedToChain, AddedAsOrphan, RemovedFromOrphan
				difficulty + "," +
				cycles
					);
	}

	
	/**
	 * Save Block report to file. File name is "BlockLog - [Simulation Date Time].csv"
	 * 
	 * @author Sotirios Liaskos
	 */
	public static void flushBlockReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(Reporter.path + "BlockLog - " + Reporter.runId + ".csv");
			for(String str: blockLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	/**
	 * Save Blockchain report to file. File name is "Blockchain - [Simulation Date Time].csv"
	 * 
	 * @author Sotirios Liaskos
	 */
	public static void flushStructReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(Reporter.path + "StructureLog - " + Reporter.runId + ".csv");
			for(String str: structureLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
