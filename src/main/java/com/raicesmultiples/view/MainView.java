package com.raicesmultiples.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import com.raicesmultiples.model.CalculationMethod;
import com.raicesmultiples.model.Expression;
import com.raicesmultiples.model.SolverResult;

public class MainView {
    private static final Color BACKGROUND = new Color(248, 245, 252);
    private static final Color SURFACE = Color.WHITE;
    private static final Color PRIMARY = new Color(108, 63, 160);
    private static final Color PRIMARY_DARK = new Color(67, 38, 95);
    private static final Color PRIMARY_LIGHT = new Color(233, 221, 244);
    private static final Color TEXT = new Color(41, 35, 47);

    private final JFrame frame = new JFrame("Calculadora de raíces");
    private final JComboBox<CalculationMethod> methodSelector =
            new JComboBox<>(CalculationMethod.values());
    private final JLabel instructionLabel = new JLabel();
    private final JTextArea functionField = new JTextArea(2, 30);
    private final JTextField firstValueField = new JTextField();
    private final JTextField secondValueField = new JTextField();
    private final JTextField toleranceField = new JTextField("0.000001");
    private final JTextArea resultArea = new JTextArea(8, 30);
    private final JButton calculateButton = new JButton("Calcular");
    private final JButton clearButton = new JButton("Limpiar");
    private final JButton brentExampleButton = new JButton("Cargar ejemplo de Brent");
    private final JButton multipleRootsExampleButton = new JButton("Cargar ejemplo de raíces múltiples");
    private final CartesianPlotPanel plotPanel = new CartesianPlotPanel();

    public MainView() {
        frame.setTitle("Calculadora de raíces");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new java.awt.Dimension(900, 820));
        frame.setLocationByPlatform(true);
        frame.getContentPane().setBackground(BACKGROUND);
        frame.add(createContent());
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.add(createHeader(), BorderLayout.NORTH);
        content.add(createInputPanel(), BorderLayout.CENTER);
        content.add(createExamplesPanel(), BorderLayout.EAST);
        content.add(createBottomPanel(), BorderLayout.SOUTH);
        return content;
    }

    private JPanel createPlotPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_LIGHT),
                "Plano cartesiano (rueda del mouse: zoom)",
                javax.swing.border.TitledBorder.LEADING,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                null,
                PRIMARY_DARK));
        panel.add(plotPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(createPlotPanel(), BorderLayout.CENTER);
        panel.add(createFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setBackground(BACKGROUND);
        JLabel title = new JLabel("Calculadora de métodos numéricos");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(PRIMARY_DARK);
        instructionLabel.setForeground(PRIMARY);
        header.add(title, BorderLayout.NORTH);
        header.add(instructionLabel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_LIGHT), "Datos de entrada",
                javax.swing.border.TitledBorder.LEADING,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PRIMARY_DARK));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;

        addField(panel, constraints, 0, "Método:", methodSelector);
        addField(panel, constraints, 1, "Función f(x):", new JScrollPane(functionField));

        JPanel values = new JPanel(new GridLayout(1, 2, 8, 0));
        values.add(labeledField("Valor inicial / a:", firstValueField));
        values.add(labeledField("Valor final / b:", secondValueField));
        addField(panel, constraints, 2, "Valores:", values);
        addField(panel, constraints, 3, "Tolerancia:", toleranceField);

        resultArea.setEditable(false);
        resultArea.setForeground(TEXT);
        resultArea.setBackground(SURFACE);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setText("Aquí aparecerán el resultado, las iteraciones y los mensajes de validación.");
        addField(panel, constraints, 4, "Resultado:", new JScrollPane(resultArea));
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints constraints, int row,
                          String label, java.awt.Component component) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        panel.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.weighty = row == 4 ? 1 : 0;
        constraints.fill = row == 4 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        panel.add(component, constraints);
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(BACKGROUND);
        field.setForeground(TEXT);
        field.setBackground(SURFACE);
        field.setCaretColor(PRIMARY);
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        footer.setBackground(BACKGROUND);
        clearButton.setBackground(PRIMARY_LIGHT);
        clearButton.setForeground(PRIMARY_DARK);
        clearButton.setFocusPainted(false);
        calculateButton.setBackground(PRIMARY);
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        footer.add(clearButton);
        footer.add(calculateButton);
        return footer;
    }

    private JPanel createExamplesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_LIGHT), "Vista rápida: ejercicios",
                javax.swing.border.TitledBorder.LEADING,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PRIMARY_DARK));
        panel.setPreferredSize(new java.awt.Dimension(270, 0));

        JTextArea examples = new JTextArea(
                "Brent\n"
                        + "f(x) = x^2 - 4\n"
                        + "Intervalo: [0, 3]\n"
                        + "Raíz esperada: x = 2\n\n"
                        + "Raíces múltiples\n"
                        + "f(x) = (x - 2)^3\n"
                        + "x0 = 3\n"
                        + "f'(x) = 3(x - 2)^2\n"
                        + "f''(x) = 6(x - 2)\n"
                        + "Raíz esperada: x = 2");
        examples.setEditable(false);
        examples.setLineWrap(true);
        examples.setWrapStyleWord(true);
        examples.setBackground(panel.getBackground());
        examples.setForeground(TEXT);
        panel.add(new JScrollPane(examples), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 4, 4));
        buttons.setBackground(BACKGROUND);
        for (JButton button : new JButton[]{brentExampleButton, multipleRootsExampleButton}) {
            button.setBackground(PRIMARY_LIGHT);
            button.setForeground(PRIMARY_DARK);
            button.setFocusPainted(false);
        }
        buttons.add(brentExampleButton);
        buttons.add(multipleRootsExampleButton);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    public CalculationMethod getSelectedMethod() {
        return (CalculationMethod) methodSelector.getSelectedItem();
    }

    public String getFunctionText() {
        return functionField.getText();
    }

    public String getFirstValueText() {
        return firstValueField.getText();
    }

    public String getSecondValueText() {
        return secondValueField.getText();
    }

    public String getToleranceText() {
        return toleranceField.getText();
    }

    public void showCalculationResult(SolverResult result) {
        resultArea.setText(String.format(
                "Raíz aproximada: %.12f%nIteraciones: %d%n%n%s",
                result.root(), result.iterations(), result.details()));
    }

    public void showPlot(Expression function, SolverResult result, double referenceX) {
        plotPanel.plot(function, result.root(), referenceX);
    }

    public void showCalculationError(String message) {
        resultArea.setText("No se pudo calcular:\n" + message
                + "\n\nRevisa la función y los valores ingresados.");
    }

    public void showMethodInstructions(CalculationMethod method) {
        instructionLabel.setText("  " + method.getInstruction());
    }

    public void setMethodSelectionListener(ActionListener listener) {
        methodSelector.addActionListener(listener);
    }

    public void setCalculateListener(ActionListener listener) {
        calculateButton.addActionListener(listener);
    }

    public void setClearListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void setBrentExampleListener(ActionListener listener) {
        brentExampleButton.addActionListener(listener);
    }

    public void setMultipleRootsExampleListener(ActionListener listener) {
        multipleRootsExampleButton.addActionListener(listener);
    }

    public void loadBrentExample() {
        methodSelector.setSelectedItem(CalculationMethod.BRENT);
        functionField.setText("x^2 - 4");
        firstValueField.setText("0");
        secondValueField.setText("3");
        toleranceField.setText("0.000001");
        resultArea.setText("Ejemplo cargado. Brent debe encontrar una raíz dentro de [0, 3]. "
                + "La raíz esperada es x = 2.");
    }

    public void loadMultipleRootsExample() {
        methodSelector.setSelectedItem(CalculationMethod.MULTIPLE_ROOTS);
        functionField.setText("(x - 2)^3\nf'(x) = 3(x - 2)^2\nf''(x) = 6(x - 2)");
        firstValueField.setText("3");
        secondValueField.setText("");
        toleranceField.setText("0.000001");
        resultArea.setText("Ejemplo cargado. Para raíces múltiples, la primera línea es "
                + "f(x), seguida de f'(x) y f''(x). La raíz esperada es x = 2.");
    }

    public void showPendingCalculationMessage() {
        resultArea.setText("La interfaz está lista. En el siguiente paso conectaremos aquí "
                + "la implementación del método seleccionado.");
    }

    public void clearInputs() {
        functionField.setText("");
        firstValueField.setText("");
        secondValueField.setText("");
        toleranceField.setText("0.000001");
        resultArea.setText("Aquí aparecerán el resultado, las iteraciones y los mensajes de validación.");
        plotPanel.clearPlot();
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }
}
