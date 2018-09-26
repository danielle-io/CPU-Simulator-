import java.io.*;
import java.util.Scanner;
import java.text.DecimalFormat;

public class MFQ {

    // Declare instance variables
    private PrintWriter pw;
    private ObjectQueue inputQ = new ObjectQueue();
    private ObjectQueue[] network = new ObjectQueue[4];
    private int totalJobAmount = 0;
    private int averageStats;
    private int totalResponseTime;
    private int totalSystemTime;
    private double averageThroughput;
    private double averageResponseTime;
    private double averageTurnaround;
    private double averageWaitingTime;
    private int cpuIdleTime;
    CPU cpu = new CPU();

    public MFQ() {
        network[0] = new ObjectQueue();
        network[1] = new ObjectQueue();
        network[2] = new ObjectQueue();
        network[3] = new ObjectQueue();
    }

    /**
     * Overloaded constructor for an InfixToPostfix object
     *
     * @param pw PrintWriter pointing to a file that will be written to
     */
    public MFQ(PrintWriter pw) {

        // Assigns the print writer passed into the private instance variable
        this.pw = pw;
        network[0] = new ObjectQueue();
        network[1] = new ObjectQueue();
        network[2] = new ObjectQueue();
        network[3] = new ObjectQueue();
    }

    public void getJobs() throws IOException {

        // Open a file for input and create a Scanner object
        Scanner fileScan = new Scanner(new File("mfq.txt"));

        // Read a line from the file using the Scanner object
        while (fileScan.hasNext()) {

            // Create Job object with a line from file
            Job job = new Job(fileScan.nextLine());

            // Insert Job into the input queue
            inputQ.insert(job);
        }
        // Close the Scanner object
        fileScan.close();
    }

    public void outputHeader() {
        System.out.println("\t\t\t   Process Table");
        pw.println("\t\tProcess Table");
        System.out.println();
        pw.println();
        System.out.println("             \t Process    CPU    Total    Lowest");
        pw.println("            \t\tProcess    CPU      Total      Lowest");
        System.out.println("        System\tIdentifier  Time   Time in  Level");
        pw.println("\t System \tIdentifier  Time     Time in   Level");
        System.out.println("Event    Time \t  (PID)\t   Needed  System   Queue");
        pw.println("Event\t  Time  \t  (PID)\t Needed  System    Queue");
        System.out.println("__________________________________________________");
        pw.println("______________________________________________");
    }

    public void runSimulation() {

        // Declare variables
        int arrivalTime = 0;
        int pid = 0;
        int cpuReq = 0;
        boolean keepTicking = true;
        int time = 0;
        Job cpuJob;
        Job job = new Job();
        boolean jobLoad = true;
        cpuJob = new Job();
        int cpuPid;
        int totalWaitingTime = 0;

        // Make a Clock Object
        Clock clock = new Clock();

        // Run a while loop until inputQ is empty
        while (!inputQ.isEmpty() || !networkIsEmpty()) {

            // Set first job to the front of input Q. Job load starts off true and is made true after the previous
            // job has an arrival time = to time, and enters the network
            if (jobLoad) {
                if (!inputQ.isEmpty()) {
                    job = new Job((Job) inputQ.query());
                    jobLoad = false;
                }
            }

            // Get variables relating to our input queue job
            arrivalTime = job.getArrivalTime();
            pid = job.getPid();
            cpuReq = job.getCpuTimeRequired();

            // Tick the clock until arrival time == time, if there are no active jobs
            if (arrivalTime != time && networkIsEmpty()){

                while (keepTicking) {

                    // Increment the clock tick and store it in time
                    time = clock.incrementTick();

                    // Add to CPU idle time, since no job is on CPU
                    if (time >= 1){
                        cpuIdleTime++;
                    }

                    // Insert the object into the 1st queue when the arrival time matches time
                    if (arrivalTime == time) {

                        // Set networkEmpty to false
                        keepTicking = false;

                        // Set jobLoad = to true
                        jobLoad = true;
                    }
                }
            }

            // Set jobLoad to false if arrivalTime does not = time, because we only want to load
            // in one once one is exiting, which happens once arrivalTime = time
            if (arrivalTime != time && !networkIsEmpty()) {
                jobLoad = false;
            }

            // This loop will move a job FROM input queue TO network queue 1
            if (arrivalTime == time) {

                // Enter the job from the inputQ to q 0, set current queue to 0, set cpu time remaining
                job.setCurrentQueue(0);

                //job.setCpuTimeRemaining(job.getCpuTimeRequired());

                network[0].insert(job);

                // Every time a job enters the network we add one to total jobs for the summary output
                ++totalJobAmount;

                arrivalOutput(time, pid, cpuReq);

                // After a job's arrival time is output, it has entered the system


                // Remove the object from inputQ
                inputQ.remove();
                jobLoad = true;
            }

            // If CPU is not busy, set current queue, set CPU to job, set busy flag
            if (!cpu.isBusy() && !networkIsEmpty()) {

                // In Job class, cpuJob now holds job
                cpuJob = new Job((Job)network[0].query());

                // EVERY time a job is assigned to cpu job, the response time of that job is finalized
                totalResponseTime += (clock.getTick() + 1 - cpuJob.getArrivalTime());

                // Set CpuJob in CPU class to define which job we are working with and set busy flag
                cpu.setCpuJob(cpuJob);

                // Set the quantum clock
                cpu.initializeQuantumClock(job.getCurrentQueue() + 1);

                // Set the time remaining to the time required
                cpuJob.setCpuTimeRemaining(job.getCpuTimeRequired());

                // Set cpuPID and cpuArrivalTime
                cpuJob.setPid(pid);
                cpuJob.settArrivalTime(arrivalTime);
            }

            // This runs if the CPU is busy
            else if (cpu.isBusy() && !networkIsEmpty()){

                // Decrement the quantum clock and the cpu time remaining
                cpu.setQuantumClock(cpu.getQuantumClock() - 1);
                cpuJob.setCpuTimeRemaining(cpuJob.getCpuTimeRemaining() - 1);

                // Run if there is still time remaining
                if (cpuJob.getCpuTimeRemaining() != 0) {

                    // Run if we just added a new job to queue 1 and CPU is busy
                    // or the quantum clock ran out & move job down a queue
                    if (arrivalTime == time || cpu.getQuantumClock() == 0) {

                        // Make a new job called shift to temporarily hold the cpuJob we are moving down
                        Job shift = new Job((Job) network[cpuJob.getCurrentQueue()].query());

                        // Store the cpuJob current queue in int current. Get method returns 1 for queue #1, [0]
                        int current = cpuJob.getCurrentQueue();

                        // Set the shift current queue to int current. The set method adds 1 to current queue,
                        // so if current was 1 set adds 1 to that, making it 2
                        shift.setCurrentQueue(current);
                        shift.setCpuTimeRemaining(cpuJob.getCpuTimeRemaining());

                        // If queue is 4 we reinsert into 4, if not
                        if (current == 3) {
                            network[current].insert(shift);

                        }

                        // If queue is not 4 we add one to move it to a lower queue
                        else {
                            network[current + 1].insert(shift);
                            cpuJob.setCurrentQueue(current+1);
                        }

                        // Remove the cpuJob from wherever it was before
                        network[current].remove();

                        // Reset CPU and set busy flag to false
                        cpu.resetCpuJob();

                        // Call assignCpuJob method to assign a new CPU job when a preemption occurs
                        cpuJob = new Job(assignCpuJob(clock));
                    }
                }

                // Run if a job is complete (time remaining is 0)
                else {

                    // Get total time job was in system
                    int jobSystemTime = time - cpuJob.getArrivalTime();

                    // Add the int jobSystemTime to totalSystemTime to get the total system time for all jobs
                    totalSystemTime += jobSystemTime;

                    // jobWaiting time is the jobSystemTime - the time required
                    int jobWaitingTime = jobSystemTime - cpuJob.getCpuTimeRequired();

                    // Add the jobWaitingTime to the total to combine all waiting time
                    totalWaitingTime += jobWaitingTime;

                    // get cpuPID and cpuArrivalTime
                    cpuPid = cpuJob.getPid();

                    // Output message
                    departureOutput(time, jobSystemTime, cpuPid, cpuReq, cpuJob);

                    // Remove job from network
                    network[cpuJob.getCurrentQueue()].remove();

                    // Reset CPU
                    cpu.resetCpuJob();

                    // If network isn't empty, call assignCpuJob method to assign a new CPU job when a job is completed
                    if (!networkIsEmpty()){
                        cpuJob = new Job(assignCpuJob(clock));
                    }
                }
            }

            // Increment clock
            time = clock.incrementTick();
        }

        // End of simulation results-  we can now divide the total system time by job amount
        averageThroughput =  totalJobAmount / (double) totalSystemTime;

        // average turnaround is the totalSystemTime(total of arrival-completion) / total jobs
        averageTurnaround = totalSystemTime / (double)totalJobAmount;

        // We can now get the average response time by dividing totalResponseTime by totalJobAmount
        averageResponseTime = totalResponseTime / (double)totalJobAmount;

        // We can now get the average waiting time by totalWaitingTime / all the jobs
        averageWaitingTime = totalWaitingTime / (double)totalJobAmount;
    }

    public void outStats() {

        // Set decimal format to two places
        DecimalFormat df = new DecimalFormat("0.00");
        String averageTurnaroundStr = df.format(averageTurnaround);
        String averageResponseTimeStr = df.format(averageResponseTime);
        String averageThroughputStr = df.format(averageThroughput);
        String averageWaitingTimeStr = df.format(averageWaitingTime);

        System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
        pw.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
        System.out.println();
        pw.println();
        System.out.println("\t\t\t\tSummary");
        pw.println("\t\tSummary");
        System.out.println();
        pw.println();
        System.out.println("Total Number of Jobs: " + totalJobAmount);
        pw.println("Total Number of Jobs: " + totalJobAmount);
        System.out.println("Total Time of All Jobs in System: " + totalSystemTime);
        pw.println("Total Time of All Jobs in System: " + totalSystemTime);
        System.out.println("Average Response Time: " + averageResponseTimeStr);
        pw.println("Average Response Time: " + averageResponseTimeStr);
        System.out.println("Average Turnaround Time for the Jobs: " + averageTurnaroundStr);
        pw.println("Average Turnaround Time for the Jobs: " + averageTurnaroundStr);
        System.out.println("Average Waiting Time: " + averageWaitingTimeStr);
        pw.println("Average Waiting Time: " + averageWaitingTimeStr);
        System.out.println("Average Throughput: " + averageThroughputStr);
        pw.println("Average Throughput: " + averageThroughputStr);
        System.out.println("Total CPU Ide Time: " + cpuIdleTime);
        pw.println("Total CPU Ide Time: " + cpuIdleTime);
    }

    /**
     * Outputs the arrival message of each job entering system
     * @param time
     * @param pid
     * @param cpuReq
     */
    private void arrivalOutput(int time, int pid, int cpuReq) {
        System.out.println("Arrival\t\t" + time + "\t   " + pid + "      " + cpuReq);
        pw.println("Arrival\t     " + time + "\t  " + pid + "         " + cpuReq);
    }

    /**
     * Outputs the departure message of each job entering system
     * @param time
     * @param jobSystemTime
     * @param cpuPID
     * @param cpuReq
     * @param cpuJob
     */
    private void departureOutput(int time, int jobSystemTime, int cpuPID, int cpuReq, Job cpuJob) {

        // Standardize spaces
        String spaces = "";
        if (jobSystemTime < 100){
            spaces = "\t\t  ";
        }
        else{
            spaces = "\t  ";
        }
        System.out.println("Departure\t" + time + "\t   " + cpuPID + "\t\t         "
                + jobSystemTime + spaces + (cpuJob.getCurrentQueue() + 1));
        pw.println("Departure     " + time + "\t  " + cpuPID + "\t             "
                + jobSystemTime + "\t " + (cpuJob.getCurrentQueue() + 1));
    }

    /**
     * Checks if network is empty
     * @return boolean true if network is empty and false if network is not empty
     */
    private boolean networkIsEmpty() {
        for (int i = 0; i < network.length; i++) {
            if (!network[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Assigns a job to the cpu when needed, by finding a job at the front of the lowest level queue
     * @param clock
     * @return Job object cpuJob
     */
    private Job assignCpuJob(Clock clock) {
        Job cpuJob = new Job();

        // After a job is complete or preempted, CPU is not busy and this will run
        if (!cpu.isBusy()) {
            // Search for the next lowest level queue job and set CPU to it
            for (int i = 0; i < network.length; i++) {
                if (!network[i].isEmpty()) {

                    // We set cpuJob to the first valid job off the network
                    cpuJob = new Job((Job) network[i].query());
                    cpuJob.setCurrentQueue(i);

                    // EVERY time a job in q 1 is assigned to cpu job, the response time of that job is finalized
                    if (i == 0) {
                        totalResponseTime += ((clock.getTick() + 1) - cpuJob.getArrivalTime());
                    }
                    break;
                }
            }

            // Set the job we found to cpuJob in the CPU class, and get CPU time remaining
            cpu.setCpuJob(cpuJob);

            // Initialize QuantumClock with the current queue (which was [i + 1])
            cpu.initializeQuantumClock(cpuJob.getCurrentQueue() + 1);
        }
        return cpuJob;
    }
}
