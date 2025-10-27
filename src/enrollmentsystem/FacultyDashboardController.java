package enrollmentsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.List;

public class FacultyDashboardController {
    
    @FXML private Label studentNameLabel;
    @FXML private Label studentEmailLabel;
    @FXML private Label studentIdLabel;
    @FXML private TabPane mainTabPane;
    @FXML private HBox topBar;
    @FXML private VBox sidebar;
    @FXML private VBox mainContainer;
    
    // Faculty Profile Labels
    @FXML private Label nameLabel;
    @FXML private Label depatmentLabel; // Note: typo from FXML
    @FXML private Label specalizationLabel; // Note: typo from FXML
    @FXML private Label yearISLabel;
    @FXML private Label subjectHandledLabel;
    @FXML private Label sectionHandledLabel;
    @FXML private Label subjectsIcon; // From your original FXML
    @FXML private Label nameLabel5;
    
    // Schedule Table
    @FXML private TableView<FacultySchedule> tblSchedule;
    @FXML private TableColumn<FacultySchedule, String> colCode;
    @FXML private TableColumn<FacultySchedule, String> colName;
    @FXML private TableColumn<FacultySchedule, String> colSec;
    @FXML private TableColumn<FacultySchedule, String> colDay;
    @FXML private TableColumn<FacultySchedule, String> colTime;
    @FXML private TableColumn<FacultySchedule, String> colRoom;
    
    private Faculty currentFaculty;
    private ObservableList<FacultySchedule> scheduleList;
    
    @FXML
    public void initialize() {
        // Initialize table columns
        setupTableColumns();
        
        // Load faculty data from session
        loadFacultyData();
        
        // Load schedule data
        loadScheduleData();
    }
    
    private void setupTableColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colSec.setCellValueFactory(new PropertyValueFactory<>("section"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("day"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        
        scheduleList = FXCollections.observableArrayList();
        tblSchedule.setItems(scheduleList);
    }
    
    private void loadFacultyData() {
        String facultyId = SessionManager.getInstance().getFacultyId();
        
        if (facultyId == null || facultyId.isEmpty()) {
            System.err.println("No faculty ID found in session!");
            return;
        }
        
        // Load faculty from database
        currentFaculty = Faculty.loadById(facultyId);
        
        if (currentFaculty != null) {
            // Update teaching load counts
            currentFaculty.updateTeachingLoad();
            
            // Set profile information (with null checks for FXML labels)
            if (nameLabel != null) {
                nameLabel.setText(currentFaculty.getFirstName() + " " + currentFaculty.getLastName());
            }
            if (depatmentLabel != null) {
                depatmentLabel.setText(currentFaculty.getDepartment() != null ? 
                                       currentFaculty.getDepartment() : "N/A");
            }
            if (specalizationLabel != null) {
                specalizationLabel.setText(currentFaculty.getSpecialization() != null ? 
                                           currentFaculty.getSpecialization() : "N/A");
            }
            if (yearISLabel != null) {
                yearISLabel.setText(String.valueOf(currentFaculty.getYearsOfService()));
            }
            if (subjectHandledLabel != null) {
                subjectHandledLabel.setText(String.valueOf(currentFaculty.getSubjectHolding()));
            }
            if (sectionHandledLabel != null) {
                sectionHandledLabel.setText(String.valueOf(currentFaculty.getSectionsHandled()));
            }
            
            // Get and display subjects list
            List<String> subjects = currentFaculty.getSubjectsList();
            if (subjectsIcon != null) {
                if (!subjects.isEmpty()) {
                    subjectsIcon.setText(String.join(", ", subjects));
                } else {
                    subjectsIcon.setText("No subjects assigned");
                }
            }
            
            // Set top bar info (optional)
            if (studentNameLabel != null) {
                studentNameLabel.setText(currentFaculty.getFirstName() + " " + currentFaculty.getLastName());
            }
            if (studentEmailLabel != null) {
                studentEmailLabel.setText(currentFaculty.getEmail());
            }
            if (studentIdLabel != null) {
                studentIdLabel.setText(facultyId);
            }
        } else {
            System.err.println("Failed to load faculty data for ID: " + facultyId);
        }
    }
    
    private void loadScheduleData() {
        if (currentFaculty == null) return;
        
        scheduleList.clear();
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return;
            
            String sql = "SELECT co.offering_id, c.course_code, c.course_title, " +
                        "s.section_name, co.schedule_day, co.schedule_time, co.room " +
                        "FROM course_offerings co " +
                        "JOIN courses c ON co.course_id = c.course_id " +
                        "LEFT JOIN section s ON co.section_id = s.section_id " +
                        "WHERE co.faculty_id = ? AND co.status = 'Open' " +
                        "ORDER BY co.schedule_day, co.schedule_time";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentFaculty.getFacultyId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String courseCode = rs.getString("course_code");
                String courseTitle = rs.getString("course_title");
                String section = rs.getString("section_name") != null ? 
                               rs.getString("section_name") : "N/A";
                String day = rs.getString("schedule_day") != null ? 
                           rs.getString("schedule_day") : "TBA";
                String time = rs.getString("schedule_time") != null ? 
                            rs.getString("schedule_time") : "TBA";
                String room = rs.getString("room") != null ? 
                            rs.getString("room") : "TBA";
                String offeringId = rs.getString("offering_id");
                
                FacultySchedule schedule = new FacultySchedule(
                    courseCode, courseTitle, section, day, time, room, offeringId
                );
                scheduleList.add(schedule);
            }
            
            if (scheduleList.isEmpty()) {
                System.out.println("No schedule found for faculty: " + currentFaculty.getFacultyId());
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading schedule data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    public void refreshSchedule() {
        if (currentFaculty != null) {
            currentFaculty.updateTeachingLoad();
            loadFacultyData();
            loadScheduleData();
        }
    }
}