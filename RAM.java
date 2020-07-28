import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RAM{
    
    private int[] RAM;
    private int SIZE;
    private int NUMBER_OF_PAGES;
    private int PAGE_SIZE; 
    private int FREE_PAGES;
    
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
            this.FREE_PAGES = NUMBER_OF_PAGES;
            this.NUMBER_OF_PAGES = size/PAGE_SIZE;
            this.RAM = new int[size];
    
            Arrays.fill(RAM,-1);
        
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*Add new process to RAM, allocate and return in which pages was it allocated*/

    public List<int[]> addProcess(int processId, int size){
        List<int[]> addresses = new ArrayList<>();
        try{
            if(size > SIZE){
                throw new RamException("Process cannot be fit into RAM");
            }
            int amountOfPagesToFill = size/PAGE_SIZE;
            int bytesLeft = size%PAGE_SIZE;
    
            if(bytesLeft!=0){
                amountOfPagesToFill++;
            }
    
            if(FREE_PAGES < amountOfPagesToFill){
                freeSpace(amountOfPagesToFill-FREE_PAGES);
            }
            
            for(int i = 0; i < RAM.length && amountOfPagesToFill > 0; i+=PAGE_SIZE){
                if(RAM[i] == -1){
                    if(amountOfPagesToFill==1 && bytesLeft!=0){
                        Arrays.fill(RAM, i, i+bytesLeft, processId);
                    }
                    else{
                        Arrays.fill(RAM, i, i+PAGE_SIZE, processId);
                    }
                    addresses.add(new int[]{0,i});
                    amountOfPagesToFill--;  
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return addresses;

    }

    public void freeSpace(int amountOfPagesToFree){

    }

    public int getNUMBER_OF_PAGES(){
        return this.NUMBER_OF_PAGES;
    }
    
}