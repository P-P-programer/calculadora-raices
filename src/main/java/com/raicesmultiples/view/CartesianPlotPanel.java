package com.raicesmultiples.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import com.raicesmultiples.model.Expression;

public class CartesianPlotPanel extends javax.swing.JPanel {
    private static final Color GRID = new Color(232, 224, 240);
    private static final Color AXIS = new Color(120, 103, 133);
    private static final Color CURVE = new Color(108, 63, 160);
    private static final Color ROOT = new Color(177, 72, 132);
    private static final int PADDING = 42;
    private Expression function;
    private double root;
    private double centerX;
    private double centerY;
    private double scaleX = 45;
    private double scaleY = 45;
    private String coordinates = "";

    public CartesianPlotPanel() {
        setPreferredSize(new Dimension(700, 300));
        setBackground(new Color(255, 253, 255));
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                coordinates = String.format("x = %.5f, y = %.5f",
                        toMathX(event.getX()), toMathY(event.getY()));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                coordinates = "";
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                double factor = event.getWheelRotation() < 0 ? 1.15 : 1 / 1.15;
                scaleX = clamp(scaleX * factor, 8, 500);
                scaleY = clamp(scaleY * factor, 8, 500);
                repaint();
            }
        };
        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
    }

    public void plot(Expression function, double root, double referenceX) {
        this.function = function;
        this.root = root;
        this.centerX = Double.isFinite(referenceX) ? referenceX : root;
        this.centerY = 0;
        repaint();
    }

    public void clearPlot() {
        function = null;
        coordinates = "";
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawAxes(g);
        if (function != null) {
            drawFunction(g);
            drawRoot(g);
        } else {
            g.setColor(Color.GRAY);
            g.drawString("Ingresa una función y presiona Calcular para graficarla.", PADDING, 30);
        }
        if (!coordinates.isEmpty()) {
            g.setColor(new Color(30, 30, 30));
            g.fillRect(PADDING, 8, 145, 20);
            g.setColor(Color.WHITE);
            g.drawString(coordinates, PADDING + 5, 23);
        }
        g.dispose();
    }

    private void drawAxes(Graphics2D g) {
        g.setColor(GRID);
        g.setStroke(new BasicStroke(1));
        for (int x = PADDING; x < getWidth() - PADDING; x += Math.max(1, (int) scaleX)) {
            g.drawLine(x, PADDING, x, getHeight() - PADDING);
        }
        for (int y = PADDING; y < getHeight() - PADDING; y += Math.max(1, (int) scaleY)) {
            g.drawLine(PADDING, y, getWidth() - PADDING, y);
        }
        g.setColor(AXIS);
        int axisX = toScreenX(0);
        int axisY = toScreenY(0);
        if (axisX >= PADDING && axisX <= getWidth() - PADDING) {
            g.drawLine(axisX, PADDING, axisX, getHeight() - PADDING);
        }
        if (axisY >= PADDING && axisY <= getHeight() - PADDING) {
            g.drawLine(PADDING, axisY, getWidth() - PADDING, axisY);
        }
        g.drawString("x", getWidth() - PADDING + 8, Math.min(getHeight() - PADDING, Math.max(PADDING, axisY)) + 4);
        g.drawString("y", Math.min(getWidth() - PADDING, Math.max(PADDING, axisX)) + 5, PADDING - 8);
    }

    private void drawFunction(Graphics2D g) {
        g.setColor(CURVE);
        g.setStroke(new BasicStroke(2));
        int previousX = PADDING;
        int previousY = toScreenY(safeEvaluate(toMathX(previousX)));
        for (int screenX = PADDING + 1; screenX <= getWidth() - PADDING; screenX++) {
            double value = safeEvaluate(toMathX(screenX));
            int screenY = toScreenY(value);
            if (Double.isFinite(value) && Math.abs(screenY - previousY) < getHeight() * 2) {
                g.drawLine(previousX, previousY, screenX, screenY);
            }
            previousX = screenX;
            previousY = screenY;
        }
    }

    private void drawRoot(Graphics2D g) {
        int screenX = toScreenX(root);
        int screenY = toScreenY(0);
        if (screenX < 0 || screenX > getWidth() || screenY < 0 || screenY > getHeight()) {
            return;
        }
        g.setColor(ROOT);
        g.fillOval(screenX - 6, screenY - 6, 12, 12);
        g.drawString(String.format("raíz (%.6f, 0)", root), screenX + 8, screenY - 8);
    }

    private double safeEvaluate(double x) {
        try {
            double value = function.evaluate(x);
            return Double.isFinite(value) && Math.abs(value) < 1e6 ? value : Double.NaN;
        } catch (RuntimeException exception) {
            return Double.NaN;
        }
    }

    private int toScreenX(double x) {
        return (int) Math.round(getWidth() / 2.0 + (x - centerX) * scaleX);
    }

    private int toScreenY(double y) {
        return (int) Math.round(getHeight() / 2.0 - (y - centerY) * scaleY);
    }

    private double toMathX(int screenX) {
        return centerX + (screenX - getWidth() / 2.0) / scaleX;
    }

    private double toMathY(int screenY) {
        return centerY - (screenY - getHeight() / 2.0) / scaleY;
    }

    private double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
