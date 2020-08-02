
/* Author: Gerardo Granados Aldaz */

import java.util.*;

public class Process{
    private int id; // unique id for process
    private int size;
    private List<int[]> pageList = new ArrayList<>(); // list of int array of size 2, [0] 0 is RAM, 1 is VRAM, [1] is
                                                      // address.
    private Time startTime;
    private Time endTime;
    private Time turnaround;
    private boolean active;
    private int PAGE_FAULTS;

    /*
        Constructor of class Process
    */

    public Process(int id, int size, Time startTime, List<int[]> pageList) {
        this.id = id;
        this.size = size;
        this.startTime = new Time(startTime);
        this.pageList = pageList;
        this.active = true;
    }

    /*
        Called by RAM, moves processes from RAM to VRAM, and vice versa
    */

    public void changeProcess(int oldAddress, int newAddress) {
        int i = searchPageWithAddress(oldAddress);
        
        if (pageList.get(i)[0] == 0){
            pageList.get(i)[0] = 1;
            pageList.get(i)[1] = newAddress;
        }
        else{
            pageList.get(i)[0] = 0;
            pageList.get(i)[1] = newAddress;
        }
    }

    public void addPage_Fault(int i){
        PAGE_FAULTS+=i;
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
        this.endTime = new Time(endTime);
        this.turnaround = Time.substractTimes(endTime, startTime);
        this.active = false;
    }

    /*
        returns Id of the process
    */

    public int getId(){
        return this.id;
    }

    /*
        returns size of the process
    */

    public int getSize(){
        return this.size;
    }

    /*
        returns turnaround time;
    */

    public Time getTurnaround(){
        return this.turnaround;
    }

    /*
        returns pageList
    */

    public List<int[]> getPageList(){
        return this.pageList;
    }

    /*
        returns active status
    */

    public boolean getStatus(){
        return this.active;
    }

     /*
        returns number of page faults
    */

    public int getPageFaults(){
        return this.PAGE_FAULTS;
    }

    /*
        Print the addresses of the process
    */

    public void printAddresses(){
        System.out.printf("Process %d address list\n",id);
        
        List<Integer> real = new ArrayList<Integer>();
        List<Integer> virtual = new ArrayList<Integer>();

        for(int[]x : pageList){
            if(x[0] == 0){
                real.add((Integer)x[1]);
            }
            else{
                virtual.add((Integer)x[1]);
            }
        }

        System.out.printf("Frames in RAM memory: [");
        for (int i = 0; i<real.size();i++) {
            if(i!=real.size()-1)
                System.out.printf("%d,", real.get(i));
            else
                System.out.printf("%d", real.get(i));
        }
        System.out.println("]");
        
        System.out.printf("Frames in VRAM memory: [");
        for (int i = 0; i<virtual.size();i++) {
            if(i!=virtual.size()-1)
                System.out.printf("%d,", virtual.get(i));
            else
                System.out.printf("%d", virtual.get(i));
        }
        System.out.println("]");
    }

}