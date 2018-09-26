import java.util.*;
public class CPU {

    // Declare variables
    private Job job;
    private int cpuQuantumClock;
    private boolean busyFlag = false;

    public CPU () {
        this.job = new Job();
        this.cpuQuantumClock = 0;
    }

    /**
     *
     *
     * @return boolean determining whether or not CPU is busy
     */
    public boolean isBusy(){
      return busyFlag;
    }

    /**
     *
     *
     * @param job
     */
    public void setCpuJob(Job job){
        this.job = new Job(job);
        busyFlag = true;
    }

    public void resetCpuJob(){
        this.job = new Job();
        busyFlag = false;
    }

    public Job getCpuJob(){
        return this.job;
    }

    /**
     *
     *
     * @param current
     */
    public void initializeQuantumClock(int current){
        cpuQuantumClock = (int)Math.pow(2, current);
    }

    public void setQuantumClock(int current){
        this.cpuQuantumClock = current;
    }

    /**
     *
     * @return int representing the quantum clock value
     */
    public int getQuantumClock(){
        return this.cpuQuantumClock;
    }
}
