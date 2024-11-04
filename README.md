# Paralelización de Modelos de Simulación de Crecimiento Tumoral

## Descripción

Este proyecto aborda el desarrollo de una herramienta de simulación de crecimiento tumoral altamente especializada y eficiente, diseñada para funcionar en entornos paralelos. La simulación permite realizar cálculos complejos y ejecutar grandes simulaciones que contribuyen a la investigación y tratamiento del cáncer.

El software implementado en este proyecto utiliza modelos recientes y avanzados de simulación tumoral, optimizados para aprovechar recursos de cómputo paralelos (como múltiples núcleos). Además, incluye una interfaz gráfica y herramientas de visualización para facilitar la interpretación de los resultados.

## Motivación

El cáncer sigue siendo una de las enfermedades más desafiantes de tratar y entender. La simulación del crecimiento tumoral ofrece una herramienta poderosa para analizar el comportamiento del tumor y predecir su evolución bajo distintos tratamientos y condiciones ambientales. Sin embargo, el escalamiento de estas simulaciones sigue siendo un reto debido a la gran cantidad de recursos computacionales necesarios. Este proyecto tiene como objetivo crear una solución escalable y eficiente que permita simular y analizar el crecimiento tumoral de manera accesible y precisa.

## Características

- **Simulación de crecimiento tumoral en entornos paralelos:** Optimización y paralelización de modelos computacionales para manejar grandes volúmenes de datos y mejorar la eficiencia.
- **Interfaz gráfica intuitiva:** Herramientas de visualización avanzadas que permiten representar gráficamente los resultados de las simulaciones.
- **Escalabilidad y rendimiento:** Algoritmos optimizados para utilizar eficientemente recursos computacionales paralelos.
- **Compatible con entornos de investigación y clínicos:** Validación del software en estos entornos para asegurar su precisión y aplicabilidad.

## Tecnologías utilizadas

- **Lenguajes de programación:** Java (para la interfaz gráfica) y C++ (para el rendimiento en cálculos complejos).
- **Frameworks y bibliotecas:** 
  - **Qt Creator** para el desarrollo de interfaces gráficas en C++.
  - **Java Swing** para la interfaz de usuario en Java.
  - **QCustomPlot** y **JFreeChart** para la visualización de resultados.
- **Paralelización:** OpenMP y MPI para mejorar la escalabilidad y distribución de tareas.

## Estructura del Repositorio

El repositorio se encuentra estructurado en diferentes carpetas que representan las distintas implementaciones y escenarios. A continuación, se detalla cada sección:

- **CPP (con UI)**: Contiene la implementación en C++ con interfaz gráfica (UI) utilizando *Qt*. Esta carpeta incluye:
  - `main.cpp`, `mainwindow.cpp`, `mainwindow.h`: Ficheros principales de la lógica de la UI.
  - `qcustomplot.cpp`, `qcustomplot.h`: Ficheros para gráficos personalizados en la interfaz.
  - `presets/`: Archivos de configuración predefinidos que permiten cargar escenarios de simulación específicos.
    
- **CPP (sin UI)**: Implementación en C++ sin interfaz gráfica, dividida en subcarpetas basadas en las técnicas de sincronización y tipos de datos utilizados (e.g., `char_mutex`, `int_semaphore`). Estas variaciones permiten comparar el rendimiento y comportamiento del sistema bajo diferentes configuraciones.

- **Java (con UI)**: Implementación en Java con una interfaz gráfica para simular y visualizar el crecimiento tumoral. Esta sección se organiza en:
  - `UI.java`: Código fuente principal de la interfaz.
  - `presets/`: Escenarios de configuración que se pueden cargar desde la UI.
    
- **Java (sin UI)**: Incluye diversas versiones de la implementación sin interfaz gráfica, diferenciadas por el uso de mecanismos de sincronización como `synchronized` y `ReentrantLock`, y el tipo de dato utilizado (e.g., `byte`, `char`, `int`). Estas variaciones facilitan el análisis del rendimiento bajo distintas configuraciones, en línea con las mejores prácticas de concurrencia en Java.

## Instalación

1. **Requisitos previos**:
   - Java (versión mínima recomendada: 11)
   - C++ (con soporte para OpenMP/MPI)
   - Qt Creator y Java Swing para la interfaz gráfica
   - CMake para la compilación del proyecto en C++

2. **Clonación del repositorio**:
   ```bash
   git clone https://github.com/usuario/proyecto-simulacion-tumoral.git
   cd proyecto-simulacion-tumoral```

3. **Compilación**:
   - Para la parte de C++:
     - Sin IU.
     ```bash
     g++ task.h task.cpp main.cpp -std=c++2a -o main.exe
     ```
      - Con IU.
    ```
    Desde QtCreator
    ```
   - Para la parte de Java:
     - Sin IU.
     ```bash
     javac nombre_del_archivo.java
     ```
      - Con IU.
     ```bash
     javac -cp jcommon-1.0.23.jar jfreechart-1.0.19.jar UI.java
     ```

## Uso
 - Para C++:
1. **Ejecuta el binario compilado en C++ para iniciar la simulación.**
   - Para Java (
   Dependiendo de si es la versión con o sin IU.):
1. ```bash
     Java UI
     ```
     o
     ```bash
     Java nombre_del_archivo
     ```

2. **A través de la interfaz, selecciona las configuraciones de la simulación, ejecuta y visualiza los resultados.**
