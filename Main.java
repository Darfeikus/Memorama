import java.util.List;

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
            
            ram.addProcess(4, 17, vram, time);
            ram.addProcess(5, 32, vram, time);
            ram.addProcess(1, 15, vram, time); //Deallocs
            ram.addProcess(2, 16, vram, time); //Deallocs
            ram.addProcess(3, 32, vram, time); //Deallocs
            ram.addProcess(6, 64, vram, time); //Fills
            // ram.addProcess(7, 1, vram, time); //error
            ram.print();
            vram.print();
            ram.printProcessesAddressList();

            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }   
}