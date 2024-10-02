QT += core gui
QT += charts
QT += printsupport
QT += core
LIBS += -lpthread

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++17

# You can make your code fail to compile if it uses deprecated APIs.
# In order to do so, uncomment the following line.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    main.cpp \
    mainwindow.cpp \
    qcustomplot.cpp \
    settingswindow.cpp \
    simulation.cpp

HEADERS += \
    mainwindow.h \
    qcustomplot.h \
    settingswindow.h \
    simulation.h

FORMS += \
    mainwindow.ui \
    settingswindow.ui

TRANSLATIONS += \
    TFG_es_ES.ts
CONFIG += lrelease
CONFIG += embed_translations

QMAKE_CXXFLAGS += -Wa,-mbig-obj


# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target

# Configuraciones de optimización

# Sin optimización (Debug)
debug {
    QMAKE_CXXFLAGS_DEBUG += -O0
}

# Optimización mínima
release {
    QMAKE_CXXFLAGS_RELEASE += -O1
}

# Optimización media
release {
    QMAKE_CXXFLAGS_RELEASE += -O2
}

# Optimización máxima
release {
    QMAKE_CXXFLAGS_RELEASE += -O3
}

# Optimización para tamaño
release {
    QMAKE_CXXFLAGS_RELEASE += -Os
}

# Optimización agresiva
release {
    QMAKE_CXXFLAGS_RELEASE += -Ofast
}

# Desenrollado de bucles
release {
    QMAKE_CXXFLAGS_RELEASE += -funroll-loops
}

# Expansión en línea de funciones
release {
    QMAKE_CXXFLAGS_RELEASE += -finline-functions
}
