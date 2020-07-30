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
     * Add new process to RAM, allocate and return in which pages was it allocated
     */

    public List<int[]> addProcess(int processId, int size, VRAM vram, Time time) throws Exception {

        List<int[]> addresses = new ArrayList<>();

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
            freeSpace(amountOfPagesToFill - FREE_PAGES, vram, time);
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

                // Add page index to FIFO_STACK

                FIFO_STACK.add(i);

                amountOfPagesToFill--;
                FREE_PAGES--;
                time.add(1);
            }
        }
        return addresses;
    }

    public void freeSpace(int amountOfPagesToFree, VRAM vram, Time time) throws Exception {

        int[] indexes = FIFO(amountOfPagesToFree);
        // int[] indexes = LRU(amountOfPagesToFree);

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
            
            List<int[]> addresses = vram.addProcess(currentId, sizeOfProcess);

            for (int[] x : addresses) {
                System.out.println("Address " + x[0] + " " + x[1]);
            }

            Arrays.fill(RAM, indexes[i], indexes[i] + offset - 1, -1);

            time.add(1);

            FREE_PAGES++;

        }
    }

    public int[] FIFO(int amountOfPagesToFree) {

        int[] indexes = new int[amountOfPagesToFree];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = FIFO_STACK.get(0);
            FIFO_STACK.remove(0);
        }

        return indexes;
    }

    public int getNUMBER_OF_PAGES() {
        return this.NUMBER_OF_PAGES;
    }

    // Print RAM by pages

    public void print() {
        for (int i = 0; i < RAM.length; i++) {
            if (i % PAGE_SIZE == 0) {
                System.out.println();
            }
            System.out.printf("[%4d]\t", RAM[i]);
        }
        System.out.println("\n");
    }

    public void printFIFO() {
        System.out.println(FIFO_STACK);
    }
}