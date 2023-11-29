package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Profiling;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.StandardSampler;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.network.RandomEndToEndNetwork;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.transaction.TransactionWorkload;


public class BitcoinMainDriver {

	public static void main(String[] args) {
        BitcoinMainDriver b = new BitcoinMainDriver();
        b.run();
	}

	private void run() {
        AbstractSampler sampler;
    	AbstractNetwork n;
        Simulation s;
        NodeSet ns;
        TransactionWorkload ts;
        AbstractNodeFactory nf;
        
        Config.init("/home/amir/Projects/CNSim/cnsim/CNSim/resources/config.txt");

        //Creating sampler
        sampler = new StandardSampler();
        sampler.LoadConfig();
        //TODO add reading from file option

        
        //Create first the simulator
        s = new Simulation(sampler);
        
        //
        // Network Construction
        //
        
        //Create the node factory
        nf = new BitcoinNodeFactory("Honest",s);
        //Create and populate a NodeSet.
        ns = new NodeSet(nf);
        //ns.addNodes(Parameters.NumofNodes); //a network where all nodes are honest
        ns.addNodes(Config.getPropertyInt("net.numOfNodes"));
        //Create a network based on the NodeSet and the sampler
        n = new RandomEndToEndNetwork(ns,sampler);
        //Set this network to the simulator
        s.setNetwork(n);
        
        //
        // Workload Construction
        //

            ts = new TransactionWorkload(sampler);
            //ts.appendTransactions(Parameters.numTransactions);
            ts.appendTransactions(Config.getPropertyLong("workload.numTransactions"));
            s.schedule(ts);

            Profiling.simBeginningTime = System.currentTimeMillis();


            s.run();
            long realTime = (System.currentTimeMillis() - Profiling.simBeginningTime); // in Milli-Sec
            System.out.printf("\n");
        System.out.println("Real time(ms): " + realTime);
        System.out.println("Simulation time(ms): " + Simulation.currTime);
        
        s.getNodeSet().closeNodes();
        
        BitcoinReporter.flushBlockReport();
        BitcoinReporter.flushStructReport();
        BitcoinReporter.flushEvtReport();
        BitcoinReporter.flushNodeReport();
        BitcoinReporter.flushInputReport();
        BitcoinReporter.flushConfig();
	}
	
	
}
