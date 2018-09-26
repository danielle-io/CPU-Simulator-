import java.io.*;

/**
 * Project Description: This program simulates an operating system's job scheduling policy to determine which
 * process will be assigned the CPU when it becomes available.
 * How to Start the Project: Run "Driver" to start the program

 * User Instructions: Run "Driver"
 */

/**
 * Driver Class
 * @author Richard Stegman
 * @version 2.1 - October 18, 2017
 */
public class Driver {

    public static void main(String[] args) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter("csis.txt"));
        MFQ mfq = new MFQ(pw);
        mfq.getJobs();
        mfq.outputHeader();
        mfq.runSimulation();
        mfq.outStats();
        pw.close();
    }
}