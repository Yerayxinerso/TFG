#include <iostream>
#include <fstream>
#include <thread>
#include "task.h"

using namespace std;

int size;
int numThreads = 8;
int generations;

void initializeGrid(vector<vector<char>> *currentGrid, vector<vector<char>> *nextGrid, bool starter_cell_is_STC, int cell_proliferation_potential_max)
{
    ::size = 400;
    currentGrid->assign(::size, vector<char>(::size, 0));
    nextGrid->assign(::size, vector<char>(::size, 0));
    if (starter_cell_is_STC)
    {
        (*currentGrid)[::size / 2][::size / 2] = (char) cell_proliferation_potential_max + 1;
        (*nextGrid)[::size / 2][::size / 2] = (char) cell_proliferation_potential_max + 1;
    }
    else
    {
        (*currentGrid)[::size / 2][::size / 2] = (char) cell_proliferation_potential_max;
        (*nextGrid)[::size / 2][::size / 2] = (char) cell_proliferation_potential_max;
    }
}

int main()
{
    srand(time(0));
    int cell_proliferation_potential_max = 10;
    float chance_spontaneous_death = 0.1f;
    int chance_proliferation = 10;
    int chance_migration = 10;
    int chance_STC_creation = 10;
    bool starter_cell_is_STC = true;

    cout << "Scenario selector" << endl;
    cout << "=====================================================================\n"
         << endl;
    cout << "1. Default settings" << endl;
    cout << "2. Scenario 1 Pmax 10" << endl;
    cout << "3. Scenario 1 Pmax 15" << endl;
    cout << "4. Scenario 1 Pmax 20" << endl;
    cout << "5. Scenario 2 Pmax 10" << endl;
    cout << "6. Scenario 2 Pmax 15" << endl;
    cout << "7. Scenario 2 Pmax 20" << endl;
    cout << "8. Scenario 3 Pmax 10" << endl;
    cout << "9. Scenario 3 Pmax 15" << endl;
    cout << "10. Scenario 3 Pmax 20" << endl;
    cout << "11. Scenario 3 Pmax 5" << endl;
    cout << "12. Scenario 4 Po 0" << endl;
    cout << "13. Scenario 4 Po 1" << endl;
    cout << "14. Scenario 4 Po 10" << endl;
    cout << "15. Scenario 4 Po 30" << endl;
    cout << "16. Scenario 5 Cw 10 Ps 1" << endl;
    cout << "17. Scenario 5 Cw 10 Ps 10" << endl;
    cout << "18. Scenario 5 Cw 1 Ps 1" << endl;
    cout << "19. Scenario 5 Cw 1 Ps 10" << endl;
    cout << "20. Scenario 5 Cw 5 Ps 1" << endl;
    cout << "21. Scenario 5 Cw 5 Ps 10" << endl;
    cout << "22. Exit" << endl;
    cout << "=====================================================================\n"
         << endl;

    int scenario;
    cout << "Please enter the number of the scenario you want to run (1-21): ";
    cin >> scenario;

    while (scenario < 1 || scenario > 22)
    {
        cout << "Invalid scenario number. Please enter a number between 1 and 22: ";
        cin >> scenario;
    }

    if (scenario == 22)
    {
        return 0;
    }

    cout << "Desea imprimir la reticula cada varias generaciones? (y/n)";
    char print;
    cin >> print;
    bool printGrid = print == 'y';

    cout << "Cuantos hilos desea utilizar? (2, 4, 8, 16...)";
    cin >> numThreads;
    
    vector<string> scenarios = {"defaultsettings.settings", "Scenario1Pmax10.settings",
                                "Scenario1Pmax15.settings", "Scenario1Pmax20.settings", "Scenario2Pmax10.settings",
                                "Scenario2Pmax15.settings", "Scenario2Pmax20.settings", "Scenario3Pmax10.settings",
                                "Scenario3Pmax15.settings", "Scenario3Pmax20.settings", "Scenario3Pmax5.settings",
                                "Scenario4Po0.settings", "Scenario4Po1.settings", "Scenario4Po10.settings",
                                "Scenario4Po30.settings", "Scenario5Cw10Ps1.settings", "Scenario5Cw10Ps10.settings",
                                "Scenario5Cw1Ps1.settings", "Scenario5Cw1Ps10.settings", "Scenario5Cw5Ps1.settings",
                                "Scenario5Cw5Ps10.settings"};

    ifstream file("./presets/" + scenarios[scenario - 1]);
    cout << "opening file: " << "./presets/" + scenarios[scenario - 1] << endl;
    ::size = 400;
    file >> generations;
    file >> cell_proliferation_potential_max;
    file >> chance_spontaneous_death;
    file >> chance_proliferation;
    file >> chance_migration;
    file >> chance_STC_creation;
    string starter_cell_is_STC_str;
    file >> starter_cell_is_STC_str;
    starter_cell_is_STC = starter_cell_is_STC_str == "true";
    file.close();

    cout << "Generations: " << generations << endl;
    cout << "Cell proliferation potential max: " << cell_proliferation_potential_max << endl;
    cout << "Chance spontaneous death: " << chance_spontaneous_death << endl;
    cout << "Chance proliferation: " << chance_proliferation << endl;
    cout << "Chance migration: " << chance_migration << endl;
    cout << "Chance STC creation: " << chance_STC_creation << endl;
    cout << "Starter cell is STC: " << starter_cell_is_STC << endl;

    vector<vector<char>> currentGrid(::size, vector<char>(::size, 0));
    vector<vector<char>> nextGrid(::size, vector<char>(::size, 0));

    initializeGrid(&currentGrid, &nextGrid, starter_cell_is_STC, cell_proliferation_potential_max);
    task::setSimulationParameters(::size, generations, currentGrid, nextGrid, cell_proliferation_potential_max, chance_spontaneous_death, chance_proliferation, chance_STC_creation, chance_migration, starter_cell_is_STC);
    task::printing = printGrid;

    task::numThreads = numThreads;
    //barrier b(numThreads);
    barrier<> b(numThreads);
    task::b = &b;
    cout << "Number of threads: " << numThreads << endl;
    vector<thread> threads;

    // Parallel execution
    auto start = chrono::high_resolution_clock::now();

    for (int i = 0; i < numThreads; i++)
    {
        threads.push_back(thread(task(i, i * ::size / numThreads, (i + 1) * ::size / numThreads)));
    }
    for (int i = 0; i < numThreads; i++)
    {
        threads[i].join();
    }
    auto end = chrono::high_resolution_clock::now();
    chrono::duration<double> parallel_time = end - start;
    cout << "Parallel execution time: " << parallel_time.count() << "s" << endl;

    // secuential execution
    initializeGrid(&currentGrid, &nextGrid, starter_cell_is_STC, cell_proliferation_potential_max);
    task::setSimulationParameters(::size, generations, currentGrid, nextGrid, cell_proliferation_potential_max, chance_spontaneous_death, chance_proliferation, chance_STC_creation, chance_migration, starter_cell_is_STC);
    task::printing = printGrid;
    task::numThreads = 1;

    task::b = nullptr;

    start = chrono::high_resolution_clock::now();

    thread t(task(0, 0, ::size));
    t.join();

    end = chrono::high_resolution_clock::now();
    chrono::duration<double> sequential_time = end - start;
    cout << "Sequential execution time: " << sequential_time.count() << "s" << endl;

    cout << "Speedup: " << sequential_time.count() / parallel_time.count() << endl << endl;

    cout << "Press any key to exit" << endl;
    cin.ignore();
    cin.get();

    return 0;
} // main