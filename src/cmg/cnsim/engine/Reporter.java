package cmg.cnsim.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.Transaction;

/**
 * Handles all main measurement and reporting for simulations. 
 * Meant to be used via its static methods.
 * Supports three log actions which add a line to the corresponding file:
 * 1. Events: adds a log line every time an event is processed.
 * 2. Transactions: adds a log line for every transaction arrival event.
 * 3. Nodes: adds a new line for every node known to the simulator. This happens at the end of the simulator.
 * Additional measurements and files can be produced by other classes (e.g., Nodes, Structures).
 * 
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University`
 * 
 */
public class Reporter {
	// Each of the arraylists below contain a line in the output
	protected static ArrayList<String> inputTxLog = new ArrayList<String>();
	protected static ArrayList<String> eventLog = new ArrayList<String>();
	protected static ArrayList<String> nodeLog = new ArrayList<String>();
	protected static ArrayList<String> netLog = new ArrayList<String>();
	protected static ArrayList<String> beliefLog = new ArrayList<String>();

	protected static String runId;
	protected static String path;
	protected static String root = "./log/";

	static {
		root = Config.getPropertyString("sim.output.directory");

		//ID the run
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm.ss");  
		LocalDateTime now = LocalDateTime.now();
		runId = dtf.format(now);
		path = root + runId + "/";
		new File(path).mkdirs();
		FileWriter writer;
		try {
			writer = new FileWriter(root + "LatestFileName.txt");
			writer.write(runId + "\n");
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
		
		//Prepare the reporting structures
		eventLog.add("SimID, EventID, SimTime, SysTime, EventType, Node, Object");
		inputTxLog.add("SimID, TxID, Size (bytes), Value (coins), ArrivalTime (ms)");
		nodeLog.add("SimID, NodeID, HashPower (GH/s), ElectricPower (W), ElectricityCost (USD/kWh), TotalCycles");
		netLog.add("SimID, From (NodeID), To (NodeID), Bandwidth (bps), Time (ms from start)");
		beliefLog.add("SimID, Node ID, Transaction ID, Believes, Time (ms from start)");
	}
	
	public static String getRunId() {
		return(runId);
	}
	
	/**
	 * Adds a line to the event log with information about the event.
	 *  
	 * @param simID The simulation ID.
	 * @param evtID ID of the event.
	 * @param simTime Simulation time in which the event is happening.
	 * @param sysTime Real time in which the event is happening.
	 * @param evtType The Type of the event.
	 * @param nodeInvolved The {@linkplain Node} involved in the event.
	 * @param objInvolved The object ID involved in the event (transaction, block, etc).
	 * @author Sotirios Liaskos
	 */
	public static void addEvent(int simID, long evtID, long simTime, long sysTime, 
			String evtType, int nodeInvolved, long objInvolved) {
		eventLog.add(simID + "," + 
					evtID + "," + 
					simTime + "," + 
					sysTime + "," +
					evtType + "," +
					nodeInvolved + "," +
					objInvolved);
	}

	/**
	 * Adds an entry to the transaction log with information about the transaction.
	 * 
	 * @param simID The simulation ID.
	 * @param txID Transaction ID
	 * @param size Transaction size in bytes
	 * @param value Transaction value in local tokens.
	 * @param simTime Time transaction arrived in system
	 * @author Sotirios Liaskos
	 */
	public static void addTx(int simID, long txID, float size, float value, long simTime) {
		inputTxLog.add(simID + "," +
					txID + "," + 
					size + "," +
					value + "," +
					simTime);
	}
	
	/**
	 * Adds an entry to the nodes log with information about the node.
	 * @param simID The simulation ID.
	 * @param nodeID Node ID
	 * @param hashPower Hashpower of the node (hashes/second)
	 * @param electricPower The power consumed by the node (Watts)
	 * @param electricityCost The electricity cost per kWh of the node
	 * @param totalCycles The total hashes the node performed
	 * @author Sotirios Liaskos
	 */
	public static void addNode(int simID, int nodeID, float hashPower, float electricPower, 
			float electricityCost, double totalCycles) {
		nodeLog.add(simID + "," +
					nodeID + "," + 
					hashPower + "," +
					electricPower + "," +
					electricityCost + "," +
					totalCycles);
	}
	
	
	
	/**
	 * Adds an entry to the network log with information about an event that establishes 
	 * or updates the bandwidth between two nodes. 
	 * For static networks only initial network creation is reported here. 
	 * For dynamic networks any change in the bandwidth between two nodes is reported.
	 * Zero bandwidth means no link.  
	 * @param simID The simulation ID.
	 * @param from The node from which the link is established
	 * @param to The node to which the link is established
	 * @param bandwidth The speed of the link
	 * @param simTime The time at which the event happened
	 */
	public static void addNetEvent(int simID, int from, int to, float bandwidth, long simTime) {
		netLog.add(simID + "," +
					from + "," + 
					to + "," +
					bandwidth + "," +
					simTime);
	}
	
	
	/**
	 * Adds an entry to the belief log. Each entry   
	 * @param simID The simulation ID.
	 * @param node The ID of the node whose belief is registered.
	 * @param tx The transaction the node's belief about its validity is registered. 
	 * @param believes Whether the node believes the transaction is valid and "final". Will report {@code false} if 
	 * the transaction is not in the structure or has not been seen before.   
	 * @param simTime The time at which the report is produced.
	 */
	public static void addBeliefEntry(int simID, int node, long tx, boolean believes, long simTime) {
		beliefLog.add(simID + "," +
					node + "," + 
					tx + "," +
					believes + "," +
					simTime);
	}
	
	
	
	
	
	
	
	/**
	 * Save reporter's event log to file. File name is "EventLog - [Simulation Date Time].csv"
	 * @author Sotirios Liaskos
	 */
	public static void flushEvtReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(path + "EventLog - " + runId + ".csv");
			for(String str: eventLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Save reporter's transaction log to file. File name is "Input - [Simulation Date Time].csv"
	 * @author Sotirios Liaskos
	 */
	public static void flushInputReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(path + "Input - " + runId + ".csv");
			for(String str: inputTxLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Save reporter's node log to file. File name is "Nodes - [Simulation Date Time].csv"
	 * @author Sotirios Liaskos
	 */

	public static void flushNodeReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(path + "Nodes - " + runId + ".csv");
			for(String str: nodeLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Save reporter's network log to file. File name is "NetLog - [Simulation Date Time].csv"
	 * @author Sotirios Liaskos
	 */
	public static void flushNetworkReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(path + "NetLog - " + runId + ".csv");
			for(String str: netLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Save reporter's belief log to file. File name is "BeliefLog - [Simulation Date Time].csv"
	 * @author Sotirios Liaskos
	 */
	public static void flushBeliefReport() {
		FileWriter writer;
		try {
			writer = new FileWriter(path + "BeliefLog - " + runId + ".csv");
			for(String str: beliefLog) {
				  writer.write(str + System.lineSeparator());
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	
	
	/**
	 * Export configuration. File name is "Config - [Simulation Date Time].csv"
	 * @author Sotirios Liaskos
	 */
	public static void flushConfig() {
		FileWriter writer;
		try {
			writer = new FileWriter(path + "Config - " + runId + ".csv");
			writer.write("Key, Value" + System.lineSeparator());
			writer.write(Config.printPropertiesToString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
