package cmg.cnsim.tangle;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Profiling;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.StandardSampler;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.network.RandomEndToEndNetwork;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.transaction.TransactionWorkload;


public class TangleMainDriver {
	
    public static void main(String[] args) {
        TangleMainDriver b = new TangleMainDriver();
        b.run();
    }

    private void run() {
        AbstractSampler sampler;
    	AbstractNetwork n;
        Simulation s;
        NodeSet ns;
        TransactionWorkload ts;
        TangleNodeFactory nf;

        //Load configuration
        Config.init("./resources/config.txt");

                
        //Creating sampler
        sampler = new StandardSampler();
        sampler.LoadConfig();
        
        //Create first the simulator
        s = new Simulation(sampler);
        
        
        //
        // Network Construction
        //
        
        //Create the a node factory
        nf = new TangleNodeFactory("Honest",s);
        //Create and populate a NodeSet.
        ns = new NodeSet(nf);
        //ns.addNodes(Parameters.NumofNodes); //a network where all nodes are honest
        ns.addNodes(Config.getPropertyInt("net.numOfNodes"));
        //Create a network based on the NodeSet
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
        
        
        TangleReporter.setUp();
        TangleReporter.setTrackedTransactions(ts.pickRandomTransactions(5,0.25f));
        
        Profiling.simBeginningTime = System.currentTimeMillis();
        s.run();
        long realTime = (System.currentTimeMillis() - Profiling.simBeginningTime); // in Milli-Sec
        System.out.printf("\n");
        System.out.println("Real time(ms): " + realTime);
        System.out.println("Simulation time(ms): " + Simulation.currTime);
        System.out.println("File ID is: " + TangleReporter.getRunId());
        
        TangleReporter.flushEvtReport();
        TangleReporter.flushStructReport();
        TangleReporter.flushTangleState(ns);
        TangleReporter.flushNodesList(ns);
        TangleReporter.closeReporting();
    }
}

