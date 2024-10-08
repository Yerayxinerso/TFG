
#include "task.h"
#include <mutex>

// Inicialización de las variables estáticas de la clase task
int task::generations;
int task::cell_proliferation_potential_max;
float task::chance_spontaneous_death;
int task::chance_proliferation;
int task::chance_STC_creation;
int task::chance_migration;
bool task::starter_cell_is_STC;
string task::SCENARIO;
int task::numThreads;
int task::size;
vector<vector<char>> task::currentGrid;
vector<vector<char>> task::nextGrid;
barrier<> *task::b;
bool task::printing = false;
mutex mtx;

/**
 * @brief Verifica si alguna célula ha alcanzado el borde de la cuadrícula.
 * 
 * @param nextGrid La cuadrícula siguiente que se está evaluando.
 * @return true si se alcanzó el borde, false en caso contrario.
 */
bool task::check_reach_border(vector<vector<char>> nextGrid)
{
    for (int i = 0; i < size; i++)
        if ((int) nextGrid[0][i] != 0 || (int) nextGrid[size - 1][i] != 0 || (int) nextGrid[i][0] != 0 || (int) nextGrid[i][size - 1] != 0)
            return true;
    return false;
}

/**
 * @brief Extiende el tamaño de la cuadrícula para acomodar células que se acercan a los bordes.
 */
void task::extend_domain()
{
    vector<vector<char>> newGrid(size + size / 2, vector<char>(size + size / 2, 0));
    for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++)
            newGrid[i + size / 4][j + size / 4] = currentGrid[i][j];
    size = size + size / 2;
    currentGrid = newGrid;
    nextGrid = newGrid;
}

/**
 * @brief Constructor de la clase task.
 * 
 * @param th_indx Índice del hilo.
 * @param startRow Fila de inicio asignada al hilo.
 * @param endRow Fila de fin asignada al hilo.
 */
task::task(int th_indx, int startRow, int endRow)
{
    this->th_indx = th_indx;
    this->startRow = startRow;
    this->endRow = endRow;
}

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
void task::setSimulationParameters(int size, int generations, vector<vector<char>> currentGrid, vector<vector<char>> nextGrid, int cell_proliferation_potential_max, float chance_spontaneous_death, int chance_proliferation, int chance_STC_creation, int chance_migration, bool starter_cell_is_STC)
{
    task::size = size;
    task::generations = generations;
    task::currentGrid = currentGrid;
    task::nextGrid = nextGrid;
    task::cell_proliferation_potential_max = cell_proliferation_potential_max;
    task::chance_spontaneous_death = chance_spontaneous_death;
    task::chance_proliferation = chance_proliferation;
    task::chance_STC_creation = chance_STC_creation;
    task::chance_migration = chance_migration;
    task::starter_cell_is_STC = starter_cell_is_STC;
}

/**
 * @brief Verifica si una célula en la posición (i, j) muere espontáneamente.
 * 
 * @param i Índice de la fila.
 * @param j Índice de la columna.
 * @return true si la célula muere espontáneamente, false en caso contrario.
 */
bool task::check_chance_spontaneous_death(int i, int j)
{
    // check if cell is empty
    if (currentGrid[i][j] == (char) 0 || (int) currentGrid[i][j] > cell_proliferation_potential_max)
        return false;
    int random_number = rand() % 100;
    if (random_number < chance_spontaneous_death)
        return true;
    return false;
}

/**
 * @brief Verifica si una célula en la posición (i, j) prolifera.
 * 
 * @param i Índice de la fila.
 * @param j Índice de la columna.
 * @return true si la célula prolifera, false en caso contrario.
 */
bool task::check_chance_proliferation(int i, int j)
{
    if (currentGrid[i][j] == (char) 0)
        return false;
    int random_number = rand() % 100;
    if (random_number < chance_proliferation)
        return true;
    return false;
}

/**
 * @brief Verifica si una célula en la posición (i, j) migra.
 * 
 * @param i Índice de la fila.
 * @param j Índice de la columna.
 * @return true si la célula migra, false en caso contrario.
 */
bool task::check_chance_migration(int i, int j)
{
    if (currentGrid[i][j] == (char) 0)
        return false;
    int random_number = rand() % 100;
    if (random_number < chance_migration)
        return true;
    return false;
}

/**
 * @brief Verifica si se crea una célula STC en la posición (i, j).
 * 
 * @param i Índice de la fila.
 * @param j Índice de la columna.
 * @return true si se crea una STC, false en caso contrario.
 */
bool task::check_chance_STC_creation(int i, int j)
{
    if (currentGrid[i][j] == (char) 0)
        return false;
    int random_number = rand() % 100;
    if (random_number < chance_STC_creation)
        return true;
    return false;
}

/**
 * @brief Busca un espacio libre alrededor de la posición (i, j).
 * 
 * @param i Índice de la fila.
 * @param j Índice de la columna.
 * @return Vector con las coordenadas de un espacio libre.
 */
vector<int> task::look_free_space(int i, int j)
{
    vector<int> free_space;
    int random_number = rand() % 4;
    for (int k = 0; k < 4; k++)
    {
        switch (random_number)
        {
        case 0:
            if (i - 1 >= 0 && currentGrid[i - 1][j] == (char) 0)
            {
                free_space.push_back(i - 1);
                free_space.push_back(j);
            }
            break;
        case 1:
            if (i + 1 < size && currentGrid[i + 1][j] == (char) 0)
            {
                free_space.push_back(i + 1);
                free_space.push_back(j);
            }
            break;
        case 2:
            if (j - 1 >= 0 && currentGrid[i][j - 1] == (char) 0)
            {
                free_space.push_back(i);
                free_space.push_back(j - 1);
            }
            break;
        case 3:
            if (j + 1 < size && currentGrid[i][j + 1] == (char) 0)
            {
                free_space.push_back(i);
                free_space.push_back(j + 1);
            }
            break;
        }
        if (free_space != vector<int>())
        {
            currentGrid[free_space[0]][free_space[1]] = (char) -1;
            return free_space;
        }
        random_number = (random_number - 1) % 4;
    }
    return vector<int>();
}

/**
 * @brief Imprime la cuadrícula en un archivo BMP.
 * 
 * @param grid La cuadrícula que se va a imprimir.
 * @param iteration Iteración actual de la simulación.
 * @param numThreads Número de hilos en uso.
 */
void task::printGrid(vector<vector<char>> grid, int iteration, int numThreads)
{
    // write grid to bitmap (STC cells are yellow, empty cells are white, RTC cells are a gradient of red to black)
    string filename = "output/" + SCENARIO.substr(0, SCENARIO.find(".")) + "_" + to_string(numThreads) + "threads_" + to_string(iteration) + ".bmp";
    ofstream file(filename, ios::binary);
    int width = grid.size();
    int height = grid[0].size();
    int padding = (4 - (width * 3) % 4) % 4;
    int sizeData = (width * 3 + padding) * height;
    int sizeAll = 54 + sizeData;
    char header[54] = {
        0x42, 0x4D,                                                                              // signature
        (char)(sizeAll), (char)(sizeAll >> 8), (char)(sizeAll >> 16), (char)(sizeAll >> 24),     // size of file
        0x00, 0x00, 0x00, 0x00,                                                                  // reserved
        0x36, 0x00, 0x00, 0x00,                                                                  // offset to start of pixel data
        0x28, 0x00, 0x00, 0x00,                                                                  // size of BITMAPINFOHEADER
        (char)(width), (char)(width >> 8), (char)(width >> 16), (char)(width >> 24),             // width
        (char)(height), (char)(height >> 8), (char)(height >> 16), (char)(height >> 24),         // height
        0x01, 0x00,                                                                              // planes
        0x18, 0x00,                                                                              // bits per pixel
        0x00, 0x00, 0x00, 0x00,                                                                  // compression
        (char)(sizeData), (char)(sizeData >> 8), (char)(sizeData >> 16), (char)(sizeData >> 24), // size of pixel data
        0x13, 0x0B, 0x00, 0x00,                                                                  // horizontal resolution
        0x13, 0x0B, 0x00, 0x00,                                                                  // vertical resolution
        0x00, 0x00, 0x00, 0x00,                                                                  // colors in color table
        0x00, 0x00, 0x00, 0x00                                                                   // important color count
    };
    file.write(header, 54);
    for (int i = 0; i < height; i++)
    {
        for (int j = 0; j < width; j++)
        {
            char pixel[3] = {0, 0, 0};
            if (grid[j][i] == (char) 0)
            {
                pixel[0] = 255;
                pixel[1] = 255;
                pixel[2] = 255;
            }
            else if (grid[j][i] > cell_proliferation_potential_max)
            {
                pixel[0] = 0;
                pixel[1] = 255;
                pixel[2] = 255;
            }
            else
            {
                pixel[0] = 0;
                pixel[1] = 0;
                pixel[2] = 255 * (int) grid[j][i] / cell_proliferation_potential_max;
            }
            file.write(pixel, 3);
        }
        char pad[3] = {0, 0, 0};
        file.write(pad, padding);
    }
    file.close();
}

/**
 * @brief Calcula el siguiente estado de la célula en la posición (i, j).
 * 
 * @param i Índice de la fila.
 * @param j Índice de la columna.
 */
void task::nextState(int i, int j)
{
    if ((int) currentGrid[i][j] <= 0)
    {
        return;
    }
    // Check chance_spontaneous_death
    if (check_chance_spontaneous_death(i, j))
    {
        // Empty tumor_cell
        nextGrid[i][j] = (char) 0;
    }
    else
    {
        // Check free_space
        vector<int> free_space = look_free_space(i, j);
        if (free_space != vector<int>())
        {
            // Check proliferation_chance
            if (check_chance_proliferation(i, j))
            {
                // Check cell_is_STC
                if ((int) currentGrid[i][j] > cell_proliferation_potential_max)
                {
                    // Check chance_STC_creation
                    if (check_chance_STC_creation(i, j))
                    {
                        // Create STC_Daugther
                        nextGrid[free_space[0]][free_space[1]] = (char) cell_proliferation_potential_max + 1;
                    }
                    else
                    {
                        // Create RTC_Daugther
                        nextGrid[free_space[0]][free_space[1]] = (char) cell_proliferation_potential_max;
                    }
                }
                else
                {
                    // Adjust proliferation_potential
                    if ((int) currentGrid[i][j] < cell_proliferation_potential_max + 1)
                        nextGrid[i][j] = (char) ((int)currentGrid[i][j] - 1);
                    if ((int) nextGrid[i][j] > 0)
                    {
                        // Create RTC_Daugther
                        nextGrid[free_space[0]][free_space[1]] = nextGrid[i][j];
                    }
                    else
                    {
                        // empty cell
                        currentGrid[free_space[0]][free_space[1]] = (char) 0;
                        nextGrid[i][j] = (char) 0;
                    }
                }
            }
            else
            {
                // Check migration_chance
                if (check_chance_migration(i, j))
                {
                    // Update cell_position
                    nextGrid[free_space[0]][free_space[1]] = (char) currentGrid[i][j];
                    nextGrid[i][j] = (char) 0;
                }
                else
                {
                    // release free_space
                    currentGrid[free_space[0]][free_space[1]] = (char) 0;
                }
            }
        }
    }
}

/**
 * @brief Sobrecarga del operador de llamada de función para ejecutar la tarea.
 * 
 * Ejecuta las operaciones necesarias para la simulación en el rango de filas asignado al hilo.
 */
void task::operator()()
{
    for (int gen = 0; gen < generations; gen++)
    {
        for (int i = startRow; i < endRow; i++)
        {
            for (int j = 0; j < size; j++)
            {
                if ((i == startRow || i == endRow - 1) && (i != 0 && i != size - 1))
                {
                    unique_lock<mutex> lock{mtx, defer_lock};
                    lock.lock();
                    nextState(i, j);
                    lock.unlock();
                }
                else
                    nextState(i, j);
            }
        }

        if (b != nullptr)
            b->arrive_and_wait();

        if (th_indx == 0)
        {
            currentGrid = nextGrid;
            if ((gen % (generations/20) == 0 || gen == generations - 1) && printing)
                printGrid(nextGrid, gen, numThreads);
        }
        
        if (check_reach_border(currentGrid))
        {

            if (th_indx == 0)
            {
                extend_domain();
                if (b != nullptr)
                    b->arrive_and_wait();
                endRow = size / numThreads;
            }
            else
            {
                if (b != nullptr)
                    b->arrive_and_wait();
                startRow = th_indx * size / numThreads;
                endRow = (th_indx + 1) * size / numThreads;
            }
        }
    }
}
