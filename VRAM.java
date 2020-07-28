public class VRAM {
    private static final int PAGE_SIZE = 16; //Tamaño de la página
    private static final int MEMORY_LENGTH = 4096; //Se declara la longitud de la memoria virtual
    private static final int NUMBER_OF_PAGES = (int) MEMORY_LENGTH / PAGE_SIZE; //Es el número de páginas totales
    
    private int[] vram = new int [MEMORY_LENGTH]; //Se crea el arreglo
    private int freePages;

    private VRAM() {
        initArray();
        this.freePages = NUMBER_OF_PAGES;
    }

    //Inicializa todo el array en -1, siendo este número equivalente a vacio
    private void initArray() { 
        for (int i = 0; i < MEMORY_LENGTH; i++)
            vram[i] = -1;
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
            storeInMemory(processId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    //Se itera sobre el arreglo para ver los espacios disponibles donde agregar las páginas
    private void storeInMemory(int id) {
        for (int i = 0; i < MEMORY_LENGTH; i+=PAGE_SIZE) {
            if (vram[i] == -1) {
                for (int j = i; j < i+PAGE_SIZE; j++)
                    vram[j] = id;
                freePages--;
            }
        }
    }

    public boolean movePageToRam(int processId) {
        int numberOfProcessPagesInMemory = 0;
        int sizeOfProcess = 0;
        for (int i = 0; i < MEMORY_LENGTH; i+=PAGE_SIZE) 
            if(vram[i] == processId)
                numberOfProcessPagesInMemory++;
        sizeOfProcess = numberOfProcessPagesInMemory*PAGE_SIZE;
        return this.removeProcessFromMemory(processId);
        
    }

    public static void main(String[] args) {
        VRAM v = new VRAM();
        
    }


}