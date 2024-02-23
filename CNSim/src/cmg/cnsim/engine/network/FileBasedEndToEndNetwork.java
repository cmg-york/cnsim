package cmg.cnsim.engine.network;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.node.NodeSet;

public class FileBasedEndToEndNetwork extends AbstractNetwork {
    private AbstractSampler sampler;
    public FileBasedEndToEndNetwork(NodeSet ns, String filename) {
        super(ns);
        LoadFromFile(filename);
    }


    private void CreateRandomNetwork() {
        for (int i=1; i <= Config.getPropertyInt("net.numOfNodes"); i++) {
            for (int j=1; j <= Config.getPropertyInt("net.numOfNodes"); j++) {
                if(i!=j && Net[i][j] == 0)
                {
                    Net[i][j] = (float) sampler.getNextConnectionThroughput(); //network throughput refers to how much data can be transferred from source to destination within a given time frame
                    Net[j][i] = Net[i][j];
                }
            }
        }
    }

    public void LoadFromFile(String filename) {
        //The file is in csv and have three columns: origin, destination, throughput
        for (int i=1; i <= Config.getPropertyInt("net.numOfNodes"); i++) {
            for (int j = 1; j <= Config.getPropertyInt("net.numOfNodes"); j++) {
                if(i!=j && Net[i][j] == 0)
                {
                    Net[i][j] = (float) sampler.getNextConnectionThroughput(); //network throughput refers to how much data can be transferred from source to destination within a given time frame
                    Net[j][i] = Net[i][j];
                }
            }
        }
    }
}