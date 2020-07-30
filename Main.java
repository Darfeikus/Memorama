import java.util.List;
import java.util.ArrayList;

class Main{
    
    public static void print(List<int[]> list){
        for(int[] x:list){
            System.out.println(x[0] + " " + x[1]);
        }
        System.out.println();
    }

    public static void main(String args[]){
        try{
            RAM ram = new RAM(64,16);
            VRAM vram = new VRAM(128,16);
            Time time = new Time();

            List<int[]> addresses = new ArrayList<>();
            
            addresses = ram.addProcess(4,15, vram, time);
            addresses = ram.addProcess(3,17, vram, time);
            addresses = ram.addProcess(2,15, vram, time);
            ram.print();
            // addresses = ram.addProcess(4,32, vram, time);
            addresses = ram.addProcess(6,32, vram, time);
            addresses = ram.addProcess(8,32, vram, time);
            ram.print();
            ram.printFIFO();
            addresses = ram.addProcess(512,64, vram, time);
            ram.printFIFO();
            print(addresses);
            addresses = ram.addProcess(35,1, vram, time);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }   
}