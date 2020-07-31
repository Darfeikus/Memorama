//Class that emulates a virtual memory
public class VRAM {
    private final int PAGE_SIZE ; //Size of the page
    private final int MEMORY_LENGTH; //It is declared the size of the memory
    private final int NUMBER_OF_PAGES; //The number of pages that the memory has, it depens on the page size 
    
    private int[] vram; //The virtual memory
    private int freePages;

    public VRAM(int memorySize, int pageSize) {
        this.PAGE_SIZE = pageSize;
        this.MEMORY_LENGTH = memorySize;
        this.NUMBER_OF_PAGES = (int) MEMORY_LENGTH / PAGE_SIZE;
        this.freePages = NUMBER_OF_PAGES;
        initArray();
    }

    /*
        The whole array has -1, it means that it is completely empty
    */

    private void initArray() { 
        vram = new int [MEMORY_LENGTH];
        for (int i = 0; i < MEMORY_LENGTH; i++)
            vram[i] = -1;
    }
    
    /*
        It removes the process from the memory, it iterates over each page, if the process id is found it is removed
    */

    public boolean removeProcessFromMemory(int id) { 
        boolean foundIt = false;
        for (int i = 0; i < MEMORY_LENGTH; i+=PAGE_SIZE) {
            if (vram[i] == id) {
                for (int j = i; j < i+PAGE_SIZE; j++)
                    vram[j] = -1;
                foundIt = true;
                freePages++;
            }
        }
        return foundIt;
    }

    /* 
        A process is added to memory, first check if there are enough spaces, if there is then it is stored in memory
        returns the index of where it was stored
    */

    public int addProcess(int processId, int size) throws Exception{ 
        int index;
        int numberOfPages = (int)(Math.ceil((double)size/PAGE_SIZE));
        if (numberOfPages > freePages) {
            throw new MoreThanFreeMemoryException("Se pide mas memoria de la que hay disponible");
        }
        index = storeInMemory(processId, numberOfPages, size);
        return index;
    }

    /*
        Iterate over the arrangement to see the available spaces where to add the pages
        returns a list with the addresses of where the process was stored
    */

    private int storeInMemory(int processId, int numberOfPages, int size) {
        for (int i = 0; i < MEMORY_LENGTH && numberOfPages != 0; i+=PAGE_SIZE) {
            if (vram[i] == -1) {
                fillingMemory(processId, size, numberOfPages, i);
                freePages--;
                return i;
            }
        }
        return -1;
    }

    /*
        Memory is full, if the space you want to add is not multiple of PAGE_SIZE, then in the last iteration just the space is saved
    */

    private void fillingMemory(int processId, int size, int numberOfPages, int indexOfPage) {
        int remainingSpace = size%PAGE_SIZE;
        if(numberOfPages == 1 && remainingSpace != 0){
            for (int j = indexOfPage; j < indexOfPage+remainingSpace; j++)
                vram[j] = processId;
        }else {
            for (int j = indexOfPage; j < indexOfPage+PAGE_SIZE; j++)
                vram[j] = processId;
        }
    }

    /*
        It returns the size of a process that is gonna be moved to ram, then it cleans memory
    */
    
    public int movePageToRam(int index) {
        int size = 0;
        for (int j = index; j < index + PAGE_SIZE; j++) {
            if (vram[j] != -1) {
                size++;
                vram[j] = -1;
            } else {
                freePages++;
                return size;
            }
        }
        freePages++;
        return size;        
    }

    /*
        Returns the size of certain process
    */

    private int countSpace(int processId) {
        int sizeOfProcess = 0;
        for (int i = 0; i < MEMORY_LENGTH; i++) 
            if(vram[i] == processId)
                sizeOfProcess++;
        return sizeOfProcess;
    }

    /*
        Returns the number of free pages
    */ 

    public int getFreePages() {
        return this.freePages;
    }

    public void print() {
        System.out.printf("VRAM:\n");
        for (int i = 0; i < vram.length; i++) {
            if (i % PAGE_SIZE == 0) {
                System.out.println();
            }
            System.out.printf("[%4d]\t", vram[i]);
        }
        System.out.println("\n");
    }

}