/* Author: Gerardo Granados Aldaz */
import java.util.*;

public class Process {
    int id; // unique id for process
    List<int[]> pageList = new ArrayList<>(); // list of int array of size 2, [0] 0 is RAM, 1 is VRAM, [1] is address.
    Time startTime;
    Time endTime;
    Time turnaround;

    public Process(int id, Time startTime, List<int[]> pageList) {
        this.id = id;
        this.startTime = startTime;
        this.pageList = pageList;
    }

    // Called by RAM, moves processes from RAM to VRAM, and vice versa
    void changeProcess(int oldAddress, int newAddress) {
        int i = pageList.indexOf(new int[] { 0, oldAddress }) == -1 ? pageList.indexOf(new int[] { 1, oldAddress })
                : pageList.indexOf(new int[] { 0, oldAddress });

        if (pageList.get(i)[0] == 0)
            pageList.set(i, new int[] { 1, newAddress });
        else
            pageList.set(i, new int[] { 0, newAddress });
    }

    void endProcess(Time endTime) {
        this.endTime = endTime;
        this.turnaround = Time.substractTimes(endTime, startTime);
    }

}