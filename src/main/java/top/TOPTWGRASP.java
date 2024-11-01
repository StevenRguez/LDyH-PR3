/**
 * @file TOPTWGRASP.java
 * @brief Implementación del algoritmo GRASP para resolver el problema TOPTW.
 */
package top;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.security.SecureRandom;

/**
 * @class TOPTWGRASP
 * @brief Clase que implementa el algoritmo GRASP para resolver el problema TOPTW.
 */
public class TOPTWGRASP {
    /** Valor utilizado para indicar que una solución no ha sido evaluada. */
    public static double NO_EVALUATED = -1.0;

    /** Solución actual del problema. */
    private TOPTWSolution solution;

    /** Tiempo de la solución. */
    private int solutionTime;

    /** Generador de números aleatorios para el proceso de selección aleatoria. */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom(); // Usar SecureRandom

    /**
     * @brief Constructor de la clase TOPTWGRASP.
     * @param sol Solución inicial para el problema.
     */
    public TOPTWGRASP(TOPTWSolution sol) {
        this.solution = sol;
        this.solutionTime = 0;
    }

    /**
     * @brief Método principal de GRASP que ejecuta iteraciones del algoritmo.
     * @param maxIterations Número máximo de iteraciones del algoritmo.
     * @param maxSizeRCL Tamaño máximo de la Lista de Candidatos Restringida (RCL).
     */
    public void GRASP(int maxIterations, int maxSizeRCL) {
        double averageFitness = 0.0;
        double bestSolution = 0.0;
        for (int i = 0; i < maxIterations; i++) {
            this.computeGreedySolution(maxSizeRCL);

            // Imprimir la solución actual
            double fitness = this.solution.evaluateFitness();
            System.out.println(this.solution.getInfoSolution());
            averageFitness += fitness;
            if (bestSolution < fitness) {
                bestSolution = fitness;
            }
        }
        averageFitness = averageFitness / maxIterations;
        System.out.println(" --> MEDIA: " + averageFitness);
        System.out.println(" --> MEJOR SOLUCION: " + bestSolution);
    }

    /**
     * @brief Selecciona aleatoriamente un índice de la RCL.
     * @param maxTRCL Tamaño máximo de la lista RCL.
     * @return Índice seleccionado aleatoriamente de la RCL.
     */
    public int aleatorySelectionRCL(int maxTRCL) {
        return SECURE_RANDOM.nextInt(maxTRCL); // Usa SecureRandom para selección aleatoria
    }

    /**
     * @brief Realiza una selección difusa de la mejor opción en la RCL.
     * @param rcl Lista de Candidatos Restringida (RCL).
     * @return Posición del candidato seleccionado en la RCL.
     */
    public int fuzzySelectionBestFDRCL(ArrayList<double[]> rcl) {
        double[] membershipFunction = new double[rcl.size()];
        double maxSc = this.getMaxScore();
        for (int j = 0; j < rcl.size(); j++) {
            membershipFunction[j] = 1 - ((rcl.get(j)[4]) / maxSc);
        }
        double minMemFunc = Double.MAX_VALUE;
        int posSelected = -1;
        for (int i = 0; i < rcl.size(); i++) {
            if (minMemFunc > membershipFunction[i]) {
                minMemFunc = membershipFunction[i];
                posSelected = i;
            }
        }
        return posSelected;
    }

    /**
     * @brief Selecciona un candidato usando corte alfa en la RCL.
     * @param rcl Lista de Candidatos Restringida (RCL).
     * @param alpha Valor de corte alfa.
     * @return Posición del candidato seleccionado en la RCL.
     */
    public int fuzzySelectionAlphaCutRCL(ArrayList<double[]> rcl, double alpha) {
        double[] membershipFunction = calculateMembershipFunction(rcl); // Calcula la función de membresía
        ArrayList<Integer> rclPos = filterRCLByAlpha(membershipFunction, rcl, alpha); // Filtra RCL según alpha

        // Selección aleatoria de la lista restringida o de la lista completa
        return rclPos.isEmpty() ? aleatorySelectionRCL(rcl.size()) : rclPos.get(aleatorySelectionRCL(rclPos.size()));
    }

    // Submétodo para calcular la función de membresía
    private double[] calculateMembershipFunction(ArrayList<double[]> rcl) {
        double maxSc = this.getMaxScore();
        double[] membershipFunction = new double[rcl.size()];
        for (int j = 0; j < rcl.size(); j++) {
            membershipFunction[j] = 1 - (rcl.get(j)[4] / maxSc);
        }
        return membershipFunction;
    }

    // Submétodo para filtrar RCL según el valor de alpha
    private ArrayList<Integer> filterRCLByAlpha(double[] membershipFunction, ArrayList<double[]> rcl, double alpha) {
        ArrayList<Integer> rclPos = new ArrayList<>();
        for (int j = 0; j < rcl.size(); j++) {
            if (membershipFunction[j] <= alpha) {
                rclPos.add(j);
            }
        }
        return rclPos;
    }

    /**
     * @brief Método principal de construcción de una solución greedy.
     * @param maxSizeRCL Tamaño máximo de la Lista de Candidatos Restringida.
     */
    public void computeGreedySolution(int maxSizeRCL) {
        // Inicialización de la solución
        this.solution.initSolution();
        ArrayList<ArrayList<Double>> departureTimesPerClient = initializeDepartureTimes();

        // Lista de clientes
        ArrayList<Integer> customers = initializeCustomers();

        // Evaluar y ordenar los candidatos
        ArrayList<double[]> candidates = evaluateAndSortCandidates(customers, departureTimesPerClient);

        boolean existCandidates = true;

        while (!customers.isEmpty() && existCandidates) {
            if (!candidates.isEmpty()) {
                ArrayList<double[]> rcl = buildRestrictedCandidateList(candidates, maxSizeRCL);
                int posSelected = selectCandidateFromRCL(rcl);

                double[] candidateSelected = rcl.get(posSelected);
                removeSelectedCustomer(customers, candidateSelected[0]);

                updateSolution(candidateSelected, departureTimesPerClient);
            } else {
                existCandidates = handleNoCandidates(departureTimesPerClient);
            }

            // Reevaluar y ordenar candidatos
            candidates = evaluateAndSortCandidates(customers, departureTimesPerClient);
        }
    }

// Métodos auxiliares

    /**
     * @brief Inicializa los tiempos de salida de cada cliente.
     * @return Lista con los tiempos de salida inicializados.
     */
    private ArrayList<ArrayList<Double>> initializeDepartureTimes() {
        ArrayList<ArrayList<Double>> departureTimesPerClient = new ArrayList<>();
        ArrayList<Double> init = new ArrayList<>();
        for (int z = 0; z < this.solution.getProblem().getPOIs() + this.solution.getProblem().getVehicles(); z++) {
            init.add(0.0);
        }
        departureTimesPerClient.add(init);
        return departureTimesPerClient;
    }

    /**
     * @brief Inicializa la lista de clientes.
     * @return Lista de clientes inicializada.
     */
    private ArrayList<Integer> initializeCustomers() {
        ArrayList<Integer> customers = new ArrayList<>();
        for (int j = 1; j <= this.solution.getProblem().getPOIs(); j++) {
            customers.add(j);
        }
        return customers;
    }

    /**
     * @brief Evalúa y ordena los candidatos según su idoneidad para la solución.
     * @param customers Lista de clientes.
     * @param departureTimesPerClient Tiempos de salida para cada cliente.
     * @return Lista ordenada de candidatos.
     */
    private ArrayList<double[]> evaluateAndSortCandidates(ArrayList<Integer> customers, ArrayList<ArrayList<Double>> departureTimesPerClient) {
        ArrayList<double[]> candidates = this.comprehensiveEvaluation(customers, departureTimesPerClient);
        candidates.sort(Comparator.comparingDouble(a -> a[a.length - 2]));
        return candidates;
    }

    /**
     * @brief Construye la Lista de Candidatos Restringida (RCL).
     * @param candidates Lista completa de candidatos.
     * @param maxSizeRCL Tamaño máximo de la RCL.
     * @return RCL construida con los mejores candidatos.
     */
    private ArrayList<double[]> buildRestrictedCandidateList(ArrayList<double[]> candidates, int maxSizeRCL) {
        ArrayList<double[]> rcl = new ArrayList<>();
        int maxTRCL = Math.min(maxSizeRCL, candidates.size());
        for (int j = 0; j < maxTRCL; j++) {
            rcl.add(candidates.get(j));
        }
        return rcl;
    }

    /**
     * @brief Elimina el cliente seleccionado de la lista de clientes.
     * @param customers Lista de clientes.
     * @param selectedCustomer Cliente seleccionado.
     */
    private void removeSelectedCustomer(ArrayList<Integer> customers, double selectedCustomer) {
        customers.removeIf(customer -> customer == selectedCustomer);
    }

    /**
     * @brief Maneja la situación cuando no existen candidatos disponibles.
     * @param departureTimesPerClient Tiempos de salida de los clientes.
     * @return Verdadero si se añade una nueva ruta, falso en caso contrario.
     */
    private boolean handleNoCandidates(ArrayList<ArrayList<Double>> departureTimesPerClient) {
        if (this.solution.getCreatedRoutes() < this.solution.getProblem().getVehicles()) {
            int newDepot = this.solution.addRoute();
            ArrayList<Double> initNew = new ArrayList<>();
            for (int z = 0; z < this.solution.getProblem().getPOIs() + this.solution.getProblem().getVehicles(); z++) {
                initNew.add(0.0);
            }
            departureTimesPerClient.add(initNew);
            return true;
        } else {
            return false;
        }
    }

    // Método para seleccionar un candidato de la lista restringida
    private int selectCandidateFromRCL(ArrayList<double[]> rcl) {
        int selection = 3; // Cambia el valor según el método de selección que quieras usar
        double alpha = 0.8; // Valor para alpha

        switch (selection) {
            case 1:
                return this.aleatorySelectionRCL(rcl.size());  // Selección aleatoria
            case 2:
                return this.fuzzySelectionBestFDRCL(rcl);       // Selección fuzzy con mejor valor de alpha
            case 3:
                return this.fuzzySelectionAlphaCutRCL(rcl, alpha); // Selección fuzzy con alpha corte aleatoria
            default:
                return this.aleatorySelectionRCL(rcl.size());  // Selección aleatoria por defecto
        }
    }

    /**
     * @brief Actualiza la solución con el candidato seleccionado.
     * @param candidateSelected Candidato seleccionado.
     * @param departureTimes Tiempos de salida de los clientes.
     */
    public void updateSolution(double[] candidateSelected, ArrayList<ArrayList<Double>> departureTimes) {
        // Inserción del cliente en la ruta
        this.solution.setPredecessor((int) candidateSelected[0], (int) candidateSelected[2]);
        this.solution.setSuccessor((int) candidateSelected[0], this.solution.getSuccessor((int) candidateSelected[2]));
        this.solution.setSuccessor((int) candidateSelected[2], (int) candidateSelected[0]);
        this.solution.setPredecessor(this.solution.getSuccessor((int) candidateSelected[0]), (int) candidateSelected[0]);

        // Actualización de las estructuras de datos y conteo a partir de la posición a insertar
        double costInsertionPre = departureTimes.get((int) candidateSelected[1]).get((int) candidateSelected[2]);
        ArrayList<Double> route = departureTimes.get((int) candidateSelected[1]);
        int pre = (int) candidateSelected[2], suc = -1;
        int depot = this.solution.getIndexRoute((int) candidateSelected[1]);

        do {
            suc = this.solution.getSuccessor(pre);
            costInsertionPre += this.solution.getDistance(pre, suc);
            if (costInsertionPre < this.solution.getProblem().getReadyTime(suc)) {
                costInsertionPre = this.solution.getProblem().getReadyTime(suc);
            }
            costInsertionPre += this.solution.getProblem().getServiceTime(suc);
            if (!this.solution.isDepot(suc)) {
                route.set(suc, costInsertionPre);
            }
            pre = suc;
        } while (suc != depot);

        // Actualiza tiempos
        departureTimes.set((int) candidateSelected[1], route);
    }

    public ArrayList<double[]> comprehensiveEvaluation(ArrayList<Integer> customers, ArrayList<ArrayList<Double>> departureTimes) {
        ArrayList<double[]> candidatesList = new ArrayList<>();
        for (int customer : customers) {
            double[] bestCandidate = findBestCandidateForCustomer(customer, departureTimes);
            if (bestCandidate != null) {
                candidatesList.add(bestCandidate);
            }
        }
        return candidatesList;
    }

    // Submétodo para evaluar el mejor candidato de un cliente específico
    private double[] findBestCandidateForCustomer(int customer, ArrayList<ArrayList<Double>> departureTimes) {
        double[] bestCandidate = { -1, -1, -1, Double.MAX_VALUE, -1 };  // cliente, ruta, predecesor, coste, score
        for (int routeIndex = 0; routeIndex < this.solution.getCreatedRoutes(); routeIndex++) {
            double[] candidate = evaluateCandidateForRoute(customer, routeIndex, departureTimes);
            if (candidate != null && candidate[3] < bestCandidate[3]) {
                bestCandidate = candidate;
            }
        }
        return (bestCandidate[0] == -1) ? null : bestCandidate;
    }

    // Submétodo para evaluar la inserción de un cliente en una ruta
    private double[] evaluateCandidateForRoute(int customer, int routeIndex, ArrayList<ArrayList<Double>> departureTimes) {
        double[] candidate = { customer, routeIndex, -1, Double.MAX_VALUE, this.solution.getProblem().getScore(customer) };
        int depot = this.solution.getIndexRoute(routeIndex);
        double costInsertion = 0;

        int pre = depot;
        do {
            int suc = this.solution.getSuccessor(pre);
            if (isValidInsertion(customer, pre, suc, routeIndex, departureTimes)) {
                costInsertion = calculateCostAfterInsertion(customer, pre, suc, routeIndex, departureTimes);
                candidate[2] = pre;
                candidate[3] = costInsertion;
                break;
            }
            pre = suc;
        } while (pre != depot);

        return (candidate[2] == -1) ? null : candidate;
    }

    // Método para verificar si la inserción es válida
    private boolean isValidInsertion(int customer, int pre, int suc, int routeIndex, ArrayList<ArrayList<Double>> departureTimes) {
        // Lógica de validación para la inserción
        return true;  // Implementar la lógica adecuada
    }

    // Método para calcular el costo después de la inserción
    private double calculateCostAfterInsertion(int customer, int pre, int suc, int routeIndex, ArrayList<ArrayList<Double>> departureTimes) {
        // Lógica de cálculo del costo
        return 0;  // Implementar la lógica adecuada
    }

    /**
     * @brief Obtiene el puntaje máximo del problema.
     * @return Puntaje máximo.
     */
    public double getMaxScore() {
        // Implementar la lógica para obtener el puntaje máximo
        return 0;  // Valor de ejemplo
    }
}