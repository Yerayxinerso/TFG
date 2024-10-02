#include "mainwindow.h"
#include <QApplication>
#include <QLocale>
#include <QTranslator>

/**
    * @brief main   
    * @param argc
    * @param argv
    * @return int
    
    * @details This function is the main function of the program. It creates the main window and shows it.
 */
int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    QTranslator translator;
    const QStringList uiLanguages = QLocale::system().uiLanguages();
    for (const QString &locale : uiLanguages)
    {
        const QString baseName = "TFG_" + QLocale(locale).name();
        if (translator.load(":/i18n/" + baseName))
        {
            a.installTranslator(&translator);
            break;
        }
    }
    MainWindow w;
    w.show();
    return a.exec();
}
