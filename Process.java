
/* Author: Gerardo Granados Aldaz */

import java.util.*;

public class Process {
    private int id; // unique id for process
    private int size;
    private List<int[]> pageList = new ArrayList<>(); // list of int array of size 2, [0] 0 is RAM, 1 is VRAM, [1] is
                                                      // address.
    private Time startTime;
    private Time endTime;
    private Time turnaround;

    /*
        Constructor of class Process
    */

    public Process(int id, int size, Time startTime, List<int[]> pageList) {
        this.id = id;
        this.size = size;
        this.startTime = startTime;
        this.pageList = pageList;
    }

    /*
        Called by RAM, moves processes from RAM to VRAM, and vice versa
    */

    public void changeProcess(int oldAddress, int newAddress) {
        int i = searchPageWithAddress(oldAddress);
        if (pageList.get(i)[0] == 0)
            pageList.set(i, new int[] { 1, newAddress });
        else
            pageList.set(i, new int[] { 0, newAddress });
    }

    /*
        search for the index of specific address
    */

    private int searchPageWithAddress(int address){
        for(int i = 0; i < pageList.size(); i++){
            if (pageList.get(i)[1] == address) {
                return i;
            }
        }
        return -1;
    }

    /*
        ends process and calculates turnaround time
    */

    public void endProcess(Time endTime) {
        this.endTime = endTime;
        this.turnaround = Time.substractTimes(endTime, startTime);
    }

    /*
        returns Id of the process
    */

    public int getId(){
        return this.id;
    }

    /*
        Print the addresses of the process
    */

    public void printAddresses(){
        System.out.printf("\nProcess %d address list\n",id);
        for(int[]x : pageList){
            System.out.printf("[%d][%d]\n",x[0],x[1]);
        }
        System.out.println();
    }
}