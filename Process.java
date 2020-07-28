import java.util.*;

public class Process {
    int id; // unique id for process
    List<int[]> pageList = new ArrayList<>(); // list of int array of size 2, [0] 0 is RAM, 1 is VRAM, [1] is address.
    Time startTime;
    Time endTime;
    Time turnaround;

    public Process(int id, Time startTime, List<int[]> pageList) {
        // this.id = Simulation.createProcess()
        // this.pageList = Viene de RAM.addProcess que llama simulacion
        // this.startTime = viene de Simulacion

    }

    // Genera Page Fault
    void changeProcess(int oldAddress, int newAddress) {
        // if 0 -> 1; else 1 -> 0
    }

    void endProcess(Time endTime) {
        this.endTime = endTime;
        // turnaround = Time.substractTimes(endTime, startTime);
    }

}