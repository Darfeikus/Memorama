import java.util.Scanner;
 
class Main{
    public static void main(String args[]){
        Scanner s = new Scanner(System.in);
        int method = 1; //0 for FIFO, 1 for LRU
        //Simulation(Scanner, page_Size, RAM_Size,VRAM_Size,Time of swap, time of access, time of freeing)
        try{
            Simulation simulation = new Simulation(s, 16, 2048, 4096, 1, 0.1,0.1,method);
            simulation.startSimulation();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
