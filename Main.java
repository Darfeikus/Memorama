import java.util.List;
import java.util.Arrays;

class Main{
    
    public static void print(List<int[]> list){
        for(int[] x:list){
            System.out.println(x[0] + " " + x[1]);
        }
        System.out.println();
    }

    public static void sum(int[] x, int[] y){
        x[0]+=y[0];
        x[1]+=y[1];
    }

    public static void main(String args[]){
        try{
            RAM ram = new RAM(64,16);
            VRAM vram = new VRAM(128,16);
            Time time = new Time();
            
            int[] swaps = new int[2];
            
            sum(swaps,ram.addProcess(4, 17, vram, time,0));
            sum(swaps,ram.addProcess(5, 32, vram, time,0));
            sum(swaps,ram.addProcess(1, 15, vram, time,0)); //Deallocs
            sum(swaps,ram.addProcess(2, 16, vram, time,0)); //Deallocs
            sum(swaps,ram.addProcess(6, 64, vram, time,0)); //Fills
            sum(swaps,ram.addProcess(3, 32, vram, time,0)); //Deallocs
            
            ram.print();
            vram.print();
            ram.printProcessesAddressList();
            System.out.println("Swaps: " + Arrays.toString(swaps));
            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }   
}