import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;

public class CellularAutomaton2D_Frontera_acotada_tipo_byte_bloqueo_lock {
    static byte[][] currentGrid;
    static byte[][] nextGrid;
    static int size;
    static int numThreads = 1;
    static int generations;

    public static void main(String[] args) throws InterruptedException, IOException {
        // first of all, empty the output folder
        File outputFolder = new File("output");
        File[] files = outputFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }

        int cell_proliferation_potential_max = 10;
        float chance_spontaneous_death = 0.1f;
        int chance_proliferation = 10;
        int chance_migration = 10;
        int chance_STC_creation = 10;
        boolean starter_cell_is_STC = true;

        System.out.println("Scenario selector");
        System.out.println("=====================================================================\n");
        System.out.println("1. Default settings");
        System.out.println("2. Scenario 1 Pmax 10");
        System.out.println("3. Scenario 1 Pmax 15");
        System.out.println("4. Scenario 1 Pmax 20");
        System.out.println("5. Scenario 2 Pmax 10");
        System.out.println("6. Scenario 2 Pmax 15");
        System.out.println("7. Scenario 2 Pmax 20");
        System.out.println("8. Scenario 3 Pmax 10");
        System.out.println("9. Scenario 3 Pmax 15");
        System.out.println("10. Scenario 3 Pmax 20");
        System.out.println("11. Scenario 3 Pmax 5");
        System.out.println("12. Scenario 4 Po 0");
        System.out.println("13. Scenario 4 Po 1");
        System.out.println("14. Scenario 4 Po 10");
        System.out.println("15. Scenario 4 Po 30");
        System.out.println("16. Scenario 5 Cw 10 Ps 1");
        System.out.println("17. Scenario 5 Cw 10 Ps 10");
        System.out.println("18. Scenario 5 Cw 1 Ps 1");
        System.out.println("19. Scenario 5 Cw 1 Ps 10");
        System.out.println("20. Scenario 5 Cw 5 Ps 1");
        System.out.println("21. Scenario 5 Cw 5 Ps 10");
        System.out.println("22. Exit\n");
        System.out.println("=====================================================================");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int scenario = 0;
        while (scenario < 1 || scenario > 21) {
            System.out.println("Please enter the number of the scenario you want to run (1-21): ");
            try {
                scenario = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (scenario == 22) {
                System.exit(0);
            }
        }

        System.out.println("Desea imprimir la reticula cada varias generaciones? (y/n)");
        String print = "";
        try {
            print = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (print.equals("y"))
            task.printing = true;
        else
            task.printing = false;

        int imputThreads = 0;
        System.out.println("Numero de hilos (2, 4, 8, 16): ");
        try {
            imputThreads = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] scenarios = { "defaultsettings.settings", "Scenario1Pmax10.settings",
                "Scenario1Pmax15.settings", "Scenario1Pmax20.settings", "Scenario2Pmax10.settings",
                "Scenario2Pmax15.settings", "Scenario2Pmax20.settings", "Scenario3Pmax10.settings",
                "Scenario3Pmax15.settings", "Scenario3Pmax20.settings", "Scenario3Pmax5.settings",
                "Scenario4Po0.settings", "Scenario4Po1.settings", "Scenario4Po10.settings",
                "Scenario4Po30.settings", "Scenario5Cw10Ps1.settings", "Scenario5Cw10Ps10.settings",
                "Scenario5Cw1Ps1.settings", "Scenario5Cw1Ps10.settings", "Scenario5Cw5Ps1.settings",
                "Scenario5Cw5Ps10.settings" };

        task.SCENARIO = scenarios[scenario - 1];

        try {
            BufferedReader readr = new BufferedReader(new FileReader("./presets/" + scenarios[scenario - 1]));
            String line = readr.readLine();
            size = 400;
            generations = Integer.parseInt(line);
            line = readr.readLine();
            cell_proliferation_potential_max = Integer.parseInt(line);
            line = readr.readLine();
            chance_spontaneous_death = Float.parseFloat(line);
            line = readr.readLine();
            chance_proliferation = Integer.parseInt(line);
            line = readr.readLine();
            chance_migration = Integer.parseInt(line);
            line = readr.readLine();
            chance_STC_creation = Integer.parseInt(line);
            line = readr.readLine();
            starter_cell_is_STC = Boolean.parseBoolean(line);
            readr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Tamano de la reticula: " + size);
        System.out.println("Numero de generaciones: " + generations);
        System.out.println("Potencial de proliferacion celular maximo: " + cell_proliferation_potential_max);
        System.out.println("Probabilidad de muerte espontanea: " + chance_spontaneous_death + "%");
        System.out.println("Probabilidad de proliferacion: " + chance_proliferation + "%");
        System.out.println("Probabilidad de creacion de celula madre: " + chance_STC_creation + "%");
        System.out.println("Probabilidad de migracion: " + chance_migration + "%");
        System.out.println("La celula inicial es una celula madre: " + starter_cell_is_STC + "\n");

        task.setSimulationParameters(size, generations, currentGrid, nextGrid, cell_proliferation_potential_max,
                chance_spontaneous_death, chance_proliferation, chance_STC_creation, chance_migration,
                starter_cell_is_STC);
        currentGrid = initializeGrid(currentGrid);

        // Ejecutar con un solo hilo
        Thread singleThread = new Thread(new task(0, 0, size));
        long secuentialStartTime = System.currentTimeMillis();
        singleThread.start();
        singleThread.join();
        long secuentialEndTime = System.currentTimeMillis();
        long singleThreadTime = secuentialEndTime - secuentialStartTime;

        System.out.println("Tiempo de ejecucion con un solo hilo: " + singleThreadTime + " ms\n");

        // Ejecutar con multiples hilos
        long parallelStartTime;
        long parallelEndTime;
        currentGrid = initializeGrid(currentGrid);

            numThreads = imputThreads;
            Thread[] threads = new Thread[numThreads];
            CyclicBarrier barrier = new CyclicBarrier(numThreads);
            task.barrier = barrier;
            for (int i = 0; i < numThreads; i++) {
                int startRow = i * size / numThreads;
                int endRow = (i + 1) * size / numThreads;
                threads[i] = new Thread(new task(i, startRow, endRow));
            }
            parallelStartTime = System.currentTimeMillis();
            for (int i = 0; i < numThreads; i++) {
                threads[i].start();
            }
            for (int i = 0; i < numThreads; i++) {
                threads[i].join();
            }
            parallelEndTime = System.currentTimeMillis();

            System.out.println("Tiempo de ejecucion con " + numThreads + " hilos: "
                    + (parallelEndTime - parallelStartTime) + " ms");

            // Calcular el speedup
            double speedup = (double) singleThreadTime
                    / (parallelEndTime - parallelStartTime);

            System.out.println("Speedup: " + speedup + "\n");

    }

    static byte[][] initializeGrid(byte[][] grid) {
        task.size = 400;
        grid = new byte[size][size];
        if (task.starter_cell_is_STC) {
            grid[size / 2][size / 2] = (byte) (task.cell_proliferation_potential_max + 1);
        } else {
            grid[size / 2][size / 2] = (byte) task.cell_proliferation_potential_max;
        }
        task.currentGrid = grid;
        task.nextGrid = grid;
        return grid;
    }
}

class task implements Runnable {
    public static String SCENARIO;
    private int th_indx;
    private int startRow;
    private int endRow;
    public static int size;
    private static int generations;
    public static byte[][] currentGrid;
    public static byte[][] nextGrid;
    public static CyclicBarrier barrier;
    static ReentrantLock lck = new ReentrantLock();
    public static boolean printing = false;

    static int cell_proliferation_potential_max;
    static float chance_spontaneous_death;
    static int chance_proliferation;
    static int chance_STC_creation;
    static int chance_migration;
    static boolean starter_cell_is_STC = true;

    task(int th_indx, int startRow, int endRow) {
        this.th_indx = th_indx;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    public static void setSimulationParameters(int size, int generations, byte[][] currentGrid, byte[][] nextGrid,
            int cell_proliferation_potential_max, float chance_spontaneous_death, int chance_proliferation,
            int chance_STC_creation, int chance_migration, boolean starter_cell_is_STC) {
        task.size = size;
        task.generations = generations;
        task.currentGrid = currentGrid;
        task.nextGrid = nextGrid;
        task.cell_proliferation_potential_max = cell_proliferation_potential_max;
        task.chance_spontaneous_death = chance_spontaneous_death;
        task.chance_proliferation = chance_proliferation;
        task.chance_STC_creation = chance_STC_creation;
        task.chance_migration = chance_migration;
        task.starter_cell_is_STC = starter_cell_is_STC;
    }

    public void nextState(int i, int j) {
        if (currentGrid[i][j] <= 0) {
            return;
        }
        // Check chance_spontaneous_death
        if (chance_spontaneous_death(i, j)) {
            // Empty tumor_cell
            nextGrid[i][j] = 0;
        } else {
            // Check free_space
            int[] free_space = look_free_space(i, j);
            if (free_space != null) {
                // Check proliferation_chance
                if (chance_proliferation(i, j)) {
                    // Check cell_is_STC
                    if (currentGrid[i][j] > cell_proliferation_potential_max) {
                        // Check chance_STC_creation
                        if (chance_STC_creation(i, j)) {
                            // Create STC_Daugther
                            nextGrid[free_space[0]][free_space[1]] = (byte) (cell_proliferation_potential_max + 1);
                        } else {
                            // Create RTC_Daugther
                            nextGrid[free_space[0]][free_space[1]] = (byte) cell_proliferation_potential_max;
                        }
                    } else {
                        // Adjust proliferation_potential
                        if (currentGrid[i][j] < cell_proliferation_potential_max + 1)
                            nextGrid[i][j]--;
                        if (nextGrid[i][j] > 0) {
                            // Create RTC_Daugther
                            nextGrid[free_space[0]][free_space[1]] = nextGrid[i][j];
                        } else {
                            // empty cell
                            currentGrid[free_space[0]][free_space[1]] = 0;
                            nextGrid[i][j] = 0;
                        }
                    }
                } else {
                    // Check migration_chance
                    if (chance_migration(i, j)) {
                        // Update cell_position
                        nextGrid[free_space[0]][free_space[1]] = currentGrid[i][j];
                        nextGrid[i][j] = 0;
                    } else {
                        // empty cell
                        currentGrid[free_space[0]][free_space[1]] = 0;
                    }
                }
            }
        }
    }

    public static boolean chance_spontaneous_death(int i, int j) {
        // check if cell is empty
        if (currentGrid[i][j] == 0 || currentGrid[i][j] > cell_proliferation_potential_max)
            return false;
        int random_number = ThreadLocalRandom.current().nextInt(0, 100);
        if (random_number < chance_spontaneous_death)
            return true;
        return false;
    }

    public static boolean chance_proliferation(int i, int j) {
        if (currentGrid[i][j] == 0)
            return false;
        int random_number = ThreadLocalRandom.current().nextInt(0, 100);
        if (random_number < chance_proliferation)
            return true;
        return false;
    }

    public static boolean chance_migration(int i, int j) {
        if (currentGrid[i][j] == 0)
            return false;
        int random_number = ThreadLocalRandom.current().nextInt(0, 100);
        if (random_number < chance_migration)
            return true;
        return false;
    }

    public static boolean chance_STC_creation(int i, int j) {
        if (currentGrid[i][j] == 0)
            return false;
        int random_number = ThreadLocalRandom.current().nextInt(0, 100);
        if (random_number < chance_STC_creation)
            return true;
        return false;
    }

    public static int[] look_free_space(int i, int j) {
        int[] free_space = new int[2];
        int random_number = ThreadLocalRandom.current().nextInt(0, 1000) % 4;
        for (int k = 0; k < 4; k++) {
            switch (random_number) {
                case 0:
                    if (i - 1 >= 0 && currentGrid[i - 1][j] == 0) {
                        free_space[0] = i - 1;
                        free_space[1] = j;
                    }
                    break;
                case 1:
                    if (i + 1 < size && currentGrid[i + 1][j] == 0) {
                        free_space[0] = i + 1;
                        free_space[1] = j;
                    }
                    break;
                case 2:
                    if (j - 1 >= 0 && currentGrid[i][j - 1] == 0) {
                        free_space[0] = i;
                        free_space[1] = j - 1;
                    }
                    break;
                case 3:
                    if (j + 1 < size && currentGrid[i][j + 1] == 0) {
                        free_space[0] = i;
                        free_space[1] = j + 1;
                    }
                    break;
            }
            if (free_space[0] != 0 || free_space[1] != 0) {
                currentGrid[free_space[0]][free_space[1]] = -1;
                return free_space;
            }
            random_number = (random_number - 1) % 4;
        }
        return null;
    }

    public static void printGrid(byte[][] grid, int iteration, int numThreads) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) {
                    g.setColor(Color.WHITE);
                } else if (grid[i][j] > cell_proliferation_potential_max) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(new Color(255 * grid[i][j] / cell_proliferation_potential_max, 0, 0));
                }
                g.fillRect(i, j, 1, 1);
            }
        }
        try {
            ImageIO.write(img, "png", new File("output/" + SCENARIO + "_" + numThreads + "_" + iteration + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (int gen = 0; gen < generations; gen++) {
            for (int i = endRow - 1; i >= startRow; i--) {
                for (int j = size - 1; j > 0; j--) {
                    if ((i == startRow || i == endRow - 1) && (i != 0 && i != size - 1))
                        try {
                            lck.lock();
                            nextState(i, j);
                        } finally {
                            lck.unlock();
                        }
                    else
                        nextState(i, j);
                }
            }
            if (barrier != null)
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            if (th_indx == 0) {
                currentGrid = nextGrid;
                if ((gen % (generations / 10 - 1) == 0 || gen == generations - 1) && printing)
                    printGrid(nextGrid, gen, CellularAutomaton2D_Frontera_acotada_tipo_byte_bloqueo_lock.numThreads);
            }
            if (check_reach_border(currentGrid)) {

                if (th_indx == 0) {
                    extend_domain();
                    if (barrier != null)
                        try {
                            barrier.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    endRow = size / CellularAutomaton2D_Frontera_acotada_tipo_byte_bloqueo_lock.numThreads;
                } else {
                    if (barrier != null)
                        try {
                            barrier.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    startRow = th_indx * size / CellularAutomaton2D_Frontera_acotada_tipo_byte_bloqueo_lock.numThreads;
                    endRow = (th_indx + 1) * size / CellularAutomaton2D_Frontera_acotada_tipo_byte_bloqueo_lock.numThreads;
                }
            }
        }
    }

    private boolean check_reach_border(byte[][] nextGrid) {
        for (int i = 0; i < size; i++)
            if (nextGrid[0][i] != 0 || nextGrid[size - 1][i] != 0 || nextGrid[i][0] != 0 || nextGrid[i][size - 1] != 0)
                return true;
        return false;
    }

    private void extend_domain() {
        byte[][] newGrid = new byte[size + size / 2][size + size / 2];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                newGrid[i + size / 4][j + size / 4] = currentGrid[i][j];
        size = size + size / 2;
        currentGrid = newGrid;
        nextGrid = newGrid;
    }
}
