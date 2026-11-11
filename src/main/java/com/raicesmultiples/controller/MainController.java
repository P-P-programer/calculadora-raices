package com.raicesmultiples.controller;

import com.raicesmultiples.model.BrentSolver;
import com.raicesmultiples.model.CalculationMethod;
import com.raicesmultiples.model.CalculatorModel;
import com.raicesmultiples.model.Expression;
import com.raicesmultiples.model.ExpressionParser;
import com.raicesmultiples.model.MultipleRootsSolver;
import com.raicesmultiples.model.SolverResult;
import com.raicesmultiples.view.MainView;

public class MainController {
    private final CalculatorModel model;
    private final MainView view;

    public MainController(CalculatorModel model, MainView view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        view.setMethodSelectionListener(event -> {
            CalculationMethod method = view.getSelectedMethod();
            model.setSelectedMethod(method);
            view.showMethodInstructions(method);
        });
        view.setCalculateListener(event -> calculate());
        view.setClearListener(event -> view.clearInputs());
        view.setBrentExampleListener(event -> view.loadBrentExample());
        view.setMultipleRootsExampleListener(event -> view.loadMultipleRootsExample());
        view.showMethodInstructions(model.getSelectedMethod());
        view.show();
    }

    private void calculate() {
        try {
            double first = parseNumber(view.getFirstValueText(), "valor inicial o a");
            double tolerance = parseNumber(view.getToleranceText(), "tolerancia");
            Expression function = ExpressionParser.parse(
                    removeLabel(view.getFunctionText().split("\\R")[0]));
            SolverResult result;
            if (model.getSelectedMethod() == CalculationMethod.BRENT) {
                double second = parseNumber(view.getSecondValueText(), "valor final o b");
                result = BrentSolver.solve(function, first, second, tolerance, 100);
            } else {
                String[] lines = view.getFunctionText().split("\\R");
                if (lines.length < 3) {
                    throw new IllegalArgumentException(
                            "Para raíces múltiples ingresa f(x), f'(x) y f''(x), cada una en una línea.");
                }
                Expression firstDerivative = ExpressionParser.parse(removeLabel(lines[1]));
                Expression secondDerivative = ExpressionParser.parse(removeLabel(lines[2]));
                result = MultipleRootsSolver.solve(function, firstDerivative, secondDerivative,
                        first, tolerance, 100);
            }
            view.showCalculationResult(result);
            view.showPlot(function, result, first);
        } catch (IllegalArgumentException exception) {
            view.showCalculationError(exception.getMessage());
        }
    }

    private double parseNumber(String value, String fieldName) {
        try {
            double number = Double.parseDouble(value.trim().replace(',', '.'));
            if (!Double.isFinite(number)) {
                throw new NumberFormatException();
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El " + fieldName + " debe ser un número válido.");
        }
    }

    private String removeLabel(String line) {
        int equalsIndex = line.indexOf('=');
        return equalsIndex >= 0 ? line.substring(equalsIndex + 1) : line;
    }
}
