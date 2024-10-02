/**
 * @file simulation.h
 * @author Yeray Doello González
 * @brief This file contains the declaration of the Simulation class.
 * @version 0.1
 * @date 2024-04-10
 * 
 * @copyright Copyright (c) 2024
 * 
 */

#ifndef SIMULATION_H///
#define SIMULATION_H

#include <vector>
#include <utility>

using namespace std;


class Cell
{
public:
    int x;
    int y;

    Cell(int x, int y)
    {
        this->x = x;
        this->y = y;
    }
};


class Simulation
{
public:
    Simulation();

    std::vector<Cell> tumor_cells;
    int domain_size = 100;
    int *domain = new int [domain_size*domain_size];

    int last_step;
    int cell_proliferation_potential_max; // number of proliferations before cell dies (10, 20, 30, 40,
                                          // 50, 60, 70, 80, 90, 100)
    float chance_spontaneous_death_;      // 1-100
    int chance_proliferation_;            // 1-100
    int chance_STC_creation_;             // 1-100
    int chance_migration_;                // 1-100

    int simul_time = 0;
    bool starter_cell_is_STC = true;
    std::vector<int> STC_count;
    std::vector<int> RTC_count;

    int get_cell_type(Cell cell);

    int get_cell_proliferation_potential(Cell cell);
    void set_cell_proliferation_potential(int proliferation_potential, Cell cell);

    bool check_STC(Cell cell);

    void adjust_proliferation_potential(Cell cell);

    void reset_domain();

    bool chance_spontaneous_death(Cell cell);

    bool chance_proliferation(Cell cell);

    bool chance_STC_creation(Cell cell);

    bool chance_migration(Cell cell);

    void shuffle_tumor_cells();

    bool check_reach_border();

    void extend_domain();

    void update_system();

    void create_STC_daughter(std::pair<int, int> free_space);

    void create_RTC_daughter(Cell cell, std::pair<int, int> free_space);

    void set_value_in_domain(std::pair<int, int> position, int value);

    bool check_free_space(Cell cell);

    std::pair<int, int> look_free_space(Cell cell);

    void update_cell_position(Cell *cell, std::pair<int, int> new_position);

    void run(bool counting = false);
};

#endif // SIMULATION_H
