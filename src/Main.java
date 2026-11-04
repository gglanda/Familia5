
/** ********************************************************************
 * APLICACIÓN: "Familia5ExecutorCallable"
 **********************************************************************
 * PROGRAMACIÓN DE SERVICIOS Y PROCESOS 2 DAM
  **********************************************************************
 * @author  GERARDO LUIS GARCIA LANDA
 * @version 1.0 - Versión inicial.
 * @since   02NOV2026
 **********************************************************************
* Módulo: 0490 – PROGRAMACIÓN DE SERVICIOS Y PROCESOS Curso: 2DAM
* Departamento: INFORMÁTICA Evaluación: 1
* TRABAJO 1 – FAMILA5 EXECUTOR CALLABLE Fecha entrega: viernes 06/NOV/2025 - 23:00 horas
* El objetivo de esta actividad es practicar la programación paralela con el framework Executor de Java.
* Concretamente, en esta ocasión vamos a practicar el uso de la clase ExecutorService vista en el
* ejemplo de clase “MultiplicaLista”.
* Para ello, se pide la realización de un programa en Java capaz de encontrar todos los números
* múltiplos de 5, cuya suma de dígitos sea además múltiplo de 5, y que sean más pequeños que 100.
* La aplicación deberá observar las siguientes directrices de diseño:
* 1. Se deberá crear dos listas de tareas, una para verificar qué números son múltiplos de 5, y otra
* para determinar qué números están compuestos de dígitos cuya suma es múltiplo de 5.
* 2. Las 200 tareas deberán lanzarse en paralelo.
* 3. Será el main() el que mostrará por pantalla los números que cumplan simultáneamente ambas condiciones.
* 4. La clase de las tareas deberá implementar Callable.
 **********************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Familia5
{
    // Tope superior del rango de búsqueda (números < MAXIMO)
    private static final int MAXIMO = 100;

    /**
     * Metodo main que mostrará por pantalla los números que cumplan simultáneamente ambas condiciones.
     * Creamos las dos listas de tareas, las lanza en paralelo y muestra los números que cumplen ambas condiciones.
     */

    static void main(String[] p_Args)

    {
        // Declaración de variables
        ExecutorService l_Ejecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Integer>> l_ListaMultiplos = new ArrayList<Callable<Integer>>();
        List<Callable<Integer>> l_ListaSumaDigitos = new ArrayList<Callable<Integer>>();
        List<Future<Integer>> l_FuturosMultiplos = null;
        List<Future<Integer>> l_FuturosSumaDigitos = null;
        List<Integer> l_ResultadosMultiplos = new ArrayList<Integer>();
        List<Integer> l_ResultadosSumaDigitos = new ArrayList<Integer>();
        List<Integer> l_ResultadosFinales = new ArrayList<Integer>();

        int l_Numero =0;
        Integer l_Valor = null;
        boolean l_Existe = false;

        // 1. Creación de las dos listas de tareas (100 + 100 = 200)

        // Lista 1: verificar múltiplos de 5
        l_Numero = 0;
        while (l_Numero < MAXIMO)   {
            l_ListaMultiplos.add(new TareaMultiploDe5(l_Numero));
            l_Numero = l_Numero + 1;
        }

        // Lista 2: verificar suma de dígitos múltiplo de 5
        l_Numero = 0;
        while (l_Numero < MAXIMO) {
            l_ListaSumaDigitos.add(new TareaSumaDigitosMultiploDe5(l_Numero));
            l_Numero = l_Numero + 1;
        }

        // 2. Lanzamiento de las 200 tareas en paralelo
        try {
            l_FuturosMultiplos = l_Ejecutor.invokeAll(l_ListaMultiplos);
            l_FuturosSumaDigitos = l_Ejecutor.invokeAll(l_ListaSumaDigitos);

        }  catch (InterruptedException p_Excepcion) {
            // En caso de interrupción se muestra el mensaje y se continúa
            System.out.println("Error al lanzar las tareas: " + p_Excepcion.getMessage());
        }

        // Recogida de resultados de la primera lista (múltiplos de 5)
        if (l_FuturosMultiplos != null) {
            l_Numero = 0;
            while (l_Numero < l_FuturosMultiplos.size()){
                try {
                    l_Valor = l_FuturosMultiplos.get(l_Numero).get();
                    if (l_Valor != null) {
                        l_ResultadosMultiplos.add(l_Valor);
                    }
                }
                catch (InterruptedException p_Excepcion) {
                    System.out.println("Error al obtener resultado múltiplo: " + p_Excepcion.getMessage());
                }
                catch (ExecutionException p_Excepcion){
                    System.out.println("Error de ejecución múltiplo: " + p_Excepcion.getMessage());
                }
                l_Numero++;
            }
        }
        // Recogida de resultados de la segunda lista (suma de dígitos)

        if (l_FuturosSumaDigitos != null) {
            l_Numero = 0;
            while (l_Numero < l_FuturosSumaDigitos.size()) {
                try {
                    l_Valor = l_FuturosSumaDigitos.get(l_Numero).get();
                    if (l_Valor != null) {

                        l_ResultadosSumaDigitos.add(l_Valor);
                    }
                }
                catch (InterruptedException p_Excepcion){
                    System.out.println("Error al obtener resultado suma: " + p_Excepcion.getMessage());
                }
                catch (ExecutionException p_Excepcion) {
                    System.out.println("Error de ejecución suma: " + p_Excepcion.getMessage());
                }
                l_Numero ++;
            }
        }

        // 3. El main determina los números que cumplen AMBAS condiciones

        // (intersección de las dos listas de resultados)
        l_Numero = 0;
        while (l_Numero < l_ResultadosMultiplos.size()) {
            l_Valor = l_ResultadosMultiplos.get(l_Numero);
            l_Existe = false;

            // Comprobación de existencia en la segunda lista
            int l_Indice = 0;
            while (l_Indice < l_ResultadosSumaDigitos.size()) {
                if (l_ResultadosSumaDigitos.get(l_Indice).equals(l_Valor)) {
                    l_Existe = true;
                    break;
                }
                l_Indice = l_Indice + 1;
            }
            if (l_Existe) {
                l_ResultadosFinales.add(l_Valor);
            }
            l_Numero = l_Numero + 1;
        }

        // Mostrar resultados por pantalla
        System.out.println("Números menores que " + MAXIMO
                + " que son múltiplos de 5 y cuya suma de dígitos también es múltiplo de 5:");
        System.out.println("---------------------------------------------------------------");
        if (l_ResultadosFinales.isEmpty()) {
            System.out.println("No se han encontrado números que cumplan las condiciones.");
        }  else  {
            l_Numero = 0;
            while (l_Numero < l_ResultadosFinales.size()){
                System.out.println(l_ResultadosFinales.get(l_Numero));
                l_Numero = l_Numero + 1;
            }
        }
        // Cierre ordenado del pool de hilos
        l_Ejecutor.shutdown();
    }
}

/**
 * Tarea Callable TareaMultiploDe5 que comprueba si un número es múltiplo de 5.
 * Devuelve el número si lo es, o null en caso contrario.
 */

class TareaMultiploDe5 implements Callable<Integer>
{
    // Atributo de la clase
    private final int a_Numero;
    /**
     * Constructor con parametro p_Numero contiene el número a comprobar
     */

    public TareaMultiploDe5(int p_Numero)
    {
        this.a_Numero = p_Numero;
    }
    /**
     * Usando el metodo call comprueba si el número es múltiplo de 5.
     * Devuelve el número si es múltiplo de 5 o null en caso contrario
     */

    @Override

    public Integer call()
    {
        // Declaración de variables al principio
        Integer l_Resultado;
        l_Resultado = null;

        // Comprobación
        if (this.a_Numero % 5 == 0) {
            l_Resultado = Integer.valueOf(this.a_Numero);
        }

        // Único punto de salida
        return l_Resultado;
    }
}


/**
 * Tarea Callable TareaSumaDigitosMultiploDe5 que comprueba si la suma de los dígitos de un número es múltiplo de 5.
 * Devuelve el número si lo es, o null en caso contrario.
 */

class TareaSumaDigitosMultiploDe5 implements Callable<Integer>
{
    // Atributo de la clase
    private final int a_Numero;
    /**
     * Usamos el parámerto p_Numero número a comprobar
     */
    public TareaSumaDigitosMultiploDe5(int p_Numero)
    {
        this.a_Numero = p_Numero;
    }

    /**
     * Usamos el metodo call y calculamos la suma de dígitos y comprueba si es múltiplo.
     * Devuelve el número si la suma de dígitos es múltiplo o null en caso contrario
     */
    @Override
    public Integer call()
    {
        // Declaración de variables al principio
        Integer l_Resultado= null;
        int l_Suma = 0;
        int l_Auxiliar= this.a_Numero;


        // Cálculo de la suma de dígitos
        if (l_Auxiliar == 0)  {
            l_Suma = 0;
        } else {
            while (l_Auxiliar > 0)
            {
                l_Suma = l_Suma + (l_Auxiliar % 10);
                l_Auxiliar = l_Auxiliar / 10;
            }
        }

        // Comprobación
        if (l_Suma % 5 == 0) {
            l_Resultado = Integer.valueOf(this.a_Numero);
        }
        // Único punto de salida
        return l_Resultado;
    }
}

