package ca.yorku.cmg.cnsim.engine;

public class ProgressBar {
    public static void printProgress(int progress, int total, int offset) {
        int barLength = 50;  // Length of the progress bar
        int progressLength = (int) ((double) progress / total * barLength);
        StringBuilder progressBar = new StringBuilder("[");

        // Add the beginning offset
        for (int i = 0; i < offset; i++) {
            progressBar.insert(0," ");
        }
        
        // Add progress part of the bar
        for (int i = 0; i < progressLength; i++) {
            progressBar.append("#");
        }

        // Add remaining part of the bar
        for (int i = progressLength; i < barLength; i++) {
            progressBar.append(" ");
        }

        progressBar.append("]");

        // Print the progress bar along with the percentage
        System.out.print("\r" + progressBar.toString() + " " + (int) ((double) progress / total * 100) + "%");
    }
}
