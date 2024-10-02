
/**
 * @file UI.java
 * @author Yeray Doello Gonzalez
 * @brief This file contains the user interface for the cellular automata simulation
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @brief This class is a user interface for simulating a cellular automata
 *        system
 * @details The user interface contains a grid where the cells are
 *          represented by pixels. The cells can be of three types: empty,
 *          regular
 *          tumor cell (RTC) and stem tumor cell (STC). The RTCs have a
 *          proliferation potential that decreases with each division. The STCs
 *          have
 *          a higher proliferation potential than the RTCs. The cells can die
 *          spontaneously, divide, migrate and transform into STCs. The
 *          simulation can
 *          be started, stopped, reset, saved, plotted, and the settings can be
 *          changed.
 *          The simulation can be run for a certain number of iterations. The
 *          user can
 *          save the settings to a file and load them from a file. The user can
 *          also
 *          exit the program. The user can also get help and information about
 *          the
 *          program.
 * @note The user interface is implemented using Java Swing and JFreeChart
 *       libraries.
 * @note The simulation is implemented using a cellular automata model.
 * @note The simulation is based on the following rules: 1. A cell can die
 *       spontaneously with a certain probability. 2. A cell can divide with a
 *       certain probability. 3. A cell can migrate with a certain probability.
 *       4.
 *       A cell can transform into a STC with a certain probability. 5. A cell
 *       can
 *       divide into a RTC or a STC. 6. A cell can migrate to a free space. 7. A
 *       cell
 *       can only divide if there is a free space. 8. A cell can only migrate if
 *       there
 *       is a free space. 9. A cell can only transform into a STC if there is a
 *       free
 *       space. 10. A cell can only divide into a STC if there is a free space.
 * 
 * @note The simulation is based on the following parameters: 1. Number of
 *       iterations. 2. Cell proliferation potential max. 3. Chance spontaneous
 *       death. 4. Chance proliferation. 5. Chance migration. 6. Chance STC
 *       creation.
 * 
 * @note The simulation is based on the following variables: 1. Tumor cells. 2.
 *       Domain size. 3. Domain. 4. Cell proliferation potential max. 5. Chance
 *       spontaneous death. 6. Chance proliferation. 7. Chance STC creation. 8.
 *       Chance
 *       migration.
 * 
 * @note The simulation is based on the following functions: 1. Reset domain. 2.
 *       Get cell type. 3. Get cell proliferation potential. 4. Set cell
 *       proliferation potential. 5. Check STC. 6. Adjust proliferation
 *       potential. 7.
 *       Shuffle tumor cells. 8. Check reach border. 9. Extend domain. 10.
 *       Update
 *       system. 11. Create STC daughter. 12. Create RTC daughter. 13. Check
 *       free
 *       space. 14. Look free space. 15. Update cell position. 16. Time. 17.
 *       Last
 *       step. 18. STC count. 19. RTC count. 20. UI. 21. Main. 22. Print domain.
 *       23.
 *       Print plot. 24. Simulation.
 * 
 * @note The user interface is implemented using Java Swing and JFreeChart
 *       libraries.
 */
public class UI {

    public static int num_threads = Runtime.getRuntime().availableProcessors();
    /**
     * @brief This class represents a cell in the cellular automata system
     * @details The cell has an x and y position
     */
    public static class Cell {
        int x;
        int y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * @brief This function returns the type of a cell in the domain
     * @details The function returns 0 if the cell is empty, 1 if the cell is a RTC
     *          and 2 if the cell is a STC
     * @param cell The cell
     * @return The type of the cell
     * @note The function uses a read lock to read the value of the cell in the
     *       domain
     */
    public static int get_cell_type(Cell cell) {
        try {
            lock.readLock().lock();
            if (domain[cell.x + cell.y * domain_size] == 0) {
                return 0;
            } else if (domain[cell.x + cell.y * domain_size] > cell_proliferation_potential_max) {
                return 2;
            } else {
                return 1;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @brief This function returns the proliferation potential of a cell in the
     *        domain
     * @details The function returns the proliferation potential of the cell
     * @param cell The cell
     * @return The proliferation potential of the cell
     */
    public static int get_cell_proliferation_potential(Cell cell) {
        try {
            lock.readLock().lock();
            return domain[cell.x + cell.y * domain_size];
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @brief This function sets the proliferation potential of a cell in the domain
     * @details The function sets the proliferation potential of the cell
     * @param proliferation_potential The proliferation potential
     * @param cell                    The cell
     * 
     * @note The function uses a write lock to change the value of the cell in the
     *       domain
     */
    public void set_cell_proliferation_potential(int proliferation_potential, Cell cell) {
        try {
            lock.readLock().lock();
            domain[cell.x + cell.y * domain_size] = proliferation_potential;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @brief This function checks if a cell is a STC
     * @details The function returns true if the cell is a STC and false otherwise
     * @param cell The cell
     * @return True if the cell is a STC and false otherwise
     * 
     * @note The function uses a read lock to read the value of the cell in the
     *       domain
     */
    public static boolean check_STC(Cell cell) {
        try {
            lock.readLock().lock();
            if (get_cell_type(cell) == 2) {
                return true;
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @brief This function adjusts the proliferation potential of a cell in the
     *        domain
     * @details The function decreases the proliferation potential of the cell by 1
     * @param cell The cell
     * 
     * @note The function uses a write lock to change the value of the cell in the
     *       domain
     */
    public static void adjust_proliferation_potential(Cell cell) {
        try {
            lock.writeLock().lock();
            if (get_cell_proliferation_potential(cell) > 0 && get_cell_type(cell) == 1)
                domain[cell.x + cell.y * domain_size] = (int) (get_cell_proliferation_potential(cell) - 1);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @brief tumour cells is a list of cells that represent the tumour cells in the
     *        domain
     * @brief domain_size is the size of the domain
     * @brief domain is a 2D array that represents the domain
     * @brief cell_proliferation_potential_max is the number of proliferations
     *        before cell dies
     * @brief chance_spontaneous_death is the chance of spontaneous death
     * @brief chance_proliferation is the chance of proliferation
     * @brief chance_STC_creation is the chance of STC creation
     * @brief chance_migration is the chance of migration
     */
    private static ArrayList<Cell> tumor_cells;
    private static int domain_size;
    private static int[] domain;

    private static int cell_proliferation_potential_max; // number of proliferations before cell dies (10, 20, 30, 40,
                                                         // 50, 60, 70, 80, 90, 100)
    private static float chance_spontaneous_death; // 1-100
    private static int chance_proliferation; // 1-100
    private static int chance_STC_creation; // 1-100
    private static int chance_migration; // 1-100

    /**
     * @brief This function resets the domain
     * @details The function sets the domain size to 100 and initializes the domain
     */
    public void reset_domain() {
        // reset domain
        domain_size = 100;
        domain = new int[domain_size * domain_size];
        for (int i = 0; i < domain_size; i++) {
            for (int j = 0; j < domain_size; j++) {
                domain[i + j * domain_size] = 0;
            }
        }
    }

    /**
     * @brief This function checks if a cell randomly dies
     * @details The function returns true if the cell is empty or a STC and false
     *          otherwise
     * @param cell The cell
     * @return True if the cell randomly dies and false otherwise
     * @note The function generates a random number between 0 and 100 and checks if
     *       the number is less than the chance of spontaneous death
     */
    public static boolean chance_spontaneous_death(Cell cell) {
        // check if cell is empty
        if (get_cell_type(cell) == 0 || get_cell_type(cell) == 2) {
            return false;
        }
        double random_number = Math.random() * 100;
        if (random_number < chance_spontaneous_death) {
            return true;
        }
        return false;
    }

    /**
     * @brief This function checks if a cell randomly proliferates
     * @details The function returns true if the cell is a RTC and false otherwise
     * @param cell The cell
     * @return True if the cell randomly proliferates and false otherwise
     * @note The function generates a random number between 0 and 100 and checks if
     *       the number is less than the chance of proliferation
     */
    public static boolean chance_proliferation(Cell cell) {
        if (get_cell_type(cell) == 0) {
            return false;
        }
        int random_number = (int) (Math.random() * 100);
        if (random_number < chance_proliferation) {
            return true;
        }
        return false;
    }

    /**
     * @brief This function checks if a cell randomly transforms into a STC
     * @details The function returns true if the cell is a RTC and false otherwise
     * @param cell The cell
     * @return True if the cell randomly transforms into a STC and false otherwise
     * @note The function generates a random number between 0 and 100 and checks if
     *       the number is less than the chance of STC creation
     */
    public static boolean chance_STC_creation(Cell cell) {
        if (get_cell_type(cell) == 0) {
            return false;
        }
        int random_number = (int) (Math.random() * 100);
        if (random_number < chance_STC_creation) {
            return true;
        }
        return false;
    }

    /**
     * @brief This function checks if a cell randomly migrates
     * @details The function returns true if the cell is a RTC and false otherwise
     * @param cell The cell
     * @return True if the cell randomly migrates and false otherwise
     * @note The function generates a random number between 0 and 100 and checks if
     *       the number is less than the chance of migration
     */
    public static boolean chance_migration(Cell cell) {
        if (get_cell_type(cell) == 0) {
            return false;
        }
        int random_number = (int) (Math.random() * 100);
        if (random_number < chance_migration) {
            return true;
        }
        return false;
    }

    /**
     * @brief This function shuffles the tumor cells
     * @details The function shuffles the tumor cells
     */
    public static void shuffle_tumor_cells() {
        Collections.shuffle(tumor_cells);
    }

    /**
     * @brief This function changes the value of a cell in the domain
     * @details The function changes the value of the cell in the domain
     * @param x The x position of the cell
     * @param y The y position of the cell
     * @param i The value of the cell
     * @note The function uses a write lock to change the value of the cell in the
     *       domain
     */
    public static void set_value_in_domain(int x, int y, int i) {
        try {
            lock.writeLock().lock();
            domain[x + y * domain_size] = i;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @brief This function checks if a cell reaches the border
     * @details The function returns true if the cell is at the border and false
     *          otherwise
     * @return True if the cell reaches the border and false otherwise
     */
    public static boolean check_reach_border() {
        for (int i = 0; i < tumor_cells.size(); i++)
            // check if cell is at border (1 cell away from border)
            if (tumor_cells.get(i).x <= 5
                    || tumor_cells.get(i).x >= domain_size - 6
                    || tumor_cells.get(i).y <= 5
                    || tumor_cells.get(i).y >= domain_size - 6)
                return true;
        return false;
    }

    /**
     * @brief This function extends the domain
     * @details The function extends the border by 2 in each direction
     */
    public static void extend_domain() {
        // extend border by 2 in each direction
        int[] new_domain = new int[(domain_size + 4) * (domain_size + 4)];
        // copy old domain into new domain
        for (int i = 0; i < domain_size - 1; i++) {
            for (int j = 0; j < domain_size - 1; j++) {
                new_domain[(i + 2) + (j + 2) * (domain_size + 4)] = domain[i + j * domain_size];
            }
        }
        domain_size += 4;
        // update domain
        domain = new_domain;
        // update tumor cell positions
        for (int i = 0; i < tumor_cells.size(); i++) {
            tumor_cells.get(i).x = (int) (tumor_cells.get(i).x + 2);
            tumor_cells.get(i).y = (int) (tumor_cells.get(i).y + 2);
        }

    }

    /**
     * @brief This function updates the system
     * @details The function removes dead cells from the tumor cells list
     */
    public static void update_system() {
        for (int i = 0; i < tumor_cells.size(); i++) {
            if (get_cell_type(tumor_cells.get(i)) == 0)
                tumor_cells.remove(i);
        }
    }

    /**
     * @brief This function creates a STC daughter cell
     * @details The function creates a STC daughter cell at the free space
     * @param free_space The free space
     * 
     * @note The function uses a write lock to change the value of the cell in the
     *       domain
     */
    public static void create_STC_daughter(int[] free_space) {
        try {
            lock.writeLock().lock();
            tumor_cells.add(new Cell(free_space[0], free_space[1]));
            domain[free_space[0] + free_space[1] * domain_size] = (int) (cell_proliferation_potential_max + 1);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @brief This function creates a RTC daughter cell
     * @details The function creates a RTC daughter cell at the free space
     * @param free_space The free space
     * 
     * @note The function uses a write lock to change the value of the cell in the
     *       domain
     */
    public static void create_RTC_daughter(Cell cell, int[] free_space) {
        try {
            lock.writeLock().lock();
            tumor_cells.add(new Cell(free_space[0], free_space[1]));
            if (get_cell_type(cell) == 2)
                domain[free_space[0] + free_space[1] * domain_size] = (int) (domain[cell.x + cell.y * domain_size] - 1);
            else
                domain[free_space[0] + free_space[1] * domain_size] = (int) (domain[cell.x + cell.y * domain_size]);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @brief This function looks for free space around a cell
     * @details The function returns the position of the free space
     * @param cell The cell
     * @return The position of the free space
     * @note The function selects a random direction to start looking for free space
     *       and wraps around
     * 
     * @note The function uses a read lock to read the value of the cell in the
     *       domain
     */
    public static int[] look_free_space(Cell cell) {
        try {
            lock.writeLock().lock();
            int[] free_space = new int[2];
            // select random direction to start looking for free space
            int random_number = (int) (Math.random() * 4);

            if (random_number == 0) {
                // look right and wrap around
                if (domain[(cell.x + 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x + 1);
                    free_space[1] = cell.y;
                } else if (domain[(cell.x - 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x - 1);
                    free_space[1] = cell.y;
                } else if (domain[cell.x + (cell.y + 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y + 1);
                } else if (domain[cell.x + (cell.y - 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y - 1);
                } else {
                    free_space[0] = -1;
                    free_space[1] = -1;
                }
            } else if (random_number == 1) {
                // look left and wrap around
                if (domain[(cell.x - 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x - 1);
                    free_space[1] = cell.y;
                } else if (domain[(cell.x + 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x + 1);
                    free_space[1] = cell.y;
                } else if (domain[cell.x + (cell.y - 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y - 1);
                } else if (domain[cell.x + (cell.y + 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y + 1);
                } else {
                    free_space[0] = -1;
                    free_space[1] = -1;
                }
            } else if (random_number == 2) {
                // look up and wrap around
                if (domain[cell.x + (cell.y + 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y + 1);
                } else if (domain[cell.x + (cell.y - 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y - 1);
                } else if (domain[(cell.x + 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x + 1);
                    free_space[1] = cell.y;
                } else if (domain[(cell.x - 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x - 1);
                    free_space[1] = cell.y;
                } else {
                    free_space[0] = -1;
                    free_space[1] = -1;
                }
            } else {
                // look down and wrap around
                if (domain[cell.x + (cell.y - 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y - 1);
                } else if (domain[cell.x + (cell.y + 1) * domain_size] == 0) {
                    free_space[0] = cell.x;
                    free_space[1] = (int) (cell.y + 1);
                } else if (domain[(cell.x - 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x - 1);
                    free_space[1] = cell.y;
                } else if (domain[(cell.x + 1) + cell.y * domain_size] == 0) {
                    free_space[0] = (int) (cell.x + 1);
                    free_space[1] = cell.y;
                } else {
                    free_space[0] = -1;
                    free_space[1] = -1;
                }
            }
            if (free_space[0] != -1 && free_space[1] != -1) {
                domain[free_space[0] + free_space[1] * domain_size] = -1;
                return free_space;
            } else
                return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @brief This function updates the position of a cell in the domain
     * @details The function updates the position of the cell in the domain
     * @param cell         The cell
     * @param new_position The new position of the cell
     * 
     * @note The function uses a write lock to change the value of the cell in the
     *       domain
     */
    public static void update_cell_position(Cell cell, int[] new_position) {
        try {
            lock.writeLock().lock();
            // previously checked that there is free space
            domain[new_position[0] + new_position[1] * domain_size] = domain[cell.x + cell.y * domain_size];
            domain[cell.x + cell.y * domain_size] = 0;

            cell.x = new_position[0];
            cell.y = new_position[1];
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @brief time is the time of the simulation
     * @brief last_step is the number of iterations
     * @brief STC_count is the number of STCs
     * @brief RTC_count is the number of RTCs
     */
    public static int time = 0;
    public static int last_step = 100;
    private boolean starter_cell_is_STC = true;
    private static int[] STC_count;
    private static int[] RTC_count;

    private JFrame main_frame;
    private static JPanel main_panel;
    private JPanel button_panel;
    private JButton start_button;
    private JButton time_gather_button;
    private JButton reset_button;
    private JButton exit_button;
    private JButton save_button;
    private JButton plot_button;
    private JButton help_button;
    private JButton about_button;
    private JButton settings_button;
    private JButton settings_save_button;
    private JButton settings_load_button;
    private JButton settings_exit_button;
    private JButton settings_help_button;
    private JButton settings_default_button;
    private JButton settings_apply_button;

    static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(num_threads);
    static CyclicBarrier EntryBarrier = new CyclicBarrier(num_threads + 1);
    static CyclicBarrier ExitBarrier = new CyclicBarrier(num_threads + 1);
    static private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public UI() {
        main_frame = new JFrame("CA Simulation");
        main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_frame.setSize(800, 600);
        main_frame.setLayout(new BorderLayout());

        main_panel = new JPanel();
        main_panel.setLayout(new BorderLayout());

        button_panel = new JPanel();
        button_panel.setLayout(new GridLayout(4, 4));

        start_button = new JButton("Start");
        time_gather_button = new JButton("time gather");
        reset_button = new JButton("Reset");
        exit_button = new JButton("Exit");
        save_button = new JButton("Save");
        plot_button = new JButton("plot");
        help_button = new JButton("Help");
        about_button = new JButton("About");
        settings_button = new JButton("Settings");

        button_panel.add(start_button);
        button_panel.add(time_gather_button);
        button_panel.add(reset_button);
        button_panel.add(exit_button);
        button_panel.add(save_button);
        button_panel.add(plot_button);
        button_panel.add(help_button);
        button_panel.add(about_button);
        button_panel.add(settings_button);

        main_panel.add(button_panel, BorderLayout.SOUTH);
        main_frame.add(main_panel, BorderLayout.CENTER);
        main_frame.setVisible(true);

        /**
         * @brief This button starts the simulation
         * @details The button starts the simulation and prints the domain
         * @note The button starts the timer
         * @note The button creates a starter cell at the center of the domain
         * @note The button adds the starter cell to the tumor cells list
         * @note The button prints the domain
         * @note The button calls the simulation function
         * @note The button stops the timer
         * @note The button calculates the simulation time
         * @note The button prints the simulation time
         * @note The button prints the domain
         */
        start_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start");
                // start timer (system.nanoTime())
                long start_time = System.nanoTime();
                if (starter_cell_is_STC) {
                    domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max + 1;
                    Cell cell = new Cell(domain_size/2, domain_size/2);
                    tumor_cells.add(cell);
                } else {
                    domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max;
                    Cell cell = new Cell(domain_size/2, domain_size/2);
                    tumor_cells.add(cell);
                }
                print_domain();
                simulation(false);
                executor.shutdown();
                while (!executor.isTerminated()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                // stop timer (system.nanoTime())
                long end_time = System.nanoTime();
                // calculate time
                long simulation_time = end_time - start_time;
                System.out.println("Simulation took " + simulation_time / 1000000 + " milliseconds");
            }
        });

        time_gather_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "This process may take a few hours to complete. Do you want to continue?", "Warning",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    // array with the different scenarios
                    String[] scenarios = { "defaultsettings.settings", "Scenario1Pmax10.settings",
                            "Scenario1Pmax15.settings", "Scenario1Pmax20.settings", "Scenario2Pmax10.settings",
                            "Scenario2Pmax15.settings", "Scenario2Pmax20.settings", "Scenario3Pmax10.settings",
                            "Scenario3Pmax15.settings", "Scenario3Pmax20.settings", "Scenario3Pmax5.settings",
                            "Scenario4Po0.settings", "Scenario4Po1.settings", "Scenario4Po10.settings",
                            "Scenario4Po30.settings", "Scenario5Cw10Ps1.settings", "Scenario5Cw10Ps10.settings",
                            "Scenario5Cw1Ps1.settings", "Scenario5Cw1Ps10.settings", "Scenario5Cw5Ps1.settings",
                            "Scenario5Cw5Ps10.settings" };

                    // results file
                    for (int i = 0; i < scenarios.length; i++) {
                        try {
                            BufferedReader reader = new BufferedReader(
                                    new FileReader("./src/presets/" + scenarios[i]));
                            String line = reader.readLine();
                            last_step = Integer.parseInt(line);
                            line = reader.readLine();
                            
                            cell_proliferation_potential_max = Integer.parseInt(line);
                            line = reader.readLine();
                            chance_spontaneous_death = Float.parseFloat(line);
                            line = reader.readLine();
                            chance_proliferation = Integer.parseInt(line);
                            line = reader.readLine();
                            chance_migration = Integer.parseInt(line);
                            line = reader.readLine();
                            chance_STC_creation = Integer.parseInt(line);
                            line = reader.readLine();
                            starter_cell_is_STC = Boolean.parseBoolean(line);
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            FileWriter writer = new FileWriter("MacroResults.txt", true);
                            writer.write(scenarios[i] + "(multiple runs)\n");
                            writer.write("=====================================================\n");
                            writer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        for (int j = 1; j <= 10; j++) {
                            // start timer (system.nanoTime())
                            long start_time = System.nanoTime();
                            if (starter_cell_is_STC) {
                                domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max + 1;
                                Cell cell = new Cell(domain_size/2, domain_size/2);
                                tumor_cells.add(cell);
                            } else {
                                domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max;
                                Cell cell = new Cell(domain_size/2, domain_size/2);
                                tumor_cells.add(cell);
                            }
                            print_domain();
                            simulation(false);
                            executor.shutdown();
                            while (!executor.isTerminated()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            // stop timer (system.nanoTime())
                            long end_time = System.nanoTime();
                            // calculate time
                            long simulation_time = end_time - start_time;
                            // write results to file
                            try {
                                FileWriter writer = new FileWriter("MacroResults.txt", true);
                                writer.write(simulation_time / 1000000 + " ");
                                writer.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                            // reset simulation
                            reset_domain();
                            tumor_cells.clear();
                            time = 0;
                            executor = (ThreadPoolExecutor) Executors
                                    .newFixedThreadPool(num_threads);
                            Graphics g = main_panel.getGraphics();
                            g.clearRect(0, 0, main_panel.getWidth(), main_panel.getHeight() - 120);
                            g.clearRect(0, main_panel.getHeight() - 20, main_panel.getWidth(), main_panel.getHeight());
                            print_domain();
                        }
                        try {
                            FileWriter writer = new FileWriter("MacroResults.txt", true);
                            writer.write("\n\n");
                            writer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    int[][] last_step_results = new int[scenarios.length][11];
                    last_step_results[0] = new int[] { 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150 };
                    last_step_results[1] = new int[] { 144, 172, 201, 230, 259, 288, 317, 345, 374, 403, 432 };
                    last_step_results[2] = new int[] { 600, 720, 840, 960, 1080, 1200, 1320, 1440, 1560, 1680, 1800 };
                    last_step_results[3] = new int[] { 9372, 11246, 13020, 14794, 16568, 18342, 20116, 21890, 23664,
                            25438, 27212 };
                    last_step_results[4] = new int[] { 1500, 1800, 2100, 2400, 2700, 3000, 3300, 3600, 3900, 4200,
                            4500 };
                    last_step_results[5] = new int[] { 1500, 1800, 2100, 2400, 2700, 3000, 3300, 3600, 3900, 4200,
                            4500 };
                    last_step_results[6] = new int[] { 1500, 1800, 2100, 2400, 2700, 3000, 3300, 3600, 3900, 4200,
                            4500 };
                    last_step_results[7] = new int[] { 2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988,
                            7487 };
                    last_step_results[8] = new int[] { 2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988,
                            7487 };
                    last_step_results[9] = new int[] { 2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988,
                            7487 };
                    last_step_results[10] = new int[] { 2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988,
                            7487 };
                    last_step_results[11] = new int[] { 4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976,
                            14974 };
                    last_step_results[12] = new int[] { 4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976,
                            14974 };
                    last_step_results[13] = new int[] { 4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976,
                            14974 };
                    last_step_results[14] = new int[] { 4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976,
                            14974 };
                    last_step_results[15] = new int[] { 3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188,
                            11987 };
                    last_step_results[16] = new int[] { 3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188,
                            11987 };
                    last_step_results[17] = new int[] { 3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188,
                            11987 };
                    last_step_results[18] = new int[] { 3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188,
                            11987 };
                    last_step_results[19] = new int[] { 3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188,
                            11987 };
                    last_step_results[20] = new int[] { 3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188,
                            11987 };

                    for (int i = 0; i < /*scenarios.length*/0; i++) {
                        try {
                            BufferedReader reader = new BufferedReader(
                                    new FileReader("./src/presets/" + scenarios[i]));
                            String line = reader.readLine();
                            last_step = Integer.parseInt(line);
                            line = reader.readLine();
                            cell_proliferation_potential_max = Integer.parseInt(line);
                            line = reader.readLine();
                            chance_spontaneous_death = Float.parseFloat(line);
                            line = reader.readLine();
                            chance_proliferation = Integer.parseInt(line);
                            line = reader.readLine();
                            chance_migration = Integer.parseInt(line);
                            line = reader.readLine();
                            chance_STC_creation = Integer.parseInt(line);
                            line = reader.readLine();
                            starter_cell_is_STC = Boolean.parseBoolean(line);
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            FileWriter writer = new FileWriter("MacroResults.txt", true);
                            writer.write(scenarios[i] + "(different number of iterations)\n");
                            writer.write("=====================================================\n");
                            writer.write("X: ");
                            for (int j = 0; j < 11; j++) {
                                writer.write(last_step_results[i][j] + " ");
                            }
                            writer.write("\n");
                            writer.write("Y: ");
                            writer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        for (int j = 1; j <= 10; j++) {
                            last_step = last_step_results[i][j];
                            // start timer (system.nanoTime())
                            long start_time = System.nanoTime();
                            if (starter_cell_is_STC) {
                                domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max + 1;
                                Cell cell = new Cell(domain_size/2, domain_size/2);
                                tumor_cells.add(cell);
                            } else {
                                domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max;
                                Cell cell = new Cell(domain_size/2, domain_size/2);
                                tumor_cells.add(cell);
                            }
                            print_domain();
                            simulation(false);
                            executor.shutdown();
                            while (!executor.isTerminated()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            // stop timer (system.nanoTime())
                            long end_time = System.nanoTime();
                            // calculate time
                            long simulation_time = end_time - start_time;
                            // write results to file
                            try {
                                FileWriter writer = new FileWriter("MacroResults.txt", true);
                                writer.write(simulation_time / 1000000 + " ");
                                writer.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                            // reset simulation
                            reset_domain();
                            tumor_cells.clear();
                            time = 0;
                            executor = (ThreadPoolExecutor) Executors
                                    .newFixedThreadPool(num_threads);
                            Graphics g = main_panel.getGraphics();
                            g.clearRect(0, 0, main_panel.getWidth(), main_panel.getHeight() - 120);
                            g.clearRect(0, main_panel.getHeight() - 20, main_panel.getWidth(), main_panel.getHeight());
                            print_domain();
                        }
                        try {
                            FileWriter writer = new FileWriter("MacroResults.txt", true);
                            writer.write("\n\n");
                            writer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        /**
         * This button resets the domain
         * 
         * @details The button resets the domain and clears the tumor cells list
         * @note The button resets the domain
         * @note The button clears the tumor cells list
         * @note The button resets the time
         * @note The button clears the main panel
         * @note The button prints the domain
         */
        reset_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Reset");
                reset_domain();
                tumor_cells.clear();
                time = 0;
                executor = (ThreadPoolExecutor) Executors
                        .newFixedThreadPool(num_threads);
                Graphics g = main_panel.getGraphics();
                g.clearRect(0, 0, main_panel.getWidth(), main_panel.getHeight() - 120);
                g.clearRect(0, main_panel.getHeight() - 20, main_panel.getWidth(), main_panel.getHeight());
                print_domain();
            }
        });

        /**
         * This button exits the program
         * 
         * @details The button exits the program
         * @note The button exits the program
         */
        exit_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exit");
                System.exit(0);
            }
        });

        /**
         * This button saves the domain as an image
         * 
         * @details The button saves the domain as an image
         * @note The button creates a new buffered image
         * @note The button creates a new graphics 2D object
         * @note The button draws the cells
         * @note The button saves the image to a file
         * @note The button prints the file name
         */
        save_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Save");
                BufferedImage image = new BufferedImage(domain_size, domain_size, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                // Draw cells
                for (int i = 0; i < domain_size; i++) {
                    for (int j = 0; j < domain_size; j++) {
                        if (domain[i + j * domain_size] == 0) {
                            g2.setColor(Color.WHITE);
                        } else if (domain[i + j * domain_size] > cell_proliferation_potential_max) {
                            g2.setColor(Color.YELLOW);
                        } else
                            g2.setColor(new Color(255 * domain[i + j * domain_size] / cell_proliferation_potential_max, 0, 0));
                        g2.fillRect(i, j, 1, 1);
                    }
                }
                // get time stamp
                long timeStamp = System.currentTimeMillis();
                try {
                    ImageIO.write(image, "png", new File("image" + timeStamp + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Image saved to image" + timeStamp + ".png");
            }
        });

        /**
         * This button plots the number of STCs and RTCs
         * 
         * @details The button creates a new window
         * @note The button creates a new window
         * @note The button creates a new chart
         * @note The button creates a new chart panel
         * @note The button adds the chart panel to the window
         */
        plot_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // create a new window
                long start_time = System.nanoTime();
                if (starter_cell_is_STC) {
                    domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max + 1;
                    Cell cell = new Cell(domain_size/2, domain_size/2);
                    tumor_cells.add(cell);
                } else {
                    domain[domain_size/2 + domain_size/2 * domain_size] = cell_proliferation_potential_max;
                    Cell cell = new Cell(domain_size/2, domain_size/2);
                    tumor_cells.add(cell);
                }
                STC_count = new int[last_step];
                RTC_count = new int[last_step];
                simulation(true);
                long end_time = System.nanoTime();
                long simulation_time = end_time - start_time;
                System.out.println("Simulation took " + simulation_time / 1000000 + " milliseconds");
                jFreeChart_plot(1, STC_count, RTC_count, last_step, simulation_time);
            }
        });

        help_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Help");
            }
        });

        about_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("About");
            }
        });

        /**
         * This button opens the settings window
         * 
         * @details The button opens the settings window
         * @note The button creates a new window
         * @note The button creates a new panel
         * @note The button creates new labels
         * @note The button creates new textfields
         * @note The button creates new checkboxes
         * @note The button creates new buttons
         * @note The button adds the buttons to the panel
         * @note The button adds the panel to the window
         * @note The button makes the window visible
         */
        settings_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Settings");

                JFrame settings_frame = new JFrame("Settings");
                settings_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                settings_frame.setSize(800, 600);
                settings_frame.setLayout(new BorderLayout());

                JPanel settings_panel = new JPanel();
                settings_panel.setLayout(new BorderLayout());

                JPanel settings_options = new JPanel();
                settings_options.setLayout(new GridLayout(4, 4));

                JLabel settings_options_label = new JLabel("Options");

                JLabel starter_cell_is_STC_label = new JLabel("Starter cell is STC");
                JCheckBox starter_cell_is_STC_checkbox = new JCheckBox();
                starter_cell_is_STC_checkbox.setSelected(starter_cell_is_STC);

                JLabel settings_options_last_step_label = new JLabel("Number of iterations");
                JTextField settings_options_last_step_textfield = new JTextField();
                settings_options_last_step_textfield.setText(Integer.toString(last_step));

                JLabel settings_options_cell_proliferation_potential_max_label = new JLabel(
                        "Cell proliferation potential max");
                JTextField settings_options_cell_proliferation_potential_max_textfield = new JTextField();
                settings_options_cell_proliferation_potential_max_textfield
                        .setText(Integer.toString(cell_proliferation_potential_max));

                JLabel settings_options_chance_spontaneous_death_label = new JLabel("Chance spontaneous death");
                JTextField settings_options_chance_spontaneous_death_textfield = new JTextField();
                settings_options_chance_spontaneous_death_textfield
                        .setText(Float.toString(chance_spontaneous_death * 24));

                JLabel settings_options_chance_proliferation_label = new JLabel("Chance proliferation");
                JTextField settings_options_chance_proliferation_textfield = new JTextField();
                settings_options_chance_proliferation_textfield.setText(Integer.toString(chance_proliferation));
                JLabel settings_options_chance_migration_label = new JLabel("Chance migration");
                JTextField settings_options_chance_migration_textfield = new JTextField();
                settings_options_chance_migration_textfield.setText(Integer.toString(chance_migration));

                JLabel settings_options_chance_STC_creation_label = new JLabel("Chance STC creation");
                JTextField settings_options_chance_STC_creation_textfield = new JTextField();
                settings_options_chance_STC_creation_textfield.setText(Integer.toString(chance_STC_creation));

                JPanel settings_button_panel = new JPanel();
                settings_button_panel.setLayout(new GridLayout(4, 4));

                settings_save_button = new JButton("Save");
                settings_load_button = new JButton("Load");
                settings_exit_button = new JButton("Exit");
                settings_help_button = new JButton("Help");
                settings_default_button = new JButton("Default");
                settings_apply_button = new JButton("Apply");

                JPanel input_panel = new JPanel();
                input_panel.setLayout(new GridLayout(2, 2));
                // Add labels and textfields to input_panel
                input_panel.add(settings_options_last_step_label);
                input_panel.add(settings_options_last_step_textfield);
                input_panel.add(settings_options_cell_proliferation_potential_max_label);
                input_panel.add(settings_options_cell_proliferation_potential_max_textfield);
                input_panel.add(settings_options_chance_spontaneous_death_label);
                input_panel.add(settings_options_chance_spontaneous_death_textfield);
                input_panel.add(settings_options_chance_proliferation_label);
                input_panel.add(settings_options_chance_proliferation_textfield);
                input_panel.add(settings_options_chance_migration_label);
                input_panel.add(settings_options_chance_migration_textfield);
                input_panel.add(settings_options_chance_STC_creation_label);
                input_panel.add(settings_options_chance_STC_creation_textfield);
                // Add input_panel to settings_options
                settings_options.add(input_panel);
                // Add settings_options_label to settings_options
                settings_options.add(settings_options_label);
                // Add starter_cell_is_STC_label and starter_cell_is_STC_checkbox to
                // settings_options
                JPanel starter_cell_is_STC_panel = new JPanel();
                starter_cell_is_STC_panel.setLayout(new GridLayout(1, 2));
                starter_cell_is_STC_panel.add(starter_cell_is_STC_label);
                starter_cell_is_STC_panel.add(starter_cell_is_STC_checkbox);
                settings_options.add(starter_cell_is_STC_panel);
                // Add settings_options to settings_panel
                settings_panel.add(settings_options, BorderLayout.CENTER);
                // Add starter_cell_is_STC_label and starter_cell_is_STC_checkbox to
                // settings_panel
                // Add buttons to settings_button_panel
                settings_button_panel.add(settings_save_button);
                settings_button_panel.add(settings_load_button);
                settings_button_panel.add(settings_exit_button);
                settings_button_panel.add(settings_help_button);
                settings_button_panel.add(settings_default_button);
                settings_button_panel.add(settings_apply_button);
                // Add settings_button_panel to settings_panel
                settings_panel.add(settings_button_panel, BorderLayout.SOUTH);
                // Add settings_panel to settings_frame
                settings_frame.add(settings_panel, BorderLayout.CENTER);
                // Make settings_frame visible
                settings_frame.setVisible(true);

                /**
                 * This button saves the settings to a file
                 * 
                 * @details The button saves the settings to a file
                 * @note The button gets the settings from the textfields
                 * @note The button saves the settings to a file
                 * @note The button prints the file name
                 */
                settings_save_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String settings = new String();
                        // number of iterations
                        settings += settings_options_last_step_textfield.getText() + "\n";
                        // cell proliferation potential max
                        settings += settings_options_cell_proliferation_potential_max_textfield.getText() + "\n";
                        // chance spontaneous death
                        settings += settings_options_chance_spontaneous_death_textfield.getText() + "\n";
                        // chance proliferation
                        settings += settings_options_chance_proliferation_textfield.getText() + "\n";
                        // chance migration
                        settings += settings_options_chance_migration_textfield.getText() + "\n";
                        // chance STC creation
                        settings += settings_options_chance_STC_creation_textfield.getText() + "\n";
                        // Starter cell is STC
                        if (starter_cell_is_STC_checkbox.isSelected()) {
                            settings += "true";
                        } else {
                            settings += "false";
                        }
                        // save settings to file
                        // get time stamp
                        long timeStamp = System.currentTimeMillis();
                        try {
                            FileWriter file = new FileWriter("./src/presets/settings" + timeStamp + ".settings");
                            file.write(settings);
                            file.close();
                            System.out.println("Settings saved to /src/presets/settings" + timeStamp + ".settings");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                /**
                 * This button loads the settings from a file
                 * 
                 * @details The button loads the settings from a file
                 * @note The button opens a file chooser
                 * @note The button reads the settings from the file
                 * @note The button prints the settings
                 */
                settings_load_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Settings Load");
                        // open default file chooser
                        JFileChooser file_chooser = new JFileChooser();
                        // set directory to current directory
                        file_chooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/src/presets"));
                        System.out.println(System.getProperty("user.dir") + "/src/presets");
                        int return_value = file_chooser.showOpenDialog(null);
                        if (return_value == JFileChooser.APPROVE_OPTION) {
                            File file = file_chooser.getSelectedFile();
                            try {
                                // read settings from file
                                FileReader FR = new FileReader(file);
                                BufferedReader BR = new BufferedReader(FR);
                                // number of iterations
                                settings_options_last_step_textfield.setText(BR.readLine());
                                // cell proliferation potential max
                                settings_options_cell_proliferation_potential_max_textfield.setText(BR.readLine());
                                // chance spontaneous death
                                settings_options_chance_spontaneous_death_textfield.setText(BR.readLine());
                                // chance proliferation
                                settings_options_chance_proliferation_textfield.setText(BR.readLine());
                                // chance migration
                                settings_options_chance_migration_textfield.setText(BR.readLine());
                                // chance STC creation
                                settings_options_chance_STC_creation_textfield.setText(BR.readLine());
                                // Starter cell is STC
                                if (BR.readLine().equals("true")) {
                                    starter_cell_is_STC_checkbox.setSelected(true);
                                    starter_cell_is_STC = true;
                                } else {
                                    starter_cell_is_STC_checkbox.setSelected(false);
                                    starter_cell_is_STC = false;
                                }
                                BR.close();
                                FR.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

                /**
                 * This button exits the settings window
                 * 
                 * @details The button exits the settings window
                 * @note The button exits the settings window
                 */
                settings_exit_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Settings Exit");
                        settings_frame.dispose();
                    }
                });

                settings_help_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Settings Help");
                    }
                });

                /**
                 * This button loads the default settings
                 * 
                 * @details The button loads the default settings
                 * @note The button loads the default settings
                 */
                settings_default_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Settings Default");
                        last_step = 100;
                        cell_proliferation_potential_max = 20;
                        chance_spontaneous_death = 1;
                        chance_proliferation = 90;
                        chance_migration = 90;
                        chance_STC_creation = 90;
                        settings_options_last_step_textfield.setText(Integer.toString(last_step));
                        settings_options_cell_proliferation_potential_max_textfield
                                .setText(Integer.toString(cell_proliferation_potential_max));
                        settings_options_chance_spontaneous_death_textfield
                                .setText(Float.toString(chance_spontaneous_death * 24));
                        settings_options_chance_proliferation_textfield.setText(Integer.toString(chance_proliferation));
                        settings_options_chance_migration_textfield.setText(Integer.toString(chance_migration));
                        settings_options_chance_STC_creation_textfield.setText(Integer.toString(chance_STC_creation));
                    }
                });

                /**
                 * This button applies the settings
                 * 
                 * @details The button applies the settings
                 * @note The button applies the settings
                 */
                settings_apply_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Settings Apply");
                        last_step = Integer.parseInt(settings_options_last_step_textfield.getText());
                        cell_proliferation_potential_max = Integer
                                .parseInt(settings_options_cell_proliferation_potential_max_textfield.getText());
                        chance_spontaneous_death = Float
                                .parseFloat(settings_options_chance_spontaneous_death_textfield.getText()) / 24;
                        chance_proliferation = Integer
                                .parseInt(settings_options_chance_proliferation_textfield.getText());
                        chance_migration = Integer.parseInt(settings_options_chance_migration_textfield.getText());
                        chance_STC_creation = Integer
                                .parseInt(settings_options_chance_STC_creation_textfield.getText());
                        settings_frame.dispose();
                    }
                });

                starter_cell_is_STC_checkbox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (starter_cell_is_STC_checkbox.isSelected()) {
                            starter_cell_is_STC = true;
                        } else {
                            starter_cell_is_STC = false;
                        }
                    }
                });
            }
        });

    }

    public static void main(String[] args) {
        UI ui = new UI();
        tumor_cells = new ArrayList<Cell>();
        domain_size = 100;
        domain = new int[domain_size * domain_size];

        cell_proliferation_potential_max = 20;
        chance_spontaneous_death = 1;
        chance_proliferation = 90;
        chance_STC_creation = 90;
        chance_migration = 90;

        // initialize domain
        for (int i = 0; i < domain_size; i++) {
            for (int j = 0; j < domain_size; j++) {
                domain[i + j * domain_size] = 0;
            }
        }
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
    public static void simulation(boolean counting) {
        // While time != last_step
        while (time != last_step) {
            // shuffle tumor_cells
            shuffle_tumor_cells();
            ArrayList<Cell> read_tumor_cells = new ArrayList<Cell>(tumor_cells);
            // For every tumor_cell
            for (int j = 0; j < executor.getMaximumPoolSize(); j++) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EntryBarrier.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int threadId = (int) Thread.currentThread().getId() % executor.getMaximumPoolSize();
                        float step = (float) tumor_cells.size() / (float) executor.getMaximumPoolSize();
                        int[] free_space;
                        for (int i = (int) (step * threadId); i < (int) (step * (threadId + 1)); i++) {
                            if (tumor_cells.get(0) != tumor_cells.get(0))
                                System.out.println("Error");
                            // Check chance_spontaneous_death
                            if (chance_spontaneous_death(tumor_cells.get(i))) {
                                // Empty tumor_cell
                                set_value_in_domain(tumor_cells.get(i).x, tumor_cells.get(i).y, 0);
                            } else {
                                // Check free_space
                                free_space = look_free_space(tumor_cells.get(i));
                                if (free_space != null) {
                                    // Check proliferation_chance
                                    if (chance_proliferation(tumor_cells.get(i))) {
                                        // Check cell_is_STC
                                        if (check_STC(tumor_cells.get(i))) {
                                            // Check chance_STC_creation
                                            if (chance_STC_creation(tumor_cells.get(i))) {
                                                // Create STC_Daugther
                                                create_STC_daughter(free_space);
                                            } else {
                                                // Create RTC_Daugther
                                                create_RTC_daughter(tumor_cells.get(i), free_space);
                                            }
                                        } else {
                                            // Adjust proliferation_potential
                                            adjust_proliferation_potential(tumor_cells.get(i));
                                            if (get_cell_proliferation_potential(tumor_cells.get(i)) > 0) {
                                                // Create RTC_Daugther
                                                create_RTC_daughter(tumor_cells.get(i), free_space);
                                            } else {
                                                // empty cell
                                                set_value_in_domain(tumor_cells.get(i).x, tumor_cells.get(i).y, 0);
                                                set_value_in_domain(free_space[0], free_space[1], 0);
                                            }
                                        }
                                    } else {
                                        // Check migration_chance
                                        if (chance_migration(tumor_cells.get(i))) {
                                            // Update cell_position
                                            update_cell_position(tumor_cells.get(i), free_space);
                                        } else {
                                            // empty cell
                                            set_value_in_domain(free_space[0], free_space[1], 0);
                                        }
                                    }
                                }
                            }
                        }
                        try {
                            ExitBarrier.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            // main thread waits for all threads to finish
            try {
                EntryBarrier.await();
                ExitBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Update system
            update_system();
            // Check Reach_border
            while (check_reach_border()) {
                // Extend domain
                extend_domain();
            }
            print_domain();
            // Advance time
            time++;

            if (counting) {
                STC_count[time - 1] = 0;
                RTC_count[time - 1] = 0;
                for (int i = 0; i < tumor_cells.size(); i++) {
                    if (get_cell_type(tumor_cells.get(i)) == 1)
                        RTC_count[time - 1] = RTC_count[time - 1] + 1;
                    else if (get_cell_type(tumor_cells.get(i)) == 2)
                        STC_count[time - 1] = STC_count[time - 1] + 1;
                }
            }
        }
    }

    /**
     * @brief This function prints the domain
     * @details The function prints the domain
     * @note The function prints the domain to the grid
     * @note The function prints the number of iterations
     */
    static void print_domain() {
        // Print domain to grid (JPanel)
        Graphics g = main_panel.getGraphics();
        for (int i = 0; i < domain_size; i++) {
            for (int j = 0; j < domain_size; j++) {
                if (domain[i + j * domain_size] == 0) {
                    g.setColor(Color.WHITE);
                } else if (domain[i + j * domain_size] > cell_proliferation_potential_max) {
                    g.setColor(Color.YELLOW);
                } else {
                    if (domain[i + j * domain_size] < 0)
                        domain[i + j * domain_size] = 0;
                    g.setColor(new Color(255 * domain[i + j * domain_size] / cell_proliferation_potential_max, 0, 0));
                }
                // paintin the pixel (size responsive to the window size)
                int window_width = main_panel.getWidth();
                int window_height = main_panel.getHeight();
                int pixel_width = 2;// window_width/domain_size;
                int pixel_height = 2;// window_height/domain_size;
                if (window_height < 700) {
                    pixel_width = 1;
                    pixel_height = 1;
                }
                // print centered
                int centerX = (window_width - domain_size * pixel_width) / 2;
                int centerY = (window_height - domain_size * pixel_height) / 2;
                g.fillRect(centerX + i * pixel_width, centerY + j * pixel_height, pixel_width, pixel_height);
            }
        }
        // print number of iterations
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 20);
        g.setColor(Color.BLACK);
        g.drawString("Time: " + time, 10, 10);
        g.drawString("/" + last_step + " hours", 10, 20);
    }

    /**
     * @brief This function prints the plot
     * @details The function prints the plot
     * @param mode            The mode of the plot
     * @param STC_count       The number of STCs
     * @param RTC_count       The number of RTCs
     * @param last_step       The number of iterations
     * @param simulation_time The simulation time
     * @note The function prints the plot
     */
    static void print_plot(int mode, int[] STC_count, int[] RTC_count, int last_step, long simulation_time) {
        Graphics g = main_panel.getGraphics();
        // print plot
        int window_width = main_panel.getWidth();
        int window_height = main_panel.getHeight();
        int window_center_x = window_width / 2;
        int window_center_y = window_height / 2;
        int plot_width = window_width / 2;
        int plot_height = window_height / 2;

        g.setColor(Color.WHITE);
        g.fillRect(window_center_x - plot_width / 2, window_center_y - plot_height / 2, plot_width, plot_height);
        g.setColor(Color.BLACK);
        g.drawRect(window_center_x - plot_width / 2, window_center_y - plot_height / 2, plot_width, plot_height);
        g.drawLine(window_center_x - plot_width / 2, window_center_y, window_center_x + plot_width / 2,
                window_center_y);
        g.drawLine(window_center_x, window_center_y - plot_height / 2, window_center_x,
                window_center_y + plot_height / 2);
        g.drawString("Time", window_center_x + plot_width / 2 - 20, window_center_y + plot_height / 2 + 15);
        g.drawString("Number of cells", window_center_x - plot_width / 2 + 10, window_center_y - plot_height / 2 - 5);
        g.drawString("STC", window_center_x + plot_width / 2 + 10, window_center_y - 10);
        g.drawString("RTC", window_center_x + plot_width / 2 + 10, window_center_y + 10);
        int max = 1;
        for (int i = 0; i < last_step; i++) {
            if (STC_count[i] > max)
                max = STC_count[i];
            if (RTC_count[i] > max)
                max = RTC_count[i];
        }

        if (last_step > plot_width) {
            int step = last_step / plot_width;
            int[] STC_count_temp = new int[plot_width];
            int[] RTC_count_temp = new int[plot_width];
            max = 1;
            for (int i = 0; i < plot_width; i++) {
                STC_count_temp[i] = 0;
                RTC_count_temp[i] = 0;
                for (int j = 0; j < step; j++) {
                    STC_count_temp[i] = STC_count_temp[i] + STC_count[i * step + j];
                    RTC_count_temp[i] = RTC_count_temp[i] + RTC_count[i * step + j];
                }
                STC_count_temp[i] = STC_count_temp[i] / step;
                RTC_count_temp[i] = RTC_count_temp[i] / step;
                if (STC_count_temp[i] > max)
                    max = STC_count_temp[i];
                if (RTC_count_temp[i] > max)
                    max = RTC_count_temp[i];
            }
            STC_count = STC_count_temp;
            RTC_count = RTC_count_temp;
            last_step = plot_width;
        }

        for (int i = 0; i < last_step; i++) {
            g.setColor(Color.RED);
            // plot upwards
            g.fillRect(window_center_x - plot_width / 2 + i * plot_width / last_step,
                    window_center_y - RTC_count[i] * plot_height / 2 / max, plot_width / last_step,
                    RTC_count[i] * plot_height / 2 / max);
            g.setColor(Color.YELLOW);
            // plot downwards
            g.fillRect(window_center_x - plot_width / 2 + i * plot_width / last_step, window_center_y,
                    plot_width / last_step, STC_count[i] * plot_height / 2 / max);
        }
        g.setColor(Color.BLACK);
        g.drawString("Simulation took " + simulation_time / 1000000 + " milliseconds", window_center_x + 10,
                window_center_y + plot_height - 10);

    }

    /**
     * @brief This function prints the plot using jFreeChart
     * @details The function prints the plot using jFreeChart
     * @param mode            The mode of the plot
     * @param STC_count       The number of STCs
     * @param RTC_count       The number of RTCs
     * @param last_step       The number of iterations
     * @param simulation_time The simulation time
     * @note The function prints the plot using jFreeChart
     * @note The function creates a simple XY chart
     * @note The function creates a dataset
     * @note The function adds the series to the dataset
     * @note The function creates a chart
     * @note The function creates a chart panel
     * @note The function adds the chart panel to the frame
     */
    static void jFreeChart_plot(int mode, int[] STC_count, int[] RTC_count, int last_step, long simulation_time) {
        // same as print_plot but using jFreeChart
        // Create a simple XY chart
        XYSeries series1 = new XYSeries("STC");
        XYSeries series2 = new XYSeries("RTC");
        for (int i = 0; i < last_step; i++) {
            series1.add(i, STC_count[i]);
            series2.add(i, RTC_count[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        JFreeChart chart = ChartFactory.createXYLineChart("Cell Count", "Time", "Number of cells", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

}
