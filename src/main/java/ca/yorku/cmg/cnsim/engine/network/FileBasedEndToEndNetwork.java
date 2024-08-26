package ca.yorku.cmg.cnsim.engine.network;


import ca.yorku.cmg.cnsim.ResourceLoader;
import ca.yorku.cmg.cnsim.engine.node.NodeSet;

import java.io.*;

public class FileBasedEndToEndNetwork extends AbstractNetwork {

	private String networkFilePath;

    public FileBasedEndToEndNetwork(NodeSet ns, String filename) throws Exception {
        super(ns);
        networkFilePath = filename;
        try {
			LoadFromFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public FileBasedEndToEndNetwork(){
    }

    public void LoadFromFile() throws Exception {
    	LoadFromFile(true);
    }

    public void LoadFromFile(boolean hasHeaders) throws Exception {
    	int lineCount = 0;
        //read the file from file path
        try(InputStream stream = ResourceLoader.getResourceAsStream(networkFilePath)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = br.readLine()) != null) {
            	lineCount++;
                String[] values = line.split(",");
                if (values.length != 4) {
                    continue; // Skip lines that don't have exactly 3 values
                }
				if (hasHeaders && lineCount == 1) {
					continue; // Skip first line
				}
                try {
                    int from = Integer.parseInt(values[0].trim());
                    int to = Integer.parseInt(values[1].trim());
                    float throughput = Float.parseFloat(values[2].trim());
                    //Debug.p(line);
                    if (from < 0 || from > Net.length || to < 0 || to > Net.length) {
                        throw new Exception("Invalid node ID in throughput (out of max ranger) line: " + line);
                    }
                    //check the number of nodes and put data based on that
                    if (from > ns.getNodeSetCount() || to > ns.getNodeSetCount()) {
                        throw new Exception("Invalid node ID in throughput line (ID not in nodeset): " + line);
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