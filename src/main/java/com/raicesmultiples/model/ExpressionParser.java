package com.raicesmultiples.model;

public final class ExpressionParser {
    private final String text;
    private int position;

    private ExpressionParser(String text) {
        this.text = text.replace(" ", "").replace(",", ".");
    }

    public static Expression parse(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("La función no puede estar vacía.");
        }
        int equalsIndex = expression.indexOf('=');
        if (equalsIndex >= 0) {
            expression = expression.substring(equalsIndex + 1);
        }
        ExpressionParser parser = new ExpressionParser(expression);
        Expression result = parser.parseExpression();
        if (parser.position != parser.text.length()) {
            throw parser.error("símbolo inesperado");
        }
        return result;
    }

    private Expression parseExpression() {
        Expression result = parseTerm();
        while (hasNext() && (peek() == '+' || peek() == '-')) {
            char operator = next();
            Expression right = parseTerm();
            Expression left = result;
            result = operator == '+' ? x -> left.evaluate(x) + right.evaluate(x)
                    : x -> left.evaluate(x) - right.evaluate(x);
        }
        return result;
    }

    private Expression parseTerm() {
        Expression result = parsePower();
        while (hasNext() && (peek() == '*' || peek() == '/' || startsImplicitFactor())) {
            char operator = startsImplicitFactor() ? '*' : next();
            Expression right = parsePower();
            Expression left = result;
            result = operator == '*' ? x -> left.evaluate(x) * right.evaluate(x)
                    : x -> left.evaluate(x) / right.evaluate(x);
        }
        return result;
    }

    private boolean startsImplicitFactor() {
        if (!hasNext()) {
            return false;
        }
        char nextCharacter = peek();
        return nextCharacter == '(' || nextCharacter == '.'
                || Character.isDigit(nextCharacter) || Character.isLetter(nextCharacter);
    }

    private Expression parsePower() {
        Expression base = parseUnary();
        if (hasNext() && peek() == '^') {
            next();
            Expression exponent = parsePower();
            return x -> Math.pow(base.evaluate(x), exponent.evaluate(x));
        }
        return base;
    }

    private Expression parseUnary() {
        if (hasNext() && (peek() == '+' || peek() == '-')) {
            boolean negative = next() == '-';
            Expression operand = parseUnary();
            return negative ? x -> -operand.evaluate(x) : operand;
        }
        return parsePrimary();
    }

    private Expression parsePrimary() {
        if (!hasNext()) {
            throw error("se esperaba un valor");
        }
        if (peek() == '(') {
            next();
            Expression result = parseExpression();
            require(')');
            return result;
        }
        if (Character.isDigit(peek()) || peek() == '.') {
            return constant(parseNumber());
        }
        String name = parseName();
        if (name.equals("x")) {
            return x -> x;
        }
        if (name.equals("pi")) {
            return constant(Math.PI);
        }
        if (name.equals("e")) {
            return constant(Math.E);
        }
        require('(');
        Expression argument = parseExpression();
        require(')');
        return function(name, argument);
    }

    private Expression function(String name, Expression argument) {
        return switch (name) {
            case "sin" -> x -> Math.sin(argument.evaluate(x));
            case "cos" -> x -> Math.cos(argument.evaluate(x));
            case "tan" -> x -> Math.tan(argument.evaluate(x));
            case "sqrt" -> x -> Math.sqrt(argument.evaluate(x));
            case "abs" -> x -> Math.abs(argument.evaluate(x));
            case "exp" -> x -> Math.exp(argument.evaluate(x));
            case "ln", "log" -> x -> Math.log(argument.evaluate(x));
            default -> throw error("función no reconocida: " + name);
        };
    }

    private double parseNumber() {
        int start = position;
        while (hasNext() && (Character.isDigit(peek()) || peek() == '.')) {
            position++;
        }
        try {
            return Double.parseDouble(text.substring(start, position));
        } catch (NumberFormatException exception) {
            throw error("número inválido");
        }
    }

    private String parseName() {
        int start = position;
        while (hasNext() && Character.isLetter(peek())) {
            position++;
        }
        return text.substring(start, position).toLowerCase();
    }

    private void require(char expected) {
        if (!hasNext() || next() != expected) {
            throw error("se esperaba '" + expected + "'");
        }
    }

    private Expression constant(double value) {
        return ignored -> value;
    }

    private char peek() {
        return text.charAt(position);
    }

    private char next() {
        return text.charAt(position++);
    }

    private boolean hasNext() {
        return position < text.length();
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException("Error en la función, posición " + position + ": " + message);
    }
}
