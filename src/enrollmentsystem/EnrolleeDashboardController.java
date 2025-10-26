package enrollmentsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import java.time.LocalDateTime;
import java.sql.*;

public class EnrolleeDashboardController {
    
    private Enrollee enrollee;
    private boolean isPaid = false;
    private String cashierComment = "None";
    private String adminComment = "None";
    
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
        
        // Load enrollee data using EnrolleeDataLoader
        enrollee = EnrolleeDataLoader.loadEnrolleeData();
        
        if (enrollee != null) {
            System.out.println("Enrollee loaded successfully: " + enrollee.getEnrolleeId());
            loadPaymentAndComments();
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
                      "(SELECT COUNT(*) FROM payment p WHERE p.enrollee_id = e.enrollee_id) as payment_count, " +
                      "(SELECT c.first_name FROM cashier c WHERE c.cashier_id = " +
                      "(SELECT p.cashier_id FROM payment p WHERE p.enrollee_id = e.enrollee_id ORDER BY p.payment_date DESC LIMIT 1)) as cashier_name, " +
                      "(SELECT p.remarks FROM payment p WHERE p.enrollee_id = e.enrollee_id ORDER BY p.payment_date DESC LIMIT 1) as cashier_comment, " +
                      "e.reviewed_by " +
                      "FROM enrollees e " +
                      "WHERE e.enrollee_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, enrollee.getEnrolleeId());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int paymentCount = rs.getInt("payment_count");
                isPaid = paymentCount > 0;
                
                String cashierName = rs.getString("cashier_name");
                String remarks = rs.getString("cashier_comment");
                cashierComment = (remarks != null && !remarks.isEmpty()) ? remarks : "None";
                
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
        // Set student name with tooltip for long names
        String fullName = buildFullName();
        studentNameLabel.setText(fullName);
        
        // Add tooltip to show full name on hover
        Tooltip nameTooltip = new Tooltip(fullName);
        Tooltip.install(studentNameLabel, nameTooltip);
        
        // Set student ID (enrollee ID)
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
        // Make table unscrollable - disable scrollbars
        overViewTable.setFixedCellSize(50); // Fixed row height
        
        // Disable scrollbars
        overViewTable.setStyle("-fx-background-color: transparent;");
        
        // Setup cell value factories with centered text
        semCol.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());
        courseCol.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        sectionCol.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        yearCol.setCellValueFactory(cellData -> cellData.getValue().yearProperty());
        
        // Center align all columns
        semCol.setCellFactory(col -> {
            TableCell<OverviewData, String> cell = new TableCell<OverviewData, String>() {
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
            return cell;
        });
        
        courseCol.setCellFactory(col -> {
            TableCell<OverviewData, String> cell = new TableCell<OverviewData, String>() {
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
            return cell;
        });
        
        sectionCol.setCellFactory(col -> {
            TableCell<OverviewData, String> cell = new TableCell<OverviewData, String>() {
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
            return cell;
        });
        
        yearCol.setCellFactory(col -> {
            TableCell<OverviewData, String> cell = new TableCell<OverviewData, String>() {
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
            return cell;
        });
        
        // Create data for the table
        ObservableList<OverviewData> overviewData = FXCollections.observableArrayList();
        
        // Since enrollee is not yet enrolled, show N/A for most fields
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        
        OverviewData data = new OverviewData(
            "N/A",  // Semester - not enrolled yet
            "N/A",  // Course - not enrolled yet
            "N/A",  // Section - not enrolled yet
            currentYear  // Current year
        );
        
        overviewData.add(data);
        overViewTable.setItems(overviewData);
        
        // Set table height based on content (1 row + header)
        overViewTable.setPrefHeight(80); // Header + 1 row
        overViewTable.setMinHeight(80);
        overViewTable.setMaxHeight(80);
        
        System.out.println("Overview table setup complete");
    }

    private void setupEvaluationTable() {
        // Make table unscrollable - fixed size
        evalTable.setFixedCellSize(40); // Fixed row height
        
        // Setup cell value factories
        evalCol.setCellValueFactory(cellData -> cellData.getValue().evaluationProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        // Custom cell factory for status column to handle hyperlink for payment
        statusCol.setCellFactory(column -> new TableCell<EvaluationData, String>() {
            private final Hyperlink hyperlink = new Hyperlink();
            private final Label label = new Label();
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    EvaluationData rowData = getTableView().getItems().get(getIndex());
                    
                    // Check if this is the Payment Status row
                    if (rowData.getEvaluation().equals("Payment Status")) {
                        if (item.equals("Not Paid")) {
                            // Show red hyperlink for unpaid
                            hyperlink.setText(item);
                            hyperlink.setTextFill(Color.RED);
                            hyperlink.setStyle("-fx-font-weight: bold;");
                            hyperlink.setOnAction(e -> handlePaymentLink());
                            setGraphic(hyperlink);
                        } else {
                            // Show green text for paid
                            label.setText(item);
                            label.setTextFill(Color.GREEN);
                            label.setStyle("-fx-font-weight: bold;");
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
        
        // Application Status
        String enrollmentStatus = enrollee.getEnrollmentStatus() != null ? 
                                  enrollee.getEnrollmentStatus() : "Pending";
        evaluationData.add(new EvaluationData("Application Status", enrollmentStatus));
        
        // Form Status
        String formStatus = enrollee.hasFilledUpForm() ? "Completed" : "Incomplete";
        evaluationData.add(new EvaluationData("Form Status", formStatus));
        
        // Document Submission
        String docStatus = checkDocumentStatus();
        evaluationData.add(new EvaluationData("Document Submission", docStatus));
        
        // Program Applied
        String program = enrollee.getProgramAppliedFor() != null ? 
                        enrollee.getProgramAppliedFor() : "Not Selected";
        evaluationData.add(new EvaluationData("Program Applied", program));
        
        // Year Level
        String yearLevel = enrollee.getYearLevel() != null ? 
                          enrollee.getYearLevel() : "Not Specified";
        evaluationData.add(new EvaluationData("Year Level", yearLevel));
        
        // Payment Status (NEW)
        String paymentStatus = isPaid ? "Paid" : "Not Paid";
        evaluationData.add(new EvaluationData("Payment Status", paymentStatus));
        
        // Cashier Comment (NEW)
        evaluationData.add(new EvaluationData("Cashier Comment", cashierComment));
        
        // Admin Comment (NEW)
        evaluationData.add(new EvaluationData("Admin Comment", adminComment));
        
        evalTable.setItems(evaluationData);
        
        // Set table height based on content (8 rows + header)
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
     * Handle payment link click
     */
    private void handlePaymentLink() {
        showInfoDialog("Payment Required", 
            "Your enrollment is not yet paid.\n\n" +
            "Please proceed to the cashier to complete your payment.\n\n" +
            "Enrollment Fee: ₱5,000.00\n" +
            "Location: Cashier's Office, Ground Floor");
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
    
    // Getters for payment status and comments (for other components if needed)
    public boolean isPaid() { return isPaid; }
    public String getCashierComment() { return cashierComment; }
    public String getAdminComment() { return adminComment; }
    
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