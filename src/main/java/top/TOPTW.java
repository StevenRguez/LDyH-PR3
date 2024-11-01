/**
 * @file TOPTW.java
 * @brief Clase para gestionar problemas de ruteo con tiempo y puntuación.
 */

package top;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.esit.utilities.ExpositoUtilities;

/**
 * @class TOPTW
 * @brief Esta clase representa el modelo de un problema de ruteo con restricciones de tiempo.
 */
public class TOPTW {
    private int nodes;               /**< Número de puntos de interés (POIs). */
    private double[] x;              /**< Coordenadas X de los puntos de interés. */
    private double[] y;              /**< Coordenadas Y de los puntos de interés. */
    private double[] score;          /**< Puntuación asociada a cada punto de interés. */
    private double[] readyTime;      /**< Tiempo de inicio de servicio en cada punto de interés. */
    private double[] dueTime;        /**< Tiempo límite de servicio en cada punto de interés. */
    private double[] serviceTime;    /**< Tiempo de servicio requerido en cada punto de interés. */
    private int vehicles;            /**< Número de vehículos disponibles. */
    private int depots;              /**< Número de depósitos disponibles. */
    private double maxTimePerRoute;  /**< Tiempo máximo permitido por ruta. */
    private double maxRoutes;        /**< Número máximo de rutas permitidas. */
    private double[][] distanceMatrix; /**< Matriz de distancias entre puntos de interés. */

    /**
     * @brief Constructor de la clase TOPTW.
     * @param nodes Número de puntos de interés.
     * @param routes Número de rutas (vehículos) disponibles.
     */
    public TOPTW(int nodes, int routes) {
        this.nodes = nodes;
        this.depots = 0;
        this.x = new double[this.nodes + 1];
        this.y = new double[this.nodes + 1];
        this.score = new double[this.nodes + 1];
        this.readyTime = new double[this.nodes + 1];
        this.dueTime = new double[this.nodes + 1];
        this.serviceTime = new double[this.nodes + 1];
        this.distanceMatrix = new double[this.nodes + 1][this.nodes + 1];
        for (int i = 0; i < this.nodes + 1; i++) {
            for (int j = 0; j < this.nodes + 1; j++) {
                this.distanceMatrix[i][j] = 0.0;
            }
        }
        this.maxRoutes = routes;
        this.vehicles = routes;
    }

    /**
     * @brief Verifica si un punto es un depósito.
     * @param a Índice del punto.
     * @return Verdadero si el punto es un depósito, falso en caso contrario.
     */
    public boolean isDepot(int a) {
        return a > this.nodes;
    }

    /**
     * @brief Calcula la distancia total de una ruta.
     * @param route Array de índices que representan la ruta.
     * @return Distancia total de la ruta.
     */
    public double getDistance(int[] route) {
        double distance = 0.0;
        for (int i = 0; i < route.length - 1; i++) {
            int node1 = route[i];
            int node2 = route[i + 1];
            distance += this.getDistance(node1, node2);
        }
        return distance;
    }

    /**
     * @brief Calcula la distancia total de una ruta representada como lista.
     * @param route Lista de índices que representan la ruta.
     * @return Distancia total de la ruta.
     */
    public double getDistance(ArrayList<Integer> route) {
        double distance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            int node1 = route.get(i);
            int node2 = route.get(i + 1);
            distance += this.getDistance(node1, node2);
        }
        return distance;
    }

    /**
     * @brief Calcula la distancia total de varias rutas.
     * @param routes Array de listas, cada una representando una ruta.
     * @return Distancia total de todas las rutas.
     */
    public double getDistance(ArrayList<Integer>[] routes) {
        double distance = 0.0;
        for (ArrayList<Integer> route : routes) {
            distance += this.getDistance(route);
        }
        return distance;
    }

    /**
     * @brief Calcula la matriz de distancias entre puntos de interés.
     */
    public void calculateDistanceMatrix() {
        for (int i = 0; i < this.nodes + 1; i++) {
            for (int j = 0; j < this.nodes + 1; j++) {
                if (i != j) {
                    double diffXs = this.x[i] - this.x[j];
                    double diffYs = this.y[i] - this.y[j];
                    this.distanceMatrix[i][j] = Math.sqrt(diffXs * diffXs + diffYs * diffYs);
                    this.distanceMatrix[j][i] = this.distanceMatrix[i][j];
                } else {
                    this.distanceMatrix[i][j] = 0.0;
                }
            }
        }
    }

    /**
     * @brief Obtiene el tiempo máximo permitido por ruta.
     * @return Tiempo máximo por ruta.
     */
    public double getMaxTimePerRoute() {
        return maxTimePerRoute;
    }

    /**
     * @brief Establece el tiempo máximo permitido por ruta.
     * @param maxTimePerRoute Tiempo máximo por ruta.
     */
    public void setMaxTimePerRoute(double maxTimePerRoute) {
        this.maxTimePerRoute = maxTimePerRoute;
    }

    /**
     * @brief Obtiene el número máximo de rutas.
     * @return Número máximo de rutas.
     */
    public double getMaxRoutes() {
        return maxRoutes;
    }

    /**
     * @brief Establece el número máximo de rutas.
     * @param maxRoutes Número máximo de rutas.
     */
    public void setMaxRoutes(double maxRoutes) {
        this.maxRoutes = maxRoutes;
    }

    /**
     * @brief Obtiene el número de puntos de interés.
     * @return Número de puntos de interés.
     */
    public int getPOIs() {
        return this.nodes;
    }

    /**
     * @brief Obtiene la distancia entre dos puntos específicos.
     * @param i Índice del primer punto.
     * @param j Índice del segundo punto.
     * @return Distancia entre los dos puntos.
     */
    public double getDistance(int i, int j) {
        if(this.isDepot(i)) { i=0; }
        if(this.isDepot(j)) { j=0; }
        return this.distanceMatrix[i][j];
    }

    /**
     * @brief Obtiene el tiempo necesario para viajar entre dos puntos.
     * @param i Índice del primer punto.
     * @param j Índice del segundo punto.
     * @return Tiempo entre los dos puntos.
     */
    public double getTime(int i, int j) {
        if(this.isDepot(i)) { i=0; }
        if(this.isDepot(j)) { j=0; }
        return this.distanceMatrix[i][j];
    }

    /**
     * @brief Obtiene el número total de puntos de interés (nodos).
     * @return Número de nodos.
     */
    public int getNodes() {
        return this.nodes;
    }

    /**
     * @brief Establece el número de nodos en el problema.
     * @param nodes Número de nodos.
     */
    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    /**
     * @brief Obtiene la coordenada X de un punto de interés.
     * @param index Índice del punto de interés.
     * @return Coordenada X del punto de interés.
     */
    public double getX(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.x[index];
    }

    /**
     * @brief Establece la coordenada X de un punto de interés.
     * @param index Índice del punto de interés.
     * @param x Coordenada X a establecer.
     */
    public void setX(int index, double x) {
        this.x[index] = x;
    }

    /**
     * @brief Obtiene la coordenada Y de un punto de interés.
     * @param index Índice del punto de interés.
     * @return Coordenada Y del punto de interés.
     */
    public double getY(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.y[index];
    }

    /**
     * @brief Establece la coordenada Y de un punto de interés.
     * @param index Índice del punto de interés.
     * @param y Coordenada Y a establecer.
     */
    public void setY(int index, double y) {
        this.y[index] = y;
    }

    /**
     * @brief Obtiene la puntuación asociada a un punto de interés.
     * @param index Índice del punto de interés.
     * @return Puntuación del punto de interés.
     */
    public double getScore(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.score[index];
    }

    /**
     * @brief Obtiene el arreglo de puntuaciones de todos los puntos de interés.
     * @return Arreglo de puntuaciones.
     */
    public double[] getScore() {
        return this.score;
    }

    /**
     * @brief Establece la puntuación de un punto de interés.
     * @param index Índice del punto de interés.
     * @param score Puntuación a establecer.
     */
    public void setScore(int index, double score) {
        this.score[index] = score;
    }

    /**
     * @brief Obtiene el tiempo de inicio de servicio de un punto de interés.
     * @param index Índice del punto de interés.
     * @return Tiempo de inicio de servicio.
     */
    public double getReadyTime(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.readyTime[index];
    }

    /**
     * @brief Establece el tiempo de inicio de servicio de un punto de interés.
     * @param index Índice del punto de interés.
     * @param readyTime Tiempo de inicio de servicio a establecer.
     */
    public void setReadyTime(int index, double readyTime) {
        this.readyTime[index] = readyTime;
    }

    /**
     * @brief Obtiene el tiempo límite de servicio de un punto de interés.
     * @param index Índice del punto de interés.
     * @return Tiempo límite de servicio.
     */
    public double getDueTime(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.dueTime[index];
    }

    /**
     * @brief Establece el tiempo límite de servicio de un punto de interés.
     * @param index Índice del punto de interés.
     * @param dueTime Tiempo límite de servicio a establecer.
     */
    public void setDueTime(int index, double dueTime) {
        this.dueTime[index] = dueTime;
    }

    /**
     * @brief Obtiene el tiempo de servicio de un punto de interés.
     * @param index Índice del punto de interés.
     * @return Tiempo de servicio.
     */
    public double getServiceTime(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.serviceTime[index];
    }

    /**
     * @brief Establece el tiempo de servicio de un punto de interés.
     * @param index Índice del punto de interés.
     * @param serviceTime Tiempo de servicio a establecer.
     */
    public void setServiceTime(int index, double serviceTime) {
        this.serviceTime[index] = serviceTime;
    }

    /**
     * @brief Obtiene el número de vehículos disponibles.
     * @return Número de vehículos.
     */
    public int getVehicles() {
        return this.vehicles;
    }

    /**
     * @brief Convierte el objeto a una representación de cadena con información detallada.
     * @return Representación en formato de texto del objeto.
     */
    @Override
    public String toString() {
        final int COLUMN_WIDTH = 15;
        String text = "Nodes: " + this.nodes + "\n";
        String[] strings = new String[]{"CUST NO.", "XCOORD.", "YCOORD.", "SCORE", "READY TIME", "DUE DATE", "SERVICE TIME"};
        int[] width = new int[strings.length];
        Arrays.fill(width, COLUMN_WIDTH);
        text += ExpositoUtilities.getFormat(strings, width) + "\n";
        for (int i = 0; i < this.nodes; i++) {
            strings = new String[strings.length];
            int index = 0;
            strings[index++] = Integer.toString(i);
            strings[index++] = "" + this.x[i];
            strings[index++] = "" + this.y[i];
            strings[index++] = "" + this.score[i];
            strings[index++] = "" + this.readyTime[i];
            strings[index++] = "" + this.dueTime[i];
            strings[index++] = "" + this.serviceTime[i];
            text += ExpositoUtilities.getFormat(strings, width);
            text += "\n";
        }
        text += "Vehicles: " + this.vehicles + "\n";
        strings = new String[]{"VEHICLE", "CAPACITY"};
        width = new int[strings.length];
        Arrays.fill(width, COLUMN_WIDTH);
        text += ExpositoUtilities.getFormat(strings, width) + "\n";
        return text;
    }

    /**
     * @brief Añade un nuevo nodo al conjunto de puntos de interés.
     * @return Número total de nodos tras la adición.
     */
    public int addNode() {
        this.nodes++;
        return this.nodes;
    }

    /**
     * @brief Añade un nuevo depósito al conjunto de depósitos.
     * @return Número total de depósitos tras la adición.
     */
    public int addNodeDepot() {
        this.depots++;
        return this.depots;
    }
}
