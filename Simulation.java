import java.util.*;

public class Simulation {
    private Scanner scanner;
    private Time time;
    private RAM ram;
    private VRAM vram;
    private List<Block> blockList = new ArrayList<>();
    // Log log = new Log();
    private int currentPageFaults = 0;
    private int[] swaps = new int[2]; // 0 for out y 1 for in
    private boolean running;

    Simulation(Scanner scanner, int PAGE_SIZE, int RAM_SIZE, int VRAM_SIZE, double timeOfSwap, double timeOfAccess) {
        this.scanner = scanner;
        try {
            this.ram = new RAM(RAM_SIZE, PAGE_SIZE,timeOfSwap,timeOfAccess);
            this.vram = new VRAM(VRAM_SIZE, PAGE_SIZE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.time = new Time();
        this.running = true;
    }

    /*
        Ends Simulation by changing running value
    */
    
    private void endSimulation(){
        ram.print();
        vram.print();
        this.running = false;
    }

    /*
        Prints a comment from the user in console
    */

    public void commentary(String s){
        System.out.println(s);
    }

    /*
        Reads the entry from the user, and performs the correspondent assignment
    */

    private void readEntry(String s){
        String[] inputs = s.split(" ");
        switch(inputs[0]){
            case "A":
                    accessVirtualAddress(Integer.parseInt(inputs[1]),Integer.parseInt(inputs[2]),Integer.parseInt(inputs[3]));
                break;
            case "C":
                commentary(inputs[1]);
                break;
            case "E":
                break;
            case "F":
                endSimulation();
                //report();
                break;
            case "L":
                break;
            case "P":
                createProcess(Integer.parseInt(inputs[2]),Integer.parseInt(inputs[1]));
                break;
        }
    }

    private void createProcess(int processId, int processSize) {
        try{
            int[] operationSwaps = ram.addProcess(processId, processSize, vram, time, 0);
            swaps[0] += operationSwaps[0];
            swaps[1] += operationSwaps[1];
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void accessVirtualAddress(int address, int processId, int method){
        try{
            ram.access(address, processId, vram, time, method);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    private boolean checkValidString(String s){
        return true;
    }

    public void startSimulation(){
        String command;
        while(running && scanner.hasNextLine()){
            command = scanner.nextLine();
            if(checkValidString(command)){
                readEntry(command);
            }
            else{
                System.out.println("Command not valid");
            }
            ram.print();
        }
    }

}