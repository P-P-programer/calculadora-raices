package com.raicesmultiples.model;

public enum CalculationMethod {
    MULTIPLE_ROOTS(
            "Raíces múltiples",
            "Este método requiere f(x), f'(x), f''(x), un valor inicial y una tolerancia."),
    BRENT(
            "Método de Brent",
            "Este método requiere f(x), un intervalo [a, b] que encierre la raíz y una tolerancia.");

    private final String displayName;
    private final String instruction;

    CalculationMethod(String displayName, String instruction) {
        this.displayName = displayName;
        this.instruction = instruction;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
