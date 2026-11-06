import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
/******************************************************************************************************
 * APLICACIÓN: "Familia5"
 ******************************************************************************************************
 * PROGRAMACIÓN DE SERVICIOS Y PROCESOS 2 DAM
 ******************************************************************************************************
 * @author  GERARDO LUIS GARCIA LANDA
 * @version 1.1 - Las 200 tareas se lanzan en una única llamada a invokeAll.
 * @since   02NOV2026
 ******************************************************************************************************
 * Módulo: 0490 – PROGRAMACIÓN DE SERVICIOS Y PROCESOS Curso: 2ºDAM
 * Departamento: INFORMÁTICA Evaluación: 1
 * TRABAJO 1 – FAMILIA5 EXECUTOR CALLABLE Fecha entrega: viernes 06/NOV/2026 - 23:00 horas
 * El objetivo de esta actividad es practicar la programación paralela con el framework Executor de Java.
 * Concretamente, en esta ocasión vamos a practicar el uso de la clase ExecutorService vista en el
 * ejemplo de clase "MultiplicaLista".
 * Para ello, se pide la realización de un programa en Java capaz de encontrar todos los números
 * múltiplos de 5, cuya suma de dígitos sea además múltiplo de 5, y que sean más pequeños que 100.
 * La aplicación deberá observar las siguientes directrices de diseño:
 * 1. Se deberá crear dos listas de tareas, una para verificar qué números son múltiplos de 5, y otra
 *    para determinar qué números están compuestos de dígitos cuya suma es múltiplo de 5.
 * 2. Las 200 tareas deberán lanzarse en paralelo.
 * 3. Será el main() el que mostrará por pantalla los números que cumplan simultáneamente ambas
 *    condiciones.
 * 4. La clase de las tareas deberá implementar Callable.
 ******************************************************************************************************/

class Familia5
{
    // Número para las comprobaciones en este caso el 5
    public static final int NUMERO = 5;
    // Tope superior en este caso 100
    public static final int MAXIMO = 100;
    //numero de Hilos maximo pactado
    public static final int NUMERO_HILOS = 3;

    /**
     * Tarea Callable que comprueba si un número es múltiplo de NUMERO.
     * Devuelve el número si lo es o null si no lo es.
     */
    class TareaMultiplo implements Callable<Integer>
    {
        private final int a_Numero;

        //Crea la tarea con. el parámetro p_Numero que es número a comprobar
        public TareaMultiplo(int p_Numero)
        {
            this.a_Numero = p_Numero;
        }

        // Comprueba si el número es múltiplo de NUMERO.
        @Override
        public Integer call()
        {
            Integer l_Resultado = null;

            // Comprobamos si es múltiplo
            if (this.a_Numero % NUMERO == 0) {
                l_Resultado = this.a_Numero;
            }

            return l_Resultado;
        } //call
    } //TareaMultiplo

    /**
     * Tarea Callable que comprueba si la suma de los dígitos de un número es múltiplo de NUMERO5.
     * Devuelve el número si lo es, o null si no lo es.
     */
    class TareaSumaDigitos implements Callable<Integer>
    {
        private final int a_Numero;

        // Crea la tarea con el número a comprobar, el parámetro p_Numero número a comprobar
        public TareaSumaDigitos(int p_Numero)
        {
            this.a_Numero = p_Numero;
        }

        // Calcula la suma de los dígitos del número y comprueba si es múltiplo de NUMERO.

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

            // Comprobamos si la suma es múltiplo
            if (l_Suma % NUMERO == 0) {
                l_Resultado = this.a_Numero;
            }

            return l_Resultado;
        }
    }

    /**
     * Método main que muestra por pantalla los números que cumplen a la vez ambas condiciones.
     * Crea las dos listas de tareas, las lanza todas en paralelo y calcula la intersección de resultados.
     */
    public static void main(String[] p_Args)
    {
        // Declaración de variables
        ThreadPoolExecutor l_Ejecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMERO_HILOS);
        List<Callable<Integer>> l_ListaMultiplos = new ArrayList<>();   //Lista de múltiplos
        List<Callable<Integer>> l_ListaSumaDigitos = new ArrayList<>(); //Lista de sumas de dígitos
        List<Callable<Integer>> l_ListaTodas = new ArrayList<>();       //Lista de todas las tareas
        List<Future<Integer>> l_FuturosTodos = null;                    //Lista de todos los futuros múltiplos y sumas
        List<Future<Integer>> l_FuturosMultiplos = null;                //Lista de futuros múltiplos
        List<Future<Integer>> l_FuturosSumaDigitos = null;              //Lista de futuros de suma de dígitos
        List<Integer> l_ResultadosMultiplos = new ArrayList<>();        //Lista de resultados de múltiplos
        List<Integer> l_ResultadosSumaDigitos = new ArrayList<>();      //Lista de resultados de sumas de dígitos
        List<Integer> l_ResultadosFinales = new ArrayList<>();          //Lista de resultados finales

        Familia5 l_Aplicacion = new Familia5();

       // int l_Numero = 0;
        Integer l_Valor = null;
        TareaMultiplo l_TareaMultiplo = null;
        TareaSumaDigitos l_TareaSumaDigitos = null;

        // Creación de las dos listas de MAXIMO tareas

        // Lista 1: verificar múltiplos de NUMERO
        for (int l_Numero = 0;l_Numero < MAXIMO; l_Numero++){

            // Lista 1: verificar múltiplos de NUMERO
            //L_Tarea= TareaMultiplo(l_Numero);
            l_TareaMultiplo = l_Aplicacion.new TareaMultiplo(l_Numero);
            l_ListaMultiplos.add(Executors.callable(l_TareaMultiplo));

            // Lista 2: verificar suma de dígitos múltiplo de NUMERO
           //l_ListaSumaDigitos.add(new TareaSumaDigitos(l_Numero));
            l_TareaSumaDigitos = l_Aplicacion.new TareaSumaDigitos(l_Numero);
            l_ListaSumaDigitos.add(Executors.callable(l_TareaSumaDigitos));
        }
/*
        int l_Numero = 0;
        while (l_Numero < MAXIMO) {
            l_ListaMultiplos.add(new TareaMultiplo(l_Numero));
            l_Numero++;
        }

        // Lista 2: verificar suma de dígitos múltiplo de NUMERO
        l_Numero = 0;
        while (l_Numero < MAXIMO) {
            l_ListaSumaDigitos.add(new TareaSumaDigitos(l_Numero));
            l_Numero++;
        }

        // Lanzamiento de la lista de  2X MAXIMO tareas en paralelo
        l_ListaTodas.addAll(l_ListaMultiplos);
        l_ListaTodas.addAll(l_ListaSumaDigitos);
*/
        try {
            l_FuturosTodos = l_Ejecutor.invokeAll(l_ListaTodas);

            // Separamos los futuros, primero los de la lista 1 y después los de la lista 2
            l_FuturosMultiplos = l_FuturosTodos.subList(0, l_ListaMultiplos.size());
            l_FuturosSumaDigitos = l_FuturosTodos.subList(l_ListaMultiplos.size(), l_FuturosTodos.size());

        } catch (InterruptedException p_Excepcion) {
            // Se restaura el estado de interrupción del hilo y se muestra el mensaje
            Thread.currentThread().interrupt();
            System.out.println("Error al lanzar las tareas: " + p_Excepcion.getMessage());

        } finally {

            // Cerramos el pool de hilos
            l_Ejecutor.shutdown();
        }

        // Recogida de resultados de la primera lista de múltiplos de NUMERO
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

        // Recogida de resultados de la lista que suma los dígitos.
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

        /**
         *  El main comprueba que los números cumplen las dos condiciones en la
         *  intersección de las dos listas de resultados.
         */
        l_Numero = 0;
        while (l_Numero < l_ResultadosMultiplos.size()) {
            l_Valor = l_ResultadosMultiplos.get(l_Numero);
            if (l_ResultadosSumaDigitos.contains(l_Valor)) {
                l_ResultadosFinales.add(l_Valor);
            }
            l_Numero++;
        }

        // Mostrar resultados por pantalla los números que cumplan simultáneamente ambas condiciones.
        System.out.println("Números menores que " + MAXIMO
                + " que son múltiplos de " + NUMERO + " \ny la suma de sus dígitos también es múltiplo de " + NUMERO + ":");
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
