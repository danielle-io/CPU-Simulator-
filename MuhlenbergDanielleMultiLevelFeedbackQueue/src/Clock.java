
public class Clock {

    // Declare Variables
    private int tick = 0;

    public Clock(){ }

    public int incrementTick(){
        return ++tick;
    }

    public int getTick(){
        return tick;
    }
}
