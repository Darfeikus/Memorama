import java.util.*;

public class Simulation {
    private Scanner scanner;
    private Time time;
    private RAM ram;
    private VRAM vram;
    private int swapMethod;
    private List<Block> blockList = new ArrayList<>();
    // Log log = new Log();
    private int[] swaps = new int[2]; // 0 for out y 1 for in
    private boolean running;

    /* Simulation constructor */

    Simulation(Scanner scanner, int PAGE_SIZE, int RAM_SIZE, int VRAM_SIZE, double timeOfSwap, double timeOfAccess,
            double timeOfFreeing, int swapMethod) {
        this.scanner = scanner;
        try {
            this.ram = new RAM(RAM_SIZE, PAGE_SIZE, timeOfSwap, timeOfAccess, timeOfFreeing);
            this.vram = new VRAM(VRAM_SIZE, PAGE_SIZE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.swapMethod = swapMethod;
        this.time = new Time();
        this.running = true;
    }

    /*
        Returns a String parsed as an int
   */

    private int parseInt(String s) {
        return Integer.parseInt(s);
    }

    /*
        Ends Simulation by changing running value
    */

    private void endSimulation() {
        ram.print();
        vram.print();
        System.out.println("GAMER OVER");
        this.running = false;
    }

    /*
        Prints a comment from the user in console
    */

    public void commentary(String s) {
        System.out.println(s);
    }

    /*
        Reads the entry from the user, and performs the correspondent assignment
    */

    private void readEntry(String s) {
        String[] inputs = s.split(" ");
        System.out.println(s);
        switch (inputs[0]) {
            case "A":
                accessVirtualAddress(parseInt(inputs[1]), parseInt(inputs[2]), parseInt(inputs[3]));
                break;
            case "C":
                commentary(s.substring(2));
                break;
            case "E":
                endSimulation();
                break;
            case "F":
                report();
                break;
            case "L":
                clean(parseInt(inputs[1]));
                break;
            case "P":
                createProcess(parseInt(inputs[2]), parseInt(inputs[1]));
                break;
        }
    }

    /*
        Calls to clean operation in RAM memory for a specific Process
    */

    private void clean(int processId) {
        try {
            swaps[0] += ram.cleanProcess(processId, vram, time);
            Process temp = ram.getDeadProcesses().get(ram.getDeadProcesses().size() - 1);
            temp.printAddresses();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
        Calls to addProcess operation in RAM memory for a specific id
    */

    private void createProcess(int processId, int processSize) {
        try {
            int[] operationSwaps = ram.addProcess(processId, processSize, vram, time, 0);
            swaps[0] += operationSwaps[0];
            swaps[1] += operationSwaps[1];
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
        Calls to access operation in RAM memory for a reading/Modifying an address
    */

    private void accessVirtualAddress(int address, int processId, int readWrite) {
        try {
            int[] operationSwaps = ram.access(address, processId, vram, time, swapMethod, readWrite);
            swaps[0] += operationSwaps[0];
            swaps[1] += operationSwaps[1];
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
        Checks if a command isued by the user has a fvalid syntax
    */

    private boolean checkValidString(String s) {
        String[] inputs = s.split(" ");
        switch (inputs[0]) {
            case "A":
                if (inputs.length == 4) { // Check size first to avoid outOfRangeException
                    // Check correct data types
                    if (isInt(inputs[1]) && isInt(inputs[2]) && (inputs[3].equals("0") | inputs[3].equals("1")))
                        return true;
                    else
                        return false;
                } else
                    return false;
            case "C":
                if (s.length() > 3)
                    return true;
                else
                    return false;
            case "E":
                if (s.equals("E"))
                    return true;
                else
                    return false;
            case "F":
                if (s.equals("F"))
                    return true;
                else
                    return false;
            case "L":
                if (inputs.length == 2) {
                    if (isInt(inputs[1]))
                        return true;
                    else
                        return false;
                } else
                    return false;
            case "P":
                if (inputs.length == 3) {
                    if (isInt(inputs[1]) && isInt(inputs[2]))
                        return true;
                    else
                        return false;
                } else
                    return false;
        }
        return false;
    }

    /*
        Starts simulation
    */

    public void startSimulation() {
        String command;
        while (running && scanner.hasNextLine()) {
            command = scanner.nextLine();
            if (checkValidString(command)) {
                readEntry(command);
            } else {
                System.out.println("Command not valid");
            }
        }
    }

    /*
        Checks if a String is a valid integer
    */

    static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /*
        Prints a whole report of the block of processes
    */
    public void report() {
        List<Process> deadProcesses = ram.getDeadProcesses();
        if (deadProcesses.size() == 0) {
            System.out.println("Processes were not ended during this block");
            return;
        }
        turnaround(deadProcesses);
        numberOfPageFaults(deadProcesses);
        numberOfSwaps();
        ram.deleteProcesses();
        swaps = new int[2];
    }

    /* 
        Calculates the average and individual turnaround of all processes in the block
   */
    private void turnaround(List<Process> deadProcesses) {
        System.out.println("Turnaround Por Proceso: ");
        double sumSeconds = 0;
        Time timeTemp;
        for (Process process : deadProcesses) {
            timeTemp = process.getTurnaround();
            System.out.print("\tProceso " + process.getId() + ": ");
            timeTemp.print();
            sumSeconds += timeTemp.getMiliseconds() * 0.001;
            sumSeconds += timeTemp.getSeconds();
            sumSeconds += timeTemp.getMinutes() * 60;
            sumSeconds += timeTemp.getHours() * 3600;
        }
        average(deadProcesses.size(), sumSeconds);

    }

    /*
        displays the average turnaround
    */

    private void average(int size, double sumSeconds) {
        double average = sumSeconds / size;
        Time timeTemp = new Time();
        timeTemp.addSeconds(average);
        System.out.println("Turnaround Promedio: ");
        System.out.printf("\t");
        timeTemp.print();
    }

    /*
        Displays the number of pagefault in a block
    */

    private void numberOfPageFaults(List<Process> deadProcesses) {
        System.out.println("Page Faults");
        for (Process process : deadProcesses) {
            System.out.println("\tProceso " + process.getId() + ": " + process.getPageFaults());
        }
    }
    
    /*
        Displays number of swaps in a block
    */

    private void numberOfSwaps() {
        System.out.println("Swap out: " + swaps[0]);
        System.out.println("Swap in: " + swaps[1]);
    }

}