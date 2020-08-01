import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RAM {

    private int PAGE_SIZE;
    private int SIZE;
    private int NUMBER_OF_PAGES;
    private int PAGE_FAULTS = 0;

    private double timeOfSwap;
    private double timeOfAccess;

    private int[] RAM;
    private int FREE_PAGES;

    private List<Integer> FIFO_STACK = new ArrayList<Integer>();
    private List<Integer> LRU_STACK = new ArrayList<Integer>();
    private List<Integer> IDS = new ArrayList<Integer>();

    private List<Process> PROCESS_LIST = new ArrayList<Process>();

    /* Constructor of class RAM */

    public RAM(int size, int PAGE_SIZE, double timeOfSwap, double timeOfAccess) throws Exception {

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
        this.timeOfSwap = timeOfSwap;
        this.timeOfAccess = timeOfAccess;

        Arrays.fill(RAM, -1);
    }

    /*
     * Add new process to RAM, allocate and return amount of swaps in the operation
     */

    public int[] addProcess(int processId, int size, VRAM vram, Time time, int method) throws Exception {

        List<int[]> addresses = new ArrayList<>();
        int[] swaps = new int[2];
        int page = 0;

        if (IDS.indexOf(processId) != -1) {
            throw new RamException("ID already exists");
        } else {
            IDS.add(processId);
        }

        if (size > SIZE) {
            throw new RamException("Process cannot be fit into RAM");
        }

        System.out.printf("Assign %d bytes to process %d\n",size, processId);

        int amountOfPagesToFill = size / PAGE_SIZE;
        int bytesLeft = size % PAGE_SIZE;

        if (bytesLeft != 0) {
            amountOfPagesToFill++;
        }

        if (FREE_PAGES < amountOfPagesToFill) {
            // System.out.printf("Not enough pages, rellocating %d pages\n", amountOfPagesToFill - FREE_PAGES);
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

                addresses.add(new int[] { 0, i, page });
                page += PAGE_SIZE;

                // Add page index to FIFO_STACK and LRU

                addToStacks(i);

                amountOfPagesToFill--;
                FREE_PAGES--;

                // Swap in RAM
                time.addSeconds(timeOfSwap);
                swaps[1]++;
            }
        }
        Process newProcess = new Process(processId, size, time, addresses);
        PROCESS_LIST.add(newProcess);
        return swaps;
    }

    /*
     * Frees space in RAM by moving it to the instance of VRAM, this is where it
     * uses the algorithms for paging
     */

    private void freeSpace(int amountOfPagesToFree, VRAM vram, Time time, int[] swaps, int method) throws Exception {

        // Gets the indexes of where to store the process in RAM
        int[] indexes;

        if (method == 1) {
            indexes = LRUStack(amountOfPagesToFree);
        } else {
            indexes = FIFOStack(amountOfPagesToFree);
        }

        // System.out.println("Indexes of pages to dealloc " + Arrays.toString(indexes));
        int offset, sizeOfProcess, currentId;

        for (int i = 0; i < indexes.length; i++) {
            offset = 0;
            sizeOfProcess = 0;
            currentId = RAM[indexes[i]];

            while (offset < PAGE_SIZE && RAM[indexes[i] + offset++] != -1) {
                sizeOfProcess++;
            }

            //address from the process that was moved to VRAM
            
            int address = vram.addProcess(currentId, sizeOfProcess);
            
            System.out.printf("Frame %d of process %d swapped to frame %d of VRAM\n", indexes[i],currentId, address/PAGE_SIZE);

            updateList(currentId, indexes[i], address);

            Arrays.fill(RAM, indexes[i], indexes[i] + offset, -1);

            // Swap in VRAM
            time.addSeconds(timeOfSwap);
            swaps[0]++;

            FREE_PAGES++;

        }
    }

    /*
     * Cleans a process from both RAM and VRAM and returns the amount of swap outs
     */

    public int cleanProcess(int processId, VRAM vram, Time time) throws Exception {
        int swaps = 0;
        Process process = getProcess(processId);

        if (process == null) {
            throw new Exception("Process not found in memory");
        }

        // Count swaps

        swaps = process.getPageList().size();

        // RemoveFromVram

        vram.removeProcessFromMemory(processId);
        removeProcessFromMemory(processId);
        time.addSeconds(timeOfSwap * swaps);
        process.endProcess(time);
        return swaps;
    }

    /*
     * Removes a process from RAM memory
     */

    private boolean removeProcessFromMemory(int id) {
        boolean foundIt = false;
        for (int i = 0; i < SIZE; i += PAGE_SIZE) {
            if (RAM[i] == id) {
                for (int j = i; j < i + PAGE_SIZE; j++)
                    RAM[j] = -1;
                foundIt = true;
                FREE_PAGES++;
            }
        }
        return foundIt;
    }

    /*
        Removes a process from RAM memory and returns the size of the page
    */

    private int removePageFromMemory(int index) {
        int size = 0;
        for (int j = index; j < index + PAGE_SIZE; j++) {
            if (RAM[j] != -1) {
                size++;
                RAM[j] = -1;
            } else {
                FREE_PAGES++;
                return size;
            }
        }
        FREE_PAGES++;
        return size;
    }

    /*
        allocates a page in RAM, return its index
    */

    private int allocatePage(int processId, int size) {
        for (int i = 0; i < SIZE; i+= PAGE_SIZE) {
            if(RAM[i]==-1){
                Arrays.fill(RAM, i, i+size, processId);
                FREE_PAGES--;
                return i;
            }
        }
        return -1;
    }

    public int[] access(int address, int processId, VRAM vram, Time time, int method, int accessRead) throws Exception {
        time.addSeconds(timeOfAccess);
        
        Process process = getProcess(processId);
        System.out.printf("Obtaining real address from virtual address %d of process %d\n",address,processId);

        if(method == 1){
            System.out.println("and modifying");
        }
        
        if(process==null || !process.getStatus()){
            throw new Exception("Process not in memory");
        }
        
        if(address >= process.getSize()){
            throw new Exception("Virtual Address Not Found");
        }

        int[] swaps = new int[2];
        boolean found = false;
        
        //For each address search for the one we are looking for
        
        for (int[] x : process.getPageList()) {
            if(x[2] <= address && address < x[2]+PAGE_SIZE){
                found = true;
                //Si esta en memoria RAM
                if(x[0] == 0){
                    if(method==1){
                        System.out.printf("Frame %d of process %d modified\n",address/PAGE_SIZE,processId);
                    }
                    System.out.printf("Virtual address: %d RAM address: %d\n",address,x[1]+address%PAGE_SIZE);
                    LRU(x[1]);
                }
                else{
                    PAGE_FAULTS++;
                    int vramAddress = x[1];
                    int index;
                    //Remove page from VRAM and get the size of the page
                    int sizeOfPageVRAM = vram.movePageToRam(vramAddress);
                    int newIndexRam;
                    
                    if(FREE_PAGES>0){
                        newIndexRam = allocatePage(processId, sizeOfPageVRAM);
                        updateList(processId, vramAddress, newIndexRam);
                        System.out.printf("Page %d of process %d was localized in frame %d of VRAM\nit has been changed to frame %d in RAM\n", address, processId, vramAddress/PAGE_SIZE, newIndexRam/PAGE_SIZE);
                        if(method==1){
                            System.out.printf("Frame %d of process %d modified\n",address/PAGE_SIZE,processId);
                        }
                        System.out.printf("Virtual address: %d RAM address: %d\n",address,newIndexRam);
                        //Swap In
                        addToStacks(newIndexRam);
                        time.addSeconds(timeOfSwap);
                        swaps[1]++;
                        return swaps;
                    }
                    
                    if(method == 1){
                        index = LRUStack(1)[0];
                    }
                    else{
                        index = FIFOStack(1)[0];
                    }

                    removeFromStacks(index);
                    
                    int movedProcessId = RAM[index];
                    int sizeOfPageRAM = removePageFromMemory(index);
                    
                    //swap out
                    
                    time.addSeconds(timeOfSwap);
                    swaps[0]++;
                    int newIndexVram = vram.addProcess(movedProcessId, sizeOfPageRAM);
                    updateList(movedProcessId, index, newIndexVram);
                    //swap in
                    time.addSeconds(timeOfSwap);
                    swaps[1]++;
                    newIndexRam = allocatePage(processId, sizeOfPageVRAM);
                    updateList(processId, vramAddress, newIndexRam);
                    
                    System.out.printf("Frame %d of process %d swapped to frame %d of VRAM\n", index, movedProcessId, newIndexVram/PAGE_SIZE);
                    System.out.printf("Page %d of process %d was localized in frame %d of VRAM\nit has been changed to frame %d in RAM\n", address, processId, vramAddress/PAGE_SIZE, newIndexRam/PAGE_SIZE);
                    if(method==1){
                        System.out.printf("Frame %d of process %d modified\n",address/PAGE_SIZE,processId);
                    }
                    System.out.printf("Virtual address: %d RAM address: %d\n",address,newIndexRam+address%PAGE_SIZE);
                    addToStacks(newIndexRam);
                }
            }
        }
        
        if(!found){
            throw new Exception("Virtual Address not found");
        }

        return swaps;
    }

    private Process getProcess(int processId) {
        for (int i = 0; i < PROCESS_LIST.size(); i++) {
            if (PROCESS_LIST.get(i).getId() == processId)
                return PROCESS_LIST.get(i);
        }
        return null;
    }

    // private int indexOfProcess(int processId){
    // for(int i =0;i<PROCESS_LIST.size();i++){
    // if(PROCESS_LIST.get(i).getId() == processId)
    // return i;
    // }
    // return -1;
    // }

    /*
     * Updates which addresses where moved from VRAM to RAM in the processList
     */

    private void updateList(int processId, int oldIndex,int newIndex) {
        Process process = processAtIndex(processId);
        process.changeProcess(oldIndex, newIndex);
    }

    /*
     * Returns Process in PROCESS_LIST from processId
     */

    private Process processAtIndex(int processId) {
        for (Process x : PROCESS_LIST) {
            if (x.getId() == processId)
                return x;
        }
        return null;
    }

    /*
     * algorithm that returns an array of indexes of pages based on FIFO
     */

    private int[] FIFOStack(int amountOfPagesToFree) {

        int[] indexes = new int[amountOfPagesToFree];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = FIFO_STACK.get(0);
            removeFromStacks(indexes[i]);
        }

        return indexes;
    }

    /*
     * algorithm that returns an array of indexes of pages based on FIFO
     */

    private int[] LRUStack(int amountOfPagesToFree) {

        int[] indexes = new int[amountOfPagesToFree];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = LRU_STACK.get(0);
            removeFromStacks(indexes[i]);
        }

        return indexes;
    }

    /*
     * Remove address from all stacks
     */

    private void removeFromStacks(int address) {
        FIFO_STACK.remove((Integer) address);
        LRU_STACK.remove((Integer) address);
    }

    private void addToStacks(int address) {
        FIFO_STACK.add((Integer) address);
        LRU_STACK.add((Integer) address);
    }

    /*
     * updates LRU with the most recentrly used page
     */

    private void LRU(int address) {
        LRU_STACK.remove((Integer) address);
        LRU_STACK.add(address);
    }

    /*
        returns number of Free Pages
    */
    public int get_NUMBER_OF_PAGES() {
        return this.NUMBER_OF_PAGES;
    }

    /*
        returns number of PAGE FAULTS
    */

    public int get_PAGE_FAULTS() {
        return this.PAGE_FAULTS;
    }

    /*
     * Print RAM by pages
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
     * Prints all processes address list
     */

    public void printProcessesAddressList() {
        for (Process x : PROCESS_LIST) {
            x.printAddresses();
        }
    }

    /*
     * Prints FIFO stack
     */

    public void printFIFO() {
        System.out.println(FIFO_STACK);
    }
}