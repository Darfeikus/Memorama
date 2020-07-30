import java.util.List;

//Class used to encapsulate information about each block of processes
public class Block {
    private List<Process> listOfProcesses; // List of processes in each block
    private int swapOut; // Number of swap outs made
    private int swapIn; // Number of swap in made
    private Time startTime; // Time in which the block starts
    private Time endTime; // Time in which the block ends

    // Constructor
    public Block(List<Process> listOfProcesses, int swapOut, int swapIn, Time startTime, Time endTime) {
        this.listOfProcesses = listOfProcesses;
        this.swapOut = swapOut;
        this.swapIn = swapIn;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Method to get listOfProcesses attribute
    public List<Process> getListOfProcesses() {
        return this.listOfProcesses;
    }

    // Method to get swapOut attribute
    public int getSwapOut() {
        return this.swapOut;
    }

    // Method to get swapIn attribute
    public int getSwapIn() {
        return this.swapIn;
    }

    // Method to get startTime attribute
    public Time getStartTime() {
        return this.startTime;
    }

    // Method to get endTime attribute
    public Time getEndTime() {
        return this.endTime;
    }

}