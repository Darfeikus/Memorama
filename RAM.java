import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RAM {

    private int PAGE_SIZE;
    private int SIZE;
    private int NUMBER_OF_PAGES;

    private int[] RAM;
    private int FREE_PAGES;

    private List<Integer> FIFO_STACK = new ArrayList<Integer>();
    private List<Integer> LRU_STACK = new ArrayList<Integer>();
    private List<Integer> IDS = new ArrayList<Integer>();

    private List<Process> PROCESS_LIST = new ArrayList<Process>();

    /* Constructor of class RAM */

    public RAM(int size, int PAGE_SIZE) throws Exception {

        boolean powersOfTwo = size > 0 && ((size & (size - 1))) == 0 && PAGE_SIZE > 0
                && ((PAGE_SIZE & (PAGE_SIZE - 1))) == 0;

        if (!powersOfTwo) {
            throw new RamException("Only powers of 2 allowed");
        }

        if (size < PAGE_SIZE) {
            throw new RamException("Page size can't be greater than RAM size");
        }

        this.SIZE = size;
        this.PAGE_SIZE = PAGE_SIZE;
        this.NUMBER_OF_PAGES = size / PAGE_SIZE;
        this.FREE_PAGES = NUMBER_OF_PAGES;
        this.RAM = new int[size];

        Arrays.fill(RAM, -1);
    }

    /*
        Add new process to RAM, allocate and return amount of swaps in the operation
    */

    public int[] addProcess(int processId, int size, VRAM vram, Time time, int method) throws Exception {

        List<int[]> addresses = new ArrayList<>();
        int[] swaps = new int[2];

        if (IDS.indexOf(processId) != -1) {
            throw new RamException("ID already exists");
        } else {
            IDS.add(processId);
        }

        if (size > SIZE) {
            throw new RamException("Process cannot be fit into RAM");
        }

        int amountOfPagesToFill = size / PAGE_SIZE;
        int bytesLeft = size % PAGE_SIZE;

        if (bytesLeft != 0) {
            amountOfPagesToFill++;
        }

        if (FREE_PAGES < amountOfPagesToFill) {
            System.out.printf("Not enough pages, rellocating %d pages\n", amountOfPagesToFill - FREE_PAGES);
            freeSpace(amountOfPagesToFill - FREE_PAGES, vram, time, swaps, method);
        }

        // Fill RAM

        for (int i = 0; i < RAM.length && amountOfPagesToFill > 0; i += PAGE_SIZE) {
            if (RAM[i] == -1) {

                if (amountOfPagesToFill == 1 && bytesLeft != 0) {
                    Arrays.fill(RAM, i, i + bytesLeft, processId);
                } else {
                    Arrays.fill(RAM, i, i + PAGE_SIZE, processId);
                }

                // Add page index to list

                addresses.add(new int[] { 0, i });

                // Add page index to FIFO_STACK and LRU

                FIFO_STACK.add(i);
                LRU_STACK.add(i);

                amountOfPagesToFill--;
                FREE_PAGES--;
                
                //Swap in RAM
                time.addSeconds(1);
                swaps[0]++;
            }
        }
        Process newProcess = new Process(processId, size, time, addresses);
        PROCESS_LIST.add(newProcess);
        return swaps;
    }

    /*
        Frees space in RAM by moving it to the instance of VRAM, this is where it uses 
        the algorithms for paging
    */

    private void freeSpace(int amountOfPagesToFree, VRAM vram, Time time, int[] swaps, int method) throws Exception {

        //Gets the indexes of where to store the process in RAM
        int[] indexes;
        
        if(method == 1){
            indexes = LRU(amountOfPagesToFree);
        }
        else{
            indexes = FIFO(amountOfPagesToFree);
        }

        System.out.println("Indexes of pages to dealloc " + Arrays.toString(indexes));
        int offset, sizeOfProcess, currentId;

        for (int i = 0; i < indexes.length; i++) {
            offset = 0;
            sizeOfProcess = 0;
            currentId = RAM[indexes[i]];

            while (offset < PAGE_SIZE && RAM[indexes[i] + offset++] != -1) {
                sizeOfProcess++;
            }

            System.out.printf("Requesting to move process %d in page %d of size %d\n", currentId, indexes[i],
                    sizeOfProcess);
            
            //List of addresses from the process that was moved to VRAM 
            
            List<int[]> addresses = vram.addProcess(currentId, sizeOfProcess);

            updateList(currentId, indexes[i], addresses);
            
            Arrays.fill(RAM, indexes[i], indexes[i] + offset, -1);
            
            //Swap in VRAM
            time.addSeconds(1);
            swaps[1]++; 

            FREE_PAGES++;

        }
    }

    /*
        Updates which addresses where moved from VRAM to RAM in the process processList
    */

    private void updateList(int processId, int oldIndex, List<int[]> addresses){
        Process process = processAtIndex(processId);

        for (int[] x : addresses) {
            process.changeProcess(oldIndex, x[1]);
        }
    }

     /*
        Returns Process in PROCESS_LIST from processID
    */
    
    private Process processAtIndex(int processId){
        for (Process x : PROCESS_LIST) {
            if(x.getId() == processId)
                return x;
        }
        return null;
    }

    /*
        algorithm that returns an array of indexes of pages based on FIFO
    */

    private int[] FIFO(int amountOfPagesToFree) {

        int[] indexes = new int[amountOfPagesToFree];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = FIFO_STACK.get(0);
            FIFO_STACK.remove(0);
        }

        return indexes;
    }

    private int[] LRU(int amountOfPagesToFree) {

        int[] indexes = new int[amountOfPagesToFree];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = FIFO_STACK.get(0);
            LRU_STACK.remove(0);
        }

        return indexes;
    }

    public int getNUMBER_OF_PAGES() {
        return this.NUMBER_OF_PAGES;
    }

    /*
        Print RAM by pages
    */
     
    public void print() {
        System.out.printf("RAM:\n");
        for (int i = 0; i < RAM.length; i++) {
            if (i % PAGE_SIZE == 0) {
                System.out.println();
            }
            System.out.printf("[%4d]\t", RAM[i]);
        }
        System.out.println("\n");
    }

    /*
        Prints all processes address list
    */

    public void printProcessesAddressList(){
        for(Process x:PROCESS_LIST){
            x.printAddresses();
        }
    }

    /*
        Prints FIFO stack
    */

    public void printFIFO() {
        System.out.println(FIFO_STACK);
    }
}