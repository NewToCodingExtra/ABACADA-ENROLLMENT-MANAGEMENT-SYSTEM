package enrollmentsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class CashierDashboardController {

    @FXML
    private Label cashierNameLabel;

    @FXML
    private Label cashierEmailLabel;

    @FXML
    private Label pendingLabel;

    @FXML
    private Label paidLabel;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private void initialize() {
        // Static placeholders for front-end display only
        cashierNameLabel.setText("Cashier Full Name");
        cashierEmailLabel.setText("cashiername@starlight.cashier");
        pendingLabel.setText("5");
        paidLabel.setText("5");
    }
}
