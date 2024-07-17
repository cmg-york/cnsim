    package cmg.cnsim.bitcoin;

    import cmg.cnsim.engine.*;
    import cmg.cnsim.engine.commandline.CommandLineParser;
    import cmg.cnsim.engine.network.AbstractNetwork;
    import cmg.cnsim.engine.network.FileBasedEndToEndNetwork;
    import cmg.cnsim.engine.network.RandomEndToEndNetwork;
    import cmg.cnsim.engine.node.AbstractNodeFactory;
    import cmg.cnsim.engine.node.INode;
    import cmg.cnsim.engine.node.NodeSet;
    import cmg.cnsim.engine.transaction.Transaction;
    import cmg.cnsim.engine.transaction.TransactionWorkload;

    import java.io.IOException;
    import java.util.List;
    import java.util.Scanner;


    public class BitcoinMainDriver {

        public static void main(String[] args) {
            //run simulation with the given configuration for n times
            BitcoinMainDriver b = new BitcoinMainDriver();
            b.run(args);
        }

        
         private void run(String[] args) {
            //print current directory
            System.out.println("Current directory: " + System.getProperty("user.dir"));
//            Config.init("./resources/config.txt");

             // Parse command line arguments
             CommandLineParser commandLineParser = CommandLineParser.parse(args);
             if (commandLineParser == null) {
                 return; // Exit if help was requested or parsing failed
             }

             // Build SimulationConfig
             SimulationConfig config;
             try {
                 config = SimulationConfigFactory.create(commandLineParser);
             } catch (IOException | IllegalArgumentException e) {
                 System.err.println("Error building configuration: " + e.getMessage());
                 return;
             }


             //
            //
            // Sampler Creation
            //
            //
            Sampler sampler = new Sampler();

            //Define node sampler. 
            //If a file exists it will be file-based, otherwise, just create a standard sampler.
            
            long nodeSeed = -1;
            boolean hasNodeSeed = false;
            if (config.hasProperty("node.sampler.seed")) {
            	nodeSeed = config.getPropertyLong("node.sampler.seed");
            	hasNodeSeed = true;
            	Debug.p("Seed found for node sampler: " + nodeSeed);
            }
            
            String nodeSamplerPath = config.getNodeFile();
            if (nodeSamplerPath!=null) {
            	Debug.p("Creating a file-based node sampler");
            	if (hasNodeSeed) {
            		sampler.setNodeSampler(new FileBasedNodeSampler(nodeSamplerPath, new StandardNodeSampler(sampler,nodeSeed)));
            	} else {
            		sampler.setNodeSampler(new FileBasedNodeSampler(nodeSamplerPath, new StandardNodeSampler(sampler)));
            	}
            } else {
            	Debug.p("Creating random node sampler");
            	if (hasNodeSeed) {
            		sampler.setNodeSampler(new StandardNodeSampler(sampler,nodeSeed));
            	} else {
            		sampler.setNodeSampler(new StandardNodeSampler(sampler));
            	}
            }
            
            
            //Define network sampler. 
            //Will be used only if a random network is required
            Debug.p("Creating random network sampler");
            sampler.setNetworkSampler(new StandardNetworkSampler(sampler));
            
            
            if (Config.hasProperty("net.sampler.seed")) {
            	Debug.p("Adding seed to network sampler: " + config.getPropertyLong("net.sampler.seed"));
            	sampler.getNetworkSampler().setSeed(config.getPropertyLong("net.sampler.seed"));
            }
            
            
            //Define workload sampler. 
            
            long workloadSeed = -1;
            boolean hasWorkloadSeed = false;
            if (config.hasProperty("workload.sampler.seed")) {
            	workloadSeed = config.getPropertyLong("workload.sampler.seed");
            	hasWorkloadSeed = true;
            	Debug.p("Seed found for workload sampler: " + workloadSeed);
            }
            
            //If a file exists it will be file-based, otherwise, just create a standard sampler.
            String workloadSamplerPath = config.getWorkloadFile();
            if (workloadSamplerPath!=null) {
            	Debug.p("Creating file-based workload sampler");
            	if (hasWorkloadSeed) {
            		sampler.setTransactionSampler(new FileBasedTransactionSampler(workloadSamplerPath, new StandardTransactionSampler(sampler,workloadSeed)));	
            	} else {
        			sampler.setTransactionSampler(new FileBasedTransactionSampler(workloadSamplerPath, new StandardTransactionSampler(sampler)));            		
            	}
            } else {
                Debug.p("Creating random workload sampler");
            	if (hasWorkloadSeed) {
            		sampler.setTransactionSampler(new StandardTransactionSampler(sampler, workloadSeed));
            	} else {
            		sampler.setTransactionSampler(new StandardTransactionSampler(sampler));
            	}
            }
            
            
            
            //
            //
            // Creating the simulation object
            //
            //
            
            Simulation s = new Simulation(sampler);
            
            
            //
            //
            // Creating the nodes
            //
            //
            
            AbstractNodeFactory nf = new BitcoinNodeFactory("Honest", s);
            NodeSet ns = new NodeSet(nf);
            Debug.p("Nodeset created");
            
            // Adding nodes
            ns.addNodes(config.getPropertyInt("net.numOfNodes"));
            Debug.p("Nodes added");

            //
            //
            // Creating the network
            //
            //
            
            //Define network. 
            //If a file exists it will be file-based, otherwise, just create a standard network.
            AbstractNetwork net = null;
            String netFilePath = config.getNetworkFile();
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
				ts.appendTransactions(config.getPropertyLong("workload.numTransactions"));
			} catch (Exception e) {
				e.printStackTrace();
			}
            s.schedule(ts);

            // Assign a target transaction for malicious behavior
            Transaction targetTransaction = null;
            if (config.getPropertyBoolean("node.createMaliciousNode")) {
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