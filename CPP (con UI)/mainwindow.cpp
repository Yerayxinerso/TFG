/**
 * @file mainwindow.cpp
 * @author Yeray Doello González
 * @brief this file contains the implementation of the MainWindow class
 * @version 0.1
 * @date 2024-04-10
 *
 * @copyright Copyright (c) 2024
 *
 */

#include <QMessageBox>
#include <QFileDialog>
#include <QPainter>
#include <QImage>
#include <QPen>
#include <QBrush>
#include <QGraphicsScene>
#include <QGraphicsRectItem>
#include <QGraphicsView>
#include <QApplication>
#include <QDialog>
#include <iostream>
#include <fstream>

#include "mainwindow.h"
#include "settingswindow.h"
#include "ui_mainwindow.h"
#include "simulation.h"
#include "qcustomplot.h"

/**
 * @brief MainWindow::MainWindow
 * @param parent
 * @details This function is the constructor of the MainWindow class. It initializes the main window.
 * @return void
 *
 * @details This function is the constructor of the MainWindow class. It initializes the main window.
 */
Simulation sim;

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent), ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    // set the graphics scene
    QGraphicsScene *scene = new QGraphicsScene(this);
    ui->graphicsView->setScene(scene);
    scene->setSceneRect(0, 0, 400, 400);
    ui->graphicsView->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOn);
    ui->graphicsView->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOn);

    // set the graphics items
    QBrush whiteBrush(Qt::white);

    sim.reset_domain();
    sim.last_step = 100;
    sim.cell_proliferation_potential_max = 20;
    sim.chance_spontaneous_death_ = 1;
    sim.chance_proliferation_ = 90;
    sim.chance_STC_creation_ = 90;
    sim.chance_migration_ = 90;
    sim.starter_cell_is_STC = true;
    sim.simul_time = 0;

    // draw the domain
    scene->addRect(0, 0, 400, 400, QPen(Qt::black), whiteBrush);
}

/**
 * @brief MainWindow::~MainWindow
 * @details This function is the destructor of the MainWindow class.
 * @return void
 *
 * @details This function is the destructor of the MainWindow class.
 */
MainWindow::~MainWindow()
{
    delete ui;
}

/**
 * @brief MainWindow::on_pushButton_9_clicked
 * @details This function is the slot for the SETTINGS button. It opens the settings window.
 * @return void
 *
 * @details This function is the slot for the SETTINGS button. It opens the settings window.
 */
void MainWindow::on_pushButton_9_clicked() // SETTINGS
{
    settingswindow *Settingswindow = new settingswindow(this, &sim);
    Settingswindow->exec();
    delete Settingswindow;
}

/**
 * @brief MainWindow::on_pushButton_4_clicked
 * @details This function is the slot for the START button. It starts the simulation.
 * @return void
 *
 * @details This function is the slot for the START button. It starts the simulation.
 */
void MainWindow::on_pushButton_4_clicked() // START
{
    sim.reset_domain();
    // start the timer
    auto start = std::chrono::system_clock::now();
    sim.tumor_cells.push_back(Cell(50, 50));
    if (sim.starter_cell_is_STC)
        sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max + 1;
    else
        sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max;
    print_domain();
    simulate();
    print_domain_end();
    // stop the timer
    auto end = std::chrono::system_clock::now();
    std::chrono::duration<float, std::milli> duration = end - start;
    // print the time
    std::cout << "Simulation took: " << duration.count() << " ms" << std::endl;
}

/**
 * @brief MainWindow::on_pushButton_3_clicked
 * @details This function is the slot for the PAUSE button. It pauses the simulation.
 * @return void
 *
 * @details This function is the slot for the PAUSE button. It pauses the simulation.
 */
void MainWindow::on_pushButton_6_clicked() // EXIT
{
    QApplication::quit();
}

void MainWindow::on_pushButton_5_clicked() // HELP
{
    QMessageBox::information(this, "Help", "This is a help message");
}

void MainWindow::on_pushButton_10_clicked() // TIME GATHERING
{
    // show confirmation message
    QMessageBox::StandardButton reply;
    reply = QMessageBox::question(this, "Start recording", "Do you want to start recording the time? (this takes a while)", QMessageBox::Yes | QMessageBox::No);
    if (reply == QMessageBox::Yes)
    {
        // get proyect_path/presets
        QString presets_path = QCoreApplication::applicationDirPath();
        std::cout << presets_path.toStdString() << std::endl;
        presets_path = presets_path.left(presets_path.lastIndexOf("/"));
        QString output_path = presets_path + "/output";
        presets_path += "/src/presets";
        std::cout << presets_path.toStdString() << std::endl;

        // create the file / clear it on the output directory (TFG/output)
        std::ofstream file;
        file.open(output_path.toStdString() + "/MacroResults.txt", std::ios_base::trunc);
        file.close();
        std::cout << output_path.toStdString() + "/MacroResults.txt";

        // string array with the different scenarios
        std::string scenarios[] = {"defaultsettings.settings", "Scenario1Pmax10.settings", "Scenario1Pmax15.settings", "Scenario1Pmax20.settings", "Scenario2Pmax10.settings", "Scenario2Pmax15.settings", "Scenario2Pmax20.settings", "Scenario3Pmax10.settings", "Scenario3Pmax15.settings", "Scenario3Pmax20.settings", "Scenario3Pmax5.settings", "Scenario4Po0.settings", "Scenario4Po1.settings", "Scenario4Po10.settings", "Scenario4Po30.settings", "Scenario5Cw10Ps1.settings", "Scenario5Cw10Ps10.settings", "Scenario5Cw1Ps1.settings", "Scenario5Cw1Ps10.settings", "Scenario5Cw5Ps1.settings", "Scenario5Cw5Ps10.settings"};

        QString fileName;
        QString line;
        QStringList list;
        auto start = std::chrono::system_clock::now();
        auto end = std::chrono::system_clock::now();
        std::chrono::duration<float, std::milli> duration;
        std::string _Temporary_buffer;

        // results file
        for (size_t i = 0; i < scenarios->size(); i++)
        {
            _Temporary_buffer.clear();
            // load the settings from a file
            fileName = presets_path + "/" + QString::fromStdString(scenarios[i]);
            if (fileName.isEmpty())
            {
                return;
            }
            else
            {
                QFile file(fileName);
                if (!file.open(QIODevice::ReadOnly))
                {
                    QMessageBox::information(this, tr("Unable to open file"), file.errorString());
                    return;
                }
                QTextStream in(&file);
                line = in.readLine();
                list = line.split(" ");
                sim.last_step = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.cell_proliferation_potential_max = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_spontaneous_death_ = list.at(0).toFloat();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_proliferation_ = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_migration_ = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_STC_creation_ = list.at(0).toInt();
                line = in.readLine();
                if (line == "true")
                    sim.starter_cell_is_STC = true;
                else
                    sim.starter_cell_is_STC = false;
                file.close();
                line.clear();
                list.clear();
            }
            std::cout << scenarios[i] << " loaded successfully" << std::endl;
            std::ofstream file;
            file.open(output_path.toStdString() + "/MacroResults.txt", std::ios_base::app);
            file << scenarios[i] << "(multiple runs)" << std::endl << "=====================================================\n" << std::endl;
            file.close();

            // repeat the simulation 10 times
            for (int j = 0; j < 10; j++)
            {
                // reset the domain
                sim.simul_time = 0;
                sim.reset_domain();
                // start the timer
                start = std::chrono::system_clock::now();
                // run the simulation
                sim.tumor_cells.push_back(Cell(50, 50));
                if (sim.starter_cell_is_STC)
                    sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max + 1;
                else
                    sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max;
                print_domain();
                simulate(false);
                print_domain_end();
                // stop the timer
                end = std::chrono::system_clock::now();
                duration = end - start;
                _Temporary_buffer += std::to_string(duration.count()) + " ";
            }
            file.open(output_path.toStdString() + "/MacroResults.txt", std::ios_base::app);
            file << _Temporary_buffer << std::endl;
            file << "\n\n" << std::endl;
            file.close();
        }

        // multiple runs changing last_step
        std::vector<std::vector<int>> last_steps= {
            {50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150},
            {144, 172, 201, 230, 259, 288, 317, 345, 374, 403, 432},
            {600, 720, 840, 960, 1080, 1200, 1320, 1440, 1560, 1680, 1800},
            {9372, 11246, 13020, 14794, 16568, 18342, 20116, 21890, 23664, 25438, 27212},
            {1500, 1800, 2100, 2400, 2700, 3000, 3300, 3600, 3900, 4200, 4500},
            {1500, 1800, 2100, 2400, 2700, 3000, 3300, 3600, 3900, 4200, 4500},
            {1500, 1800, 2100, 2400, 2700, 3000, 3300, 3600, 3900, 4200, 4500},
            {2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988, 7487},
            {2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988, 7487},
            {2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988, 7487},
            {2496, 2995, 3494, 3993, 4492, 4992, 5491, 5990, 6489, 6988, 7487},
            {4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976, 14974},
            {4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976, 14974},
            {4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976, 14974},
            {4992, 5990, 6988, 7986, 8984, 9984, 10982, 11980, 12978, 13976, 14974},
            {3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188, 11987},
            {3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188, 11987},
            {3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188, 11987},
            {3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188, 11987},
            {3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188, 11987},
            {3996, 4795, 5594, 6393, 7192, 7992, 8791, 9590, 10389, 11188, 11987}
        };
        for (size_t i = 0; i < 0/*last_steps.size()*/; i++)
        {
            _Temporary_buffer.clear();
            // load the settings from a file
            fileName = presets_path + "/" + QString::fromStdString(scenarios[i]);
            if (fileName.isEmpty())
            {
                return;
            }
            else
            {
                QFile file(fileName);
                if (!file.open(QIODevice::ReadOnly))
                {
                    QMessageBox::information(this, tr("Unable to open file"), file.errorString());
                    return;
                }
                QTextStream in(&file);
                line = in.readLine();
                list = line.split(" ");
                sim.last_step = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.cell_proliferation_potential_max = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_spontaneous_death_ = list.at(0).toFloat();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_proliferation_ = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_migration_ = list.at(0).toInt();
                line = in.readLine();
                list = line.split(" ");
                sim.chance_STC_creation_ = list.at(0).toInt();
                line = in.readLine();
                if (line == "true")
                    sim.starter_cell_is_STC = true;
                else
                    sim.starter_cell_is_STC = false;
                file.close();
            }
            std::cout << scenarios[i] << " loaded successfully" << std::endl;
            std::ofstream file;
            file.open(output_path.toStdString() + "/MacroResults.txt", std::ios_base::app);
            file << scenarios[i] << "(multiple runs)" << std::endl << "=====================================================\n" << std::endl;
            file.close();

            // repeat the simulation 10 times
            for (size_t j = 0; j < last_steps[i].size(); j++)
            {
                sim.last_step = last_steps[i][j];
                // reset the domain
                sim.simul_time = 0;
                sim.reset_domain();
                // start the timer
                start = std::chrono::system_clock::now();
                // run the simulation
                sim.tumor_cells.push_back(Cell(50, 50));
                if (sim.starter_cell_is_STC)
                    sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max + 1;
                else
                    sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max;
                print_domain();
                simulate(false);
                print_domain_end();
                // stop the timer
                end = std::chrono::system_clock::now();
                duration = end - start;
                _Temporary_buffer += std::to_string(duration.count()) + " ";
            }
            file.open(output_path.toStdString() + "/MacroResults.txt", std::ios_base::app);
            file << _Temporary_buffer << std::endl;
            file << "\n\n" << std::endl;
            file.close();
        }
    }
}

/**
 * @brief MainWindow::on_pushButton_12_clicked
 * @details This function is the slot for the SAVE button. It saves the graphics to a image file.
 * @return void
 *
 * @details This function is the slot for the SAVE button. It saves the graphics to a image file.
 */
void MainWindow::on_pushButton_12_clicked() // SAVE
{
    // saves the graphics to a image file
    QString path = "C:/Users/yeras/Mi unidad (yeray.doegon@alum.uca.es)/Uni/ESI/TFG/Codigo/Secuencial/reduced/C++/TFG/images";
    QString fileName = QFileDialog::getSaveFileName(this, tr("Save File"), path, tr("Images (*.png *.xpm *.jpg)"));
    if (fileName.isEmpty())
    {
        return;
    }
    else
    {
        QImage image(400, 400, QImage::Format_RGB32);
        QPainter painter(&image);
        ui->graphicsView->render(&painter);
        image.save(fileName);
    }
}

void MainWindow::on_pushButton_11_clicked() // ABOUT
{
    QMessageBox::information(this, "About", "This is a simulation of a tumor growth");
}

/**
 * @brief MainWindow::on_pushButton_8_clicked
 * @details This function is the slot for the RESET button. It resets the simulation.
 * @return void
 *
 * @details This function is the slot for the RESET button. It resets the simulation.
 */
void MainWindow::on_pushButton_8_clicked() // RESET
{
    sim.reset_domain();
    sim.simul_time = 0;
    sim.tumor_cells.push_back(Cell(50, 50));
    if (sim.starter_cell_is_STC)
        sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max + 1;
    else
        sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max;
    // update the graphics
    QGraphicsScene *scene = new QGraphicsScene(this);
    ui->graphicsView->setScene(scene);
    scene->setSceneRect(0, 0, 400, 400);

    scene->addRect(0, 0, 400, 400, QPen(Qt::black), QBrush(Qt::white));
    update();
}

/**
 * @brief MainWindow::on_pushButton_7_clicked
 * @details This function is the slot for the PLOT button. It opens a plot window.
 * @return void
 *
 * @details This function is the slot for the PLOT button. It opens a plot window.
 */
void MainWindow::on_pushButton_7_clicked() // PLOT
{
    sim.reset_domain();
    // start the timer
    auto start = std::chrono::system_clock::now();
    sim.tumor_cells.push_back(Cell(50, 50));
    if (sim.starter_cell_is_STC)
        sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max + 1;
    else
        sim.domain[50 + 50 * sim.domain_size] = sim.cell_proliferation_potential_max;
    print_domain();
    simulate(true);
    print_domain_end();
    // stop the timer
    auto end = std::chrono::system_clock::now();
    std::chrono::duration<float, std::milli> duration = end - start;
    // print the time
    std::cout << "Simulation took: " << duration.count() << " ms" << std::endl;

    QCustomPlot *customPlot = new QCustomPlot;
    customPlot->setFixedSize(800, 600);

    QVector<double> x1(sim.STC_count.size());
    QVector<double> RTC_count_(sim.RTC_count.size());
    QVector<double> STC_count_(sim.STC_count.size());
    for (size_t i = 0; i < sim.STC_count.size(); i++)
    {
        x1[i] = i;
        RTC_count_[i] = sim.RTC_count[i];
        STC_count_[i] = sim.STC_count[i];
    }

    QCPCurve *newCurve = new QCPCurve(customPlot->xAxis, customPlot->yAxis);
    newCurve->setData(x1, RTC_count_);
    newCurve->setPen(QPen(Qt::blue));
    newCurve->setScatterStyle(QCPScatterStyle(QCPScatterStyle::ssCircle, 5));
    newCurve->setName("RTC");

    newCurve = new QCPCurve(customPlot->xAxis, customPlot->yAxis);
    newCurve->setData(x1, STC_count_);
    newCurve->setPen(QPen(Qt::red));
    newCurve->setScatterStyle(QCPScatterStyle(QCPScatterStyle::ssCircle, 5));
    newCurve->setName("STC");

    customPlot->legend->setVisible(true);
    customPlot->legend->setFont(QFont("Helvetica", 9));
    customPlot->legend->setBrush(QBrush(QColor(255, 255, 255, 150)));
    customPlot->xAxis->setLabel("Time");
    customPlot->yAxis->setLabel("Cell count");
    customPlot->xAxis->setRange(0, sim.STC_count.size() + round(sim.STC_count.size() / 100));
    int max = std::max(*std::max_element(sim.STC_count.begin(), sim.STC_count.end()), *std::max_element(sim.RTC_count.begin(), sim.RTC_count.end()));
    customPlot->yAxis->setRange(0, pow(2, 1 + trunc(log2(max))));

    customPlot->replot();
    customPlot->show();
    customPlot->replot();
}

/**
 * @brief MainWindow::simulate
 * @param counting
 * @details This function simulates the tumor growth.
 * @return void
 *
 * @details This function simulates the tumor growth.
 */
void MainWindow::simulate(bool counting)
{
    // While time != last_step
    while (sim.simul_time != sim.last_step)
    {
        // run the simulation
        sim.run(counting);
        // print the domain
        MainWindow::print_domain();
    }
}

/**
 * @brief MainWindow::simulate
 * @param counting
 * @details This function simulates the tumor growth.
 * @return void
 *
 * @details This function simulates the tumor growth.
 */
void MainWindow::print_domain()
{
    // set the domain size
    int sizeX = sim.domain_size;
    int sizeY = sim.domain_size;

    // create the image
    QImage image(sizeX, sizeY, QImage::Format_RGB32);

    // fill the image with white
    image.fill(qRgb(255, 255, 255));

    // draw the domain
    for (int i = 0; i < (int)sim.tumor_cells.size(); i++)
    {
        int x = sim.tumor_cells[i].x;
        int y = sim.tumor_cells[i].y;
        if (sim.domain[x + y * sim.domain_size] == sim.cell_proliferation_potential_max + 1) // STC (Yellow)
            image.setPixel(x, y, qRgb(255, 255, 0));
        else if (sim.domain[x + y * sim.domain_size] > 0) // RTC (red to black gradient)
            image.setPixel(x, y, qRgb(255 * sim.domain[x + y * sim.domain_size] / sim.cell_proliferation_potential_max, 0, 0));
    }

    // upscale the image to the graphics view
    image = image.scaled(400, 400, Qt::KeepAspectRatio);

    // create the graphics scene
    QGraphicsScene *scene = new QGraphicsScene(this);

    // set the image to the graphics scene
    scene->addPixmap(QPixmap::fromImage(image));

    // display the graphics scene
    ui->graphicsView->setScene(scene);

    // print number of iterations out of the square
    QGraphicsTextItem *text = scene->addText("Iterations: " + QString::number(sim.simul_time) + " / " + QString::number(sim.last_step), QFont("Arial", 10));
    text->setPos(0, -20);
    ui->graphicsView->viewport()->repaint();
    delete scene;
    image.~QImage();

}

/**
 * @brief MainWindow::print_domain_end
 * @details This function prints the final state of the domain.
 * @return void
 *
 * @details This function prints the final state of the domain.
 */
void MainWindow::print_domain_end()
{
    // set the domain size
    int sizeX = sim.domain_size;
    int sizeY = sim.domain_size;

    // create the image
    QImage image(sizeX, sizeY, QImage::Format_RGB32);

    // fill the image with white
    image.fill(qRgb(255, 255, 255));

    // draw the domain
    for (int i = 0; i < (int)sim.tumor_cells.size(); i++)
    {
        int x = sim.tumor_cells[i].x;
        int y = sim.tumor_cells[i].y;
        if (sim.domain[x + y * sim.domain_size] == sim.cell_proliferation_potential_max + 1) // STC (Yellow)
            image.setPixel(x, y, qRgb(255, 255, 0));
        else if (sim.domain[x + y * sim.domain_size] > 0) // RTC (red to black gradient)
            image.setPixel(x, y, qRgb(255 * sim.domain[x + y * sim.domain_size] / sim.cell_proliferation_potential_max, 0, 0));
    }

    // upscale the image to the graphics view
    image = image.scaled(400, 400, Qt::KeepAspectRatio);

    // create the graphics scene
    QGraphicsScene *scene = new QGraphicsScene(this);

    // set the image to the graphics scene
    scene->addPixmap(QPixmap::fromImage(image));

    // display the graphics scene
    ui->graphicsView->setScene(scene);

    // print number of iterations out of the square
    QGraphicsTextItem *text = scene->addText("Iterations: " + QString::number(sim.simul_time) + " / " + QString::number(sim.last_step), QFont("Arial", 10));
    text->setPos(0, -20);
    ui->graphicsView->viewport()->repaint();
    delete scene;
    image.~QImage();
}
