package enrollmentsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EnrolleeDashboardController {
    
    private Enrollee enrollee;
    
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
            loadEnrolleeInfo();
            setupOverviewTable();
            setupEvaluationTable();
        } else {
            System.err.println("Failed to load enrollee data!");
            showErrorDialog("Error", "Failed to load enrollee data. Please try logging in again.");
        }
    }
  
    private void loadEnrolleeInfo() {
        // Set student name
        String fullName = buildFullName();
        studentNameLabel.setText(fullName);
        
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
        // Setup cell value factories
        semCol.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());
        courseCol.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        sectionCol.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        yearCol.setCellValueFactory(cellData -> cellData.getValue().yearProperty());
        
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
        
        System.out.println("Overview table setup complete");
    }

    private void setupEvaluationTable() {
        evalCol.setCellValueFactory(cellData -> cellData.getValue().evaluationProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
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
        
        evalTable.setItems(evaluationData);
        
        System.out.println("Evaluation table setup complete");
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