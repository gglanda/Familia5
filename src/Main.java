import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
/******************************************************************************************************
 * APLICACIÓN: "Familia5"
 ******************************************************************************************************
 * PROGRAMACIÓN DE SERVICIOS Y PROCESOS 2 DAM
 ******************************************************************************************************
 * @author  GERARDO LUIS GARCIA LANDA
 * @version 1.2 - Las 200 tareas se lanzan en una única llamada a invokeAll.
 * @since   02NOV2026
 ******************************************************************************************************/

class Familia5
{
    // Número para las comprobaciones en este caso el 5
    public static final int NUMERO = 5;
    // Tope superior en este caso 100
    public static final int MAXIMO = 100;
    // Número de hilos máximo pactado
    public static final int NUMERO_HILOS = 3;

    /**
     * Tarea Callable que comprueba si un número es múltiplo de NUMERO.
     * Devuelve el número si lo es o null si no lo es.
     */
    static class TareaMultiplo implements Callable<Integer>
    {
        private final int a_Numero;

        // Crea la tarea con el parámetro p_Numero que es el número a comprobar
        public TareaMultiplo(int p_Numero)
        {
            this.a_Numero = p_Numero;
        }

        @Override
        public Integer call()
        {
            Integer l_Resultado = null;

            if (this.a_Numero % NUMERO == 0) {
                l_Resultado = this.a_Numero;
            }

            return l_Resultado;
        }
    }

    /**
     * Tarea Callable que comprueba si la suma de los dígitos de un número es múltiplo de NUMERO.
     * Devuelve el número si lo es, o null si no lo es.
     */
    static class TareaSumaDigitos implements Callable<Integer>
    {
        private final int a_Numero;

        public TareaSumaDigitos(int p_Numero)
        {
            this.a_Numero = p_Numero;
        }

        @Override
        public Integer call()
        {
            Integer l_Resultado = null;
            int l_Suma = 0;
            int l_Auxiliar = this.a_Numero;

            // Cálculo de la suma de dígitos
            while (l_Auxiliar > 0) {
                l_Suma = l_Suma + (l_Auxiliar % 10);
                l_Auxiliar = l_Auxiliar / 10;
            }

            if (l_Suma % NUMERO == 0) {
                l_Resultado = this.a_Numero;
            }

            return l_Resultado;
        }
    }

    /**
     * Crea las dos listas de tareas, las lanza todas en paralelo con un único invokeAll
     * y muestra la intersección de resultados.
     */
    public static void main(String[] p_Args)
    {
        // Declaración de variables
        ExecutorService l_Ejecutor = Executors.newFixedThreadPool(NUMERO_HILOS);
        List<Callable<Integer>> l_ListaMultiplos = new ArrayList<>();
        List<Callable<Integer>> l_ListaSumaDigitos = new ArrayList<>();
        List<Callable<Integer>> l_ListaTodas = new ArrayList<>();
        List<Future<Integer>> l_FuturosTodos = null;
        List<Future<Integer>> l_FuturosMultiplos = null;
        List<Future<Integer>> l_FuturosSumaDigitos = null;
        List<Integer> l_ResultadosMultiplos = new ArrayList<>();
        List<Integer> l_ResultadosSumaDigitos = new ArrayList<>();
        List<Integer> l_ResultadosFinales = new ArrayList<>();

        int l_Numero = 0;
        Integer l_Valor = null;

        // Creación de las dos listas de MAXIMO tareas
        for (l_Numero = 0; l_Numero < MAXIMO; l_Numero++) {
            // Lista 1: verificar múltiplos de NUMERO
            l_ListaMultiplos.add(new TareaMultiplo(l_Numero));
            // Lista 2: verificar suma de dígitos múltiplo de NUMERO
            l_ListaSumaDigitos.add(new TareaSumaDigitos(l_Numero));
        }

        // Unimos las dos listas: 2 x MAXIMO = 200 tareas
        l_ListaTodas.addAll(l_ListaMultiplos);
        l_ListaTodas.addAll(l_ListaSumaDigitos);

        try {
            // Una única llamada lanza las 200 tareas
            l_FuturosTodos = l_Ejecutor.invokeAll(l_ListaTodas);

            // Separamos los futuros: primero los de la lista 1 y después los de la lista 2
            l_FuturosMultiplos = l_FuturosTodos.subList(0, l_ListaMultiplos.size());
            l_FuturosSumaDigitos = l_FuturosTodos.subList(l_ListaMultiplos.size(), l_FuturosTodos.size());

        } catch (InterruptedException p_Excepcion) {
            Thread.currentThread().interrupt();
            System.out.println("Error al lanzar las tareas: " + p_Excepcion.getMessage());

        } finally {
            // Cerramos el pool de hilos
            l_Ejecutor.shutdown();
        }

        // Recogida de resultados de la lista de múltiplos
        if (l_FuturosMultiplos != null) {
            l_Numero = 0;
            while (l_Numero < l_FuturosMultiplos.size()) {
                try {
                    l_Valor = l_FuturosMultiplos.get(l_Numero).get();
                    if (l_Valor != null) {
                        l_ResultadosMultiplos.add(l_Valor);
                    }
                } catch (InterruptedException p_Excepcion) {
                    Thread.currentThread().interrupt();
                    System.out.println("Error al obtener resultado múltiplo: " + p_Excepcion.getMessage());
                } catch (ExecutionException p_Excepcion) {
                    System.out.println("Error de ejecución múltiplo: " + p_Excepcion.getMessage());
                }
                l_Numero++;
            }
        }

        // Recogida de resultados de la lista de suma de dígitos
        if (l_FuturosSumaDigitos != null) {
            l_Numero = 0;
            while (l_Numero < l_FuturosSumaDigitos.size()) {
                try {
                    l_Valor = l_FuturosSumaDigitos.get(l_Numero).get();
                    if (l_Valor != null) {
                        l_ResultadosSumaDigitos.add(l_Valor);
                    }
                } catch (InterruptedException p_Excepcion) {
                    Thread.currentThread().interrupt();
                    System.out.println("Error al obtener resultado suma: " + p_Excepcion.getMessage());
                } catch (ExecutionException p_Excepcion) {
                    System.out.println("Error de ejecución suma: " + p_Excepcion.getMessage());
                }
                l_Numero++;
            }
        }

        // El main calcula la intersección de las dos listas de resultados
        l_Numero = 0;
        while (l_Numero < l_ResultadosMultiplos.size()) {
            l_Valor = l_ResultadosMultiplos.get(l_Numero);
            if (l_ResultadosSumaDigitos.contains(l_Valor)) {
                l_ResultadosFinales.add(l_Valor);
            }
            l_Numero++;
        }

        // Mostrar resultados
        System.out.println("Números menores que " + MAXIMO
                + " que son múltiplos de " + NUMERO
                + "\ny la suma de sus dígitos también es múltiplo de " + NUMERO + ":");
        System.out.println("---------------------------------------------------");
        if (l_ResultadosFinales.isEmpty()) {
            System.out.println("No se han encontrado números que cumplan las condiciones.");
        } else {
            l_Numero = 0;
            while (l_Numero < l_ResultadosFinales.size()) {
                System.out.println(l_ResultadosFinales.get(l_Numero));
                l_Numero++;
            }
        }
    }
}
