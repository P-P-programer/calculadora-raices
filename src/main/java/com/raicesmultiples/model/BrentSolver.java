package com.raicesmultiples.model;

public final class BrentSolver {
    private BrentSolver() {
    }

    public static SolverResult solve(Expression function, double lower, double upper,
                                     double tolerance, int maxIterations) {
        if (!(tolerance > 0) || maxIterations < 1) {
            throw new IllegalArgumentException("La tolerancia y las iteraciones deben ser positivas.");
        }
        double a = lower;
        double b = upper;
        double fa = function.evaluate(a);
        double fb = function.evaluate(b);
        if (!Double.isFinite(fa) || !Double.isFinite(fb)) {
            throw new IllegalArgumentException("La función no es finita en los extremos del intervalo.");
        }
        if (fa == 0) {
            return new SolverResult(a, 0, "El extremo inferior ya es una raíz.");
        }
        if (fb == 0) {
            return new SolverResult(b, 0, "El extremo superior ya es una raíz.");
        }
        if (Math.signum(fa) == Math.signum(fb)) {
            throw new IllegalArgumentException("f(a) y f(b) deben tener signos opuestos.");
        }

        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            double candidate;
            if (fb != fa) {
                candidate = b - fb * (b - a) / (fb - fa);
            } else {
                candidate = (a + b) / 2;
            }
            if (candidate <= Math.min(a, b) || candidate >= Math.max(a, b)
                    || !Double.isFinite(candidate)) {
                candidate = (a + b) / 2;
            }
            double fc = function.evaluate(candidate);
            if (!Double.isFinite(fc)) {
                throw new IllegalArgumentException("La función dejó de ser finita durante el cálculo.");
            }
            if (Math.abs(fc) <= tolerance || Math.abs(b - a) <= tolerance) {
                return new SolverResult(candidate, iteration, "Convergencia alcanzada con tolerancia " + tolerance + ".");
            }
            if (Math.signum(fa) != Math.signum(fc)) {
                b = candidate;
                fb = fc;
            } else {
                a = candidate;
                fa = fc;
            }
            if (Math.abs(b - a) <= tolerance) {
                return new SolverResult((a + b) / 2, iteration,
                        "Convergencia alcanzada con tolerancia " + tolerance + ".");
            }
            if (iteration == maxIterations) {
                break;
            }
            if (Math.abs(fa) < Math.abs(fb)) {
                double temporaryValue = a;
                a = b;
                b = temporaryValue;
                double temporaryFunction = fa;
                fa = fb;
                fb = temporaryFunction;
            }
        }
        throw new IllegalArgumentException("No hubo convergencia en " + maxIterations + " iteraciones.");
    }
}
