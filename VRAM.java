public class VRAM {
    private final int PAGE_SIZE ; //Tamaño de la página
    private final int MEMORY_LENGTH; //Se declara la longitud de la memoria virtual
    private final int NUMBER_OF_PAGES; //Es el número de páginas totales
    
    private int[] vram; //Se crea el arreglo
    private int freePages;

    private VRAM(int pageSize, int memorySize) {
        this.PAGE_SIZE = pageSize;
        this.MEMORY_LENGTH = memorySize;
        this.NUMBER_OF_PAGES = (int) MEMORY_LENGTH / PAGE_SIZE;
        this.freePages = NUMBER_OF_PAGES;
        initArray();
    }

    //Inicializa todo el array en -1, siendo este número equivalente a vacio
    private void initArray() { 
        vram = new int [MEMORY_LENGTH];
        for (int i = 0; i < MEMORY_LENGTH/4; i++)
            vram[i] = -1;
        for (int i = MEMORY_LENGTH/4; i < (MEMORY_LENGTH*2)/4; i++)
            vram[i] = 2;
        for (int i = (MEMORY_LENGTH*2)/4; i < (MEMORY_LENGTH*3)/4; i++)
            vram[i] = -1;
        for (int i = (MEMORY_LENGTH*3)/4; i < (MEMORY_LENGTH*4)/4; i++)
            vram[i] = 2;
    }
    
    //Se desaloja el proceso de la memoria, se itera en cada pagina y si ahi esta el proceso, se limpia la página
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

    //Se agrega un proceso a la memoria , primero se checa si hay suficientes espacios, si hay entonces se guarda en memoria
    public boolean addProcess(int processId, int size) { 
        try {
            int numberOfPages = (int)(Math.ceil((double)size/PAGE_SIZE));
            if (numberOfPages > freePages) {
                throw new MoreThanFreeMemoryException("Se pide mas memoria de la que hay disponible");
            }
            storeInMemory(processId, numberOfPages, size);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    //Se itera sobre el arreglo para ver los espacios disponibles donde agregar las páginas
    private void storeInMemory(int processId, int numberOfPages, int size) {
        for (int i = 0; i < MEMORY_LENGTH && numberOfPages != 0; i+=PAGE_SIZE) {
            System.out.println("Me itere" + numberOfPages);
            if (vram[i] == -1) {
                fillingMemory(processId, size, numberOfPages, i);
                freePages--;
                numberOfPages--;
            }
        }
    }

    //Se llena la memoria, si el espacio que se desea agregar no es multiplo de PAGE_SIZE, entonces en la ultima iteración se guarda el espacio justo
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

    //Se envia la cantidad total de datos que hay que mover a la RAM y se desalojan las páginas que ya no se usarán
    public int movePageToRam(int processId) {
        int sizeOfProcess = countSpace(processId);
        this.removeProcessFromMemory(processId);
        return sizeOfProcess;
        
    }

    //Cuenta el tamaño de un proceso
    private int countSpace(int processId) {
        int sizeOfProcess = 0;
        for (int i = 0; i < MEMORY_LENGTH; i++) 
            if(vram[i] == processId)
                sizeOfProcess++;
        return sizeOfProcess;
    }

    //Metodos de prueba (eliminar al final)
/*     public static void main(String[] args) {
        VRAM v = new VRAM(4,16);
        System.out.println(v.PAGE_SIZE + " " + v.MEMORY_LENGTH + " " + v.NUMBER_OF_PAGES);
        printArray(v);
        v.addProcess(3, 6);
        printArray(v);
        v.removeProcessFromMemory(2);
        printArray(v);
        System.out.println(v.movePageToRam(3));
        printArray(v);
        
    }

    private static void printArray(VRAM v) {
        for(int i = 0; i < v.MEMORY_LENGTH; i++){
            System.out.print(" " + v.vram[i]);
        }
        System.out.println("\n");
    }
 */
}