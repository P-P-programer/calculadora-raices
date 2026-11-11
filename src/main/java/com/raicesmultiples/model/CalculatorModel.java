package com.raicesmultiples.model;

public class CalculatorModel {
    private CalculationMethod selectedMethod = CalculationMethod.MULTIPLE_ROOTS;

    public CalculationMethod getSelectedMethod() {
        return selectedMethod;
    }

    public void setSelectedMethod(CalculationMethod selectedMethod) {
        if (selectedMethod == null) {
            throw new IllegalArgumentException("El método seleccionado no puede ser nulo.");
        }
        this.selectedMethod = selectedMethod;
    }
}
