package cmg.cnsim.engine.node;

import java.util.ArrayList;

import cmg.cnsim.engine.Debug;
import cmg.cnsim.engine.Reporter;
import cmg.cnsim.engine.Simulation;

/**
 * Represents a set of nodes participating in a network
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class NodeSet {

	protected ArrayList<INode> nodes;
	protected AbstractNodeFactory nodeFactory;
	private INode maliciousNode = null;
	private float totalHonestHP;


	/**
	 * Constructs a new NodeSet using the provided AbstractNodeFactory. 
	 * @param nf The AbstractNodeFactory used to create new nodes.
	 */
	public NodeSet(AbstractNodeFactory nf) {
        nodes = new ArrayList<>();
        nodeFactory = nf;
	}
	
	
	// P r i n t i n g
	/**
	 * Manipulating the set
	 * @deprecated 
	 */
	public void _______________Set_Manipulation() {}
	
	
	/**
	 * Adds a new node to the NodeSet using the configured node factory.
	 * @throws Exception if an error occurs while creating a new node.
	 * @author Sotirios Liaskos
	 */
	public void addNode() throws Exception {
        INode o = nodeFactory.createNewNode();
        nodes.add(o);
        this.totalHonestHP += o.getHashPower();
	}
	
	/**
	 * Add a number num of nodes in the NoteSet, using the sampler specified in the Simulation object supplied to the NodeSet object
	 * @param num Is the number of nodes to add.
	 */
	public void addNodes(int num) {
	    if(num < 0)
	        throw new ArithmeticException("num < 0");
	    for (int i = 1; i<=num; i++){
	        try {
				addNode();
			} catch (Exception e) {e.printStackTrace();}
	    }
	}

	
	/**
	 * Get the actual ArrayList object. TODO: refactor so that this is not needed.
	 * @return The Arraylist object with the nodes
	 */
	public ArrayList<INode> getNodes() {
	    return nodes;
	}
	
	/**
	 * Returns how many nodes are contained in the NodeSet
	 * @return The number of nodes in the NodeSet
	 */
	public int getNodeSetCount() {
	    return (nodes.size());
	}
	

	/**
	 * Returns the total honest hash power of the nodes in the NodeSet.
	 * @return The total honest hash power.
	 * @author Sotirios Liaskos
	 */
	public float getTotalHonestHP() {
		return totalHonestHP;
	}

	

	/**
	 * Returns a random node from the NodeSet
	 * @return a Node object
	 */
	public INode pickRandomNode() {
	    return (nodes.get(nodeFactory.getSampler().getNodeSampler().getNextRandomNode(nodes.size())));
	}

	/**
	 * Returns a specific node from the NodeSet
	 * @param nodeID The of the node to be picked.
	 * @return The node object.
	 */
	public INode pickSpecificNode(int nodeID) {
	    return (nodes.get(nodeID));
	}
	
	/**
	 * Perform any closing reporting to all nodes.
	 * @author Sotirios Liaskos
	 */
	public void closeNodes() {
		for (INode n:this.getNodes()) {
			n.close(n);
			Reporter.addNode(Simulation.currentSimulationID, n.getID(), n.getHashPower(), n.getElectricPower(), n.getElectricityCost(), n.getTotalCycles());
		}
	}
	
	

	// P r i n t i n g
	/**
	 * @deprecated 
	 */
	public void _______________Printing() {}
	
	
	
	/** Returns a string displaying the NodeSet.
	 * @return A string displaying the node IDs, the power of each and whether it is malicious
	 */
	public String debugPrintNodeSet() {
	    String s = "";
	    for(int i = 0; i< nodes.size();i++){
	        s = s + "Node ID:" + nodes.get(i).getID() + 
	                "\t Hashpower: " + nodes.get(i).getHashPower() + " (H/sec)" +
	                "\t Malicious?: " + (nodes.get(i) == maliciousNode) +
	                "\n";
	    }
	    return (s);
	}


	
	/**
	 * Generates an array of strings representing the NodeSet.
	 * Each element of the array represents a node in the NodeSet and includes the node's ID, electric power,
	 * hashpower, electricity cost, cost per GH, average connectedness, and total cycles.
	 * @return An array of strings representing the NodeSet.
	 * @author Sotirios Liaskos
	 */
	public String[] printNodeSet() {
	    String s[] = new String[nodes.size()];
	    for(int i = 0; i< nodes.size();i++){
	    	// 6 items
	        s[i] = nodes.get(i).getID() + "," + 
	        		+ nodes.get(i).getElectricPower() + ","
	        		+ nodes.get(i).getHashPower() + ","
	        		+ nodes.get(i).getElectricityCost() + "," 
	        		+ nodes.get(i).getCostPerGH() + ","
	        		+ nodes.get(i).getAverageConnectedness() + ","
	        		+ nodes.get(i).getTotalCycles();
	    }
	    return (s);
	}

}