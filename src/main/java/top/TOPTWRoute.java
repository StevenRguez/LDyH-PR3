package top;

/**
 * @class TOPTWRoute
 * @brief Clase que representa una ruta en el problema de TOPTW.
 *        Cada ruta tiene un nodo predecesor, un nodo sucesor y un identificador.
 */
public class TOPTWRoute {
    int predecessor; /**< Nodo predecesor de la ruta */
    int succesor; /**< Nodo sucesor de la ruta */
    int id; /**< Identificador de la ruta */

    /**
     * @brief Constructor por defecto de la clase TOPTWRoute.
     */
    TOPTWRoute() {}

    /**
     * @brief Constructor de la clase TOPTWRoute con valores iniciales.
     * @param pre Nodo predecesor de la ruta.
     * @param succ Nodo sucesor de la ruta.
     * @param id Identificador de la ruta.
     */
    TOPTWRoute(int pre, int succ, int id) {
        this.predecessor = pre;
        this.succesor = succ;
        this.id = id;
    }

    /**
     * @brief Obtiene el nodo predecesor de la ruta.
     * @return Nodo predecesor.
     */
    public int getPredeccesor() {
        return this.predecessor;
    }

    /**
     * @brief Obtiene el nodo sucesor de la ruta.
     * @return Nodo sucesor.
     */
    public int getSuccesor() {
        return this.succesor;
    }

    /**
     * @brief Obtiene el identificador de la ruta.
     * @return Identificador de la ruta.
     */
    public int getId() {
        return this.id;
    }

    /**
     * @brief Establece el nodo predecesor de la ruta.
     * @param pre Nodo predecesor.
     */
    public void setPredeccesor(int pre) {
        this.predecessor = pre;
    }

    /**
     * @brief Establece el nodo sucesor de la ruta.
     * @param suc Nodo sucesor.
     */
    public void setSuccesor(int suc) {
        this.succesor = suc;
    }

    /**
     * @brief Establece el identificador de la ruta.
     * @param id Identificador de la ruta.
     */
    public void setId(int id) {
        this.id = id;
    }
}