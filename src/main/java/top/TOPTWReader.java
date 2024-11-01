package top;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import es.ull.esit.utilities.ExpositoUtilities;

/**
 * @class TOPTWReader
 * @brief Clase para leer archivos de entrada y cargar los datos de problemas de TOPTW (Team Orienteering Problem with Time Windows).
 */
public class TOPTWReader {

    /**
     * Lee el archivo de entrada y crea una instancia del problema TOPTW.
     *
     * @param filePath La ruta del archivo que contiene los datos del problema.
     * @return Una instancia de TOPTW con los datos cargados desde el archivo.
     */
    public static TOPTW readProblem(String filePath) {
        TOPTW problem = null;

        // Uso de try-with-resources para asegurarse de que el BufferedReader se cierre automáticamente.
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            // Leer la primera línea del archivo y simplificarla
            String line = reader.readLine();
            line = ExpositoUtilities.simplifyString(line);

            // Dividir la línea en partes y crear una instancia de TOPTW usando valores de partes
            String[] parts = line.split(" ");
            problem = new TOPTW(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]));

            // Leer y descartar la segunda línea
            line = reader.readLine();
            line = null; parts = null; // Limpiar referencias para liberar memoria

            // Leer los datos de cada POI (Punto de Interés) y asignarlos al objeto problem
            for (int i = 0; i < problem.getPOIs() + 1; i++) {
                line = reader.readLine(); // Leer la siguiente línea
                line = ExpositoUtilities.simplifyString(line); // Simplificar el contenido de la línea
                parts = line.split(" "); // Dividir la línea en partes para procesar

                // Asignar valores de coordenadas, tiempo de servicio y puntuación del POI
                problem.setX(i, Double.parseDouble(parts[1]));
                problem.setY(i, Double.parseDouble(parts[2]));
                problem.setServiceTime(i, Double.parseDouble(parts[3]));
                problem.setScore(i, Double.parseDouble(parts[4]));

                // Asignar tiempos de disponibilidad y límite del POI, con manejo especial para el primer POI
                if (i == 0) {
                    problem.setReadyTime(i, Double.parseDouble(parts[7]));
                    problem.setDueTime(i, Double.parseDouble(parts[8]));
                } else {
                    problem.setReadyTime(i, Double.parseDouble(parts[8]));
                    problem.setDueTime(i, Double.parseDouble(parts[9]));
                }

                // Limpiar referencias de la línea y las partes para liberar memoria
                line = null; parts = null;
            }

            // Calcular la matriz de distancias para el problema una vez cargados los datos
            problem.calculateDistanceMatrix();
        } catch (IOException e) {
            // Manejo de errores de E/S
            System.err.println(e);
            System.exit(0);
        }

        // Establecer el tiempo máximo por ruta utilizando el tiempo de vencimiento del primer POI
        problem.setMaxTimePerRoute(problem.getDueTime(0));

        return problem; // Retornar el problema con todos los datos cargados
    }
}
