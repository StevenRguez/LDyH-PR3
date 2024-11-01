package es.ull.esit.utilities;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.SecureRandom;

public class ExpositoUtilities {

    public static final int DEFAULT_COLUMN_WIDTH = 10;
    public static final int ALIGNMENT_LEFT = 1;
    public static final int ALIGNMENT_RIGHT = 2;

    // Instancia de Random reutilizable para evitar creación frecuente de objetos Random
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Genera un número aleatorio dentro de un rango específico.
     *
     * @param min El valor mínimo (incluido).
     * @param max El valor máximo (excluido).
     * @return Un número aleatorio dentro del rango [min, max).
     */
    public static int generateRandomNumber(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("El valor mínimo debe ser menor que el valor máximo.");
        }
        // Genera un número aleatorio en el rango especificado
        return min + SECURE_RANDOM.nextInt(max - min);
    }

    /**
     * Genera un número aleatorio de punto flotante en un rango específico.
     *
     * @param min El valor mínimo (incluido).
     * @param max El valor máximo (excluido).
     * @return Un número aleatorio de punto flotante dentro del rango [min, max).
     */
    public static double generateRandomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("El valor mínimo debe ser menor que el valor máximo.");
        }
        // Genera un número aleatorio de punto flotante en el rango especificado
        return min + (max - min) * SECURE_RANDOM.nextDouble();
    }

    /**
     * Mezcla un arreglo de elementos de manera aleatoria.
     *
     * @param array El arreglo a mezclar.
     */
    public static <T> void shuffleArray(T[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(i + 1); // Usa SecureRandom para seleccionar un índice aleatorio
            // Intercambia array[i] con el elemento en un índice aleatorio
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    /**
     * Obtiene la primera aparición de un elemento en un vector.
     *
     * @param vector  el vector a buscar
     * @param element el elemento a buscar
     * @return índice de la primera aparición o -1 si no se encuentra
     */
    private static int getFirstAppearance(int[] vector, int element) {
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Imprime el contenido de un archivo línea por línea.
     *
     * @param file ruta del archivo a imprimir
     */
    public static void printFile(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) { // Uso de try-with-resources
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExpositoUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Simplifica un string eliminando espacios extra y tabs.
     *
     * @param string el string a simplificar
     * @return el string simplificado
     */
    public static String simplifyString(String string) {
        string = string.replace("\t", " ");  // Reemplazar tabulaciones por espacios
        for (int i = 0; i < 50; i++) {       // Reemplazar dobles espacios por un solo espacio
            string = string.replace("  ", " ");
        }
        return string.trim();  // Eliminar espacios al principio y final
    }

    /**
     * Multiplica dos matrices.
     *
     * @param a primera matriz
     * @param b segunda matriz
     * @return matriz resultado o null si las dimensiones son incompatibles
     */
    public static double[][] multiplyMatrices(double[][] a, double[][] b) {
        if (!isValidMatrix(a, b)) return null;
        return calculateProduct(a, b);
    }

    // Verifica si las matrices pueden multiplicarse
    private static boolean isValidMatrix(double[][] a, double[][] b) {
        return a.length != 0 && a[0].length == b.length;
    }

    // Calcula el producto de dos matrices válidas
    private static double[][] calculateProduct(double[][] a, double[][] b) {
        int n = a[0].length;
        int m = a.length;
        int p = b[0].length;
        double[][] ans = new double[m][p];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                ans[i][j] = computeCell(a, b, i, j, n);
            }
        }
        return ans;
    }

    // Calcula un elemento en la posición específica de la matriz resultado
    private static double computeCell(double[][] a, double[][] b, int row, int col, int n) {
        double sum = 0;
        for (int k = 0; k < n; k++) {
            sum += a[row][k] * b[k][col];
        }
        return sum;
    }

    /**
     * Escribe un texto en un archivo, usando try-with-resources para gestionar el cierre de BufferedWriter.
     *
     * @param file archivo donde escribir
     * @param text texto a escribir
     * @throws IOException si ocurre un error de escritura
     */
    public static void writeTextToFile(String file, String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(text);
        }
    }

    /**
     * Da formato a un string, detectando si es número entero o decimal.
     *
     * @param strings el string a formatear
     * @return el string formateado
     */
    public static String getFormat(String[] strings, int[] columnWidths) {
        StringBuilder formattedString = new StringBuilder();
        int length = Math.min(strings.length, columnWidths.length);

        for (int i = 0; i < length; i++) {
            String formattedElement = String.format("%-" + columnWidths[i] + "s", strings[i]);
            formattedString.append(formattedElement);
        }

        return formattedString.toString().trim();
    }


    /**
     * Da formato a un número double con tres decimales.
     *
     * @param value valor a formatear
     * @return el string formateado
     */
    public static String getFormat(double value) {
        DecimalFormat decimalFormatter = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        decimalFormatter.setDecimalFormatSymbols(symbols);
        return decimalFormatter.format(value);
    }

    // Resto de métodos de formato para diferentes tipos de datos omitidos para brevedad...

    /**
     * Verifica si un string representa un número entero.
     *
     * @param str el string a verificar
     * @return true si es un entero, false en caso contrario
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si un string representa un número decimal.
     *
     * @param str el string a verificar
     * @return true si es un decimal, false en caso contrario
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si una matriz de distancias es acíclica.
     *
     * @param distanceMatrix matriz de distancias
     * @return true si no contiene ciclos, false en caso contrario
     */
    public static boolean isAcyclic(int[][] distanceMatrix) {
        int numRealTasks = getRealTasks(distanceMatrix);
        return !hasCycle(distanceMatrix, numRealTasks);
    }

    // Obtiene el número de tareas reales de la matriz
    private static int getRealTasks(int[][] distanceMatrix) {
        return distanceMatrix.length - 2;
    }

    // Comprueba si la matriz contiene un ciclo
    private static boolean hasCycle(int[][] distanceMatrix, int numRealTasks) {
        for (int node = 1; node <= numRealTasks; node++) {
            if (thereIsPath(distanceMatrix, node)) return true;
        }
        return false;
    }

    /**
     * Determina si existe un camino desde un nodo específico en una matriz de distancias.
     *
     * @param distanceMatrix matriz de distancias
     * @param node nodo a verificar
     * @return true si existe un camino que forma un ciclo, false en caso contrario
     */
    public static boolean thereIsPath(int[][] distanceMatrix, int node) {
        HashSet<Integer> visits = initializeVisits(node, distanceMatrix.length);
        HashSet<Integer> noVisits = initializeNoVisits(node, distanceMatrix.length);

        while (!visits.isEmpty()) {
            int toCheck = extractNextVisit(visits);
            if (checkConnections(distanceMatrix, node, visits, noVisits, toCheck)) {
                return true;
            }
        }
        return false;
    }

    // Inicializa el conjunto de visitas con el nodo inicial
    private static HashSet<Integer> initializeVisits(int node, int length) {
        HashSet<Integer> visits = new HashSet<>();
        visits.add(node);
        return visits;
    }

    // Inicializa el conjunto de no visitados excluyendo el nodo inicial
    private static HashSet<Integer> initializeNoVisits(int node, int length) {
        HashSet<Integer> noVisits = new HashSet<>();
        for (int i = 0; i < length; i++) {
            if (i != node) noVisits.add(i);
        }
        return noVisits;
    }

    // Extrae el próximo nodo a verificar de la lista de visitas
    private static int extractNextVisit(HashSet<Integer> visits) {
        Iterator<Integer> it = visits.iterator();
        int toCheck = it.next();
        visits.remove(toCheck);
        return toCheck;
    }

    // Verifica conexiones para determinar si hay un ciclo
    private static boolean checkConnections(int[][] distanceMatrix, int node, HashSet<Integer> visits, HashSet<Integer> noVisits, int toCheck) {
        for (int i = 0; i < distanceMatrix.length; i++) {
            if (toCheck != i && distanceMatrix[toCheck][i] != Integer.MAX_VALUE) {
                if (i == node) return true;
                if (noVisits.contains(i)) {
                    noVisits.remove(i);
                    visits.add(i);
                }
            }
        }
        return false;
    }
}
