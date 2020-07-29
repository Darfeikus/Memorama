import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RAM{
    
    private int[] RAM;
    private int SIZE;
    private int NUMBER_OF_PAGES;
    private int PAGE_SIZE; 
    private int FREE_PAGES;
    private List<Integer> FIFO_STACK = new ArrayList<Integer>();
    private List<Integer> LRU_STACK = new ArrayList<Integer>();
    private List<Integer> IDS = new ArrayList<Integer>();

    /*Constructor of class RAM*/

    public RAM(int size, int PAGE_SIZE){
        try{

            boolean powersOfTwo = size > 0 && (( size & (size-1) )) == 0 && PAGE_SIZE > 0 && (( PAGE_SIZE & (PAGE_SIZE-1) )) == 0;
    
            if(!powersOfTwo){
                throw new RamException("Only powers of 2 allowed");
            }
    
            if(size < PAGE_SIZE){
                throw new RamException("Page size can't be greater than RAM size");
            }
            
            this.SIZE = size;
            this.PAGE_SIZE = PAGE_SIZE;
            this.NUMBER_OF_PAGES = size/PAGE_SIZE;
            this.FREE_PAGES = NUMBER_OF_PAGES;
            this.RAM = new int[size];
    
            Arrays.fill(RAM,-1);
        
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*Add new process to RAM, allocate and return in which pages was it allocated*/

    public List<int[]> addProcess(int processId, int size, VRAM vram, Time time){
        
        List<int[]> addresses = new ArrayList<>();
        
        try{
                       
            if(IDS.indexOf(processId) != -1){
                throw new RamException("ID already exists");
            }
            else{
                IDS.add(processId);
            }

            if(size > SIZE){
                throw new RamException("Process cannot be fit into RAM");
            }
            
            int amountOfPagesToFill = size/PAGE_SIZE;
            int bytesLeft = size%PAGE_SIZE;
    
            if(bytesLeft!=0){
                amountOfPagesToFill++;
            }
            
            if(FREE_PAGES < amountOfPagesToFill){
                System.out.printf("Not enough pages, rellocating %d pages\n", amountOfPagesToFill-FREE_PAGES);
                freeSpace(amountOfPagesToFill-FREE_PAGES, vram, time);
            }
            
            //Fill RAM

            for(int i = 0; i < RAM.length && amountOfPagesToFill > 0; i+=PAGE_SIZE){
                if(RAM[i] == -1){

                    if(amountOfPagesToFill==1 && bytesLeft!=0){
                        Arrays.fill(RAM, i, i+bytesLeft, processId);
                    }
                    else{
                        Arrays.fill(RAM, i, i+PAGE_SIZE, processId);
                    }
                    
                    //Add page index to list

                    addresses.add(new int[]{0,i});
                    
                    //Add page index to FIFO_STACK
                    
                    FIFO_STACK.add(i);

                    amountOfPagesToFill--;
                    FREE_PAGES--;
                    time.add(1);
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return addresses;
    }

    public void freeSpace(int amountOfPagesToFree, VRAM vram, Time time){
        int[] indexes = FIFO(amountOfPagesToFree);
        // int[] indexes = LRU(amountOfPagesToFree);

        System.out.println("Pages to dealloc " + Arrays.toString(indexes));
        int offset,sizeOfProcess, currentId;

        for(int i = 0; i < indexes.length ;i++){
            
            offset = 0;
            sizeOfProcess=0;
            currentId = RAM[indexes[i]];
            
            System.out.println("Freeing page of process_id = " + currentId);
            
            while( RAM[indexes[i]+offset] != -1 && offset<PAGE_SIZE){
                sizeOfProcess++;
                RAM[indexes[i]+offset++] = -1;
            }

            try{
                System.out.printf("Requesting to move process %d with size %d\n",currentId,sizeOfProcess);
                vram.addProcess(currentId,sizeOfProcess);
                time.add(1);
            }
            catch(Exception e){
                throw e;
            }

        }
    }

    public int[] FIFO(int amountOfPagesToFree){
        
        int[] indexes = new int[amountOfPagesToFree];

        for(int i = 0; i < indexes.length; i++){
            indexes[i] = FIFO_STACK.get(0);
            FIFO_STACK.remove(0);
        }

        return indexes;
    }

    public int getNUMBER_OF_PAGES(){
        return this.NUMBER_OF_PAGES;
    }
    
    //Print RAM by pages

    public void print(){
        for(int i = 0; i < RAM.length; i++){
            if(i%PAGE_SIZE==0){
                System.out.println();
            }
            System.out.printf("%d\t",RAM[i]);
        }
        System.out.println();
    }

    public void printFIFO(){
        System.out.println(FIFO_STACK);
    }
}