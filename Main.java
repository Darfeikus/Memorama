import java.util.Scanner;
 
class Main{
    public static void main(String args[]){
        Scanner s = new Scanner(System.in);
        System.out.println("Press 1 for FIFO, 2 for LRU. Default is FIFO");
        int method = 0; //0 for FIFO, 1 for LRU
        switch(s.nextLine()){
            case "2":
                System.out.println("LRU selected. Starting simulation...");
                method=1;
                break;
            default:
                System.out.println("FIFO selected. Starting simulation...");
                break;
        }
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
