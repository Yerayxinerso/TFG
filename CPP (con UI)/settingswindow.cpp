/**
 * @file settingswindow.cpp
 * @author Yeray Doello González
 * @brief This file contains the implementation of the settingswindow class.
 * @version 0.1
 * @date 2024-04-10
 *
 * @copyright Copyright (c) 2024
 *
 */

#include <QMessageBox>
#include <QFile>
#include <QTextStream>
#include <QFileDialog>
#include <QDateTime>
#include <iostream>
#include <fstream>
#include "settingswindow.h"

/**
 * @brief Construct a new settingswindow::settingswindow object
 *
 * @param parent
 * @param sim
 *
 * @details This function is the constructor of the settingswindow class.
 */
settingswindow::settingswindow(QWidget *parent, Simulation *sim) : parent(parent),
                                                                   sim(sim),
                                                                   ui(new Ui::settingswindow)
{
    ui->setupUi(this);
    ui->lineEdit_5->setText(QString::number(sim->last_step));
    ui->lineEdit_2->setText(QString::number(sim->cell_proliferation_potential_max));
    ui->lineEdit_6->setText(QString::number(sim->chance_spontaneous_death_ * 24));
    ui->lineEdit_3->setText(QString::number(sim->chance_proliferation_));
    ui->lineEdit->setText(QString::number(sim->chance_migration_));
    ui->lineEdit_4->setText(QString::number(sim->chance_STC_creation_));
    ui->checkBox->setChecked(sim->starter_cell_is_STC);
}

/**
 * @brief Destroy the settingswindow::settingswindow object
 *
 * @details This function is the destructor of the settingswindow class.
 */
settingswindow::~settingswindow()
{
    delete ui;
}

/**
 * @brief This function is called when the Save button is clicked.
 *
 * @details This function saves the settings to a file.
 */
void settingswindow::on_SaveButton_clicked()
{
    // get proyect_path/presets
    std::filesystem::path route = std::filesystem::current_path();
    QString presets_path = QString::fromStdString(route.string());
    presets_path = presets_path.left(presets_path.lastIndexOf("\\"));
    presets_path += "\\TFG\\presets";

    QString fileName = QFileDialog::getSaveFileName(this, tr("Save File"), presets_path, tr("settings files (*.settings)"));
    if (fileName.isEmpty())
    {
        return;
    }
    else
    {
        QFile file(fileName);
        if (!file.open(QIODevice::WriteOnly))
        {
            QMessageBox::information(this, tr("Unable to open file"), file.errorString());
            return;
        }
        QTextStream out(&file);
        out << ui->lineEdit_5->text() << "\n";
        out << ui->lineEdit_2->text() << "\n";
        out << ui->lineEdit_6->text() << "\n";
        out << ui->lineEdit_3->text() << "\n";
        out << ui->lineEdit->text() << "\n";
        out << ui->lineEdit_4->text() << "\n";
        if (ui->checkBox->isChecked())
            out << "true\n";
        else
            out << "false\n";
        file.close();
    }
}

/**
 * @brief This function is called when the Load button is clicked.
 *
 * @details This function loads the settings from a file.
 */
void settingswindow::on_LoadButton_clicked()
{
    // get proyect_path/presets
    std::filesystem::path route = std::filesystem::current_path();
    QString presets_path = QString::fromStdString(route.string());
    presets_path = presets_path.left(presets_path.lastIndexOf("\\"));
    presets_path += "\\TFG\\presets";

    // load the settings from a file
    QString fileName = QFileDialog::getOpenFileName(this, tr("Open File"), presets_path, tr("settings files (*.settings)"));
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
        QString line = in.readLine();
        QStringList list = line.split(" ");
        ui->lineEdit_5->setText(list.at(0));
        line = in.readLine();
        list = line.split(" ");
        ui->lineEdit_2->setText(list.at(0));
        line = in.readLine();
        list = line.split(" ");
        ui->lineEdit_6->setText(list.at(0));
        line = in.readLine();
        list = line.split(" ");
        ui->lineEdit_3->setText(list.at(0));
        line = in.readLine();
        list = line.split(" ");
        ui->lineEdit->setText(list.at(0));
        line = in.readLine();
        list = line.split(" ");
        ui->lineEdit_4->setText(list.at(0));
        line = in.readLine();
        if (line == "true")
            ui->checkBox->setChecked(true);
        else
            ui->checkBox->setChecked(false);
        file.close();
    }
    std::cout << "Settings loaded successfully" << std::endl;
}

/**
 * @brief This function is called when the Exit button is clicked.
 *
 * @details This function closes the settings window.
 */
void settingswindow::on_ExitButton_clicked()
{
    this->close();
}

/**
 * @brief This function is called when the Help button is clicked.
 *
 * @details This function shows a help message.
 */
void settingswindow::on_helpButton_clicked()
{
    QMessageBox::information(this, "Help", "This is a help message");
}

/**
 * @brief This function is called when the Default button is clicked.
 *
 * @details This function sets the default values.
 */
void settingswindow::on_DefaultButton_clicked()
{
    // set the default values
    ui->lineEdit_5->setText("100");
    ui->lineEdit_2->setText("20");
    ui->lineEdit_6->setText("1");
    ui->lineEdit_3->setText("90");
    ui->lineEdit->setText("90");
    ui->lineEdit_4->setText("90");
    ui->checkBox->setChecked(true);
}

/**
 * @brief This function is called when the Apply button is clicked.
 *
 * @details This function applies the settings.
 */
void settingswindow::on_ApplyButton_clicked()
{
    // apply the settings
    this->sim->last_step = ui->lineEdit_5->text().toInt();                        // number of iterations
    this->sim->cell_proliferation_potential_max = ui->lineEdit_2->text().toInt(); // cell proliferation potential max
    this->sim->chance_spontaneous_death_ = ui->lineEdit_6->text().toFloat() / 24; // chance spontaneous death
    this->sim->chance_proliferation_ = ui->lineEdit_3->text().toInt();            // chance proliferation
    this->sim->chance_migration_ = ui->lineEdit->text().toInt();                  // chance migration
    this->sim->chance_STC_creation_ = ui->lineEdit_4->text().toInt();             // chance STC creation
    this->sim->starter_cell_is_STC = ui->checkBox->isChecked();                   // Starter cell is STC
    this->close();
}
