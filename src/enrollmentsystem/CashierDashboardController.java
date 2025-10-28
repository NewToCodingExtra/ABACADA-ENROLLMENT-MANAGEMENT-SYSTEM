package enrollmentsystem;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Controller for Cashier Dashboard
 * Displays pending payments and allows cashier to accept/reject them
 * 
 * @author Wes
 */
public class CashierDashboardController implements Initializable {

    @FXML
    private HBox topBar;
    
    @FXML
    private VBox sidebar;
    
    @FXML
    private ImageView adminLogo;
    
    @FXML
    private Label pendingLabel;
    
    @FXML
    private Label paidLabel;
    
    @FXML
    private VBox mainContainer;
    
    @FXML
    private TabPane mainTabPane;
    
    @FXML
    private TableView<PendingPayment> paidEnrolleeTable;
    
    @FXML
    private TableColumn<PendingPayment, String> enrolleeIDCol;
    
    @FXML
    private TableColumn<PendingPayment, String> enrolleeCol;
    
    @FXML
    private TableColumn<PendingPayment, String> programCol;
    
    @FXML
    private TableColumn<PendingPayment, String> yearLevelCol;
    
    @FXML
    private TableColumn<PendingPayment, BigDecimal> amountPaidCol;
    
    @FXML
    private TableColumn<PendingPayment, Void> actionCol;
    
    @FXML
    private TableColumn<PendingPayment, String> paymentTypeCol;
    
    private String cashierId;
    private ObservableList<PendingPayment> pendingPaymentsList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get cashier ID from session
        cashierId = SessionManager.getInstance().getCashierId();
        
        if (cashierId == null) {
            showError("Session Error", "Cashier ID not found in session. Please log in again.");
            return;
        }
        
        // Setup table columns
        setupTableColumns();
        
        // Load pending payments
        loadPendingPayments();
        
        System.out.println("Cashier Dashboard initialized for cashier: " + cashierId);
    }
    
    /**
     * Setup table columns with proper cell factories
     */
    private void setupTableColumns() {
        // Enrollee ID Column
        enrolleeIDCol.setCellValueFactory(new PropertyValueFactory<>("enrolleeId"));
        enrolleeIDCol.setStyle("-fx-alignment: CENTER;");
        
        // Enrollee (Student Name) Column
        enrolleeCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        
        // Program Column
        programCol.setCellValueFactory(new PropertyValueFactory<>("programAppliedFor"));
        
        // Year Level Column
        yearLevelCol.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));
        yearLevelCol.setStyle("-fx-alignment: CENTER;");
        
        // Amount Paid Column with currency formatting
        amountPaidCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountPaidCol.setCellFactory(column -> new TableCell<PendingPayment, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("₱" + String.format("%,.2f", item));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });
        
        // Payment Type Column
        paymentTypeCol.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        paymentTypeCol.setStyle("-fx-alignment: CENTER;");
        
        // Action Column with Accept/Reject buttons
        actionCol.setCellFactory(new Callback<TableColumn<PendingPayment, Void>, TableCell<PendingPayment, Void>>() {
            @Override
            public TableCell<PendingPayment, Void> call(TableColumn<PendingPayment, Void> param) {
                return new TableCell<PendingPayment, Void>() {
                    private final Button acceptBtn = new Button("Accept");
                    private final Button rejectBtn = new Button("Reject");
                    private final HBox pane = new HBox(5);
                    
                    {
                        // Style buttons
                        acceptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                        rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                        
                        // Set button actions
                        acceptBtn.setOnAction(event -> {
                            PendingPayment payment = getTableView().getItems().get(getIndex());
                            handleAcceptPayment(payment);
                        });
                        
                        rejectBtn.setOnAction(event -> {
                            PendingPayment payment = getTableView().getItems().get(getIndex());
                            handleRejectPayment(payment);
                        });
                        
                        // Add buttons to HBox
                        pane.getChildren().addAll(acceptBtn, rejectBtn);
                        pane.setAlignment(Pos.CENTER);
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }
    
    /**
     * Load pending payments from database and update UI
     */
    private void loadPendingPayments() {
        List<PendingPayment> payments = PaymentMonitor.getPendingPayments();
        pendingPaymentsList = FXCollections.observableArrayList(payments);
        paidEnrolleeTable.setItems(pendingPaymentsList);
        
        // Update count labels
        updateCountLabels();
        
        System.out.println("Loaded " + payments.size() + " pending payments");
    }
 
    private void updateCountLabels() {
        if (pendingLabel != null) {
            int pendingCount = pendingPaymentsList != null ? pendingPaymentsList.size() : 0;
            pendingLabel.setText(String.valueOf(pendingCount));
        }
        
        if (paidLabel != null) {
            int paidCount = countVerifiedPayments();
            paidLabel.setText(String.valueOf(paidCount));
        }
    }
   
    private int countVerifiedPayments() {
        String query = "SELECT COUNT(*) as count FROM enrollees WHERE payment_status = 'Verified'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting verified payments: " + e.getMessage());
        }
        
        return 0;
    }
  
    private void handleAcceptPayment(PendingPayment payment) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Accept Payment");
        confirmation.setHeaderText("Confirm Payment Acceptance");
        confirmation.setContentText(
            "Are you sure you want to accept this payment?\n\n" +
            "Student: " + payment.getStudentName() + "\n" +
            "Enrollee ID: " + payment.getEnrolleeId() + "\n" +
            "Amount: ₱" + String.format("%,.2f", payment.getAmount()) + "\n" +
            "Payment Type: " + payment.getPaymentType() + "\n\n" +
            "This will generate a receipt for the student."
        );
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = PaymentMonitor.acceptPayment(payment.getPaymentId(), cashierId);
            
            if (success) {
                showInfo("Payment Accepted", 
                        "Payment has been accepted successfully.\n" +
                        "Receipt has been generated for the student.");
                
                loadPendingPayments();
            } else {
                showError("Error", "Failed to accept payment. Please try again.");
            }
        }
    }
   
    private void handleRejectPayment(PendingPayment payment) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Payment");
        dialog.setHeaderText("Reject Payment - Provide Reason");
        dialog.setContentText(
            "Student: " + payment.getStudentName() + "\n" +
            "Enrollee ID: " + payment.getEnrolleeId() + "\n" +
            "Amount: ₱" + String.format("%,.2f", payment.getAmount()) + "\n\n" +
            "Please provide a reason for rejection:"
        );
        
        dialog.getDialogPane().setPrefWidth(450);
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String reason = result.get().trim();
            
            // Confirm rejection
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Rejection");
            confirmation.setHeaderText("Confirm Payment Rejection");
            confirmation.setContentText(
                "Are you sure you want to reject this payment?\n\n" +
                "Reason: " + reason + "\n\n" +
                "The student will be notified of the rejection."
            );
            
            Optional<ButtonType> confirmResult = confirmation.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                boolean success = PaymentMonitor.rejectPayment(payment.getPaymentId(), cashierId, reason);
                
                if (success) {
                    showInfo("Payment Rejected", 
                            "Payment has been rejected.\n" +
                            "Student will see the rejection reason in their dashboard.");
                    
                    loadPendingPayments();
                } else {
                    showError("Error", "Failed to reject payment. Please try again.");
                }
            }
        } else if (result.isPresent()) {
            showError("Invalid Input", "Please provide a reason for rejection.");
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}