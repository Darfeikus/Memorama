import java.util.*;

public class Simulation {
    Scanner scanner;
    Time time;
    RAM ram;
    VRAM vram;
    List<Block> blockList = new ArrayList<>();
    // Log log = new Log();
    int currentPageFaults = 0;
    int[] swaps = new int[2]; // 0 for out y 1 for in

    Simulation(Scanner scanner, int PAGE_SIZE, int RAM_SIZE, int VRAM_SIZE, double timeOfSwap, double timeOfAccess) {
        this.scanner = scanner;
        try {
            this.ram = new RAM(RAM_SIZE, PAGE_SIZE,timeOfSwap,timeOfAccess);
            this.vram = new VRAM(VRAM_SIZE, PAGE_SIZE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.time = new Time();
    }

    private void readEntry(String s){

    }

    public void createProcess(int processId, int processSize) {

    }

    private boolean checkValidString(String s){
        return true;
    }

    public void startSimulation(){
        String command = "";
        while(command.charAt(0) != 'F'){
            command = scanner.nextLine();
            if(checkValidString(command)){
                readEntry(command);
            }
            else{
                System.out.println("Command not valid");
            }
        }
        System.out.println("Out");
        // report();
    }

}