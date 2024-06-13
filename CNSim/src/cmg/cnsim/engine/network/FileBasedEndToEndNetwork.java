package cmg.cnsim.engine.network;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.node.NodeSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileBasedEndToEndNetwork extends AbstractNetwork {

    private AbstractSampler sampler;
    public FileBasedEndToEndNetwork(NodeSet ns, AbstractSampler s, String filename) {
        super(ns);
        this.sampler =  s;
        LoadFromFile(filename);
    }

    public FileBasedEndToEndNetwork(){
    }


    public void LoadFromFile(String filepath) {
        //read the file from file path
        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 3) {
                    continue; // Skip lines that don't have exactly 3 values
                }
                try {
                    int from = Integer.parseInt(values[0].trim());
                    int to = Integer.parseInt(values[1].trim());
                    float throughput = Float.parseFloat(values[2].trim());
                    if (from < 0 || from > Net.length || to < 0 || to > Net.length) {
                        System.err.println("Invalid node ID in throughput line: " + line);
                        continue;
                    }
                    //check the number of nodes and put data based on that
                    if (from > ns.getNodeSetCount() || to > ns.getNodeSetCount()) {
                        System.err.println("Invalid node ID in throughput line: " + line);
                        continue;
                    }
                    this.setThroughput(from, to, throughput);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing throughput line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}