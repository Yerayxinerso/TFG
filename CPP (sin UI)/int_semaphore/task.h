#include <iostream>
#include <vector>
#include <thread>
#include <fstream>
#include <random>
#include <chrono>
#include <string>
//#include "barrier.h"
#include <barrier>

using namespace std;

class task
{
private:
    int th_indx;
    int startRow;
    int endRow;
    static int generations;

    static int cell_proliferation_potential_max;
    static float chance_spontaneous_death;
    static int chance_proliferation;
    static int chance_STC_creation;
    static int chance_migration;
    static bool starter_cell_is_STC;
    
    bool check_reach_border(vector<vector<int>> nextGrid);

    void extend_domain();

public:
    static string SCENARIO;
    static int numThreads;
    static int size;
    static vector<vector<int>> currentGrid;
    static vector<vector<int>> nextGrid;
    //static barrier* b;
    static barrier<>* b;
    static bool printing;

    task(int th_indx, int startRow, int endRow);

    static void setSimulationParameters(int size, int generations, vector<vector<int>> currentGrid, vector<vector<int>> nextGrid, int cell_proliferation_potential_max, float chance_spontaneous_death, int chance_proliferation, int chance_STC_creation, int chance_migration, bool starter_cell_is_STC);

    bool check_chance_spontaneous_death(int i, int j);

    bool check_chance_proliferation(int i, int j);

    bool check_chance_migration(int i, int j);

    bool check_chance_STC_creation(int i, int j);

    vector<int> look_free_space(int i, int j);

    void printGrid(vector<vector<int>> grid, int iteration, int numThreads);

    void nextState(int i, int j);

    void operator()();
};
