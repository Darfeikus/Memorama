# Memoria

Objetivo: Ejercicio de programación para mostrar su comprensión de algunos de
los algoritmos de administración de memoria vistos en clase
Procedimiento: Desarrollar un programa en cualquier lenguaje con las siguientes
especificaciones:
1. El programa deberá simular un manejador de memoria virtual utilizando
paginación parecido al visto en clase, y medir su rendimiento. Se utilizarán
las estrategias de reemplazo FIFO y LRU, para que comparen el rendimiento
de ambas estrategias. Asuman que la computadora tiene un solo
procesador; una memoria real de 2048 bytes, y que las páginas son de 16
bytes.
2. Para la simulación, su programa contará con las siguientes estructuras de
datos:
a. Un área de memoria que simulará la parte de la memoria real de una
computadora reservada a marcos de página. Por ejemplo, puede ser un
vector llamado M de 2048 elementos, cada uno de 1 byte. La primera
posición del arreglo, y por tanto del primer marco de página, representa
la dirección 0 de la memoria real. La última dirección sería la 2047.
Inicialmente la memoria está vacía. Otro tipo de estructura de datos para
simular los marcos de página es posible.
b. Una segunda área de memoria contigua, que simulará el área de disco
reservada para el swapping de páginas. Podría ser por ejemplo un vector
llamado S de 4096 elementos, cada uno de 1 byte.
c. Otras estructuras de datos que encuentren Uds. convenientes o
indispensables para lograr la simulación y que se encuentran en áreas de
memoria distintas de las áreas M y S.
3. INPUT: Simulará las peticiones que hace el sistema operativo a su propio
algoritmo manejador de memoria virtual, tales como asignar o liberar
espacios de memoria o acceder direcciones virtuales para obtener la
dirección en memoria real. 
