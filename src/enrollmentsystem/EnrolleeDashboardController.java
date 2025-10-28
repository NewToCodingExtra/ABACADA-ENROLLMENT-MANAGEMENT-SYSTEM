package enrollmentsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import java.sql.*;

public class EnrolleeDashboardController {
    
    private Enrollee enrollee;
    private boolean isPaid = false;
    private String cashierComment = "None";
    private String adminComment = "None";
    private InvoiceInfo invoiceInfo = null;
    
    @FXML
    private Label studentNameLabel;
    
    @FXML
    private Label studentIdLabel;
    
    @FXML
    private TabPane mainTabPane;
    
    @FXML
    private HBox topBar;
    
    @FXML
    private VBox sidebar;
    
    @FXML
    private VBox mainContainer;
    
    @FXML
    private TableView<OverviewData> overViewTable;
    
    @FXML
    private TableColumn<OverviewData, String> semCol;
    
    @FXML
    private TableColumn<OverviewData, String> courseCol;
    
    @FXML
    private TableColumn<OverviewData, String> sectionCol;
    
    @FXML
    private TableColumn<OverviewData, String> yearCol;
    
    @FXML
    private TableView<EvaluationData> evalTable;
    
    @FXML
    private TableColumn<EvaluationData, String> evalCol;
    
    @FXML
    private TableColumn<EvaluationData, String> statusCol;
    
    @FXML
    private Hyperlink updateBtn;
    
    public void initialize() {
        System.out.println("Initializing Enrollee Dashboard...");
        
        enrollee = EnrolleeDataLoader.loadEnrolleeData();
        
        if (enrollee != null) {
            System.out.println("Enrollee loaded successfully: " + enrollee.getEnrolleeId());
            loadPaymentAndComments();
            loadInvoiceInfo();
            loadEnrolleeInfo();
            setupOverviewTable();
            setupEvaluationTable();
        } else {
            System.err.println("Failed to load enrollee data!");
            showErrorDialog("Error", "Failed to load enrollee data. Please try logging in again.");
        }
    }
    
    /**
     * Load payment status and comments from database
     */
    private void loadPaymentAndComments() {
        String query = "SELECT " +
                      "e.payment_status, " +
                      "(SELECT p.remarks FROM payment p WHERE p.enrollee_id = e.enrollee_id ORDER BY p.payment_date DESC LIMIT 1) as payment_remarks, " +
                      "e.cashier_rejection_reason, " +
                      "e.reviewed_by " +
                      "FROM enrollees e " +
                      "WHERE e.enrollee_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrollee.getEnrolleeId());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String paymentStatus = rs.getString("payment_status");
                
                if ("Verified".equals(paymentStatus)) {
                    isPaid = true;
                    cashierComment = "Payment verified and accepted";
                } else if ("Rejected".equals(paymentStatus)) {
                    isPaid = false;
                    String rejectionReason = rs.getString("cashier_rejection_reason");
                    cashierComment = "Payment rejected: " + (rejectionReason != null ? rejectionReason : "No reason provided");
                } else if ("Paid_Pending_Verification".equals(paymentStatus)) {
                    isPaid = false;
                    cashierComment = "Payment pending cashier verification";
                } else {
                    isPaid = false;
                    cashierComment = "No payment made yet";
                }
                
                Integer reviewedBy = (Integer) rs.getObject("reviewed_by");
                if (reviewedBy != null) {
                    adminComment = getAdminComment(reviewedBy);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading payment and comments: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load invoice information if payment is verified
     */
    private void loadInvoiceInfo() {
        if (isPaid) {
            invoiceInfo = PaymentMonitor.getEnrolleeInvoice(enrollee.getEnrolleeId());
            if (invoiceInfo != null) {
                System.out.println("Invoice loaded: " + invoiceInfo.getInvoiceNumber());
            }
        }
    }
    
    /**
     * Get admin comment based on reviewed_by
     */
    private String getAdminComment(int reviewedBy) {
        String query = "SELECT first_name, last_name FROM admin WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, reviewedBy);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                return "Reviewed by " + firstName + " " + lastName;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting admin comment: " + e.getMessage());
        }
        
        return "None";
    }
  
    private void loadEnrolleeInfo() {
        String fullName = buildFullName();
        studentNameLabel.setText(fullName);
        
        Tooltip nameTooltip = new Tooltip(fullName);
        Tooltip.install(studentNameLabel, nameTooltip);
        
        studentIdLabel.setText(enrollee.getEnrolleeId() != null ? enrollee.getEnrolleeId() : "N/A");
        
        System.out.println("Dashboard loaded for: " + fullName);
    }
  
    private String buildFullName() {
        StringBuilder name = new StringBuilder();
        
        if (enrollee.getFirstName() != null && !enrollee.getFirstName().isEmpty()) {
            name.append(enrollee.getFirstName());
        }
        
        if (enrollee.getMiddleName() != null && !enrollee.getMiddleName().isEmpty()) {
            name.append(" ").append(enrollee.getMiddleName());
        }
        
        if (enrollee.getLastName() != null && !enrollee.getLastName().isEmpty()) {
            name.append(" ").append(enrollee.getLastName());
        }
        
        if (enrollee.getSuffix() != null && !enrollee.getSuffix().isEmpty()) {
            name.append(" ").append(enrollee.getSuffix());
        }
        
        return name.length() > 0 ? name.toString() : "Enrollee";
    }

    private void setupOverviewTable() {
        overViewTable.setFixedCellSize(50);
        overViewTable.setStyle("-fx-background-color: transparent;");
        
        semCol.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());
        courseCol.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        sectionCol.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        yearCol.setCellValueFactory(cellData -> cellData.getValue().yearProperty());
        
        semCol.setCellFactory(col -> createCenteredCell());
        courseCol.setCellFactory(col -> createCenteredCell());
        sectionCol.setCellFactory(col -> createCenteredCell());
        yearCol.setCellFactory(col -> createCenteredCell());
        
        ObservableList<OverviewData> overviewData = FXCollections.observableArrayList();
        
        String currentYear = String.valueOf(java.time.LocalDateTime.now().getYear());
        
        OverviewData data = new OverviewData("N/A", "N/A", "N/A", currentYear);
        overviewData.add(data);
        overViewTable.setItems(overviewData);
        
        overViewTable.setPrefHeight(80);
        overViewTable.setMinHeight(80);
        overViewTable.setMaxHeight(80);
        
        System.out.println("Overview table setup complete");
    }
    
    private TableCell<OverviewData, String> createCenteredCell() {
        return new TableCell<OverviewData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }

    private void setupEvaluationTable() {
        evalTable.setFixedCellSize(40);
        
        evalCol.setCellValueFactory(cellData -> cellData.getValue().evaluationProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        statusCol.setCellFactory(column -> new TableCell<EvaluationData, String>() {
            private final Hyperlink hyperlink = new Hyperlink();
            private final Hyperlink receiptLink = new Hyperlink();
            private final Label label = new Label();
            private final HBox hbox = new HBox(10);
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    EvaluationData rowData = getTableView().getItems().get(getIndex());
                    
                    if (rowData.getEvaluation().equals("Payment Status")) {
                        hbox.getChildren().clear();
                        
                        if (isPaid && invoiceInfo != null) {
                            label.setText("Paid");
                            label.setTextFill(Color.GREEN);
                            label.setStyle("-fx-font-weight: bold;");
                            
                            receiptLink.setText("(Receipt)");
                            receiptLink.setTextFill(Color.RED);
                            receiptLink.setStyle("-fx-font-style: italic;");
                            receiptLink.setOnAction(e -> handleReceiptLink());
                            
                            hbox.getChildren().addAll(label, receiptLink);
                            hbox.setAlignment(Pos.CENTER_LEFT);
                            setGraphic(hbox);
                        } else if ("Paid_Pending_Verification".equals(item)) {
                            // Show orange pending text
                            label.setText("Pending Verification");
                            label.setTextFill(Color.ORANGE);
                            label.setStyle("-fx-font-weight: bold;");
                            setGraphic(label);
                        } else if (item.equals("Not Paid")) {
                            // Show red hyperlink for unpaid
                            hyperlink.setText(item);
                            hyperlink.setTextFill(Color.RED);
                            hyperlink.setStyle("-fx-font-weight: bold;");
                            hyperlink.setOnAction(e -> handlePaymentLink());
                            setGraphic(hyperlink);
                        } else {
                            // Normal text for other statuses
                            label.setText(item);
                            label.setTextFill(Color.BLACK);
                            label.setStyle("");
                            setGraphic(label);
                        }
                    } else {
                        // Normal text for other rows
                        label.setText(item);
                        label.setTextFill(Color.BLACK);
                        label.setStyle("");
                        setGraphic(label);
                    }
                }
            }
        });
        
        ObservableList<EvaluationData> evaluationData = FXCollections.observableArrayList();
        
        String enrollmentStatus = enrollee.getEnrollmentStatus() != null ? 
                                  enrollee.getEnrollmentStatus() : "Pending";
        evaluationData.add(new EvaluationData("Application Status", enrollmentStatus));
        
        String formStatus = enrollee.hasFilledUpForm() ? "Completed" : "Incomplete";
        evaluationData.add(new EvaluationData("Form Status", formStatus));
        
        String docStatus = checkDocumentStatus();
        evaluationData.add(new EvaluationData("Document Submission", docStatus));
        
        String program = enrollee.getProgramAppliedFor() != null ? 
                        enrollee.getProgramAppliedFor() : "Not Selected";
        evaluationData.add(new EvaluationData("Program Applied", program));
        
        String yearLevel = enrollee.getYearLevel() != null ? 
                          enrollee.getYearLevel() : "Not Specified";
        evaluationData.add(new EvaluationData("Year Level", yearLevel));
        
        // Payment Status
        String paymentStatus;
        if (isPaid && invoiceInfo != null) {
            paymentStatus = "Paid";
        } else {
            paymentStatus = loadPaymentStatusFromDB();
        }
        evaluationData.add(new EvaluationData("Payment Status", paymentStatus));
        
        evaluationData.add(new EvaluationData("Cashier Comment", cashierComment));
        evaluationData.add(new EvaluationData("Admin Comment", adminComment));
        
        evalTable.setItems(evaluationData);
        
        int rowCount = evaluationData.size();
        double headerHeight = 30;
        double rowHeight = 40;
        double totalHeight = headerHeight + (rowCount * rowHeight);
        
        evalTable.setPrefHeight(totalHeight);
        evalTable.setMinHeight(totalHeight);
        evalTable.setMaxHeight(totalHeight);
        
        System.out.println("Evaluation table setup complete with " + rowCount + " rows");
    }
    
    /**
     * Load payment status from database
     */
    private String loadPaymentStatusFromDB() {
        String query = "SELECT payment_status FROM enrollees WHERE enrollee_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrollee.getEnrolleeId());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String status = rs.getString("payment_status");
                if ("Verified".equals(status)) {
                    return "Paid";
                } else if ("Paid_Pending_Verification".equals(status)) {
                    return "Paid_Pending_Verification";
                } else if ("Rejected".equals(status)) {
                    return "Rejected";
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading payment status: " + e.getMessage());
        }
        
        return "Not Paid";
    }
    
    /**
     * Handle receipt link click
     */
    private void handleReceiptLink() {
        if (invoiceInfo != null) {
            showReceiptDialog();
        } else {
            showInfoDialog("No Receipt", "Receipt information is not available.");
        }
    }
    
    /**
     * Show receipt dialog with invoice details
     */
    private void showReceiptDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Receipt");
        alert.setHeaderText("Official Receipt");
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy");
        
        String content = String.format(
            "═══════════════════════════════════════\n" +
            "        ABAKADA UNIVERSITY\n" +
            "        OFFICIAL RECEIPT\n" +
            "═══════════════════════════════════════\n\n" +
            "Invoice Number: %s\n" +
            "Date: %s\n" +
            "Student: %s\n" +
            "Enrollee ID: %s\n\n" +
            "───────────────────────────────────────\n" +
            "Description: Tuition Fee Payment\n" +
            "Payment Type: %s\n" +
            "Amount Paid: ₱%,.2f\n" +
            "───────────────────────────────────────\n\n" +
            "Total Amount: ₱%,.2f\n\n" +
            "═══════════════════════════════════════\n" +
            "     Thank you for your payment!\n" +
            "═══════════════════════════════════════",
            invoiceInfo.getInvoiceNumber(),
            sdf.format(invoiceInfo.getInvoiceDate()),
            buildFullName(),
            enrollee.getEnrolleeId(),
            invoiceInfo.getPaymentType(),
            invoiceInfo.getTotalAmount(),
            invoiceInfo.getTotalAmount()
        );
        
        alert.setContentText(content);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    private void handlePaymentLink() {
        WindowOpener.openDialogWithCSS("/enrollmentsystem/PaymentMethod.fxml", 
                                      "/paymentwindow.css", 
                                      "Choose payment method", 
                                      429, 300);
    }
  
    private String checkDocumentStatus() {
        int uploadedCount = 0;
        int totalRequired = 4;
        
        if (enrollee.getPhotoLink() != null && !enrollee.getPhotoLink().isEmpty()) uploadedCount++;
        if (enrollee.getBirthCertLink() != null && !enrollee.getBirthCertLink().isEmpty()) uploadedCount++;
        if (enrollee.getReportCardLink() != null && !enrollee.getReportCardLink().isEmpty()) uploadedCount++;
        if (enrollee.getForm137Link() != null && !enrollee.getForm137Link().isEmpty()) uploadedCount++;
        
        if (uploadedCount == totalRequired) {
            return "Complete (" + uploadedCount + "/" + totalRequired + ")";
        } else if (uploadedCount > 0) {
            return "Incomplete (" + uploadedCount + "/" + totalRequired + ")";
        } else {
            return "Not Submitted";
        }
    }
  
    @FXML
    private void updateBtnAction(ActionEvent event) {
        try {
            WindowOpener.openSceneWithCSS("/enrollmentsystem/Enrollment1.fxml", 
                             "/enrollment.css",
                             "ABAKADA UNIVERSITY - ENROLLEE FORM",
                             900, 520);
        } catch (Exception e) {
            System.err.println("Error navigating to update form: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error", "Failed to open update form.");
        }
    }
   
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
  
    public Enrollee getEnrollee() {
        return enrollee;
    }
    
    public boolean isPaid() { return isPaid; }
    public String getCashierComment() { return cashierComment; }
    public String getAdminComment() { return adminComment; }
    public InvoiceInfo getInvoiceInfo() { return invoiceInfo; }
    
    public static class OverviewData {
        private final javafx.beans.property.SimpleStringProperty semester;
        private final javafx.beans.property.SimpleStringProperty course;
        private final javafx.beans.property.SimpleStringProperty section;
        private final javafx.beans.property.SimpleStringProperty year;
        
        public OverviewData(String semester, String course, String section, String year) {
            this.semester = new javafx.beans.property.SimpleStringProperty(semester);
            this.course = new javafx.beans.property.SimpleStringProperty(course);
            this.section = new javafx.beans.property.SimpleStringProperty(section);
            this.year = new javafx.beans.property.SimpleStringProperty(year);
        }
        
        public javafx.beans.property.SimpleStringProperty semesterProperty() { return semester; }
        public javafx.beans.property.SimpleStringProperty courseProperty() { return course; }
        public javafx.beans.property.SimpleStringProperty sectionProperty() { return section; }
        public javafx.beans.property.SimpleStringProperty yearProperty() { return year; }
        
        public String getSemester() { return semester.get(); }
        public String getCourse() { return course.get(); }
        public String getSection() { return section.get(); }
        public String getYear() { return year.get(); }
    }
   
    public static class EvaluationData {
        private final javafx.beans.property.SimpleStringProperty evaluation;
        private final javafx.beans.property.SimpleStringProperty status;
        
        public EvaluationData(String evaluation, String status) {
            this.evaluation = new javafx.beans.property.SimpleStringProperty(evaluation);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }
        
        public javafx.beans.property.SimpleStringProperty evaluationProperty() { return evaluation; }
        public javafx.beans.property.SimpleStringProperty statusProperty() { return status; }
        
        public String getEvaluation() { return evaluation.get(); }
        public String getStatus() { return status.get(); }
    }
}