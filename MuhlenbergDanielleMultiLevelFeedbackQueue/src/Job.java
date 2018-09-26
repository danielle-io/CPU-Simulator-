
public class Job {

    // Declare variables
    private int pid;
    private int arrivalTime;
    private int cpuTimeRequired;
    private int cpuTimeRemaining;
    private int currentQueue;

    public Job(String line){
        String delims = "[ ]+";
        String[] tokens = line.split(delims);
        int k = tokens[0].equals("") ? 1 : 0;
        this.arrivalTime = Integer.parseInt(tokens[k]);
        this.pid = Integer.parseInt(tokens[k + 1]);
        this.cpuTimeRequired = Integer.parseInt(tokens[k + 2]);
        this.cpuTimeRemaining = 0;
        this.cpuTimeRemaining = this.cpuTimeRequired;
        this.currentQueue = 1;
    }

    public Job() {
        this.arrivalTime = 0;
        this.pid = 0;
        this.cpuTimeRequired = 0;
        this.cpuTimeRemaining = 0;
        this.currentQueue = 0;
    }

    public Job(Job j){
        this.arrivalTime = j.arrivalTime;
        this.pid = j.pid;
        this.cpuTimeRequired = j.cpuTimeRequired;
        this.cpuTimeRemaining = j.cpuTimeRemaining;
        this.currentQueue = j.currentQueue;
    }

    /**
     *
     *
     * @return
     */
    public int getArrivalTime(){
        return this.arrivalTime;
    }

    public void settArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    /**
     *
     * @param pid
     * @return
     */
    public void setPid(int pid){
        this.pid = pid;
    }

    /**
     *
     *
     * @return
     */
    public int getPid(){
        return this.pid;
    }

    /**
     *
     *
     * @return
     */
    public int getCpuTimeRequired(){
        return this.cpuTimeRequired;
    }

    /**
     *
     *
     * @return
     */
    public int getCpuTimeRemaining(){
        return this.cpuTimeRemaining;
    }

    public void setCpuTimeRemaining(int time){
        this.cpuTimeRemaining = time;

    }

    public void setCurrentQueue(int a){
        this.currentQueue = a;
    }

    /**
     *
     *
     * @return
     */
    public int getCurrentQueue(){
        return this.currentQueue;
    }
}
