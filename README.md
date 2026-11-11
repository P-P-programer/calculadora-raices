# Calculadora de raíces

Aplicación de escritorio Java Swing organizada con el patrón MVC y construida
con Maven. La interfaz inicial permite escoger entre:

- **Raíces múltiples**: usará `f(x)`, `f'(x)`, `f''(x)` y un valor inicial.
- **Método de Brent**: usará `f(x)` y un intervalo `[a, b]`.

La lógica numérica todavía está separada de la interfaz y se incorporará en la
siguiente etapa.

La ventana incluye una sección de ejercicios rápidos para cargar un ejemplo
de cada método. El ejemplo de Brent usa `f(x) = x^2 - 4` en `[0, 3]`; el de
raíces múltiples usa `f(x) = (x - 2)^3` con sus dos derivadas y `x0 = 3`.

## Calcular

El botón **Calcular** ya ejecuta ambos métodos. Las expresiones aceptan:

- Operadores `+`, `-`, `*`, `/` y `^`.
- Multiplicación implícita, como `3(x - 2)^2`, `2x` y `x(x + 1)`.
- Variable `x`, constantes `pi` y `e`.
- Funciones `sin`, `cos`, `tan`, `sqrt`, `abs`, `exp`, `ln` y `log`.

También se pueden escribir con etiquetas descriptivas, por ejemplo
`f(x) = (x - 2)^3`, `f'(x) = 3*(x - 2)^2` y
`f''(x) = 6*(x - 2)`.

Para raíces múltiples, escribe cada expresión en una línea:

```text
(x - 2)^3
f'(x) = 3*(x - 2)^2
f''(x) = 6*(x - 2)
```

El resultado muestra la raíz aproximada, el número de iteraciones y el
mensaje de convergencia. Los errores de sintaxis, intervalos sin cambio de
signo y valores inválidos se muestran en el área de resultado.

La ventana también incluye un plano cartesiano hecho con Java2D. Después de
calcular, muestra la curva de `f(x)` y marca la raíz encontrada en rojo.
Puedes mover el mouse sobre el plano para consultar coordenadas y usar la
rueda para acercar o alejar la vista.

## Estructura

```text
src/main/java/com/raicesmultiples/
├── Main.java
├── controller/
│   └── MainController.java
├── model/
│   ├── BrentSolver.java
│   ├── CalculationMethod.java
│   ├── CalculatorModel.java
│   ├── Expression.java
│   ├── ExpressionParser.java
│   ├── MultipleRootsSolver.java
│   └── SolverResult.java
└── view/
    ├── CartesianPlotPanel.java
    └── MainView.java
```

## Ejecutar

Requiere Java 17 o superior y Maven:

```bash
mvn clean compile
mvn exec:java
```

## Publicar una versión portable

El workflow `.github/workflows/release.yml` crea paquetes portables con
`jpackage --type app-image`. El paquete de Windows contiene el `.exe` y su
runtime Java, por lo que el usuario no necesita instalar Java. Al publicar un
tag con formato `v*`, por ejemplo:

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions compila en Windows y Linux, comprime cada aplicación y adjunta
los siguientes archivos a una GitHub Release:

```text
CalculadoraRaices-v1.0.0-windows.zip
CalculadoraRaices-v1.0.0-linux.tar.gz
```

En Windows, descomprime el ZIP y ejecuta:

```text
CalculadoraRaices/CalculadoraRaices.exe
```
