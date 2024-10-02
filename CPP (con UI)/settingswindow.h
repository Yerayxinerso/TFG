/**
 * @file settingswindow.h
 * @author Yeray Doello González
 * @brief This file contains the declaration of the settingswindow class.
 * @version 0.1
 * @date 2024-04-10
 * 
 * @copyright Copyright (c) 2024
 * 
 */

#ifndef SETTINGSWINDOW_H
#define SETTINGSWINDOW_H

#include <QDialog>
#include <QVBoxLayout>
#include "simulation.h"
#include "ui_settingswindow.h"

namespace Ui {
class settingswindow;

}

class settingswindow : public QDialog
{
    Q_OBJECT

public:
    explicit settingswindow(QWidget *parent = nullptr, Simulation *sim = nullptr);
    ~settingswindow();

    QWidget *parent;
    Simulation *sim;

private slots:
    void on_SaveButton_clicked();
    void on_LoadButton_clicked();
    void on_ExitButton_clicked();
    void on_helpButton_clicked();
    void on_DefaultButton_clicked();
    void on_ApplyButton_clicked();

private:
    Ui::settingswindow *ui;
};

#endif // SETTINGSWINDOW_H
