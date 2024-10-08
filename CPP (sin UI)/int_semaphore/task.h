#include <iostream>
#include <vector>
#include <thread>
#include <fstream>
#include <random>
#include <chrono>
#include <string>
#include <barrier>

using namespace std;

/**
 * @brief Clase que representa una tarea para un hilo en la simulación de la cuadrícula.
 */
class task
{
private:
    int th_indx;       /**< Índice del hilo. */
    int startRow;      /**< Fila de inicio para este hilo. */
    int endRow;        /**< Fila de fin para este hilo. */
    static int generations;  /**< Número total de generaciones en la simulación. */

    static int cell_proliferation_potential_max; /**< Máximo potencial de proliferación de las células. */
    static float chance_spontaneous_death; /**< Probabilidad de muerte espontánea de una célula. */
    static int chance_proliferation; /**< Probabilidad de proliferación de una célula. */
    static int chance_STC_creation; /**< Probabilidad de creación de una célula STC. */
    static int chance_migration; /**< Probabilidad de migración de una célula. */
    static bool starter_cell_is_STC; /**< Indica si la célula inicial es una STC. */

    /**
     * @brief Verifica si se alcanzó el borde de la cuadrícula.
     * 
     * @param nextGrid La cuadrícula siguiente que se está evaluando.
     * @return true si se alcanzó el borde, false en caso contrario.
     */
    bool check_reach_border(vector<vector<char>> nextGrid);

    /**
     * @brief Extiende el dominio de la simulación.
     * 
     * Esta función se utiliza para aumentar el tamaño de la cuadrícula en caso de ser necesario.
     */
    void extend_domain();

public:
    static string SCENARIO; /**< Escenario actual de la simulación. */
    static int numThreads; /**< Número de hilos utilizados en la simulación. */
    static int size; /**< Tamaño de la cuadrícula. */
    static vector<vector<int>> currentGrid; /**< Cuadrícula actual de la simulación. */
    static vector<vector<int>> nextGrid; /**< Cuadrícula siguiente de la simulación. */
    static barrier<>* b; /**< Barrera utilizada para sincronización entre hilos. */
    static bool printing; /**< Indica si se debe imprimir la cuadrícula en cada iteración. */

    /**
     * @brief Constructor de la clase task.
     * 
     * @param th_indx Índice del hilo.
     * @param startRow Fila de inicio asignada al hilo.
     * @param endRow Fila de fin asignada al hilo.
     */
    task(int th_indx, int startRow, int endRow);

    /**
     * @brief Configura los parámetros de la simulación.
     * 
     * @param size Tamaño de la cuadrícula.
     * @param generations Número de generaciones a simular.
     * @param currentGrid Cuadrícula actual.
     * @param nextGrid Cuadrícula siguiente.
     * @param cell_proliferation_potential_max Potencial máximo de proliferación de las células.
     * @param chance_spontaneous_death Probabilidad de muerte espontánea.
     * @param chance_proliferation Probabilidad de proliferación.
     * @param chance_STC_creation Probabilidad de creación de células STC.
     * @param chance_migration Probabilidad de migración.
     * @param starter_cell_is_STC Indica si la célula inicial es una STC.
     */
    static void setSimulationParameters(int size, int generations, vector<vector<int>> currentGrid, vector<vector<int>> nextGrid, int cell_proliferation_potential_max, float chance_spontaneous_death, int chance_proliferation, int chance_STC_creation, int chance_migration, bool starter_cell_is_STC);

    /**
     * @brief Verifica si ocurre muerte espontánea en la posición (i, j).
     * 
     * @param i Índice de la fila.
     * @param j Índice de la columna.
     * @return true si la célula muere espontáneamente, false en caso contrario.
     */
    bool check_chance_spontaneous_death(int i, int j);

    /**
     * @brief Verifica si ocurre proliferación en la posición (i, j).
     * 
     * @param i Índice de la fila.
     * @param j Índice de la columna.
     * @return true si la célula prolifera, false en caso contrario.
     */
    bool check_chance_proliferation(int i, int j);

    /**
     * @brief Verifica si ocurre migración en la posición (i, j).
     * 
     * @param i Índice de la fila.
     * @param j Índice de la columna.
     * @return true si la célula migra, false en caso contrario.
     */
    bool check_chance_migration(int i, int j);

    /**
     * @brief Verifica si se crea una célula STC en la posición (i, j).
     * 
     * @param i Índice de la fila.
     * @param j Índice de la columna.
     * @return true si se crea una STC, false en caso contrario.
     */
    bool check_chance_STC_creation(int i, int j);

    /**
     * @brief Busca un espacio libre alrededor de la posición (i, j).
     * 
     * @param i Índice de la fila.
     * @param j Índice de la columna.
     * @return Vector con las coordenadas de un espacio libre.
     */
    vector<int> look_free_space(int i, int j);

    /**
     * @brief Imprime la cuadrícula actual en la consola.
     * 
     * @param grid La cuadrícula que se va a imprimir.
     * @param iteration Número de iteración actual.
     * @param numThreads Número de hilos en uso.
     */
    void printGrid(vector<vector<int>> grid, int iteration, int numThreads);

    /**
     * @brief Calcula el siguiente estado de la célula en la posición (i, j).
     * 
     * @param i Índice de la fila.
     * @param j Índice de la columna.
     */
    void nextState(int i, int j);

    /**
     * @brief Sobrecarga del operador de llamada de función para ejecutar la tarea.
     * 
     * Ejecuta las operaciones necesarias para la simulación en el rango de filas asignado al hilo.
     */
    void operator()();
};
