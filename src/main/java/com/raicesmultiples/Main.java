package com.raicesmultiples;

import javax.swing.SwingUtilities;
import com.raicesmultiples.controller.MainController;
import com.raicesmultiples.model.CalculatorModel;
import com.raicesmultiples.view.MainView;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorModel model = new CalculatorModel();
            MainView view = new MainView();
            new MainController(model, view).start();
        });
    }
}
