package com.raicesmultiples.model;

public final class MultipleRootsSolver {
    private MultipleRootsSolver() {
    }

    public static SolverResult solve(Expression function, Expression firstDerivative,
                                     Expression secondDerivative, double initial,
                                     double tolerance, int maxIterations) {
        if (!(tolerance > 0) || maxIterations < 1) {
            throw new IllegalArgumentException("La tolerancia y las iteraciones deben ser positivas.");
        }
        double x = initial;
        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            double fx = function.evaluate(x);
            double f1 = firstDerivative.evaluate(x);
            double f2 = secondDerivative.evaluate(x);
            double denominator = f1 * f1 - fx * f2;
            if (!Double.isFinite(fx) || !Double.isFinite(denominator)) {
                throw new IllegalArgumentException("La función produjo un valor no válido.");
            }
            if (Math.abs(denominator) < 1e-15) {
                throw new IllegalArgumentException("El denominador se aproxima a cero en x = " + x + ".");
            }
            double next = x - (fx * f1) / denominator;
            if (!Double.isFinite(next)) {
                throw new IllegalArgumentException("El cálculo produjo un valor no válido.");
            }
            if (Math.abs(next - x) < tolerance || Math.abs(function.evaluate(next)) < tolerance) {
                return new SolverResult(next, iteration, "Convergencia alcanzada con tolerancia " + tolerance + ".");
            }
            x = next;
        }
        throw new IllegalArgumentException("No hubo convergencia en " + maxIterations + " iteraciones.");
    }
}
