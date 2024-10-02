/**
 * @file simulation.cpp
 * @author Yeray Doello González
 * @brief This file contains the implementation of the simulation class.
 * @version 0.1
 * @date 2024-04-10
 *
 * @copyright Copyright (c) 2024
 *
 */

#include "simulation.h"
#include <algorithm>
#include <ctime>
#include <iostream>
#include <random>
#include <QReadWriteLock>
#include <QThreadPool>
#include <QRunnable>
#include <thread>
#include <mutex>

auto rng = std::default_random_engine{};
std::recursive_mutex ReadLock;
std::recursive_mutex WriteLock;

static const int MAX_THREADS = std::thread::hardware_concurrency();

/**
 * @brief Construct a new Simulation:: Simulation object
 *
 * @details This function is the constructor of the Simulation class.
 */
Simulation::Simulation()
{
    rng.seed(time(0));
    QThreadPool::globalInstance()->setMaxThreadCount(std::thread::hardware_concurrency());
}

/**
 * @brief Destroy the Simulation:: Simulation object
 *
 * @details This function is the destructor of the Simulation class.
 */
int Simulation::get_cell_type(Cell cell)
{
    int type = 0;
    std::lock_guard<std::recursive_mutex> lock(ReadLock);
    if (domain[cell.x + cell.y * domain_size] == 0)
        type = 0;
    else if (domain[cell.x + cell.y * domain_size] > cell_proliferation_potential_max)
        type = 2;
    else
        type = 1;

    return type;
}

/**
 * @brief This function returns the proliferation potential of a cell.
 * @param cell
 * @return int
 *
 * @details This function returns the proliferation potential of a cell.
 */
int Simulation::get_cell_proliferation_potential(Cell cell)
{
    int proliferation_potential = 0;
    std::lock_guard<std::recursive_mutex> lock(ReadLock);
    proliferation_potential = domain[cell.x + cell.y * domain_size];
    return proliferation_potential;
}

/**
 * @brief This function sets the proliferation potential of a cell.
 * @param proliferation_potential
 * @param cell
 *
 * @details This function sets the proliferation potential of a cell.
 */
void Simulation::set_cell_proliferation_potential(int proliferation_potential, Cell cell)
{
    std::lock_guard<std::recursive_mutex> lock(WriteLock);
    domain[cell.x + cell.y * domain_size] = proliferation_potential;
}

/**
 * @brief This function checks if a cell is a STC cell
 * @param cell
 * @return bool
 *
 * @details This function checks if a cell is a STC cell
 */
bool Simulation::check_STC(Cell cell)
{
    bool stc = false;
    std::lock_guard<std::recursive_mutex> lock(ReadLock);
    if (get_cell_type(cell) == 2)
        stc = true;
    return stc;
}

/**
 * @brief This function adjusts the proliferation potential of a cell
 * @param cell
 *
 * @details This function adjusts the proliferation potential of a cell
 */
void Simulation::adjust_proliferation_potential(Cell cell)
{
    std::lock_guard<std::recursive_mutex> lock(WriteLock);
    if (get_cell_proliferation_potential(cell) > 0 && get_cell_type(cell) == 1)
        domain[cell.x + cell.y * domain_size] = (int)(get_cell_proliferation_potential(cell) - 1);
}

/**
 * @brief This function initializes the domain
 *
 * @details This function initializes the domain
 */
void Simulation::reset_domain()
{
    domain_size = 100;
    domain = new int [domain_size*domain_size];
    for (int i = 0; i < domain_size; i++)
    {
        for (int j = 0; j < domain_size; j++)
            domain[i + j * domain_size] = 0;
    }
    tumor_cells.clear();
    STC_count.clear();
    RTC_count.clear();
}

/**
 * @brief This function checks if a cell dies spontaneously
 * @param cell
 * @return bool
 *
 * @details This function checks if a cell dies spontaneously
 */
bool Simulation::chance_spontaneous_death(Cell cell)
{
    bool dead = false;
    std::lock_guard<std::recursive_mutex> lock(ReadLock);
    if (get_cell_type(cell) != 0 && get_cell_type(cell) != 2)
    {
        int random_number = (double)(rng() - rng.min()) / (rng.max() - rng.min()) * 100;
        if (random_number < chance_spontaneous_death_)
            dead = true;
    }
    return dead;
}

/**
 * @brief This function checks if a cell proliferates
 * @param cell
 * @return bool
 *
 * @details This function checks if a cell proliferates
 */
bool Simulation::chance_proliferation(Cell cell)
{
    bool proliferate = false;
    std::lock_guard<std::recursive_mutex> lock(ReadLock);
    if (get_cell_type(cell) != 0)
    {
        int random_number = (double)(rng() - rng.min()) / (rng.max() - rng.min()) * 100;
        if (random_number < chance_proliferation_)
            proliferate = true;
    }
    return proliferate;
}

/**
 * @brief This function checks if a cell creates a STC cell
 * @param cell
 * @return bool
 *
 * @details This function checks if a cell creates a STC cell
 */
bool Simulation::chance_STC_creation(Cell cell)
{
    bool create_STC = false;
    std::lock_guard<std::recursive_mutex> lock(ReadLock);
    if (get_cell_type(cell) != 0)
    {
        int random_number = (double)(rng() - rng.min()) / (rng.max() - rng.min()) * 100;
        if (random_number < chance_STC_creation_)
            create_STC = true;
    }
    return create_STC;
}

/**
 * @brief This function checks if a cell migrates
 * @param cell
 * @return bool
 *
 * @details This function checks if a cell migrates
 */
bool Simulation::chance_migration(Cell cell)
{
    bool migrate = false;
    std::lock_guard<std::recursive_mutex> lock(ReadLock);
    if (get_cell_type(cell) != 0)
    {
        int random_number = (double)(rng() - rng.min()) / (rng.max() - rng.min()) * 100;
        if (random_number < chance_migration_)
            migrate = true;
    }
    return migrate;
}

/**
 * @brief This function shuffles the tumor cells
 *
 * @details This function shuffles the tumor cells
 */
void Simulation::shuffle_tumor_cells()
{
    std::shuffle(std::begin(tumor_cells), std::end(tumor_cells), rng);
}

void Simulation::set_value_in_domain(std::pair<int, int> free_space, int value)
{
    std::lock_guard<std::recursive_mutex> lock(WriteLock);
    domain[free_space.first + free_space.second * domain_size] = value;
}

/**
 * @brief This function checks if the tumor cells reach the border
 * @return bool
 *
 * @details This function checks if the tumor cells reach the border
 */
bool Simulation::check_reach_border()
{
    for (int i = 0; i < (int)tumor_cells.size(); i++)
    {
        if (tumor_cells.at(i).x <= 5 || tumor_cells.at(i).x >= domain_size - 6 || tumor_cells.at(i).y <= 5 || tumor_cells.at(i).y >= domain_size - 6)
            return true;
    }
    return false;
}

/**
 * @brief This function extends the domain
 *
 * @details This function extends the domain by 2 in each direction
 */
void Simulation::extend_domain()
{
    // extend border by 2 in each direction
    int *new_domain = new int [(domain_size + 4)*(domain_size + 4)];
    for (int i = 0; i < domain_size + 4; i++)
    {
        for (int j = 0; j < domain_size + 4; j++)
            new_domain[i + j * domain_size] = 0;
    }
    // copy old domain into new domain
    for (int i = 0; i < domain_size - 1; i++)
    {
        for (int j = 0; j < domain_size - 1; j++)
        {
            new_domain[(i+2) + (j+2) * (domain_size+4)] = domain[i + j * domain_size];
        }
    }
    domain_size += 4;
    // update domain
    domain = new_domain;
    // update tumor cell positions
    for (int i = 0; i < (int)tumor_cells.size(); i++)
    {
        tumor_cells.at(i).x = (int)(tumor_cells.at(i).x + 2);
        tumor_cells.at(i).y = (int)(tumor_cells.at(i).y + 2);
    }
}

/**
 * @brief This function updates the system
 *
 * @details This function updates the system by removing dead cells
 */
void Simulation::update_system()
{
    // remove dead cells
    for (int i = 0; i < (int)tumor_cells.size(); i++)
    {
        if (get_cell_type(tumor_cells.at(i)) == 0)
        {
            domain[tumor_cells.at(i).x + tumor_cells.at(i).y * domain_size] = 0;
            tumor_cells.erase(tumor_cells.begin() + i);
        }
    }
}

/**
 * @brief This function creates a STC daughter cell
 * @param free_space
 *
 * @details This function creates a STC daughter cell
 */
void Simulation::create_STC_daughter(std::pair<int, int> free_space)
{
    std::lock_guard<std::recursive_mutex> lock(WriteLock);
    tumor_cells.push_back(Cell(free_space.first, free_space.second));
    domain[free_space.first + free_space.second * domain_size] = (int)(cell_proliferation_potential_max + 1);
}

/**
 * @brief This function creates a RTC daughter cell
 * @param cell
 * @param free_space
 *
 * @details This function creates a RTC daughter cell
 */
void Simulation::create_RTC_daughter(Cell cell, std::pair<int, int> free_space)
{
    std::lock_guard<std::recursive_mutex> lock(WriteLock);
    tumor_cells.push_back(Cell(free_space.first, free_space.second));
    if (get_cell_type(cell) == 2)
        domain[free_space.first + free_space.second * domain_size] = (int)(domain[cell.x + cell.y * domain_size] - 1);
    else
        domain[free_space.first + free_space.second * domain_size] = (int)(domain[cell.x + cell.y * domain_size]);
}

/**
 * @brief This function looks for free space around a cell
 * @param cell
 * @return std::pair<int, int>
 *
 * @details This function looks for free space around a cell, starting from a random direction and wrapping around and returns the first free space found.
 */
std::pair<int, int> Simulation::look_free_space(Cell cell)
{
    std::pair<int, int> free_space = std::make_pair(0, 0);
    std::lock_guard<std::recursive_mutex> lock(WriteLock);
    // select random direction to start looking for free space
    int random_number = (double)(rng() - rng.min()) / (rng.max() - rng.min()) * 4;
    if (random_number == 0)
    {
        // look right and wrap around
        if (domain[(cell.x + 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x + 1);
            free_space.second = cell.y;
        }
        else if (domain[(cell.x - 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x - 1);
            free_space.second = cell.y;
        }
        else if (domain[cell.x + (cell.y + 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y + 1);
        }
        else if (domain[cell.x + (cell.y - 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y - 1);
        }
        else
        {
            free_space.first = -1;
            free_space.second = -1;
        }
    }
    else if (random_number == 1)
    {
        // look left and wrap around
        if (domain[(cell.x - 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x - 1);
            free_space.second = cell.y;
        }
        else if (domain[(cell.x + 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x + 1);
            free_space.second = cell.y;
        }
        else if (domain[cell.x + (cell.y - 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y - 1);
        }
        else if (domain[cell.x + (cell.y + 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y + 1);
        }
        else
        {
            free_space.first = -1;
            free_space.second = -1;
        }
    }
    else if (random_number == 2)
    {
        // look up and wrap around
        if (domain[cell.x + (cell.y + 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y + 1);
        }
        else if (domain[cell.x + (cell.y - 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y - 1);
        }
        else if (domain[(cell.x + 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x + 1);
            free_space.second = cell.y;
        }
        else if (domain[(cell.x - 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x - 1);
            free_space.second = cell.y;
        }
        else
        {
            free_space.first = -1;
            free_space.second = -1;
        }
    }
    else
    {
        // look down and wrap around
        if (domain[cell.x + (cell.y - 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y - 1);
        }
        else if (domain[cell.x + (cell.y + 1) * domain_size] == 0)
        {
            free_space.first = cell.x;
            free_space.second = (int)(cell.y + 1);
        }
        else if (domain[(cell.x - 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x - 1);
            free_space.second = cell.y;
        }
        else if (domain[(cell.x + 1) + cell.y * domain_size] == 0)
        {
            free_space.first = (int)(cell.x + 1);
            free_space.second = cell.y;
        }
        else
        {
            free_space.first = -1;
            free_space.second = -1;
        }
    }
    if (free_space.first != -1 && free_space.second != -1)
        domain[free_space.first + free_space.second * domain_size] = -1;
    return free_space;
}

/**
 * @brief This function updates the position of a cell
 * @param cell
 * @param new_position
 *
 * @details This function updates the position of a cell
 */
void Simulation::update_cell_position(Cell *cell, std::pair<int, int> new_position)
{
    std::lock_guard<std::recursive_mutex> lock(WriteLock);
    domain[new_position.first + new_position.second * domain_size] = domain[cell->x + cell->y * domain_size];
    domain[cell->x + cell->y * domain_size] = 0;
    cell->x = new_position.first;
    cell->y = new_position.second;
}

/**
 * @brief This function performs the simulation
 * @details The function performs the simulation
 * @param counting If true, the function counts the number of STCs and RTCs
 * @note The function shuffles the tumor cells
 * @note The function reads the tumor cells
 * @note The function checks the chance of spontaneous death
 * @note The function empties the tumor cell
 * @note The function checks free space
 * @note The function checks the chance of proliferation
 * @note The function looks for free space
 * @note The function checks if the cell is a STC
 * @note The function checks the chance of STC creation
 * @note The function creates a STC daughter cell
 * @note The function adjusts the proliferation potential
 * @note The function creates a RTC daughter cell
 * @note The function checks the chance of migration
 * @note The function updates the cell position
 * @note The function updates the system
 * @note The function checks if the cell reaches the border
 * @note The function extends the domain
 * @note The function prints the domain
 * @note The function advances the time
 * @note The function counts the number of STCs and RTCs
 */
void Simulation::run(bool counting)
{
    // shuffle tumor_cells
    shuffle_tumor_cells();
    std::vector<Cell>* read_tumor_cells = new std::vector<Cell>(tumor_cells);
    // For every tumor_cell
    std::vector<std::thread *>* threads = new std::vector<std::thread *>();
    for (int j = 0; j < MAX_THREADS; j++)
    {
        threads->emplace_back(new thread([read_tumor_cells, this]()
                                        {
                                 int start_index = std::hash<std::thread::id>{}(std::this_thread::get_id()) % MAX_THREADS;
                                 
                                 std::pair<int, int> free_space;
                                 for (size_t i = start_index; i < read_tumor_cells->size(); i += MAX_THREADS)
                                 {
                                     // Check chance_spontaneous_death
                                     if (this->chance_spontaneous_death(read_tumor_cells->at(i)))
                                     {
                                         // Empty tumor_cell
                                            this->set_value_in_domain(std::make_pair(read_tumor_cells->at(i).x, read_tumor_cells->at(i).y), 0);
                                     }
                                     else
                                     {
                                         // Check free_space
                                         free_space = this->look_free_space(read_tumor_cells->at(i));
                                         if (free_space.first != -1 && free_space.second != -1)
                                         {
                                             // Check proliferation_chance
                                             if (this->chance_proliferation(read_tumor_cells->at(i)))
                                             {
                                                 // Check cell_is_STC
                                                 if (this->check_STC(read_tumor_cells->at(i)))
                                                 {
                                                     // Check chance_STC_creation
                                                     if (this->chance_STC_creation(read_tumor_cells->at(i)))
                                                     {
                                                         // Create STC_Daugther
                                                         this->create_STC_daughter(free_space);
                                                     }
                                                     else
                                                     {
                                                         // Create RTC_Daugther
                                                         this->create_RTC_daughter(this->tumor_cells.at(i), free_space);
                                                     }
                                                 }
                                                 else
                                                 {
                                                     // Adjust proliferation_potential
                                                     this->adjust_proliferation_potential(this->tumor_cells.at(i));
                                                     if (this->get_cell_proliferation_potential(this->tumor_cells.at(i)) > 0)
                                                     {
                                                         // Create RTC_Daugther
                                                         this->create_RTC_daughter(this->tumor_cells.at(i), free_space);
                                                     }
                                                     else
                                                     {
                                                         // empty cell
                                                            this->set_value_in_domain(free_space, 0);
                                                     }
                                                 }
                                             }
                                             else
                                             {
                                                 // Check migration_chance
                                                 if (this->chance_migration(read_tumor_cells->at(i)))
                                                 {
                                                     // Update cell_position
                                                     this->update_cell_position(&this->tumor_cells.at(i), free_space);
                                                 }
                                                 else
                                                 {
                                                     this->set_value_in_domain(free_space, 0);
                                                 }
                                             }
                                         }
                                     }
                                 } }));
    }
    for (size_t i = 0; i < threads->size(); i++)
    {
        threads->at(i)->join();
    }
    // Update system
    update_system();
    // Check Reach_border
    while (check_reach_border())
    {
        // Extend domain
        extend_domain();
    }
    // Advance time
    simul_time++;

    if (counting)
    {
        int STC_count_ = 0;
        int RTC_count_ = 0;
        for (int i = 0; i < (int)tumor_cells.size(); i++)
        {
            if (get_cell_type(tumor_cells.at(i)) == 1)
                RTC_count_ = RTC_count_ + 1;
            else if (get_cell_type(tumor_cells.at(i)) == 2)
                STC_count_ = STC_count_ + 1;
        }
        STC_count.push_back(STC_count_);
        RTC_count.push_back(RTC_count_);
    }
    delete threads;
    delete read_tumor_cells;
}
