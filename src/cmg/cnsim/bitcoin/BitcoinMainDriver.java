    package cmg.cnsim.bitcoin;

    import cmg.cnsim.engine.*;
import cmg.cnsim.engine.event.Event_SeedUpdate;
import cmg.cnsim.engine.network.AbstractNetwork;
    import cmg.cnsim.engine.network.FileBasedEndToEndNetwork;
    import cmg.cnsim.engine.network.RandomEndToEndNetwork;
    import cmg.cnsim.engine.node.AbstractNodeFactory;
    import cmg.cnsim.engine.node.INode;
    import cmg.cnsim.engine.node.NodeSet;
    import cmg.cnsim.engine.transaction.Transaction;
    import cmg.cnsim.engine.transaction.TransactionWorkload;

import java.util.Arrays;
import java.util.List;
    import java.util.Scanner;


    public class BitcoinMainDriver {

        public static void main(String[] args) {
            //run simulation with the given configuration for n times
            BitcoinMainDriver b = new BitcoinMainDriver();
            b.run();
        }

        
         private void run() {
            //print current directory
            System.out.println("Current directory: " + System.getProperty("user.dir"));
            Config.init("./resources/config.txt");

            
            //
            //
            // Creating simulation object
            //
            //
            
            Simulation s = new Simulation();
            
            //
            //
            // Creating Sampler
            //
            //
            Sampler sampler = new Sampler();
            
            //
            //
            // Set Sampler
            //
            //            
            s.setSampler(sampler);

            
            //Develop sampler 1: Node Sampler 
            //
            try {
				sampler.setNodeSampler(new NodeSamplerFactory().getSampler(
						Config.getPropertyString("node.sampler.file"),
						Config.getPropertyString("node.sampler.seed"),
						Config.getPropertyString("node.sampler.seedUpdateTimes"),
						sampler,
						s
						));
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            //Develop sampler 2: Network Sampler 
            //            
            sampler.setNetworkSampler(new NetworkSamplerFactory().getNetworkSampler(sampler,Config.getPropertyLong("net.sampler.seed")));
            

            //Develop sampler 3: Transaction Sampler 
            //
            try {
				sampler.setTransactionSampler(
						new TransactionSamplerFactory().getSampler(
								Config.getPropertyString("workload.sampler.file"), 
								(Config.hasProperty("workload.sampler.seed") ? Config.getPropertyLong("workload.sampler.seed") : null), 
								sampler));
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            
            //
            //
            // Creating the nodes
            //
            //
            
            AbstractNodeFactory nf = new BitcoinNodeFactory("Honest", s);
            NodeSet ns = new NodeSet(nf);
            Debug.p("Nodeset created");
            
            // Adding nodes
            ns.addNodes(Config.getPropertyInt("net.numOfNodes"));
            Debug.p("Nodes added");

            //
            //
            // Creating the network
            //
            //
            
            //Define network. 
            //If a file exists it will be file-based, otherwise, just create a standard network.
            AbstractNetwork net = null;
            String netFilePath = Config.getPropertyString("net.sampler.file");
			if (netFilePath != null) {
				try {
					Debug.p("Creating file-based network.");
					net = new FileBasedEndToEndNetwork(ns, netFilePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					net = new RandomEndToEndNetwork(ns, sampler);
					Debug.p("Creating random network.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
            


			
            s.setNetwork(net);
            //n.printNetwork();



            //
            //
            // Creating the transaction workload
            //
            //
            
            // Transaction workload
            TransactionWorkload ts = new TransactionWorkload(sampler);
            try {
				ts.appendTransactions(Config.getPropertyLong("workload.numTransactions"));
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            
            //
            // Scheduling
            //
            //
            
            //Schedule the workload
            s.schedule(ts);


            
            
            
            // Assign a target transaction for malicious behavior
            Transaction targetTransaction = null;
            if (Config.getPropertyBoolean("node.createMaliciousNode")) {
                targetTransaction = getTargetTransactionFromUser(ts.getAllTransactions());
            }
            for (INode node : ns.getNodes()) {
                if (node instanceof BitcoinNode) {
                    BitcoinNode bNode = (BitcoinNode) node;
                    if (bNode.getBehaviorStrategy() instanceof MaliciousNodeBehavior) {
                        ((MaliciousNodeBehavior) bNode.getBehaviorStrategy()).setTargetTransaction(targetTransaction);
                    }
                }
            }

            
            //
            //
            // Running the simulator
            //
            //

            
            Profiling.simBeginningTime = System.currentTimeMillis();
            s.run();
            long realTime = (System.currentTimeMillis() - Profiling.simBeginningTime); // in Milli-Sec
            System.out.print("\n");
            System.out.println("Real time(ms): " + realTime);
            System.out.println("Simulation time(ms): " + Simulation.currTime);

            

            //
            //
            // Clean-up and generate reports
            //
            //
            
            
            s.getNodeSet().closeNodes();

            BitcoinReporter.flushBlockReport();
            BitcoinReporter.flushStructReport();
            BitcoinReporter.flushEvtReport();
            BitcoinReporter.flushNodeReport();
            BitcoinReporter.flushInputReport();
            BitcoinReporter.flushNetworkReport();
            BitcoinReporter.flushConfig();
            // each node should log its own blockchain in the end
        }


        private Transaction getTargetTransactionFromUser(List<Transaction> transactions) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select a transaction ID to target for the attack:");
            String chosenId = scanner.nextLine();
            //TODO Add the target transaction to the config file instead of taking it from user in the begining.
            for (Transaction t : transactions) {
                if (t.getID() == Integer.parseInt(chosenId)) {
                    return t;
                }
            }
            return null; // Or handle invalid selection appropriately
        }


    }